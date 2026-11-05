// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.config;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;

public class ModelDefinitionTest {

    // <editor-fold defaultstate="collapsed" desc="requireKey">
    @Test
    public void requireKey_set_isTheKey() {
        // arrange
        final ModelDefinition definition = new ModelDefinition();
        definition.setKey("gpt-oss");

        // act, assert
        assertThat(definition.requireKey(), is("gpt-oss"));
    }

    @Test
    public void requireKey_unset_isRefused() {
        // arrange
        final ModelDefinition definition = new ModelDefinition();

        // act
        final IllegalStateException thrown = assertThrows(IllegalStateException.class, definition::requireKey);

        // assert
        assertThat(thrown.getMessage(), is("The AI definition has no key: " + definition));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="samplingScalars">
    @Test
    public void samplingScalars_set_gettersReflectSetters() {
        // arrange
        ModelDefinition d = new ModelDefinition();

        // act
        d.setTopP(0.66f);
        d.setTopK(33);
        d.setRepeatPenalty(1.4f);

        // assert
        // Distinct non-zero values kill the "return 0" primitive mutants on these getters.
        assertThat(d.getTopP(), is(0.66f));
        assertThat(d.getTopK(), is(33));
        assertThat(d.getRepeatPenalty(), is(1.4f));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="stopStrings">
    @Test
    public void stopStrings_newDefinition_isNullByDefault() {
        // act, assert
        assertThat(new ModelDefinition().getStopStrings(), is(nullValue()));
    }

    @Test
    public void stopStrings_set_roundTrips() {
        // arrange
        ModelDefinition d = new ModelDefinition();

        // act
        d.setStopStrings(Arrays.asList("</s>", "END"));

        // assert
        // Asserting content kills both the empty-return mutant on the getter and the
        // negate mutant on the setter null-guard (which would null the field for a non-null arg).
        assertThat(d.getStopStrings(), hasItem("</s>"));
        assertThat(d.getStopStrings(), hasItem("END"));
    }

    @Test
    public void stopStrings_setNull_clearsToNull() {
        // arrange
        ModelDefinition d = new ModelDefinition();
        d.setStopStrings(Arrays.asList("a"));

        // act
        d.setStopStrings(null);

        // assert
        assertThat(d.getStopStrings(), is(nullValue()));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="calibration">
    @Test
    public void calibration_defaultAndSet_isNullByDefaultAndRoundTrips() {
        // arrange
        final ModelDefinition d = new ModelDefinition();

        // pre-assert
        assertThat(d.getCalibration(), is(nullValue()));

        // act
        final Calibration calibration = new Calibration();
        calibration.setPrefillTokensPerSecond(1000.0d);
        d.setCalibration(calibration);

        // assert
        // Asserting the same instance kills the empty-return mutant on the getter and the
        // assignment-removal mutant on the setter.
        assertThat(d.getCalibration(), is(sameInstance(calibration)));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="cachePrompt">
    @Test
    public void cachePrompt_defaultAndSet_defaultsTrueAndTogglesFalse() {
        // arrange
        ModelDefinition d = new ModelDefinition();

        // pre-assert
        // Default true kills the false-default / "return false" getter mutants.
        assertThat(d.isCachePrompt(), is(true));

        // act
        d.setCachePrompt(false);

        // assert
        // Observing false after the setter kills the "return true" getter mutant and the
        // removed-assignment setter mutant.
        assertThat(d.isCachePrompt(), is(false));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="reasoningEffort">
    @Test
    public void reasoningEffort_defaultAndSet_defaultsLowAndRoundTrips() {
        // arrange
        ModelDefinition d = new ModelDefinition();

        // pre-assert
        // Default "low" kills the empty/null return mutants on the getter.
        assertThat(d.getReasoningEffort(), is("low"));

        // act
        d.setReasoningEffort("medium");

        // assert
        // Round-tripped value kills the removed-assignment setter mutant.
        assertThat(d.getReasoningEffort(), is("medium"));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="minP">
    @Test
    public void minP_defaultAndSet_defaultsDisabledAndRoundTrips() {
        // arrange
        ModelDefinition d = new ModelDefinition();

        // pre-assert
        // Default 0.0 (disabled) kills the inline-constant / "return 1.0" getter mutants.
        assertThat(d.getMinP(), is(0.0f));

        // act
        d.setMinP(0.05f);

        // assert
        // Round-tripped non-zero value kills the "return 0" getter and removed-assignment setter mutants.
        assertThat(d.getMinP(), is(0.05f));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="topNSigma">
    @Test
    public void topNSigma_defaultAndSet_defaultsDisabledAndRoundTrips() {
        // arrange
        ModelDefinition d = new ModelDefinition();

        // pre-assert
        // Default -1.0 (disabled) kills the inline-constant / "return 0" getter mutants.
        assertThat(d.getTopNSigma(), is(-1.0f));

        // act
        d.setTopNSigma(1.5f);

        // assert
        // Round-tripped value kills the getter "return 0" and removed-assignment setter mutants.
        assertThat(d.getTopNSigma(), is(1.5f));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="swaFull">
    @Test
    public void swaFull_defaultAndSet_defaultsTrueAndTogglesFalse() {
        // arrange
        ModelDefinition d = new ModelDefinition();

        // pre-assert
        assertThat(d.isSwaFull(), is(true));

        // act
        d.setSwaFull(false);

        // assert
        assertThat(d.isSwaFull(), is(false));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="cacheReuse">
    @Test
    public void cacheReuse_defaultAndSet_defaultsTwoFiftySixAndRoundTrips() {
        // arrange
        ModelDefinition d = new ModelDefinition();

        // pre-assert
        assertThat(d.getCacheReuse(), is(256));

        // act
        d.setCacheReuse(128);

        // assert
        assertThat(d.getCacheReuse(), is(128));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="gpuLayers">
    @Test
    public void gpuLayers_defaultAndSet_defaultsMinusOneAndRoundTrips() {
        // arrange
        ModelDefinition d = new ModelDefinition();

        // pre-assert
        assertThat(d.getGpuLayers(), is(-1));

        // act
        d.setGpuLayers(33);

        // assert
        assertThat(d.getGpuLayers(), is(33));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="cpuMoeLayers">
    @Test
    public void cpuMoeLayers_defaultAndSet_defaultsMinusOneAndRoundTrips() {
        // arrange
        ModelDefinition c = new ModelDefinition();

        // pre-assert
        // Default -1 (leave binding/build default) kills the inline-constant / "return 0" getter mutants.
        assertThat(c.getCpuMoeLayers(), is(-1));

        // act
        c.setCpuMoeLayers(24);

        // assert
        // Round-tripped value kills the "return 0" getter and removed-assignment setter mutants.
        assertThat(c.getCpuMoeLayers(), is(24));
    }

    @Test
    public void cpuMoeLayers_zero_isAccepted() {
        // arrange
        ModelDefinition c = new ModelDefinition();

        // act
        c.setCpuMoeLayers(0);

        // assert
        // 0 is a meaningful value ("keep no expert layer on the CPU"), not "unset" — it must survive
        // the setter unchanged, which is what makes the provider's >= 0 guard forward it.
        assertThat(c.getCpuMoeLayers(), is(0));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="cpuFfnLayers">
    @Test
    public void cpuFfnLayers_defaultAndSet_defaultsMinusOneAndRoundTrips() {
        // arrange
        ModelDefinition c = new ModelDefinition();

        // pre-assert
        // Default -1 (leave binding/build default) kills the inline-constant / "return 0" getter mutants.
        assertThat(c.getCpuFfnLayers(), is(-1));

        // act
        c.setCpuFfnLayers(16);

        // assert
        // Round-tripped value kills the "return 0" getter and removed-assignment setter mutants.
        assertThat(c.getCpuFfnLayers(), is(16));
    }

    @Test
    public void cpuFfnLayers_zero_isAccepted() {
        // arrange
        ModelDefinition c = new ModelDefinition();

        // act
        c.setCpuFfnLayers(0);

        // assert
        // Same "0 is meaningful, not unset" contract as cpuMoeLayers.
        assertThat(c.getCpuFfnLayers(), is(0));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="kvUnifiedPerSlot">
    @Test
    public void kvUnifiedPerSlot_defaultAndSet_defaultsMinusOneAndRoundTrips() {
        // arrange
        ModelDefinition c = new ModelDefinition();

        // pre-assert
        // Default -1 (leave binding/build default) kills the inline-constant / "return 0" getter mutants.
        assertThat(c.getKvUnifiedPerSlot(), is(-1));

        // act
        c.setKvUnifiedPerSlot(4096);

        // assert
        // Round-tripped value kills the "return 0" getter and removed-assignment setter mutants.
        assertThat(c.getKvUnifiedPerSlot(), is(4096));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="lazyMode">
    @Test
    public void lazyMode_defaultAndSet_defaultsEmptyAndRoundTrips() {
        // arrange
        ModelDefinition c = new ModelDefinition();

        // pre-assert
        // Default "" (leave binding/build default) kills the null/non-empty return mutants on the getter.
        assertThat(c.getLazyMode(), is(""));

        // act
        c.setLazyMode("on");

        // assert
        // Round-tripped value kills the empty-return getter and removed-assignment setter mutants.
        assertThat(c.getLazyMode(), is("on"));
    }

    @Test
    public void lazyMode_setNull_resetsToEmpty() {
        // arrange
        ModelDefinition c = new ModelDefinition();
        c.setLazyMode("auto");

        // act
        c.setLazyMode(null);

        // assert
        // null arg resets to "" — kills the negate mutant on the setter ternary (which would store null).
        assertThat(c.getLazyMode(), is(""));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="mainGpu">
    @Test
    public void mainGpu_defaultAndSet_defaultsMinusOneAndRoundTrips() {
        // arrange
        ModelDefinition d = new ModelDefinition();

        // pre-assert
        assertThat(d.getMainGpu(), is(-1));

        // act
        d.setMainGpu(1);

        // assert
        assertThat(d.getMainGpu(), is(1));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="devices">
    @Test
    public void devices_defaultAndSet_defaultsEmptyAndRoundTrips() {
        // arrange
        ModelDefinition d = new ModelDefinition();

        // pre-assert
        assertThat(d.getDevices(), is(""));

        // act
        d.setDevices("Vulkan1");

        // assert
        assertThat(d.getDevices(), is("Vulkan1"));
    }

    @Test
    public void devices_setNull_resetsToEmpty() {
        // arrange
        ModelDefinition d = new ModelDefinition();
        d.setDevices("CUDA0");

        // act
        d.setDevices(null);

        // assert
        assertThat(d.getDevices(), is(""));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="provider">
    @Test
    public void provider_defaultAndSet_defaultsAndRoundTrips() {
        // arrange
        ModelDefinition c = new ModelDefinition();

        // pre-assert
        assertThat(c.getProvider(), is(""));

        // act
        c.setProvider("openai-compatible");

        // assert
        assertThat(c.getProvider(), is("openai-compatible"));
    }

    @Test
    public void provider_setNull_resetsToDefault() {
        // arrange
        ModelDefinition c = new ModelDefinition();
        c.setProvider("openai-compatible");

        // act
        c.setProvider(null);

        // assert
        assertThat(c.getProvider(), is(""));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="remoteModel">
    @Test
    public void remoteModel_defaultAndSet_defaultsAndRoundTrips() {
        // arrange
        ModelDefinition c = new ModelDefinition();

        // pre-assert
        assertThat(c.getRemoteModel(), is(""));

        // act
        c.setRemoteModel("openai/gpt-6-luna");

        // assert
        assertThat(c.getRemoteModel(), is("openai/gpt-6-luna"));
    }

    @Test
    public void remoteModel_setNull_resetsToDefault() {
        // arrange
        ModelDefinition c = new ModelDefinition();
        c.setRemoteModel("openai/gpt-6-luna");

        // act
        c.setRemoteModel(null);

        // assert
        assertThat(c.getRemoteModel(), is(""));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="baseUrl">
    @Test
    public void baseUrl_defaultAndSet_defaultsAndRoundTrips() {
        // arrange
        ModelDefinition c = new ModelDefinition();

        // pre-assert
        assertThat(c.getBaseUrl(), is("https://openrouter.ai/api/v1"));

        // act
        c.setBaseUrl("http://127.0.0.1:8080/v1");

        // assert
        assertThat(c.getBaseUrl(), is("http://127.0.0.1:8080/v1"));
    }

    @Test
    public void baseUrl_setNull_resetsToDefault() {
        // arrange
        ModelDefinition c = new ModelDefinition();
        c.setBaseUrl("http://127.0.0.1:8080/v1");

        // act
        c.setBaseUrl(null);

        // assert
        assertThat(c.getBaseUrl(), is("https://openrouter.ai/api/v1"));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="apiKeyEnv">
    @Test
    public void apiKeyEnv_defaultAndSet_defaultsAndRoundTrips() {
        // arrange
        ModelDefinition c = new ModelDefinition();

        // pre-assert
        assertThat(c.getApiKeyEnv(), is(""));

        // act
        c.setApiKeyEnv("MY_KEY");

        // assert
        assertThat(c.getApiKeyEnv(), is("MY_KEY"));
    }

    @Test
    public void apiKeyEnv_setNull_resetsToDefault() {
        // arrange
        ModelDefinition c = new ModelDefinition();
        c.setApiKeyEnv("MY_KEY");

        // act
        c.setApiKeyEnv(null);

        // assert
        assertThat(c.getApiKeyEnv(), is(""));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="stream">
    @Test
    public void stream_defaultAndSet_defaultsOffAndRoundTrips() {
        // arrange
        ModelDefinition c = new ModelDefinition();

        // pre-assert
        assertThat(c.isStream(), is(false));

        // act
        c.setStream(true);

        // assert
        assertThat(c.isStream(), is(true));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="maxRetries">
    @Test
    public void maxRetries_defaultAndSet_defaultsAndRoundTrips() {
        // arrange
        final ModelDefinition c = new ModelDefinition();

        // pre-assert
        assertThat(c.getMaxRetries(), is(ModelDefinition.DEFAULT_MAX_RETRIES));

        // act
        c.setMaxRetries(7);

        // assert
        assertThat(c.getMaxRetries(), is(7));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="requestsPerMinute">
    @Test
    public void requestsPerMinute_defaultAndSet_defaultsToNoLimitAndRoundTrips() {
        // arrange
        ModelDefinition c = new ModelDefinition();

        // pre-assert
        assertThat(c.getRequestsPerMinute(), is(ModelDefinition.DEFAULT_REQUESTS_PER_MINUTE));

        // act
        c.setRequestsPerMinute(25);

        // assert
        assertThat(c.getRequestsPerMinute(), is(25));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="reasoningBudgetTokens">
    @Test
    public void reasoningBudgetTokens_defaultAndSet_defaultsUnrestrictedAndRoundTrips() {
        // arrange
        ModelDefinition d = new ModelDefinition();

        // pre-assert
        assertThat(d.getReasoningBudgetTokens(), is(-1));

        // act
        d.setReasoningBudgetTokens(2048);

        // assert
        assertThat(d.getReasoningBudgetTokens(), is(2048));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="dryMultiplier">
    @Test
    public void dryMultiplier_defaultAndSet_defaultsDisabledAndRoundTrips() {
        // arrange
        ModelDefinition d = new ModelDefinition();

        // pre-assert
        assertThat(d.getDryMultiplier(), is(0.0f));

        // act
        d.setDryMultiplier(0.8f);

        // assert
        assertThat(d.getDryMultiplier(), is(0.8f));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="dryBase">
    @Test
    public void dryBase_defaultAndSet_defaultsAndRoundTrips() {
        // arrange
        ModelDefinition d = new ModelDefinition();

        // pre-assert
        assertThat(d.getDryBase(), is(1.75f));

        // act
        d.setDryBase(1.5f);

        // assert
        assertThat(d.getDryBase(), is(1.5f));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="dryAllowedLength">
    @Test
    public void dryAllowedLength_defaultAndSet_defaultsAndRoundTrips() {
        // arrange
        ModelDefinition d = new ModelDefinition();

        // pre-assert
        assertThat(d.getDryAllowedLength(), is(2));

        // act
        d.setDryAllowedLength(5);

        // assert
        assertThat(d.getDryAllowedLength(), is(5));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="dryPenaltyLastN">
    @Test
    public void dryPenaltyLastN_defaultAndSet_defaultsWholeContextAndRoundTrips() {
        // arrange
        ModelDefinition d = new ModelDefinition();

        // pre-assert
        assertThat(d.getDryPenaltyLastN(), is(-1));

        // act
        d.setDryPenaltyLastN(256);

        // assert
        assertThat(d.getDryPenaltyLastN(), is(256));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="drySequenceBreakers">
    @Test
    public void drySequenceBreakers_newDefinition_isNullByDefault() {
        // act, assert
        assertThat(new ModelDefinition().getDrySequenceBreakers(), is(nullValue()));
    }

    @Test
    public void drySequenceBreakers_set_roundTrips() {
        // arrange
        ModelDefinition d = new ModelDefinition();

        // act
        d.setDrySequenceBreakers(Arrays.asList("\n", ":"));

        // assert
        assertThat(d.getDrySequenceBreakers(), hasItem("\n"));
        assertThat(d.getDrySequenceBreakers(), hasItem(":"));
    }

    @Test
    public void drySequenceBreakers_setNull_clearsToNull() {
        // arrange
        ModelDefinition d = new ModelDefinition();
        d.setDrySequenceBreakers(Arrays.asList("a"));

        // act
        d.setDrySequenceBreakers(null);

        // assert
        assertThat(d.getDrySequenceBreakers(), is(nullValue()));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="seed">
    @Test
    public void seed_defaultAndSet_defaultsMinusOneAndRoundTrips() {
        // arrange
        ModelDefinition c = new ModelDefinition();

        // pre-assert
        // Default -1 (upstream draws a random seed per request) kills the inline-constant / "return 0"
        // getter mutants.
        assertThat(c.getSeed(), is(-1));

        // act
        c.setSeed(12345);

        // assert
        // Round-tripped value kills the "return 0" getter and removed-assignment setter mutants.
        assertThat(c.getSeed(), is(12345));
    }

    @Test
    public void seed_zero_isAccepted() {
        // arrange
        ModelDefinition c = new ModelDefinition();

        // act
        c.setSeed(0);

        // assert
        // 0 is a legitimate seed, not "unset" -- it must survive the setter so the provider's >= 0
        // guard forwards it.
        assertThat(c.getSeed(), is(0));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="repeatLastN">
    @Test
    public void repeatLastN_defaultAndSet_defaultsToUnsetAndRoundTrips() {
        // arrange
        final ModelDefinition c = new ModelDefinition();

        // pre-assert
        // -1 leaves llama.cpp's own window; 0 is a meaningful value (disables the penalty), which is why
        // the forwarding guard is >= 0 -- pinned here so the default cannot drift onto the wrong side.
        assertThat(c.getRepeatLastN(), is(-1));

        // act, assert
        c.setRepeatLastN(0);
        assertThat(c.getRepeatLastN(), is(0));
        c.setRepeatLastN(256);
        assertThat(c.getRepeatLastN(), is(256));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="cacheTypeK">
    @Test
    public void cacheTypeK_defaultAndSet_defaultsEmptyAndRoundTrips() {
        // arrange
        final ModelDefinition c = new ModelDefinition();

        // pre-assert
        assertThat(c.getCacheTypeK(), is(""));

        // act
        c.setCacheTypeK("q8_0");

        // assert
        assertThat(c.getCacheTypeK(), is("q8_0"));
    }

    @Test
    public void cacheTypeK_setNull_resetsToEmpty() {
        // arrange
        final ModelDefinition c = new ModelDefinition();
        c.setCacheTypeK("q8_0");

        // act
        c.setCacheTypeK(null);

        // assert
        assertThat(c.getCacheTypeK(), is(""));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="cacheTypeV">
    @Test
    public void cacheTypeV_defaultAndSet_defaultsEmptyAndRoundTrips() {
        // arrange
        final ModelDefinition c = new ModelDefinition();

        // pre-assert
        assertThat(c.getCacheTypeV(), is(""));

        // act
        c.setCacheTypeV("q4_0");

        // assert
        // Distinct from the K value above: the two are separate fields, not one shared setting.
        assertThat(c.getCacheTypeV(), is("q4_0"));
        assertThat(c.getCacheTypeK(), is(""));
    }

    @Test
    public void cacheTypeV_setNull_resetsToEmpty() {
        // arrange
        final ModelDefinition c = new ModelDefinition();
        c.setCacheTypeV("q4_0");

        // act
        c.setCacheTypeV(null);

        // assert
        assertThat(c.getCacheTypeV(), is(""));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="flashAttn">
    @Test
    public void flashAttn_defaultAndSet_defaultsOffAndRoundTrips() {
        // arrange
        final ModelDefinition c = new ModelDefinition();

        // pre-assert
        // llama.cpp's own default is off; enabling it is opt-in because it is also the precondition
        // for a quantized V cache.
        assertThat(c.isFlashAttn(), is(false));

        // act
        c.setFlashAttn(true);

        // assert
        assertThat(c.isFlashAttn(), is(true));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="batchSizes">
    @Test
    public void batchSizes_defaultAndSet_defaultToUnsetAndRoundTripIndependently() {
        // arrange
        final ModelDefinition c = new ModelDefinition();

        // pre-assert
        assertThat(c.getBatchSize(), is(-1));
        assertThat(c.getUbatchSize(), is(-1));

        // act
        c.setBatchSize(512);
        c.setUbatchSize(256);

        // assert
        // Different values on purpose: logical and physical batch size are separate knobs.
        assertThat(c.getBatchSize(), is(512));
        assertThat(c.getUbatchSize(), is(256));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="threadsBatch">
    @Test
    public void threadsBatch_defaultAndSet_defaultsToUnsetAndRoundTrips() {
        // arrange
        final ModelDefinition c = new ModelDefinition();

        // pre-assert
        // -1 means "reuse threads"; it must not collide with the decode thread count.
        assertThat(c.getThreadsBatch(), is(-1));

        // act
        c.setThreads(7);
        c.setThreadsBatch(12);

        // assert
        assertThat(c.getThreadsBatch(), is(12));
        assertThat(c.getThreads(), is(7));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="copy">
    /** A value of the field's type that differs from every default, so a field the copy loses shows. */
    private static Object changed(final Field field, final Object current) {
        final Class<?> type = field.getType();
        if (type == int.class) {
            return ((Integer) current) + 7;
        }
        if (type == float.class) {
            return ((Float) current) + 0.5f;
        }
        if (type == boolean.class) {
            return !((Boolean) current);
        }
        if (type == Boolean.class) {
            return Boolean.TRUE;
        }
        if (type == String.class) {
            return field.getName() + "-set";
        }
        if (type == List.class) {
            return new ArrayList<>(Arrays.asList(field.getName()));
        }
        if (type == Calibration.class) {
            return new Calibration();
        }
        throw new AssertionError("no changed value for " + field);
    }

    @Test
    public void copy_everyFieldChanged_copiesEveryField() throws IllegalAccessException {
        // arrange
        final ModelDefinition definition = new ModelDefinition();
        for (final Field field : ModelDefinition.class.getDeclaredFields()) {
            if (!Modifier.isStatic(field.getModifiers())) {
                field.setAccessible(true);
                field.set(definition, changed(field, field.get(definition)));
            }
        }

        // act
        final ModelDefinition copy = definition.copy();

        // assert
        assertThat(copy, is(not(sameInstance(definition))));
        assertThat(copy.toString(), is(definition.toString()));
    }

    @Test
    public void copy_lists_areNotShared() {
        // arrange
        final ModelDefinition definition = new ModelDefinition();
        definition.setStopStrings(Arrays.asList("<end>"));

        // act
        final ModelDefinition copy = definition.copy();
        definition.setStopStrings(Arrays.asList("<other>"));

        // assert
        assertThat(copy.getStopStrings(), is(Arrays.asList("<end>")));
    }
    // </editor-fold>
}
