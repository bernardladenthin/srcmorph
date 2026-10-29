### OpenAiCompatibleProviderSupport.java
- H: 1.0
- C: 92F0B02B
- D: 2026-10-29T13:48:14Z
- T: 2026-10-29T14:07:47Z
- G: 
- A: 0.0.0
- X: file
---
> Constructs OpenAI-compatible chat-completions request payloads, resolves secure API keys, and computes generation timings for remote LLM providers.

#### Purpose
- Encapsulates non-network logic for `OpenAiCompatibleAiGenerationProvider`: request body serialization, endpoint construction, API key security validation, HTTP status checking, and performance metric calculation.
- Ensures API keys are never transmitted over unencrypted HTTP to non-loopback hosts.

#### Type
- `final class OpenAiCompatibleProviderSupport`
- Annotated with `@ToString` (Lombok)
- Package-private visibility

#### Input
- `LlamaCppJniConfig config`: Model configuration including `remoteModel`, `baseUrl`, `apiKeyEnv`, `maxOutputTokens`, `temperature`, `topP`, `topK`, `minP`, `repeatPenalty`, `seed`, `stopStrings`, `reasoningEffort`
- `AiPromptSupport promptSupport`: Provides `systemPrompt` and `userMessage` content
- `Function<String, @Nullable String> environment`: Lookup function for environment variables
- `AiGenerationRequest request`: Contains `promptKey`, `sourceFile`, `sourceText`
- `ChatResponse response`: Contains `Usage` data for timing calculations
- `String endpoint`: URL for key security validation
- `int status`, `String body`: HTTP response data for error message construction

#### Output
- `String`: JSON-serialized request body
- `String`: Constructed chat-completions endpoint URL
- `@Nullable String`: API key (null if unset/blank)
- `boolean`: Loopback host check result
- `boolean`: HTTP success status check
- `String`: Formatted error message
- `AiGenerationTimings`: Object containing token counts and decode rate

#### Core logic
- **Request Body Construction**: Builds JSON object with `model` (if non-blank), `messages` array (system + user roles), `max_tokens`, `temperature`, `top_p`, `repetition_penalty`, `stream: false`. Conditionally includes `top_k` (>0), `min_p` (>0.0f), `seed` (>=0), `stop` array (non-empty), and `reasoning.effort` (non-blank).
- **Endpoint Resolution**: Trims `baseUrl`, validates `http://` or `https://` prefix, strips trailing slashes, appends `/chat/completions`.
- **API Key Security**: Retrieves key from environment variable named by `apiKeyEnv`. If key exists and endpoint uses `http://` (not `https://`), validates host is loopback (`localhost`, `127.x.x.x`, `[::1]`). Throws `IOException` if key would be sent in cleartext to non-loopback host.
- **Loopback Detection**: Checks exact match for `localhost`, regex match for `127.\d{1,3}.\d{1,3}.\d{1,3}`, or exact match for `[::1]`.
- **Timing Calculation**: Computes `decodeRate` as `completionTokens / seconds` (0 if seconds <= 0). Sets `prefillRate` to 0.0 (unknown). Extracts `promptTokens` and `cachedTokens` from `Usage`.

#### Public API
- `requestBody(AiGenerationRequest) -> String` - Serializes chat request JSON
- `endpoint() -> String` - Builds chat-completions URL
- `apiKey() -> @Nullable String` - Retrieves trimmed API key
- `apiKeyFor(String) -> @Nullable String` - Validates key transmission security
- `isLoopbackHost(String) -> boolean` - Checks loopback interface host
- `isSuccess(int) -> boolean` - Validates 2xx HTTP status
- `errorMessage(String, int, String) -> String` - Formats HTTP error message
- `timings(String, ChatResponse, double) -> AiGenerationTimings` - Calculates generation performance metrics

#### Dependencies
- `com.fasterxml.jackson.databind.ObjectMapper`
- `com.fasterxml.jackson.databind.node.ArrayNode`
- `com.fasterxml.jackson.databind.node.ObjectNode`
- `java.net.URL`
- `java.util.regex.Pattern`
- `lombok.ToString`
- `net.ladenthin.llama.value.ChatResponse`
- `net.ladenthin.llama.value.Usage`
- `net.ladenthin.srcmorph.document.AiGenerationRequest`
- `net.ladenthin.srcmorph.prompt.AiPromptSupport`
- `net.ladenthin.srcmorph.support.Java8CompatibilityHelper`
- `org.jspecify.annotations.Nullable`
- `LlamaCppJniConfig`
- `AiGenerationTimings`

#### Exceptions / Errors
- `IOException`: Thrown by `requestBody` if JSON serialization fails; thrown by `apiKeyFor` if API key would be sent over plain HTTP to non-loopback host or if endpoint URL parsing fails
- `IllegalArgumentException`: Thrown by `endpoint` if `baseUrl` does not start with `http://` or `https://`
- `NullPointerException`: Prevented via `Objects.requireNonNull` in constructor for `config`, `promptSupport`, `environment`

#### Concurrency
- Thread-safe: All fields are `final` and initialized in constructor
- `ObjectMapper` is thread-safe for read operations
- No mutable state after construction
