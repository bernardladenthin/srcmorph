// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.routing;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.Test;

public class AiFileContextTest {

    @Test
    public void accessorsReturnConstructorValues() {
        final AiFileContext c = new AiFileContext("Foo.java", "src/Foo.java", 1234L, 56, 7890L);
        assertThat(c.fileName(), is(equalTo("Foo.java")));
        assertThat(c.relativePath(), is(equalTo("src/Foo.java")));
        assertThat(c.sizeBytes(), is(equalTo(1234L)));
        assertThat(c.lineCount(), is(equalTo(56)));
        assertThat(c.lastModifiedEpochMilli(), is(equalTo(7890L)));
        assertThat(c.head(), is(equalTo("")));
    }

    @Test
    public void head_isTheGivenTextAndNotInToString() {
        final AiFileContext c = new AiFileContext("d.yaml", "k8s/d.yaml", 10L, 1, 5L, "apiVersion: v1\n");
        assertThat(c.fileName(), is(equalTo("d.yaml")));
        assertThat(c.relativePath(), is(equalTo("k8s/d.yaml")));
        assertThat(c.sizeBytes(), is(equalTo(10L)));
        assertThat(c.lineCount(), is(equalTo(1)));
        assertThat(c.lastModifiedEpochMilli(), is(equalTo(5L)));
        assertThat(c.head(), is(equalTo("apiVersion: v1\n")));
        assertThat(c.toString().contains("apiVersion"), is(false));
    }
}
