// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.provider;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Map;
import net.ladenthin.llama.args.CacheType;
import net.ladenthin.llama.args.LazyMode;
import net.ladenthin.llama.value.ChatChoice;
import net.ladenthin.llama.value.ChatMessage;
import net.ladenthin.llama.value.ChatResponse;
import net.ladenthin.llama.value.Usage;
import net.ladenthin.srcmorph.CommonTestFixtures;
import net.ladenthin.srcmorph.document.AiMdHeader;
import net.ladenthin.srcmorph.document.AiMdHeaderCodec;
import net.ladenthin.srcmorph.prompt.GenerationRequest;
import net.ladenthin.srcmorph.prompt.PromptCatalog;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

/**
 * Model-free tests for {@link LlamaCppJniProviderSupport}.
 *
 * <p>Every case here builds its configuration over a path that does not exist. That is not a
 * shortcut, it is the contract this class exists to keep: none of the support methods touches the
 * native handle, so all of them can be pinned without a GGUF -- which is what lets the mutation
 * gate cover them at threshold 100 without dragging the model-loading suites into every mutant.</p>
 */
public class LlamaCppJniProviderSupportTest {

    /**
     * JSON key of the DRY penalty window as the binding renders it. Quoted so the assertions cannot
     * accidentally match a different key that merely contains this one as a substring.
     */
    private static final String PARAM_DRY_PENALTY_LAST_N = "\"dry_penalty_last_n\"";

    /** JSON key of the repeat-penalty window, quoted for the same reason. */
    private static final String PARAM_REPEAT_LAST_N = "\"repeat_last_n\"";

    /** JSON key of the RNG seed, quoted for the same reason. */
    private static final String PARAM_SEED = "\"seed\"";

    /** JSON key of the DRY sequence breakers, quoted for the same reason. */
    private static final String PARAM_DRY_SEQUENCE_BREAKERS = "\"dry_sequence_breakers\"";

    /** Chat-template kwarg key for Qwen-style thinking, as the provider spells it. */
    private static final String KWARG_ENABLE_THINKING = "enable_thinking";

    /** Chat-template kwarg key for the gpt-oss reasoning-effort level. */
    private static final String KWARG_REASONING_EFFORT = "reasoning_effort";

    private static final AiMdHeader HEADER = new AiMdHeader(
            "Test.java",
            AiMdHeaderCodec.HEADER_VERSION_1_0,
            "00000000",
            "2026-03-18T00:00:00Z",
            "2026-03-18T00:00:00Z",
            "0.1.0-SNAPSHOT",
            "0.0.0",
            AiMdHeaderCodec.NODE_TYPE_FILE);

    private static GenerationRequest request(final String source) {
        return new GenerationRequest(CommonTestFixtures.PROMPT_KEY_FILE_BODY, Paths.get("Test.java"), source);
    }

    // <editor-fold defaultstate="collapsed" desc="lazyMode">
    @Test
    public void lazyMode_everyDeclaredCliString_mapsToItsMode() {
        // Every mode the binding declares must resolve; a new upstream mode is then covered for free.
        // act, assert
        for (final LazyMode mode : LazyMode.values()) {
            assertThat(LlamaCppJniProviderSupport.lazyMode(mode.getArgValue()), is(mode));
        }
    }

    @Test
    public void lazyMode_mixedCaseValue_isCaseInsensitive() {
        // act, assert
        assertThat(LlamaCppJniProviderSupport.lazyMode("ON"), is(LazyMode.ON));
        assertThat(LlamaCppJniProviderSupport.lazyMode("Auto"), is(LazyMode.AUTO));
    }

