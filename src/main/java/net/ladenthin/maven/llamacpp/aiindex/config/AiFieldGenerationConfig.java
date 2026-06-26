// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.maven.llamacpp.aiindex.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import lombok.ToString;
import org.jspecify.annotations.Nullable;

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
 * @see net.ladenthin.maven.llamacpp.aiindex.prompt.AiPromptDefinition
 */
@SuppressWarnings({"NullAway.Init", "initialization.fields.uninitialized"})
@ToString
public class AiFieldGenerationConfig {

    /** Creates a new {@link AiFieldGenerationConfig}. */
    public AiFieldGenerationConfig() {
        // no-op
    }

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
     * Optional source file extensions (e.g. {@code .java}, {@code .sql}) that select this field
     * generation for a file. When non-empty, this entry applies only to files whose name ends with
     * one of the listed extensions. When {@code null} or empty, this entry is the fallback applied to
     * any file that no extension-specific entry matched.
     *
     * @see AiFieldGenerationSelector
     */
    private @Nullable List<String> fileExtensions;

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
     * @param promptKey key that references an {@link net.ladenthin.maven.llamacpp.aiindex.prompt.AiPromptDefinition}
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

    /**
     * Returns the source file extensions that select this entry, or {@code null} when this entry is
     * the extension-agnostic fallback.
     *
     * @return the selecting file extensions, or {@code null}
     */
    public @Nullable List<String> getFileExtensions() {
        return fileExtensions != null ? Collections.unmodifiableList(fileExtensions) : null;
    }

    /**
     * Sets the source file extensions that select this entry. The list is defensively copied.
     *
     * @param fileExtensions selecting file extensions (e.g. {@code .java}); {@code null} or empty
     *                       makes this entry the fallback
     */
    public void setFileExtensions(final @Nullable Collection<String> fileExtensions) {
        this.fileExtensions = fileExtensions != null ? new ArrayList<>(fileExtensions) : null;
    }
}
