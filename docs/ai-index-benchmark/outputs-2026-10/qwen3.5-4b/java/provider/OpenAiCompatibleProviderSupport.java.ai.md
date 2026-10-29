### OpenAiCompatibleProviderSupport.java
- H: 1.0
- C: 92F0B02B
- D: 2026-10-29T00:01:31Z
- T: 2026-10-29T00:03:33Z
- G: 
- A: 0.0.0
- X: file
---
> Provides non-network support for OpenAI-compatible AI generation providers, handling request body serialization, endpoint construction, API key retrieval with security checks, loopback validation, HTTP status parsing, error message generation, and timing calculations.

#### Purpose
- Constructs JSON request bodies for AI generation requests with configurable sampling parameters.
- Validates API key transmission security by enforcing HTTPS or loopback-only connections.

#### Type
- `final class OpenAiCompatibleProviderSupport`
- Fields: `LlamaCppJniConfig config`, `AiPromptSupport promptSupport`, `Function<String, @Nullable String> environment`, `Java8CompatibilityHelper compatibilityHelper`
- Annotations: `@ToString`

#### Input
- `AiGenerationRequest request` (prompt key, source file, source text)
- `LlamaCppJniConfig config` (remote model, base URL, API key env, sampling knobs)
- `AiPromptSupport promptSupport` (system prompt, user message generation)
- `Function<String, @Nullable String> environment` (environment variable lookup)
- `String endpoint` (for API key validation)
- `int status` (HTTP status code)
- `String body` (HTTP response body)
- `String text`, `ChatResponse response`, `double seconds` (for timing)

#### Output
- `String` request body (JSON)
- `String` chat-completions endpoint URL
- `String` API key (trimmed or null)
- `String` API key for secure endpoint (throws `IOException` if insecure)
- `boolean` loopback host check
- `boolean` HTTP success check (2xx)
- `String` error message (endpoint + status + excerpt)
- `AiGenerationTimings` object (token counts, decode rate)

#### Core logic
- Serializes AI generation requests into JSON with model, messages, and sampling parameters (temperature, top_k, top_p, seed, stop, reasoning effort).
- Constructs endpoint URL by trimming base URL, removing trailing slashes, and appending `/chat/completions`.
- Retrieves API key from environment variable if configured and non-blank.
- Validates API key transmission by rejecting cleartext HTTP to non-loopback hosts.
- Checks if a host is loopback (localhost, 127.x.x.x, [::1]).
- Determines HTTP success status (200-299).
- Generates error messages with truncated response body excerpts.
- Calculates generation timings including decode rate based on completion tokens and wall-clock time.

#### Public API
- `requestbody(AiGenerationRequest request) -> String` + serializes request to JSON
- `endpoint() -> String` + constructs chat-completions URL
- `apiKey() -> String` + retrieves API key from environment
- `apiKeyFor(String endpoint) -> String` + retrieves secure API key for endpoint
- `isLoopbackHost(String host) -> boolean` + checks if host is loopback
- `isSuccess(int status) -> boolean` + checks if HTTP status is success
- `errorMessage(String endpoint, int status, String body) -> String` + generates error message
- `timings(String text, ChatResponse response, double seconds) -> AiGenerationTimings` + calculates generation timings

#### Dependencies
- `LlamaCppJniConfig`, `AiPromptSupport`, `AiGenerationRequest`, `AiGenerationTimings`, `ChatResponse`, `Usage`, `Java8CompatibilityHelper`
- `ObjectMapper`, `ArrayNode`, `ObjectNode`, `IOException`, `URL`, `List`, `Pattern`, `Function`
- `net.ladenthin.llama.value.ChatResponse`, `net.ladenthin.llama.value.Usage`, `net.ladenthin.srcmorph.document.AiGenerationRequest`, `net.ladenthin.srcmorph.prompt.AiPromptSupport`, `net.ladenthin.srcmorph.support.Java8CompatibilityHelper`

#### Exceptions / Errors
- `IOException` thrown by `apiKeyFor` when endpoint is plain HTTP to non-loopback host or cannot be parsed
- `IllegalArgumentException` thrown by `endpoint` when base URL is not http(s) scheme
- Null checks on config, promptSupport, and environment in constructor
- Truncated error body excerpted at 500 characters with ellipsis

#### Concurrency
- No concurrency concerns; uses static `ObjectMapper` instance
- Thread-safe static constants and methods
- No synchronization required
