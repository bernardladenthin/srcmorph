### OpenAiCompatibleProviderSupport.java
- H: 1.0
- C: 92F0B02B
- D: 2026-11-01T01:56:59Z
- T: 2026-11-01T02:05:01Z
- G: 
- A: 0.0.0
- X: file
---
> Provides JSON request construction, endpoint resolution, API key handling, and response timing for OpenAI‑compatible chat‑completion models.

#### Purpose
- Builds request bodies for chat‑completion calls.
- Resolves endpoint URLs and validates schemes.
- Retrieves and secures API keys.
- Determines success status and formats error messages.
- Calculates generation timings from responses.

#### Type
`final class OpenAiCompatibleProviderSupport`  
- extends `Object`  
- annotations: `@ToString` (excludes `promptSupport`, `environment`)  
- fields: `config`, `promptSupport`, `environment`, `compatibilityHelper`, `MAPPER`  
- constructor: package‑private

#### Input
- `AiGenerationRequest request` → JSON body
- `String endpoint` → API key validation
- `String host` → loopback check
- `int status` → success check
- `String body` → error message excerpt
- `String text, ChatResponse response, double seconds` → timing calculation

#### Output
- JSON string body (`requestBody`)
- Endpoint URL (`endpoint`)
- API key string or `null` (`apiKey`, `apiKeyFor`)
- Boolean success flag (`isSuccess`)
- Error message string (`errorMessage`)
- `AiGenerationTimings` object (`timings`)

#### Core logic
- **requestBody**: constructs JSON with system/user messages, sampling knobs, optional fields (`top_k`, `min_p`, `seed`, `stop`, `reasoning.effort`), and `stream=false`.
- **endpoint**: trims `baseUrl`, ensures `http://` or `https://`, removes trailing slashes, appends `/chat/completions`.
- **apiKey**: fetches env var, trims, returns `null` if unset or blank.
- **apiKeyFor**: allows key only over HTTPS or plain HTTP to loopback hosts; otherwise throws `IOException`.
- **isLoopbackHost**: checks host against `localhost`, IPv4 `127.x.x.x`, or IPv6 `[::1]`.
- **isSuccess**: true for 200–299.
- **errorMessage**: truncates body to 500 chars, appends ellipsis, formats message.
- **timings**: derives prompt, cached, completion token counts; computes decode rate; returns `AiGenerationTimings`.

#### Public API
- `String requestBody(AiGenerationRequest) -> JSON body`  
- `String endpoint() -> URL`  
- `String apiKey() -> key or null`  
- `String apiKeyFor(String) -> key or null`  
- `static boolean isLoopbackHost(String) -> true/false`  
- `static boolean isSuccess(int) -> true/false`  
- `static String errorMessage(String,int,String) -> message`  
- `static AiGenerationTimings timings(String,ChatResponse,double) -> timings`

#### Dependencies
- `com.fasterxml.jackson.databind.ObjectMapper`, `ArrayNode`, `ObjectNode`  
- `java.io.IOException`, `java.net.URL`, `java.util.List`, `java.util.Objects`, `java.util.function.Function`, `java.util.regex.Pattern`  
- `lombok.ToString`  
- `net.ladenthin.llama.value.ChatResponse`, `net.ladenthin.llama.value.Usage`  
- `net.ladenthin.srcmorph.document.AiGenerationRequest`  
- `net.ladenthin.srcmorph.prompt.AiPromptSupport`  
- `net.ladenthin.srcmorph.support.Java8CompatibilityHelper`  
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- `IllegalArgumentException` if `baseUrl` lacks `http(s)` scheme.  
- `IOException` if API key sent over plain HTTP to non‑loopback host or URL parsing fails.  
- `IOException` from JSON serialization in `requestBody`.

#### Concurrency
- All fields are immutable after construction.  
- No mutable shared state; thread‑safe.
