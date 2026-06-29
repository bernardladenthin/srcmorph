// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.maven.llamacpp.aiindex.provider;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import net.ladenthin.maven.llamacpp.aiindex.CommonTestFixtures;
import net.ladenthin.maven.llamacpp.aiindex.config.AiGenerationConfig;
import net.ladenthin.maven.llamacpp.aiindex.document.AiGenerationRequest;
import net.ladenthin.maven.llamacpp.aiindex.document.AiMdHeader;
import net.ladenthin.maven.llamacpp.aiindex.document.AiMdHeaderCodec;
import net.ladenthin.maven.llamacpp.aiindex.prompt.AiPromptSupport;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;

public class LlamaCppJniAiGenerationProviderTest {

    private static final String MODEL_PATH = Paths.get("src", "test", "resources", "SmolLM2-135M-Instruct-Q3_K_M.gguf")
            .toAbsolutePath()
            .toString();

    // <editor-fold defaultstate="collapsed" desc="generate">
    @Test
    public void generate_realProvider_returnsNonEmptyResponse() throws Exception {
        // arrange — skip if native lib is unavailable or model is missing
        Assumptions.assumeTrue(
                Boolean.getBoolean("runNativeLlamaTests"),
                "Native llama test disabled. Enable with -DrunNativeLlamaTests=true");
        Assumptions.assumeTrue(Files.exists(Paths.get(MODEL_PATH)), "Model file missing: " + MODEL_PATH);

        final LlamaCppJniConfig config = new LlamaCppJniConfig(
                null,
                MODEL_PATH,
                32768,
                128,
                0.15f,
                8,
                AiGenerationConfig.DEFAULT_TOP_P,
                AiGenerationConfig.DEFAULT_TOP_K,
                AiGenerationConfig.DEFAULT_MIN_P,
                AiGenerationConfig.DEFAULT_TOP_N_SIGMA,
                AiGenerationConfig.DEFAULT_REPEAT_PENALTY,
                AiGenerationConfig.DEFAULT_CHAT_TEMPLATE_ENABLE_THINKING,
                AiGenerationConfig.DEFAULT_CACHE_PROMPT,
                AiGenerationConfig.DEFAULT_SWA_FULL,
                AiGenerationConfig.DEFAULT_CACHE_REUSE,
                AiGenerationConfig.DEFAULT_GPU_LAYERS,
                AiGenerationConfig.DEFAULT_REASONING_EFFORT,
                AiGenerationConfig.DEFAULT_REASONING_BUDGET_TOKENS,
                AiGenerationConfig.DEFAULT_DRY_MULTIPLIER,
                AiGenerationConfig.DEFAULT_DRY_BASE,
                AiGenerationConfig.DEFAULT_DRY_ALLOWED_LENGTH,
                AiGenerationConfig.DEFAULT_DRY_PENALTY_LAST_N,
                Collections.emptyList(),
                Collections.emptyList());
        final AiPromptSupport promptSupport = new AiPromptSupport(CommonTestFixtures.createFilePromptDefinitions());

        final AiMdHeader header = new AiMdHeader(
                "Test.java",
                AiMdHeaderCodec.HEADER_VERSION_1_0,
                "00000000",
                "2026-03-18T00:00:00Z",
                "2026-03-18T00:00:00Z",
                "0.1.0-SNAPSHOT",
                "0.0.0",
                AiMdHeaderCodec.NODE_TYPE_FILE);
        final String source = "package com.example;\n" + "\n"
                + "public class Test {\n"
                + "\n"
                + "    public String hello(final String name) {\n"
                + "        return \"Hello \" + name;\n"
                + "    }\n"
                + "}\n";

        final AiGenerationRequest bodyRequest = new AiGenerationRequest(
                CommonTestFixtures.PROMPT_KEY_FILE_BODY, Paths.get("Test.java"), source, header);

        // act
        try (LlamaCppJniAiGenerationProvider provider = new LlamaCppJniAiGenerationProvider(config, promptSupport)) {
            final String body = provider.generate(bodyRequest);

            // assert
            assertThat(body, is(notNullValue()));
            assertThat(body.trim().isEmpty(), is(false));
        }
    }
    // </editor-fold>
}
