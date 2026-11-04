// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.support;

import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jspecify.annotations.Nullable;

/**
 * Reads the interpreter a script names on its first line, the {@code #!} line a Unix kernel reads:
 * {@code #!/bin/bash} names {@code bash}, {@code #!/usr/bin/env perl} and
 * {@code #!/usr/bin/env -S python3 -u} name {@code perl} and {@code python3}. So a script without an
 * extension (a {@code bin/deploy}, a Git hook) is selected and routed by what runs it.
 *
 * <p>The interpreter is the last path segment of the line's first word; when that is {@code env},
 * the first word after it that is neither an option ({@code -S}) nor an assignment ({@code LANG=C}).
 * Only the first {@value #MAX_LINE_CHARS} characters of the line count, as Linux reads it. A name
 * matches the interpreter itself and its versions: {@code python} matches {@code python3} and
 * {@code python3.12}, {@code python3} matches {@code python3.12} but not {@code python}.</p>
 */
public final class AiShebang {

    /** The characters of the first line that count; Linux reads 256 bytes of it. */
    public static final int MAX_LINE_CHARS = 256;

    /** Opens the line. */
    private static final String MARKER = "#!";

    /** The program that looks the interpreter up on the {@code PATH}. */
    private static final String ENV = "env";

    /** Opens an option of {@code env}. */
    private static final String OPTION = "-";

    /** Marks an assignment {@code env} sets before it runs the interpreter. */
    private static final char ASSIGNMENT = '=';

    /** Separates the segments of the interpreter's path. */
    private static final char SEGMENT = '/';

    /** A word of the line. */
    private static final Pattern WORD = Pattern.compile("[^ \t]+");

    /** Utility class; not instantiable. */
    private AiShebang() {
        // no-op
    }

    /**
     * Returns the interpreter a text's first line names.
     *
     * @param head the start of the file, {@code \n} line endings
     * @return the interpreter, or {@code null} when the text does not start with a {@code #!} line that
     *     names one
     */
    public static @Nullable String interpreter(final String head) {
        if (!head.startsWith(MARKER)) {
            return null;
        }
        final int newline = head.indexOf('\n');
        final int end = Math.min(newline < 0 ? head.length() : newline, MAX_LINE_CHARS);
        final Matcher words = WORD.matcher(head.substring(MARKER.length(), end));
        if (!words.find()) {
            return null;
        }
        String name = lastSegment(words.group());
        if (ENV.equals(name)) {
            name = "";
            while (words.find()) {
                final String word = words.group();
                if (!word.startsWith(OPTION) && word.indexOf(ASSIGNMENT) < 0) {
                    name = lastSegment(word);
                    break;
                }
            }
        }
        return name.isEmpty() ? null : name;
    }

    /**
     * Returns whether an interpreter is one of the names or a version of one.
     *
     * @param interpreter the interpreter, from {@link #interpreter(String)}
     * @param names       the names; {@code null} and blank entries match nothing
     * @return {@code true} when one of them matches
     */
    public static boolean matches(final String interpreter, final Collection<? extends @Nullable String> names) {
        for (final @Nullable String name : names) {
            if (name == null) {
                continue;
            }
            final String trimmed = name.trim();
            if (!trimmed.isEmpty()
                    && interpreter.startsWith(trimmed)
                    && isVersion(interpreter.substring(trimmed.length()))) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns whether a text is empty or a version: digits and dots.
     *
     * @param suffix what follows the name
     * @return {@code true} for {@code ""}, {@code 3}, {@code 3.12}
     */
    private static boolean isVersion(final String suffix) {
        for (int i = 0; i < suffix.length(); i++) {
            final char c = suffix.charAt(i);
            if (c != '.' && (c < '0' || c > '9')) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns the last segment of a path.
     *
     * @param path the path
     * @return what follows its last {@code /}
     */
    private static String lastSegment(final String path) {
        return path.substring(path.lastIndexOf(SEGMENT) + 1);
    }
}
