// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.maven.srcmorph.mojo;

import net.ladenthin.srcmorph.engine.CalibrateEngine;
import net.ladenthin.srcmorph.engine.CalibrationReport;
import net.ladenthin.srcmorph.engine.SrcMorphException;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * Maven goal {@code srcmorph:calibrate}: a preflight + timing pass that sits between {@code planOnly} (no
 * model loaded) and a real {@code generate} run.
 *
 * <p>For each distinct model a run would load (the {@code aiDefinitionKey}s referenced by
 * {@code <fieldGenerations>}) it loads the model once (catching a bad path / OOM / wrong native early),
 * runs a couple of representative generations, reads the model's own measured prefill/decode throughput,
 * and prints a paste-ready {@code <calibration>} block. Paste that onto the matching {@code <aiDefinition>}
 * so the plan's time estimate reflects <em>this</em> machine instead of the built-in reference-CPU
 * coefficients. The numbers are per machine (GPU/CPU, quant, context), so re-run on each host.</p>
 *
 * <p>Thin wrapper: builds a {@link net.ladenthin.srcmorph.config.SrcMorphConfiguration} from the shared
 * {@code @Parameter} fields and delegates the whole run to {@link CalibrateEngine}; only the final
 * paste-ready banner and {@link CalibrationReport#renderXml()} output stay in this mojo.</p>
 */
@SuppressWarnings("initialization.fields.uninitialized")
@Mojo(name = "calibrate", threadSafe = true)
public class CalibrateMojo extends AbstractAiIndexMojo {

    /** Creates a new {@link CalibrateMojo}. */
    public CalibrateMojo() {
        // no-op
    }

    /** llama.cpp context window size (unused by calibrate, which uses each model's own config). */
    @Parameter(property = "srcmorph.llama.contextSize", defaultValue = "2048")
    private int llamaContextSize;

    /** CPU threads (unused by calibrate, which uses each model's own config). */
    @Parameter(property = "srcmorph.llama.threads", defaultValue = "2")
    private int llamaThreads;

    @Override
    protected int getLlamaContextSize() {
        return llamaContextSize;
    }

    @Override
    protected int getLlamaThreads() {
        return llamaThreads;
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
            final CalibrationReport report = new CalibrateEngine(buildConfiguration()).execute();

            getLog().info("");
            getLog().info("Paste each <calibration> onto its matching <aiDefinition> (numbers are per machine).");
            getLog().info("These are the model's own measured prefill/decode throughput (from the engine's "
                    + "per-call timings); only the mock/no-timings path falls back to a wall-clock estimate.");
            for (final String line : report.renderXml().split("\n", -1)) {
                getLog().info(line);
            }
        } catch (SrcMorphException e) {
            throw new MojoExecutionException(messageOf(e), e);
        }
    }
}
