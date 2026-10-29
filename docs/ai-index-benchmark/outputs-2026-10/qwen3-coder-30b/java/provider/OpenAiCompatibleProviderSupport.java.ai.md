### OpenAiCompatibleProviderSupport.java
- H: 1.0
- C: 92F0B02B
- D: 2026-10-28T21:24:22Z
- T: 2026-10-28T22:16:18Z
- G: 
- A: 0.0.0
- X: file
---
> Generates OpenAI-compatible API request bodies and handles endpoint, key, and error logic for AI model interactions.

#### Purpose
- Constructs API request bodies for chat completions.
- Validates and formats API endpoints and keys for secure transmission.
- Processes server responses for timing and error handling.

#### Type
Final class `OpenAiCompatibleProviderSupport` implementing no interfaces. Uses `@ToString` annotation. Depends on `LlamaCppJniConfig`, `AiPromptSupport`, and `Function<String, @Nullable String>` for environment lookup.

#### Input
- Constructor parameters: `LlamaCppJniConfig`, `AiPromptSupport`, `Function<String, @Nullable String> environment`.
- `requestBody()` method parameter: `AiGenerationRequest`.
- `endpoint()` method uses `config.baseUrl()`.
- `apiKeyFor()` method parameter: `String endpoint`.
- `errorMessage()` method parameters: `String endpoint`, `int status`, `String body`.
- `timings()` method parameters: `String text`, `ChatResponse response`, `double seconds`.

#### Output
- `requestBody()` returns `String` (JSON request body).
- `endpoint()` returns `String` (formatted endpoint URL).
- `apiKey()` returns `@Nullable String` (API key or null).
- `apiKeyFor()` returns `@Nullable String` (API key or null, throws IOException).
- `isLoopbackHost()` returns `boolean`.
- `isSuccess()` returns `boolean`.
- `errorMessage()` returns `String` (error message).
- `timings()` returns `AiGenerationTimings`.

#### Core logic
- Builds JSON request body with system and user messages, sampling parameters, and optional fields.
- Constructs endpoint URL by appending `/chat/completions` to base URL, trimming slashes.
- Retrieves API key from environment variable, returning null if unset or blank.
- Validates that API key is not sent over HTTP to non-loopback hosts.
- Determines if a host is a loopback interface (localhost, 127.x.x.x, or ::1).
- Checks if HTTP status is in the 2xx success range.
- Formats error messages with endpoint, status, and body excerpt.
- Calculates token timings from response usage and wall-clock time.

#### Public API
- `requestBody(AiGenerationRequest) -> String`
- `endpoint() -> String`
- `apiKey() -> @Nullable String`
- `apiKeyFor(String) -> @Nullable String`
- `isLoopbackHost(String) -> boolean`
- `isSuccess(int) -> boolean`
- `errorMessage(String, int, String) -> String`
- `timings(String, ChatResponse, double) -> AiGenerationTimings`

#### Dependencies
- `com.fasterxml.jackson.databind.ObjectMapper`
- `com.fasterxml.jackson.databind.node.ArrayNode`
- `com.fasterxml.jackson.databind.node.ObjectNode`
- `java.io.IOException`
- `java.net.URL`
- `java.util.List`
- `java.util.Objects`
- `java.util.function.Function`
- `java.util.regex.Pattern`
- `lombok.ToString`
- `net.ladenthin.llama.value.ChatResponse`
- `net.ladenthin.llama.value.Usage`
- `net.ladenthin.srcmorph.document.AiGenerationRequest`
- `net.ladenthin.srcmorph.prompt.AiPromptSupport`
- `net.ladenthin.srcmorph.support.Java8CompatibilityHelper`
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- `IllegalArgumentException` thrown in `endpoint()` for invalid URL schemes.
- `IOException` thrown in `apiKeyFor()` for non-loopback HTTP endpoints with API keys.

#### Concurrency
- No explicit concurrency handling; class is effectively immutable after construction.
