// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.indexer;

import java.util.Objects;
import lombok.ToString;
import net.ladenthin.srcmorph.config.ModelRegistry;
import net.ladenthin.srcmorph.config.RoutingRule;
import net.ladenthin.srcmorph.prompt.PromptCatalog;
import net.ladenthin.srcmorph.provider.GenerationProvider;
import net.ladenthin.srcmorph.support.ConvertToRecord;

/**
 * What a {@link ProjectIndexer} needs to write the AI overview of a project: the provider that answers, the
 * overview's rule (its prompt and model), and the prompt and model lookups. They mean something only
 * together, so a {@link ProjectIndexer} takes all of them or none.
 */
@ConvertToRecord
@ToString
public final class ProjectOverview {

    private final GenerationProvider generationProvider;
    private final RoutingRule rule;
    private final PromptCatalog promptSupport;
    private final ModelRegistry modelDefinitionSupport;

    /**
     * Creates the overview's parts.
     *
     * @param generationProvider     the provider that writes the overview
     * @param rule                   the overview's rule: its prompt and model
     * @param promptSupport          the prompt lookup
     * @param modelDefinitionSupport the AI model definition lookup
     */
    public ProjectOverview(
            final GenerationProvider generationProvider,
            final RoutingRule rule,
            final PromptCatalog promptSupport,
            final ModelRegistry modelDefinitionSupport) {
        this.generationProvider = Objects.requireNonNull(generationProvider, "generationProvider");
        // a copy of every field: the rule is a mutable JavaBean owned by the caller
        this.rule = Objects.requireNonNull(rule, "rule").copy();
        this.promptSupport = Objects.requireNonNull(promptSupport, "promptSupport");
        this.modelDefinitionSupport = Objects.requireNonNull(modelDefinitionSupport, "modelDefinitionSupport");
    }

    /**
     * Returns the provider that writes the overview.
     *
     * @return the provider
     */
    public GenerationProvider generationProvider() {
        return generationProvider;
    }

    /**
     * Returns the overview's rule: its prompt and model.
     *
     * @return a copy of the rule
     */
    public RoutingRule rule() {
        return rule.copy();
    }

    /**
     * Returns the prompt lookup.
     *
     * @return the prompt lookup
     */
    public PromptCatalog promptSupport() {
        return promptSupport;
    }

    /**
     * Returns the AI model definition lookup.
     *
     * @return the model lookup
     */
    public ModelRegistry modelDefinitionSupport() {
        return modelDefinitionSupport;
    }
}
