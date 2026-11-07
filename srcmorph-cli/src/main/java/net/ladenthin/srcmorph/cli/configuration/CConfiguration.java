// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.cli.configuration;

import java.util.List;
import lombok.ToString;
import net.ladenthin.srcmorph.config.RoutingRule;
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
     * The shared core configuration passed to whichever engine {@link #command} selects: the
     * {@code srcMorph} section of the configuration file, the SAME {@link SrcMorphConfiguration} the
     * {@code srcmorph-maven-plugin} module's mojos read from the same file, so a key means the same to
     * both.
     */
    public SrcMorphConfiguration srcMorph = new SrcMorphConfiguration();

    /**
     * {@link CCommand#ListModels} only: keeps the models whose id contains this text, ignoring case
     * (e.g. {@code grok}). Empty, the default, lists every model.
     */
    public String modelFilter = "";

    /**
     * The rule of the package phase ({@link CCommand#AggregatePackages}, and the package step of
     * {@link CCommand#All}), the top-level {@code packageRules} of the configuration file,
     * which the Maven plugin's {@code aggregate-packages} goal reads too: exactly one rule, with the
     * package prompt and its model. The phase fails without it; the file rules of
     * {@code srcMorph.rules} never reach it.
     */
    public @Nullable List<RoutingRule> packageRules;

    /**
     * The rule of the project phase ({@link CCommand#AggregateProject}, and the project step of
     * {@link CCommand#All}), the top-level {@code projectRules} of the configuration file,
     * which the Maven plugin's {@code aggregate-project} goal reads too: at most one rule, with the
     * project-overview prompt and its model.
     * Unset (the default), the project index is written without an overview.
     */
    public @Nullable List<RoutingRule> projectRules;
}
