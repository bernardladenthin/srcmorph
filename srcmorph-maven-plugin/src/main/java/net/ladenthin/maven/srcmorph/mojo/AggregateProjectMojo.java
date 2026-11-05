// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.maven.srcmorph.mojo;

import java.io.IOException;
import lombok.ToString;
import net.ladenthin.srcmorph.config.SrcMorphConfiguration;
import net.ladenthin.srcmorph.engine.AggregateProjectEngine;
import net.ladenthin.srcmorph.engine.SrcMorphException;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * Maven goal {@code srcmorph:aggregate-project}: aggregates every {@code package.ai.md} into the one
 * {@code project.ai.md}, the top of the three-level index.
 *
 * <p>Deterministic by default: it harvests the one-line lead of each package summary into a table of
 * contents and calls no model. With a project rule in the configuration file
 * ({@code projectFieldGenerations}, at most one) it makes <em>one</em> model call for a short
 * {@code #### Overview} paragraph from the leads (see
 * {@link net.ladenthin.srcmorph.indexer.ProjectIndexer}).</p>
 *
 * <p>Thin wrapper: builds the configuration and delegates the whole run to
 * {@link AggregateProjectEngine}.</p>
 */
// @Parameter fields are populated by the Maven plugin framework via reflection after
// construction. NullAway is configured via ExcludedFieldAnnotations to skip them; Checker
// Framework has no equivalent option for plugin-framework fields, so we suppress class-level.
@SuppressWarnings("initialization.fields.uninitialized")
@Mojo(name = "aggregate-project", threadSafe = true)
@ToString(callSuper = true)
public class AggregateProjectMojo extends AbstractSrcMorphMojo {

    /** Creates a new {@link AggregateProjectMojo}. */
    public AggregateProjectMojo() {
        // no-op
    }

    /**
     * Phase switch for the <strong>project</strong> phase (the {@code aggregate-project} goal): when
     * {@code true}, only this phase is skipped. The global {@link #skip} still skips every phase.
     */
    @Parameter(property = "srcmorph.project.skip", defaultValue = "false")
    private boolean skipProject;

    @Override
    protected boolean isPhaseSkipped() {
        return skipProject;
    }

    @Override
    public void execute() throws MojoExecutionException {
        if (shouldSkip()) {
            getLog().info("AI project index aggregation skipped.");
            return;
        }
        final SrcMorphConfiguration config = buildConfiguration(RuleSection.PROJECT);
        try {
            new AggregateProjectEngine(config).execute();
        } catch (SrcMorphException e) {
            throw new MojoExecutionException(messageOf(e), e);
        } catch (IOException e) {
            throw new MojoExecutionException(ioMessage("aggregate the project index file", config, e), e);
        }
    }
}
