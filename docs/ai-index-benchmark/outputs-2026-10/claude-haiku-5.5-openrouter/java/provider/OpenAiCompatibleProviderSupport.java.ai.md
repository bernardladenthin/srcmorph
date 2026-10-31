### OpenAiCompatibleProviderSupport.java
- H: 1.0
- C: 92F0B02B
- D: 2026-10-31T09:32:09Z
- T: 2026-10-31T09:35:22Z
- G: 
- A: 0.0.0
- X: file
---
> Builds the OpenAI-compatible chat-completions request body, endpoint URL, API key lookup with cleartext-safety checks, HTTP status and error handling, and call timings for a remote LLM provider.

#### Purpose
- Network-free helper for `OpenAiCompatibleAiGenerationProvider`: request JSON, endpoint, key resolution, status check, error message, timings.
- Split out so the non-network logic can be fully unit-tested (100% PIT gate), while socket handling stays in the provider.

#### Type
- Class, package-private, `final`, annotated `@ToString` (Lombok); `@ToString.Exclude` on `promptSupport` and `environment`.
- No extends or implements.
- Type use: `Function<String, @Nullable String>` (jspecify `@Nullable` on type argument).

#### Input
- Constructor: `LlamaCppJniConfig config`, `AiPromptSupport promptSupport`, `Function<String, @Nullable String> environment`; all `requireNonNull`.
- `requestBody(AiGenerationRequest request)`: reads `request.promptKey()`, `request.sourceFile()`, `request.sourceText()`.
- `endpoint()`: reads `config.baseUrl()`.
- `apiKey()` / `apiKeyFor(String endpoint)`: reads `config.apiKeyEnv()`, environment lookup.
- `isLoopbackHost(String host)`, `isSuccess(int status)`, `errorMessage(String endpoint, int status, String body)`: plain parameters.
- `timings(String text, ChatResponse response, double seconds)`: reads `response.getUsage()`.
- Config fields read: `remoteModel`, `maxOutputTokens`, `temperature`, `topP`, `topK`, `minP`, `repeatPenalty`, `seed`, `stopStrings`, `reasoningEffort`.

#### Output
- `requestBody`: JSON string with keys `model` (non-blank), `messages` (system + user), `max_tokens`, `temperature`, `top_p`, `top_k` (>0), `min_p` (>0), `repetition_penalty`, `seed` (>=0), `stop` (non-empty), `reasoning.effort` (non-blank), `stream: false`.
- `endpoint`: `baseUrl` trimmed, trailing slashes removed, plus `/chat/completions`.
- `apiKey`: trimmed key or null.
- `apiKeyFor`: key or null.
- `isLoopbackHost`, `isSuccess`: booleans.
- `errorMessage`: string `endpoint + " answered HTTP " + status + ": " + excerpt`.
- `timings`: `AiGenerationTimings` with text, prompt tokens, cached tokens, prefill rate 0.0, completion tokens, decode rate.
- No mutable state; no I/O beyond serialisation.

#### Core logic
- Request body:
  - Creates ObjectNode via shared static `ObjectMapper`.
  - Adds `model` only when `remoteModel` not blank.
  - Adds two messages: system prompt from `promptSupport.systemPrompt(promptKey)`, user message from `promptSupport.userMessage(sourceFile, sourceText)`.
  - Adds sampling knobs; optional fields are emitted conditionally (topK > 0, minP > 0, seed >= 0, stop non-empty, reasoning effort non-blank).
  - Always sets `stream` to false.
  - Serialises with `writeValueAsString`.
- Endpoint:
  - Trims `baseUrl`; rejects anything not starting with `http://` or `https://` (prevents `file:` etc.).
  - Strips trailing `/` characters in a loop; appends `/chat/completions`.
- API key:
  - Returns null if `apiKeyEnv` blank, variable unset, or value blank; otherwise trimmed value.
- API key for endpoint (cleartext guard):
  - Returns key unchanged if none, or endpoint is not `http://`.
  - For `http://`, parses host via `URL.getHost()`; throws `IOException` unless host is loopback.
- Loopback check:
  - Matches exactly `localhost`, IPv4 regex `127.d{1,3}.d{1,3}.d{1,3}` (full-match, not prefix), or `[::1]`.
- Success check: 200 <= status < 300.
- Error message: truncates body to 500 characters, appends `...` if cut.
- Timings:
  - Reads usage from `ChatResponse`; casts long token counts to int.
  - Decode rate = completion tokens / seconds, 0 when seconds <= 0.
  - Prefill rate fixed at 0.0 (treated as unknown by callers).

#### Public API
- `requestBody(AiGenerationRequest request) -> String` (throws IOException): builds chat-completions JSON body.
- `endpoint() -> String`: resolves chat-completions URL; validates http(s).
- `apiKey() -> @Nullable String`: reads trimmed key from env.
- `apiKeyFor(String endpoint) -> @Nullable String` (throws IOException): key with cleartext check.
- `isLoopbackHost(String host) -> boolean` (static): loopback host test.
- `isSuccess(int status) -> boolean` (static): 2xx test.
- `errorMessage(String endpoint, int status, String body) -> String` (static): formats error with body excerpt.
- `timings(String text, ChatResponse response, double seconds) -> AiGenerationTimings` (static): computes timings.
- Package-private constants: `CHAT_COMPLETIONS_PATH`, `ERROR_BODY_EXCERPT`, `HTTP_SCHEME`, `HTTPS_SCHEME`.

#### Dependencies
- Jackson: `ObjectMapper`, `ArrayNode`, `ObjectNode`.
- Java: `IOException`, `URL`, `List`, `Objects`, `Function`, `Pattern`.
- Lombok: `ToString`.
- `net.ladenthin.llama.value.ChatResponse`, `net.ladenthin.llama.value.Usage`.
- `net.ladenthin.srcmorph.document.AiGenerationRequest`.
- `net.ladenthin.srcmorph.prompt.AiPromptSupport`.
- `net.ladenthin.srcmorph.support.Java8CompatibilityHelper`.
- `org.jspecify.annotations.Nullable`.
- Referenced, not imported (same package): `LlamaCppJniConfig`, `AiGenerationTimings`, `LlamaCppJniProviderSupport` (javadoc), `OpenAiCompatibleAiGenerationProvider` (javadoc), `AiGenerationConfig.DEFAULT_API_KEY_ENV` (javadoc).

#### Exceptions / Errors
- `requestBody` throws `IOException` on serialisation failure.
- `endpoint` throws `IllegalArgumentException` when base URL is not http(s).
- `apiKeyFor` throws `IOException` when key would go in cleartext to non-loopback host, or when `endpoint` cannot be parsed by `new URL`.
- Constructor throws `NullPointerException` for null arguments.
- `apiKey` returns null (not exception) for missing/blank env values.
- `errorMessage` tolerates empty body.

#### Concurrency
- Instance fields are final; `MAPPER` is shared static `ObjectMapper` (thread-safe after configuration).
- No synchronization; instance is effectively immutable.
