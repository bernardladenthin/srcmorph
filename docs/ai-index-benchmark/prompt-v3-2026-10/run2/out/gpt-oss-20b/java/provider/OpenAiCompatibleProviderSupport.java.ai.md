### OpenAiCompatibleProviderSupport.java
- H: 1.0
- C: 92F0B02B
- D: 2026-10-31T20:10:35Z
- T: 2026-10-31T20:18:56Z
- G: 
- A: 0.0.0
- X: file
---
> Provides the core logic for building OpenAI‑compatible chat‑completion requests, validating endpoints, handling API keys securely, and interpreting responses.

#### Purpose
- Builds JSON request bodies for chat‑completion APIs.
- Validates and constructs endpoint URLs.
- Retrieves and secures API keys.
- Interprets HTTP status and error bodies.
- Calculates generation timings from responses.

#### Type
`final class OpenAiCompatibleProviderSupport`  
- package‑private, final.  
- Uses `@ToString` and `@ToString.Exclude` annotations.  
- Depends on `LlamaCppJniConfig`, `AiPromptSupport`, `Function<String, @Nullable String>`, `Java8CompatibilityHelper`, `ObjectMapper`.

#### Input
- `LlamaCppJniConfig config` – model settings (remoteModel, baseUrl, apiKeyEnv, sampling knobs).  
- `AiPromptSupport promptSupport` – prompt definitions.  
- `Function<String, @Nullable String> environment` – environment variable lookup.  
- `AiGenerationRequest request` – source file and prompt key.  
- `String endpoint` – URL for API call.  
- `String host` – host part of a URL.

#### Output
- JSON string request body.  
- Endpoint URL string.  
- API key string or `null`.  
- Boolean flags for loopback host and success status.  
- Error message string.  
- `AiGenerationTimings` object with token counts and decode rate.

#### Core logic
- **requestBody**: constructs JSON with system/user messages, sampling knobs, optional fields (`top_k`, `min_p`, `seed`, `stop`, `reasoning.effort`).  
- **endpoint**: trims `baseUrl`, ensures `http(s)`, removes trailing slashes, appends `/chat/completions`.  
- **apiKey**: fetches env var, trims, returns `null` if unset or blank.  
- **apiKeyFor**: validates that a plain `http` key is only sent to loopback hosts; otherwise throws `IOException`.  
- **isLoopbackHost**: checks host against `localhost`, IPv4 127.x.x.x, or `[::1]`.  
- **isSuccess**: checks if status is 200‑299.  
- **errorMessage**: builds concise message with endpoint, status, and truncated body.  
- **timings**: derives token counts and decode rate from `ChatResponse` and elapsed seconds.

#### Public API
- `requestBody(AiGenerationRequest) -> String` – build request JSON.  
- `endpoint() -> String` – construct chat‑completion URL.  
- `apiKey() -> @Nullable String` – retrieve API key.  
- `apiKeyFor(String) -> @Nullable String` – secure key for endpoint.  
- `isLoopbackHost(String) -> boolean` – check loopback host.  
- `isSuccess(int) -> boolean` – check HTTP success.  
- `errorMessage(String,int,String) -> String` – format error message.  
- `timings(String,ChatResponse,double) -> AiGenerationTimings` – compute timings.

#### Dependencies
- `com.fasterxml.jackson.databind.ObjectMapper`, `ArrayNode`, `ObjectNode`.  
- `java.io.IOException`, `java.net.URL`.  
- `java.util.List`, `java.util.Objects`, `java.util.function.Function`, `java.util.regex.Pattern`.  
- `lombok.ToString`.  
- `net.ladenthin.llama.value.ChatResponse`, `net.ladenthin.llama.value.Usage`.  
- `net.ladenthin.srcmorph.document.AiGenerationRequest`.  
- `net.ladenthin.srcmorph.prompt.AiPromptSupport`.  
- `net.ladenthin.srcmorph.support.Java8CompatibilityHelper`.  
- `org.jspecify.annotations.Nullable`.

#### Exceptions / Errors
- `IllegalArgumentException` if `baseUrl` lacks `http(s)` scheme.  
- `IOException` if API key sent over plain `http` to non‑loopback host or URL parsing fails.  
- `IOException` from `ObjectMapper` during body serialization.

#### Concurrency
- All fields are immutable after construction.  
- No mutable shared state; thread‑safe by design.
