### OpenAiCompatibleProviderSupport.java
- H: 1.0
- C: 92F0B02B
- D: 2026-10-31T08:11:47Z
- T: 2026-10-31T08:27:09Z
- G: 
- A: 0.0.0
- X: file
---
> Extracts request body, endpoint, API key, status check, error message, and timings for OpenAI-compatible chat-completions providers.

#### Purpose
- Holds non-network logic for OpenAI-compatible AI generation providers.
- Split from provider to keep it fully testable (PIT gate).

#### Type
- final class `OpenAiCompatibleProviderSupport`
- Implements no interface; uses Lombok `@ToString` (excludes config, promptSupport, environment)
- Key generics: none; key type bounds: none
- Notable annotations: `@ToString`, `@Nullable` (org.jspecify)

#### Input
- Constructor: `LlamaCppJniConfig config`, `AiPromptSupport promptSupport`, `Function<String, @Nullable String> environment`
- Method `requestBody(AiGenerationRequest request)`: consumes config, promptSupport, request
- Method `endpoint()`: consumes config
- Method `apiKey()`: consumes config, environment
- Method `apiKeyFor(String endpoint)`: consumes config, environment, endpoint
- Method `errorMessage(String endpoint, int status, String body)`: consumes endpoint, status, body
- Method `timings(String text, ChatResponse response, double seconds)`: consumes text, response, seconds
- Static constants: `CHAT_COMPLETIONS_PATH`, `ERROR_BODY_EXCERPT`, `HTTP_OK_FIRST`, `HTTP_OK_END`, `HTTP_SCHEME`, `HTTPS_SCHEME`, `LOOPBACK_HOST_NAME`, `IPV4_LOOPBACK`, `IPV6_LOOPBACK`, `ELLIPSIS`, `MAPPER`

#### Output
- `requestBody`: String (JSON body)
- `endpoint`: String (chat-completions URL)
- `apiKey`: @Nullable String (trimmed key or null)
- `apiKeyFor`: @Nullable String (key or null)
- `isLoopbackHost`: boolean
- `isSuccess`: boolean
- `errorMessage`: String (error message)
- `timings`: `AiGenerationTimings` (text, promptTokens, cachedTokens, prefillRate, completionTokens, decodeRate)
- Mutated state: none (immutable after construction)

#### Core logic
- Builds chat-completions JSON body from config, promptSupport, and request; includes model, messages, sampling knobs, stop strings, reasoning effort, and stream flag
- Validates baseUrl is http(s); trims trailing slashes; appends chat-completions path
- Looks up API key by environment variable name; returns null if unset or blank
- Refuses to send API key in cleartext over plain http to non-loopback hosts; allows loopback (localhost, 127.x.x.x, [::1])
- Checks HTTP status is 2xx; builds error message with endpoint, status, and truncated body excerpt
- Computes timings from text, response usage, and wall-clock seconds; decode rate = completion tokens / seconds

#### Public API
- `requestBody(AiGenerationRequest) -> String`: builds chat-completions JSON body
- `endpoint() -> String`: builds chat-completions URL from baseUrl
- `apiKey() -> @Nullable String`: returns API key from env variable
- `apiKeyFor(String) -> @Nullable String`: returns key safe for endpoint scheme
- `isLoopbackHost(String) -> boolean`: checks if host is loopback
- `isSuccess(int) -> boolean`: checks if HTTP status is success
- `errorMessage(String, int, String) -> String`: builds error message from status and body
- `timings(String, ChatResponse, double) -> AiGenerationTimings`: computes generation timings

#### Dependencies
- com.fasterxml.jackson.databind.ObjectMapper
- com.fasterxml.jackson.databind.node.ArrayNode
- com.fasterxml.jackson.databind.node.ObjectNode
- net.ladenthin.llama.value.ChatResponse
- net.ladenthin.llama.value.Usage
- net.ladenthin.srcmorph.document.AiGenerationRequest
- net.ladenthin.srcmorph.prompt.AiPromptSupport
- net.ladenthin.srcmorph.support.Java8CompatibilityHelper
- org.jspecify.annotations.Nullable
- java.io.IOException
- java.net.URL
- java.util.List
- java.util.Objects
- java.util.function.Function
- java.util.regex.Pattern
- lombok.ToString

#### Exceptions / Errors
- `IllegalArgumentException`: thrown if baseUrl is not http(s) URL
- `IOException`: thrown if endpoint cannot be parsed or if key is configured and endpoint is plain http to non-loopback host
- Null handling: environment function returns @Nullable String; apiKey returns null if unset or blank; apiKeyFor returns key or null

#### Concurrency
- No concurrency notes; class is stateless after construction and uses no shared mutable state.
