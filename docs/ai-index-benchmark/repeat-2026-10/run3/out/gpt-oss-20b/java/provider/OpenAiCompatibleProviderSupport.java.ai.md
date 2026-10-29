### OpenAiCompatibleProviderSupport.java
- H: 1.0
- C: 92F0B02B
- D: 2026-10-29T14:19:17Z
- T: 2026-10-29T14:53:52Z
- G: 
- A: 0.0.0
- X: file
---
> Provides JSON request construction, endpoint resolution, API key handling, and response timing for OpenAI‑compatible chat‑completion models.

#### Purpose
- Builds request bodies for chat‑completion calls.
- Validates and constructs endpoint URLs.
- Retrieves and secures API keys.
- Generates error messages and success checks.
- Calculates call timings from responses.

#### Type
- `final class OpenAiCompatibleProviderSupport`  
  - No inheritance or interfaces.  
  - Uses Lombok `@ToString` (full and excluded fields).

#### Input
- `LlamaCppJniConfig config` – model settings (remoteModel, baseUrl, apiKeyEnv, sampling knobs).  
- `AiPromptSupport promptSupport` – prompt templates.  
- `Function<String, @Nullable String> environment` – environment variable lookup.  
- `AiGenerationRequest request` – source file and prompt key.  
- `String endpoint` – URL for API call.  
- `String body` – error body string.

#### Output
- JSON string for request body.  
- Endpoint URL string.  
- API key string or `null`.  
- Boolean success flag.  
- Error message string.  
- `AiGenerationTimings` object.

#### Core logic
- `requestBody`: builds JSON with system/user messages, sampling knobs, optional fields (`top_k`, `min_p`, `seed`, `stop`, `reasoning.effort`).  
- `endpoint`: trims `baseUrl`, ensures `http(s)`, removes trailing slashes, appends `/chat/completions`.  
- `apiKey`: fetches env var, trims, returns `null` if unset or blank.  
- `apiKeyFor`: validates that a plain `http` key is only sent to loopback hosts; otherwise throws `IOException`.  
- `isLoopbackHost`: checks host against `localhost`, IPv4 `127.x.x.x`, or IPv6 `[::1]`.  
- `isSuccess`: checks 200–299 status.  
- `errorMessage`: truncates body to 500 chars, appends ellipsis, formats message.  
- `timings`: derives token counts from `ChatResponse.Usage`, computes decode rate, constructs `AiGenerationTimings`.

#### Public API
- `requestBody(AiGenerationRequest) -> String` – build request JSON.  
- `endpoint() -> String` – construct chat‑completion endpoint.  
- `apiKey() -> @Nullable String` – retrieve API key.  
- `apiKeyFor(String) -> @Nullable String` – secure key for endpoint.  
- `isLoopbackHost(String) -> boolean` – check loopback host.  
- `isSuccess(int) -> boolean` – success status check.  
- `errorMessage(String,int,String) -> String` – format error message.  
- `timings(String,ChatResponse,double) -> AiGenerationTimings` – compute timings.

#### Dependencies
- `com.fasterxml.jackson.databind.ObjectMapper`, `ArrayNode`, `ObjectNode`  
- `java.io.IOException`, `java.net.URL`  
- `java.util.List`, `java.util.Objects`, `java.util.function.Function`, `java.util.regex.Pattern`  
- `lombok.ToString`  
- `net.ladenthin.llama.value.ChatResponse`, `Usage`  
- `net.ladenthin.srcmorph.document.AiGenerationRequest`  
- `net.ladenthin.srcmorph.prompt.AiPromptSupport`  
- `net.ladenthin.srcmorph.support.Java8CompatibilityHelper`  
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- `IllegalArgumentException` if `baseUrl` lacks `http(s)` scheme.  
- `IOException` if API key sent over plain `http` to non‑loopback host or URL parsing fails.  
- `IOException` from `ObjectMapper` during request body serialization.

#### Concurrency
- All fields are immutable after construction.  
- No mutable shared state; thread‑safe by design.
