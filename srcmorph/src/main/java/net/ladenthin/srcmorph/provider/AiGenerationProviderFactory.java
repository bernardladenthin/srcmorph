// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.provider;

import lombok.ToString;
import net.ladenthin.srcmorph.prompt.AiPromptSupport;
import net.ladenthin.srcmorph.support.Java8CompatibilityHelper;
import org.jspecify.annotations.Nullable;

/** Selects and instantiates an {@link AiGenerationProvider} implementation by name. */
@ToString
public class AiGenerationProviderFactory {

    /** Creates a new {@link AiGenerationProviderFactory}. */
    public AiGenerationProviderFactory() {
        // no-op
    }

    /** Provider key selecting the deterministic, model-free {@link MockAiGenerationProvider}. */
    public static final String PROVIDER_MOCK = "mock";

    /** Provider key selecting the llama.cpp JNI provider, which loads a real GGUF model. */
    public static final String PROVIDER_LLAMACPP_JNI = "llamacpp-jni";

    /**
     * Provider key selecting {@link OpenAiCompatibleAiGenerationProvider}: a remote or local server with
     * an OpenAI-compatible chat-completions API (OpenRouter, {@code llama-server}, jllama's
     * {@code NativeServer}).
     */
    public static final String PROVIDER_OPENAI_COMPATIBLE = "openai-compatible";

    private final Java8CompatibilityHelper compatibilityHelper = new Java8CompatibilityHelper();

    /**
     * The provider a model runs on: its own {@code provider} when it names one, otherwise the run's
     * {@code generationProvider}.
     *
     * @param runProvider   the run's {@code generationProvider}, may be {@code null}
     * @param modelProvider the model definition's {@code provider}, may be {@code null} or empty
     * @return the provider key to use, {@code null} or empty only when neither names one
     */
    public static @Nullable String effectiveProvider(
            final @Nullable String runProvider, final @Nullable String modelProvider) {
        return modelProvider == null || modelProvider.trim().isEmpty() ? runProvider : modelProvider.trim();
    }

    /**
     * Creates an {@link AiGenerationProvider} for the given provider name, or for the model's own
     * {@code provider} when its configuration names one ({@link #effectiveProvider}).
     *
     * @param providerName  the run's provider key; {@link #PROVIDER_MOCK}, {@link #PROVIDER_LLAMACPP_JNI}
     *                      or {@link #PROVIDER_OPENAI_COMPATIBLE} (defaults to mock when blank or
     *                      {@code null})
     * @param llamaConfig   the model's configuration
     * @param promptSupport prompt lookup support passed to providers that need it
     * @return a newly-created provider instance
     * @throws IllegalArgumentException if the provider name is not recognised
     */
    public AiGenerationProvider create(
            final String providerName, final LlamaCppJniConfig llamaConfig, final AiPromptSupport promptSupport) {
        final String effective = effectiveProvider(providerName, llamaConfig.provider());
        if (effective == null || compatibilityHelper.isBlank(effective)) {
            return new MockAiGenerationProvider();
        }

        switch (effective) {
            case PROVIDER_MOCK:
                return new MockAiGenerationProvider();
            case PROVIDER_LLAMACPP_JNI:
                return new LlamaCppJniAiGenerationProvider(llamaConfig, promptSupport);
            case PROVIDER_OPENAI_COMPATIBLE:
                return new OpenAiCompatibleAiGenerationProvider(llamaConfig, promptSupport);
            default:
                throw new IllegalArgumentException("Unsupported AI provider: " + effective);
        }
    }
}
