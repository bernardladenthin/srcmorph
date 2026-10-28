// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.provider;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import net.ladenthin.srcmorph.prompt.AiPromptSupport;
import org.junit.jupiter.api.Test;

public class AiGenerationProviderFactoryTest {

    private final AiGenerationProviderFactory factory = new AiGenerationProviderFactory();
    private final AiPromptSupport promptSupport = new AiPromptSupport(null);

    private static LlamaCppJniConfig jniConfig() {
        return LlamaCppJniConfig.builder("model.gguf").build();
    }

    @Test
    public void nullProviderNameDefaultsToMock() {
        assertThat(factory.create(null, jniConfig(), promptSupport), is(instanceOf(MockAiGenerationProvider.class)));
    }

    @Test
    public void blankProviderNameDefaultsToMock() {
        assertThat(factory.create("   ", jniConfig(), promptSupport), is(instanceOf(MockAiGenerationProvider.class)));
    }

    @Test
    public void mockProviderNameReturnsMock() {
        assertThat(factory.create("mock", jniConfig(), promptSupport), is(instanceOf(MockAiGenerationProvider.class)));
    }

    @Test
    public void llamacppJniProviderNameReturnsJniProvider() {
        // Lazy model loading means this constructs without touching the native lib / GGUF,
        // so the factory's jni branch is covered without a model on disk.
        assertThat(
                factory.create("llamacpp-jni", jniConfig(), promptSupport),
                is(instanceOf(LlamaCppJniAiGenerationProvider.class)));
    }

    @Test
    public void unknownProviderNameThrows() {
        assertThrows(IllegalArgumentException.class, () -> factory.create("nope", jniConfig(), promptSupport));
    }

    @Test
    public void openAiCompatibleProviderNameReturnsTheHttpProvider() {
        assertThat(
                factory.create(AiGenerationProviderFactory.PROVIDER_OPENAI_COMPATIBLE, jniConfig(), promptSupport),
                is(instanceOf(OpenAiCompatibleAiGenerationProvider.class)));
    }

    @Test
    public void theModelsOwnProviderOverridesTheRunsProvider() {
        final LlamaCppJniConfig remote = LlamaCppJniConfig.builder("")
                .provider(AiGenerationProviderFactory.PROVIDER_OPENAI_COMPATIBLE)
                .build();
        assertThat(
                factory.create(AiGenerationProviderFactory.PROVIDER_MOCK, remote, promptSupport),
                is(instanceOf(OpenAiCompatibleAiGenerationProvider.class)));
    }

    @Test
    public void anUnknownProviderOfTheModelIsNamedInTheError() {
        final LlamaCppJniConfig remote =
                LlamaCppJniConfig.builder("").provider("nope-model").build();
        final IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class,
                () -> factory.create(AiGenerationProviderFactory.PROVIDER_MOCK, remote, promptSupport));
        assertThat(thrown.getMessage(), is("Unsupported AI provider: nope-model"));
    }

    @Test
    public void effectiveProvider_aBlankModelProviderKeepsTheRunsProvider() {
        assertThat(AiGenerationProviderFactory.effectiveProvider("mock", null), is("mock"));
        assertThat(AiGenerationProviderFactory.effectiveProvider("mock", ""), is("mock"));
        assertThat(AiGenerationProviderFactory.effectiveProvider("mock", "   "), is("mock"));
    }

    @Test
    public void effectiveProvider_aModelProviderWinsAndIsTrimmed() {
        assertThat(
                AiGenerationProviderFactory.effectiveProvider("mock", " openai-compatible "), is("openai-compatible"));
    }
}
