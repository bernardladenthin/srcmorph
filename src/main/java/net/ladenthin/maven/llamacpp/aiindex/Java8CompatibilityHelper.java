// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.maven.llamacpp.aiindex;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Wrapper methods for Java 9+ APIs to provide Java 1.8 compatibility.
 * This class centralizes all compatibility layer logic and can be mocked for testing.
 */
public class Java8CompatibilityHelper {

    /** Creates a new {@link Java8CompatibilityHelper}. */
    public Java8CompatibilityHelper() {
        // no-op
    }

    /**
     * Wrapper for {@code String#isBlank()} (Java 11+).
     * Returns {@code true} if the string is empty or contains only whitespace,
     * {@code false} otherwise.
     *
     * @param str the string to check; must not be {@code null}
     * @return {@code true} if the string is empty or blank, {@code false} otherwise
     * @throws NullPointerException if {@code str} is {@code null}
     */
    public boolean isBlank(final String str) {
        return str.isEmpty() || str.trim().isEmpty();
    }

    /**
     * Wrapper for {@code String#formatted(Object...)} (Java 15+).
     * Equivalent to {@link String#format(String, Object...)}.
     *
     * @param format the format string
     * @param args   the arguments referenced by the format specifiers in the format string
     * @return a formatted string
     */
    // Not annotated @FormatMethod because callers (AiPromptSupport#buildPrompt) pass a
    // runtime template loaded from configuration, not a compile-time constant string —
    // marking this @FormatMethod would propagate FormatStringAnnotation into every
    // caller and break the build.
    @SuppressWarnings("AnnotateFormatMethod")
    public String formatted(final String format, final Object... args) {
        return String.format(format, args);
    }

    /**
     * Wrapper for {@code Files#readString(Path)} (Java 11+).
     * Reads all bytes from a file and decodes them using UTF-8.
     *
     * @param path the path to the file to read
     * @return the file content as a string
     * @throws IOException if an I/O error occurs reading from the file
     */
    public String readString(final Path path) throws IOException {
        return new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
    }

    /**
     * Wrapper for {@code Files#writeString(Path, CharSequence, Charset)} (Java 11+).
     * Writes a string to a file using the specified charset.
     *
     * @param path    the path to the file to write
     * @param content the string content to write
     * @param charset the charset to encode the content with; defaults to UTF-8 if {@code null}
     * @throws IOException if an I/O error occurs writing to the file
     */
    public void writeString(final Path path, final String content, final Charset charset) throws IOException {
        final Charset targetCharset = charset != null ? charset : StandardCharsets.UTF_8;
        Files.write(path, content.getBytes(targetCharset));
    }

    /**
     * Wrapper for {@code Stream#toList()} (Java 16+).
     * Collects stream elements into a {@link List}.
     *
     * @param stream the stream to collect
     * @param <T>    the element type
     * @return a list containing the stream elements
     */
    public <T> List<T> toList(final Stream<T> stream) {
        return stream.collect(Collectors.toList());
    }

    /**
     * Wrapper for {@code List#of(Object...)} (Java 9+).
     * Creates an immutable list containing the specified elements.
     *
     * @param elements the elements to include in the list
     * @param <T>      the element type
     * @return a list containing the specified elements
     */
    // @SafeVarargs suppresses the warning at the listOf declaration; @SuppressWarnings
    // is additionally needed because javac still flags the forwarded Arrays.asList(...)
    // call as a possible-heap-pollution site even though Arrays.asList is itself
    // @SafeVarargs in the JDK.
    @SafeVarargs
    @SuppressWarnings({"unchecked", "varargs"})
    public final <T> List<T> listOf(final T... elements) {
        return Arrays.asList(elements);
    }
}
