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

    // <editor-fold defaultstate="collapsed" desc="create">
    @Test
    public void create_blankProviderName_returnsMockProvider() {
        // act, assert
        assertThat(factory.create("   ", jniConfig(), promptSupport), is(instanceOf(MockGenerationProvider.class)));
    }

    @Test
    public void create_mockProviderName_returnsMockProvider() {
        // act, assert
        assertThat(factory.create("mock", jniConfig(), promptSupport), is(instanceOf(MockGenerationProvider.class)));
    }

    @Test
    public void create_llamacppJniProviderName_returnsJniProvider() {
        // Lazy model loading means this constructs without touching the native lib / GGUF,
        // so the factory's jni branch is covered without a model on disk.
        // act, assert
        assertThat(
                factory.create("llamacpp-jni", jniConfig(), promptSupport),
                is(instanceOf(LlamaCppJniGenerationProvider.class)));
    }

    @Test
    public void create_unknownProviderName_throwsException() {
        // arrange
        final ProviderConfig config = jniConfig();

        // act, assert
        assertThrows(IllegalArgumentException.class, () -> factory.create("nope", config, promptSupport));
    }

    @Test
    public void create_openAiCompatibleProviderName_returnsHttpProvider() {
        // act, assert
        assertThat(
                factory.create(GenerationProviderFactory.PROVIDER_OPENAI_COMPATIBLE, jniConfig(), promptSupport),
                is(instanceOf(OpenAiCompatibleGenerationProvider.class)));
    }

    @Test
    public void create_modelHasOwnProvider_overridesRunsProvider() {
        // arrange
        final ProviderConfig remote = ProviderConfig.builder()
                .modelPath("")
                .provider(GenerationProviderFactory.PROVIDER_OPENAI_COMPATIBLE)
                .build();

        // act, assert
        assertThat(
                factory.create(GenerationProviderFactory.PROVIDER_MOCK, remote, promptSupport),
                is(instanceOf(OpenAiCompatibleGenerationProvider.class)));
    }

    @Test
    public void create_unknownModelProvider_throwsExceptionNamingIt() {
        // arrange
        final ProviderConfig remote =
                ProviderConfig.builder().modelPath("").provider("nope-model").build();

        // act
        final IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class,
                () -> factory.create(GenerationProviderFactory.PROVIDER_MOCK, remote, promptSupport));

        // assert
        assertThat(thrown.getMessage(), is("Unsupported AI provider: nope-model"));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="effectiveProvider">
    @Test
    public void effectiveProvider_blankModelProvider_returnsRunsProvider() {
        // act, assert
        assertThat(GenerationProviderFactory.effectiveProvider("mock", null), is("mock"));
        assertThat(GenerationProviderFactory.effectiveProvider("mock", ""), is("mock"));
        assertThat(GenerationProviderFactory.effectiveProvider("mock", "   "), is("mock"));
    }

    @Test
    public void effectiveProvider_modelProviderGiven_returnsTrimmedModelProvider() {
        // act, assert
        assertThat(GenerationProviderFactory.effectiveProvider("mock", " openai-compatible "), is("openai-compatible"));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="countsPromptTokens">
    /** Only the local llama.cpp provider counts a prompt before the call; a remote API answers it after. */
    @Test
    public void countsPromptTokens_eachProvider_onlyLlamaCppJniCounts() {
        // act, assert
        assertThat(
                GenerationProviderFactory.countsPromptTokens(GenerationProviderFactory.PROVIDER_LLAMACPP_JNI),
                is(true));
        assertThat(
                GenerationProviderFactory.countsPromptTokens(GenerationProviderFactory.PROVIDER_OPENAI_COMPATIBLE),
                is(false));
        assertThat(GenerationProviderFactory.countsPromptTokens(GenerationProviderFactory.PROVIDER_MOCK), is(false));
        assertThat(GenerationProviderFactory.countsPromptTokens(""), is(false));
    }
    // </editor-fold>
}
