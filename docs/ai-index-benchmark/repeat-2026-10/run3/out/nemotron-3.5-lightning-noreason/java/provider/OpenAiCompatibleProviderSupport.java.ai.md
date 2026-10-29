### OpenAiCompatibleProviderSupport.java
- H: 1.0
- C: 92F0B02B
- D: 2026-10-29T14:19:17Z
- T: 2026-10-29T14:40:04Z
- G: 
- A: 0.0.0
- X: file
---
> Calculates request body, endpoint, API key handling, error messages, and timings for OpenAI-compatible model calls without network I/O.

#### Purpose
- Provides pure logic for OpenAI-compatible model interactions that do not require network I/O.
- Split from the provider class to isolate PIT-gate-critical code; provider socket handling is tested via stub-server tests.

#### Type
- Class: `final`, `@ToString`.
- Extends: none; implements: none.
- Key generics: none.
- Notable annotations: `@ToString` (with `@ToString.Exclude` on fields), Lombok.

#### Input
- Constructor: `LlamaCppJniConfig config`, `AiPromptSupport promptSupport`, `Function<String, @Nullable String> environment` (looks up env vars, may be null).
- Method `requestBody(AiGenerationRequest)`: receives an `AiGenerationRequest` object containing prompt key, source file, and source text.
- Method `apiKeyFor(String)`: receives the endpoint URL to validate loopback/HTTPS policy.

#### Output
- Method `requestBody`: returns a JSON `String` representing the chat-completions request body.
- Method `endpoint`: returns a `String` URL with trailing slashes removed and `/chat/completions` appended; throws `IllegalArgumentException` if baseUrl is not http(s).
- Method `apiKey`: returns `@Nullable String` — trimmed key, or null when env var is unset/blank.
- Method `apiKeyFor`: returns `@Nullable String` — key if configured and endpoint is https or loopback http; throws `IOException` if key sent over plain http to non-loopback host.
- Method `timings`: returns `AiGenerationTimings` built from `ChatResponse` usage and wall-clock seconds.

#### Core logic
- `requestBody`: Serializes JSON body using `ObjectMapper`. Conditionally includes `model`, `top_k`, `min_p`, `seed`, `stop` array, and `reasoning.effort` based on config values. Always includes `max_tokens`, `temperature`, `top_p`, `repetition_penalty`, and `stream: false`. Adds system/user messages from `promptSupport`.
- `endpoint`: Trims baseUrl, validates it starts with `http://` or `https://`, strips trailing slashes, appends `/chat/completions`.
- `apiKey`: Looks up key via `environment` function using `config.apiKeyEnv()`; returns null if env var is blank/missing or key is null/blank.
- `apiKeyFor`: If key is null or endpoint does not start with `http://`, returns key. If endpoint starts with `http://`, checks host via `isLoopbackHost`; throws `IOException` if non-loopback, otherwise returns key.
- `isLoopbackHost`: Returns true if host is exactly `localhost`, matches IPv4 pattern `127.x.x.x`, or equals IPv6 `[::1]`.
- `isSuccess`: Returns true if status in range [200, 300).
- `errorMessage`: Constructs exception message from endpoint, status, and body excerpt (first 500 chars followed by `...` if longer).
- `timings`: Extracts prompt tokens, cached tokens, completion tokens from `ChatResponse.getUsage`. Computes decode rate as completion tokens / seconds (0 if seconds <= 0).

#### Public API
- `requestBody(AiGenerationRequest request) -> String`
- `endpoint() -> String`
- `apiKey() -> @Nullable String`
- `apiKeyFor(String endpoint) -> @Nullable String`
- `isLoopbackHost(String host) -> boolean`
- `isSuccess(int status) -> boolean`
- `errorMessage(String endpoint, int status, String body) -> String`
- `timings(String text, ChatResponse response, double seconds) -> AiGenerationTimings`

#### Dependencies
- `com.fasterxml.jackson.databind.ObjectMapper`, `ArrayNode`, `ObjectNode`
- `java.io.IOException`, `java.net.URL`, `java.util.List`, `java.util.Objects`, `java.util.function.Function`, `java.util.regex.Pattern`
- `net.ladenthin.llama.value.ChatResponse`, `net.ladenthin.llama.value.Usage`
- `net.ladenthin.srcmorph.document.AiGenerationRequest`
- `net.ladenthin.srcmorph.prompt.AiPromptSupport`
- `net.ladenthin.srcmorph.support.Java8CompatibilityHelper`
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- `requestBody`: throws `IOException` if body cannot be serialized.
- `endpoint`: throws `IllegalArgumentException` if baseUrl is not http(s).
- `apiKeyFor`: throws `IOException` if API key is configured and endpoint is plain http to a non-loopback host.
- `apiKey`: returns null when env var is unset/blank; no thrown exceptions.

#### Concurrency
- No threading or synchronization mechanisms present.
- Class is `final` with `final` fields; immutable after construction.
