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

    // <editor-fold defaultstate="collapsed" desc="templateFile">
    @Test
    public void templateFile_newDefinition_hasNoTemplateFile() {
        // act, assert
        assertThat(new PromptDefinition().getTemplateFile(), is(nullValue()));
    }

    @Test
    public void templateFile_set_roundTrips() {
        // arrange
        final PromptDefinition definition = new PromptDefinition();

        // act
        definition.setTemplateFile("v1/file-body-java.md");

        // assert
        assertThat(definition.getTemplateFile(), is("v1/file-body-java.md"));
        assertThat(definition.getTemplate(), is(nullValue()));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="toString">
    @Test
    public void toString_keyAndTemplateFileSet_namesTheTemplateFile() {
        // arrange
        final PromptDefinition definition = new PromptDefinition();
        definition.setKey("file-body-java");
        definition.setTemplateFile("v1/file-body-java.md");

        // act, assert
        assertThat(definition.toString(), containsString("templateFile=v1/file-body-java.md"));
    }
    // </editor-fold>
}
