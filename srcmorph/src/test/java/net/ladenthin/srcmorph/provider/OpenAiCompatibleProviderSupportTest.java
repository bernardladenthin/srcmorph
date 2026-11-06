// SPDX-FileCopyrightText: 2026 Bernard Ladenthin <bernard.ladenthin@gmail.com>
//
// SPDX-License-Identifier: Apache-2.0
package net.ladenthin.srcmorph.provider;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Paths;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import net.ladenthin.llama.json.ChatResponseParser;
import net.ladenthin.llama.value.ChatChoice;
import net.ladenthin.llama.value.ChatMessage;
import net.ladenthin.llama.value.ChatResponse;
import net.ladenthin.llama.value.Usage;
import net.ladenthin.srcmorph.CommonTestFixtures;
import net.ladenthin.srcmorph.document.AiMdHeader;
import net.ladenthin.srcmorph.document.AiMdHeaderCodec;
import net.ladenthin.srcmorph.prompt.GenerationRequest;
import net.ladenthin.srcmorph.prompt.PromptCatalog;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.Test;

public class OpenAiCompatibleProviderSupportTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static final PromptCatalog PROMPT_SUPPORT =
            new PromptCatalog(CommonTestFixtures.createFilePromptDefinitions());

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

    private static final GenerationRequest REQUEST =
            new GenerationRequest(CommonTestFixtures.PROMPT_KEY_FILE_BODY, Paths.get("Test.java"), "class Test {}");

    private static OpenAiCompatibleProviderSupport support(final ProviderConfig config) {
        return new OpenAiCompatibleProviderSupport(config, PROMPT_SUPPORT, NO_ENVIRONMENT);
    }

    private static OpenAiCompatibleProviderSupport support(
            final ProviderConfig config, final Map<String, String> environment) {
        return new OpenAiCompatibleProviderSupport(config, PROMPT_SUPPORT, environment::get);
    }

    private static JsonNode body(final ProviderConfig config) throws Exception {
        return MAPPER.readTree(support(config).requestBody(REQUEST));
    }

    // <editor-fold defaultstate="collapsed" desc="constructor">
    @Test
    public void constructor_nullArguments_throw() {
        // arrange
        final ProviderConfig config = ProviderConfig.builder().modelPath("").build();

        // act, assert
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
    public void requestBody_defaultConfig_carriesTheSameMessagesAsTheJniProvider() throws Exception {
        // act
        final JsonNode body = body(ProviderConfig.builder().modelPath("").build());

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
        final JsonNode body = body(ProviderConfig.builder().modelPath("").build());

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
        final ProviderConfig config = ProviderConfig.builder()
                .modelPath("")
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
        final ProviderConfig config = ProviderConfig.builder()
                .modelPath("")
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
        // act
        final JsonNode body = body(ProviderConfig.builder().modelPath("").build());

        // assert
        assertThat(body.get("stream").asBoolean(), is(false));
        assertThat(body.has("stream_options"), is(false));
    }

    @Test
    public void requestBody_stream_asksForTheEventStreamAndItsUsageChunk() throws Exception {
        // act
        final JsonNode body =
                body(ProviderConfig.builder().modelPath("").stream(true).build());

        // assert
        assertThat(body.get("stream").asBoolean(), is(true));
        assertThat(body.get("stream_options").get("include_usage").asBoolean(), is(true));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="accept">
    @Test
    public void accept_streamOnOrOff_followsTheStreamSwitch() {
        // act, assert
        assertThat(support(ProviderConfig.builder().modelPath("").build()).accept(), is("application/json"));
        assertThat(
                support(ProviderConfig.builder().modelPath("").stream(true).build())
                        .accept(),
                is("text/event-stream"));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="completionJson">
    @Test
    public void completionJson_notStreaming_returnsTheBodyUnchanged() throws Exception {
        // a body that is no event stream: assembling it would fail for want of [DONE]
        // arrange
        final String body = "{\"choices\":[]}";

        // act, assert
        assertThat(support(ProviderConfig.builder().modelPath("").build()).completionJson(body), is(body));
    }

    @Test
    public void completionJson_streaming_assemblesTheEvents() throws Exception {
        // act
        final String json = support(
                        ProviderConfig.builder().modelPath("").stream(true).build())
                .completionJson(
                        event("{\"choices\":[{\"delta\":{\"content\":\"hi\"},\"finish_reason\":\"stop\"}]}") + DONE);

        // assert
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
        // arrange
        final String sse = ": ping\r\n\r\n"
                + "event: message\r\nid: 7\r\n"
                + "data:{\"choices\":[{\"delta\":{\"content\":\"ok\"},\"finish_reason\":\"stop\"}]}\r\n\r\n"
                + ": ping\r\n\r\n"
                + "data: [DONE]\r\n\r\n";

        // act
        final ChatResponse response = assembled(sse);

        // assert
        assertThat(response.getFirstContent(), is("ok"));
        assertThat(response.getChoices().get(0).getFinishReason(), is("stop"));
    }

    @Test
    public void assembleStream_nullDeltasAndALaterNullFinishReason_changeNothing() throws Exception {
        // arrange
        final String sse = token("\"a\"")
                + token("null")
                + event("{\"choices\":[{\"delta\":{\"content\":\"b\"},\"finish_reason\":\"stop\"}]}")
                + token("null")
                + DONE;

        // act
        final ChatResponse response = assembled(sse);

        // assert
        assertThat(response.getFirstContent(), is("ab"));
        assertThat(response.getChoices().get(0).getFinishReason(), is("stop"));
    }

    @Test
    public void assembleStream_aLaterChunkWithoutUsage_keepsTheUsage() throws Exception {
        // arrange
        final String sse = event("{\"choices\":[],\"usage\":{\"prompt_tokens\":3,\"completion_tokens\":2}}")
                + token("\"late\"")
                + DONE;

        // act
        final ChatResponse response = assembled(sse);

        // assert
        assertThat(response.getUsage().getPromptTokens(), is(3L));
        assertThat(response.getUsage().getCompletionTokens(), is(2L));
    }

    @Test
    public void assembleStream_noUsageChunk_reportsNoUsageAndNoFinishReason() throws Exception {
        // act
        final String json = OpenAiCompatibleProviderSupport.assembleStream(token("\"x\"") + DONE);

        // assert
        assertThat(
                json,
                is("{\"choices\":[{\"message\":{\"role\":\"assistant\",\"content\":\"x\"},"
                        + "\"finish_reason\":\"\"}]}"));
    }

    @Test
    public void assembleStream_afterDone_nothingIsRead() throws Exception {
        // act
        final ChatResponse response = assembled(token("\"kept\"") + DONE + "data: not json\n\n");

        // assert
        assertThat(response.getFirstContent(), is("kept"));
    }

    @Test
    public void assembleStream_noDone_failsInsteadOfPassingForAShortAnswer() {
        // arrange
        final String sse = token("\"half\"");

        // act
        final IOException thrown =
                assertThrows(IOException.class, () -> OpenAiCompatibleProviderSupport.assembleStream(sse));

        // assert
        assertThat(
                thrown.getMessage(),
                is("The stream ended without 'data: [DONE]' after " + sse.length() + " characters"));
    }

    @Test
    public void assembleStream_plainJsonAnswer_failsForWantOfDone() {
        // a server that ignores stream:true and answers with one JSON object
        // act, assert
        assertThrows(
                IOException.class,
                () -> OpenAiCompatibleProviderSupport.assembleStream(
                        "{\"choices\":[{\"message\":{\"content\":\"x\"}}]}"));
    }

    @Test
    public void assembleStream_errorChunk_fails() {
        // arrange
        final String sse = token("\"partial\"")
                + event("{\"error\":{\"message\":\"Provider returned error\",\"code\":502}}")
                + DONE;

        // act
        final IOException thrown =
                assertThrows(IOException.class, () -> OpenAiCompatibleProviderSupport.assembleStream(sse));

        // assert
        assertThat(
                thrown.getMessage(),
                is("The stream reported an error: {\"message\":\"Provider returned error\",\"code\":502}"));
    }

    @Test
    public void assembleStream_nullError_isNoError() throws Exception {
        // act
        final ChatResponse response =
                assembled(event("{\"error\":null,\"choices\":[{\"delta\":{\"content\":\"fine\"}}]}") + DONE);

        // assert
        assertThat(response.getFirstContent(), is("fine"));
    }

    @Test
    public void assembleStream_malformedData_fails() {
        // act, assert
        assertThrows(IOException.class, () -> OpenAiCompatibleProviderSupport.assembleStream("data: {oops\n\n" + DONE));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="endpoint">
    @Test
    public void endpoint_default_isOpenRouter() {
        // act, assert
        assertThat(
                support(ProviderConfig.builder().modelPath("").build()).endpoint(),
                is("https://openrouter.ai/api/v1/chat/completions"));
    }

    @Test
    public void endpoint_trailingSlashesAndBlanks_areRemoved() {
        // arrange
        final ProviderConfig config = ProviderConfig.builder()
                .modelPath("")
                .baseUrl(" http://127.0.0.1:8080/v1// ")
                .build();

        // act, assert
        assertThat(support(config).endpoint(), is("http://127.0.0.1:8080/v1/chat/completions"));
    }

    @Test
    public void endpoint_httpsAndHttp_areAccepted() {
        // act, assert
        assertThat(
                support(ProviderConfig.builder()
                                .modelPath("")
                                .baseUrl("https://h/v1")
                                .build())
                        .endpoint(),
                is("https://h/v1/chat/completions"));
        assertThat(
                support(ProviderConfig.builder()
                                .modelPath("")
                                .baseUrl("http://h/v1")
                                .build())
                        .endpoint(),
                is("http://h/v1/chat/completions"));
    }

    @Test
    public void endpoint_otherScheme_isRejected() {
        // arrange
        final OpenAiCompatibleProviderSupport support = support(ProviderConfig.builder()
                .modelPath("")
                .baseUrl("file:///etc/passwd")
                .build());

        // act
        final IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, support::endpoint);

        // assert
        assertThat(thrown.getMessage(), is("baseUrl is not an http(s) URL: 'file:///etc/passwd'"));
    }

    @Test
    public void endpoint_otherSchemeWithUserInfo_isRejectedWithoutTheUserInfo() {
        // arrange
        final OpenAiCompatibleProviderSupport support = support(ProviderConfig.builder()
                .modelPath("")
                .baseUrl("ftp://u:secret@h/v1")
                .build());

        // act
        final IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, support::endpoint);

        // assert
        assertThat(thrown.getMessage(), is("baseUrl is not an http(s) URL: 'ftp://***@h/v1'"));
    }

    /** srcmorph never sends a URL's user info, so a key written there would not authenticate. */
    @Test
    public void endpoint_userInfo_isRejectedWithoutPrintingIt() {
        // arrange
        final OpenAiCompatibleProviderSupport support = support(ProviderConfig.builder()
                .modelPath("")
                .baseUrl("https://user:secret@h/v1")
                .build());

        // act
        final IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, support::endpoint);

        // assert
        assertThat(
                thrown.getMessage(),
                is("its baseUrl holds an @ ('https://***@h/v1'), read as user info, which srcmorph never sends:"
                        + " put the key in the variable apiKeyEnv names"));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="apiKey">
    @Test
    public void apiKey_variableSet_returnsTheTrimmedValue() {
        // arrange
        final Map<String, String> environment = new HashMap<>();
        environment.put("OPENROUTER_API_KEY", " sk-test \n");
        final ProviderConfig config = ProviderConfig.builder()
                .modelPath("")
                .apiKeyEnv("OPENROUTER_API_KEY")
                .build();

        // act, assert
        assertThat(support(config, environment).apiKey(), is("sk-test"));
    }

    @Test
    public void apiKey_otherVariableNamed_readsThatOne() {
        // arrange
        final Map<String, String> environment = new HashMap<>();
        environment.put("OPENROUTER_API_KEY", "wrong");
        environment.put("MY_KEY", "right");
        final ProviderConfig config =
                ProviderConfig.builder().modelPath("").apiKeyEnv("MY_KEY").build();

        // act, assert
        assertThat(support(config, environment).apiKey(), is("right"));
    }

    @Test
    public void apiKey_variableUnset_returnsNull() {
        // arrange
        final ProviderConfig config = ProviderConfig.builder()
                .modelPath("")
                .apiKeyEnv("OPENROUTER_API_KEY")
                .build();

        // act, assert
        assertThat(support(config).apiKey(), is(nullValue()));
    }

    @Test
    public void apiKey_variableBlank_returnsNull() {
        // arrange
        final Map<String, String> environment = new HashMap<>();
        environment.put("OPENROUTER_API_KEY", "  ");
        final ProviderConfig config = ProviderConfig.builder()
                .modelPath("")
                .apiKeyEnv("OPENROUTER_API_KEY")
                .build();

        // act, assert
        assertThat(support(config, environment).apiKey(), is(nullValue()));
    }

    @Test
    public void apiKey_noVariableNamed_returnsNullWithoutLookingAnythingUp() {
        // arrange -- an environment that would answer for any name, the empty one included
        final ProviderConfig config =
                ProviderConfig.builder().modelPath("").apiKeyEnv("").build();
        final OpenAiCompatibleProviderSupport support =
                new OpenAiCompatibleProviderSupport(config, PROMPT_SUPPORT, name -> "any");

        // act, assert
        assertThat(support.apiKey(), is(nullValue()));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="apiKeyFor">
    private static OpenAiCompatibleProviderSupport supportWithKey() {
        final Map<String, String> environment = new HashMap<>();
        environment.put("OPENROUTER_API_KEY", "sk-test");
        return support(
                ProviderConfig.builder()
                        .modelPath("")
                        .apiKeyEnv("OPENROUTER_API_KEY")
                        .build(),
                environment);
    }

    /**
     * The default names no variable, so a model that only points {@code baseUrl} at another server
     * cannot send it the OpenRouter key that happens to be in the environment.
     */
    @Test
    public void apiKeyFor_defaultConfigWithACustomHttpsEndpoint_sendsNoKey() throws Exception {
        // arrange
        final Map<String, String> environment = new HashMap<>();
        environment.put("OPENROUTER_API_KEY", "sk-or-test");
        final ProviderConfig config = ProviderConfig.builder()
                .modelPath("")
                .baseUrl("https://other.example/v1")
                .build();

        // act, assert
        assertThat(
                support(config, environment).apiKeyFor("https://other.example/v1/chat/completions"), is(nullValue()));
    }

    @Test
    public void apiKeyFor_https_sendsTheKey() throws Exception {
        // act, assert
        assertThat(supportWithKey().apiKeyFor("https://example.org/v1/chat/completions"), is("sk-test"));
    }

    @Test
    public void apiKeyFor_plainHttpToLoopback_sendsTheKey() throws Exception {
        // arrange
        final OpenAiCompatibleProviderSupport support = supportWithKey();

        // act, assert
        assertThat(support.apiKeyFor("http://127.0.0.1:8080/v1/chat/completions"), is("sk-test"));
        assertThat(support.apiKeyFor("http://localhost:8080/v1/chat/completions"), is("sk-test"));
        assertThat(support.apiKeyFor("http://[::1]:8080/v1/chat/completions"), is("sk-test"));
    }

    @Test
    public void apiKeyFor_plainHttpToAnotherHost_refusesWithoutSendingTheKey() {
        // arrange
        final OpenAiCompatibleProviderSupport support = supportWithKey();

        // act
        final IOException thrown =
                assertThrows(IOException.class, () -> support.apiKeyFor("http://example.org/v1/chat/completions"));

        // assert
        assertThat(
                thrown.getMessage(),
                is("Refusing to send the API key from OPENROUTER_API_KEY in cleartext to example.org: use an"
                        + " https baseUrl, a loopback server, or an empty apiKeyEnv for a server that needs no key"));
    }

    @Test
    public void apiKeyFor_plainHttpToAnotherHostWithoutKey_isFine() throws Exception {
        // act, assert
        assertThat(
                support(ProviderConfig.builder().modelPath("").build())
                        .apiKeyFor("http://example.org/v1/chat/completions"),
                is(nullValue()));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="isLoopbackHost">
    @Test
    public void isLoopbackHost_variousHosts_trueOnlyForLoopbackLiteralsAndLocalhost() {
        // act, assert
        assertThat(OpenAiCompatibleProviderSupport.isLoopbackHost("localhost"), is(true));
        assertThat(OpenAiCompatibleProviderSupport.isLoopbackHost("LocalHost"), is(true));
        assertThat(OpenAiCompatibleProviderSupport.isLoopbackHost("[0:0:0:0:0:0:0:1]"), is(true));
        assertThat(OpenAiCompatibleProviderSupport.isLoopbackHost("[::2]"), is(false));
        assertThat(OpenAiCompatibleProviderSupport.isLoopbackHost("[1:2:3]"), is(false));
        assertThat(OpenAiCompatibleProviderSupport.isLoopbackHost("[::1:::]"), is(false));
        assertThat(OpenAiCompatibleProviderSupport.isLoopbackHost("127.0.0.1"), is(true));
        assertThat(OpenAiCompatibleProviderSupport.isLoopbackHost("127.255.0.9"), is(true));
        assertThat(OpenAiCompatibleProviderSupport.isLoopbackHost("[::1]"), is(true));
        assertThat(OpenAiCompatibleProviderSupport.isLoopbackHost("127.evil.example"), is(false));
        assertThat(OpenAiCompatibleProviderSupport.isLoopbackHost("128.0.0.1"), is(false));
        assertThat(OpenAiCompatibleProviderSupport.isLoopbackHost("localhost.example"), is(false));
        assertThat(OpenAiCompatibleProviderSupport.isLoopbackHost("llama-server"), is(false));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="keyProblem">
    private static final Function<String, @Nullable String> KEY_SET =
            name -> "OPENROUTER_API_KEY".equals(name) ? "k" : null;

    /** Found before any request, with or without a key variable, and named without the user info. */
    @Test
    public void keyProblem_userInfo_isNamedMasked() {
        // act, assert
        assertThat(
                OpenAiCompatibleProviderSupport.keyProblem(" https://u:secret@openrouter.ai/api/v1 ", " ", KEY_SET),
                is("its baseUrl holds an @ ('https://***@openrouter.ai/api/v1'), read as user info, which"
                        + " srcmorph never sends: put the key in the variable apiKeyEnv names"));
    }

    @Test
    public void keyProblem_query_isNamedWithoutIt() {
        // act
        final String problem = OpenAiCompatibleProviderSupport.keyProblem("https://gw/v1?api-key=secret", " ", KEY_SET);

        // assert: the part before the query only
        assertThat(
                problem,
                is("its baseUrl has a query or a fragment ('https://gw/v1' followed by '?...'), which"
                        + " would stand in front of the path srcmorph appends: give the path only, and the key in the"
                        + " variable apiKeyEnv names"));
    }

    @Test
    public void queryProblem_fragmentBeforeAQuestionMark_namesTheFragment() {
        // act, assert: the first of the two ends the shown part
        assertThat(
                OpenAiCompatibleProviderSupport.queryProblem("https://gw/v1#a?b"),
                containsString("'https://gw/v1' followed by '#...'"));
        assertThat(
                OpenAiCompatibleProviderSupport.queryProblem("https://gw/v1?a#b"),
                containsString("'https://gw/v1' followed by '?...'"));
        assertThat(
                OpenAiCompatibleProviderSupport.queryProblem("https://gw/v1#"), containsString("followed by '#...'"));
        assertThat(OpenAiCompatibleProviderSupport.queryProblem("https://gw/v1"), is(nullValue()));
    }

    @Test
    public void keyProblem_aPasswordWithAQuestionMark_isUserInfoNotAQuery() {
        // act
        final String problem =
                OpenAiCompatibleProviderSupport.keyProblem("https://alice:s3cr?t@gw.example.com/v1", " ", KEY_SET);

        // assert: refused as user info, and nothing of the password shown
        assertThat(problem, containsString("holds an @ ('https://***@gw.example.com/v1')"));
    }

    @Test
    public void keyProblem_aPasswordWithASlash_isRefusedWithoutShowingIt() {
        // act
        final String problem =
                OpenAiCompatibleProviderSupport.keyProblem("https://alice:pa/ss@gw.example.com/v1", " ", KEY_SET);

        // assert
        assertThat(problem, containsString("its baseUrl holds an @ ('https://***')"));
        assertThat(problem.contains("alice"), is(false));
    }

    /** {@code new URL} reads the part before the slash as host {@code u} and port {@code 123}. */
    @Test
    public void keyProblem_aPasswordWithASlashThatParsesAsAPort_isRefusedWithoutShowingIt() {
        // act
        final String problem = OpenAiCompatibleProviderSupport.keyProblem("https://u:123/abc@host/v1", " ", KEY_SET);

        // assert
        assertThat(
                problem,
                is("its baseUrl holds an @ ('https://***'), read as user info, which srcmorph never sends: put the"
                        + " key in the variable apiKeyEnv names"));
    }

    @Test
    public void keyProblem_anAtInThePath_isRefused() {
        // act
        final String problem = OpenAiCompatibleProviderSupport.keyProblem("https://gw/v1/@scope", " ", KEY_SET);

        // assert
        assertThat(problem, containsString("its baseUrl holds an @ ('https://***')"));
    }

    @Test
    public void userInfoProblem_anAtAtTheStart_isNamed() {
        // act, assert: the bound of the search
        assertThat(OpenAiCompatibleProviderSupport.userInfoProblem("@x"), containsString("holds an @ ('@x')"));
        assertThat(OpenAiCompatibleProviderSupport.userInfoProblem("x"), is(nullValue()));
    }

    @Test
    public void malformedProblem_aUrl_isNone() {
        // act, assert
        assertThat(OpenAiCompatibleProviderSupport.malformedProblem("https://gw:8443/v1"), is(nullValue()));
        assertThat(
                OpenAiCompatibleProviderSupport.malformedProblem("no url"), is("its baseUrl is not a URL: 'no url'"));
        assertThat(
                OpenAiCompatibleProviderSupport.malformedProblem("https:///v1"),
                is("its baseUrl is not a URL: 'https:///v1'"));
    }

    @Test
    public void endpoint_aPasswordWithASlash_isRefused() {
        // arrange
        final OpenAiCompatibleProviderSupport support = support(ProviderConfig.builder()
                .modelPath("m")
                .baseUrl("https://alice:pa/ss@gw/v1")
                .build());

        // act
        final IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, support::endpoint);

        // assert
        assertThat(thrown.getMessage(), containsString("holds an @ ('https://***')"));
    }

    @Test
    public void modelsEndpoint_aPasswordWithASlashThatParsesAsAPort_isRefusedWithoutShowingIt() {
        // arrange: new URL reads it as host u, port 123
        final OpenAiCompatibleProviderSupport support = support(ProviderConfig.builder()
                .modelPath("m")
                .baseUrl("https://u:123/abc@host/v1")
                .build());

        // act
        final IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, support::modelsEndpoint);

        // assert
        assertThat(thrown.getMessage(), containsString("holds an @ ('https://***')"));
        assertThat(thrown.getMessage().contains("123"), is(false));
    }

    @Test
    public void queryProblem_markAtTheStart_showsNothingBeforeIt() {
        // act, assert: the first character is a mark, of either kind
        assertThat(OpenAiCompatibleProviderSupport.queryProblem("?x"), containsString("('' followed by '?...')"));
        assertThat(OpenAiCompatibleProviderSupport.queryProblem("#x?y"), containsString("('' followed by '#...')"));
    }

    @Test
    public void endpoint_baseUrlWithAQuery_isRefused() {
        // arrange
        final OpenAiCompatibleProviderSupport support = support(ProviderConfig.builder()
                .modelPath("m")
                .baseUrl("https://gw/v1?api-version=1")
                .build());

        // act
        final IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, support::endpoint);

        // assert
        assertThat(thrown.getMessage(), containsString("query or a fragment"));
    }

    @Test
    public void keyProblem_noApiKeyEnv_isNone() {
        // act, assert
        assertThat(OpenAiCompatibleProviderSupport.keyProblem("http://example.org/v1", " ", KEY_SET), is(nullValue()));
    }

    @Test
    public void keyProblem_unsetOrBlankVariable_isNamed() {
        // act, assert
        assertThat(
                OpenAiCompatibleProviderSupport.keyProblem("https://openrouter.ai/api/v1", " OTHER_KEY ", KEY_SET),
                is("its apiKeyEnv names OTHER_KEY, which is not set in this process's environment"));
        assertThat(
                OpenAiCompatibleProviderSupport.keyProblem("https://openrouter.ai/api/v1", "BLANK", name -> " "),
                is("its apiKeyEnv names BLANK, which is not set in this process's environment"));
    }

    @Test
    public void keyProblem_setKeyOverHttpsOrToLoopback_isNone() {
        // act, assert
        assertThat(
                OpenAiCompatibleProviderSupport.keyProblem(
                        "https://openrouter.ai/api/v1", "OPENROUTER_API_KEY", KEY_SET),
                is(nullValue()));
        assertThat(
                OpenAiCompatibleProviderSupport.keyProblem("http://127.0.0.1:8080/v1", "OPENROUTER_API_KEY", KEY_SET),
                is(nullValue()));
    }

    @Test
    public void keyProblem_setKeyInCleartextToAnotherHost_isNamed() {
        // act, assert
        assertThat(
                OpenAiCompatibleProviderSupport.keyProblem("http://example.org:8080/v1", "OPENROUTER_API_KEY", KEY_SET),
                is("its key from OPENROUTER_API_KEY would go in cleartext to example.org; use an https baseUrl,"
                        + " a loopback server, or an empty apiKeyEnv"));
    }

    @Test
    public void keyProblem_httpBaseUrlThatIsNoUrl_isNamed() {
        // act, assert
        assertThat(
                OpenAiCompatibleProviderSupport.keyProblem("http://[bad/v1", "OPENROUTER_API_KEY", KEY_SET),
                is("its baseUrl is not a URL: 'http://[bad/v1'"));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="isRetryable">
    @Test
    public void isRetryable_statusCodes_trueForTimeoutRateLimitAndServerErrors() {
        // act, assert
        assertThat(OpenAiCompatibleProviderSupport.isRetryable(408, ""), is(true));
        assertThat(OpenAiCompatibleProviderSupport.isRetryable(429, "{\"error\":\"rate limited\"}"), is(true));
        assertThat(OpenAiCompatibleProviderSupport.isRetryable(500, ""), is(true));
        assertThat(OpenAiCompatibleProviderSupport.isRetryable(502, ""), is(true));
        assertThat(OpenAiCompatibleProviderSupport.isRetryable(504, ""), is(true));
        assertThat(OpenAiCompatibleProviderSupport.isRetryable(599, ""), is(true));
        assertThat(OpenAiCompatibleProviderSupport.isRetryable(499, ""), is(false));
        assertThat(OpenAiCompatibleProviderSupport.isRetryable(600, ""), is(false));
        assertThat(OpenAiCompatibleProviderSupport.isRetryable(400, ""), is(false));
        assertThat(OpenAiCompatibleProviderSupport.isRetryable(401, ""), is(false));
        assertThat(OpenAiCompatibleProviderSupport.isRetryable(413, ""), is(false));
    }

    @Test
    public void isRetryable_errorsARetryCannotChange_returnsFalse() {
        // act, assert
        assertThat(OpenAiCompatibleProviderSupport.isRetryable(501, ""), is(false));
        assertThat(OpenAiCompatibleProviderSupport.isRetryable(505, ""), is(false));
        assertThat(
                OpenAiCompatibleProviderSupport.isRetryable(429, "{\"error\":{\"code\":\"insufficient_quota\"}}"),
                is(false));
        assertThat(
                OpenAiCompatibleProviderSupport.isRetryable(
                        429,
                        "You exceeded your current quota, please check your plan; \"code\":\"insufficient_quota\""),
                is(false));
        assertThat(
                OpenAiCompatibleProviderSupport.isRetryable(429, "{\"error\":{\"message\":\"Insufficient credits\"}}"),
                is(false));
    }

    @Test
    public void isRetryable_geminisQuota_perMinuteWaitsPerDayDoesNot() {
        // arrange
        final String perMinute = "You exceeded your current quota, please check your plan. quotaId:"
                + " GenerateRequestsPerMinutePerProjectPerModel";
        final String perDay = "You exceeded your current quota, please check your plan. quotaId:"
                + " GenerateRequestsPerDayPerProjectPerModel";

        // act, assert
        assertThat(OpenAiCompatibleProviderSupport.isRetryable(429, perMinute), is(true));
        assertThat(OpenAiCompatibleProviderSupport.isRetryable(429, perDay), is(false));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="isContextOverflow">
    @Test
    public void isContextOverflow_statusAndBody_trueForPayloadTooLargeOrABadRequestThatSaysSo() {
        // act, assert
        assertThat(OpenAiCompatibleProviderSupport.isContextOverflow(413, ""), is(true));
        assertThat(
                OpenAiCompatibleProviderSupport.isContextOverflow(
                        400, "{\"error\":\"This model's Maximum Context length is 131072\"}"),
                is(true));
        assertThat(OpenAiCompatibleProviderSupport.isContextOverflow(400, "exceed the context window"), is(true));
        assertThat(
                OpenAiCompatibleProviderSupport.isContextOverflow(
                        400, "the request exceeds the available context size, try increasing it"),
                is(true));
        assertThat(
                OpenAiCompatibleProviderSupport.isContextOverflow(
                        400, "The input token count (1200000) exceeds the maximum number of tokens allowed"),
                is(true));
        assertThat(OpenAiCompatibleProviderSupport.isContextOverflow(400, "unknown model"), is(false));
        assertThat(OpenAiCompatibleProviderSupport.isContextOverflow(500, "context length"), is(false));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="retryDelayMillis">
    @Test
    public void retryDelayMillis_retryAfterSeconds_isHonouredUpToItsCap() {
        // act, assert
        assertThat(OpenAiCompatibleProviderSupport.retryDelayMillis(" 2 ", 1, 0.9d, 0L), is(2_000L));
        assertThat(OpenAiCompatibleProviderSupport.retryDelayMillis("0", 3, 0.0d, 0L), is(0L));
        assertThat(
                OpenAiCompatibleProviderSupport.retryDelayMillis("100000", 1, 0.0d, 0L),
                is(OpenAiCompatibleProviderSupport.MAX_RETRY_AFTER_MILLIS));
        assertThat(
                OpenAiCompatibleProviderSupport.retryDelayMillis("299", 1, 0.0d, 0L),
                is(OpenAiCompatibleProviderSupport.MAX_RETRY_AFTER_MILLIS - 1_000L));
        // a number of seconds of any length is a long wait, not the short backoff
        assertThat(
                OpenAiCompatibleProviderSupport.retryDelayMillis("99999999999999999999", 1, 0.0d, 0L),
                is(OpenAiCompatibleProviderSupport.MAX_RETRY_AFTER_MILLIS));
    }

    @Test
    public void retryDelayMillis_aRetryAfterThatIsNoNumber_takesTheBackoff() {
        // act, assert
        assertThat(OpenAiCompatibleProviderSupport.retryDelayMillis("", 1, 0.0d, 0L), is(1_000L));
        assertThat(OpenAiCompatibleProviderSupport.retryDelayMillis("-1", 1, 0.0d, 0L), is(1_000L));
        assertThat(OpenAiCompatibleProviderSupport.retryDelayMillis("1.5", 1, 0.0d, 0L), is(1_000L));
        assertThat(OpenAiCompatibleProviderSupport.retryDelayMillis("9a", 1, 0.0d, 0L), is(1_000L));
    }

    @Test
    public void retryDelayMillis_withoutRetryAfter_doublesUpToItsCapPlusJitter() {
        // act, assert
        assertThat(OpenAiCompatibleProviderSupport.retryDelayMillis(null, 1, 0.0d, 0L), is(1_000L));
        assertThat(OpenAiCompatibleProviderSupport.retryDelayMillis(null, 2, 0.0d, 0L), is(2_000L));
        assertThat(OpenAiCompatibleProviderSupport.retryDelayMillis(null, 3, 0.5d, 0L), is(4_500L));
        assertThat(
                OpenAiCompatibleProviderSupport.retryDelayMillis("not a date", 7, 0.0d, 0L),
                is(OpenAiCompatibleProviderSupport.MAX_RETRY_BACKOFF_MILLIS));
        assertThat(
                OpenAiCompatibleProviderSupport.retryDelayMillis(null, 99, 0.0d, 0L),
                is(OpenAiCompatibleProviderSupport.MAX_RETRY_BACKOFF_MILLIS));
    }

    @Test
    public void retryDelayMillis_aRetryAfterDate_waitsUntilThen() {
        // arrange
        final long at = ZonedDateTime.parse("Wed, 21 Oct 2026 07:28:00 GMT", DateTimeFormatter.RFC_1123_DATE_TIME)
                .toInstant()
                .toEpochMilli();

        // act, assert
        assertThat(
                OpenAiCompatibleProviderSupport.retryDelayMillis(
                        "Wed, 21 Oct 2026 07:28:00 GMT", 1, 0.0d, at - 30_000L),
                is(30_000L));
        // a date in the past waits nothing, one far ahead the longest honoured wait
        assertThat(
                OpenAiCompatibleProviderSupport.retryDelayMillis("Wed, 21 Oct 2026 07:28:00 GMT", 1, 0.0d, at + 1L),
                is(0L));
        assertThat(
                OpenAiCompatibleProviderSupport.retryDelayMillis("Wed, 21 Oct 2026 07:28:00 GMT", 1, 0.0d, 0L),
                is(OpenAiCompatibleProviderSupport.MAX_RETRY_AFTER_MILLIS));
        assertThat(
                OpenAiCompatibleProviderSupport.retryDelayMillis(
                        "Wed, 21 Oct 2026 07:28:00 GMT",
                        1,
                        0.0d,
                        at - OpenAiCompatibleProviderSupport.MAX_RETRY_AFTER_MILLIS),
                is(OpenAiCompatibleProviderSupport.MAX_RETRY_AFTER_MILLIS));
    }

    @Test
    public void retryDelayMillis_theObsoleteHttpDateForms_areRead() {
        // arrange: the same instant as an rfc850-date and an asctime-date
        final long at = ZonedDateTime.parse("Wed, 21 Oct 2026 07:28:00 GMT", DateTimeFormatter.RFC_1123_DATE_TIME)
                .toInstant()
                .toEpochMilli();

        // act, assert
        assertThat(
                OpenAiCompatibleProviderSupport.retryDelayMillis(
                        "Wednesday, 21-Oct-26 07:28:00 GMT", 1, 0.0d, at - 30_000L),
                is(30_000L));
        assertThat(
                OpenAiCompatibleProviderSupport.retryDelayMillis("Wed Oct 21 07:28:00 2026", 1, 0.0d, at - 30_000L),
                is(30_000L));
        // a one-digit day is padded with a space
        final long sixth = at - 15L * 24 * 3_600_000L;
        assertThat(
                OpenAiCompatibleProviderSupport.retryDelayMillis("Tue Oct  6 07:28:00 2026", 1, 0.0d, sixth - 1_000L),
                is(1_000L));
    }

    @Test
    public void retryDelayMillis_anRfc850YearMoreThanFiftyYearsAhead_isOfThePastCentury() {
        // arrange: now is 2026-10-21 07:28:00 UTC
        final long now = ZonedDateTime.parse("Wed, 21 Oct 2026 07:28:00 GMT", DateTimeFormatter.RFC_1123_DATE_TIME)
                .toInstant()
                .toEpochMilli();

        // act, assert: 94 is 1994, in the past, so the retry waits nothing
        assertThat(
                OpenAiCompatibleProviderSupport.retryDelayMillis("Sunday, 06-Nov-94 08:49:37 GMT", 1, 0.0d, now),
                is(0L));
        // exactly fifty years ahead stays ahead, and waits the cap
        assertThat(
                OpenAiCompatibleProviderSupport.retryDelayMillis("Wednesday, 21-Oct-76 07:28:00 GMT", 1, 0.0d, now),
                is(OpenAiCompatibleProviderSupport.MAX_RETRY_AFTER_MILLIS));
        // a second later is of the past century, whose weekday it is checked against: 1976-10-21 was a Thursday
        assertThat(
                OpenAiCompatibleProviderSupport.retryDelayMillis("Thursday, 21-Oct-76 07:28:01 GMT", 1, 0.0d, now),
                is(0L));
        assertThat(
                OpenAiCompatibleProviderSupport.retryDelayMillis("Wednesday, 21-Oct-76 07:28:01 GMT", 1, 0.0d, now),
                is(OpenAiCompatibleProviderSupport.RETRY_BASE_DELAY_MILLIS));
    }

    @Test
    public void retryDelayMillis_aDateOfNoForm_isTheBackoff() {
        // a wrong weekday or a two-digit year in the asctime form is no HTTP date
        // act, assert
        assertThat(
                OpenAiCompatibleProviderSupport.retryDelayMillis("Monday, 21-Oct-26 07:28:00 GMT", 1, 0.0d, 0L),
                is(OpenAiCompatibleProviderSupport.RETRY_BASE_DELAY_MILLIS));
        assertThat(
                OpenAiCompatibleProviderSupport.retryDelayMillis("Wed Oct 21 07:28:00 26", 1, 0.0d, 0L),
                is(OpenAiCompatibleProviderSupport.RETRY_BASE_DELAY_MILLIS));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="isSuccess">
    @Test
    public void isSuccess_statusCodes_trueOnlyForTheTwoHundreds() {
        // act, assert
        assertThat(OpenAiCompatibleProviderSupport.isSuccess(199), is(false));
        assertThat(OpenAiCompatibleProviderSupport.isSuccess(200), is(true));
        assertThat(OpenAiCompatibleProviderSupport.isSuccess(299), is(true));
        assertThat(OpenAiCompatibleProviderSupport.isSuccess(300), is(false));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="isRedirect">
    @Test
    public void isRedirect_statusCodes_trueOnlyForTheThreeHundreds() {
        // act, assert
        assertThat(OpenAiCompatibleProviderSupport.isRedirect(299), is(false));
        assertThat(OpenAiCompatibleProviderSupport.isRedirect(300), is(true));
        assertThat(OpenAiCompatibleProviderSupport.isRedirect(399), is(true));
        assertThat(OpenAiCompatibleProviderSupport.isRedirect(400), is(false));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="redirectMessage">
    @Test
    public void redirectMessage_aLocation_isNamedMasked() {
        // act, assert
        assertThat(
                OpenAiCompatibleProviderSupport.redirectMessage(
                        "https://h/v1/models", 302, "https://other/v1/models?key=secret"),
                is("https://h/v1/models answered HTTP 302, a redirect to https://other/v1/models?***, which srcmorph"
                        + " does not follow (the key would go along): point baseUrl at the server it redirects to"));
    }

    @Test
    public void redirectMessage_noLocation_saysSo() {
        // act, assert
        assertThat(
                OpenAiCompatibleProviderSupport.redirectMessage("https://h/v1/models", 301, null),
                containsString("answered HTTP 301, a redirect to no Location, which srcmorph does not follow"));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="errorMessage">
    @Test
    public void errorMessage_shortBody_isQuotedWhole() {
        // arrange
        final String body = repeat('x', OpenAiCompatibleProviderSupport.ERROR_BODY_EXCERPT);

        // act, assert
        assertThat(
                OpenAiCompatibleProviderSupport.errorMessage("http://h/chat/completions", 401, body),
                is("http://h/chat/completions answered HTTP 401: " + body));
    }

    @Test
    public void errorMessage_longBody_isCutAtTheExcerptLength() {
        // arrange
        final String body = repeat('x', OpenAiCompatibleProviderSupport.ERROR_BODY_EXCERPT + 1);

        // act, assert
        assertThat(
                OpenAiCompatibleProviderSupport.errorMessage("http://h", 500, body),
                is("http://h answered HTTP 500: " + repeat('x', OpenAiCompatibleProviderSupport.ERROR_BODY_EXCERPT)
                        + "..."));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="noChoiceMessage">
    @Test
    public void noChoiceMessage_longBody_namesEndpointFileAndTheStartOfTheBody() {
        // arrange
        final String body = repeat('x', OpenAiCompatibleProviderSupport.ERROR_BODY_EXCERPT + 1);

        // act, assert
        assertThat(
                OpenAiCompatibleProviderSupport.noChoiceMessage("http://h", Paths.get("A.java"), body),
                is("http://h answered without a completion for A.java: "
                        + repeat('x', OpenAiCompatibleProviderSupport.ERROR_BODY_EXCERPT) + "..."));
    }

    private static String repeat(final char c, final int count) {
        final char[] chars = new char[count];
        Arrays.fill(chars, c);
        return new String(chars);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="modelsEndpoint">
    @Test
    public void modelsEndpoint_baseUrlWithBlanksAndTrailingSlashes_isTheTrimmedBaseUrlPlusModels() {
        // act, assert
        assertThat(
                support(ProviderConfig.builder()
                                .modelPath("")
                                .baseUrl(" http://127.0.0.1:3030/v1// ")
                                .build())
                        .modelsEndpoint(),
                is("http://127.0.0.1:3030/v1/models"));
    }

    @Test
    public void modelsEndpoint_notHttp_throwsBeforeAnyConnection() {
        // arrange
        final OpenAiCompatibleProviderSupport support = support(
                ProviderConfig.builder().modelPath("").baseUrl("file:///etc").build());

        // act
        final IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, support::modelsEndpoint);

        // assert
        assertThat(thrown.getMessage(), is("baseUrl is not an http(s) URL: 'file:///etc'"));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="serverKey">
    @Test
    public void serverKey_baseUrlAndKeyVariable_isTheBaseUrlWithoutTrailingSlashesAndTheKeyVariable() {
        // act, assert
        assertThat(
                support(ProviderConfig.builder()
                                .modelPath("")
                                .baseUrl(" http://127.0.0.1:3030/v1// ")
                                .apiKeyEnv(" COPILOT_GATEWAY_API_KEY ")
                                .build())
                        .serverKey(),
                is("http://127.0.0.1:3030/v1 COPILOT_GATEWAY_API_KEY"));
    }

    @Test
    public void serverKey_nonHttpScheme_doesNotCheckTheScheme() {
        // act, assert
        assertThat(
                support(ProviderConfig.builder()
                                .modelPath("")
                                .baseUrl("file:///etc/")
                                .build())
                        .serverKey(),
                is("file:///etc "));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="parseModels">
    private static RemoteModel model(final String id, final int contextLength) {
        return new RemoteModel(id, contextLength, null, null);
    }

    private static List<RemoteModel> parse(final String entries) throws IOException {
        return OpenAiCompatibleProviderSupport.parseModels("{\"data\":[" + entries + "]}");
    }

    @Test
    public void parseModels_copilotGateway_dropsTheAliasAndTheRepeatedIdAndSortsById() throws Exception {
        // act, assert
        assertThat(
                OpenAiCompatibleProviderSupport.parseModels(ModelListFixtures.COPILOT_GATEWAY),
                is(Arrays.asList(
                        model("auto", 871793),
                        model("claude-haiku-4.5", 127790),
                        model("mai-code-1.1-flash", 127790))));
    }

    @Test
    public void parseModels_openRouter_readsTheContextAndThePricePerMillionTokens() throws Exception {
        // act
        final List<RemoteModel> models = OpenAiCompatibleProviderSupport.parseModels(ModelListFixtures.OPENROUTER);

        // assert
        assertThat(models.size(), is(2));
        assertThat(models.get(0), is(model("openrouter/auto", 2000000)));
        final RemoteModel grok = models.get(1);
        assertThat(grok.id(), is("x-ai/grok-4.7"));
        assertThat(grok.contextLength(), is(500000));
        assertThat(grok.inputUsdPerMillion(), is(new BigDecimal("2")));
        assertThat(grok.outputUsdPerMillion(), is(new BigDecimal("6")));
    }

    @Test
    public void parseModels_llamaServer_readsTheRunningWindowFromMeta() throws Exception {
        // act, assert
        assertThat(
                OpenAiCompatibleProviderSupport.parseModels(ModelListFixtures.LLAMA_SERVER),
                is(Collections.singletonList(model("/models/SmolLM2-135M-Instruct-Q3_K_M.gguf", 8192))));
    }

    @Test
    public void parseModels_jllama_namesNoLimit() throws Exception {
        // act, assert
        assertThat(
                OpenAiCompatibleProviderSupport.parseModels(ModelListFixtures.JLLAMA),
                is(Collections.singletonList(model("smollm2", RemoteModel.UNKNOWN_CONTEXT_LENGTH))));
    }

    @Test
    public void parseModels_theFirstPositiveLimitWins_inTheDocumentedOrder() throws Exception {
        // act, assert
        assertThat(
                parse("{\"id\":\"a\",\"context_length\":100,\"max_input_tokens\":200}"
                        + ",{\"id\":\"b\",\"context_length\":0,\"max_input_tokens\":200,\"context_window\":300}"
                        + ",{\"id\":\"c\",\"context_window\":300,\"max_model_len\":400}"
                        + ",{\"id\":\"d\",\"max_model_len\":400,\"top_provider\":{\"context_length\":500}}"
                        + ",{\"id\":\"e\",\"context_length\":-1,\"top_provider\":{\"context_length\":500},"
                        + "\"meta\":{\"n_ctx\":600}}"
                        + ",{\"id\":\"f\",\"top_provider\":{\"context_length\":0},\"meta\":{\"n_ctx\":600,"
                        + "\"n_ctx_train\":700}}"
                        + ",{\"id\":\"g\",\"meta\":{\"n_ctx\":0,\"n_ctx_train\":700}}"
                        + ",{\"id\":\"h\",\"meta\":{\"n_ctx_train\":0}}"),
                is(Arrays.asList(
                        model("a", 100),
                        model("b", 200),
                        model("c", 300),
                        model("d", 400),
                        model("e", 500),
                        model("f", 600),
                        model("g", 700),
                        model("h", RemoteModel.UNKNOWN_CONTEXT_LENGTH))));
    }

    @Test
    public void parseModels_pricesAsTextOrNumber_unknownWhenAbsentNegativeOrNotANumber() throws Exception {
        // act
        final List<RemoteModel> models = parse("{\"id\":\"a\",\"pricing\":{\"prompt\":\" 0.0000005 \","
                + "\"completion\":0.0000015}}"
                + ",{\"id\":\"b\",\"pricing\":{\"prompt\":\"0\",\"completion\":\"-1\"}}"
                + ",{\"id\":\"c\",\"pricing\":{\"prompt\":\"free\",\"completion\":true}}"
                + ",{\"id\":\"d\"}");

        // assert
        assertThat(models.get(0).inputUsdPerMillion(), is(new BigDecimal("0.5")));
        assertThat(models.get(0).outputUsdPerMillion(), is(new BigDecimal("1.5")));
        assertThat(models.get(1).inputUsdPerMillion(), is(BigDecimal.ZERO));
        assertThat(models.get(1).outputUsdPerMillion(), is(nullValue()));
        assertThat(models.get(2).inputUsdPerMillion(), is(nullValue()));
        assertThat(models.get(2).outputUsdPerMillion(), is(nullValue()));
        assertThat(models.get(3).inputUsdPerMillion(), is(nullValue()));
        assertThat(models.get(3).outputUsdPerMillion(), is(nullValue()));
    }

    @Test
    public void parseModels_anEntryWithoutATextualId_isLeftOut() throws Exception {
        // act, assert
        assertThat(
                parse("{\"object\":\"model\"},{\"id\":7},{\"id\":\"kept\"}"),
                is(Collections.singletonList(model("kept", RemoteModel.UNKNOWN_CONTEXT_LENGTH))));
    }

    @Test
    public void parseModels_noDataArray_failsWithTheStartOfTheAnswer() {
        // act
        final IOException thrown = assertThrows(
                IOException.class, () -> OpenAiCompatibleProviderSupport.parseModels("{\"data\":{\"id\":\"a\"}}"));

        // assert
        assertThat(
                thrown.getMessage(),
                is("The answer is not an OpenAI model list (no 'data' array): {\"data\":{\"id\":\"a\"}}"));
    }

    @Test
    public void parseModels_notJson_fails() {
        // act, assert
        assertThrows(IOException.class, () -> OpenAiCompatibleProviderSupport.parseModels("<html>502</html>"));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="timings">
    private static ChatResponse responseWith(final Usage usage) {
        final ChatChoice choice = new ChatChoice(0, new ChatMessage("assistant", "body"), "stop");
        return new ChatResponse("id", Collections.singletonList(choice), usage, null, "{}");
    }

    @Test
    public void timings_usageAndSeconds_reportUsageAndTheWallClockDecodeRate() {
        // act
        final TimedGeneration timings =
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
        // act
        final TimedGeneration timings =
                OpenAiCompatibleProviderSupport.timings("text", responseWith(new Usage(1, 5)), 0.0d);

        // assert
        assertThat(timings.decodeTokensPerSecond(), is(0.0d));
    }
    // </editor-fold>
}
