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
import net.ladenthin.srcmorph.config.PromptDefinition;
import net.ladenthin.srcmorph.support.Java8CompatibilityHelper;
import net.ladenthin.srcmorph.support.ProfileFileSupport;
import net.ladenthin.srcmorph.support.SourcePaths;
import org.jspecify.annotations.Nullable;

/**
 * Reads the prompts of a configuration from files, so the rest of the pipeline sees inline templates
 * only: the {@code templateFile} a definition names, and, for a prompt key a rule uses that no
 * definition has, the file {@code <profileDirectory>/<key>.md} ({@link ProfileFileSupport}).
 *
 * <p>A relative {@code templateFile} is resolved against the profile directory
 * ({@link SourcePaths#resolveAgainst}, the rule model paths follow too); an absolute one is always
 * used as it is. Every file is read as UTF-8, a leading byte order mark dropped, and otherwise used as
 * it is: the template is the system message verbatim. A definition with both {@code template} and
 * {@code templateFile} is refused, as is a file that holds nothing but whitespace. With a profile
 * directory set, a rule's prompt key that is neither defined nor a file there is refused right away,
 * naming the file it looked for. The configured definitions are never modified: a definition with a
 * file is replaced by a copy carrying its key and the read template.</p>
 */
@ToString
public final class PromptTemplateLoader {

    private final @Nullable File profileDirectory;

    /**
     * Creates a loader for one configuration.
     *
     * @param profileDirectory directory relative template files and the prompts named by key are
     *                         resolved against, or {@code null} for the working directory and no
     *                         lookup by key
     */
    public PromptTemplateLoader(final @Nullable File profileDirectory) {
        this.profileDirectory = profileDirectory;
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
    public List<PromptDefinition> load(
            final @Nullable List<PromptDefinition> promptDefinitions, final Collection<@Nullable String> usedPromptKeys)
            throws IOException {
        final List<PromptDefinition> configured =
                promptDefinitions != null ? promptDefinitions : Collections.<PromptDefinition>emptyList();
        final List<PromptDefinition> loaded = new ArrayList<>(configured.size() + usedPromptKeys.size());
        final Set<String> defined = new HashSet<>(
                new Java8CompatibilityHelper().hashMapCapacityFor(configured.size() + usedPromptKeys.size()));
        for (int i = 0; i < configured.size(); i++) {
            final PromptDefinition definition = configured.get(i);
            final String key = definition.getKey();
            if (key == null) {
                throw new IllegalArgumentException(
                        "promptDefinitions[" + i + "].key is required (bad entry: " + definition + ")");
            }
            defined.add(key);
            final String templateFile = definition.getTemplateFile();
            if (templateFile == null) {
                loaded.add(definition);
                continue;
            }
            final String label = "promptDefinitions[" + i + "] (key " + key + ")";
            if (definition.getTemplate() != null) {
                throw new IllegalArgumentException(label + " sets both template and templateFile; set one of them");
            }
            final String path = SourcePaths.resolveAgainst(profileDirectory, templateFile);
            loaded.add(inline(key, ProfileFileSupport.read(label + ": templateFile", String.valueOf(path))));
        }
        final File directory = profileDirectory;
        if (directory == null) {
            return loaded;
        }
        for (final String key : usedPromptKeys) {
            if (key == null || !defined.add(key)) {
                continue;
            }
            final File file = ProfileFileSupport.conventionalFile(directory, key, ProfileFileSupport.PROMPT_EXTENSION);
            if (!file.isFile()) {
                throw new IllegalArgumentException("prompt key " + key
                        + " is used by a rule, but no promptDefinition has it and the profile has no " + file);
            }
            loaded.add(inline(key, ProfileFileSupport.read("prompt key " + key + ": file", file.getPath())));
        }
        return loaded;
    }

    private static PromptDefinition inline(final String key, final String template) {
        final PromptDefinition copy = new PromptDefinition();
        copy.setKey(key);
        copy.setTemplate(template);
        return copy;
    }
}
