// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.maven.llamacpp.aiindex.config;

import java.util.List;
import lombok.ToString;
import org.jspecify.annotations.Nullable;

/**
 * Selects the {@link AiFieldGenerationConfig} that applies to a given source file, based on the
 * entries' {@link AiFieldGenerationConfig#getFileExtensions() file extensions}.
 *
 * <p>Selection rule, in list order: the first entry whose non-empty extension list matches the file
 * name wins; otherwise the first extension-agnostic entry (empty or absent extension list) is the
 * fallback. Returns {@code null} when no entry matches and no fallback is configured.</p>
 *
 * <p>This lets the prompt vary by language while a single AI model stays loaded for the whole run —
 * e.g. a Java-specific prompt for {@code .java}, a SQL-schema prompt for {@code .sql}, and a generic
 * fallback for everything else. A configuration with one extension-agnostic entry (the historical
 * shape) keeps working unchanged: that entry is the fallback and applies to every file.</p>
 */
@ToString
public final class AiFieldGenerationSelector {

    /** Creates a new {@link AiFieldGenerationSelector}. */
    public AiFieldGenerationSelector() {
        // no-op
    }

    /**
     * Returns the field generation that applies to {@code fileName}.
     *
     * @param configs  the configured field generations, in declaration order; {@code null} entries
     *                 are skipped
     * @param fileName the source file name (e.g. {@code Foo.java})
     * @return the first extension-matching entry, else the first fallback entry, else {@code null}
     */
    public @Nullable AiFieldGenerationConfig selectForFileName(
            final Iterable<AiFieldGenerationConfig> configs, final String fileName) {
        AiFieldGenerationConfig fallback = null;
        for (final AiFieldGenerationConfig config : configs) {
            if (config == null) {
                continue;
            }
            final List<String> extensions = config.getFileExtensions();
            if (extensions == null || extensions.isEmpty()) {
                if (fallback == null) {
                    fallback = config;
                }
                continue;
            }
            for (final String extension : extensions) {
                if (fileName.endsWith(extension)) {
                    return config;
                }
            }
        }
        return fallback;
    }
}
