### OpenAiCompatibleProviderSupport.java
- H: 1.0
- C: 92F0B02B
- D: 2026-11-01T01:13:43Z
- T: 2026-11-01T01:19:19Z
- G: 
- A: 0.0.0
- X: file
---
> Provides utilities for building and validating OpenAI‑compatible chat‑completion requests, including request body construction, endpoint resolution, API key handling, loopback checks, status evaluation, error messaging, and timing calculation.

#### Purpose
- Build JSON body for chat‑completion requests.
- Resolve endpoint URL from configuration.
- Retrieve and validate API key usage.
- Determine loopback hosts and HTTP success.
- Generate error messages and compute call timings.

#### Type
`final class OpenAiCompatibleProviderSupport`  
- final, no inheritance.  
- Uses Lombok `@ToString` and `@ToString.Exclude`.  
- Depends on `LlamaCppJniConfig`, `AiPromptSupport`, `Function<String, String>`, `Java8CompatibilityHelper`, Jackson `ObjectMapper`.

#### Input
- `LlamaCppJniConfig config` – model settings (remoteModel, baseUrl, apiKeyEnv, sampling knobs).  
- `AiPromptSupport promptSupport` – prompt templates.  
- `Function<String, @Nullable String> environment` – environment variable lookup.  
- `AiGenerationRequest request` – source file and prompt key.  
- `String endpoint` – URL for API key validation.  
- `String host` – host part of a URL.  
- `int status` – HTTP status code.  
- `String body` – error response body.  
- `String text` – generated completion text.  
- `ChatResponse response` – parsed server response.  
- `double seconds` – wall‑clock duration.

#### Output
- JSON string for request body.  
- Resolved endpoint URL.  
- Trimmed API key or `null`.  
- Boolean flags for loopback host, success status.  
- Error message string.  
- `AiGenerationTimings` instance with token counts and rates.

#### Core logic
- **requestBody**: Assemble `ObjectNode` with model, messages, sampling knobs, optional fields (`top_k`, `min_p`, `seed`, `stop`, `reasoning.effort`), and `stream=false`; serialize to JSON.  
- **endpoint**: Trim `baseUrl`, ensure it starts with `http://` or `https://`, strip trailing slashes, append `/chat/completions`.  
- **apiKey**: Return trimmed key from environment variable if configured and non‑blank.  
- **apiKeyFor**: If key exists and endpoint uses plain `http`, parse host; reject if not loopback, else return key.  
- **isLoopbackHost**: Check host against `localhost`, IPv4 `127.x.x.x`, or IPv6 `[::1]`.  
- **isSuccess**: Return true for 200–299.  
- **errorMessage**: Truncate body to 500 chars, append ellipsis, combine with endpoint and status.  
- **timings**: Compute completion token count, decode rate (`tokens / seconds`), construct `AiGenerationTimings` with prompt, cached, and completion tokens.

#### Public API
- `String requestBody(AiGenerationRequest) -> JSON body` – build request payload.  
- `String endpoint() -> URL` – compute chat‑completion endpoint.  
- `String apiKey() -> key` – fetch API key.  
- `String apiKeyFor(String) -> key` – validate key for endpoint.  
- `static boolean isLoopbackHost(String) -> flag` – loopback check.  
- `static boolean isSuccess(int) -> flag` – HTTP success.  
- `static String errorMessage(String,int,String) -> message` – error description.  
- `static AiGenerationTimings timings(String,ChatResponse,double) -> timings` – compute metrics.

#### Dependencies
- `com.fasterxml.jackson.databind.ObjectMapper`, `ArrayNode`, `ObjectNode`.  
- `java.io.IOException`, `java.net.URL`.  
- `java.util.List`, `java.util.Objects`, `java.util.function.Function`, `java.util.regex.Pattern`.  
- `lombok.ToString`.  
- `net.ladenthin.llama.value.ChatResponse`, `Usage`.  
- `net.ladenthin.srcmorph.document.AiGenerationRequest`.  
- `net.ladenthin.srcmorph.prompt.AiPromptSupport`.  
- `net.ladenthin.srcmorph.support.Java8CompatibilityHelper`.  
- `org.jspecify.annotations.Nullable`.

#### Exceptions / Errors
- `IllegalArgumentException` if `baseUrl` lacks `http(s)` scheme.  
- `IOException` if API key is sent over plain `http` to non‑loopback host or URL parsing fails.  
- `IOException` from `ObjectMapper.writeValueAsString` in `requestBody`.

#### Concurrency
- All fields are immutable after construction; no synchronization needed.  
- `ObjectMapper` is thread‑safe for read operations.
