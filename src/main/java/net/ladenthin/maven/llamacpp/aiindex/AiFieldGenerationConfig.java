// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
// Copyright 2026 Bernard Ladenthin bernard.ladenthin@gmail.com
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.maven.llamacpp.aiindex;

/**
 * Maven plugin configuration POJO that associates a prompt template (by key) with an
 * AI model definition (also by key) for a single field-generation step.
 *
 * <p>Instances are declared inside the {@code <fieldGenerations>} list in the plugin
 * configuration. Each entry causes one AI generation call per indexed file or package:
 * the prompt identified by {@link #promptKey} is prepared and sent to the AI provider
 * configured by the {@link AiModelDefinition} identified by {@link #aiDefinitionKey}.</p>
 *
 * <p>Example POM fragment:</p>
 * <pre>{@code
 * <fieldGeneration>
 *     <promptKey>file-body</promptKey>
 *     <aiDefinitionKey>codestral-32k</aiDefinitionKey>
 * </fieldGeneration>
 * }</pre>
 *
 * <p><strong>Note:</strong> This class must remain a mutable JavaBean with setters because
 * Maven's plugin framework instantiates configuration objects via reflection and injects
 * values through setters.</p>
 *
 * @see AiModelDefinition
 * @see AiModelDefinitionSupport
 * @see AiPromptDefinition
 */
public class AiFieldGenerationConfig {

    private String promptKey;

    /**
     * Key that references an {@link AiModelDefinition} registered in the
     * {@code <aiDefinitions>} list.
     *
     * <p>The referenced definition supplies all AI generation parameters (model path,
     * context size, temperature, retry policy, input trimming limits, etc.) for this
     * field-generation step.</p>
     */
    private String aiDefinitionKey;

    /**
     * Returns the prompt template key.
     *
     * @return the key that identifies the prompt template to use for this field
     */
    public String getPromptKey() {
        return promptKey;
    }

    /**
     * Sets the prompt template key.
     *
     * @param promptKey key that references an {@link AiPromptDefinition}
     */
    public void setPromptKey(final String promptKey) {
        this.promptKey = promptKey;
    }

    /**
     * Returns the AI model definition key.
     *
     * @return the key that references the {@link AiModelDefinition} to use
     */
    public String getAiDefinitionKey() {
        return aiDefinitionKey;
    }

    /**
     * Sets the AI model definition key.
     *
     * @param aiDefinitionKey key that references an {@link AiModelDefinition}
     */
    public void setAiDefinitionKey(final String aiDefinitionKey) {
        this.aiDefinitionKey = aiDefinitionKey;
    }
}
