// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.support;

import java.util.regex.Pattern;

/**
 * Translates the glob syntax of the configuration ({@code excludes}, the {@code pathGlob} and
 * {@code fileNames} conditions, the run's {@code fileNames}) into an anchored regular expression:
 * <ul>
 *   <li>{@code *} — any run of characters except {@code /} (one path segment)</li>
 *   <li>{@code **} — any run of characters including {@code /} (spans directories). When written as
 *       {@code **}{@code /} it also matches zero directories, so {@code **}{@code /package-info.java}
 *       matches the file at the project root as well as nested under any package.</li>
 *   <li>{@code ?} — exactly one character except {@code /}</li>
 *   <li><code>{a,b}</code> — one of the comma-separated alternatives, each itself a glob (nesting
 *       allowed): <code>**&#47;*.{yml,yaml}</code>; a <code>}</code> without its opener is a
 *       literal</li>
 *   <li>every other character is a literal</li>
 * </ul>
 */
public final class Glob {

    /** Opens an alternation. */
    private static final char GROUP_OPEN = '{';

    /** Closes an alternation. */
    private static final char GROUP_CLOSE = '}';

    /** Separates the alternatives of an alternation. */
    private static final char ALTERNATIVE = ',';

    /** Utility class; not instantiable. */
    private Glob() {
        // no-op
    }

    /**
     * Compiles a glob into an anchored pattern.
     *
     * @param glob       the glob (already trimmed and non-empty)
     * @param ignoreCase whether letters match in any case
     * @return the pattern; {@link java.util.regex.Matcher#matches()} tells whether a path matches it
     * @throws IllegalArgumentException if an alternation is not closed
     */
    public static Pattern compile(final String glob, final boolean ignoreCase) {
        final String regex = toRegex(glob);
        return ignoreCase
                ? Pattern.compile(regex, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE)
                : Pattern.compile(regex);
    }

    /**
     * Translates a glob into an anchored regular expression.
     *
     * @param glob the glob (already trimmed and non-empty)
     * @return the equivalent anchored regex source
     * @throws IllegalArgumentException if an alternation is not closed
     */
    static String toRegex(final String glob) {
        final int length = glob.length();
        final StringBuilder regex = new StringBuilder();
        regex.append('^');
        int depth = 0;
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
            } else if (c == GROUP_OPEN) {
                regex.append("(?:");
                depth++;
                i += 1;
            } else if (c == ALTERNATIVE && depth > 0) {
                regex.append('|');
                i += 1;
            } else if (c == GROUP_CLOSE && depth > 0) {
                regex.append(')');
                depth--;
                i += 1;
            } else {
                if (isRegexMeta(c)) {
                    regex.append('\\');
                }
                regex.append(c);
                i += 1;
            }
        }
        if (depth > 0) {
            throw new IllegalArgumentException("the glob '" + glob + "' opens a '{' it does not close");
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
