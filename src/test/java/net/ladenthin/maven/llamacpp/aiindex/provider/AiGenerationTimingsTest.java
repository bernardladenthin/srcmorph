// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.maven.llamacpp.aiindex.provider;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.Test;

public class AiGenerationTimingsTest {

    @Test
    public void accessorsReturnConstructorValues() {
        final AiGenerationTimings t = new AiGenerationTimings("summary", 1500, 900.0d, 64, 45.0d);
        assertThat(t.text(), is("summary"));
        assertThat(t.promptTokens(), is(1500));
        assertThat(t.prefillTokensPerSecond(), is(900.0d));
        assertThat(t.predictedTokens(), is(64));
        assertThat(t.decodeTokensPerSecond(), is(45.0d));
    }
}