    @Test
    public void lazyMode_unknownValue_throwsExceptionNamingTheAcceptedOnes() {
        // A typo must fail loud rather than be dropped, and the message must name what is accepted.
        // act
        final IllegalArgumentException thrown = Assertions.assertThrows(
                IllegalArgumentException.class, () -> LlamaCppJniProviderSupport.lazyMode("lazy"));

        // assert
        assertThat(thrown.getMessage(), containsString("lazy"));
        assertThat(thrown.getMessage(), containsString("off, auto, on"));
        // Anchored to the "expected one of: " prefix on purpose: the renderer emits its separator
        // only from the second value on, and a guard written as >= 0 instead of > 0 would prepend a
        // stray ", " that a containsString on the middle of the list cannot see.
        assertThat(thrown.getMessage(), containsString("expected one of: off"));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="cacheType">
    @Test
    public void cacheType_everyDeclaredCliString_mapsToItsType() {
        // Every cache type the binding declares must resolve; a type upstream adds is covered for free.
        // act, assert
        for (final CacheType type : CacheType.values()) {
            assertThat(LlamaCppJniProviderSupport.cacheType("cacheTypeK", type.getArgValue()), is(type));
        }
    }

    @Test
    public void cacheType_mixedCaseValue_isCaseInsensitive() {
        // act, assert
        assertThat(LlamaCppJniProviderSupport.cacheType("cacheTypeK", "Q8_0"), is(CacheType.Q8_0));
        assertThat(LlamaCppJniProviderSupport.cacheType("cacheTypeV", "F16"), is(CacheType.F16));
    }

    @Test
    public void cacheType_unknownValue_throwsExceptionNamingTheKnobAndTheAcceptedOnes() {
        // A typo must fail loud rather than be dropped. The knob name is in the message because the same
        // resolver serves cacheTypeK and cacheTypeV -- without it the user cannot tell which one is wrong.
        // act
        final IllegalArgumentException thrown = Assertions.assertThrows(
                IllegalArgumentException.class, () -> LlamaCppJniProviderSupport.cacheType("cacheTypeV", "q3_k"));

        // assert
        assertThat(thrown.getMessage(), containsString("cacheTypeV"));
        assertThat(thrown.getMessage(), containsString("q3_k"));
        assertThat(thrown.getMessage(), containsString("f32, f16, bf16, q8_0"));
        // Same anchor as lazyMode above, for the same separator boundary.
        assertThat(thrown.getMessage(), containsString("expected one of: f32"));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="knobProblem">
    @Test
    public void knobProblem_blankAndKnownValues_isNull() {
        // act, assert
        assertThat(LlamaCppJniProviderSupport.knobProblem("", " ", ""), is(nullValue()));
        assertThat(LlamaCppJniProviderSupport.knobProblem("auto", "q8_0", "F16"), is(nullValue()));
    }

    @Test
    public void knobProblem_unknownLazyMode_namesIt() {
        // act
        final String problem = LlamaCppJniProviderSupport.knobProblem("sometimes", "", "");

        // assert
        assertThat(problem, containsString("lazyMode"));
        assertThat(problem, containsString("sometimes"));
    }

    @Test
    public void knobProblem_unknownCacheTypeK_namesIt() {
        // act
        final String problem = LlamaCppJniProviderSupport.knobProblem("", "q3_k", "");

        // assert
        assertThat(problem, containsString("cacheTypeK"));
    }

    @Test
    public void knobProblem_unknownCacheTypeV_namesIt() {
        // act
        final String problem = LlamaCppJniProviderSupport.knobProblem("", "q8_0", "q3_k");

        // assert
        assertThat(problem, containsString("cacheTypeV"));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="buildInferenceParameters: negative-sentinel guards">

    /**
     * Builds a provider over a path that does not exist. The model is loaded lazily on the first
     * generate, and {@code buildInferenceParameters} never touches it, so these tests need no GGUF and
     * no native library -- which is the point: the regression they guard shipped twice while every
     * model-free gate stayed green.
     */
    private static LlamaCppJniProviderSupport providerWith(final ProviderConfig config) {
        return new LlamaCppJniProviderSupport(
                config, new PromptCatalog(CommonTestFixtures.createFilePromptDefinitions()));
    }

    /**
     * The regression itself. Both penalty windows default to {@code -1}, and the binding rejects any
     * negative window outright ({@code IllegalArgumentException}) because llama.cpp b10273 dropped
     * "{@code -1} = context size". Forwarding one unguarded therefore throws before a single token is
     * produced -- which is exactly what {@code dry_penalty_last_n} did in 1.1.0 and 1.1.1.
     */
    @Test
    public void buildInferenceParameters_defaultConfig_doesNotThrowOnTheNegativeSentinels() {
        // arrange
        final ProviderConfig defaults =
                ProviderConfig.builder().modelPath("/does/not/exist.gguf").build();

        // act, assert
        Assertions.assertDoesNotThrow(() -> providerWith(defaults).buildInferenceParameters(request("class A {}")));
    }

    /** A sentinel means "say nothing", so llama.cpp keeps its own window -- it must not be sent as -1. */
    @Test
    public void buildInferenceParameters_defaultConfig_sendsNeitherPenaltyWindow() {
        // arrange
        final ProviderConfig defaults =
                ProviderConfig.builder().modelPath("/does/not/exist.gguf").build();

        // act
        final String json = providerWith(defaults)
                .buildInferenceParameters(request("class A {}"))
                .toJson();

        // assert
        assertThat(json, not(containsString(PARAM_DRY_PENALTY_LAST_N)));
        assertThat(json, not(containsString(PARAM_REPEAT_LAST_N)));
    }

    /**
     * {@code 0} is a real value, not a second sentinel: it disables the penalty. A guard written as
     * {@code > 0} would swallow it, so this pins the boundary from the other side.
     */
    @Test
    public void buildInferenceParameters_zeroWindows_sendsBothAsZero() {
        // arrange
        final ProviderConfig zeroed = ProviderConfig.builder()
                .modelPath("/does/not/exist.gguf")
                .dryPenaltyLastN(0)
                .repeatLastN(0)
                .build();

        // act
        final String json = providerWith(zeroed)
                .buildInferenceParameters(request("class A {}"))
                .toJson();

        // assert
        assertThat(json, containsString(PARAM_DRY_PENALTY_LAST_N));
        assertThat(json, containsString(PARAM_REPEAT_LAST_N));
    }

    /** A configured positive window reaches the request unchanged. */
    @Test
    public void buildInferenceParameters_configuredWindows_arePassedThrough() {
        // arrange
        final ProviderConfig configured = ProviderConfig.builder()
                .modelPath("/does/not/exist.gguf")
                .dryPenaltyLastN(64)
                .repeatLastN(128)
                .build();

        // act
        final String json = providerWith(configured)
                .buildInferenceParameters(request("class A {}"))
                .toJson();

        // assert -- distinct values, so a transposition of the two guards fails rather than cancelling out
        assertThat(json, containsString(PARAM_DRY_PENALTY_LAST_N + ":64"));
        assertThat(json, containsString(PARAM_REPEAT_LAST_N + ":128"));
    }

    /**
     * The seed sentinel is {@link net.ladenthin.srcmorph.config.ModelDefinition#DEFAULT_SEED} (-1),
     * meaning "random per request". Forwarding it would pin every run to seed -1 and silently destroy
     * the randomness upstream provides, so an unconfigured run must send nothing at all.
     */
    @Test
    public void buildInferenceParameters_defaultConfig_sendsNoSeed() {
        // arrange
        final ProviderConfig defaults =
                ProviderConfig.builder().modelPath("/does/not/exist.gguf").build();

        // act
        final String json = providerWith(defaults)
                .buildInferenceParameters(request("class A {}"))
                .toJson();

        // assert
        assertThat(json, not(containsString(PARAM_SEED)));
    }

    /**
     * {@code 0} is a legitimate seed, not a second sentinel, so the guard has to be {@code >= 0}. Pins
     * that boundary from the other side, exactly as the penalty windows do above -- a guard written as
     * {@code > 0} would silently drop a run the user pinned to seed 0.
     */
    @Test
    public void buildInferenceParameters_zeroSeed_isStillSent() {
        // arrange
        final ProviderConfig zeroSeed = ProviderConfig.builder()
                .modelPath("/does/not/exist.gguf")
                .seed(0)
                .build();

        // act
        final String json = providerWith(zeroSeed)
                .buildInferenceParameters(request("class A {}"))
                .toJson();

        // assert
        assertThat(json, containsString(PARAM_SEED + ":0"));
    }

    /**
     * An empty breaker list means "keep the binding's own default set", not "clear it". Sending an empty
     * array would replace a sensible default with nothing, which is a behaviour change rather than a
     * no-op -- hence the guard, and hence this test.
     */
    @Test
    public void buildInferenceParameters_defaultConfig_sendsNoDrySequenceBreakers() {
        // arrange
        final ProviderConfig defaults =
                ProviderConfig.builder().modelPath("/does/not/exist.gguf").build();

        // act
        final String json = providerWith(defaults)
                .buildInferenceParameters(request("class A {}"))
                .toJson();

        // assert
        assertThat(json, not(containsString(PARAM_DRY_SEQUENCE_BREAKERS)));
    }

    /** A configured breaker list reaches the request. */
    @Test
    public void buildInferenceParameters_configuredDrySequenceBreakers_arePassedThrough() {
        // arrange
        final ProviderConfig configured = ProviderConfig.builder()
                .modelPath("/does/not/exist.gguf")
                .drySequenceBreakers(Collections.singletonList("\\n"))
                .build();

        // act
        final String json = providerWith(configured)
                .buildInferenceParameters(request("class A {}"))
                .toJson();

        // assert
        assertThat(json, containsString(PARAM_DRY_SEQUENCE_BREAKERS));
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="truncationWarning">

    /** Builds a response carrying just the one field each of these two helpers reads. */
    private static ChatResponse responseWith(final String finishReason, final Usage usage) {
        final ChatChoice choice = new ChatChoice(0, new ChatMessage("assistant", "body"), finishReason);
        return new ChatResponse("id", Collections.singletonList(choice), usage, null, "{}");
    }

    /** The logger's level before {@link #attachAppender(Level)}, restored by {@link #detachAppender}. */
    private static Level previousLevel;

    private static ListAppender<ILoggingEvent> attachAppender(final Level level) {
        final ListAppender<ILoggingEvent> appender = new ListAppender<>();
        appender.start();
        final Logger logger = (Logger) LoggerFactory.getLogger(LlamaCppJniProviderSupport.class);
        previousLevel = logger.getLevel();
        logger.setLevel(level);
        logger.addAppender(appender);
        return appender;
    }

    private static void detachAppender(final ListAppender<ILoggingEvent> appender) {
        final Logger logger = (Logger) LoggerFactory.getLogger(LlamaCppJniProviderSupport.class);
        logger.detachAppender(appender);
        logger.setLevel(previousLevel);
        appender.stop();
    }

    /**
     * The whole point of the feature: {@code length} means the model hit the output budget and stopped
     * mid-sentence, so the {@code .ai.md} it produced is incomplete and the user has to be told.
     */
    @Test
    public void truncationWarning_finishReasonLength_namesTheFileAndTheBudget() {
        // arrange
        final LlamaCppJniProviderSupport provider = providerWith(ProviderConfig.builder()
                .modelPath("/does/not/exist.gguf")
                .maxOutputTokens(128)
                .build());

        // act
        final String warning =
                provider.truncationWarning(request("class A {}"), responseWith("length", new Usage(1, 1)));

        // assert
        assertThat(
                warning,
                is("Generated text for Test.java was cut off at the 128-token output budget, not finished by the"
                        + " model. The .ai.md will end mid-thought; raise maxOutputTokens for this model, or"
                        + " shorten the prompt."));
    }

    /**
     * A normal completion must stay silent. This is the assertion that pins the {@code "length"}
     * literal: {@code StopReason.fromStopType("length")} is {@code NONE}, so comparing against the
     * wrong vocabulary would make the warning fire never or always -- silently, either way.
     */
    @Test
    public void truncationWarning_finishReasonStop_isNone() {
        // arrange
        final LlamaCppJniProviderSupport provider = providerWith(
                ProviderConfig.builder().modelPath("/does/not/exist.gguf").build());

        // act, assert
        assertThat(
                provider.truncationWarning(request("class A {}"), responseWith("stop", new Usage(1, 1))),
                is(nullValue()));
    }

    /** The calibration's answer the provider stopped is named with its reason, not as a budget cut. */
    @Test
    public void truncationWarning_finishReasonError_namesTheReason() {
        // arrange
        final LlamaCppJniProviderSupport provider = providerWith(
                ProviderConfig.builder().modelPath("/does/not/exist.gguf").build());

        // act, assert
        assertThat(
                provider.truncationWarning(request("class A {}"), responseWith("error", new Usage(1, 1))),
                is("Generated text for Test.java ended with finish reason 'error': the provider stopped it, not"
                        + " the model."));
    }

    /** No choices means nothing to judge; reading {@code get(0)} anyway would throw. */
    @Test
    public void truncationWarning_noChoices_isNone() {
        // arrange
        final LlamaCppJniProviderSupport provider = providerWith(
                ProviderConfig.builder().modelPath("/does/not/exist.gguf").build());
        final ChatResponse empty = new ChatResponse("id", Collections.emptyList(), new Usage(1, 1), null, "{}");

        // act, assert
        assertThat(provider.truncationWarning(request("class A {}"), empty), is(nullValue()));
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="requireCompleteAnswer">

    // requireCompleteAnswer: what generate refuses to return as a summary
    @Test
    public void requireCompleteAnswer_finishedAnswerWithText_passes() throws Exception {
        // arrange
        final LlamaCppJniProviderSupport provider = providerWith(
                ProviderConfig.builder().modelPath("/does/not/exist.gguf").build());

        // act, assert
        provider.requireCompleteAnswer(request("class A {}"), responseWith("stop", new Usage(1, 1)), "A summary.");
    }

    @Test
    public void requireCompleteAnswer_noChoice_isNoAnswer() {
        // arrange
        final LlamaCppJniProviderSupport provider = providerWith(
                ProviderConfig.builder().modelPath("/does/not/exist.gguf").build());
        final ChatResponse empty = new ChatResponse("id", Collections.emptyList(), new Usage(1, 1), null, "{}");
        final GenerationRequest generationRequest = request("class A {}");

        // act
        final IncompleteAnswerException thrown = assertThrows(
                IncompleteAnswerException.class, () -> provider.requireCompleteAnswer(generationRequest, empty, ""));

        // assert
        assertThat(thrown.reason(), is(IncompleteAnswerException.Reason.NO_ANSWER));
        assertThat(thrown.getMessage(), is("The response for Test.java has no answer (no choice)"));
    }

    @Test
    public void requireCompleteAnswer_cutOffAtTheBudget_isTruncatedWithTheBudgetAndTheText() {
        // arrange
        final LlamaCppJniProviderSupport provider = providerWith(ProviderConfig.builder()
                .modelPath("/does/not/exist.gguf")
                .maxOutputTokens(128)
                .build());
        final GenerationRequest generationRequest = request("class A {}");
        final ChatResponse response = responseWith("length", new Usage(1, 1));

        // act
        final IncompleteAnswerException thrown = assertThrows(
                IncompleteAnswerException.class,
                () -> provider.requireCompleteAnswer(generationRequest, response, "half a sum"));

        // assert
        assertThat(thrown.reason(), is(IncompleteAnswerException.Reason.TRUNCATED));
        assertThat(thrown.getMessage(), containsString("Test.java was cut off at the 128-token output budget"));
        assertThat(thrown.partialText(), is("half a sum"));
    }

    @Test
    public void requireCompleteAnswer_finishReasonError_isAbortedWithTheReasonAndTheText() {
        // arrange
        final LlamaCppJniProviderSupport provider = providerWith(
                ProviderConfig.builder().modelPath("/does/not/exist.gguf").build());
        final GenerationRequest generationRequest = request("class A {}");
        final ChatResponse response = responseWith("error", new Usage(1, 1));

        // act
        final IncompleteAnswerException thrown = assertThrows(
                IncompleteAnswerException.class,
                () -> provider.requireCompleteAnswer(generationRequest, response, "- test paging ("));

        // assert
        assertThat(thrown.reason(), is(IncompleteAnswerException.Reason.ABORTED));
        assertThat(
                thrown.getMessage(),
                is("The answer for Test.java ended with finish reason 'error': the provider stopped it, not the"
                        + " model"));
        assertThat(thrown.partialText(), is("- test paging ("));
    }

    @Test
    public void requireCompleteAnswer_finishReasonContentFilter_isAborted() {
        // arrange
        final LlamaCppJniProviderSupport provider = providerWith(
                ProviderConfig.builder().modelPath("/does/not/exist.gguf").build());
        final GenerationRequest generationRequest = request("class A {}");
        final ChatResponse response = responseWith("content_filter", new Usage(1, 1));

        // act
        final IncompleteAnswerException thrown = assertThrows(
                IncompleteAnswerException.class,
                () -> provider.requireCompleteAnswer(generationRequest, response, "A summ"));

        // assert
        assertThat(thrown.reason(), is(IncompleteAnswerException.Reason.ABORTED));
        assertThat(thrown.getMessage(), containsString("finish reason 'content_filter'"));
    }

    /** A server's own word for a finished answer (TGI's {@code eos_token}) or none at all is the model's. */
    @Test
    public void requireCompleteAnswer_anotherOrNoFinishReason_passes() throws Exception {
        // arrange
        final LlamaCppJniProviderSupport provider = providerWith(
                ProviderConfig.builder().modelPath("/does/not/exist.gguf").build());

        // act, assert
        provider.requireCompleteAnswer(request("class A {}"), responseWith("eos_token", new Usage(1, 1)), "A summary.");
        provider.requireCompleteAnswer(request("class A {}"), responseWith(null, new Usage(1, 1)), "A summary.");
        assertThat(LlamaCppJniProviderSupport.stoppedByProvider(null), is(false));
    }

    @Test
    public void requireCompleteAnswer_finishedButBlank_isEmpty() {
        // arrange
        final LlamaCppJniProviderSupport provider = providerWith(
                ProviderConfig.builder().modelPath("/does/not/exist.gguf").build());
        final GenerationRequest generationRequest = request("class A {}");
        final ChatResponse response = responseWith("stop", new Usage(1, 1));

        // act
        final IncompleteAnswerException thrown = assertThrows(
                IncompleteAnswerException.class,
                () -> provider.requireCompleteAnswer(generationRequest, response, " \n"));

        // assert
        assertThat(thrown.reason(), is(IncompleteAnswerException.Reason.EMPTY));
        assertThat(
                thrown.getMessage(),
                is("The model answered nothing for Test.java (an empty or reasoning-only answer)"));
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="completeAnswer">
    /** A response whose one choice carries this content and finish reason. */
    private static ChatResponse answered(final String content, final String finishReason) {
        final ChatChoice choice = new ChatChoice(0, new ChatMessage("assistant", content), finishReason);
        return new ChatResponse("id", Collections.singletonList(choice), new Usage(1, 1), null, "{}");
    }

    /** A reasoning block the model opened and never closed. */
    private static final String OPEN_REASONING = "<think>I will look at the class and";

    @Test
    public void completeAnswer_finishedAnswer_returnsItWithoutTheReasoning() throws Exception {
        // arrange
        final LlamaCppJniProviderSupport provider = providerWith(
                ProviderConfig.builder().modelPath("/does/not/exist.gguf").build());

        // act
        final String text = provider.completeAnswer(
                request("class A {}"), answered("<think>hmm</think>\nA summary.", "stop"), new CompletionParser());

        // assert
        assertThat(text, is("A summary."));
    }

    /** The provider stopped the answer inside the reasoning: that, not the budget, is why it is open. */
    @Test
    public void completeAnswer_openReasoningTheProviderStopped_isAbortedWithoutTheReasoning() {
        // arrange
        final LlamaCppJniProviderSupport provider = providerWith(
                ProviderConfig.builder().modelPath("/does/not/exist.gguf").build());
        final GenerationRequest generationRequest = request("class A {}");
        final ChatResponse response = answered(OPEN_REASONING, "error");
        final CompletionParser parser = new CompletionParser();

        // act
        final IncompleteAnswerException thrown = assertThrows(
                IncompleteAnswerException.class, () -> provider.completeAnswer(generationRequest, response, parser));

        // assert
        assertThat(thrown.reason(), is(IncompleteAnswerException.Reason.ABORTED));
        assertThat(
                thrown.getMessage(),
                is("The answer for Test.java ended with finish reason 'error': the provider stopped it, not the"
                        + " model"));
        assertThat(thrown.partialText(), is(""));
    }

    @Test
    public void completeAnswer_openReasoningTheModelStopped_isTruncated() {
        // arrange: the model ended inside its reasoning, at the budget or not
        final LlamaCppJniProviderSupport provider = providerWith(
                ProviderConfig.builder().modelPath("/does/not/exist.gguf").build());
        final GenerationRequest generationRequest = request("class A {}");
        final ChatResponse atTheBudget = answered(OPEN_REASONING, "length");
        final ChatResponse stopped = answered(OPEN_REASONING, "stop");
        final CompletionParser parser = new CompletionParser();

        // act
        final IncompleteAnswerException budget = assertThrows(
                IncompleteAnswerException.class, () -> provider.completeAnswer(generationRequest, atTheBudget, parser));
        final IncompleteAnswerException stop = assertThrows(
                IncompleteAnswerException.class, () -> provider.completeAnswer(generationRequest, stopped, parser));

        // assert
        assertThat(budget.reason(), is(IncompleteAnswerException.Reason.TRUNCATED));
        assertThat(budget.getMessage(), containsString("inside thinking block"));
        assertThat(stop.reason(), is(IncompleteAnswerException.Reason.TRUNCATED));
    }

    @Test
    public void completeAnswer_answerTheProviderStopped_isAbortedWithTheText() {
        // arrange
        final LlamaCppJniProviderSupport provider = providerWith(
                ProviderConfig.builder().modelPath("/does/not/exist.gguf").build());
        final GenerationRequest generationRequest = request("class A {}");
        final ChatResponse response = answered("<think>hmm</think>\n- test paging (", "content_filter");
        final CompletionParser parser = new CompletionParser();

        // act
        final IncompleteAnswerException thrown = assertThrows(
                IncompleteAnswerException.class, () -> provider.completeAnswer(generationRequest, response, parser));

        // assert
        assertThat(thrown.reason(), is(IncompleteAnswerException.Reason.ABORTED));
        assertThat(thrown.partialText(), is("- test paging ("));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="logPromptCacheReuse">

    /** The cache line reports all three counts, so a transposition of two of them is visible. */
    @Test
    public void logPromptCacheReuse_debugEnabled_reportsCachedTotalAndGenerated() {
        // arrange
        final ListAppender<ILoggingEvent> appender = attachAppender(Level.DEBUG);
        try {
            final LlamaCppJniProviderSupport provider = providerWith(
                    ProviderConfig.builder().modelPath("/does/not/exist.gguf").build());

            // act -- distinct values so each lands in its own placeholder
            provider.logPromptCacheReuse(request("class A {}"), responseWith("stop", new Usage(70, 11, 33)));

            // assert
            assertThat(appender.list.size(), is(1));
            final String message = appender.list.get(0).getFormattedMessage();
            assertThat(message, containsString("33 of 70 prompt token(s)"));
            assertThat(message, containsString("11 generated"));
        } finally {
            detachAppender(appender);
        }
    }

    /** At INFO the line must not be built at all -- it is one log line per indexed file. */
    @Test
    public void logPromptCacheReuse_debugDisabled_saysNothing() {
        // arrange
        final ListAppender<ILoggingEvent> appender = attachAppender(Level.INFO);
        try {
            final LlamaCppJniProviderSupport provider = providerWith(
                    ProviderConfig.builder().modelPath("/does/not/exist.gguf").build());

            // act
            provider.logPromptCacheReuse(request("class A {}"), responseWith("stop", new Usage(70, 11, 33)));

            // assert
            assertThat(appender.list.isEmpty(), is(true));
        } finally {
            detachAppender(appender);
        }
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="buildChatTemplateKwargs: opt-in kwargs">

    /**
     * The point of the tri-state. While {@code chatTemplateEnableThinking} was a plain
     * {@code boolean} defaulting to {@code true}, every run handed {@code enable_thinking} to the
     * chat template -- including templates that have never heard of it, which llama.cpp's Jinja
     * layer has been moving from "silently ignored" toward "warned about". Unset must now mean
     * "say nothing", so the template's own default applies.
     *
     * <p>The sibling kwarg is deliberately not asserted away here: {@code reasoningEffort} does
     * default to {@code "low"} and is therefore still sent by a default run, but unlike
     * {@code enable_thinking} it has an escape that means exactly "unset" -- the empty string -- so a
     * non-gpt-oss user can switch it off without picking a value that says something else. That is a
     * documented choice, not the same defect; the next test pins that escape.</p>
     */
    @Test
    public void buildChatTemplateKwargs_defaultConfig_doesNotSendEnableThinking() {
        // arrange
        final ProviderConfig defaults =
                ProviderConfig.builder().modelPath("/does/not/exist.gguf").build();

        // act
        final Map<String, String> kwargs = providerWith(defaults).buildChatTemplateKwargs();

        // assert
        assertThat(kwargs.containsKey(KWARG_ENABLE_THINKING), is(false));
        assertThat(kwargs.get(KWARG_REASONING_EFFORT), is("low"));
    }

    /** The other half of "unset means unset": a blank reasoning effort omits its kwarg too. */
    @Test
    public void buildChatTemplateKwargs_blankReasoningEffort_sendsNoKwargAtAll() {
        // arrange
        final ProviderConfig blank = ProviderConfig.builder()
                .modelPath("/does/not/exist.gguf")
                .reasoningEffort("")
                .build();

        // act
        final Map<String, String> kwargs = providerWith(blank).buildChatTemplateKwargs();

        // assert
        assertThat(kwargs.isEmpty(), is(true));
    }

    /**
     * {@code true} is a real configured value, not a second spelling of "unset". A guard written as
     * "send it only when false" would swallow it, so this pins the boundary from the other side --
     * the same shape as the {@code 0}-versus-{@code -1} penalty-window pair above.
     */
    @Test
    public void buildChatTemplateKwargs_thinkingSetToTrue_isStillSent() {
        // arrange
        final ProviderConfig enabled = ProviderConfig.builder()
                .modelPath("/does/not/exist.gguf")
                .chatTemplateEnableThinking(Boolean.TRUE)
                .build();

        // act
        final Map<String, String> kwargs = providerWith(enabled).buildChatTemplateKwargs();

        // assert
        assertThat(kwargs.get(KWARG_ENABLE_THINKING), is("true"));
        assertThat(kwargs.get(KWARG_REASONING_EFFORT), is("low"));
    }

    /**
     * The Gemma-4 case the knob exists for: suppress the thinking block at the Jinja level. The
     * default reasoning effort goes unsent with it: Kolibri-1's template reads
     * {@code reasoning_effort} first and reasoned at {@code "low"} although thinking was off.
     */
    @Test
    public void buildChatTemplateKwargs_thinkingSetToFalse_isSentAsFalse() {
        // arrange
        final ProviderConfig disabled = ProviderConfig.builder()
                .modelPath("/does/not/exist.gguf")
                .chatTemplateEnableThinking(Boolean.FALSE)
                .build();

        // act
        final Map<String, String> kwargs = providerWith(disabled).buildChatTemplateKwargs();

        // assert
        assertThat(kwargs.get(KWARG_ENABLE_THINKING), is("false"));
        assertThat(kwargs.containsKey(KWARG_REASONING_EFFORT), is(false));
    }

    /**
     * The second kwarg was already opt-in (blank omits it); pinned here so the extraction of
     * {@code buildChatTemplateKwargs} out of {@code model()} cannot drop it unnoticed.
     */
    @Test
    public void buildChatTemplateKwargs_reasoningEffortConfigured_isSentAlongside() {
        // arrange
        final ProviderConfig configured = ProviderConfig.builder()
                .modelPath("/does/not/exist.gguf")
                .reasoningEffort("high")
                .build();

        // act
        final Map<String, String> kwargs = providerWith(configured).buildChatTemplateKwargs();

        // assert
        assertThat(kwargs.get(KWARG_REASONING_EFFORT), is("high"));
        assertThat(kwargs.containsKey(KWARG_ENABLE_THINKING), is(false));
    }

    /** Thinking off wins over a configured effort too, as llama.cpp's own {@code --reasoning off} does. */
    @Test
    public void buildChatTemplateKwargs_thinkingOffWithConfiguredEffort_sendsOnlyThinkingOff() {
        // arrange
        final ProviderConfig configured = ProviderConfig.builder()
                .modelPath("/does/not/exist.gguf")
                .reasoningEffort("high")
                .chatTemplateEnableThinking(Boolean.FALSE)
                .build();

        // act
        final Map<String, String> kwargs = providerWith(configured).buildChatTemplateKwargs();

        // assert
        assertThat(kwargs.size(), is(1));
        assertThat(kwargs.get(KWARG_ENABLE_THINKING), is("false"));
    }

    /**
     * The other way to thinking off: llama.cpp's server maps a request's {@code reasoning_effort}
     * {@code "none"} to {@code enable_thinking} {@code false} and drops the kwarg. The model's
     * default kwargs skip that mapping, so without it the template alone would know thinking is off.
     */
    @Test
    public void buildChatTemplateKwargs_reasoningEffortNone_sendsThinkingOffInstead() {
        // arrange
        final ProviderConfig none = ProviderConfig.builder()
                .modelPath("/does/not/exist.gguf")
                .reasoningEffort("none")
                .build();

        // act
        final Map<String, String> kwargs = providerWith(none).buildChatTemplateKwargs();

        // assert
        assertThat(kwargs.size(), is(1));
        assertThat(kwargs.get(KWARG_ENABLE_THINKING), is("false"));
    }

    /** {@code "none"} wins over thinking switched on, as it does in llama.cpp's server. */
    @Test
    public void buildChatTemplateKwargs_reasoningEffortNoneWithThinkingOn_sendsThinkingOff() {
        // arrange
        final ProviderConfig conflicting = ProviderConfig.builder()
                .modelPath("/does/not/exist.gguf")
                .reasoningEffort("none")
                .chatTemplateEnableThinking(Boolean.TRUE)
                .build();

        // act
        final Map<String, String> kwargs = providerWith(conflicting).buildChatTemplateKwargs();

        // assert
        assertThat(kwargs.size(), is(1));
        assertThat(kwargs.get(KWARG_ENABLE_THINKING), is("false"));
    }

    // </editor-fold>
}
