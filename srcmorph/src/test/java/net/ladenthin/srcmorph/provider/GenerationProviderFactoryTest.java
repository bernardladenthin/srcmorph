// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.provider;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import net.ladenthin.srcmorph.prompt.PromptCatalog;
import org.junit.jupiter.api.Test;

public class GenerationProviderFactoryTest {

    private final GenerationProviderFactory factory = new GenerationProviderFactory();
    private final PromptCatalog promptSupport = new PromptCatalog(null);

    private static ProviderConfig jniConfig() {
        return ProviderConfig.builder().modelPath("model.gguf").build();
    }

    @Test
    public void blankProviderNameDefaultsToMock() {
        assertThat(factory.create("   ", jniConfig(), promptSupport), is(instanceOf(MockGenerationProvider.class)));
    }

    @Test
    public void mockProviderNameReturnsMock() {
        assertThat(factory.create("mock", jniConfig(), promptSupport), is(instanceOf(MockGenerationProvider.class)));
    }

    @Test
    public void llamacppJniProviderNameReturnsJniProvider() {
        // Lazy model loading means this constructs without touching the native lib / GGUF,
        // so the factory's jni branch is covered without a model on disk.
        assertThat(
                factory.create("llamacpp-jni", jniConfig(), promptSupport),
                is(instanceOf(LlamaCppJniGenerationProvider.class)));
    }

    @Test
    public void unknownProviderNameThrows() {
        assertThrows(IllegalArgumentException.class, () -> factory.create("nope", jniConfig(), promptSupport));
    }

    @Test
    public void openAiCompatibleProviderNameReturnsTheHttpProvider() {
        assertThat(
                factory.create(GenerationProviderFactory.PROVIDER_OPENAI_COMPATIBLE, jniConfig(), promptSupport),
                is(instanceOf(OpenAiCompatibleGenerationProvider.class)));
    }

    @Test
    public void theModelsOwnProviderOverridesTheRunsProvider() {
        final ProviderConfig remote = ProviderConfig.builder()
                .modelPath("")
                .provider(GenerationProviderFactory.PROVIDER_OPENAI_COMPATIBLE)
                .build();
        assertThat(
                factory.create(GenerationProviderFactory.PROVIDER_MOCK, remote, promptSupport),
                is(instanceOf(OpenAiCompatibleGenerationProvider.class)));
    }

    @Test
    public void anUnknownProviderOfTheModelIsNamedInTheError() {
        final ProviderConfig remote =
                ProviderConfig.builder().modelPath("").provider("nope-model").build();
        final IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class,
                () -> factory.create(GenerationProviderFactory.PROVIDER_MOCK, remote, promptSupport));
        assertThat(thrown.getMessage(), is("Unsupported AI provider: nope-model"));
    }

    @Test
    public void effectiveProvider_aBlankModelProviderKeepsTheRunsProvider() {
        assertThat(GenerationProviderFactory.effectiveProvider("mock", null), is("mock"));
        assertThat(GenerationProviderFactory.effectiveProvider("mock", ""), is("mock"));
        assertThat(GenerationProviderFactory.effectiveProvider("mock", "   "), is("mock"));
    }

    @Test
    public void effectiveProvider_aModelProviderWinsAndIsTrimmed() {
        assertThat(GenerationProviderFactory.effectiveProvider("mock", " openai-compatible "), is("openai-compatible"));
    }
}
