// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.maven.llamacpp.aiindex;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.ToString;

/** Registry of prompt templates that renders prompt strings for AI generation requests. */
@ToString
public class AiPromptSupport {

    private final Map<String, String> templates;
    private final Java8CompatibilityHelper compatibilityHelper = new Java8CompatibilityHelper();

    /**
     * Creates a new {@link AiPromptSupport} pre-populated with the given definitions.
     *
     * <p>Every entry in {@code promptDefinitions} must have both a non-null
     * {@code key} and a non-null {@code template}; a null in either field
     * throws {@link NullPointerException} naming the list index and dumping
     * the offending entry. This is the contract enforcement boundary that
     * makes misconfigured POM {@code <promptDefinitions>} fail at build
     * configuration time rather than silently dropping the entry and
     * surfacing as a "Missing prompt template for key" failure deeper in
     * the goal. Mojos wrap construction in {@link NullPointerException}
     * &rarr; {@link org.apache.maven.plugin.MojoExecutionException} so the
     * Maven framework reports it as a user configuration error rather
     * than a plugin bug.
     *
     * @param promptDefinitions prompt definitions to register; may be
     *                          {@code null} (treated as no definitions);
     *                          individual entries must be well-formed
     * @throws NullPointerException if any entry has a {@code null} {@code key}
     *                              or {@code null} {@code template}
     */
    public AiPromptSupport(final List<AiPromptDefinition> promptDefinitions) {
        if (promptDefinitions == null) {
            this.templates = new HashMap<>(1);
            return;
        }
        final int count = promptDefinitions.size();
        // Presize the load-factor-corrected capacity so the loop's put() calls
        // never trigger a rehash (fb-contrib PSC_PRESIZE_COLLECTIONS).
        this.templates = new HashMap<>((int) (count / 0.75f) + 1);
        for (int i = 0; i < count; i++) {
            final AiPromptDefinition definition = promptDefinitions.get(i);
            final int index = i;
            Objects.requireNonNull(
                    definition.getKey(),
                    () -> "promptDefinitions[" + index + "].key is required (bad entry: " + definition + ")");
            Objects.requireNonNull(
                    definition.getTemplate(),
                    () -> "promptDefinitions[" + index + "].template is required (bad entry: " + definition + ")");
            templates.put(definition.getKey(), definition.getTemplate());
        }
    }

    /**
     * Builds the prompt for the given request.
     *
     * @param request request whose prompt key, source file, and source text are used
     * @return rendered prompt string
     * @throws IllegalArgumentException if no template is registered for the request's prompt key
     */
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

        final java.nio.file.Path fileName = sourceFile.getFileName();
        final Object fileNameArg = fileName != null ? fileName : sourceFile;
        return compatibilityHelper.formatted(template, fileNameArg, sourceText);
    }
}
