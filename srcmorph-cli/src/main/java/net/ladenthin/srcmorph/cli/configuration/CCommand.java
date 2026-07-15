// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.cli.configuration;

/**
 * Selects which {@code srcmorph} phase (or phases) {@link net.ladenthin.srcmorph.cli.Main} runs for
 * a given {@link CConfiguration}.
 */
public enum CCommand {

    /**
     * Builds and logs the Phase 1 routing plan only ({@code GenerateEngine} with {@code planOnly}
     * forced {@code true} regardless of what the configuration file says); no model is loaded and
     * nothing is written. The safe default (see {@link CConfiguration#command}).
     */
    Plan,

    /**
     * Phase 1: indexes source files and fills in their AI-generated summary fields
     * ({@code net.ladenthin.srcmorph.engine.GenerateEngine}, exactly as configured).
     */
    GenerateFileIndex,

    /**
     * Phase 2: aggregates per-package {@code .ai.md} index files
     * ({@code net.ladenthin.srcmorph.engine.AggregatePackagesEngine}).
     */
    AggregatePackages,

    /**
     * Phase 3: aggregates the single project-level {@code .ai.md} index
     * ({@code net.ladenthin.srcmorph.engine.AggregateProjectEngine}).
     */
    AggregateProject,

    /**
     * Runs all three phases in order: {@link #GenerateFileIndex}, then {@link #AggregatePackages},
     * then {@link #AggregateProject}, stopping at the first phase that fails.
     */
    All,

    /**
     * Calibrates every distinct model referenced by the configured routing rules
     * ({@code net.ladenthin.srcmorph.engine.CalibrateEngine}) and prints a paste-ready
     * {@code <calibration>} XML block per model to standard output.
     */
    Calibrate
}
