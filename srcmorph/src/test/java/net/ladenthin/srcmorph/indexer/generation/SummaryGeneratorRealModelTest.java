// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.indexer.generation;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import net.ladenthin.srcmorph.CommonTestFixtures;
import net.ladenthin.srcmorph.NativeLlamaAvailability;
import net.ladenthin.srcmorph.config.FactCounter;
import net.ladenthin.srcmorph.config.ModelDefinition;
import net.ladenthin.srcmorph.config.ModelRegistry;
import net.ladenthin.srcmorph.config.RoutingRule;
import net.ladenthin.srcmorph.facts.FactExtractor;
import net.ladenthin.srcmorph.prompt.PromptCatalog;
import net.ladenthin.srcmorph.prompt.PromptPreparer;
import net.ladenthin.srcmorph.provider.LlamaCppJniGenerationProvider;
import net.ladenthin.srcmorph.provider.ProviderConfig;
import org.junit.jupiter.api.Test;

/**
 * End-to-end smoke test of the {@code onOversize=mapReduce} pipeline (chunk &rarr; hierarchical reduce)
 * plus the exact {@code <facts>} block, driven by the <em>real</em> llama.cpp JNI provider and the small
 * bundled test model. Runs by default; skipped only when the native library or the model is missing
 * (see {@link net.ladenthin.srcmorph.NativeLlamaAvailability}).
 * Unit tests cover the orchestration with the mock provider; this proves the same path works against a
 * real model (real generation, real prompt-cache reuse, real trimming).
 */
public class SummaryGeneratorRealModelTest {

    private static final String MODEL_PATH = NativeLlamaAvailability.modelPath();

    /**
     * Small context so a modest synthetic source is over-window and triggers map-reduce with the tiny model, yet
     * leaves a chunk of at least the minimum next to the prompts.
     */
    private static final int SMALL_CONTEXT = 1024;

    private static final String MODEL_KEY = "smol";

    private static String largeSource(final int lines) {
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < lines; i++) {
            sb.append("line ").append(i).append(" = value;\n");
        }
        return sb.toString();
    }

    @Test
    public void mapReduceWithFacts_realModel_producesFactsPlusSummary() throws Exception {
        NativeLlamaAvailability.assumeAvailable();

        final ModelDefinition def = new ModelDefinition();
        def.setKey(MODEL_KEY);
        def.setModelPath(MODEL_PATH);
        def.setContextSize(SMALL_CONTEXT);
        def.setMaxOutputTokens(48);
        def.setCharsPerToken(3);
        final ModelRegistry models = new ModelRegistry(Collections.singletonList(def));

        final PromptCatalog promptSupport = new PromptCatalog(CommonTestFixtures.createFilePromptDefinitions());
        final PromptPreparer prep = new PromptPreparer(promptSupport);
        final ProviderConfig jniConfig = ProviderConfig.builder()
                .modelPath(MODEL_PATH)
                .contextSize(SMALL_CONTEXT)
                .maxOutputTokens(48)
                .temperature(0.15f)
                .threads(NativeLlamaAvailability.THREADS)
                .build();

        final RoutingRule rule = new RoutingRule();
        rule.setPromptKey(CommonTestFixtures.PROMPT_KEY_FILE_BODY);
        rule.setAiDefinitionKey(MODEL_KEY);
        rule.setOnOversize("sampleChunks");
        rule.setMaxChunks(2);
        // the tiny test model is cut off at the test budget; keep what it wrote, as the path is under test
        rule.setOnIncomplete("keep");
        final FactCounter counter = new FactCounter();
        counter.setLabel("lines");
        counter.setPattern("(?m)^line ");
        rule.setFacts(Collections.singletonList(counter));

        final int lineCount = 400;
        final String source = largeSource(lineCount);
        final Path contextFile = Files.createTempFile("Data", ".java");

        try (LlamaCppJniGenerationProvider provider = new LlamaCppJniGenerationProvider(jniConfig, promptSupport)) {
            final SummaryGenerator support = new SummaryGenerator(provider, prep, models);
            final SummaryResult result = support.generate((rule), contextFile, "file", source);

            // The exact facts (counted over the whole source) lead the body; the map-reduced AI summary
            // follows. This proves the real-model chunk -> hierarchical-reduce path completes end-to-end.
            assertThat(result.body(), startsWith(FactExtractor.FACTS_HEADER));
            assertThat(result.body(), containsString("lines: " + lineCount));
            assertThat(
                    result.body().length()
                            > FactExtractor.factsBlock(rule.getFacts(), source).length(),
                    is(true));
        }
    }
}
