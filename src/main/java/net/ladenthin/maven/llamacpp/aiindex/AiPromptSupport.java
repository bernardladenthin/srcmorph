// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
// Copyright 2026 Bernard Ladenthin bernard.ladenthin@gmail.com
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.maven.llamacpp.aiindex;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AiPromptSupport {

    private final Map<String, String> templates = new HashMap<>();
    private final Java8CompatibilityHelper compatibilityHelper = new Java8CompatibilityHelper();

    public AiPromptSupport(final List<AiPromptDefinition> promptDefinitions) {
        if (promptDefinitions != null) {
            for (AiPromptDefinition definition : promptDefinitions) {
                if (definition.getKey() != null && definition.getTemplate() != null) {
                    templates.put(definition.getKey(), definition.getTemplate());
                }
            }
        }
    }

    public String buildPrompt(final AiGenerationRequest request) {
        return buildPrompt(request.promptKey(), request.sourceFile(), request.sourceText());
    }

    /**
     * Builds the prompt string for the given key, file, and source text without
     * requiring a full {@link AiGenerationRequest}. Useful when only template
     * length measurement is needed and no {@link AiMdHeader} is available.
     *
     * @param promptKey  the key identifying the prompt template
     * @param sourceFile the file path substituted as the filename argument
     * @param sourceText the source text substituted into the template
     * @return the rendered prompt string
     * @throws IllegalArgumentException if no template is registered for {@code promptKey}
     */
    public String buildPrompt(final String promptKey, final java.nio.file.Path sourceFile, final String sourceText) {
        final String template = templates.get(promptKey);
        if (template == null || compatibilityHelper.isBlank(template)) {
            throw new IllegalArgumentException("Missing prompt template for key: " + promptKey);
        }

        return compatibilityHelper.formatted(template,
                sourceFile.getFileName(),
                sourceText
        );
    }
}