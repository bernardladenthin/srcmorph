// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.prompt;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import lombok.ToString;
import net.ladenthin.srcmorph.support.AiPathSupport;
import net.ladenthin.srcmorph.support.Java8CompatibilityHelper;
import org.jspecify.annotations.Nullable;

/**
 * Reads the template of every prompt definition that names a {@code templateFile}, so the rest of
 * the pipeline sees inline templates only.
 *
 * <p>A relative file is resolved against the prompt directory ({@link AiPathSupport#resolveAgainst},
 * the rule model paths follow too) and read as UTF-8, a leading byte order mark dropped. The text is
 * used as it is otherwise: the template is the system message verbatim, so trailing whitespace and
 * line breaks in the file reach the model. A definition with both {@code template} and
 * {@code templateFile} is refused, as is a file that holds nothing but whitespace. The configured
 * definitions are never modified: a definition with a file is replaced by a copy carrying its key and
 * the read template.</p>
 */
@ToString
public final class AiPromptTemplateLoader {

    /** The byte order mark a UTF-8 editor may put before the first character. */
    private static final char BYTE_ORDER_MARK = '﻿';

    private final @Nullable File promptDirectory;
    private final Java8CompatibilityHelper compatibilityHelper = new Java8CompatibilityHelper();

    /**
     * Creates a loader for one configuration.
     *
     * @param promptDirectory directory relative template files are resolved against, or {@code null}
     *                        to resolve them against the working directory
     */
    public AiPromptTemplateLoader(final @Nullable File promptDirectory) {
        this.promptDirectory = promptDirectory;
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
        final int count = promptDefinitions.size();
        final List<AiPromptDefinition> loaded = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            final AiPromptDefinition definition = promptDefinitions.get(i);
            final String templateFile = definition.getTemplateFile();
            if (templateFile == null) {
                loaded.add(definition);
                continue;
            }
            final String label = "promptDefinitions[" + i + "] (key " + definition.getKey() + ")";
            if (definition.getTemplate() != null) {
                throw new IllegalArgumentException(label + " sets both template and templateFile; set one of them");
            }
            final String path = AiPathSupport.resolveAgainst(promptDirectory, templateFile);
            final String template = read(label, String.valueOf(path));
            final AiPromptDefinition copy = new AiPromptDefinition();
            copy.setKey(definition.getKey());
            copy.setTemplate(template);
            loaded.add(copy);
        }
        return loaded;
    }

    /**
     * Reads one template file.
     *
     * @param label the definition's position and key, for the messages
     * @param path  the resolved path
     * @return the file's text, without a leading byte order mark
     * @throws IOException              if the file cannot be read, or the path is no valid path
     * @throws IllegalArgumentException if the file holds nothing but whitespace
     */
    private String read(final String label, final String path) throws IOException {
        final String text;
        try {
            text = new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
        } catch (final IOException | InvalidPathException e) {
            throw new IOException(label + ": templateFile " + path + " cannot be read: " + e, e);
        }
        final String template = !text.isEmpty() && text.charAt(0) == BYTE_ORDER_MARK ? text.substring(1) : text;
        if (compatibilityHelper.isBlank(template)) {
            throw new IllegalArgumentException(label + ": templateFile " + path + " is empty");
        }
        return template;
    }
}
