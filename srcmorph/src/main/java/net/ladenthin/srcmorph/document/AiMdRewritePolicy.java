// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.document;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import lombok.ToString;
import net.ladenthin.srcmorph.facts.FactExtractor;
import net.ladenthin.srcmorph.support.Java8CompatibilityHelper;

/**
 * Decides whether an {@code .ai.md} file is rewritten: always under {@code force}, for a missing, empty or
 * structurally different summary, for another {@code aiVersion}, and for each change whose switch is on
 * (the configuration's {@code regenerateOn}, {@code net.ladenthin.srcmorph.config.RegenerateOn}).
 */
@ToString
public class AiMdRewritePolicy {

    /** Creates a new {@link AiMdRewritePolicy} with every switch on. */
    public AiMdRewritePolicy() {
        this(true, true, true, true, true);
    }

    /**
     * Creates a new {@link AiMdRewritePolicy} with one switch per header field: whether that field
     * rewrites the file when it differs.
     *
     * @param source   the checksum ({@code C})
     * @param prompt   the prompt hash ({@code P})
     * @param facts    the facts hash ({@code N})
     * @param model    the model hash ({@code M})
     * @param children the children hash ({@code K})
     */
    public AiMdRewritePolicy(
            final boolean source,
            final boolean prompt,
            final boolean facts,
            final boolean model,
            final boolean children) {
        this.source = source;
        this.prompt = prompt;
        this.facts = facts;
        this.model = model;
        this.children = children;
    }

    private final Java8CompatibilityHelper compatibilityHelper = new Java8CompatibilityHelper();

    private final boolean source;

    private final boolean prompt;

    private final boolean facts;

    private final boolean model;

    private final boolean children;

    /**
     * Determines whether the target {@code .ai.md} file should be rewritten.
     *
     * @param force           when {@code true}, always rewrite
     * @param targetFile      path to the {@code .ai.md} file
     * @param expectedHeader  header that would be written
     * @return {@code true} when the file is missing, the body is blank or holds the facts line alone (what
     *         1.3.0 wrote for an empty or cut-off answer), the header version, node type, title or
     *         {@code aiVersion} differs from {@code expectedHeader}, or a field whose switch is on differs:
     *         the checksum ({@code C}, source), the prompt hash ({@code P}), the facts hash ({@code N}), the
     *         model hash ({@code M}), the children hash ({@code K}). A field the summary does not record
     *         differs from one that is set. The source's modification time and the plugin version are not
     *         compared: a fresh clone, a touched file or a new srcmorph release regenerate nothing whose
     *         content is the same.
     * @throws IOException if the existing file cannot be read
     */
    public boolean shouldRewrite(final boolean force, final Path targetFile, final AiMdHeader expectedHeader)
            throws IOException {
        if (force) {
            return true;
        }

        if (!Files.exists(targetFile)) {
            return true;
        }

        final AiMdDocument actualDocument = new AiMdDocumentCodec().read(targetFile);
        final AiMdHeader actualHeader = actualDocument.header();

        if (!AiMdHeaderCodec.HEADER_VERSION_1_0.equals(actualHeader.headerVersion())) {
            return true;
        }

        if (compatibilityHelper.isBlank(actualDocument.body()) || isFactsLineOnly(actualDocument.body())) {
            return true;
        }

        return !expectedHeader.headerVersion().equals(actualHeader.headerVersion())
                || !expectedHeader.nodeType().equals(actualHeader.nodeType())
                || !expectedHeader.title().equals(actualHeader.title())
                || !expectedHeader.aiVersion().equals(actualHeader.aiVersion())
                || changed(source, expectedHeader.checksum(), actualHeader.checksum())
                || changed(prompt, expectedHeader.promptHash(), actualHeader.promptHash())
                || changed(facts, expectedHeader.factsHash(), actualHeader.factsHash())
                || changed(model, expectedHeader.modelHash(), actualHeader.modelHash())
                || changed(children, expectedHeader.childrenHash(), actualHeader.childrenHash());
    }

    /**
     * Returns whether a field rewrites the file: its switch is on and the values differ.
     *
     * @param enabled  the field's switch
     * @param expected the value that would be written
     * @param actual   the value the file holds
     * @return {@code true} when it rewrites the file
     */
    private static boolean changed(final boolean enabled, final String expected, final String actual) {
        return enabled && !expected.equals(actual);
    }

    /**
     * Returns whether a body is srcmorph's facts line and nothing else: no summary. Since 1.4.0 an
     * incomplete answer is never written, but 1.3.0 wrote the facts line alone for an empty or cut-off one.
     *
     * @param body the body of the existing file
     * @return {@code true} if the body is one line that starts with {@link FactExtractor#FACTS_HEADER}
     */
    private static boolean isFactsLineOnly(final String body) {
        final String trimmed = body.trim();
        return trimmed.startsWith(FactExtractor.FACTS_HEADER) && !trimmed.contains("\n");
    }
}
