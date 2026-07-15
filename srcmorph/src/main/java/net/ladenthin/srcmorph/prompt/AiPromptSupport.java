// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.prompt;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.ToString;
import net.ladenthin.srcmorph.document.AiGenerationRequest;
import net.ladenthin.srcmorph.support.Java8CompatibilityHelper;
import org.jspecify.annotations.Nullable;

/** Registry of prompt templates that renders prompt strings for AI generation requests. */
@ToString
public final class AiPromptSupport {

    /** Separator between the name line and the body within the user message. */
    private static final String USER_NAME_BODY_SEPARATOR = "\n\n";

    /**
     * Separator used only to concatenate the system instructions and the user message for length
     * budgeting in {@link AiPromptPreparationSupport}; at inference time the provider sends the two
     * parts as a separate system and user message.
     */
    private static final String SYSTEM_USER_SEPARATOR = "\n\n";

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
     * the goal. Callers (e.g. the llamacpp-ai-index-maven-plugin module's Mojos) wrap
     * construction in {@link NullPointerException}
     * &rarr; {@code org.apache.maven.plugin.MojoExecutionException} so the
     * Maven framework reports it as a user configuration error rather
     * than a plugin bug. This library itself has no Maven dependency, so that type is
     * referenced here only in prose.
     *
     * @param promptDefinitions prompt definitions to register; may be
     *                          {@code null} (treated as no definitions);
     *                          individual entries must be well-formed
     * @throws NullPointerException if any entry has a {@code null} {@code key}
     *                              or {@code null} {@code template}
     */
    public AiPromptSupport(final @Nullable List<AiPromptDefinition> promptDefinitions) {
        if (promptDefinitions == null) {
            this.templates = new HashMap<>(compatibilityHelper.hashMapCapacityFor(0));
            return;
        }
        final int count = promptDefinitions.size();
        // Presize so the loop's put() calls never trigger a rehash
        // (fb-contrib PSC_PRESIZE_COLLECTIONS).
        this.templates = new HashMap<>(compatibilityHelper.hashMapCapacityFor(count));
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
     * Builds the combined system + user text for the given key, file, and source text.
     *
     * <p>This concatenation ({@code systemPrompt + separator + userMessage}) is used only for
     * length budgeting / trimming by {@link AiPromptPreparationSupport} and for the
     * {@code maxInputChars} calculation; at inference time the provider sends the two parts
     * separately — see {@link #systemPrompt(String)} and {@link #userMessage(java.nio.file.Path, String)}.</p>
     *
     * @param promptKey  the key identifying the prompt template (system instructions)
     * @param sourceFile the file path whose name heads the user message
     * @param sourceText the source text delivered in the user message
     * @return the combined system + user text
     * @throws IllegalArgumentException if no template is registered for {@code promptKey}
     */
    public String buildPrompt(final String promptKey, final java.nio.file.Path sourceFile, final String sourceText) {
        return systemPrompt(promptKey) + SYSTEM_USER_SEPARATOR + userMessage(sourceFile, sourceText);
    }

    /**
     * Returns the static system-instruction prompt (the registered template, verbatim) for the
     * given key. The template carries no placeholders; the variable file name and content are
     * delivered separately via {@link #userMessage(java.nio.file.Path, String)}.
     *
     * @param promptKey the key identifying the prompt template
     * @return the system instructions registered for {@code promptKey}
     * @throws IllegalArgumentException if no (non-blank) template is registered for {@code promptKey}
     */
    public String systemPrompt(final String promptKey) {
        final String template = templates.get(promptKey);
        if (template == null || compatibilityHelper.isBlank(template)) {
            throw new IllegalArgumentException("Missing prompt template for key: " + promptKey);
        }
        return template;
    }

    /**
     * Builds the user message carrying the variable data: the file (or package/project) name on
     * the first line, a blank line, then the body (file source, child summaries, or package leads,
     * depending on the prompt). Falls back to the full path when the file name is {@code null}
     * (e.g. a filesystem root).
     *
     * @param sourceFile the file path whose name heads the message
     * @param sourceText the body delivered after the name
     * @return the user message text
     */
    public String userMessage(final java.nio.file.Path sourceFile, final String sourceText) {
        final java.nio.file.Path fileName = sourceFile.getFileName();
        final Object nameArg = fileName != null ? fileName : sourceFile;
        return nameArg + USER_NAME_BODY_SEPARATOR + sourceText;
    }
}
