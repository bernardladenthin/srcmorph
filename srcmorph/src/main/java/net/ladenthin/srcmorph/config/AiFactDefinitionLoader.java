// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.config;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import lombok.ToString;
import net.ladenthin.srcmorph.support.AiPathSupport;
import net.ladenthin.srcmorph.support.AiProfileFileSupport;
import net.ladenthin.srcmorph.support.Java8CompatibilityHelper;
import org.jspecify.annotations.Nullable;

/**
 * Reads fact-counter groups from files, so the rest of the pipeline sees inline {@code facts} only:
 * the {@code factsFile} a definition names, and, for a {@code factsKey} a rule uses that no definition
 * has, the file {@code <profileDirectory>/<key>.tsv} ({@link AiProfileFileSupport}).
 *
 * <p>A facts file holds one counter per line, its label and its regular expression separated by the
 * first tab: {@code label<TAB>pattern}. Empty lines and lines starting with {@code #} are comments.
 * The pattern is used as written, with Java's regex syntax and its inline flags ({@code (?im)}), and
 * is compiled while the file is read, so a broken pattern fails the run with its file and line before
 * any file is summarized. A relative {@code factsFile} is resolved against the profile directory, an
 * absolute one is always used as it is ({@link AiPathSupport#resolveAgainst}). A definition with both
 * {@code facts} and {@code factsFile} is refused; with a profile directory set, a used key that is
 * neither defined nor a file there is refused, naming the file it looked for. The configured
 * definitions are never modified.</p>
 */
@ToString
public final class AiFactDefinitionLoader {

    /** Comment marker at the start of a line of a facts file. */
    private static final String COMMENT = "#";

    /** Separator between a counter's label and its pattern. */
    private static final char SEPARATOR = '\t';

    private final @Nullable File profileDirectory;

    /**
     * Creates a loader for one configuration.
     *
     * @param profileDirectory directory relative facts files and the groups named by key are resolved
     *                         against, or {@code null} for the working directory and no lookup by key
     */
    public AiFactDefinitionLoader(final @Nullable File profileDirectory) {
        this.profileDirectory = profileDirectory;
    }

    /**
     * Returns the definitions with every facts file read, plus a definition read from the profile
     * directory for each used facts key no definition has.
     *
     * @param factDefinitions the configured definitions, or {@code null} for none
     * @param usedFactsKeys   the {@code factsKey}s the configuration's rules use; {@code null} entries
     *                        (rules without one) are ignored
     * @return the configured definitions (files read), then one per used key read from
     *         {@code <profileDirectory>/<key>.tsv}, in the order the keys come
     * @throws IOException              if a facts file cannot be read
     * @throws IllegalArgumentException if a definition sets both {@code facts} and {@code factsFile}, a
     *                                  file is empty or has a malformed line or pattern, or with a profile
     *                                  directory a used key is neither defined nor a file
     */
    public List<AiFactDefinition> load(
            final @Nullable List<AiFactDefinition> factDefinitions, final Collection<@Nullable String> usedFactsKeys)
            throws IOException {
        final List<AiFactDefinition> configured =
                factDefinitions != null ? factDefinitions : Collections.<AiFactDefinition>emptyList();
        final List<AiFactDefinition> loaded = new ArrayList<>(configured.size() + usedFactsKeys.size());
        final Set<String> defined = new HashSet<>(
                new Java8CompatibilityHelper().hashMapCapacityFor(configured.size() + usedFactsKeys.size()));
        for (int i = 0; i < configured.size(); i++) {
            final AiFactDefinition definition = configured.get(i);
            defined.add(definition.getKey());
            final String factsFile = definition.getFactsFile();
            if (factsFile == null) {
                loaded.add(definition);
                continue;
            }
            final String label = "factDefinitions[" + i + "] (key " + definition.getKey() + ")";
            if (definition.getFacts() != null) {
                throw new IllegalArgumentException(label + " sets both facts and factsFile; set one of them");
            }
            final String path = String.valueOf(AiPathSupport.resolveAgainst(profileDirectory, factsFile));
            loaded.add(group(
                    definition.getKey(),
                    parse(label + ": factsFile " + path, AiProfileFileSupport.read(label + ": factsFile", path))));
        }
        final File directory = profileDirectory;
        if (directory == null) {
            return loaded;
        }
        for (final String key : usedFactsKeys) {
            if (key == null || !defined.add(key)) {
                continue;
            }
            final File file =
                    AiProfileFileSupport.conventionalFile(directory, key, AiProfileFileSupport.FACTS_EXTENSION);
            if (!file.isFile()) {
                throw new IllegalArgumentException("factsKey " + key
                        + " is used by a rule, but no factDefinition has it and the profile has no " + file);
            }
            final String label = "factsKey " + key;
            loaded.add(group(
                    key, parse(label + ": file " + file, AiProfileFileSupport.read(label + ": file", file.getPath()))));
        }
        return loaded;
    }

    /**
     * Parses the text of a facts file into counters.
     *
     * @param where the file, for the messages
     * @param text  the file's text
     * @return one counter per non-comment line, in file order
     * @throws IllegalArgumentException if a line has no tab, an empty label or pattern, or a pattern
     *                                  that does not compile
     */
    static List<AiFactCounter> parse(final String where, final String text) {
        final List<AiFactCounter> counters = new ArrayList<>();
        final String[] lines = text.split("\r?\n", -1);
        for (int i = 0; i < lines.length; i++) {
            final String line = lines[i];
            if (line.trim().isEmpty() || line.startsWith(COMMENT)) {
                continue;
            }
            final int tab = line.indexOf(SEPARATOR);
            final String lineLabel = where + " line " + (i + 1);
            if (tab < 0) {
                throw new IllegalArgumentException(lineLabel + ": expected label<TAB>pattern, found no tab");
            }
            final String label = line.substring(0, tab).trim();
            final String pattern = line.substring(tab + 1);
            if (label.isEmpty() || pattern.trim().isEmpty()) {
                throw new IllegalArgumentException(lineLabel + ": the label and the pattern must not be empty");
            }
            try {
                Pattern.compile(pattern);
            } catch (final PatternSyntaxException e) {
                throw new IllegalArgumentException(
                        lineLabel + ": the pattern does not compile: " + e.getDescription(), e);
            }
            final AiFactCounter counter = new AiFactCounter();
            counter.setLabel(label);
            counter.setPattern(pattern);
            counters.add(counter);
        }
        if (counters.isEmpty()) {
            throw new IllegalArgumentException(where + ": no counter, only comments");
        }
        return counters;
    }

    private static AiFactDefinition group(final String key, final List<AiFactCounter> facts) {
        final AiFactDefinition copy = new AiFactDefinition();
        copy.setKey(key);
        copy.setFacts(facts);
        return copy;
    }
}
