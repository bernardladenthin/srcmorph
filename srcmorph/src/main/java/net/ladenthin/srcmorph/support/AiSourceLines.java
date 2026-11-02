// @formatter:off
// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
// @formatter:on
package net.ladenthin.srcmorph.support;

import java.util.regex.Pattern;

/**
 * The lines of a source text. A source is read once and its line endings are made {@code \n}
 * ({@link #normalize(String)}), so the prompt, the facts, the chunks, the plan and the checksum see the
 * same text whether a working copy has {@code \r\n} (Windows with {@code core.autocrlf}), {@code \r} or
 * {@code \n}.
 */
public final class AiSourceLines {

    /** The line feed every line ends with after {@link #normalize(String)}. */
    private static final char LINE_FEED = '\n';

    /** A line ending of any platform: {@code \r\n}, {@code \r} or {@code \n}. */
    private static final Pattern LINE_ENDING = Pattern.compile("\r\n|\r|\n");

    /** The line feed as a replacement string. */
    private static final String LINE_FEED_STRING = "\n";

    /** Utility class; not instantiable. */
    private AiSourceLines() {
        // no-op
    }

    /**
     * Returns the text with every {@code \r\n} and every lone {@code \r} replaced by {@code \n}.
     *
     * @param text the source text
     * @return the text with {@code \n} line endings only
     */
    public static String normalize(final String text) {
        return text.indexOf('\r') < 0 ? text : LINE_ENDING.matcher(text).replaceAll(LINE_FEED_STRING);
    }

    /**
     * Returns the bytes with every {@code \r\n} and every lone {@code \r} replaced by {@code \n}: the
     * line endings of {@link #normalize(String)} on the bytes themselves, so a checksum over them sees
     * every byte of a file that is not valid UTF-8 (whose decoded text has the replacement character
     * where those bytes were) and, for valid UTF-8, equals the checksum of the normalized text: a
     * {@code \r} or {@code \n} byte is never part of a multi-byte UTF-8 sequence.
     *
     * @param bytes the file's bytes
     * @return the bytes with {@code \n} line endings only (the same array when it has no {@code \r})
     */
    public static byte[] normalize(final byte[] bytes) {
        boolean carriageReturn = false;
        for (final byte b : bytes) {
            if (b == '\r') {
                carriageReturn = true;
                break;
            }
        }
        if (!carriageReturn) {
            return bytes;
        }
        final byte[] out = new byte[bytes.length];
        int length = 0;
        for (int i = 0; i < bytes.length; i++) {
            if (bytes[i] == '\r') {
                out[length++] = (byte) LINE_FEED;
                if (i + 1 < bytes.length && bytes[i + 1] == '\n') {
                    i++;
                }
            } else {
                out[length++] = bytes[i];
            }
        }
        return java.util.Arrays.copyOf(out, length);
    }

    /**
     * Counts the lines of a text: its line endings ({@code \r\n}, {@code \r} or {@code \n}), plus one when
     * the text does not end with one, so a final line ending adds no empty line. An empty text has none.
     *
     * @param text the source text
     * @return the line count
     */
    public static int count(final String text) {
        final String normalized = normalize(text);
        if (normalized.isEmpty()) {
            return 0;
        }
        int lineFeeds = 0;
        for (int i = 0; i < normalized.length(); i++) {
            if (normalized.charAt(i) == LINE_FEED) {
                lineFeeds++;
            }
        }
        return normalized.charAt(normalized.length() - 1) == LINE_FEED ? lineFeeds : lineFeeds + 1;
    }

    /**
     * Splits a text at its line endings, keeping a trailing empty element after a final line ending (as
     * {@code String.split(regex, -1)} does).
     *
     * @param text the source text
     * @return the lines, without their line endings
     */
    public static String[] split(final String text) {
        return LINE_ENDING.split(text, -1);
    }
}
