// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.maven.llamacpp.aiindex;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import lombok.ToString;

/** Reads and writes {@code .ai.md} documents (header plus body) from and to disk. */
@ToString
public class AiMdDocumentCodec {

    /** Creates a new {@link AiMdDocumentCodec}. */
    public AiMdDocumentCodec() {
        // no-op
    }

    /**
     * Separator line between the metadata header and document body.
     */
    public static final String HEADER_BODY_SEPARATOR = "---";

    private final Java8CompatibilityHelper compatibilityHelper = new Java8CompatibilityHelper();

    /**
     * Reads an {@link AiMdDocument} from a file.
     *
     * @param file path to the {@code .ai.md} file to read
     * @return parsed document
     * @throws IOException if the file cannot be read
     */
    public AiMdDocument read(final Path file) throws IOException {
        return read(Files.readAllLines(file, StandardCharsets.UTF_8));
    }

    /**
     * Parses an {@link AiMdDocument} from the given lines.
     *
     * <p>Package-private: production code reaches this through
     * {@link #read(Path)}; the line-form overload is a test seam used by
     * unit tests that build the input directly without round-tripping
     * through the file system.
     *
     * @param lines raw lines of the {@code .ai.md} file
     * @return parsed document
     */
    AiMdDocument read(final List<String> lines) {
        final AiMdHeader header = new AiMdHeaderCodec().read(lines);

        final StringBuilder body = new StringBuilder();
        boolean bodyStarted = false;
        boolean headerFinished = false;

        for (String line : lines) {
            if (!headerFinished) {
                if (line.startsWith(AiMdHeaderCodec.HEADER_TITLE_PREFIX)
                        || line.startsWith(AiMdHeaderCodec.HEADER_FIELD_PREFIX)) {
                    continue;
                }

                if (compatibilityHelper.isBlank(line)) {
                    headerFinished = true;
                    continue;
                }

                headerFinished = true;
            }

            if (!bodyStarted) {
                if (compatibilityHelper.isBlank(line) || HEADER_BODY_SEPARATOR.equals(line)) {
                    continue;
                }
                bodyStarted = true;
            }

            body.append(line).append('\n');
        }

        return new AiMdDocument(header, body.toString());
    }

    /**
     * Renders the given document to its serialised {@code .ai.md} string form.
     *
     * <p>Package-private: production code reaches this through
     * {@link #write(Path, AiMdDocument)}; the string-form overload is a
     * test seam used by unit tests that assert against the rendered text
     * without round-tripping through the file system.
     *
     * @param document document to serialise
     * @return serialised document text
     */
    String write(final AiMdDocument document) {
        final StringBuilder builder = new StringBuilder();
        builder.append(new AiMdHeaderCodec().write(document.header()));

        if (!compatibilityHelper.isBlank(document.body())) {
            builder.append(HEADER_BODY_SEPARATOR).append('\n');
            builder.append(document.body());
            if (!document.body().endsWith("\n")) {
                builder.append('\n');
            }
        }

        return builder.toString();
    }

    /**
     * Writes the given document to a UTF-8 file.
     *
     * @param file     destination file
     * @param document document to write
     * @throws IOException if the file cannot be written
     */
    public void write(final Path file, final AiMdDocument document) throws IOException {
        compatibilityHelper.writeString(file, write(document), StandardCharsets.UTF_8);
    }
}
