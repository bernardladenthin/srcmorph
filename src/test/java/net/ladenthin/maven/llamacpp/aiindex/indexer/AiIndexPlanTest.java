// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.maven.llamacpp.aiindex.indexer;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.nio.file.Path;
import java.nio.file.Paths;
import net.ladenthin.maven.llamacpp.aiindex.config.AiFieldGenerationConfig;
import org.junit.jupiter.api.Test;

public class AiIndexPlanTest {

    private static AiFieldGenerationConfig rule(final String promptKey) {
        final AiFieldGenerationConfig config = new AiFieldGenerationConfig();
        config.setPromptKey(promptKey);
        return config;
    }

    private static AiFieldGenerationConfig rule(final String id, final String promptKey) {
        final AiFieldGenerationConfig config = new AiFieldGenerationConfig();
        config.setId(id);
        config.setPromptKey(promptKey);
        return config;
    }

    @Test
    public void routedCountAndPerModelGrouping() {
        final AiIndexPlan plan = new AiIndexPlan();
        plan.addRoute("modelA", Paths.get("a/Foo.java"), rule("p1"), 60);
        plan.addRoute("modelA", Paths.get("a/Bar.java"), rule("p1"), 120);
        plan.addRoute("modelB", Paths.get("b/Baz.java"), rule("p2"), 30);

        assertThat(plan.routedCount(), is(equalTo(3)));
        assertThat(plan.routesByModel().get("modelA").size(), is(equalTo(2)));
        assertThat(plan.routesByModel().get("modelB").size(), is(equalTo(1)));
    }

    @Test
    public void totalEstimatedSecondsSumsAllEntries() {
        final AiIndexPlan plan = new AiIndexPlan();
        plan.addRoute("modelA", Paths.get("a/Foo.java"), rule("p1"), 60);
        plan.addRoute("modelA", Paths.get("a/Bar.java"), rule("p1"), 120);
        plan.addRoute("modelB", Paths.get("b/Baz.java"), rule("p2"), 30);

        assertThat(plan.totalEstimatedSeconds(), is(equalTo(210L)));
    }

    @Test
    public void renderMarkdown_containsModelSectionsFilesAndTotals() {
        final Path base = Paths.get("");
        final AiIndexPlan plan = new AiIndexPlan();
        plan.addRoute("modelA", Paths.get("Foo.java"), rule("java-small", "file-body-java"), 60);
        plan.addSkipped(Paths.get("Generated.java"));
        plan.addUnmatched(Paths.get("weird.txt"));

        final String md = plan.renderMarkdown(base);

        assertThat(md, containsString("## AI index plan"));
        assertThat(md, containsString("**Total:**"));
        assertThat(md, containsString("### Model `modelA`"));
        assertThat(md, containsString("| File | Rule | Prompt | Est. |"));
        assertThat(md, containsString("Foo.java"));
        assertThat(md, containsString("java-small")); // the rule id shows in its own column
        assertThat(md, containsString("file-body-java"));
        assertThat(md, containsString("Skipped (1)"));
        assertThat(md, containsString("Generated.java"));
        assertThat(md, containsString("Unmatched"));
        assertThat(md, containsString("weird.txt"));
    }
}
