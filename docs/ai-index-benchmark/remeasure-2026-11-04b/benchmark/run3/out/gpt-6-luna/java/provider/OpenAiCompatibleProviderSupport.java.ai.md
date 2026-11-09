### OpenAiCompatibleProviderSupport.java
- H: 1.0
- C: 92F0B02B
- D: 2026-11-04T12:04:37Z
- T: 2026-11-04T12:06:11Z
- G: 
- A: 0.0.0
- X: file
- P: 0B812A3C
---
> Builds OpenAI-compatible chat requests and safely handles endpoint selection, API-key transmission, HTTP errors, and generation timing for configured models.

#### Purpose
- Provides network-independent request and response support for `OpenAiCompatibleAiGenerationProvider`.
- Builds chat-completions JSON, validates endpoints and API-key destinations, and calculates `AiGenerationTimings`.

#### Type
- `final class OpenAiCompatibleProviderSupport` — final package-private class; annotated `@ToString`.

#### Input
- Constructor: `LlamaCppJniConfig config`, `AiPromptSupport promptSupport`, and `Function<String, @Nullable String> environment`.
- `requestBody(AiGenerationRequest request)` reads the request’s prompt key, source file, and source text; reads model, sampling, stop-string, and reasoning settings from `config`.
- `endpoint()` reads `config.baseUrl()`.
- `apiKey()` reads `config.apiKeyEnv()` and invokes `environment.apply(...)`.
- `apiKeyFor(String endpoint)` parses the endpoint host when a configured key would be sent over HTTP.
- `errorMessage(String endpoint, int status, String body)` consumes an error response body.
- `timings(String text, ChatResponse response, double seconds)` reads usage counts from `response`.

#### Output
- `requestBody(...)` returns serialized JSON with system and user messages, sampling settings, and `stream: false`.
- `endpoint()` returns the normalized chat-completions URL.
- `apiKey()` and `apiKeyFor(...)` return a trimmed key or `null`.
- `errorMessage(...)` returns an endpoint/status/body-excerpt message.
- `timings(...)` returns `AiGenerationTimings`; decode rate is completion tokens per positive elapsed second, otherwise zero, and prefill rate is zero.
- No mutable instance state; static `MAPPER` is used for JSON construction and serialization.

#### Core logic
- `requestBody(...)`:
  - Adds `model` only when `config.remoteModel()` is not blank.
  - Creates `messages` using `promptSupport.systemPrompt(request.promptKey())` and `promptSupport.userMessage(request.sourceFile(), request.sourceText())`.
  - Always writes `max_tokens`, `temperature`, `top_p`, `repetition_penalty`, and `stream`.
  - Adds `top_k` and `min_p` only when positive; `seed` only when nonnegative; `stop` only when nonempty; `reasoning.effort` only when nonblank.
  - Serializes with `MAPPER.writeValueAsString(body)`.
- `endpoint()` trims `config.baseUrl()`, accepts only `HTTP_SCHEME` or `HTTPS_SCHEME`, strips trailing slashes, and appends `CHAT_COMPLETIONS_PATH`.
- `apiKey()` returns `null` if `config.apiKeyEnv()` is blank, the environment lookup is unset, or the value is blank; otherwise returns the trimmed value.
- `apiKeyFor(...)` allows a configured key over HTTPS; for HTTP, parses the host with `new URL(endpoint).getHost()` and requires `isLoopbackHost(host)`.
- `isLoopbackHost(String host)` accepts exact lowercase `localhost`, hosts matching `IPV4_LOOPBACK`, or exact `[::1]`.
- `isSuccess(int status)` checks `HTTP_OK_FIRST <= status < HTTP_OK_END` (200–299).
- `errorMessage(...)` truncates bodies longer than `ERROR_BODY_EXCERPT` (500 characters), appends `ELLIPSIS`, and formats endpoint, status, and excerpt.
- `timings(...)` reads completion, prompt, and cached token counts from `response.getUsage()` and constructs `AiGenerationTimings`.

#### Dependencies
- `LlamaCppJniConfig`, `AiPromptSupport`, `AiGenerationRequest`, `AiGenerationTimings`
- `ChatResponse`, `Usage`
- Jackson: `ObjectMapper`, `ArrayNode`, `ObjectNode`
- `Java8CompatibilityHelper`
- Java: `IOException`, `URL`, `List`, `Objects`, `Function`, `Pattern`
- Lombok: `ToString`; JSpecify: `Nullable`

#### Exceptions / Errors
- Constructor uses `Objects.requireNonNull` for `config`, `promptSupport`, and `environment`.
- `requestBody(...)` propagates `IOException` from JSON serialization.
- `endpoint()` throws `IllegalArgumentException` for non-HTTP(S) base URLs.
- `apiKeyFor(...)` propagates `IOException` for URL parsing failures and throws `IOException` if a configured key would be sent over HTTP to a non-loopback host.
- `timings(...)` assumes `response.getUsage()` is non-null.

#### Concurrency
- No synchronization or concurrency primitives; instance dependencies are final.
