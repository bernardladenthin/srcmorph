### OpenAiCompatibleProviderSupport.java
- H: 1.0
- C: 92F0B02B
- D: 2026-10-29T09:50:36Z
- T: 2026-10-29T09:58:15Z
- G: 
- A: 0.0.0
- X: file
---
> Constructs OpenAI-compatible chat-completions request payloads, resolves secure API keys, and computes generation timings for remote LLM providers.

#### Purpose
- Provides network-agnostic logic for `OpenAiCompatibleAiGenerationProvider`: request serialization, endpoint validation, API key security checks, and response timing calculation.
- Enforces that API keys are never sent over plain HTTP to non-loopback hosts.

#### Type
- `final class OpenAiCompatibleProviderSupport`
- Annotated with `@ToString` (Lombok)
- Package-private visibility

#### Input
- `LlamaCppJniConfig config`: model name, `baseUrl`, `apiKeyEnv`, sampling parameters (`maxOutputTokens`, `temperature`, `topP`, `topK`, `minP`, `repeatPenalty`, `seed`, `stopStrings`, `reasoningEffort`)
- `AiPromptSupport promptSupport`: provides system prompt and user message templates
- `Function<String, @Nullable String> environment`: environment variable lookup function
- `AiGenerationRequest request`: contains `promptKey`, `sourceFile`, `sourceText`
- `ChatResponse response`: contains `Usage` token counts
- `String endpoint`: URL for key security validation
- `int status`, `String body`: HTTP response details for error formatting

#### Output
- `String`: JSON request body for chat completions
- `String`: Validated chat-completions endpoint URL
- `@Nullable String`: API key (null if unset/blank or insecure)
- `boolean`: Loopback host check result
- `boolean`: HTTP 2xx success check
- `String`: Formatted error message with body excerpt
- `AiGenerationTimings`: Token counts and decode rate

#### Core logic
- **Request Body Construction**: Builds JSON with `model`, `messages` (system + user), `max_tokens`, `temperature`, `top_p`, `repetition_penalty`, `stream: false`. Conditionally includes `top_k` (>0), `min_p` (>0.0f), `seed` (>=0), `stop` (non-empty list), and `reasoning.effort` (non-blank).
- **Endpoint Resolution**: Trims `baseUrl`, strips trailing slashes, validates `http://` or `https://` scheme, appends `/chat/completions`.
- **API Key Security**: `apiKeyFor()` checks if endpoint is plain HTTP; if so, parses host and rejects non-loopback hosts (`localhost`, `127.x.x.x`, `[::1]`) with `IOException` to prevent cleartext key transmission.
- **Loopback Detection**: Matches host against `localhost` (exact), IPv4 loopback regex `127\.\d{1,3}\.\d{1,3}\.\d{1,3}`, or IPv6 `[::1]`.
- **Timing Calculation**: Computes `decodeRate` as `completionTokens / seconds` (wall-clock); sets `prefillRate` to 0.0 (unknown for remote).

#### Public API
- `requestBody(AiGenerationRequest) -> String` (throws `IOException`) - Serializes chat request JSON
- `endpoint() -> String` (throws `IllegalArgumentException`) - Builds validated chat URL
- `apiKey() -> @Nullable String` - Retrieves trimmed API key
- `apiKeyFor(String) -> @Nullable String` (throws `IOException`) - Secure key with host check
- `isLoopbackHost(String) -> boolean` - Checks loopback interface
- `isSuccess(int) -> boolean` - Checks 2xx status
- `errorMessage(String, int, String) -> String` - Formats error with excerpt
- `timings(String, ChatResponse, double) -> AiGenerationTimings` - Computes token rates

#### Dependencies
- `com.fasterxml.jackson.databind.ObjectMapper`, `ObjectNode`, `ArrayNode`
- `net.ladenthin.llama.value.ChatResponse`, `Usage`
- `net.ladenthin.srcmorph.document.AiGenerationRequest`
- `net.ladenthin.srcmorph.prompt.AiPromptSupport`
- `net.ladenthin.srcmorph.support.Java8CompatibilityHelper`
- `net.ladenthin.srcmorph.provider.LlamaCppJniConfig`
- `java.net.URL`, `java.util.regex.Pattern`
- `org.jspecify.annotations.Nullable`
- `lombok.ToString`

#### Exceptions / Errors
- `IllegalArgumentException`: `baseUrl` not `http(s)` scheme
- `IOException`: API key sent over plain HTTP to non-loopback host; JSON serialization failure
- `Objects.requireNonNull`: Constructor validates `config`, `promptSupport`, `environment` non-null

#### Concurrency
- `ObjectMapper MAPPER` is static final (thread-safe for read-only serialization)
- Instance fields are final; no mutable state
- Thread-safe for concurrent use per instance
