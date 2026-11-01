// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.support;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;

/**
 * The files of a profile directory: a set of prompts ({@code <key>.md}) and fact counters
 * ({@code <key>.tsv}) that configurations name by key instead of listing them. A rule's
 * {@code promptKey} or {@code factsKey} is looked up as a file of that name in the configured
 * {@code profileDirectory} when no definition with that key is configured; an explicit definition
 * (inline, or naming its own file) always wins. Every such file is read the same way: UTF-8, a
 * leading byte order mark dropped, nothing else changed, and a file holding nothing but whitespace
 * is an error.
 */
public final class AiProfileFileSupport {

    /** File name extension of a prompt in a profile directory. */
    public static final String PROMPT_EXTENSION = ".md";

    /** File name extension of a fact-counter group in a profile directory. */
    public static final String FACTS_EXTENSION = ".tsv";

    /** The byte order mark a UTF-8 editor may put before the first character. */
    private static final char BYTE_ORDER_MARK = '\uFEFF';

    private AiProfileFileSupport() {
        // static helpers only
    }

    /**
     * The file a key names by convention in the profile directory.
     *
     * @param profileDirectory the configured profile directory
     * @param key              the prompt or facts key
     * @param extension        {@link #PROMPT_EXTENSION} or {@link #FACTS_EXTENSION}
     * @return the file {@code <profileDirectory>/<key><extension>}
     */
    public static File conventionalFile(final File profileDirectory, final String key, final String extension) {
        return new File(profileDirectory, key + extension);
    }

    /**
     * Reads one profile file.
     *
     * @param label what the file is for, for the messages (e.g. {@code promptDefinitions[2] (key x): templateFile})
     * @param path  the resolved path
     * @return the file's text, without a leading byte order mark
     * @throws IOException              if the file cannot be read, or the path is no valid path
     * @throws IllegalArgumentException if the file holds nothing but whitespace
     */
    public static String read(final String label, final String path) throws IOException {
        final String text;
        try {
            text = new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
        } catch (final IOException | InvalidPathException e) {
            throw new IOException(label + " " + path + " cannot be read: " + e, e);
        }
        final String content = !text.isEmpty() && text.charAt(0) == BYTE_ORDER_MARK ? text.substring(1) : text;
        if (new Java8CompatibilityHelper().isBlank(content)) {
            throw new IllegalArgumentException(label + " " + path + " is empty");
        }
        return content;
    }
}
