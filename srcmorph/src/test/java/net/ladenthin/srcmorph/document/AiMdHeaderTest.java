// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.document;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;

public class AiMdHeaderTest {

    private static AiMdHeader headerWithChildren(final List<String> children) {
        return new AiMdHeader(
                "main/java/com/example",
                AiMdHeaderCodec.HEADER_VERSION_1_0,
                "AAAAAAAA",
                "2026-03-16T00:00:00Z",
                "2026-03-16T00:00:10Z",
                "1.0.0",
                "0.0.0",
                AiMdHeaderCodec.NODE_TYPE_PACKAGE,
                children);
    }

    // <editor-fold defaultstate="collapsed" desc="children: canonical constructor">
    @Test
    public void canonicalConstructor_storesChildrenInOrder() {
        // act
        final AiMdHeader header =
                headerWithChildren(Arrays.asList("[Foo.java](Foo.java.ai.md)", "[sub/](sub/package.ai.md)"));

        // assert
        assertThat(
                header.children(),
                is(equalTo(Arrays.asList("[Foo.java](Foo.java.ai.md)", "[sub/](sub/package.ai.md)"))));
    }

    @Test
    public void canonicalConstructor_nullChildren_throws() {
        // act + assert
        assertThrows(NullPointerException.class, () -> headerWithChildren(null));
    }

    @Test
    public void canonicalConstructor_defensivelyCopiesSourceList() {
        // arrange: a mutable source list passed into the header
        final List<String> source = new ArrayList<>();
        source.add("[Foo.java](Foo.java.ai.md)");
        final AiMdHeader header = headerWithChildren(source);

        // act: mutating the source after construction must not affect the header
        source.add("[Bar.java](Bar.java.ai.md)");

        // assert
        assertThat(header.children(), is(equalTo(Arrays.asList("[Foo.java](Foo.java.ai.md)"))));
    }

    @Test
    public void children_returnedListIsUnmodifiable() {
        // arrange
        final AiMdHeader header = headerWithChildren(Arrays.asList("[Foo.java](Foo.java.ai.md)"));

        // act + assert
        assertThrows(
                UnsupportedOperationException.class, () -> header.children().add("[Bar.java](Bar.java.ai.md)"));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="children: convenience constructor">
    @Test
    public void convenienceConstructor_hasEmptyChildren() {
        // act: the 8-arg constructor used by file nodes
        final AiMdHeader header = new AiMdHeader(
                "Foo.java",
                AiMdHeaderCodec.HEADER_VERSION_1_0,
                "AAAAAAAA",
                "2026-03-16T00:00:00Z",
                "2026-03-16T00:00:10Z",
                "1.0.0",
                "0.0.0",
                AiMdHeaderCodec.NODE_TYPE_FILE);

        // assert
        assertThat(header.children().isEmpty(), is(true));
    }
    // </editor-fold>
}
