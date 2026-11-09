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
import net.ladenthin.srcmorph.config.ModelRegistry;
import net.ladenthin.srcmorph.config.RoutingRule;
import net.ladenthin.srcmorph.prompt.PromptCatalog;
import net.ladenthin.srcmorph.provider.GenerationProvider;
import net.ladenthin.srcmorph.provider.MockGenerationProvider;
import org.junit.jupiter.api.Test;

public class ProjectOverviewTest {

    private static RoutingRule rule() {
        final RoutingRule rule = new RoutingRule();
        rule.setPromptKey("project-overview");
        rule.setAiDefinitionKey("default");
        return rule;
    }

    // <editor-fold defaultstate="collapsed" desc="accessors">
    @Test
    public void accessors_builtOverview_returnWhatTheOverviewWasBuiltWith() {
        // arrange
        final GenerationProvider provider = new MockGenerationProvider();
        final PromptCatalog promptSupport = new PromptCatalog(Collections.emptyList());
        final ModelRegistry modelSupport = CommonTestFixtures.createDefaultAiModelDefinitionSupport();

        // act
        final ProjectOverview overview = new ProjectOverview(provider, rule(), promptSupport, modelSupport);

        // assert
        assertThat(overview.generationProvider(), is(sameInstance(provider)));
        assertThat(overview.promptSupport(), is(sameInstance(promptSupport)));
        assertThat(overview.modelDefinitionSupport(), is(sameInstance(modelSupport)));
        assertThat(overview.rule().requirePromptKey(), is("project-overview"));
        assertThat(overview.rule().requireAiDefinitionKey(), is("default"));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="rule">
    @Test
    public void rule_isACopy_soNeitherTheCallerNorAReaderChangesIt() {
        // arrange
        final RoutingRule given = rule();
        final ProjectOverview overview = new ProjectOverview(
                new MockGenerationProvider(),
                given,
                new PromptCatalog(Collections.emptyList()),
                CommonTestFixtures.createDefaultAiModelDefinitionSupport());

        // act
        given.setPromptKey("changed-by-the-caller");
        overview.rule().setPromptKey("changed-by-a-reader");

        // assert
        assertThat(overview.rule(), is(not(sameInstance(overview.rule()))));
        assertThat(overview.rule().requirePromptKey(), is("project-overview"));
    }
    // </editor-fold>
}
