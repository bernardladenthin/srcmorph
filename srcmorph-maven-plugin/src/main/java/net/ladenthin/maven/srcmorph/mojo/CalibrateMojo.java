// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.maven.srcmorph.mojo;

import net.ladenthin.srcmorph.engine.CalibrateEngine;
import net.ladenthin.srcmorph.engine.CalibrationReport;
import net.ladenthin.srcmorph.engine.SrcMorphException;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;

/**
 * Maven goal {@code srcmorph:calibrate}: a preflight + timing pass that sits between {@code planOnly} (no
 * model loaded) and a real {@code generate} run.
 *
 * <p>For each distinct model a run would load (the {@code aiDefinitionKey}s referenced by
 * file rules) it loads the model once (catching a bad path / OOM / wrong native early), runs a couple of
 * representative generations, reads the model's own measured prefill/decode throughput, and prints a
 * paste-ready YAML {@code calibration:} block. Paste that into the model's entry under
 * {@code aiDefinitions} of the configuration file so the plan's time estimate reflects <em>this</em>
 * machine instead of the built-in reference-CPU coefficients. The numbers are per machine (GPU/CPU,
 * quant, context), so re-run on each host.</p>
 *
 * <p>Thin wrapper: builds the configuration of the file rules from the configuration file and delegates
 * the whole run to {@link CalibrateEngine}; only the final paste-ready banner and
 * {@link CalibrationReport#renderPasteBlock()} output stay in this mojo.</p>
 */
@SuppressWarnings("initialization.fields.uninitialized")
@Mojo(name = "calibrate", threadSafe = true)
public class CalibrateMojo extends AbstractAiIndexMojo {

    /** Creates a new {@link CalibrateMojo}. */
    public CalibrateMojo() {
        // no-op
    }

    @Override
    protected boolean isPhaseSkipped() {
        // calibrate is a manual diagnostic goal, not one of the file/package/project phases; only the
        // global srcmorph.skip disables it.
        return false;
    }

    @Override
    public void execute() throws MojoExecutionException {
        if (shouldSkip()) {
            getLog().info("AI index calibration skipped.");
            return;
        }

        try {
            final CalibrationReport report = new CalibrateEngine(buildConfiguration(RuleSection.FILES)).execute();

            getLog().info("");
            getLog().info("Paste each calibration: block into its model's entry under aiDefinitions"
                    + " (numbers are per machine).");
            getLog().info("These are the model's own measured prefill/decode throughput (from the engine's "
                    + "per-call timings); only the mock/no-timings path falls back to a wall-clock estimate.");
            for (final String line : report.renderPasteBlock().split("\n", -1)) {
                getLog().info(line);
            }
        } catch (SrcMorphException e) {
            throw new MojoExecutionException(messageOf(e), e);
        }
    }
}
