// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.document;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class AiMdHeaderSupportTest {

    @TempDir
    public Path folder;

    private final AiMdHeaderSupport headerSupport = new AiMdHeaderSupport();
    private final AiMdHeaderCodec headerCodec = new AiMdHeaderCodec();
    private final AiMdDocumentCodec documentCodec = new AiMdDocumentCodec();

    /** Fixed title used across all shouldWrite tests to reduce duplication. */
    private static final String FIXED_TITLE = "Test.java";

    /** Fixed creation timestamp used across all shouldWrite tests. */
    private static final String FIXED_D = "2026-03-16T00:00:00Z";

    /** Fixed generation timestamp used across all shouldWrite tests. */
    private static final String FIXED_T = "2026-03-16T00:00:10Z";

    /** Fixed generator version used in tests that do not exercise the generator-version change. */
    private static final String FIXED_G = "1.0.0";

    /** Fixed AI version used across all shouldWrite tests. */
    private static final String FIXED_A = "0.0.0";

    /** Fixed checksum used in tests that do not exercise checksum-change detection. */
    private static final String FIXED_CHECKSUM = "12345678";

    /**
     * Builds an {@link AiMdHeader} for {@link #FIXED_TITLE} / {@link AiMdHeaderCodec#NODE_TYPE_FILE}
     * using the supplied checksum and generator version. All other structural fields are
     * taken from the class-level {@code FIXED_*} constants.
     *
     * @param checksum         value for the {@code c} field
     * @param generatorVersion value for the {@code g} field
     * @return a fully populated header suitable for use in shouldWrite tests
     */
    private AiMdHeader buildHeader(final String checksum, final String generatorVersion) {
        return new AiMdHeader(
                FIXED_TITLE,
                AiMdHeaderCodec.HEADER_VERSION_1_0,
                checksum,
                FIXED_D,
                FIXED_T,
                generatorVersion,
                FIXED_A,
                AiMdHeaderCodec.NODE_TYPE_FILE);
    }

    // <editor-fold defaultstate="collapsed" desc="shouldWrite">
    @Test
    public void shouldWrite_fileDoesNotExist_returnsTrue() throws IOException {
        // arrange
        final Path target = folder.resolve("test.ai.md");
        final AiMdHeader header = buildHeader(FIXED_CHECKSUM, FIXED_G);

        // act
        final boolean result = headerSupport.shouldWrite(false, target, header);

        // assert
        assertThat(result, is(true));
    }

    @Test
    public void shouldWrite_matchingExistingHeaderWithBody_returnsFalse() throws IOException {
        // arrange
        final Path target = folder.resolve("test.ai.md");
        final AiMdHeader header = buildHeader("ABCDEF12", FIXED_G);
        final AiMdDocument document = new AiMdDocument(header, "Existing body content.\n");
        documentCodec.write(target, document);

        // act
        final boolean result = headerSupport.shouldWrite(false, target, header);

        // assert
        assertThat(result, is(false));
    }

    @Test
    public void shouldWrite_existingHeaderVersionMismatch_returnsTrue() throws IOException {
        // arrange: an existing doc whose header format version is NOT 1.0, with a non-blank body.
        final Path target = folder.resolve("test.ai.md");
        final AiMdHeader oldVersionHeader = new AiMdHeader(
                FIXED_TITLE, "0.9", "ABCDEF12", FIXED_D, FIXED_T, FIXED_G, FIXED_A, AiMdHeaderCodec.NODE_TYPE_FILE);
        documentCodec.write(target, new AiMdDocument(oldVersionHeader, "Existing body content.\n"));
        final AiMdHeader expected = buildHeader("ABCDEF12", FIXED_G);

        // act: the stored 0.9 header forces a rewrite regardless of field equality.
        final boolean result = headerSupport.shouldWrite(false, target, expected);

        // assert
        assertThat(result, is(true));
    }

    @Test
    public void shouldWrite_matchingExistingHeaderEmptyBody_returnsTrue() throws IOException {
        // arrange
        final Path target = folder.resolve("test.ai.md");
        final AiMdHeader header = buildHeader("ABCDEF12", FIXED_G);
        // write document with blank body to simulate a previously failed AI generation
        final AiMdDocument document = new AiMdDocument(header, "");
        documentCodec.write(target, document);

        // act
        final boolean result = headerSupport.shouldWrite(false, target, header);

        // assert
        assertThat(result, is(true));
    }

    @Test
    public void shouldWrite_checksumChanged_returnsTrue() throws IOException {
        // arrange
        final Path target = folder.resolve("test.ai.md");
        final AiMdHeader original = buildHeader("AAAAAAAA", FIXED_G);
        Files.write(target, headerCodec.write(original).getBytes(StandardCharsets.UTF_8));

        final AiMdHeader changed = buildHeader("BBBBBBBB", FIXED_G);

        // act
        final boolean result = headerSupport.shouldWrite(false, target, changed);

        // assert
        assertThat(result, is(true));
    }

    @Test
    public void shouldWrite_generatorVersionChanged_returnsTrue() throws IOException {
        // arrange
        final Path target = folder.resolve("test.ai.md");
        final AiMdHeader original = buildHeader(FIXED_CHECKSUM, FIXED_G);
        Files.write(target, headerCodec.write(original).getBytes(StandardCharsets.UTF_8));

        final AiMdHeader changed = buildHeader(FIXED_CHECKSUM, "2.0.0");

        // act
        final boolean result = headerSupport.shouldWrite(false, target, changed);

        // assert
        assertThat(result, is(true));
    }

    @Test
    public void shouldWrite_forceEnabled_returnsTrue() throws IOException {
        // arrange
        final Path target = folder.resolve("test.ai.md");
        final AiMdHeader header = buildHeader(FIXED_CHECKSUM, FIXED_G);
        Files.write(target, headerCodec.write(header).getBytes(StandardCharsets.UTF_8));

        // act
        final boolean result = headerSupport.shouldWrite(true, target, header);

        // assert
        assertThat(result, is(true));
    }
    // </editor-fold>
}
