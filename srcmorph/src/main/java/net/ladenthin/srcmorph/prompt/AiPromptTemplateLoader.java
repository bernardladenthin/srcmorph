// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.prompt;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.ToString;
import net.ladenthin.srcmorph.support.AiPathSupport;
import net.ladenthin.srcmorph.support.AiProfileFileSupport;
import net.ladenthin.srcmorph.support.Java8CompatibilityHelper;
import org.jspecify.annotations.Nullable;

/**
 * Reads the prompts of a configuration from files, so the rest of the pipeline sees inline templates
 * only: the {@code templateFile} a definition names, and, for a prompt key a rule uses that no
 * definition has, the file {@code <profileDirectory>/<key>.md} ({@link AiProfileFileSupport}).
 *
 * <p>A relative {@code templateFile} is resolved against the profile directory
 * ({@link AiPathSupport#resolveAgainst}, the rule model paths follow too); an absolute one is always
 * used as it is. Every file is read as UTF-8, a leading byte order mark dropped, and otherwise used as
 * it is: the template is the system message verbatim. A definition with both {@code template} and
 * {@code templateFile} is refused, as is a file that holds nothing but whitespace. With a profile
 * directory set, a rule's prompt key that is neither defined nor a file there is refused right away,
 * naming the file it looked for. The configured definitions are never modified: a definition with a
 * file is replaced by a copy carrying its key and the read template.</p>
 */
@ToString
public final class AiPromptTemplateLoader {

    private final @Nullable File profileDirectory;

    /**
     * Creates a loader for one configuration.
     *
     * @param profileDirectory directory relative template files and the prompts named by key are
     *                         resolved against, or {@code null} for the working directory and no
     *                         lookup by key
     */
    public AiPromptTemplateLoader(final @Nullable File profileDirectory) {
        this.profileDirectory = profileDirectory;
    }

    /**
     * Returns the definitions with every template file read into its template.
     *
     * @param promptDefinitions the configured definitions, or {@code null} for none
     * @return the definitions in the same order, those with a template file replaced by a copy with
     *         the file's text as template; {@code null} when {@code promptDefinitions} is {@code null}
     * @throws IOException              if a template file cannot be read
     * @throws IllegalArgumentException if a definition sets both {@code template} and
     *                                  {@code templateFile}, or its file holds nothing but whitespace
     */
    public @Nullable List<AiPromptDefinition> load(final @Nullable List<AiPromptDefinition> promptDefinitions)
            throws IOException {
        if (promptDefinitions == null) {
            return null;
        }
        return load(promptDefinitions, Collections.<@Nullable String>emptyList());
    }

    /**
     * Returns the definitions with every template file read, plus a definition read from the profile
     * directory for each used prompt key no definition has.
     *
     * @param promptDefinitions the configured definitions, or {@code null} for none
     * @param usedPromptKeys    the prompt keys the configuration's rules use; {@code null} entries
     *                          (a skip rule's) are ignored
     * @return the configured definitions (template files read), then one per used key read from
     *         {@code <profileDirectory>/<key>.md}, in the order the keys come
     * @throws IOException              if a template file cannot be read
     * @throws IllegalArgumentException if a definition sets both {@code template} and
     *                                  {@code templateFile}, a file holds nothing but whitespace, or
     *                                  with a profile directory a used key is neither defined nor a file
     */
    public List<AiPromptDefinition> load(
            final @Nullable List<AiPromptDefinition> promptDefinitions,
            final Collection<@Nullable String> usedPromptKeys)
            throws IOException {
        final List<AiPromptDefinition> configured =
                promptDefinitions != null ? promptDefinitions : Collections.<AiPromptDefinition>emptyList();
        final List<AiPromptDefinition> loaded = new ArrayList<>(configured.size() + usedPromptKeys.size());
        final Set<String> defined = new HashSet<>(
                new Java8CompatibilityHelper().hashMapCapacityFor(configured.size() + usedPromptKeys.size()));
        for (int i = 0; i < configured.size(); i++) {
            final AiPromptDefinition definition = configured.get(i);
            defined.add(definition.getKey());
            final String templateFile = definition.getTemplateFile();
            if (templateFile == null) {
                loaded.add(definition);
                continue;
            }
            final String label = "promptDefinitions[" + i + "] (key " + definition.getKey() + ")";
            if (definition.getTemplate() != null) {
                throw new IllegalArgumentException(label + " sets both template and templateFile; set one of them");
            }
            final String path = AiPathSupport.resolveAgainst(profileDirectory, templateFile);
            loaded.add(inline(
                    definition.getKey(), AiProfileFileSupport.read(label + ": templateFile", String.valueOf(path))));
        }
        final File directory = profileDirectory;
        if (directory == null) {
            return loaded;
        }
        for (final String key : usedPromptKeys) {
            if (key == null || !defined.add(key)) {
                continue;
            }
            final File file =
                    AiProfileFileSupport.conventionalFile(directory, key, AiProfileFileSupport.PROMPT_EXTENSION);
            if (!file.isFile()) {
                throw new IllegalArgumentException("prompt key " + key
                        + " is used by a rule, but no promptDefinition has it and the profile has no " + file);
            }
            loaded.add(inline(key, AiProfileFileSupport.read("prompt key " + key + ": file", file.getPath())));
        }
        return loaded;
    }

    private static AiPromptDefinition inline(final String key, final String template) {
        final AiPromptDefinition copy = new AiPromptDefinition();
        copy.setKey(key);
        copy.setTemplate(template);
        return copy;
    }
}
