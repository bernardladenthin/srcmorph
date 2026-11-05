// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.engine;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.LongSupplier;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import net.ladenthin.srcmorph.indexer.IndexOutcome;
import net.ladenthin.srcmorph.provider.GenerationUsage;

/**
 * The record of one {@code generate} run: one entry per routed file with its rule, prompt and model,
 * what happened to it, the tokens and calls it cost, how its last call ended, its wall time, and the
 * hashes of its prompt and its facts, so a run can be costed per file and model and a changed prompt
 * is visible; the totals add the files the plan skipped, the run's wall time and each model's load
 * time, which no file's time holds. Written as JSON to {@code runRecordFile} when the configuration
 * names one.
 */
@ToString
public final class RunRecord {

    /** Seconds per nanosecond. */
    private static final double NANOS_PER_SECOND = 1_000_000_000.0d;

    private final String generatorVersion;

    /** The files the plan skipped (a skip rule): no entry of their own, counted in the totals. */
    private final int skipped;

    /** The clock of the run's wall time, in nanoseconds. */
    @ToString.Exclude
    private final LongSupplier clock;

    /** When the run started, on {@link #clock}. */
    private final long startNanos;

    private final List<FileRecord> entries = new ArrayList<>();

    /** The time each model took to load, by model key, in the order they loaded. */
    private final Map<String, Long> modelLoadNanos = new LinkedHashMap<>();

    /**
     * Creates an empty record of a run that starts now.
     *
     * @param generatorVersion the version recorded with it
     * @param skipped       the files the plan skipped
     */
    public RunRecord(final String generatorVersion, final int skipped) {
        this(generatorVersion, skipped, System::nanoTime);
    }

    /**
     * Creates an empty record of a run that starts now on {@code clock} (tests).
     *
     * @param generatorVersion the version recorded with it
     * @param skipped       the files the plan skipped
     * @param clock         the run's clock in nanoseconds
     */
    RunRecord(final String generatorVersion, final int skipped, final LongSupplier clock) {
        this.generatorVersion = generatorVersion;
        this.skipped = skipped;
        this.clock = clock;
        this.startNanos = clock.getAsLong();
    }

    /**
     * Adds the time a model took to load (a local model loads on the first file that needs it), so that
     * file's own time leaves it out; a model that loads twice adds up.
     *
     * @param model the model key
     * @param nanos the load time, {@code 0} or less adds nothing
     */
    public void addModelLoad(final String model, final long nanos) {
        if (nanos > 0) {
            modelLoadNanos.merge(model, nanos, Long::sum);
        }
    }

    /** One routed file of the run. */
    @ToString
    @EqualsAndHashCode
    public static final class FileRecord {

        private final String file;

        private final String rule;

        private final String promptKey;

        private final String model;

        private final IndexOutcome outcome;

        private final GenerationUsage usage;

        private final long nanos;

        private final String promptHash;

        private final String factsHash;

        /**
         * Creates a file record.
         *
         * @param file       the file, relative to the base directory with {@code /} separators
         * @param rule       the rule's id, or its prompt key when it has none
         * @param promptKey  the prompt key
         * @param model      the model key
         * @param outcome    what happened to the file
         * @param usage      the tokens and calls of the file
         * @param nanos      the wall time of the file
         * @param promptHash the CRC32 of the prompt text
         * @param factsHash  the CRC32 of the fact counters, empty without facts
         */
        public FileRecord(
                final String file,
                final String rule,
                final String promptKey,
                final String model,
                final IndexOutcome outcome,
                final GenerationUsage usage,
                final long nanos,
                final String promptHash,
                final String factsHash) {
            this.file = file;
            this.rule = rule;
            this.promptKey = promptKey;
            this.model = model;
            this.outcome = outcome;
            this.usage = usage;
            this.nanos = nanos;
            this.promptHash = promptHash;
            this.factsHash = factsHash;
        }

        /**
         * Returns the file.
         *
         * @return the relative path
         */
        public String file() {
            return file;
        }

