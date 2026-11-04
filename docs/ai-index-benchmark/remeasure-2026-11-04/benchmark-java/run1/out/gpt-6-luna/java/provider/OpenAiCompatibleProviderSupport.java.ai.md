### OpenAiCompatibleProviderSupport.java
- H: 1.0
- C: 92F0B02B
- D: 2026-11-04T07:09:59Z
- T: 2026-11-04T07:11:31Z
- G: 
- A: 0.0.0
- X: file
- P: 36159BF2
---
> Builds OpenAI-compatible chat-completion requests for configured models while protecting API keys and translating responses into generation timing data.

#### Purpose
- Provides network-independent request construction and policy logic for OpenAI-compatible model calls.
- Handles endpoint and API-key validation, HTTP status/error formatting, and response timing calculation.

#### Type
- `final class OpenAiCompatibleProviderSupport`; package-private, final; annotated `@ToString`.
- No superclass or implemented interfaces.

#### Input
- Constructor: `LlamaCppJniConfig`, `AiPromptSupport`, and an environment-variable lookup `Function<String, @Nullable String>`.
- `requestBody`: `AiGenerationRequest`; reads model, sampling options, API configuration, prompts, source file, and source text through dependencies.
- `apiKeyFor`: endpoint URL string; resolves the configured environment variable.
- `errorMessage`: endpoint, HTTP status, and error-body text.
- `timings`: completion text, `ChatResponse`, and elapsed seconds.

#### Output
- Serializes request data to a JSON string; `endpoint()` returns the chat-completions URL.
- Returns a trimmed API key or `null`; rejects unsafe cleartext key delivery.
- Returns HTTP success status, formatted error message, loopback-host classification, and `AiGenerationTimings`.
- Maintains final references to configuration, prompt support, environment lookup, and compatibility helper.

#### Core logic
- `requestBody` creates two chat messages: system prompt from `promptKey`, then user content from source file and text.
- Includes `model` and `reasoning.effort` only when nonblank; always writes `max_tokens`, `temperature`, `top_p`, `repetition_penalty`, and `stream=false`.
- Adds `top_k` and `min_p` only when positive, `seed` when nonnegative, and `stop` when the configured list is nonempty.
- `endpoint` trims `baseUrl`, permits only `http://` or `https://`, removes trailing slashes, and appends `/chat/completions`.
- `apiKey` looks up the configured environment variable and returns its trimmed nonblank value.
- `apiKeyFor` allows configured keys over HTTPS or plain HTTP only to exact `localhost`, IPv4 `127.x.x.x`, or `[::1]`.
- `isSuccess` recognizes HTTP 200–299; `errorMessage` includes at most 500 body characters, followed by `...` when truncated.
- `timings` extracts prompt, cached, and completion token counts; computes completion tokens per elapsed second, or zero for nonpositive duration; prefill rate is zero.

#### Public API
- `requestBody(AiGenerationRequest) -> String` — serialize chat request.
- `endpoint() -> String` — build chat-completions URL.
- `apiKey() -> @Nullable String` — resolve configured key.
- `apiKeyFor(String) -> @Nullable String` — enforce safe key transport.
- `isLoopbackHost(String) -> boolean` — recognize loopback hosts.
- `isSuccess(int) -> boolean` — classify HTTP status.
- `errorMessage(String, int, String) -> String` — format HTTP failure.
- `timings(String, ChatResponse, double) -> AiGenerationTimings` — calculate generation rates.

#### Dependencies
- `LlamaCppJniConfig`, `AiPromptSupport`, `AiGenerationRequest`, `Java8CompatibilityHelper`.
- Jackson `ObjectMapper`, `ObjectNode`, `ArrayNode`.
- `ChatResponse`, `Usage`, `AiGenerationTimings`.
- Java `IOException`, `URL`, `List`, `Objects`, `Function`, `Pattern`; JSpecify `Nullable`; Lombok `ToString`.

#### Exceptions / Errors
- Constructor uses `Objects.requireNonNull` for all three dependencies.
- `requestBody` propagates `IOException` from JSON serialization.
- `endpoint` throws `IllegalArgumentException` for non-HTTP(S) base URLs.
- `apiKeyFor` propagates URL parsing `IOException`; throws `IOException` when a key would be sent over plain HTTP to a non-loopback host.
- `errorMessage` truncates long bodies; no null handling is specified for its arguments.

#### Concurrency
- Final configuration and dependency references; no synchronization or mutable shared request state.
- Static `ObjectMapper` is shared across calls. A new `ObjectNode` is built per request. isumaqatigiiss
</final> 二四六
 恒一


> Builds OpenAI-compatible chat-completion requests for configured models while protecting API keys and deriving generation timing data from responses.

#### Purpose
- Supplies network-independent request construction and policy logic for OpenAI-compatible model calls.
- Handles endpoint validation, API-key transport checks, HTTP error formatting, and response timing calculations.

#### Type
- `final class OpenAiCompatibleProviderSupport`; package-private, final; annotated `@ToString`.
- No explicit superclass or implemented interfaces.

#### Input
- Constructor receives `LlamaCppJniConfig`, `AiPromptSupport`, and an environment-variable lookup function.
- Request construction consumes an `AiGenerationRequest` and model sampling configuration.
- Key validation consumes an endpoint URL; timing calculation consumes completion text, `ChatResponse`, and elapsed seconds.

#### Output
- Produces serialized JSON request bodies, endpoint URLs, API keys or `null`, error messages, and `AiGenerationTimings`.
- Stores final references to configuration, prompt support, environment lookup, and compatibility helper.

#### Core logic
- Builds a system message from the request’s prompt key and a user message from its source file and text.
- Always includes `max_tokens`, `temperature`, `top_p`, `repetition_penalty`, and `stream=false`; conditionally includes model, `top_k`, `min_p`, seed, stop strings, and reasoning effort.
- Trims the configured base URL, accepts only `http://` or `https://`, removes trailing slashes, and appends `/chat/completions`.
- Resolves a configured environment variable to a trimmed API key; permits sending it over plain HTTP only to exact `localhost`, matching `127.<1–3 digits>.<1–3 digits>.<1–3 digits>`, or `[::1]`.
- Classifies HTTP 200–299 as success; formats failures with endpoint, status, and up to 500 body characters plus `...` if truncated.
- Extracts token usage; computes completion-token rate from elapsed seconds, using zero for nonpositive duration, and sets prefill rate to zero.

#### Dependencies
- `LlamaCppJniConfig`, `AiPromptSupport`, `AiGenerationRequest`, `Java8CompatibilityHelper`.
- Jackson `ObjectMapper`, `ObjectNode`, `ArrayNode`; `ChatResponse`, `Usage`, `AiGenerationTimings`.
- Java `IOException`, `URL`, `List`, `Objects`, `Function`, `Pattern`; JSpecify `Nullable`; Lombok `ToString`.

#### Exceptions / Errors
- Constructor rejects null configuration, prompt support, or environment lookup with `Objects.requireNonNull`.
- Request serialization can throw `IOException`.
- `endpoint()` throws `IllegalArgumentException` for a base URL without an accepted HTTP(S) prefix.
- `apiKeyFor()` can throw `IOException` for an invalid URL or a configured key targeting a non-loopback plain-HTTP host.
