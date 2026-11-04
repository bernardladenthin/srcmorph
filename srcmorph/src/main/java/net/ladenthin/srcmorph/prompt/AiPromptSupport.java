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

    /**
     * Joins a rule's prompt key and the key of a prompt appended to it into one key
     * ({@link #composedKey(String, String)}); a registered key cannot contain it.
     */
    public static final char COMPOSED_KEY_SEPARATOR = '+';

    /** Separator between the two templates of a composed key in its system prompt. */
    private static final String COMPOSED_TEMPLATE_SEPARATOR = "\n\n";

    private final Map<String, String> templates;
    private final Java8CompatibilityHelper compatibilityHelper = new Java8CompatibilityHelper();

    /**
     * Creates a new {@link AiPromptSupport} pre-populated with the given definitions.
     *
     * <p>Every entry in {@code promptDefinitions} must have both a non-null {@code key} and a non-null
     * {@code template}, and no key may contain {@link #COMPOSED_KEY_SEPARATOR}. This is the contract
     * enforcement boundary: a misconfigured {@code promptDefinitions} entry of the configuration file fails
     * here, naming the list index and the entry, instead of surfacing as a "Missing prompt template for key"
     * failure deeper in the run. The callers ({@code EngineSupport}) wrap it into a {@code SrcMorphException},
     * so the CLI and the plugin report it as a configuration error.
     *
     * @param promptDefinitions prompt definitions to register; may be {@code null} (treated as no
     *                          definitions); individual entries must be well-formed
     * @throws NullPointerException     if any entry has a {@code null} {@code key} or {@code null}
     *                                  {@code template}
     * @throws IllegalArgumentException if a key contains {@link #COMPOSED_KEY_SEPARATOR}
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
            if (definition.getKey().indexOf(COMPOSED_KEY_SEPARATOR) >= 0) {
                throw new IllegalArgumentException("promptDefinitions[" + index + "].key '" + definition.getKey()
                        + "' contains '" + COMPOSED_KEY_SEPARATOR + "', which joins the keys of a composed prompt");
            }
            final String template = definition.getTemplate();
            if (template == null) {
                // A template file was read by AiPromptTemplateLoader before; here every template is inline.
                throw new NullPointerException(
                        "promptDefinitions[" + index + "].template is required (bad entry: " + definition + ")");
            }
            templates.put(definition.getKey(), template);
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
     * Returns the key of a prompt composed of a rule's prompt and a prompt appended to it, e.g. the
     * map or reduce prompt of a map-reduce: {@link #systemPrompt(String)} renders it as the rule's
     * template, a blank line, then the appended one.
     *
     * @param promptKey   the rule's prompt key
     * @param appendedKey the key of the prompt appended to it, or {@code null} / blank for none
     * @return {@code promptKey+appendedKey}, or {@code promptKey} when nothing is appended
     */
    public static String composedKey(final String promptKey, final @Nullable String appendedKey) {
        if (appendedKey == null || appendedKey.trim().isEmpty()) {
            return promptKey;
        }
        return promptKey + COMPOSED_KEY_SEPARATOR + appendedKey;
    }

    /**
     * Returns the static system-instruction prompt (the registered template, verbatim) for the
     * given key; for a {@link #composedKey(String, String) composed key} the two templates, joined by
     * a blank line. The template carries no placeholders; the variable file name and content are
     * delivered separately via {@link #userMessage(java.nio.file.Path, String)}.
     *
     * @param promptKey the key identifying the prompt template, or a composed key
     * @return the system instructions registered for {@code promptKey}
     * @throws IllegalArgumentException if no (non-blank) template is registered for {@code promptKey}, or
     *                                  for one of the two keys of a composed key
     */
    public String systemPrompt(final String promptKey) {
        final int separator = promptKey.indexOf(COMPOSED_KEY_SEPARATOR);
        if (separator >= 0) {
            return template(promptKey.substring(0, separator))
                    + COMPOSED_TEMPLATE_SEPARATOR
                    + template(promptKey.substring(separator + 1));
        }
        return template(promptKey);
    }

    /**
     * Returns the template registered for one key.
     *
     * @param promptKey the key
     * @return the template
     * @throws IllegalArgumentException if no (non-blank) template is registered for {@code promptKey}
     */
    private String template(final String promptKey) {
        final String template = templates.get(promptKey);
        if (template == null || compatibilityHelper.isBlank(template)) {
            throw new IllegalArgumentException("Missing prompt template for key: " + promptKey);
        }
        return template;
    }

    /**
     * Builds the user message carrying the variable data: the file (or package/project) on the first
     * line, a blank line, then the body (file source, child summaries, or package leads, depending on
     * the prompt). A relative path heads it whole, with {@code /} separators: the file phase passes a
     * source file's path below the base directory, so two {@code web.xml} of different modules read
     * apart. An absolute path heads it by its name (a package's or the project's index file), or whole
     * when it has none (a filesystem root).
     *
     * @param sourceFile the path that heads the message
     * @param sourceText the body delivered after it
     * @return the user message text
     */
    public String userMessage(final java.nio.file.Path sourceFile, final String sourceText) {
        return heading(sourceFile) + USER_NAME_BODY_SEPARATOR + sourceText;
    }

    /**
     * Returns how a path heads the user message.
     *
     * @param sourceFile the path
     * @return a relative path whole with {@code /} separators, else its name, else the path itself
     */
    private static String heading(final java.nio.file.Path sourceFile) {
        if (!sourceFile.isAbsolute()) {
            return sourceFile.toString().replace('\\', '/');
        }
        final java.nio.file.Path fileName = sourceFile.getFileName();
        return fileName != null ? fileName.toString() : sourceFile.toString();
    }
}
