### OpenAiCompatibleProviderSupport.java
- H: 1.0
- C: 92F0B02B
- D: 2026-11-01T14:41:21Z
- T: 2026-11-01T14:46:53Z
- G: 
- A: 0.0.0
- X: file
---
> Builds and validates the JSON request, endpoint, and API key handling for an OpenAI‑compatible chat‑completion provider, and computes call timings.

#### Purpose
- Construct chat‑completion request bodies.
- Resolve endpoint URLs and API keys safely.
- Provide helper utilities for status checks and error messaging.
- Compute generation timings from responses.

#### Type
`final class OpenAiCompatibleProviderSupport`  
modifiers: final  
extends: none  
implements: none  
annotations: `@ToString`

#### Input
- `LlamaCppJniConfig config` – model settings (remoteModel, baseUrl, apiKeyEnv, sampling knobs).  
- `AiPromptSupport promptSupport` – prompt templates.  
- `Function<String, @Nullable String> environment` – environment variable lookup.  
- `AiGenerationRequest request` – source file and prompt key.  
- `String endpoint` – URL for API call.  
- `String body` – error response body.

#### Output
- JSON string for request body.  
- Endpoint URL string.  
- API key string or `null`.  
- Boolean flags for loopback host and success status.  
- Error message string.  
- `AiGenerationTimings` instance.

#### Core logic
- `requestBody`: builds a JSON object with system/user messages, sampling knobs, optional fields (`top_k`, `min_p`, `seed`, `stop`, `reasoning.effort`), and `stream=false`.  
- `endpoint`: trims `baseUrl`, ensures it starts with `http://` or `https://`, removes trailing slashes, appends `/chat/completions`.  
- `apiKey`: retrieves and trims the key from the environment variable named in `config.apiKeyEnv()`.  
- `apiKeyFor`: validates that a plain `http` key is only sent to loopback hosts; otherwise throws `IOException`.  
- `isLoopbackHost`: checks host against `localhost`, IPv4 127.x.x.x, or IPv6 `[::1]`.  
- `isSuccess`: returns true for 2xx status codes.  
- `errorMessage`: truncates error body to 500 chars, appends ellipsis, and formats a message with endpoint and status.  
- `timings`: extracts token counts from `ChatResponse.usage`, computes decode rate, and constructs `AiGenerationTimings`.

#### Public API
- `String requestBody(AiGenerationRequest request) -> JSON body`  
- `String endpoint() -> URL`  
- `String apiKey() -> key or null`  
- `String apiKeyFor(String endpoint) -> key or null`  
- `static boolean isLoopbackHost(String host) -> true/false`  
- `static boolean isSuccess(int status) -> true/false`  
- `static String errorMessage(String endpoint, int status, String body) -> message`  
- `static AiGenerationTimings timings(String text, ChatResponse response, double seconds) -> timings`

#### Dependencies
- `com.fasterxml.jackson.databind.ObjectMapper`, `ArrayNode`, `ObjectNode`  
- `java.io.IOException`, `java.net.URL`  
- `java.util.List`, `java.util.Objects`, `java.util.function.Function`, `java.util.regex.Pattern`  
- `lombok.ToString`  
- `net.ladenthin.llama.value.ChatResponse`, `net.ladenthin.llama.value.Usage`  
- `net.ladenthin.srcmorph.document.AiGenerationRequest`  
- `net.ladenthin.srcmorph.prompt.AiPromptSupport`  
- `net.ladenthin.srcmorph.support.Java8CompatibilityHelper`  
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- `IllegalArgumentException` if `baseUrl` lacks `http://` or `https://`.  
- `IOException` if a plain `http` key is sent to a non‑loopback host or URL parsing fails.  
- `IOException` from `ObjectMapper.writeValueAsString` in `requestBody`.

#### Concurrency
- All fields are immutable after construction.  
- No mutable shared state; thread‑safe by design.
