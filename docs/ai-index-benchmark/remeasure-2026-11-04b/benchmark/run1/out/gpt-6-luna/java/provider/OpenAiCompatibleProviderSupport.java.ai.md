### OpenAiCompatibleProviderSupport.java
- H: 1.0
- C: 92F0B02B
- D: 2026-11-04T11:46:43Z
- T: 2026-11-04T11:48:21Z
- G: 
- A: 0.0.0
- X: file
- P: 0B812A3C
---
> Builds OpenAI-compatible chat-completion requests, protects API keys from cleartext remote transmission, and converts responses into generation timings.

#### Purpose
- `OpenAiCompatibleProviderSupport` prepares request JSON and endpoint details for an OpenAI-compatible model provider.
- It validates key transport safety, classifies HTTP responses, formats errors, and computes generation timings.

#### Type
- `OpenAiCompatibleProviderSupport` — final class; package-private; annotated `@ToString`.

#### Input
- Constructor: `LlamaCppJniConfig config`, `AiPromptSupport promptSupport`, and `Function<String, @Nullable String> environment`.
- `requestBody(AiGenerationRequest request)` reads prompt key, source file, and source text from the request; reads model, sampling, stop-string, and reasoning settings from `config`.
- `endpoint()` reads `config.baseUrl()`.
- `apiKey()` reads `config.apiKeyEnv()` and looks up that environment variable through `environment`.
- `apiKeyFor(String endpoint)` parses the endpoint host when a configured key would be sent over HTTP.
- `errorMessage(String endpoint, int status, String body)` consumes an error response body.
- `timings(String text, ChatResponse response, double seconds)` consumes response usage counts and elapsed wall-clock seconds.

#### Output
- `requestBody(...)` returns serialized JSON with system and user messages, sampling options, and `stream: false`.
- `endpoint()` returns the normalized chat-completions URL.
- `apiKey()` and `apiKeyFor(...)` return a trimmed key or `null`.
- `isLoopbackHost(...)` and `isSuccess(...)` return booleans; `errorMessage(...)` returns formatted text.
- `timings(...)` returns `AiGenerationTimings` with prompt, cached, and completion token counts, zero prefill rate, and computed decode rate.
- No fields are mutated; `MAPPER` is used for JSON serialization.

#### Core logic
- `requestBody(...)` creates an `ObjectNode` using `MAPPER`; includes `model` and `reasoning.effort` only when non-blank, always adds system/user messages and `max_tokens`, `temperature`, `top_p`, `repetition_penalty`, and `stream: false`.
- Adds `top_k` only when `config.topK() > 0`, `min_p` only when `config.minP() > 0.0f`, `seed` only when `config.seed() >= 0`, and `stop` only when `config.stopStrings()` is non-empty; serializes with `MAPPER.writeValueAsString(body)`.
- Builds `endpoint()` from trimmed `config.baseUrl()`: accepts only `HTTP_SCHEME` or `HTTPS_SCHEME`, removes trailing slashes, then appends `CHAT_COMPLETIONS_PATH`.
- `apiKey()` returns `null` if `config.apiKeyEnv()` is blank, the lookup is unset, or the value is blank; otherwise returns the trimmed environment value from `environment.apply(...)`.
- `apiKeyFor(...)` returns the key unchanged for HTTPS or when no key is configured. For HTTP with a key, parses the URL using `new URL(endpoint)` and permits it only if `isLoopbackHost(host)`; otherwise throws `IOException`.
- `isLoopbackHost(...)` accepts exactly lowercase `localhost`, an IPv4 literal matching `IPV4_LOOPBACK`, or `[::1]`.
- `isSuccess(...)` recognizes statuses `200` through `299`.
- `errorMessage(...)` uses the whole body up to `ERROR_BODY_EXCERPT` characters; longer bodies are truncated and suffixed with `ELLIPSIS`.
- `timings(...)` reads usage through `response.getUsage()`, casts token counts to `int`, and computes `completionTokens / seconds` when `seconds > 0.0d`, otherwise `0.0d`.

#### Public API
- `CHAT_COMPLETIONS_PATH: String` — chat-completions endpoint path.
- `ERROR_BODY_EXCERPT: int` — maximum error-body excerpt length.
- `HTTP_SCHEME: String` — plain HTTP scheme prefix.
- `HTTPS_SCHEME: String` — HTTPS scheme prefix.
- `OpenAiCompatibleProviderSupport(LlamaCppJniConfig, AiPromptSupport, Function<String, @Nullable String>)` — initialize model support.
- `requestBody(AiGenerationRequest) -> String` — serialize chat request.
- `endpoint() -> String` — resolve chat-completions URL.
- `apiKey() -> @Nullable String` — look up configured key.
- `apiKeyFor(String) -> @Nullable String` — enforce safe key transport.
- `isLoopbackHost(String) -> boolean` — identify loopback host.
- `isSuccess(int) -> boolean` — classify HTTP status.
- `errorMessage(String, int, String) -> String` — format HTTP failure.
- `timings(String, ChatResponse, double) -> AiGenerationTimings` — calculate generation metrics.

#### Dependencies
- Jackson: `ObjectMapper`, `ArrayNode`, `ObjectNode`.
- Java: `IOException`, `URL`, `List`, `Objects`, `Function`, `Pattern`.
- Lombok: `ToString`.
- `ChatResponse`, `Usage`, `AiGenerationRequest`, `AiPromptSupport`, `Java8CompatibilityHelper`, `LlamaCppJniConfig`, `AiGenerationTimings`.
- JSpecify: `Nullable`.

#### Exceptions / Errors
- Constructor uses `Objects.requireNonNull` for `config`, `promptSupport`, and `environment`, with corresponding parameter names in failure messages.
- `requestBody(...)` declares `IOException` for JSON serialization failure.
- `endpoint()` throws `IllegalArgumentException` when `baseUrl` does not start with `http://` or `https://`.
- `apiKeyFor(...)` declares `IOException` for an unparsable endpoint or a configured key sent over plain HTTP to a non-loopback host.

#### Concurrency
- No synchronization, threading, or coroutine behavior; mutable state is not exposed.
