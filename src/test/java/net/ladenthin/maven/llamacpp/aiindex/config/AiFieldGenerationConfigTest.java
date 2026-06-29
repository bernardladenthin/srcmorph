// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.maven.llamacpp.aiindex.config;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.Test;

public class AiFieldGenerationConfigTest {

    @Test
    public void id_defaultsNullAndRoundTrips() {
        final AiFieldGenerationConfig c = new AiFieldGenerationConfig();
        assertThat(c.getId(), is(nullValue()));
        c.setId("java-small");
        assertThat(c.getId(), is(equalTo("java-small")));
    }

    @Test
    public void promptKeyRoundTrips() {
        final AiFieldGenerationConfig c = new AiFieldGenerationConfig();
        c.setPromptKey("file-body-java");
        assertThat(c.getPromptKey(), is(equalTo("file-body-java")));
    }

    @Test
    public void aiDefinitionKeyRoundTrips() {
        final AiFieldGenerationConfig c = new AiFieldGenerationConfig();
        c.setAiDefinitionKey("gpt-oss-20B-c96k");
        assertThat(c.getAiDefinitionKey(), is(equalTo("gpt-oss-20B-c96k")));
    }

    @Test
    public void condition_defaultsNullAndRoundTrips() {
        final AiFieldGenerationConfig c = new AiFieldGenerationConfig();
        assertThat(c.getCondition(), is(nullValue()));
        final AiCondition condition = new AiCondition();
        c.setCondition(condition);
        assertThat(c.getCondition(), is(notNullValue()));
        assertThat(c.getCondition(), is(condition));
    }

    @Test
    public void priority_defaultsZeroAndRoundTrips() {
        final AiFieldGenerationConfig c = new AiFieldGenerationConfig();
        assertThat(c.getPriority(), is(equalTo(0)));
        c.setPriority(10);
        assertThat(c.getPriority(), is(equalTo(10)));
    }

    @Test
    public void fallback_defaultsFalseAndTogglesTrue() {
        final AiFieldGenerationConfig c = new AiFieldGenerationConfig();
        assertThat(c.isFallback(), is(false));
        c.setFallback(true);
        assertThat(c.isFallback(), is(true));
    }

    @Test
    public void skip_defaultsFalseAndTogglesTrue() {
        final AiFieldGenerationConfig c = new AiFieldGenerationConfig();
        assertThat(c.isSkip(), is(false));
        c.setSkip(true);
        assertThat(c.isSkip(), is(true));
    }
}
