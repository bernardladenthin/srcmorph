// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.provider;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import net.ladenthin.srcmorph.config.ModelDefinition;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Direct tests for {@link ProviderConfig}'s own constructor contract.
 *
 * <p>{@link ProviderConfigFactoryTest} exercises this class only through the factory, which never
 * passes a {@code null} list (the {@link ModelDefinition} setters normalise that away first). The
 * constructor's own null guards and its {@code modelPath} requirement are therefore only reachable
 * from here. That matters beyond coverage bookkeeping: the class carries
 * {@link net.ladenthin.srcmorph.support.ConvertToRecord} for a future Java&nbsp;17+ migration, and a
 * canonical-record rewrite is exactly where a normalising constructor body gets silently dropped.
 */
public class ProviderConfigTest {

    // <editor-fold defaultstate="collapsed" desc="fixture">

    /** Builds a config carrying the given lists; every other value stays at its default. */
    private static ProviderConfig configWithLists(
            final @Nullable List<String> drySequenceBreakers, final @Nullable List<String> stopStrings) {
        return ProviderConfig.builder()
                .modelPath("model.gguf")
                .drySequenceBreakers(drySequenceBreakers)
                .stopStrings(stopStrings)
                .build();
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="null-list normalisation">

    @Test
    public void constructor_nullDrySequenceBreakers_normalisedToEmptyList() {
        // act
        final ProviderConfig config = configWithLists(null, Arrays.asList("<end>"));

        // assert
        assertThat(config.drySequenceBreakers(), is(Collections.<String>emptyList()));
    }

    @Test
    public void constructor_nullStopStrings_normalisedToEmptyList() {
        // act
        final ProviderConfig config = configWithLists(Arrays.asList("\n"), null);

        // assert
        assertThat(config.stopStrings(), is(Collections.<String>emptyList()));
    }

    @Test
    public void constructor_nonNullLists_areKept() {
        // act
        final ProviderConfig config = configWithLists(Arrays.asList("\n", "."), Arrays.asList("<end>"));

        // assert
        assertThat(config.drySequenceBreakers(), is(Arrays.asList("\n", ".")));
        assertThat(config.stopStrings(), is(Arrays.asList("<end>")));
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="drySequenceBreakers">

    @Test
    public void drySequenceBreakers_listGiven_returnsUnmodifiableView() {
        // arrange
        final ProviderConfig config = configWithLists(Arrays.asList("\n"), Collections.<String>emptyList());
        final List<String> drySequenceBreakers = config.drySequenceBreakers();

        // act, assert
        Assertions.assertThrows(UnsupportedOperationException.class, () -> drySequenceBreakers.add("x"));
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="stopStrings">

    @Test
    public void stopStrings_listGiven_returnsUnmodifiableView() {
        // arrange
        final ProviderConfig config = configWithLists(Collections.<String>emptyList(), Arrays.asList("<end>"));
        final List<String> stopStrings = config.stopStrings();

        // act, assert
        Assertions.assertThrows(UnsupportedOperationException.class, () -> stopStrings.add("x"));
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="builder">

    /**
     * The builder's whole point: a value the caller never named is the matching
     * {@code ModelDefinition.DEFAULT_*}, not Java's zero: a knob must never arrive as
     * {@code 0}/{@code false}/{@code null} instead of what {@code ModelDefinition} says it should be.
     */
    @Test
    public void builder_onlyModelPathSet_leavesEveryUnsetValueAtItsDefinitionDefault() {
        // act -- name nothing but the one required value
        final ProviderConfig config =
                ProviderConfig.builder().modelPath("model.gguf").build();

        // assert
        assertThat(config.modelPath(), is("model.gguf"));
        assertThat(config.contextSize(), is(ModelDefinition.DEFAULT_CONTEXT_SIZE));
        assertThat(config.maxOutputTokens(), is(ModelDefinition.DEFAULT_MAX_OUTPUT_TOKENS));
        assertThat(config.temperature(), is(ModelDefinition.DEFAULT_TEMPERATURE));
        assertThat(config.threads(), is(ModelDefinition.DEFAULT_THREADS));
        assertThat(config.topP(), is(ModelDefinition.DEFAULT_TOP_P));
        assertThat(config.topK(), is(ModelDefinition.DEFAULT_TOP_K));
        assertThat(config.minP(), is(ModelDefinition.DEFAULT_MIN_P));
        assertThat(config.topNSigma(), is(ModelDefinition.DEFAULT_TOP_N_SIGMA));
        assertThat(config.repeatPenalty(), is(ModelDefinition.DEFAULT_REPEAT_PENALTY));
        assertThat(config.chatTemplateEnableThinking(), is(ModelDefinition.DEFAULT_CHAT_TEMPLATE_ENABLE_THINKING));
        assertThat(config.cachePrompt(), is(ModelDefinition.DEFAULT_CACHE_PROMPT));
        assertThat(config.swaFull(), is(ModelDefinition.DEFAULT_SWA_FULL));
        assertThat(config.cacheReuse(), is(ModelDefinition.DEFAULT_CACHE_REUSE));
        assertThat(config.gpuLayers(), is(ModelDefinition.DEFAULT_GPU_LAYERS));
        assertThat(config.seed(), is(ModelDefinition.DEFAULT_SEED));
        assertThat(config.cpuMoeLayers(), is(ModelDefinition.DEFAULT_CPU_MOE_LAYERS));
        assertThat(config.cpuFfnLayers(), is(ModelDefinition.DEFAULT_CPU_FFN_LAYERS));
        assertThat(config.kvUnifiedPerSlot(), is(ModelDefinition.DEFAULT_KV_UNIFIED_PER_SLOT));
        assertThat(config.lazyMode(), is(ModelDefinition.DEFAULT_LAZY_MODE));
        assertThat(config.repeatLastN(), is(ModelDefinition.DEFAULT_REPEAT_LAST_N));
        assertThat(config.cacheTypeK(), is(ModelDefinition.DEFAULT_CACHE_TYPE_K));
        assertThat(config.cacheTypeV(), is(ModelDefinition.DEFAULT_CACHE_TYPE_V));
        assertThat(config.flashAttn(), is(ModelDefinition.DEFAULT_FLASH_ATTN));
        assertThat(config.batchSize(), is(ModelDefinition.DEFAULT_BATCH_SIZE));
        assertThat(config.ubatchSize(), is(ModelDefinition.DEFAULT_UBATCH_SIZE));
        assertThat(config.threadsBatch(), is(ModelDefinition.DEFAULT_THREADS_BATCH));
        assertThat(config.mainGpu(), is(ModelDefinition.DEFAULT_MAIN_GPU));
        assertThat(config.devices(), is(ModelDefinition.DEFAULT_DEVICES));
        assertThat(config.provider(), is(ModelDefinition.DEFAULT_PROVIDER));
        assertThat(config.remoteModel(), is(ModelDefinition.DEFAULT_REMOTE_MODEL));
        assertThat(config.baseUrl(), is(ModelDefinition.DEFAULT_BASE_URL));
        assertThat(config.apiKeyEnv(), is(ModelDefinition.DEFAULT_API_KEY_ENV));
        assertThat(config.stream(), is(ModelDefinition.DEFAULT_STREAM));
        assertThat(config.reasoningEffort(), is(ModelDefinition.DEFAULT_REASONING_EFFORT));
        assertThat(config.reasoningBudgetTokens(), is(ModelDefinition.DEFAULT_REASONING_BUDGET_TOKENS));
        assertThat(config.dryMultiplier(), is(ModelDefinition.DEFAULT_DRY_MULTIPLIER));
        assertThat(config.dryBase(), is(ModelDefinition.DEFAULT_DRY_BASE));
        assertThat(config.dryAllowedLength(), is(ModelDefinition.DEFAULT_DRY_ALLOWED_LENGTH));
        assertThat(config.dryPenaltyLastN(), is(ModelDefinition.DEFAULT_DRY_PENALTY_LAST_N));
        assertThat(config.drySequenceBreakers(), is(Collections.<String>emptyList()));
        assertThat(config.stopStrings(), is(Collections.<String>emptyList()));
    }

    /**
     * Every setter must write its own field and no other. With thirty-six of them, several sharing a
     * type and sitting next to each other, a copy-paste slip is the realistic failure -- so each value
     * here is distinct within its type, and the two booleans that used to be adjacent in the old
     * positional constructor ({@code swaFull}, {@code flashAttn}) are given opposite values.
     */
    @Test
    public void builder_everySetterCalled_writesItsOwnField() {
        // act
        final ProviderConfig config = ProviderConfig.builder()
                .modelPath("model.gguf")
                .contextSize(1111)
                .maxOutputTokens(222)
                .temperature(0.33f)
                .threads(7)
                .topP(0.55f)
                .topK(66)
                .minP(0.07f)
                .topNSigma(0.88f)
                .repeatPenalty(1.09f)
                .chatTemplateEnableThinking(false)
                .cachePrompt(false)
                .swaFull(false)
                .cacheReuse(101)
                .gpuLayers(12)
                .seed(12345)
                .cpuMoeLayers(24)
                .cpuFfnLayers(16)
                .kvUnifiedPerSlot(4096)
                .lazyMode("on")
                .repeatLastN(128)
                .cacheTypeK("q8_0")
                .cacheTypeV("q4_0")
                .flashAttn(true)
                .batchSize(512)
                .ubatchSize(256)
                .threadsBatch(6)
                .mainGpu(3)
                .devices("Vulkan1")
                .provider("openai-compatible")
                .remoteModel("openai/gpt-6-luna")
                .baseUrl("http://127.0.0.1:8080/v1")
                .apiKeyEnv("MY_KEY")
                .stream(true)
                .reasoningEffort("high")
                .reasoningBudgetTokens(512)
                .dryMultiplier(0.5f)
                .dryBase(1.3f)
                .dryAllowedLength(7)
                .dryPenaltyLastN(999)
                .drySequenceBreakers(Arrays.asList("\n", "."))
                .stopStrings(Arrays.asList("<end>"))
                .build();

        // assert
        assertThat(config.modelPath(), is("model.gguf"));
        assertThat(config.contextSize(), is(1111));
        assertThat(config.maxOutputTokens(), is(222));
        assertThat(config.temperature(), is(0.33f));
        assertThat(config.threads(), is(7));
        assertThat(config.topP(), is(0.55f));
        assertThat(config.topK(), is(66));
        assertThat(config.minP(), is(0.07f));
        assertThat(config.topNSigma(), is(0.88f));
        assertThat(config.repeatPenalty(), is(1.09f));
        assertThat(config.chatTemplateEnableThinking(), is(false));
        assertThat(config.cachePrompt(), is(false));
        assertThat(config.swaFull(), is(false));
        assertThat(config.cacheReuse(), is(101));
        assertThat(config.gpuLayers(), is(12));
        assertThat(config.seed(), is(12345));
        assertThat(config.cpuMoeLayers(), is(24));
        assertThat(config.cpuFfnLayers(), is(16));
        assertThat(config.kvUnifiedPerSlot(), is(4096));
        assertThat(config.lazyMode(), is("on"));
        assertThat(config.repeatLastN(), is(128));
        assertThat(config.cacheTypeK(), is("q8_0"));
        assertThat(config.cacheTypeV(), is("q4_0"));
        assertThat(config.flashAttn(), is(true));
        assertThat(config.batchSize(), is(512));
        assertThat(config.ubatchSize(), is(256));
        assertThat(config.threadsBatch(), is(6));
        assertThat(config.mainGpu(), is(3));
        assertThat(config.devices(), is("Vulkan1"));
        assertThat(config.provider(), is("openai-compatible"));
        assertThat(config.remoteModel(), is("openai/gpt-6-luna"));
        assertThat(config.baseUrl(), is("http://127.0.0.1:8080/v1"));
        assertThat(config.apiKeyEnv(), is("MY_KEY"));
        assertThat(config.stream(), is(true));
        assertThat(config.reasoningEffort(), is("high"));
        assertThat(config.reasoningBudgetTokens(), is(512));
        assertThat(config.dryMultiplier(), is(0.5f));
        assertThat(config.dryBase(), is(1.3f));
        assertThat(config.dryAllowedLength(), is(7));
        assertThat(config.dryPenaltyLastN(), is(999));
        assertThat(config.drySequenceBreakers(), is(Arrays.asList("\n", ".")));
        assertThat(config.stopStrings(), is(Arrays.asList("<end>")));
    }

    /**
     * {@code null} on a String setter restores that knob's default rather than storing {@code null}, so
     * an accessor can never hand back {@code null}. Note the default is not always empty:
     * {@code baseUrl} defaults to OpenRouter, which is what distinguishes "restores the
     * default" from "clears to empty".
     */
    @Test
    public void builder_nullOnAStringSetter_restoresThatKnobsDefault() {
        // arrange -- set them all first, so the null is genuinely undoing something
        final ProviderConfig config = ProviderConfig.builder()
                .modelPath("model.gguf")
                .lazyMode("on")
                .cacheTypeK("q8_0")
                .cacheTypeV("q4_0")
                .devices("Vulkan1")
                .provider("openai-compatible")
                .remoteModel("openai/gpt-6-luna")
                .baseUrl("http://127.0.0.1:8080/v1")
                .apiKeyEnv("MY_KEY")
                .reasoningEffort("high")
                .lazyMode(null)
                .cacheTypeK(null)
                .cacheTypeV(null)
                .devices(null)
                .provider(null)
                .remoteModel(null)
                .baseUrl(null)
                .apiKeyEnv(null)
                .reasoningEffort(null)
                .build();

        // assert
        assertThat(config.lazyMode(), is(""));
        assertThat(config.cacheTypeK(), is(""));
        assertThat(config.cacheTypeV(), is(""));
        assertThat(config.devices(), is(""));
        assertThat(config.provider(), is(ModelDefinition.DEFAULT_PROVIDER));
        assertThat(config.remoteModel(), is(ModelDefinition.DEFAULT_REMOTE_MODEL));
        assertThat(config.baseUrl(), is(ModelDefinition.DEFAULT_BASE_URL));
        assertThat(config.apiKeyEnv(), is(ModelDefinition.DEFAULT_API_KEY_ENV));
        assertThat(config.reasoningEffort(), is(ModelDefinition.DEFAULT_REASONING_EFFORT));
    }

    /** A remote model names no file, so a configuration without one has none rather than {@code ""}. */
    @Test
    public void builder_withoutModelPath_hasNone() {
        // act
        final ProviderConfig config = ProviderConfig.builder().build();

        // assert
        assertThat(config.modelPath(), is(nullValue()));
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="build">

    /** A builder is reusable: {@code build()} snapshots it, so a later setter cannot reach back. */
    @Test
    public void build_snapshotsTheBuilder_soLaterSettersDoNotMutateAnAlreadyBuiltConfig() {
        // arrange
        final ProviderConfig.Builder builder =
                ProviderConfig.builder().modelPath("model.gguf").contextSize(1111);
        final ProviderConfig first = builder.build();

        // act
        final ProviderConfig second = builder.contextSize(2222).build();

        // assert
        assertThat(first.contextSize(), is(1111));
        assertThat(second.contextSize(), is(2222));
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="toString">
    @Test
    public void toString_baseUrlWithUserInfo_masksIt() {
        // arrange
        final ProviderConfig config = ProviderConfig.builder()
                .modelPath("m.gguf")
                .baseUrl("https://user:secret@host/v1")
                .build();

        // act
        final String shown = config.toString();

        // assert
        assertThat(shown.contains("secret"), is(false));
        assertThat(shown.contains("baseUrl=https://***@host/v1"), is(true));
    }
    // </editor-fold>
}
