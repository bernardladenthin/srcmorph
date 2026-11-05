// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.indexer;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Arrays;
import java.util.Collections;
import net.ladenthin.srcmorph.config.ModelDefinition;
import net.ladenthin.srcmorph.config.PromptDefinition;
import net.ladenthin.srcmorph.config.RoutingRule;
import net.ladenthin.srcmorph.prompt.PromptCatalog;
import net.ladenthin.srcmorph.support.ChecksumSupport;
import org.junit.jupiter.api.Test;

public class HeaderHashesTest {

    private static final ChecksumSupport CHECKSUMS = new ChecksumSupport();

    private static ModelDefinition model(final String remoteModel, final String modelPath) {
        final ModelDefinition config = new ModelDefinition();
        config.setRemoteModel(remoteModel);
        config.setModelPath(modelPath);
        return config;
    }

    private static String promptHash(final String template) {
        final PromptDefinition definition = new PromptDefinition();
        definition.setKey("p");
        definition.setTemplate(template);
        final RoutingRule rule = new RoutingRule();
        rule.setPromptKey("p");
        return HeaderHashes.prompt(new PromptCatalog(Collections.singletonList(definition)), rule);
    }

    // <editor-fold defaultstate="collapsed" desc="prompt">
    @Test
    public void prompt_crlfOrCrLineEndings_hashAsLineFeeds() {
        // act
        final String lf = promptHash("Summarize.\nName the types.\n");
        final String crlf = promptHash("Summarize.\r\nName the types.\r\n");
        final String cr = promptHash("Summarize.\rName the types.\r");

        // assert
        assertThat(crlf, is(lf));
        assertThat(cr, is(lf));
        assertThat(lf, is(CHECKSUMS.calculateCrc32Hex("Summarize.\nName the types.\n")));
    }

    @Test
    public void prompt_anotherText_changesIt() {
        // act
        final String one = promptHash("Summarize.\n");
        final String other = promptHash("Summarize briefly.\n");

        // assert
        assertThat(one.equals(other), is(false));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="model">
    @Test
    public void model_isTheKeyTheRemoteModelAndTheModelFileName() {
        assertThat(
                HeaderHashes.model("luna", model("gpt-6-luna", "/models/a.gguf")),
                is(CHECKSUMS.calculateCrc32Hex("luna\u0000gpt-6-luna\u0000a.gguf")));
    }

    @Test
    public void model_anotherKeyRemoteModelOrModelFile_changesIt() {
        final String base = HeaderHashes.model("luna", model("gpt-6-luna", "/models/a.gguf"));

        assertThat(
                HeaderHashes.model("oss", model("gpt-6-luna", "/models/a.gguf")).equals(base), is(false));
        assertThat(
                HeaderHashes.model("luna", model("gpt-oss-20b", "/models/a.gguf"))
                        .equals(base),
                is(false));
        assertThat(
                HeaderHashes.model("luna", model("gpt-6-luna", "/models/b.gguf"))
                        .equals(base),
                is(false));
    }

    @Test
    public void model_aMovedModelDirectory_keepsIt() {
        final String base = HeaderHashes.model("local", model("", "/models/a.gguf"));

        assertThat(HeaderHashes.model("local", model("", "C:\\other\\a.gguf")), is(base));
        assertThat(HeaderHashes.model("local", model("", "a.gguf")), is(base));
    }

    @Test
    public void model_withoutAModelPath_hashesAnEmptyFileName() {
        final ModelDefinition remote = model("gpt-6-luna", null);

        assertThat(HeaderHashes.model("luna", remote), is(CHECKSUMS.calculateCrc32Hex("luna\u0000gpt-6-luna\u0000")));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="children">
    @Test
    public void children_withoutSummaries_isEmpty() {
        assertThat(HeaderHashes.children(Collections.<String>emptyList()), is(""));
    }

    @Test
    public void children_isEverySummaryInOrderEachClosed() {
        assertThat(HeaderHashes.children(Arrays.asList("a", "b")), is(CHECKSUMS.calculateCrc32Hex("a\u0000b\u0000")));
        assertThat(
                HeaderHashes.children(Arrays.asList("b", "a")).equals(HeaderHashes.children(Arrays.asList("a", "b"))),
                is(false));
        // "ab" + "" and "a" + "b" read apart
        assertThat(
                HeaderHashes.children(Arrays.asList("ab", "")).equals(HeaderHashes.children(Arrays.asList("a", "b"))),
                is(false));
    }
    // </editor-fold>
}
