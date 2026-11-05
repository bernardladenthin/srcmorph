// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.maven.srcmorph.mojo;

import java.io.IOException;
import net.ladenthin.srcmorph.config.SrcMorphConfiguration;
import net.ladenthin.srcmorph.engine.AggregatePackagesEngine;
import net.ladenthin.srcmorph.engine.AggregateProjectEngine;
import net.ladenthin.srcmorph.engine.GenerateEngine;
import net.ladenthin.srcmorph.engine.SrcMorphException;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;

/**
 * Maven goal {@code srcmorph:check}: checks the configuration file as the three phases would run it,
 * without loading a model or writing anything: the plan of the file rules (every file routed, none
 * over a window with {@code onOversize: fail}, no two into one summary), the package and project rules
 * the file has (the package rule only when it has one), the profile files of every rule, and every model a rule names (a local model's file and
 * GGUF header, a remote model's {@code baseUrl} and {@code apiKeyEnv}). Fails naming the first problem.
 *
 * <p>A diagnostic goal like {@code calibrate}: only the global {@code srcmorph.skip} skips it.</p>
 */
// @Parameter fields are populated by the Maven plugin framework via reflection after
// construction. NullAway is configured via ExcludedFieldAnnotations to skip them; Checker
// Framework has no equivalent option for plugin-framework fields, so we suppress class-level.
@SuppressWarnings("initialization.fields.uninitialized")
@Mojo(name = "check", threadSafe = true)
public class CheckMojo extends AbstractSrcMorphMojo {

    /** Creates a new {@link CheckMojo}. */
    public CheckMojo() {
        // no-op
    }

    @Override
    protected boolean isPhaseSkipped() {
        return false;
    }

    @Override
    public void execute() throws MojoExecutionException {
        if (shouldSkip()) {
            getLog().info("srcmorph check skipped.");
            return;
        }
        try {
            new GenerateEngine(buildConfiguration(RuleSection.FILES)).check();
            // the package phase only when the file configures it: a project may not aggregate packages
            final SrcMorphConfiguration packages = buildConfiguration(RuleSection.PACKAGE);
            if (packages.getRules() != null) {
                new AggregatePackagesEngine(packages).check();
            }
            new AggregateProjectEngine(buildConfiguration(RuleSection.PROJECT)).check();
        } catch (SrcMorphException e) {
            throw new MojoExecutionException(messageOf(e), e);
        } catch (IOException e) {
            throw new MojoExecutionException("Failed to read the source tree: " + e.getMessage(), e);
        }
    }
}
