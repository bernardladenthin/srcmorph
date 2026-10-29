### OpenAiCompatibleProviderSupport.java
- H: 1.0
- C: 92F0B02B
- D: 2026-10-29T23:43:20Z
- T: 2026-10-29T23:45:39Z
- G: 
- A: 0.0.0
- X: file
---
> Builds the OpenAI-compatible chat-completions request body and validation metadata for a model while guarding API keys and formatting failures.

#### Purpose
- Builds request payloads, endpoints, and key-handling rules for `OpenAiCompatibleAiGenerationProvider`.
- Provides status checks, error-message formatting, and timing calculations for OpenAI-compatible responses.

#### Type
- `final class OpenAiCompatibleProviderSupport`
- Modifiers: `final`
- Extends: none
- Implements: none
- Annotations: `@ToString`
- Fields: `CHAT_COMPLETIONS_PATH`, `ERROR_BODY_EXCERPT`, `HTTP_OK_FIRST`, `HTTP_OK_END`, `HTTP_SCHEME`, `HTTPS_SCHEME`, `LOOPBACK_HOST_NAME`, `IPV4_LOOPBACK`, `IPV6_LOOPBACK`, `ELLIPSIS`, `MAPPER`, `config`, `promptSupport`, `environment`, `compatibilityHelper`

#### Input
- Constructor: `OpenAiCompatibleProviderSupport(LlamaCppJniConfig config, AiPromptSupport promptSupport, Function<String, @Nullable String> environment)`
- Reads `config.remoteModel()`, `config.maxOutputTokens()`, `config.temperature()`, `config.topP()`, `config.topK()`, `config.minP()`, `config.repeatPenalty()`, `config.seed()`, `config.stopStrings()`, `config.reasoningEffort()`, `config.baseUrl()`, `config.apiKeyEnv()`
- Reads prompt content through `AiPromptSupport.systemPrompt(request.promptKey())` and `AiPromptSupport.userMessage(request.sourceFile(), request.sourceText())`
- Reads environment values via `environment.apply(config.apiKeyEnv())`
- Method inputs: `AiGenerationRequest request`, `String endpoint`, `String host`, `String body`, `int status`, `String text`, `ChatResponse response`, `double seconds`

#### Output
- `String requestBody(AiGenerationRequest request) -> String`: JSON request body for chat completions
- `String endpoint() -> String`: base URL plus `/chat/completions`
- `@Nullable String apiKey() -> String`: trimmed environment API key or `null`
- `@Nullable String apiKeyFor(String endpoint) -> String`: key allowed to be sent, or `null`
- `static boolean isLoopbackHost(String host) -> boolean`: loopback host check
- `static boolean isSuccess(int status) -> boolean`: 2xx status check
- `static String errorMessage(String endpoint, int status, String body) -> String`: formatted HTTP failure message
- `static AiGenerationTimings timings(String text, ChatResponse response, double seconds) -> AiGenerationTimings`: completion timing summary
- Mutates `ObjectNode` and `ArrayNode` payloads before serialization

#### Core logic
- `requestBody(AiGenerationRequest)` creates an `ObjectNode`, adds `model` if non-blank, builds `messages` with `system` and `user` entries, and writes `max_tokens`, `temperature`, `top_p`, optional `top_k`, `min_p`, `repetition_penalty`, `seed`, `stop`, `reasoning.effort`, and `stream=false`.
- `endpoint()` trims `baseUrl`, rejects non-`http(s)` values with `IllegalArgumentException`, strips trailing `/`, and appends `CHAT_COMPLETIONS_PATH`.
- `apiKey()` resolves `config.apiKeyEnv()` from the injected environment and returns a trimmed key only when the variable is present and non-blank.
- `apiKeyFor(String)` returns early for missing keys or non-HTTP endpoints; for plain HTTP, it parses the host and rejects non-loopback hosts before sending a cleartext API key.
- `isLoopbackHost(String)` accepts `localhost`, `127.x.x.x`, and `[::1]` exactly.
- `errorMessage(String, int, String)` truncates long error bodies to `ERROR_BODY_EXCERPT` and appends `...` when needed.
- `timings(String, ChatResponse, double)` extracts `Usage` values, computes decode rate as completion tokens over elapsed seconds, and creates `AiGenerationTimings`.

#### Public API
- None

#### Dependencies
- `ObjectMapper`
- `ArrayNode`
- `ObjectNode`
- `IOException`
- `URL`
- `List`
- `Objects`
- `Function`
- `Pattern`
- `ToString`
- `ChatResponse`
- `Usage`
- `AiGenerationRequest`
- `AiPromptSupport`
- `Java8CompatibilityHelper`
- `Nullable`
- `LlamaCppJniConfig`
- `AiGenerationTimings`
- `OpenAiCompatibleAiGenerationProvider`
- `LlamaCppJniProviderSupport`

#### Exceptions / Errors
- Constructor uses `Objects.requireNonNull(config, "config")`, `Objects.requireNonNull(promptSupport, "promptSupport")`, and `Objects.requireNonNull(environment, "environment")`
- `endpoint()` throws `IllegalArgumentException` when `baseUrl` is not an `http(s)` URL
- `requestBody(AiGenerationRequest)` declares `IOException` for serialization failure
- `apiKeyFor(String)` throws `IOException` when a configured key is sent in cleartext to a non-loopback HTTP host or when the endpoint cannot be parsed
- Blank values are handled with `Java8CompatibilityHelper.isBlank(...)` before using strings or environment variables
- `errorMessage(...)` returns a string even for empty error bodies; no exceptions are thrown there

#### Concurrency
- No explicit synchronization or locking
- `MAPPER` is static and effectively immutable for use across calls
- Instance fields are `final`; no in-place mutation of the support object after construction
- No thread-safety annotations or shared mutable collections are present beyond the injected `Function` and config references
