// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.indexer;

import java.util.Objects;
import lombok.ToString;
import net.ladenthin.srcmorph.config.AiFieldGenerationConfig;
import net.ladenthin.srcmorph.config.AiModelDefinitionSupport;
import net.ladenthin.srcmorph.prompt.AiPromptSupport;
import net.ladenthin.srcmorph.provider.AiGenerationProvider;
import net.ladenthin.srcmorph.support.ConvertToRecord;

/**
 * What a {@link ProjectIndexer} needs to write the AI overview of a project: the provider that answers, the
 * overview's rule (its prompt and model), and the prompt and model lookups. They mean something only
 * together, so a {@link ProjectIndexer} takes all of them or none.
 */
@ConvertToRecord
@ToString
public final class ProjectOverview {

    private final AiGenerationProvider generationProvider;
    private final AiFieldGenerationConfig rule;
    private final AiPromptSupport promptSupport;
    private final AiModelDefinitionSupport modelDefinitionSupport;

    /**
     * Creates the overview's parts.
     *
     * @param generationProvider     the provider that writes the overview
     * @param rule                   the overview's rule: its prompt and model
     * @param promptSupport          the prompt lookup
     * @param modelDefinitionSupport the AI model definition lookup
     */
    public ProjectOverview(
            final AiGenerationProvider generationProvider,
            final AiFieldGenerationConfig rule,
            final AiPromptSupport promptSupport,
            final AiModelDefinitionSupport modelDefinitionSupport) {
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
    public AiGenerationProvider generationProvider() {
        return generationProvider;
    }

    /**
     * Returns the overview's rule: its prompt and model.
     *
     * @return a copy of the rule
     */
    public AiFieldGenerationConfig rule() {
        return rule.copy();
    }

    /**
     * Returns the prompt lookup.
     *
     * @return the prompt lookup
     */
    public AiPromptSupport promptSupport() {
        return promptSupport;
    }

    /**
     * Returns the AI model definition lookup.
     *
     * @return the model lookup
     */
    public AiModelDefinitionSupport modelDefinitionSupport() {
        return modelDefinitionSupport;
    }
}