        /**
         * Returns what happened to the file.
         *
         * @return the outcome
         */
        public IndexOutcome outcome() {
            return outcome;
        }

        /**
         * Returns the tokens and calls of the file.
         *
         * @return the usage
         */
        public GenerationUsage usage() {
            return usage;
        }
    }

    /**
     * Adds one routed file.
     *
     * @param entry the file's entry
     */
    public void add(final FileRecord entry) {
        entries.add(entry);
    }

    /**
     * Returns the entries, in the order the files ran.
     *
     * @return an unmodifiable view
     */
    public List<FileRecord> entries() {
        return Collections.unmodifiableList(entries);
    }

    /**
     * Returns the record as JSON: the version, the totals (files by outcome and the skipped ones, calls,
     * tokens, the files' seconds, the run's wall seconds and each model's load seconds) and one object per
     * file.
     *
     * @return the indented JSON text
     * @throws IOException if it cannot be serialized
     */
    String toJson() throws IOException {
        final ObjectMapper mapper = new ObjectMapper();
        final ObjectNode root = mapper.createObjectNode();
        root.put("generatorVersion", generatorVersion);
        final ObjectNode totals = root.putObject("totals");
        final ArrayNode files = root.putArray("files");
        GenerationUsage sum = GenerationUsage.NONE;
        int calls = 0;
        long nanos = 0;
        int written = 0;
        int unchanged = 0;
        int failed = 0;
        for (final FileRecord entry : entries) {
            final ObjectNode node = files.addObject();
            node.put("file", entry.file);
            node.put("rule", entry.rule);
            node.put("promptKey", entry.promptKey);
            node.put("model", entry.model);
            node.put("outcome", entry.outcome.name().toLowerCase(java.util.Locale.ROOT));
            node.put("calls", entry.usage.calls());
            node.put("promptTokens", entry.usage.promptTokens());
            node.put("cachedPromptTokens", entry.usage.cachedPromptTokens());
            node.put("completionTokens", entry.usage.completionTokens());
            node.put("finishReason", entry.usage.finishReason());
            node.put("seconds", entry.nanos / NANOS_PER_SECOND);
            node.put("promptHash", entry.promptHash);
            node.put("factsHash", entry.factsHash);
            sum = new GenerationUsage(
                    0,
                    sum.promptTokens() + entry.usage.promptTokens(),
                    sum.completionTokens() + entry.usage.completionTokens(),
                    sum.cachedPromptTokens() + entry.usage.cachedPromptTokens(),
                    "");
            calls += entry.usage.calls();
            nanos += entry.nanos;
            if (entry.outcome == IndexOutcome.WRITTEN) {
                written++;
            } else if (entry.outcome == IndexOutcome.UNCHANGED) {
                unchanged++;
            } else {
                failed++;
            }
        }
        totals.put("files", entries.size());
        totals.put("written", written);
        totals.put("unchanged", unchanged);
        totals.put("failed", failed);
        totals.put("calls", calls);
        totals.put("promptTokens", sum.promptTokens());
        totals.put("cachedPromptTokens", sum.cachedPromptTokens());
        totals.put("completionTokens", sum.completionTokens());
        totals.put("seconds", nanos / NANOS_PER_SECOND);
        totals.put("skipped", skipped);
        totals.put("wallSeconds", (clock.getAsLong() - startNanos) / NANOS_PER_SECOND);
        final ObjectNode loads = totals.putObject("modelLoadSeconds");
        for (final Map.Entry<String, Long> load : modelLoadNanos.entrySet()) {
            loads.put(load.getKey(), load.getValue() / NANOS_PER_SECOND);
        }
        return mapper.enable(SerializationFeature.INDENT_OUTPUT).writeValueAsString(root);
    }

    /**
     * Writes the record as JSON, creating the directory it lives in.
     *
     * @param file the record file
     * @throws IOException if it cannot be written
     */
    public void write(final Path file) throws IOException {
        final Path parent = file.toAbsolutePath().getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        Files.write(file, toJson().getBytes(java.nio.charset.StandardCharsets.UTF_8));
    }
}
