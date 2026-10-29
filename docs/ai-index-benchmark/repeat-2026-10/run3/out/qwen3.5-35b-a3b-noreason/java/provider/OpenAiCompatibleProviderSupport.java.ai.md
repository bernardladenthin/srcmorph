### OpenAiCompatibleProviderSupport.java
- H: 1.0
- C: 92F0B02B
- D: 2026-10-29T14:19:17Z
- T: 2026-10-29T14:43:15Z
- G: 
- A: 0.0.0
- X: file
---
> Constructs OpenAI-compatible chat request bodies, validates endpoint security for API keys, and parses HTTP responses into usage metrics.

#### Purpose
*   Provides network-agnostic support logic for OpenAI-compatible AI generation providers (request serialization, endpoint validation, key security).
*   Isolates deterministic logic from socket handling to enable precise unit testing of request construction and error handling.

#### Type
final class `OpenAiCompatibleProviderSupport`
*   Fields: `LlamaCppJniConfig config`, `AiPromptSupport promptSupport`, `Function<String, String> environment`.
*   Constants: `CHAT_COMPLETIONS_PATH`, `ERROR_BODY_EXCERPT`, `HTTP_SCHEME`, `HTTPS_SCHEME`, `IPV4_LOOPBACK`, `IPV6_LOOPBACK`.
*   Annotations: `@ToString` (excluding internal fields), `@Nullable` on return types.

#### Input
*   Constructor: `LlamaCppJniConfig`, `AiPromptSupport`, `Function<String, String>` (env var lookup).
*   Method `requestBody`: `AiGenerationRequest` (source file, text, prompt key, sampling knobs).
*   Method `apiKeyFor`: `String` (endpoint URL).
*   Static methods: `int` (status code), `String` (host name, body content, text, response object).

#### Output
*   `requestBody`: Serialized JSON `String` representing the chat-completions payload.
*   `endpoint`: Validated `String` URL ending in `/chat/completions`.
*   `apiKey`: Trimmed API key `String` or `null`.
*   `apiKeyFor`: Secure API key `String` or `null` (throws `IOException` if insecure transmission detected).
*   `errorMessage`: Formatted exception message string combining endpoint, status, and body excerpt.
*   `timings`: `AiGenerationTimings` object containing token counts and decode rates.

#### Core logic
*   **Request Construction**: Builds an `ObjectNode` with `model`, `messages` (system/user roles), and conditional sampling parameters (`top_k`, `min_p`, `seed`, `stop`, `reasoning.effort`) based on `LlamaCppJniConfig` values.
*   **Endpoint Validation**: Trims trailing slashes from `baseUrl`, enforces `http(s)` scheme prefix, and appends `/chat/completions`.
*   **Key Security Check**: Retrieves API key from environment; verifies that plain `http` connections only occur to loopback hosts (`localhost`, `127.x.x.x`, `[::1]`). Throws `IOException` if a key is sent over unencrypted non-loopback channels.
*   **Loopback Detection**: Matches host against literal `localhost`, IPv4 regex `127.\d{1,3}...\d{1,3}`, and IPv6 literal `[::1]`.
*   **Error Formatting**: Extracts first 500 characters of error body, appends ellipsis if truncated, and formats as "endpoint answered HTTP status: excerpt".
*   **Timing Calculation**: Computes `decodeRate` by dividing completion tokens by wall-clock seconds; sets prefill rate to zero.

#### Public API
*   `requestBody(AiGenerationRequest) -> String` constructs the JSON payload.
*   `endpoint() -> String` returns the validated chat URL.
*   `apiKey() -> String` retrieves the configured secret.
*   `apiKeyFor(String) -> String` secures key transmission.
*   `isLoopbackHost(String) -> boolean` checks local address.
*   `isSuccess(int) -> boolean` validates HTTP 2xx range.
*   `errorMessage(String, int, String) -> String` formats failure details.
*   `timings(String, ChatResponse, double) -> AiGenerationTimings` calculates performance metrics.

#### Dependencies
*   `com.fasterxml.jackson.databind.ObjectMapper`
*   `com.fasterxml.jackson.databind.node.ArrayNode`
*   `com.fasterxml.jackson.databind.node.ObjectNode`
*   `java.io.IOException`
*   `java.net.URL`
*   `java.util.List`
*   `java.util.Objects`
*   `java.util.function.Function`
*   `java.util.regex.Pattern`
*   `net.ladenthin.llama.value.ChatResponse`
*   `net.ladenthin.llama.value.Usage`
*   `net.ladenthin.srcmorph.document.AiGenerationRequest`
*   `net.ladenthin.srcmorph.prompt.AiPromptSupport`
*   `net.ladenthin.srcmorph.provider.LlamaCppJniConfig`
*   `net.ladenthin.srcmorph.support.Java8CompatibilityHelper`
*   `net.ladenthin.srcmorph.value.AiGenerationTimings`
*   `org.jspecify.annotations.Nullable`
*   `lombok.ToString`

#### Exceptions / Errors
*   `IllegalArgumentException`: Thrown by `endpoint()` if `baseUrl` lacks `http` or `https` scheme.
*   `IOException`: Thrown by `apiKeyFor()` if an API key is configured but the endpoint uses plain `http` to a non-loopback host.
*   `IOException`: Propagated by `requestBody()` if JSON serialization fails.
*   Null handling: Explicitly checks for null/blank strings in `apiKey()` and `requestBody()` before processing.

#### Concurrency
*   No explicit synchronization or thread-safety mechanisms noted.
*   Relies on immutable configuration objects passed via constructor.
