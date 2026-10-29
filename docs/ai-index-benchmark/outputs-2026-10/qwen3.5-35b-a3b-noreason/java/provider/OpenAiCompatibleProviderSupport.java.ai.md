### OpenAiCompatibleProviderSupport.java
- H: 1.0
- C: 92F0B02B
- D: 2026-10-29T08:50:27Z
- T: 2026-10-29T08:56:39Z
- G: 
- A: 0.0.0
- X: file
---
> Constructs OpenAI-compatible chat request bodies, validates API key transmission security, and parses HTTP responses for AI generation tasks.

#### Purpose
*   Provides stateless support logic for OpenAI-compatible AI generation providers without network I/O.
*   Handles request serialization, endpoint construction, secure API key routing, and response parsing.

#### Type
final class `OpenAiCompatibleProviderSupport`
*   Dependencies: `LlamaCppJniConfig`, `AiPromptSupport`, `Function<String, @Nullable String>`
*   Annotations: `@ToString` (excluding internal fields)

#### Input
*   **Constructor**: `LlamaCppJniConfig config`, `AiPromptSupport promptSupport`, `Function<String, @Nullable String> environment`
*   **Method `requestBody`**: `AiGenerationRequest request`
*   **Method `apiKeyFor`**: `String endpoint`
*   **Static Methods**: `int status`, `String host`, `String body`, `String text`, `ChatResponse response`, `double seconds`

#### Output
*   **Return Values**: Serialized JSON `String` (request body), `URL` string (endpoint), `@Nullable String` (API key), `boolean` (success/loopback checks), `AiGenerationTimings` object.
*   **Side Effects**: None (pure functions).
*   **Exceptions**: `IOException` (serialization failure, insecure key transmission), `IllegalArgumentException` (invalid URL scheme).

#### Core logic
*   **Request Construction**: Builds a JSON payload using Jackson `ObjectNode` and `ArrayNode`.
    *   Conditionally adds `model`, `max_tokens`, `temperature`, `top_p`, `top_k`, `min_p`, `repetition_penalty`, `seed`, `stop` strings, and `reasoning.effort` based on configuration values.
    *   Injects system and user messages via `AiPromptSupport`.
    *   Sets `stream` to false.
*   **Endpoint Validation**: Trims base URL, removes trailing slashes, and appends `/chat/completions`. Validates that the scheme is strictly `http://` or `https://`.
*   **API Key Security**:
    *   Retrieves key from environment variable lookup function.
    *   Enforces HTTPS for non-loopback hosts; throws `IOException` if attempting to send cleartext keys over HTTP to external hosts.
    *   Allows HTTP only for `localhost`, `127.x.x.x`, or `[::1]`.
*   **Loopback Detection**: Matches host against exact "localhost", IPv4 regex `127.\d{1,3}...\d{1,3}`, and IPv6 literal `[::1]`.
*   **Error Handling**: Generates error messages by truncating response bodies to 500 characters with an ellipsis.
*   **Timing Calculation**: Computes decode rate as completion tokens divided by wall-clock seconds; sets prefill rate to zero.

#### Public API
*   `requestBody(AiGenerationRequest) -> String` constructs the JSON payload
*   `endpoint() -> String` returns the validated chat-completions URL
*   `apiKey() -> String` retrieves the trimmed environment key
*   `apiKeyFor(String) -> String` secures key transmission logic
*   `isLoopbackHost(String) -> boolean` checks local host identity
*   `isSuccess(int) -> boolean` validates HTTP 2xx status range
*   `errorMessage(String, int, String) -> String` formats server errors
*   `timings(String, ChatResponse, double) -> AiGenerationTimings` calculates throughput metrics

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

#### Exceptions / Errors
*   `IOException`: Thrown if JSON serialization fails or if an API key is sent over unencrypted HTTP to a non-loopback host.
*   `IllegalArgumentException`: Thrown if `baseUrl` does not start with `http://` or `https://`.
*   Null handling: Explicitly checks for null/blank config values and environment lookups before processing.

#### Concurrency
*   No explicit synchronization or thread-safety mechanisms required; class relies on immutable configuration passed at construction and stateless static methods.
