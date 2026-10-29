### OpenAiCompatibleProviderSupport.java
- H: 1.0
- C: 92F0B02B
- D: 2026-10-29T14:19:17Z
- T: 2026-10-29T14:47:50Z
- G: 
- A: 0.0.0
- X: file
---
> Constructs OpenAI-compatible chat-completions request payloads, resolves secure API keys, and computes generation timings for remote LLM providers.

#### Purpose
- Provides network-agnostic logic for `OpenAiCompatibleAiGenerationProvider`: request serialization, endpoint validation, API key security checks, and response timing calculation.
- Separated from the main provider to ensure 100% mutation testing coverage (PIT gate) for pure logic.

#### Type
- `final class` `OpenAiCompatibleProviderSupport`
- Package-private visibility
- Annotated with `@ToString` (Lombok)

#### Input
- **Constructor**: `LlamaCppJniConfig config`, `AiPromptSupport promptSupport`, `Function<String, @Nullable String> environment`
- **Methods**: `AiGenerationRequest request`, `String endpoint`, `int status`, `String body`, `String text`, `ChatResponse response`, `double seconds`
- **Dependencies**: `ObjectMapper` (static), `Java8CompatibilityHelper` (instance)

#### Output
- **`requestBody`**: `String` (JSON payload)
- **`endpoint`**: `String` (full URL)
- **`apiKey` / `apiKeyFor`**: `@Nullable String`
- **`isLoopbackHost` / `isSuccess`**: `boolean`
- **`errorMessage`**: `String`
- **`timings`**: `AiGenerationTimings`

#### Core logic
- **Request Construction**: Builds JSON object with `model`, `messages` (system/user), `max_tokens`, `temperature`, `top_p`, `repetition_penalty`, `stream: false`. Conditionally includes `top_k` (>0), `min_p` (>0.0f), `seed` (>=0), `stop` (non-empty list), and `reasoning.effort` (non-blank).
- **Endpoint Resolution**: Trims `baseUrl`, strips trailing slashes, appends `/chat/completions`. Validates scheme is `http://` or `https://`.
- **API Key Security**: Retrieves key from environment variable. If key exists and endpoint is plain `http://`, verifies host is loopback (`localhost`, `127.x.x.x`, `[::1]`). Throws `IOException` if key would be sent in cleartext to a non-loopback host.
- **Timing Calculation**: Computes `decodeRate` as `completionTokens / seconds`. Sets `prefillRate` to `0.0d` (unknown). Extracts token counts from `ChatResponse.Usage`.
- **Error Formatting**: Truncates error body to 500 chars with ellipsis, formats as `endpoint + " answered HTTP " + status + ": " + excerpt`.

#### Public API
- `requestBody(AiGenerationRequest) -> String` (Builds JSON request body)
- `endpoint() -> String` (Resolves chat completions URL)
- `apiKey() -> @Nullable String` (Retrieves environment API key)
- `apiKeyFor(String) -> @Nullable String` (Validates key transmission security)
- `isLoopbackHost(String) -> boolean` (Checks host is local loopback)
- `isSuccess(int) -> boolean` (Checks HTTP 2xx status)
- `errorMessage(String, int, String) -> String` (Formats error response message)
- `timings(String, ChatResponse, double) -> AiGenerationTimings` (Calculates generation metrics)

#### Dependencies
- `com.fasterxml.jackson.databind.ObjectMapper`
- `com.fasterxml.jackson.databind.node.ArrayNode`
- `com.fasterxml.jackson.databind.node.ObjectNode`
- `java.net.URL`
- `java.util.regex.Pattern`
- `net.ladenthin.llama.value.ChatResponse`
- `net.ladenthin.llama.value.Usage`
- `net.ladenthin.srcmorph.document.AiGenerationRequest`
- `net.ladenthin.srcmorph.prompt.AiPromptSupport`
- `net.ladenthin.srcmorph.support.Java8CompatibilityHelper`
- `org.jspecify.annotations.Nullable`
- `lombok.ToString`

#### Exceptions / Errors
- `IllegalArgumentException`: Thrown by `endpoint()` if `baseUrl` is not `http(s)` scheme.
- `IOException`: Thrown by `requestBody()` on serialization failure; by `apiKeyFor()` if API key would be sent in cleartext to non-loopback host or URL parsing fails.
- Null checks: Constructor uses `Objects.requireNonNull` for `config`, `promptSupport`, `environment`.

#### Concurrency
- Thread-safe: All methods are stateless or use immutable/static fields. `ObjectMapper` is thread-safe for read operations. No mutable instance state modified after construction.
