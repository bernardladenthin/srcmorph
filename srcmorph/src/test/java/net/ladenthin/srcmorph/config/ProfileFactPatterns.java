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
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The fact counters of the repository's current profile ({@code profiles/v2/<key>.tsv}), read with the
 * production parser, so a pattern test checks the file that ships and never a copy of it.
 */
final class ProfileFactPatterns {

    /** The current profile, relative to the {@code srcmorph} module (Surefire's working directory). */
    static final Path PROFILE_DIRECTORY = Paths.get("../profiles/v2");

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
        final Path file = PROFILE_DIRECTORY.resolve(key + ".tsv");
        final String text;
        try {
            text = new String(Files.readAllBytes(file), StandardCharsets.UTF_8);
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
        final Map<String, String> patterns = new LinkedHashMap<>();
        for (final AiFactCounter counter : AiFactDefinitionLoader.parse(file.toString(), text)) {
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
