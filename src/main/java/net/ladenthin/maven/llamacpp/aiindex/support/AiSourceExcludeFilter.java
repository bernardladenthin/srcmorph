// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.maven.llamacpp.aiindex.support;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;
import lombok.ToString;
import org.jspecify.annotations.Nullable;

/**
 * Matches source paths against a list of glob exclude patterns so the indexer can skip trivial or
 * generated files (for example {@code package-info.java}, {@code module-info.java}, or generated
 * sources) and keep the index focused on meaningful code.
 *
 * <p>Patterns are matched against the file path <em>relative to the project base directory</em>,
 * always with {@code /} separators, so the same pattern works on Windows and POSIX. The glob syntax
 * is the familiar subset:</p>
 * <ul>
 *   <li>{@code *} — any run of characters except {@code /} (one path segment)</li>
 *   <li>{@code **} — any run of characters including {@code /} (spans directories). When written as
 *       {@code **}{@code /} it also matches zero directories, so {@code **}{@code /package-info.java}
 *       matches the file at the project root as well as nested under any package.</li>
 *   <li>{@code ?} — exactly one character except {@code /}</li>
 *   <li>every other character is a literal</li>
 * </ul>
 *
 * <p>Matching is case-sensitive and anchored: the whole relative path must match. An empty (or
 * {@code null}) pattern list excludes nothing, preserving the historical "index everything" behaviour.</p>
 */
@ToString
public final class AiSourceExcludeFilter {

    private final List<Pattern> patterns;

    /**
     * Creates a filter from the given glob patterns.
     *
     * @param globPatterns exclude globs; {@code null} or empty means "exclude nothing". Individual
     *                     {@code null} or blank entries are ignored.
     */
    public AiSourceExcludeFilter(final @Nullable Collection<String> globPatterns) {
        final List<Pattern> compiled = new ArrayList<>();
        if (globPatterns != null) {
            for (final String glob : globPatterns) {
                if (glob == null || glob.trim().isEmpty()) {
                    continue;
                }
                compiled.add(Pattern.compile(globToRegex(glob.trim())));
            }
        }
        this.patterns = compiled;
    }

    /**
     * Returns {@code true} when {@code relativePath} matches any configured exclude pattern.
     *
     * @param relativePath the source path relative to the project base directory, using {@code /}
     *                     separators
     * @return {@code true} if the path should be excluded from indexing
     */
    public boolean isExcluded(final String relativePath) {
        for (final Pattern pattern : patterns) {
            if (pattern.matcher(relativePath).matches()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Translates a glob pattern into an anchored regular expression. See the class Javadoc for the
     * supported syntax.
     *
     * @param glob the glob pattern (already trimmed and non-empty)
     * @return the equivalent anchored regex source
     */
    private static String globToRegex(final String glob) {
        final int length = glob.length();
        final StringBuilder regex = new StringBuilder();
        regex.append('^');
        int i = 0;
        while (i < length) {
            final char c = glob.charAt(i);
            if (c == '*' && i + 1 < length && glob.charAt(i + 1) == '*') {
                // "**" — consume both stars, then fold a directly-following "/" into the same token so
                // "**/" can match zero directories (matching the file at the root as well as nested).
                i += 2;
                if (i < length && glob.charAt(i) == '/') {
                    regex.append("(?:.*/)?");
                    i += 1;
                } else {
                    regex.append(".*");
                }
            } else if (c == '*') {
                // "*" matches anything within a single path segment.
                regex.append("[^/]*");
                i += 1;
            } else if (c == '?') {
                regex.append("[^/]");
                i += 1;
            } else {
                if (isRegexMeta(c)) {
                    regex.append('\\');
                }
                regex.append(c);
                i += 1;
            }
        }
        regex.append('$');
        return regex.toString();
    }

    /**
     * Returns {@code true} when {@code c} is a regular-expression metacharacter that must be escaped
     * to be matched literally.
     *
     * @param c the candidate character
     * @return {@code true} if {@code c} needs escaping in a regex
     */
    private static boolean isRegexMeta(final char c) {
        return c == '\\' || c == '.' || c == '(' || c == ')' || c == '[' || c == ']' || c == '{' || c == '}' || c == '+'
                || c == '^' || c == '$' || c == '|';
    }
}
