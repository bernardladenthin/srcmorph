// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.maven.llamacpp.aiindex.document;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;

public class AiMdHeaderCodecTest {

    private final AiMdHeaderCodec headerCodec = new AiMdHeaderCodec();

    // <editor-fold defaultstate="collapsed" desc="write">
    @Test
    public void write_fileNodeHeader_roundtripsToEqualHeader() {
        // arrange
        final AiMdHeader original = new AiMdHeader(
                "GenerateMojo.java",
                AiMdHeaderCodec.HEADER_VERSION_1_0,
                "D56BA12A",
                "2026-03-15T18:33:40Z",
                "2026-03-15T18:34:26Z",
                "0.1.0-SNAPSHOT",
                "0.0.0",
                AiMdHeaderCodec.NODE_TYPE_FILE);

        // act
        final String encoded = headerCodec.write(original);
        final AiMdHeader decoded = headerCodec.read(Arrays.asList(encoded.split("\\R")));

        // assert
        assertThat(decoded, is(equalTo(original)));
    }

    @Test
    public void write_packageNodeHeader_decodedFieldsMatchOriginal() {
        // arrange
        final AiMdHeader original = new AiMdHeader(
                "main/java/net/ladenthin/maven/llamacpp/aiindex",
                AiMdHeaderCodec.HEADER_VERSION_1_0,
                "9863444A",
                "2026-03-15T18:33:50Z",
                "2026-03-15T18:34:26Z",
                "0.1.0-SNAPSHOT",
                "0.0.0",
                AiMdHeaderCodec.NODE_TYPE_PACKAGE);

        // act
        final String encoded = headerCodec.write(original);
        final List<String> lines = Arrays.asList(encoded.split("\\R"));
        final AiMdHeader decoded = headerCodec.read(lines);

        // assert
        assertThat(decoded.title(), is(equalTo("main/java/net/ladenthin/maven/llamacpp/aiindex")));
        assertThat(decoded.h(), is(equalTo(AiMdHeaderCodec.HEADER_VERSION_1_0)));
        assertThat(decoded.c(), is(equalTo("9863444A")));
        assertThat(decoded.d(), is(equalTo("2026-03-15T18:33:50Z")));
        assertThat(decoded.t(), is(equalTo("2026-03-15T18:34:26Z")));
        assertThat(decoded.g(), is(equalTo("0.1.0-SNAPSHOT")));
        assertThat(decoded.a(), is(equalTo("0.0.0")));
        assertThat(decoded.x(), is(equalTo(AiMdHeaderCodec.NODE_TYPE_PACKAGE)));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="child links (F)">
    @Test
    public void write_headerWithChildren_emitsOneChildLinkLinePerChild() {
        // arrange
        final AiMdHeader header = new AiMdHeader(
                "main/java/com/example",
                AiMdHeaderCodec.HEADER_VERSION_1_0,
                "9863444A",
                "2026-03-15T18:33:50Z",
                "2026-03-15T18:34:26Z",
                "1.0.0",
                "0.0.0",
                AiMdHeaderCodec.NODE_TYPE_PACKAGE,
                Arrays.asList("[Foo.java](Foo.java.ai.md)", "[sub/](sub/package.ai.md)"));

        // act
        final String encoded = headerCodec.write(header);

        // assert
        assertThat(encoded, containsString("- F: [Foo.java](Foo.java.ai.md)\n"));
        assertThat(encoded, containsString("- F: [sub/](sub/package.ai.md)\n"));
    }

    @Test
    public void read_childLinkLines_collectedIntoChildrenInOrder() {
        // arrange
        final List<String> lines = Arrays.asList(
                "### main/java/com/example",
                "- H: 1.0",
                "- C: 9863444A",
                "- D: 2026-03-15T18:33:50Z",
                "- T: 2026-03-15T18:34:26Z",
                "- G: 1.0.0",
                "- A: 0.0.0",
                "- X: package",
                "- F: [Foo.java](Foo.java.ai.md)",
                "- F: [sub/](sub/package.ai.md)");

        // act
        final AiMdHeader decoded = headerCodec.read(lines);

        // assert
        assertThat(
                decoded.children(),
                is(equalTo(Arrays.asList("[Foo.java](Foo.java.ai.md)", "[sub/](sub/package.ai.md)"))));
    }

    @Test
    public void write_read_headerWithChildren_roundtripsToEqualHeader() {
        // arrange
        final AiMdHeader original = new AiMdHeader(
                "main/java/com/example",
                AiMdHeaderCodec.HEADER_VERSION_1_0,
                "9863444A",
                "2026-03-15T18:33:50Z",
                "2026-03-15T18:34:26Z",
                "1.0.0",
                "0.0.0",
                AiMdHeaderCodec.NODE_TYPE_PACKAGE,
                Arrays.asList("[Foo.java](Foo.java.ai.md)", "[sub/](sub/package.ai.md)"));

        // act
        final String encoded = headerCodec.write(original);
        final AiMdHeader decoded = headerCodec.read(Arrays.asList(encoded.split("\\R")));

        // assert
        assertThat(decoded, is(equalTo(original)));
    }
    // </editor-fold>
}
