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
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import net.ladenthin.srcmorph.indexer.AiIndexOutcome;
import net.ladenthin.srcmorph.provider.AiGenerationUsage;

/**
 * The record of one {@code generate} run: one entry per routed file with its rule, prompt and model,
 * what happened to it, the tokens and calls it cost, how its last call ended, its wall time, and the
 * hashes of its prompt and its facts, so a run can be costed per file and model and a changed prompt
 * is visible. Written as JSON to {@code runRecordFile} when the configuration names one.
 */
@ToString
public final class RunRecord {

    /** Seconds per nanosecond. */
    private static final double NANOS_PER_SECOND = 1_000_000_000.0d;

    private final String pluginVersion;

    private final List<Entry> entries = new ArrayList<>();

    /**
     * Creates an empty record.
     *
     * @param pluginVersion the version recorded with it
     */
    public RunRecord(final String pluginVersion) {
        this.pluginVersion = pluginVersion;
    }

    /** One routed file of the run. */
    @ToString
    @EqualsAndHashCode
    public static final class Entry {

        private final String file;

        private final String rule;

        private final String promptKey;

        private final String model;

        private final AiIndexOutcome outcome;

        private final AiGenerationUsage usage;

        private final long nanos;

        private final String promptHash;

        private final String factsHash;

        /**
         * Creates an entry.
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
        public Entry(
                final String file,
                final String rule,
                final String promptKey,
                final String model,
                final AiIndexOutcome outcome,
                final AiGenerationUsage usage,
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
        public AiIndexOutcome outcome() {
            return outcome;
        }

        /**
         * Returns the tokens and calls of the file.
         *
         * @return the usage
         */
        public AiGenerationUsage usage() {
            return usage;
        }
    }

    /**
     * Adds one routed file.
     *
     * @param entry the file's entry
     */
    public void add(final Entry entry) {
        entries.add(entry);
    }

    /**
     * Returns the entries, in the order the files ran.
     *
     * @return an unmodifiable view
     */
    public List<Entry> entries() {
        return Collections.unmodifiableList(entries);
    }

    /**
     * Returns the record as JSON: the version, the totals (files by outcome, calls, tokens, seconds) and
     * one object per file.
     *
     * @return the indented JSON text
     * @throws IOException if it cannot be serialized
     */
    String toJson() throws IOException {
        final ObjectMapper mapper = new ObjectMapper();
        final ObjectNode root = mapper.createObjectNode();
        root.put("pluginVersion", pluginVersion);
        final ObjectNode totals = root.putObject("totals");
        final ArrayNode files = root.putArray("files");
        AiGenerationUsage sum = AiGenerationUsage.NONE;
        int calls = 0;
        long nanos = 0;
        int written = 0;
        int unchanged = 0;
        int failed = 0;
        for (final Entry entry : entries) {
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
            sum = new AiGenerationUsage(
                    0,
                    sum.promptTokens() + entry.usage.promptTokens(),
                    sum.completionTokens() + entry.usage.completionTokens(),
                    sum.cachedPromptTokens() + entry.usage.cachedPromptTokens(),
                    "");
            calls += entry.usage.calls();
            nanos += entry.nanos;
            if (entry.outcome == AiIndexOutcome.WRITTEN) {
                written++;
            } else if (entry.outcome == AiIndexOutcome.UNCHANGED) {
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
