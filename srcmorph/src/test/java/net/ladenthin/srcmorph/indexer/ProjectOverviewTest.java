// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.indexer;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Collections;
import net.ladenthin.srcmorph.CommonTestFixtures;
import net.ladenthin.srcmorph.config.AiFieldGenerationConfig;
import net.ladenthin.srcmorph.config.AiModelDefinitionSupport;
import net.ladenthin.srcmorph.prompt.AiPromptSupport;
import net.ladenthin.srcmorph.provider.AiGenerationProvider;
import net.ladenthin.srcmorph.provider.MockAiGenerationProvider;
import org.junit.jupiter.api.Test;

public class ProjectOverviewTest {

    private static AiFieldGenerationConfig rule() {
        final AiFieldGenerationConfig rule = new AiFieldGenerationConfig();
        rule.setPromptKey("project-overview");
        rule.setAiDefinitionKey("default");
        return rule;
    }

    @Test
    public void accessors_returnWhatTheOverviewWasBuiltWith() {
        // arrange
        final AiGenerationProvider provider = new MockAiGenerationProvider();
        final AiPromptSupport promptSupport = new AiPromptSupport(Collections.emptyList());
        final AiModelDefinitionSupport modelSupport = CommonTestFixtures.createDefaultAiModelDefinitionSupport();

        // act
        final ProjectOverview overview = new ProjectOverview(provider, rule(), promptSupport, modelSupport);

        // assert
        assertThat(overview.generationProvider(), is(sameInstance(provider)));
        assertThat(overview.promptSupport(), is(sameInstance(promptSupport)));
        assertThat(overview.modelDefinitionSupport(), is(sameInstance(modelSupport)));
        assertThat(overview.rule().requirePromptKey(), is("project-overview"));
        assertThat(overview.rule().requireAiDefinitionKey(), is("default"));
    }

    @Test
    public void rule_isACopy_soNeitherTheCallerNorAReaderChangesIt() {
        // arrange
        final AiFieldGenerationConfig given = rule();
        final ProjectOverview overview = new ProjectOverview(
                new MockAiGenerationProvider(),
                given,
                new AiPromptSupport(Collections.emptyList()),
                CommonTestFixtures.createDefaultAiModelDefinitionSupport());

        // act
        given.setPromptKey("changed-by-the-caller");
        overview.rule().setPromptKey("changed-by-a-reader");

        // assert
        assertThat(overview.rule(), is(not(sameInstance(overview.rule()))));
        assertThat(overview.rule().requirePromptKey(), is("project-overview"));
    }
}
