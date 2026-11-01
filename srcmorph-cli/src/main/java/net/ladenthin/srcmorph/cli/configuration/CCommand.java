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
    Plan("the routing plan only; no model loaded, nothing written (the default)"),

    /**
     * Checks the configuration of all three phases as a run would, without loading a model or writing
     * anything: the plan, the package rule when there is one, the project rule, the profile files of
     * every rule, and every routed model (a local model's file
     * and GGUF header, a remote model's {@code baseUrl} and {@code apiKeyEnv}); fails naming the first
     * problem ({@code GenerateEngine#check()}, {@code AggregatePackagesEngine#check()},
     * {@code AggregateProjectEngine#check()}).
     */
    Check("the plan, the profile files and every model of all three phases; no model loaded"),

    /**
     * Phase 1: indexes source files and fills in their AI-generated summary fields
     * ({@code net.ladenthin.srcmorph.engine.GenerateEngine}, exactly as configured).
     */
    GenerateFileIndex("one .ai.md per source file (srcMorph.fieldGenerations)"),

    /**
     * Phase 2: aggregates per-package {@code .ai.md} index files
     * ({@code net.ladenthin.srcmorph.engine.AggregatePackagesEngine}).
     */
    AggregatePackages("one package.ai.md per directory (packageFieldGenerations)"),

    /**
     * Phase 3: aggregates the single project-level {@code .ai.md} index
     * ({@code net.ladenthin.srcmorph.engine.AggregateProjectEngine}).
     */
    AggregateProject("project.ai.md, with an overview by projectFieldGenerations"),

    /**
     * Runs all three phases in order: {@link #GenerateFileIndex}, then {@link #AggregatePackages},
     * then {@link #AggregateProject}, stopping at the first phase that fails.
     */
    All("GenerateFileIndex, then AggregatePackages, then AggregateProject"),

    /**
     * Calibrates every distinct model referenced by the configured routing rules
     * ({@code net.ladenthin.srcmorph.engine.CalibrateEngine}) and prints a paste-ready
     * {@code <calibration>} XML block per model to standard output.
     */
    Calibrate("measures every routed model and prints a <calibration> block per model"),

    /**
     * Lists the models of every OpenAI-compatible server the {@code aiDefinitions} name
     * ({@code net.ladenthin.srcmorph.engine.ListModelsEngine}, {@code GET <baseUrl>/models}) and
     * prints a table and a paste-ready {@code aiDefinitions} YAML block to standard output; narrowed
     * by {@link CConfiguration#modelFilter}. Sends no prompt and writes nothing.
     */
    ListModels("lists the models of every OpenAI-compatible server (modelFilter)");

    /** The one-line summary {@code --help} prints. */
    private final String summary;

    CCommand(final String summary) {
        this.summary = summary;
    }

    /**
     * Returns the one-line summary {@code --help} prints for this command.
     *
     * @return the summary
     */
    public String summary() {
        return summary;
    }
}
