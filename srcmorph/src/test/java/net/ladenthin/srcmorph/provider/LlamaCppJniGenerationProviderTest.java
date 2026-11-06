// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.provider;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.file.Files;
import java.nio.file.Paths;
import net.ladenthin.srcmorph.CommonTestFixtures;
import net.ladenthin.srcmorph.NativeLlamaAvailability;
import net.ladenthin.srcmorph.document.AiMdHeader;
import net.ladenthin.srcmorph.document.AiMdHeaderCodec;
import net.ladenthin.srcmorph.prompt.GenerationRequest;
import net.ladenthin.srcmorph.prompt.PromptCatalog;
import org.junit.jupiter.api.Test;

public class LlamaCppJniGenerationProviderTest {

    private static final String MODEL_PATH = NativeLlamaAvailability.modelPath();

    private static final AiMdHeader HEADER = new AiMdHeader(
            "Test.java",
            AiMdHeaderCodec.HEADER_VERSION_1_0,
            "00000000",
            "2026-03-18T00:00:00Z",
            "2026-03-18T00:00:00Z",
            "0.1.0-SNAPSHOT",
            "0.0.0",
            AiMdHeaderCodec.NODE_TYPE_FILE);

    private static ProviderConfig config(final int contextSize, final int maxOutputTokens) {
        return ProviderConfig.builder()
                .modelPath(MODEL_PATH)
                .contextSize(contextSize)
                .maxOutputTokens(maxOutputTokens)
                .temperature(0.15f)
                .threads(NativeLlamaAvailability.THREADS)
                .build();
    }

    private static GenerationRequest request(final String source) {
        return new GenerationRequest(CommonTestFixtures.PROMPT_KEY_FILE_BODY, Paths.get("Test.java"), source);
    }

    // <editor-fold defaultstate="collapsed" desc="constructor">
    @Test
    public void constructor_withoutModelPath_isRejected() {
        // arrange
        final ProviderConfig remote = ProviderConfig.builder().build();
        final PromptCatalog promptSupport = new PromptCatalog(CommonTestFixtures.createFilePromptDefinitions());

        // act
        final IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class, () -> new LlamaCppJniGenerationProvider(remote, promptSupport));

        // assert
        assertThat(thrown.getMessage().startsWith("A llamacpp-jni model needs a modelPath: "), is(true));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="generate">
    @Test
    public void generate_realProviderAtATinyBudget_refusesTheCutOffAnswerWithWhatItWrote() throws Exception {
        // arrange
        NativeLlamaAvailability.assumeAvailable();
        final PromptCatalog promptSupport = new PromptCatalog(CommonTestFixtures.createFilePromptDefinitions());
        final String source = """
                package com.example;

                public class Test {

                    public String hello(final String name) {
                        return "Hello " + name;
                    }
                }
                """;

        // At an 8-token budget the test model never finishes: generate refuses the cut-off answer and
        // hands over what was written, so a real run never stores it as a summary.
        try (LlamaCppJniGenerationProvider provider =
                new LlamaCppJniGenerationProvider(config(32768, 8), promptSupport)) {
            // act
            final IncompleteAnswerException thrown =
                    assertThrows(IncompleteAnswerException.class, () -> provider.generate(request(source)));

            // assert
            assertThat(thrown.reason(), is(IncompleteAnswerException.Reason.TRUNCATED));
            assertThat(thrown.partialText().trim().isEmpty(), is(false));
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="generateWithTimings">
    @Test
    public void generateWithTimings_realProvider_reportsEngineTimingsThatScaleWithPromptSize() throws Exception {
        // arrange
        NativeLlamaAvailability.assumeAvailable();
        final PromptCatalog promptSupport = new PromptCatalog(CommonTestFixtures.createFilePromptDefinitions());
        final StringBuilder large = new StringBuilder("package com.example;\npublic class Big {\n");
        for (int i = 0; i < 120; i++) {
            large.append("    public int field")
                    .append(i)
                    .append(" = compute(")
                    .append(i)
                    .append(");\n");
        }
        large.append("}\n");

        try (LlamaCppJniGenerationProvider provider =
                new LlamaCppJniGenerationProvider(config(4096, 16), promptSupport)) {
            // act
            final TimedGeneration small = provider.generateWithTimings(request("class A {}"));
            final TimedGeneration big = provider.generateWithTimings(request(large.toString()));

            // assert
            // Real engine timings (not zero-rate default), so the plan gets exact throughput.
            assertThat(small.prefillTokensPerSecond() > 0.0d, is(true));
            assertThat(small.decodeTokensPerSecond() > 0.0d, is(true));
            assertThat(small.promptTokens() > 0, is(true));
            // A clearly larger, distinct prompt processes more prompt tokens -> proves it is the real
            // generation path (not a discarded/zero-timings one).
            assertThat(big.promptTokens() > small.promptTokens(), is(true));
            // Both requests share the same system prompt, so the second one must find that prefix already
            // in the KV cache. This is the end-to-end proof that the prefix-reuse settings (cachePrompt /
            // cacheReuse / swaFull) actually engage -- without cache_n surfaced, their KV-memory cost was
            // paid on faith alone.
            assertThat(big.cachedPromptTokens() > 0, is(true));
            assertThat(big.totalPromptTokens() > big.promptTokens(), is(true));
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="promptTokens">
    @Test
    public void promptTokens_realProvider_areTheTokensTheCallsPrefillReads() throws Exception {
        // arrange
        NativeLlamaAvailability.assumeAvailable();
        final PromptCatalog promptSupport = new PromptCatalog(CommonTestFixtures.createFilePromptDefinitions());
        final GenerationRequest request = request("SELECT 1, 2, 3 FROM dual WHERE x = 'a,b;c';\n");

        try (LlamaCppJniGenerationProvider provider =
                new LlamaCppJniGenerationProvider(config(4096, 16), promptSupport)) {
            // act
            final int counted = provider.promptTokens(request).getAsInt();
            final TimedGeneration generated = provider.generateWithTimings(request);

            // assert
            // the count is the prompt the call reads, chat template included, before any call is made
            assertThat(counted, is(generated.totalPromptTokens()));
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="a model that does not load">
    @Test
    public void generate_aModelThatDoesNotLoad_failsEveryCallWithTheFirstError() throws Exception {
        // arrange: a path no GGUF lies at
        NativeLlamaAvailability.assumeAvailable();
        final PromptCatalog promptSupport = new PromptCatalog(CommonTestFixtures.createFilePromptDefinitions());
        final ProviderConfig missing = ProviderConfig.builder()
                .modelPath(Files.createTempDirectory("no-model")
                        .resolve("missing.gguf")
                        .toString())
                .contextSize(4096)
                .maxOutputTokens(16)
                .threads(NativeLlamaAvailability.THREADS)
                .build();

        try (LlamaCppJniGenerationProvider provider = new LlamaCppJniGenerationProvider(missing, promptSupport)) {
            // act
            final GenerationRequest a = request("class A {}");
            final GenerationRequest b = request("class B {}");
            final IllegalStateException first = assertThrows(IllegalStateException.class, () -> provider.generate(a));
            final IllegalStateException second = assertThrows(IllegalStateException.class, () -> provider.generate(b));

            // assert: the second call is not a second load, it is the first one's failure
            assertThat(first.getMessage(), containsString("missing.gguf"));
            assertThat(second, is(sameInstance(first)));
        }
    }
    // </editor-fold>
}
