// @formatter:off

// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
// Copyright 2026 Bernard Ladenthin bernard.ladenthin@gmail.com
//
// SPDX-License-Identifier: Apache-2.0
// @formatter:on
package net.ladenthin.maven.llamacpp.aiindex;

public class AiGenerationProviderFactory {

    private final Java8CompatibilityHelper compatibilityHelper = new Java8CompatibilityHelper();

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