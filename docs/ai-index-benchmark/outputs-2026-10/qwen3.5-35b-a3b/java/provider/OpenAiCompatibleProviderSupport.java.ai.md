### OpenAiCompatibleProviderSupport.java
- H: 1.0
- C: 92F0B02B
- D: 2026-10-28T22:56:50Z
- T: 2026-10-28T23:27:37Z
- G: 
- A: 0.0.0
- X: file
---
> Constructs OpenAI-compatible chat request payloads, validates secure endpoints, and calculates generation metrics.

#### Purpose
*   Provides stateless logic for constructing OpenAI-compatible HTTP requests and responses.
*   Enforces security constraints on API key transmission over HTTP versus HTTPS.

#### Type
*   `final class OpenAiCompatibleProviderSupport`
*   Modifiers: `final`
*   Annotations: `@ToString` (Lombok)
*   Fields: `private final LlamaCppJniConfig`, `private final AiPromptSupport`, `private final Function<String, @Nullable String>`, `private final Java8CompatibilityHelper`

#### Input
*   Constructor: `LlamaCppJniConfig`, `AiPromptSupport`, `Function<String, @Nullable String>` (environment lookup).
*   `requestBody`: `AiGenerationRequest` (file description, prompt key, source text).
*   `apiKeyFor`: `String` (endpoint URL).
*   `errorMessage`: `String` (endpoint), `int` (HTTP status), `String` (error body).
*   `timings`: `String` (completion text), `ChatResponse`, `double` (wall-clock seconds).

#### Output
*   Returns: `String` (JSON body, URL, API key), `boolean` (loopback check, success status), `AiGenerationTimings`.
*   Mutated fields: None (immutable configuration).
*   Side effects: None (pure functions except injected environment lookup).

#### Core logic
*   Serializes `AiGenerationRequest` into JSON including model name, system/user messages, and sampling knobs (temperature, top_p, seed, stop strings).
*   Validates `baseUrl` starts with `http://` or `https://`, strips trailing slashes, and appends `/chat/completions`.
*   Retrieves API key from environment variable named in `LlamaCppJniConfig`; trims whitespace.
*   Blocks API key transmission over plain HTTP unless host resolves to loopback (`localhost`, `127.x.x.x`, `[::1]`).
*   Truncates error response body to 500 characters plus ellipsis for exception messages.
*   Computes decode rate by dividing completion tokens by call duration; sets prefill rate to zero.

#### Public API
*   `requestBody(AiGenerationRequest) -> String` constructs JSON request body.
*   `endpoint() -> String` builds chat-completions URL.
*   `apiKey() -> String` retrieves trimmed API key from env.
*   `apiKeyFor(String) -> String` returns key or throws if insecure transport.
*   `isLoopbackHost(String) -> boolean` checks local address patterns.
*   `isSuccess(int) -> boolean` checks HTTP 2xx range.
*   `errorMessage(String, int, String) -> String` formats error report.
*   `timings(String, ChatResponse, double) -> AiGenerationTimings` computes throughput stats.
*   Total methods: 8.

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
*   `lombok.ToString`
*   `org.jspecify.annotations.Nullable`
*   `net.ladenthin.llama.value.ChatResponse`
*   `net.ladenthin.llama.value.Usage`
*   `net.ladenthin.srcmorph.document.AiGenerationRequest`
*   `net.ladenthin.srcmorph.prompt.AiPromptSupport`
*   `net.ladenthin.srcmorph.support.Java8CompatibilityHelper`
*   `net.ladenthin.srcmorph.provider.LlamaCppJniConfig`
*   `net.ladenthin.srcmorph.provider.AiGenerationTimings`

#### Exceptions / Errors
*   `IOException` thrown by `requestBody` (serialization failure) and `apiKeyFor` (URL parsing or security violation).
*   `IllegalArgumentException` thrown by `endpoint` (invalid URL scheme).
*   Null checks enforced via `Objects.requireNonNull` in constructor.
*   `@Nullable` return types indicate optional API key presence.

#### Concurrency
*   Static `ObjectMapper` instance shared across all instances.
*   Stateless methods ensure thread safety for logic execution.
*   Configuration fields are immutable after construction.
