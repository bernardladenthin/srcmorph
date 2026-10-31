// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.prompt;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.Test;

public class AiPromptDefinitionTest {

    @Test
    public void newDefinition_hasNoTemplateFile() {
        assertThat(new AiPromptDefinition().getTemplateFile(), is(nullValue()));
    }

    @Test
    public void templateFileRoundTrips() {
        final AiPromptDefinition definition = new AiPromptDefinition();
        definition.setTemplateFile("v1/file-body-java.md");

        assertThat(definition.getTemplateFile(), is("v1/file-body-java.md"));
        assertThat(definition.getTemplate(), is(nullValue()));
    }

    @Test
    public void toString_namesTheTemplateFile() {
        final AiPromptDefinition definition = new AiPromptDefinition();
        definition.setKey("file-body-java");
        definition.setTemplateFile("v1/file-body-java.md");

        assertThat(definition.toString(), containsString("templateFile=v1/file-body-java.md"));
    }
}
