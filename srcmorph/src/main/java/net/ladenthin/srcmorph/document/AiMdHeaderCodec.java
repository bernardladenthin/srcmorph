// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.document;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.ToString;
import net.ladenthin.srcmorph.support.Java8CompatibilityHelper;

/** Reads and writes the metadata header section of an {@code .ai.md} document. */
@ToString
public class AiMdHeaderCodec {

    /** Creates a new {@link AiMdHeaderCodec}. */
    public AiMdHeaderCodec() {
        // no-op
    }

    /**
     * Prefix used for the title line in every AI index document header.
     * Example: {@code "### MyClass.java"}.
     */
    public static final String HEADER_TITLE_PREFIX = "### ";

    /**
     * Prefix used for each key-value field line in every AI index document header.
     * Example: {@code "- H: 1.0"}.
     */
    public static final String HEADER_FIELD_PREFIX = "- ";

    /** Field key for the header format version ({@code h}). */
    public static final String FIELD_KEY_H = "H";

    /** Field key for the source file checksum ({@code c}). */
    public static final String FIELD_KEY_C = "C";

    /** Field key for the index creation date ({@code d}). */
    public static final String FIELD_KEY_D = "D";

    /** Field key for the last generation timestamp ({@code t}). */
    public static final String FIELD_KEY_T = "T";

    /** Field key for the plugin version ({@code g}). */
    public static final String FIELD_KEY_G = "G";

    /** Field key for the AI model version ({@code a}). */
    public static final String FIELD_KEY_A = "A";

    /** Field key for the node type ({@code x}). */
    public static final String FIELD_KEY_X = "X";

    /**
     * Field key for a child-link line ({@code F}). Unlike the scalar keys this one may appear
     * multiple times — once per child — and the values are collected into {@link AiMdHeader#children()}.
     */
    public static final String FIELD_KEY_F = "F";

    /**
     * Full line prefix for a child-link header line ({@code "- F: "}). Built from
     * {@link #HEADER_FIELD_PREFIX} + {@link #FIELD_KEY_F} so the {@code write} side stays in sync with
     * the {@code read} side's key match, and so the single-character {@link #FIELD_KEY_F} is never
     * appended on its own (SpotBugs UCPM).
     */
    private static final String CHILD_FIELD_LINE_PREFIX = HEADER_FIELD_PREFIX + FIELD_KEY_F + ": ";

    /**
     * Current metadata header format version written into every AI document.
     *
     * @see AiMdHeader#h()
     */
    public static final String HEADER_VERSION_1_0 = "1.0";

    /**
     * Node type value for source-file-level AI index documents.
     *
     * @see AiMdHeader#x()
     */
    public static final String NODE_TYPE_FILE = "file";

    /**
     * Node type value for package-level AI index documents.
     *
     * @see AiMdHeader#x()
     */
    public static final String NODE_TYPE_PACKAGE = "package";

    /**
     * Node type value for the single project-level AI index document — the top of the
     * three-level index. The {@link #PROJECT_AI_MD_FILENAME} file lists every package with a
     * one-line lead and a relative link, so an agent can read one compact file and navigate down.
     *
     * @see AiMdHeader#x()
     */
    public static final String NODE_TYPE_PROJECT = "project";

    /**
     * Title of the root AI index node representing the top-level output directory.
     *
     * @see AiMdHeader#title()
     */
    public static final String ROOT_NODE_TITLE = "ai";

    /**
     * File extension appended to every source file name to produce its AI index file name.
     * Example: {@code "MyClass.java"} becomes {@code "MyClass.java.ai.md"}.
     */
    public static final String AI_MD_EXTENSION = ".ai.md";

    /**
     * File name used for package-level AI index documents.
     * One {@value} file is created per indexed package directory.
     */
    public static final String PACKAGE_AI_MD_FILENAME = "package.ai.md";

