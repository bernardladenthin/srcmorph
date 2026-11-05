// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.maven.srcmorph.mojo;

import java.io.IOException;
import lombok.ToString;
import net.ladenthin.srcmorph.config.SrcMorphConfiguration;
import net.ladenthin.srcmorph.engine.AggregatePackagesEngine;
import net.ladenthin.srcmorph.engine.SrcMorphException;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * Maven goal {@code srcmorph:aggregate-packages}: summarizes every package of the {@code .ai.md} tree
 * into its {@code package.ai.md}, with the one package rule ({@code packageRules}) of the
 * configuration file.
 *
 * <p>Thin wrapper: builds the configuration and delegates the whole run to
 * {@link AggregatePackagesEngine}.</p>
 */
// @Parameter fields are populated by the Maven plugin framework via reflection after
// construction. NullAway is configured via ExcludedFieldAnnotations to skip them; Checker
// Framework has no equivalent option for plugin-framework fields, so we suppress class-level.
@SuppressWarnings("initialization.fields.uninitialized")
@Mojo(name = "aggregate-packages", threadSafe = true)
@ToString(callSuper = true)
public class AggregatePackagesMojo extends AbstractSrcMorphMojo {

    /** Creates a new {@link AggregatePackagesMojo}. */
    public AggregatePackagesMojo() {
        // no-op
    }

    /**
     * Phase switch for the <strong>package</strong> phase (the {@code aggregate-packages} goal): when
     * {@code true}, only this phase is skipped. The global {@link #skip} still skips every phase.
     */
    @Parameter(property = "srcmorph.package.skip", defaultValue = "false")
    private boolean skipPackage;

    @Override
    protected boolean isPhaseSkipped() {
        return skipPackage;
    }

    @Override
    public void execute() throws MojoExecutionException {
        if (shouldSkip()) {
            getLog().info("AI package aggregation skipped.");
            return;
        }
        final SrcMorphConfiguration config = buildConfiguration(RuleSection.PACKAGE);
        try {
            new AggregatePackagesEngine(config).execute();
        } catch (SrcMorphException e) {
            throw new MojoExecutionException(messageOf(e), e);
        } catch (IOException e) {
            throw new MojoExecutionException(ioMessage("aggregate the package index files", config, e), e);
        }
    }
}
