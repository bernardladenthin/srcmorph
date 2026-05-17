// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.maven.llamacpp.aiindex;

import net.ladenthin.llama.InferenceParameters;
import net.ladenthin.llama.LlamaModel;
import net.ladenthin.llama.ModelParameters;
import net.ladenthin.llama.Pair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class LlamaCppJniAiSummaryProvider implements AiGenerationProvider, AutoCloseable {

    private final LlamaCppJniConfig config;
    private final LlamaModel model;
    private final AiPromptSupport promptSupport;
    private final AiResponseNormalizer responseNormalizer = new AiResponseNormalizer();

    public LlamaCppJniAiSummaryProvider(
            final LlamaCppJniConfig config,
            final AiPromptSupport promptSupport
    ) {
        this.config = Objects.requireNonNull(config, "config");
        this.promptSupport = Objects.requireNonNull(promptSupport, "promptSupport");

        final ModelParameters modelParameters = new ModelParameters()
                .setModel(config.modelPath())
                .setCtxSize(config.contextSize())
                .setThreads(config.threads())
                .setChatTemplateKwargs(Collections.singletonMap("enable_thinking", String.valueOf(config.chatTemplateEnableThinking())));

        this.model = new LlamaModel(modelParameters);
    }

    @Override
    public String generate(final AiGenerationRequest request) throws IOException {
        return generate(request, config.temperature());
    }

    @Override
    public String generate(final AiGenerationRequest request, final float temperatureOverride) throws IOException {
        final String prompt = promptSupport.buildPrompt(request);

        final List<Pair<String, String>> messages = new ArrayList<>();
        messages.add(new Pair<>("user", prompt));

        final InferenceParameters inferenceParameters = new InferenceParameters("")
                .setMessages(null, messages)
                .setUseChatTemplate(true)
                .setTemperature(temperatureOverride)
                .setNPredict(config.maxOutputTokens())
                .setTopP(config.topP())
                .setTopK(config.topK())
                .setRepeatPenalty(config.repeatPenalty());

        inferenceParameters.setStopStrings(config.stopStrings().toArray(new String[0]));

        return responseNormalizer.normalize(model.chatCompleteText(inferenceParameters));
    }

    @Override
    public void close() throws IOException {
        model.close();
    }
}
