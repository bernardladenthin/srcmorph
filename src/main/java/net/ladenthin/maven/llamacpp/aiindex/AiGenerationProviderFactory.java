// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.maven.llamacpp.aiindex;

/** Selects and instantiates an {@link AiGenerationProvider} implementation by name. */
public class AiGenerationProviderFactory {

    /** Creates a new {@link AiGenerationProviderFactory}. */
    public AiGenerationProviderFactory() {
        // no-op
    }

    private final Java8CompatibilityHelper compatibilityHelper = new Java8CompatibilityHelper();

    /**
     * Creates an {@link AiGenerationProvider} for the given provider name.
     *
     * @param providerName  provider key; {@code "mock"} or {@code "llamacpp-jni"} (defaults to mock when blank or {@code null})
     * @param llamaConfig   configuration for the llama.cpp JNI provider
     * @param promptSupport prompt lookup support passed to providers that need it
     * @return a newly-created provider instance
     * @throws IllegalArgumentException if {@code providerName} is not recognised
     */
    public AiGenerationProvider create(
            final String providerName,
            final LlamaCppJniConfig llamaConfig,
            final AiPromptSupport promptSupport
    ) {
        if (providerName == null || compatibilityHelper.isBlank(providerName)) {
            return new MockAiGenerationProvider();
        }

        switch (providerName) {
            case "mock":
                return new MockAiGenerationProvider();
            case "llamacpp-jni":
                return new LlamaCppJniAiSummaryProvider(llamaConfig, promptSupport);
            default:
                throw new IllegalArgumentException("Unsupported AI provider: " + providerName);
        }
    }
}