    /**
     * File name used for the single project-level AI index document, written into the output root.
     * One {@value} file is created per project; it lists every {@link #PACKAGE_AI_MD_FILENAME} with
     * its one-line lead and a relative link, forming the navigable top of the three-level index.
     */
    public static final String PROJECT_AI_MD_FILENAME = "project.ai.md";

    /**
     * Prefix of internally generated marker files that must be excluded from content
     * listings and checksum calculations.
     */
    public static final String GENERATED_BY_PREFIX = ".generated-by-";

    private final Java8CompatibilityHelper compatibilityHelper = new Java8CompatibilityHelper();

    /**
     * Parses an {@link AiMdHeader} from the given header lines.
     *
     * <p>The single-letter scalar keys ({@code H}/{@code C}/{@code D}/{@code T}/{@code G}/{@code A}/{@code X})
     * map to one value each; the repeatable {@link #FIELD_KEY_F} key is collected, in encounter order, into
     * {@link AiMdHeader#children()}. Callers must pass header lines only (see {@link AiMdDocumentCodec}),
     * so a {@code - F:} line in the document body is never mistaken for a child link.</p>
     *
     * @param lines lines that include the title and key-value field lines
     * @return parsed header (missing scalar fields default to the empty string)
     */
    public AiMdHeader read(final List<String> lines) {
        String title = null;
        final Map<String, String> values = new HashMap<>(lines.size());
        final List<String> children = new ArrayList<>();

        for (String line : lines) {
            if (line.startsWith(HEADER_TITLE_PREFIX)) {
                title = line.substring(HEADER_TITLE_PREFIX.length()).trim();
                continue;
            }

            if (!line.startsWith(HEADER_FIELD_PREFIX)) {
                continue;
            }

            final int colonIndex = line.indexOf(':');
            if (colonIndex < 0 || colonIndex < HEADER_FIELD_PREFIX.length() + 1) {
                continue;
            }

            final String key =
                    line.substring(HEADER_FIELD_PREFIX.length(), colonIndex).trim();
            final String value = line.substring(colonIndex + 1).trim();
            if (FIELD_KEY_F.equals(key)) {
                children.add(value);
            } else {
                values.put(key, value);
            }
        }

        return new AiMdHeader(
                title != null ? title : "",
                valueOrEmpty(values, FIELD_KEY_H),
                valueOrEmpty(values, FIELD_KEY_C),
                valueOrEmpty(values, FIELD_KEY_D),
                valueOrEmpty(values, FIELD_KEY_T),
                valueOrEmpty(values, FIELD_KEY_G),
                valueOrEmpty(values, FIELD_KEY_A),
                valueOrEmpty(values, FIELD_KEY_X),
                children);
    }

    /**
     * Renders the given header to its serialised string form: the scalar fields followed by one
     * {@link #FIELD_KEY_F} line per {@link AiMdHeader#children() child link}.
     *
     * @param header header to serialise
     * @return serialised header text
     */
    public String write(final AiMdHeader header) {
        final StringBuilder builder = new StringBuilder(compatibilityHelper.formatted(
                "### %s\n" + "- H: %s\n"
                        + "- C: %s\n"
                        + "- D: %s\n"
                        + "- T: %s\n"
                        + "- G: %s\n"
                        + "- A: %s\n"
                        + "- X: %s\n",
                header.title(), header.h(), header.c(), header.d(), header.t(), header.g(), header.a(), header.x()));
        for (final String child : header.children()) {
            builder.append(CHILD_FIELD_LINE_PREFIX).append(child).append('\n');
        }
        return builder.toString();
    }

    private String valueOrEmpty(final Map<String, String> values, final String key) {
        final String value = values.get(key);
        return value != null ? value : "";
    }

    /**
     * Reads an {@link AiMdHeader} from a file.
     *
     * @param file path to the {@code .ai.md} file to read
     * @return parsed header
     * @throws IOException if the file cannot be read
     */
    public AiMdHeader read(final Path file) throws IOException {
        return read(Files.readAllLines(file, StandardCharsets.UTF_8));
    }
}
