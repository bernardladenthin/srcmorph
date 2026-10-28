// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.provider;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;

import java.nio.file.Paths;
import net.ladenthin.srcmorph.CommonTestFixtures;
import net.ladenthin.srcmorph.document.AiGenerationRequest;
import net.ladenthin.srcmorph.document.AiMdHeader;
import net.ladenthin.srcmorph.document.AiMdHeaderCodec;
import net.ladenthin.srcmorph.prompt.AiPromptSupport;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Runs {@link OpenAiCompatibleAiGenerationProvider} against a real server. Skipped unless
 * {@value #BASE_URL_PROPERTY} names one, so a normal build never touches the network.
 *
 * <ul>
 *   <li>OpenRouter: {@code -Dnet.ladenthin.srcmorph.openai.baseUrl=https://openrouter.ai/api/v1
 *       -Dnet.ladenthin.srcmorph.openai.model=openai/gpt-6-luna} with {@code OPENROUTER_API_KEY} in the
 *       environment;</li>
 *   <li>a llama.cpp {@code llama-server} or jllama's {@code NativeServer}:
 *       {@code -Dnet.ladenthin.srcmorph.openai.baseUrl=http://127.0.0.1:8080/v1}, no model, no key.</li>
 * </ul>
 */
public class OpenAiCompatibleLiveTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(OpenAiCompatibleLiveTest.class);

    /** System property naming the server's base URL; the test is skipped without it. */
    public static final String BASE_URL_PROPERTY = "net.ladenthin.srcmorph.openai.baseUrl";

    /** System property naming the model to request; empty for a single-model local server. */
    public static final String MODEL_PROPERTY = "net.ladenthin.srcmorph.openai.model";

    /** System property naming the environment variable that holds the key. */
    public static final String API_KEY_ENV_PROPERTY = "net.ladenthin.srcmorph.openai.apiKeyEnv";

    /** Output budget: enough for a reasoning model's low-effort thinking plus a short summary. */
    private static final int MAX_OUTPUT_TOKENS = 1024;

    private static final String SOURCE = "package com.example;\n\n"
            + "public final class Greeter {\n"
            + "    public String greet(final String name) {\n"
            + "        return \"Hello, \" + name + \"!\";\n"
            + "    }\n"
            + "}\n";

    @Test
    public void generateWithTimings_realServer_describesTheFile() throws Exception {
        // arrange
        final String baseUrl = System.getProperty(BASE_URL_PROPERTY, "");
        Assumptions.assumeTrue(!baseUrl.trim().isEmpty(), "-D" + BASE_URL_PROPERTY + " is not set");
        final LlamaCppJniConfig config = LlamaCppJniConfig.builder("")
                .provider(AiGenerationProviderFactory.PROVIDER_OPENAI_COMPATIBLE)
                .baseUrl(baseUrl)
                .remoteModel(System.getProperty(MODEL_PROPERTY, ""))
                .apiKeyEnv(System.getProperty(API_KEY_ENV_PROPERTY, "OPENROUTER_API_KEY"))
                .maxOutputTokens(MAX_OUTPUT_TOKENS)
                .build();
        final AiPromptSupport promptSupport = new AiPromptSupport(CommonTestFixtures.createFilePromptDefinitions());
        final AiMdHeader header = new AiMdHeader(
                "Greeter.java",
                AiMdHeaderCodec.HEADER_VERSION_1_0,
                "00000000",
                "2026-10-28T00:00:00Z",
                "2026-10-28T00:00:00Z",
                "0.1.0-SNAPSHOT",
                "0.0.0",
                AiMdHeaderCodec.NODE_TYPE_FILE);
        final AiGenerationRequest request = new AiGenerationRequest(
                CommonTestFixtures.PROMPT_KEY_FILE_BODY, Paths.get("Greeter.java"), SOURCE, header);

        // act -- through the factory, as an engine would create it
        final AiGenerationTimings timings;
        try (AiGenerationProvider provider = new AiGenerationProviderFactory()
                .create(AiGenerationProviderFactory.PROVIDER_LLAMACPP_JNI, config, promptSupport)) {
            timings = provider.generateWithTimings(request);
        }

        // assert -- the text is logged so a human can judge it; the test only checks it is there
        LOGGER.info(
                "{} answered with {} prompt / {} generated tokens:\n{}",
                baseUrl,
                timings.promptTokens(),
                timings.predictedTokens(),
                timings.text());
        assertThat(timings.text().trim().isEmpty(), is(false));
        assertThat(timings.promptTokens(), is(greaterThan(0)));
        assertThat(timings.predictedTokens(), is(greaterThan(0)));
    }
}
