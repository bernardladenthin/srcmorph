// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.config;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The fact counters of the repository's current profile ({@code profiles/v3/<key>.tsv}), read with the
 * production parser, so a pattern test checks the file that ships and never a copy of it.
 */
final class ProfileFactPatterns {

    /** The current profile, relative to the {@code srcmorph} module (Surefire's working directory). */
    static final Path PROFILE_DIRECTORY = Paths.get("../profiles/v3");

    private ProfileFactPatterns() {
        // static helpers only
    }

    /**
     * Reads one facts file of the profile.
     *
     * @param key the facts key, the file name without {@code .tsv}
     * @return the patterns by label, in file order
     */
    static Map<String, String> load(final String key) {
        final Map<String, String> patterns = new LinkedHashMap<>();
        for (final AiFactCounter counter : counters(key)) {
            patterns.put(counter.getLabel(), counter.getPattern());
        }
        return patterns;
    }

    /**
     * Returns one pattern, failing when the file has no counter of that label.
     *
     * @param patterns the patterns by label
     * @param label    the counter's label
     * @return its pattern
     */
    static String pattern(final Map<String, String> patterns, final String label) {
        final String pattern = patterns.get(label);
        if (pattern == null) {
            throw new IllegalStateException("no counter '" + label + "' in " + patterns.keySet());
        }
        return pattern;
    }

    /**
     * Reads one facts file of the profile as the production loader does, ignore counters and included
     * files included.
     *
     * @param key the facts key, the file name without {@code .tsv}
     * @return the counters in file order
     */
    static List<AiFactCounter> counters(final String key) {
        try {
            final List<AiFactCounter> facts = new AiFactDefinitionLoader(PROFILE_DIRECTORY.toFile())
                    .load(null, Collections.singletonList(key))
                    .get(0)
                    .getFacts();
            if (facts == null) {
                throw new IllegalStateException("no counters in " + key);
            }
            return facts;
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Counts a whole facts file over a source, as the facts line before a summary reports it: the ignore
     * counters blank their regions first.
     *
     * @param counters the counters of one facts file
     * @param source   the text
     * @return the count per reported label
     */
    static Map<String, Integer> counts(final List<AiFactCounter> counters, final String source) {
        return AiFactExtractor.counts(counters, source);
    }

    /**
     * Reads a file of the benchmark's language corpus ({@code docs/ai-index-benchmark/corpus-languages}).
     *
     * @param relative the path below the corpus directory
     * @return its text
     */
    static String languageCorpusFile(final String relative) {
        try {
            return new String(
                    Files.readAllBytes(Paths.get("../docs/ai-index-benchmark/corpus-languages")
                            .resolve(relative)),
                    StandardCharsets.UTF_8);
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Counts the matches the way {@link AiFactExtractor} does.
     *
     * @param pattern the pattern
     * @param source  the text
     * @return the number of non-overlapping matches
     */
    static int count(final String pattern, final String source) {
        final Matcher matcher = Pattern.compile(pattern).matcher(source);
        int count = 0;
        while (matcher.find()) {
            count++;
        }
        return count;
    }
}
