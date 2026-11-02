// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.maven.srcmorph.mojo;

import java.io.IOException;
import lombok.ToString;
import net.ladenthin.srcmorph.config.SrcMorphConfiguration;
import net.ladenthin.srcmorph.engine.GenerateEngine;
import net.ladenthin.srcmorph.engine.SrcMorphException;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * Maven goal {@code srcmorph:generate}: summarizes every routed source file into its {@code .ai.md},
 * with the file rules ({@code srcMorph.fieldGenerations}) of the configuration file.
 *
 * <p>Thin wrapper: builds the configuration ({@link AbstractAiIndexMojo#buildConfiguration(RuleSection)})
 * and delegates the whole run to {@link GenerateEngine}.</p>
 */
// @Parameter fields are populated by the Maven plugin framework via reflection after
// construction. NullAway is configured via ExcludedFieldAnnotations to skip them; Checker
// Framework has no equivalent option for plugin-framework fields, so we suppress class-level.
@SuppressWarnings("initialization.fields.uninitialized")
@Mojo(name = "generate", threadSafe = true)
@ToString(callSuper = true)
public class GenerateMojo extends AbstractAiIndexMojo {

    /** Creates a new {@link GenerateMojo}. */
    public GenerateMojo() {
        // no-op
    }

    /**
     * Phase switch for the <strong>file</strong> phase (the {@code generate} goal): when {@code true},
     * only this phase is skipped. The global {@link #skip} still skips every phase.
     */
    @Parameter(property = "srcmorph.file.skip", defaultValue = "false")
    private boolean skipFile;

    /**
     * When {@code true}, only builds and logs the routing plan (which model summarizes which files with
     * which prompt) and stops: no model is loaded and nothing is generated, whatever the file's
     * {@code planOnly} says.
     */
    @Parameter(property = "srcmorph.planOnly", defaultValue = "false")
    private boolean planOnly;

    @Override
    protected boolean isPhaseSkipped() {
        return skipFile;
    }

    /**
     * Returns the configuration of this goal: the file rules, with {@link #planOnly} applied.
     *
     * @return the configuration
     * @throws MojoExecutionException if the configuration file cannot be read
     */
    SrcMorphConfiguration buildGenerateConfiguration() throws MojoExecutionException {
        final SrcMorphConfiguration config = buildConfiguration(RuleSection.FILES);
        if (planOnly) {
            config.setPlanOnly(true);
        }
        return config;
    }

    @Override
    public void execute() throws MojoExecutionException {
        if (shouldSkip()) {
            getLog().info("AI index generation skipped.");
            return;
        }
        final SrcMorphConfiguration config = buildGenerateConfiguration();
        try {
            new GenerateEngine(config).execute();
        } catch (SrcMorphException e) {
            throw new MojoExecutionException(messageOf(e), e);
        } catch (IOException e) {
            throw new MojoExecutionException(ioMessage("generate the AI index files", config, e), e);
        }
    }
}
