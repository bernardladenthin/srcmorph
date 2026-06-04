// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.maven.llamacpp.aiindex;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import net.ladenthin.llama.InferenceParameters;
import net.ladenthin.llama.LlamaModel;
import net.ladenthin.llama.ModelParameters;
import net.ladenthin.llama.Pair;

/**
 * {@link AiGenerationProvider} implementation backed by the {@code net.ladenthin:llama}
 * JNI binding, running GGUF models locally via llama.cpp.
 */
public class LlamaCppJniAiGenerationProvider implements AiGenerationProvider, AutoCloseable {

    private final LlamaCppJniConfig config;
    private final LlamaModel model;
    private final AiPromptSupport promptSupport;
    private final AiCompletionParser completionParser = new AiCompletionParser();

    /**
     * Creates a new {@link LlamaCppJniAiGenerationProvider} and loads the configured GGUF model.
     *
     * @param config        llama.cpp configuration
     * @param promptSupport prompt lookup used to render request prompts
     */
    public LlamaCppJniAiGenerationProvider(final LlamaCppJniConfig config, final AiPromptSupport promptSupport) {
        this.config = Objects.requireNonNull(config, "config");
        this.promptSupport = Objects.requireNonNull(promptSupport, "promptSupport");

        final ModelParameters modelParameters = new ModelParameters()
                .setModel(config.modelPath())
                .setCtxSize(config.contextSize())
                .setThreads(config.threads())
                .setChatTemplateKwargs(Collections.singletonMap(
                        "enable_thinking", String.valueOf(config.chatTemplateEnableThinking())));

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

        // setMessages(systemMessage, ...) accepts null upstream to omit the system message,
        // but it is unannotated, so Checker Framework infers @NonNull.
        @SuppressWarnings("argument")
        final InferenceParameters inferenceParameters = new InferenceParameters("")
                .setMessages(null, messages)
                .setUseChatTemplate(true)
                .setTemperature(temperatureOverride)
                .setNPredict(config.maxOutputTokens())
                .setTopP(config.topP())
                .setTopK(config.topK())
                .setRepeatPenalty(config.repeatPenalty());

        inferenceParameters.setStopStrings(config.stopStrings().toArray(new String[0]));

        return completionParser.parseCompletion(model.chatCompleteText(inferenceParameters));
    }

    @Override
    public void close() throws IOException {
        model.close();
    }
}
