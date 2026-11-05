// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.indexer;

import java.util.List;
import net.ladenthin.srcmorph.config.AiFieldGenerationConfig;
import net.ladenthin.srcmorph.config.AiGenerationConfig;
import net.ladenthin.srcmorph.prompt.AiPromptSupport;
import net.ladenthin.srcmorph.support.AiChecksumSupport;
import net.ladenthin.srcmorph.support.AiSourceLines;
import org.jspecify.annotations.Nullable;

/**
 * The hashes a summary's header records of what wrote it, so a later run can tell a change
 * ({@link net.ladenthin.srcmorph.document.AiMdHeaderSupport}): the prompts ({@code P}), the model
 * ({@code M}) and the child summaries a package or the project reads ({@code K}); the fact counters'
 * ({@code N}) is {@link net.ladenthin.srcmorph.facts.AiFactExtractor#factsHash}. Each is a CRC32, as
 * hex. Pure; no I/O.
 */
public final class AiHeaderHashes {

    private static final AiChecksumSupport CHECKSUMS = new AiChecksumSupport();

    /** Tags the map prompt in the prompt hash, so a prompt that moves from the map to the reduce changes it. */
    private static final String MAP_PROMPT_TAG = "\u0000map\u0000";

    /** Tags the reduce prompt in the prompt hash. */
    private static final String REDUCE_PROMPT_TAG = "\u0000reduce\u0000";

    /** Separates the parts of the model hash and the child summaries of the children hash. */
    private static final char SEPARATOR = '\u0000';

    /** Utility class; not instantiable. */
    private AiHeaderHashes() {
        // no-op
    }

    /**
     * Returns the hash of the system prompt a rule's summaries are written with, and of its map and reduce
     * prompts when it has them, so a change to either shows too. Line endings are read as {@code \n}, as
     * the source checksum reads them, so a profile checked out with CRLF on one machine and LF on another
     * hashes the same.
     *
     * @param promptSupport the prompts
     * @param rule          the rule
     * @return the hash
     * @throws IllegalArgumentException if a prompt of the rule is not registered
     */
    public static String prompt(final AiPromptSupport promptSupport, final AiFieldGenerationConfig rule) {
        final String mapKey = rule.getMapPromptKey();
        final String reduceKey = rule.getReducePromptKey();
        return CHECKSUMS.calculateCrc32Hex(AiSourceLines.normalize(promptSupport.systemPrompt(rule.requirePromptKey())
                + (mapKey != null ? MAP_PROMPT_TAG + promptSupport.systemPrompt(mapKey) : "")
                + (reduceKey != null ? REDUCE_PROMPT_TAG + promptSupport.systemPrompt(reduceKey) : "")));
    }

    /**
     * Returns the hash of the model a rule's summaries are written with: the model definition's key, the
     * remote model it requests and the file name of the model it loads, so another definition, or another
     * model under the same key, shows; moving the model directory, or a sampling parameter, does not
     * (raise {@code aiVersion} for that).
     *
     * @param key    the model definition's key
     * @param config the definition's resolved configuration
     * @return the hash
     */
    public static String model(final String key, final AiGenerationConfig config) {
        return CHECKSUMS.calculateCrc32Hex(
                key + SEPARATOR + config.getRemoteModel() + SEPARATOR + fileName(config.getModelPath()));
    }

    /**
     * Returns the hash of the child summaries a package or the project is written from, in their order.
     *
     * @param summaries the child summaries (bodies or leads)
     * @return the hash, or {@code ""} without children
     */
    public static String children(final List<String> summaries) {
        if (summaries.isEmpty()) {
            return "";
        }
        final StringBuilder text = new StringBuilder();
        for (final String summary : summaries) {
            text.append(summary).append(SEPARATOR);
        }
        return CHECKSUMS.calculateCrc32Hex(text.toString());
    }

    /**
     * Returns the file name of a model path, with either separator.
     *
     * @param modelPath the path, or {@code null}
     * @return what follows its last separator, or {@code ""}
     */
    private static String fileName(final @Nullable String modelPath) {
        if (modelPath == null) {
            return "";
        }
        return modelPath.substring(Math.max(modelPath.lastIndexOf('/'), modelPath.lastIndexOf('\\')) + 1);
    }
}
