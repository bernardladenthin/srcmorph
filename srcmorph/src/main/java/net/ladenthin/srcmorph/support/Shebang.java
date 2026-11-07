// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.support;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
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
 * the first word after it that is neither an option ({@code -i}) nor an assignment ({@code LANG=C}),
 * the argument of an option that takes one skipped (GNU {@code -u} / {@code --unset},
 * {@code -C} / {@code --chdir}, {@code -a} / {@code --argv0}; BSD {@code -P altpath}, {@code -L} and
 * {@code -U user}), and the first word of a {@code -S} / {@code --split-string=} string taken as written
 * ({@code -Spython3}, {@code --split-string=python3}). Combined short options ({@code -iS}) and quotes
 * are not read.
 * Only the first {@value #MAX_LINE_CHARS} characters of the line count, as Linux reads it. A name
 * matches the interpreter itself and its versions: {@code python} matches {@code python3} and
 * {@code python3.12}, {@code python3} matches {@code python3.12} but not {@code python} or
 * {@code python31}, {@code python3.1} not {@code python3.12}.</p>
 */
public final class Shebang {

    /** The characters of the first line that count; Linux reads 256 bytes of it. */
    public static final int MAX_LINE_CHARS = 256;

    /** Opens the line. */
    private static final String MARKER = "#!";

    /** The program that looks the interpreter up on the {@code PATH}. */
    private static final String ENV = "env";

    /** Opens an option of {@code env}. */
    private static final String OPTION = "-";

    /** The options of {@code env} whose argument is the next word. */
    private static final Set<String> OPTIONS_WITH_AN_ARGUMENT = Collections.unmodifiableSet(
            new HashSet<>(Arrays.asList("-u", "--unset", "-C", "--chdir", "-a", "--argv0", "-P", "-L", "-U")));

    /** The short option of {@code env} whose argument, written next to it, is the command line. */
    private static final String SPLIT_STRING = "-S";

    /** The long option of {@code env} whose argument after {@code =} is the command line. */
    private static final String SPLIT_STRING_LONG = "--split-string=";

    /** Marks an assignment {@code env} sets before it runs the interpreter. */
    private static final String ASSIGNMENT = "=";

    /** Separates the segments of the interpreter's path. */
    private static final char SEGMENT = '/';

    /**
     * The next word of the first line with the blanks before it: {@code \G} continues where the last one
     * ended, so the words end with the line.
     */
    private static final Pattern WORD = Pattern.compile("\\G[ \t]*+[^ \t\n]++");

    /** Utility class; not instantiable. */
    private Shebang() {
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
        final Matcher words = WORD.matcher(head.substring(MARKER.length(), Math.min(head.length(), MAX_LINE_CHARS)));
        if (!words.find()) {
            return null;
        }
        String name = lastSegment(words.group().trim());
        if (ENV.equals(name)) {
            name = afterEnv(words);
        }
        return name.isEmpty() ? null : name;
    }

    /**
     * Returns the program {@code env} runs: the first word after its options and assignments.
     *
     * @param words the words of the line after {@code env}
     * @return the program's name, or {@code ""} when the line names none
     */
    private static String afterEnv(final Matcher words) {
        boolean argument = false;
        while (words.find()) {
            final String word = words.group().trim();
            if (argument) {
                argument = false;
            } else if (OPTIONS_WITH_AN_ARGUMENT.contains(word)) {
                argument = true;
            } else if (word.startsWith(SPLIT_STRING_LONG)) {
                return lastSegment(word.substring(SPLIT_STRING_LONG.length()));
            } else if (word.startsWith(SPLIT_STRING) && word.length() > SPLIT_STRING.length()) {
                return lastSegment(word.substring(SPLIT_STRING.length()));
            } else if (!word.startsWith(OPTION) && !word.contains(ASSIGNMENT)) {
                return lastSegment(word);
            }
        }
        return "";
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
                    && isVersion(trimmed, interpreter.substring(trimmed.length()))) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns whether what follows a name is empty or a version of it: digits and dots, after a name that
     * ends with a digit only from a dot on, so its number does not grow ({@code python3} and {@code 1}).
     *
     * @param name   the name
     * @param suffix what follows the name
     * @return {@code true} for {@code ""}, {@code 3}, {@code 3.12}; after {@code python3} for {@code .12}
     *     but not {@code 1}
     */
    private static boolean isVersion(final String name, final String suffix) {
        if (!suffix.isEmpty() && Character.isDigit(name.charAt(name.length() - 1)) && suffix.charAt(0) != '.') {
            return false;
        }
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
