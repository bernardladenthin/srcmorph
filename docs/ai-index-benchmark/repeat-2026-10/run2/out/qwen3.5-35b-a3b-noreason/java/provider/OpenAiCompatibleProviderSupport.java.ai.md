### OpenAiCompatibleProviderSupport.java
- H: 1.0
- C: 92F0B02B
- D: 2026-10-29T13:48:14Z
- T: 2026-10-29T14:04:18Z
- G: 
- A: 0.0.0
- X: file
---
> Generates OpenAI-compatible JSON chat request bodies, validates API key transmission security, and parses HTTP response metadata for local AI model inference.

#### Purpose
*   Provides stateless support logic for constructing OpenAI-compatible API requests and handling responses without network I/O.
*   Enforces security constraints on API key transmission over unencrypted connections to non-loopback hosts.

#### Type
final class `OpenAiCompatibleProviderSupport`
*   Fields: `LlamaCppJniConfig config`, `AiPromptSupport promptSupport`, `Function<String, @Nullable String> environment`.
*   Annotations: `@ToString` (Lombok), `@ToString.Exclude` on injected dependencies.
*   Constants: `CHAT_COMPLETIONS_PATH`, `ERROR_BODY_EXCERPT`, `HTTP_SCHEME`, `HTTPS_SCHEME`, `IPV4_LOOPBACK`, `IPV6_LOOPBACK`.

#### Input
*   **Constructor**: `LlamaCppJniConfig config`, `AiPromptSupport promptSupport`, `Function<String, @Nullable String> environment`.
*   **Methods**:
    *   `requestBody(AiGenerationRequest request)`: Source file details, prompt keys, and sampling configuration.
    *   `apiKeyFor(String endpoint)`: Target URL string.
    *   `isLoopbackHost(String host)`: Hostname string.
    *   `errorMessage(String endpoint, int status, String body)`: HTTP status code and error payload.
    *   `timings(String text, ChatResponse response, double seconds)`: Completion text, usage stats, and duration.

#### Output
*   **Return Values**:
    *   Serialized JSON `String` for request bodies.
    *   Constructed `String` endpoint URLs.
    *   Nullable `String` API keys or null if missing/blank.
    *   `boolean` flags for loopback detection and HTTP success status.
    *   `AiGenerationTimings` object containing token counts and decode rates.
*   **Side Effects**: Throws `IOException` on security violations or serialization failures; throws `IllegalArgumentException` on invalid base URLs.

#### Core logic
*   Constructs a Jackson `ObjectNode` by merging system/user prompts from `AiPromptSupport` with sampling parameters (`top_k`, `min_p`, `seed`, `stop`) from `LlamaCppJniConfig`.
*   Serializes the request node to JSON, omitting optional fields (e.g., `model`, `reasoning.effort`) if they are blank or zero.
*   Validates `baseUrl` starts with `http://` or `https://`, strips trailing slashes, and appends `/chat/completions`.
*   Retrieves API keys via the injected `environment` function, returning null if the variable is unset or empty.
*   Blocks sending cleartext API keys over `http://` unless the target host is strictly loopback (`localhost`, `127.x.x.x`, `[::1]`).
*   Parses HTTP 2xx status codes as success; generates truncated error messages for non-2xx responses.
*   Calculates decode throughput by dividing completion tokens by total wall-clock time, setting prefill rate to zero.

#### Public API
*   `requestBody(request) -> String` constructs the JSON payload.
*   `endpoint() -> String` resolves the full API URL.
*   `apiKey() -> String?` retrieves the raw environment variable value.
*   `apiKeyFor(endpoint) -> String?` returns key only if secure or loopback.
*   `isLoopbackHost(host) -> boolean` checks localhost identity.
*   `isSuccess(status) -> boolean` validates HTTP 2xx range.
*   `errorMessage(endpoint, status, body) -> String` formats failure notifications.
*   `timings(text, response, seconds) -> AiGenerationTimings` computes performance metrics.

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
*   `net.ladenthin.srcmorph.support.Java8CompatibilityHelper`
*   `net.ladenthin.srcmorph.provider.LlamaCppJniConfig`
*   `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
*   `IOException`: Thrown by `apiKeyFor` if an API key exists but the endpoint is insecure (non-loopback http); also thrown by `requestBody` on serialization failure.
*   `IllegalArgumentException`: Thrown by `endpoint` if `baseUrl` lacks `http` or `https` scheme.
*   Null safety: Explicitly handles null environment lookups and blank strings using `Java8CompatibilityHelper.isBlank`.

#### Concurrency
*   No explicit synchronization or thread-safety mechanisms; relies on immutable constants and stateless method execution.
*   `ObjectMapper` instance is static final, implying shared usage across instances (standard Jackson behavior).
