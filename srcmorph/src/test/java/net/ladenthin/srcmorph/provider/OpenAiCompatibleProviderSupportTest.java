// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.provider;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import net.ladenthin.llama.json.ChatResponseParser;
import net.ladenthin.llama.value.ChatChoice;
import net.ladenthin.llama.value.ChatMessage;
import net.ladenthin.llama.value.ChatResponse;
import net.ladenthin.llama.value.Usage;
import net.ladenthin.srcmorph.CommonTestFixtures;
import net.ladenthin.srcmorph.document.AiGenerationRequest;
import net.ladenthin.srcmorph.document.AiMdHeader;
import net.ladenthin.srcmorph.document.AiMdHeaderCodec;
import net.ladenthin.srcmorph.prompt.AiPromptSupport;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.Test;

public class OpenAiCompatibleProviderSupportTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static final AiPromptSupport PROMPT_SUPPORT =
            new AiPromptSupport(CommonTestFixtures.createFilePromptDefinitions());

    private static final Function<String, @Nullable String> NO_ENVIRONMENT = name -> null;

    private static final AiMdHeader HEADER = new AiMdHeader(
            "Test.java",
            AiMdHeaderCodec.HEADER_VERSION_1_0,
            "00000000",
            "2026-10-28T00:00:00Z",
            "2026-10-28T00:00:00Z",
            "0.1.0-SNAPSHOT",
            "0.0.0",
            AiMdHeaderCodec.NODE_TYPE_FILE);

    private static final AiGenerationRequest REQUEST = new AiGenerationRequest(
            CommonTestFixtures.PROMPT_KEY_FILE_BODY, Paths.get("Test.java"), "class Test {}", HEADER);

    private static OpenAiCompatibleProviderSupport support(final LlamaCppJniConfig config) {
        return new OpenAiCompatibleProviderSupport(config, PROMPT_SUPPORT, NO_ENVIRONMENT);
    }

    private static OpenAiCompatibleProviderSupport support(
            final LlamaCppJniConfig config, final Map<String, String> environment) {
        return new OpenAiCompatibleProviderSupport(config, PROMPT_SUPPORT, environment::get);
    }

    private static JsonNode body(final LlamaCppJniConfig config) throws Exception {
        return MAPPER.readTree(support(config).requestBody(REQUEST));
    }

    // <editor-fold defaultstate="collapsed" desc="constructor">
    @Test
    public void constructor_nullArguments_throw() {
        final LlamaCppJniConfig config = LlamaCppJniConfig.builder("").build();
        assertThrows(
                NullPointerException.class,
                () -> new OpenAiCompatibleProviderSupport(null, PROMPT_SUPPORT, NO_ENVIRONMENT));
        assertThrows(
                NullPointerException.class, () -> new OpenAiCompatibleProviderSupport(config, null, NO_ENVIRONMENT));
        assertThrows(
                NullPointerException.class, () -> new OpenAiCompatibleProviderSupport(config, PROMPT_SUPPORT, null));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="requestBody">
    @Test
    public void requestBody_carriesTheSameMessagesAsTheJniProvider() throws Exception {
        // act
        final JsonNode body = body(LlamaCppJniConfig.builder("").build());

        // assert
        final JsonNode messages = body.get("messages");
        assertThat(messages.size(), is(2));
        assertThat(messages.get(0).get("role").asText(), is("system"));
        assertThat(
                messages.get(0).get("content").asText(),
                is(PROMPT_SUPPORT.systemPrompt(CommonTestFixtures.PROMPT_KEY_FILE_BODY)));
        assertThat(messages.get(1).get("role").asText(), is("user"));
        assertThat(
                messages.get(1).get("content").asText(),
                is(PROMPT_SUPPORT.userMessage(Paths.get("Test.java"), "class Test {}")));
        assertThat(body.get("stream").asBoolean(), is(false));
    }

    @Test
    public void requestBody_defaults_sendTheDefaultSamplingAndOmitTheUnsetOptionalFields() throws Exception {
        // act
        final JsonNode body = body(LlamaCppJniConfig.builder("").build());

        // assert
        assertThat(body.has("model"), is(false));
        assertThat(body.get("max_tokens").asInt(), is(128));
        assertThat(body.get("temperature").floatValue(), is(0.15f));
        assertThat(body.get("top_p").floatValue(), is(0.9f));
        assertThat(body.get("top_k").asInt(), is(40));
        assertThat(body.has("min_p"), is(false));
        assertThat(body.get("repetition_penalty").floatValue(), is(1.0f));
        assertThat(body.has("seed"), is(false));
        assertThat(body.has("stop"), is(false));
        assertThat(body.get("reasoning").get("effort").asText(), is("low"));
    }

    @Test
    public void requestBody_setValues_areAllSent() throws Exception {
        // arrange
        final LlamaCppJniConfig config = LlamaCppJniConfig.builder("")
                .remoteModel("openai/gpt-6-luna")
                .maxOutputTokens(256)
                .temperature(0.5f)
                .topP(0.8f)
                .topK(1)
                .minP(0.05f)
                .repeatPenalty(1.1f)
                .seed(0)
                .stopStrings(Arrays.asList("</s>", "END"))
                .reasoningEffort("high")
                .build();

        // act
        final JsonNode body = body(config);

        // assert -- top_k 1, seed 0 and min_p just above 0 sit on the inclusion boundaries
        assertThat(body.get("model").asText(), is("openai/gpt-6-luna"));
        assertThat(body.get("max_tokens").asInt(), is(256));
        assertThat(body.get("temperature").floatValue(), is(0.5f));
        assertThat(body.get("top_p").floatValue(), is(0.8f));
        assertThat(body.get("top_k").asInt(), is(1));
        assertThat(body.get("min_p").floatValue(), is(0.05f));
        assertThat(body.get("repetition_penalty").floatValue(), is(1.1f));
        assertThat(body.get("seed").asInt(), is(0));
        assertThat(body.get("stop").size(), is(2));
        assertThat(body.get("stop").get(0).asText(), is("</s>"));
        assertThat(body.get("stop").get(1).asText(), is("END"));
        assertThat(body.get("reasoning").get("effort").asText(), is("high"));
    }

    @Test
    public void requestBody_disabledValues_areOmitted() throws Exception {
        // arrange
        final LlamaCppJniConfig config = LlamaCppJniConfig.builder("")
                .remoteModel("   ")
                .topK(0)
                .seed(-1)
                .stopStrings(Collections.emptyList())
                .reasoningEffort("")
                .build();

        // act
        final JsonNode body = body(config);

        // assert
        assertThat(body.has("model"), is(false));
        assertThat(body.has("top_k"), is(false));
        assertThat(body.has("seed"), is(false));
        assertThat(body.has("stop"), is(false));
        assertThat(body.has("reasoning"), is(false));
    }

    @Test
    public void requestBody_default_doesNotStream() throws Exception {
        final JsonNode body = body(LlamaCppJniConfig.builder("").build());
        assertThat(body.get("stream").asBoolean(), is(false));
        assertThat(body.has("stream_options"), is(false));
    }

    @Test
    public void requestBody_stream_asksForTheEventStreamAndItsUsageChunk() throws Exception {
        final JsonNode body = body(LlamaCppJniConfig.builder("").stream(true).build());
        assertThat(body.get("stream").asBoolean(), is(true));
        assertThat(body.get("stream_options").get("include_usage").asBoolean(), is(true));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="accept / completionJson">
    @Test
    public void accept_followsTheStreamSwitch() {
        assertThat(support(LlamaCppJniConfig.builder("").build()).accept(), is("application/json"));
        assertThat(support(LlamaCppJniConfig.builder("").stream(true).build()).accept(), is("text/event-stream"));
    }

    @Test
    public void completionJson_notStreaming_returnsTheBodyUnchanged() throws Exception {
        // a body that is no event stream: assembling it would fail for want of [DONE]
        final String body = "{\"choices\":[]}";
        assertThat(support(LlamaCppJniConfig.builder("").build()).completionJson(body), is(body));
    }

    @Test
    public void completionJson_streaming_assemblesTheEvents() throws Exception {
        final String json = support(LlamaCppJniConfig.builder("").stream(true).build())
                .completionJson(
                        event("{\"choices\":[{\"delta\":{\"content\":\"hi\"},\"finish_reason\":\"stop\"}]}") + DONE);
        assertThat(
                json,
                is("{\"choices\":[{\"message\":{\"role\":\"assistant\",\"content\":\"hi\"},"
                        + "\"finish_reason\":\"stop\"}]}"));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="assembleStream">
    private static final String DONE = "data: [DONE]\n\n";

    private static String event(final String json) {
        return "data: " + json + "\n\n";
    }

    private static String token(final String content) {
        return event("{\"id\":\"c1\",\"object\":\"chat.completion.chunk\",\"choices\":[{\"index\":0,"
                + "\"delta\":{\"content\":" + content + "},\"finish_reason\":null}],\"usage\":null}");
    }

    private static ChatResponse assembled(final String sse) throws IOException {
        return new ChatResponseParser().parseResponse(OpenAiCompatibleProviderSupport.assembleStream(sse));
    }

    /** The shape of an OpenAI stream with {@code include_usage}, as OpenRouter and llama-server send it. */
    @Test
    public void assembleStream_openAiStream_joinsTheDeltasAndKeepsFinishReasonAndUsage() throws Exception {
        // arrange
        final String sse = event("{\"choices\":[{\"index\":0,\"delta\":{\"role\":\"assistant\",\"content\":\"\"},"
                        + "\"finish_reason\":null}]}")
                + token("\"A tiny \"")
                + token("\"test class.\"")
                + event("{\"choices\":[{\"index\":0,\"delta\":{},\"finish_reason\":\"length\"}]}")
                + event("{\"choices\":[],\"usage\":{\"prompt_tokens\":42,\"completion_tokens\":7,"
                        + "\"prompt_tokens_details\":{\"cached_tokens\":40}}}")
                + DONE;

        // act
        final ChatResponse response = assembled(sse);

        // assert
        assertThat(response.getFirstContent(), is("A tiny test class."));
        assertThat(response.getChoices().get(0).getFinishReason(), is("length"));
        assertThat(response.getChoices().get(0).getMessage().getRole(), is("assistant"));
        assertThat(response.getUsage().getPromptTokens(), is(42L));
        assertThat(response.getUsage().getCompletionTokens(), is(7L));
        assertThat(response.getUsage().getCachedTokens(), is(40L));
    }

    /**
     * What the GitHub Copilot API Gateway and jllama's {@code NativeServer} send besides data: comment
     * heartbeats, CRLF line ends, and {@code data:} without the space.
     */
    @Test
    public void assembleStream_commentsOtherFieldsAndCrLf_areSkipped() throws Exception {
        final String sse = ": ping\r\n\r\n"
                + "event: message\r\nid: 7\r\n"
                + "data:{\"choices\":[{\"delta\":{\"content\":\"ok\"},\"finish_reason\":\"stop\"}]}\r\n\r\n"
                + ": ping\r\n\r\n"
                + "data: [DONE]\r\n\r\n";
        final ChatResponse response = assembled(sse);
        assertThat(response.getFirstContent(), is("ok"));
        assertThat(response.getChoices().get(0).getFinishReason(), is("stop"));
    }

    @Test
    public void assembleStream_nullDeltasAndALaterNullFinishReason_changeNothing() throws Exception {
        final String sse = token("\"a\"")
                + token("null")
                + event("{\"choices\":[{\"delta\":{\"content\":\"b\"},\"finish_reason\":\"stop\"}]}")
                + token("null")
                + DONE;
        final ChatResponse response = assembled(sse);
        assertThat(response.getFirstContent(), is("ab"));
        assertThat(response.getChoices().get(0).getFinishReason(), is("stop"));
    }

    @Test
    public void assembleStream_aLaterChunkWithoutUsage_keepsTheUsage() throws Exception {
        final String sse = event("{\"choices\":[],\"usage\":{\"prompt_tokens\":3,\"completion_tokens\":2}}")
                + token("\"late\"")
                + DONE;
        final ChatResponse response = assembled(sse);
        assertThat(response.getUsage().getPromptTokens(), is(3L));
        assertThat(response.getUsage().getCompletionTokens(), is(2L));
    }

    @Test
    public void assembleStream_noUsageChunk_reportsNoUsageAndNoFinishReason() throws Exception {
        final String json = OpenAiCompatibleProviderSupport.assembleStream(token("\"x\"") + DONE);
        assertThat(
                json,
                is("{\"choices\":[{\"message\":{\"role\":\"assistant\",\"content\":\"x\"},"
                        + "\"finish_reason\":\"\"}]}"));
    }

    @Test
    public void assembleStream_afterDone_nothingIsRead() throws Exception {
        final ChatResponse response = assembled(token("\"kept\"") + DONE + "data: not json\n\n");
        assertThat(response.getFirstContent(), is("kept"));
    }

    @Test
    public void assembleStream_noDone_failsInsteadOfPassingForAShortAnswer() {
        final String sse = token("\"half\"");
        final IOException thrown =
                assertThrows(IOException.class, () -> OpenAiCompatibleProviderSupport.assembleStream(sse));
        assertThat(
                thrown.getMessage(),
                is("The stream ended without 'data: [DONE]' after " + sse.length() + " characters"));
    }

    @Test
    public void assembleStream_plainJsonAnswer_failsForWantOfDone() {
        // a server that ignores stream:true and answers with one JSON object
        assertThrows(
                IOException.class,
                () -> OpenAiCompatibleProviderSupport.assembleStream(
                        "{\"choices\":[{\"message\":{\"content\":\"x\"}}]}"));
    }

    @Test
    public void assembleStream_errorChunk_fails() {
        final String sse = token("\"partial\"")
                + event("{\"error\":{\"message\":\"Provider returned error\",\"code\":502}}")
                + DONE;
        final IOException thrown =
                assertThrows(IOException.class, () -> OpenAiCompatibleProviderSupport.assembleStream(sse));
        assertThat(
                thrown.getMessage(),
                is("The stream reported an error: {\"message\":\"Provider returned error\",\"code\":502}"));
    }

    @Test
    public void assembleStream_nullError_isNoError() throws Exception {
        final ChatResponse response =
                assembled(event("{\"error\":null,\"choices\":[{\"delta\":{\"content\":\"fine\"}}]}") + DONE);
        assertThat(response.getFirstContent(), is("fine"));
    }

    @Test
    public void assembleStream_malformedData_fails() {
        assertThrows(IOException.class, () -> OpenAiCompatibleProviderSupport.assembleStream("data: {oops\n\n" + DONE));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="endpoint">
    @Test
    public void endpoint_default_isOpenRouter() {
        assertThat(
                support(LlamaCppJniConfig.builder("").build()).endpoint(),
                is("https://openrouter.ai/api/v1/chat/completions"));
    }

    @Test
    public void endpoint_trailingSlashesAndBlanks_areRemoved() {
        final LlamaCppJniConfig config = LlamaCppJniConfig.builder("")
                .baseUrl(" http://127.0.0.1:8080/v1// ")
                .build();
        assertThat(support(config).endpoint(), is("http://127.0.0.1:8080/v1/chat/completions"));
    }

    @Test
    public void endpoint_httpsAndHttp_areAccepted() {
        assertThat(
                support(LlamaCppJniConfig.builder("").baseUrl("https://h/v1").build())
                        .endpoint(),
                is("https://h/v1/chat/completions"));
        assertThat(
                support(LlamaCppJniConfig.builder("").baseUrl("http://h/v1").build())
                        .endpoint(),
                is("http://h/v1/chat/completions"));
    }

    @Test
    public void endpoint_otherScheme_isRejected() {
        final OpenAiCompatibleProviderSupport support = support(
                LlamaCppJniConfig.builder("").baseUrl("file:///etc/passwd").build());
        final IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, support::endpoint);
        assertThat(thrown.getMessage(), is("baseUrl is not an http(s) URL: 'file:///etc/passwd'"));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="apiKey">
    @Test
    public void apiKey_variableSet_returnsTheTrimmedValue() {
        final Map<String, String> environment = new HashMap<>();
        environment.put("OPENROUTER_API_KEY", " sk-test \n");
        final LlamaCppJniConfig config =
                LlamaCppJniConfig.builder("").apiKeyEnv("OPENROUTER_API_KEY").build();
        assertThat(support(config, environment).apiKey(), is("sk-test"));
    }

    @Test
    public void apiKey_otherVariableNamed_readsThatOne() {
        final Map<String, String> environment = new HashMap<>();
        environment.put("OPENROUTER_API_KEY", "wrong");
        environment.put("MY_KEY", "right");
        final LlamaCppJniConfig config =
                LlamaCppJniConfig.builder("").apiKeyEnv("MY_KEY").build();
        assertThat(support(config, environment).apiKey(), is("right"));
    }

    @Test
    public void apiKey_variableUnset_returnsNull() {
        final LlamaCppJniConfig config =
                LlamaCppJniConfig.builder("").apiKeyEnv("OPENROUTER_API_KEY").build();
        assertThat(support(config).apiKey(), is(nullValue()));
    }

    @Test
    public void apiKey_variableBlank_returnsNull() {
        final Map<String, String> environment = new HashMap<>();
        environment.put("OPENROUTER_API_KEY", "  ");
        final LlamaCppJniConfig config =
                LlamaCppJniConfig.builder("").apiKeyEnv("OPENROUTER_API_KEY").build();
        assertThat(support(config, environment).apiKey(), is(nullValue()));
    }

    /**
     * The default names no variable, so a model that only points {@code baseUrl} at another server
     * cannot send it the OpenRouter key that happens to be in the environment.
     */
    @Test
    public void apiKeyFor_defaultConfigWithACustomHttpsEndpoint_sendsNoKey() throws Exception {
        final Map<String, String> environment = new HashMap<>();
        environment.put("OPENROUTER_API_KEY", "sk-or-test");
        final LlamaCppJniConfig config = LlamaCppJniConfig.builder("")
                .baseUrl("https://other.example/v1")
                .build();
        assertThat(
                support(config, environment).apiKeyFor("https://other.example/v1/chat/completions"), is(nullValue()));
    }

    @Test
    public void apiKey_noVariableNamed_returnsNullWithoutLookingAnythingUp() {
        // arrange -- an environment that would answer for any name, the empty one included
        final LlamaCppJniConfig config =
                LlamaCppJniConfig.builder("").apiKeyEnv("").build();
        final OpenAiCompatibleProviderSupport support =
                new OpenAiCompatibleProviderSupport(config, PROMPT_SUPPORT, name -> "any");

        // act + assert
        assertThat(support.apiKey(), is(nullValue()));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="apiKeyFor / isLoopbackHost">
    private static OpenAiCompatibleProviderSupport supportWithKey() {
        final Map<String, String> environment = new HashMap<>();
        environment.put("OPENROUTER_API_KEY", "sk-test");
        return support(
                LlamaCppJniConfig.builder("").apiKeyEnv("OPENROUTER_API_KEY").build(), environment);
    }

    @Test
    public void apiKeyFor_https_sendsTheKey() throws Exception {
        assertThat(supportWithKey().apiKeyFor("https://example.org/v1/chat/completions"), is("sk-test"));
    }

    @Test
    public void apiKeyFor_plainHttpToLoopback_sendsTheKey() throws Exception {
        final OpenAiCompatibleProviderSupport support = supportWithKey();
        assertThat(support.apiKeyFor("http://127.0.0.1:8080/v1/chat/completions"), is("sk-test"));
        assertThat(support.apiKeyFor("http://localhost:8080/v1/chat/completions"), is("sk-test"));
        assertThat(support.apiKeyFor("http://[::1]:8080/v1/chat/completions"), is("sk-test"));
    }

    @Test
    public void apiKeyFor_plainHttpToAnotherHost_refusesWithoutSendingTheKey() {
        final OpenAiCompatibleProviderSupport support = supportWithKey();
        final IOException thrown =
                assertThrows(IOException.class, () -> support.apiKeyFor("http://example.org/v1/chat/completions"));
        assertThat(
                thrown.getMessage(),
                is("Refusing to send the API key from OPENROUTER_API_KEY in cleartext to example.org: use an"
                        + " https baseUrl, a loopback server, or an empty apiKeyEnv for a server that needs no key"));
    }

    @Test
    public void apiKeyFor_plainHttpToAnotherHostWithoutKey_isFine() throws Exception {
        assertThat(
                support(LlamaCppJniConfig.builder("").build()).apiKeyFor("http://example.org/v1/chat/completions"),
                is(nullValue()));
    }

    @Test
    public void isLoopbackHost_onlyLoopbackLiteralsAndLocalhost() {
        assertThat(OpenAiCompatibleProviderSupport.isLoopbackHost("localhost"), is(true));
        assertThat(OpenAiCompatibleProviderSupport.isLoopbackHost("LocalHost"), is(false));
        assertThat(OpenAiCompatibleProviderSupport.isLoopbackHost("127.0.0.1"), is(true));
        assertThat(OpenAiCompatibleProviderSupport.isLoopbackHost("127.255.0.9"), is(true));
        assertThat(OpenAiCompatibleProviderSupport.isLoopbackHost("[::1]"), is(true));
        assertThat(OpenAiCompatibleProviderSupport.isLoopbackHost("127.evil.example"), is(false));
        assertThat(OpenAiCompatibleProviderSupport.isLoopbackHost("128.0.0.1"), is(false));
        assertThat(OpenAiCompatibleProviderSupport.isLoopbackHost("localhost.example"), is(false));
        assertThat(OpenAiCompatibleProviderSupport.isLoopbackHost("llama-server"), is(false));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="isSuccess">
    @Test
    public void isSuccess_onlyTheTwoHundreds() {
        assertThat(OpenAiCompatibleProviderSupport.isSuccess(199), is(false));
        assertThat(OpenAiCompatibleProviderSupport.isSuccess(200), is(true));
        assertThat(OpenAiCompatibleProviderSupport.isSuccess(299), is(true));
        assertThat(OpenAiCompatibleProviderSupport.isSuccess(300), is(false));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="errorMessage">
    @Test
    public void errorMessage_shortBody_isQuotedWhole() {
        final String body = repeat('x', OpenAiCompatibleProviderSupport.ERROR_BODY_EXCERPT);
        assertThat(
                OpenAiCompatibleProviderSupport.errorMessage("http://h/chat/completions", 401, body),
                is("http://h/chat/completions answered HTTP 401: " + body));
    }

    @Test
    public void errorMessage_longBody_isCutAtTheExcerptLength() {
        final String body = repeat('x', OpenAiCompatibleProviderSupport.ERROR_BODY_EXCERPT + 1);
        assertThat(
                OpenAiCompatibleProviderSupport.errorMessage("http://h", 500, body),
                is("http://h answered HTTP 500: " + repeat('x', OpenAiCompatibleProviderSupport.ERROR_BODY_EXCERPT)
                        + "..."));
    }

    private static String repeat(final char c, final int count) {
        final char[] chars = new char[count];
        Arrays.fill(chars, c);
        return new String(chars);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="timings">
    private static ChatResponse responseWith(final Usage usage) {
        final ChatChoice choice = new ChatChoice(0, new ChatMessage("assistant", "body"), "stop");
        return new ChatResponse("id", Collections.singletonList(choice), usage, null, "{}");
    }

    @Test
    public void timings_reportUsageAndTheWallClockDecodeRate() {
        // act
        final AiGenerationTimings timings =
                OpenAiCompatibleProviderSupport.timings("text", responseWith(new Usage(100, 50, 30)), 2.0d);

        // assert
        assertThat(timings.text(), is("text"));
        assertThat(timings.promptTokens(), is(100));
        assertThat(timings.cachedPromptTokens(), is(30));
        assertThat(timings.prefillTokensPerSecond(), is(0.0d));
        assertThat(timings.predictedTokens(), is(50));
        assertThat(timings.decodeTokensPerSecond(), is(25.0d));
    }

    @Test
    public void timings_zeroSeconds_reportNoRate() {
        final AiGenerationTimings timings =
                OpenAiCompatibleProviderSupport.timings("text", responseWith(new Usage(1, 5)), 0.0d);
        assertThat(timings.decodeTokensPerSecond(), is(0.0d));
    }
    // </editor-fold>
}
