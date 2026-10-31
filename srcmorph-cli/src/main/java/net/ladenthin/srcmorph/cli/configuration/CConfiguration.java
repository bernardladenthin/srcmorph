// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.cli.configuration;

import java.util.List;
import lombok.ToString;
import net.ladenthin.srcmorph.config.AiFieldGenerationConfig;
import net.ladenthin.srcmorph.config.SrcMorphConfiguration;
import org.jspecify.annotations.Nullable;

/**
 * Root configuration object loaded from the CLI's JSON/YAML config file.
 *
 * <p>Public mutable-field JavaBean (BitcoinAddressFinder {@code cli.configuration.CConfiguration}
 * convention), bindable via Jackson's {@code ObjectMapper}/{@code YAMLMapper} without any getters or
 * setters. Carved out of the {@code noPublicMutableFields} ArchUnit rule via this package's explicit
 * exception (see {@code CliArchitectureTest}).</p>
 */
@ToString
public class CConfiguration {

    /** Creates a new {@link CConfiguration} with every default applied. */
    public CConfiguration() {
        // no-op
    }

    /**
     * The command selecting which {@code srcmorph} phase(s) {@link net.ladenthin.srcmorph.cli.Main}
     * runs. Defaults to {@link CCommand#Plan} so that a missing or misconfigured command never
     * accidentally triggers a real (possibly expensive, model-loading) run.
     */
    public CCommand command = CCommand.Plan;

    /**
     * The shared core configuration passed to whichever engine {@link #command} selects — the SAME
     * {@link SrcMorphConfiguration} type the {@code llamacpp-ai-index-maven-plugin} module's mojos
     * build from their own {@code @Parameter} fields, so a JSON/YAML key here reads identically to
     * the matching Maven {@code <configuration>} XML element.
     */
    public SrcMorphConfiguration srcMorph = new SrcMorphConfiguration();

    /**
     * {@link CCommand#ListModels} only: keeps the models whose id contains this text, ignoring case
     * (e.g. {@code grok}). Empty, the default, lists every model.
     */
    public String modelFilter = "";

    /**
     * The routing rules of the package phase ({@link CCommand#AggregatePackages}, and the package
     * step of {@link CCommand#All}), as the Maven plugin's {@code aggregate-packages} execution has
     * its own {@code <fieldGenerations>}: typically one rule with the package prompt. Unset (the
     * default), the phase uses {@code srcMorph.fieldGenerations}, whose first rule then picks the
     * prompt and the model.
     */
    public @Nullable List<AiFieldGenerationConfig> packageFieldGenerations;

    /**
     * The routing rules of the project phase ({@link CCommand#AggregateProject}, and the project step
     * of {@link CCommand#All}), as the Maven plugin's {@code aggregate-project} execution has its own
     * {@code <fieldGenerations>}: one rule with the project-overview prompt. Unset (the default), the
     * phase uses {@code srcMorph.fieldGenerations}, whose first rule then picks the prompt and the
     * model.
     */
    public @Nullable List<AiFieldGenerationConfig> projectFieldGenerations;
}
