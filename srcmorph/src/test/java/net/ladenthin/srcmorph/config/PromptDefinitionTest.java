// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.config;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.Test;

public class PromptDefinitionTest {

    @Test
    public void newDefinition_hasNoTemplateFile() {
        assertThat(new PromptDefinition().getTemplateFile(), is(nullValue()));
    }

    @Test
    public void templateFileRoundTrips() {
        final PromptDefinition definition = new PromptDefinition();
        definition.setTemplateFile("v1/file-body-java.md");

        assertThat(definition.getTemplateFile(), is("v1/file-body-java.md"));
        assertThat(definition.getTemplate(), is(nullValue()));
    }

    @Test
    public void toString_namesTheTemplateFile() {
        final PromptDefinition definition = new PromptDefinition();
        definition.setKey("file-body-java");
        definition.setTemplateFile("v1/file-body-java.md");

        assertThat(definition.toString(), containsString("templateFile=v1/file-body-java.md"));
    }
}
