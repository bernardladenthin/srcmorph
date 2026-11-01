### OpenAiCompatibleProviderSupport.java
- H: 1.0
- C: 92F0B02B
- D: 2026-11-01T01:35:52Z
- T: 2026-11-01T01:41:13Z
- G: 
- A: 0.0.0
- X: file
---
> Provides JSON request construction, endpoint resolution, API key handling, and response timing for OpenAI‑compatible chat‑completion providers.

#### Purpose
- Builds request bodies for chat‑completion calls.
- Resolves endpoint URLs and validates schemes.
- Retrieves and secures API keys.
- Determines success status and formats error messages.
- Computes generation timings from responses.

#### Type
`final class OpenAiCompatibleProviderSupport` – class, final.  
Extends `Object`.  
Annotations: `@ToString`.

#### Input
- `LlamaCppJniConfig config` – model settings (remoteModel, baseUrl, apiKeyEnv, sampling knobs).  
- `AiPromptSupport promptSupport` – prompt definitions.  
- `Function<String, @Nullable String> environment` – environment variable lookup.  
- `AiGenerationRequest request` – source file and prompt key.  
- `String endpoint` – URL for API call.  
- `String body` – error body string.  
- `String text, ChatResponse response, double seconds` – response data for timing.

#### Output
- JSON string request body.  
- Endpoint URL string.  
- API key string or `null`.  
- Boolean success flag.  
- Error message string.  
- `AiGenerationTimings` object.

#### Core logic
- `requestBody`: builds JSON with system/user messages, sampling knobs, optional fields (`top_k`, `min_p`, `seed`, `stop`, `reasoning.effort`).  
- `endpoint`: trims `baseUrl`, ensures `http(s)`, removes trailing slashes, appends `/chat/completions`.  
- `apiKey`: fetches env var, trims, returns `null` if unset/blank.  
- `apiKeyFor`: validates that plain `http` endpoints are loopback; otherwise throws `IOException`.  
- `isLoopbackHost`: checks host against `localhost`, IPv4 loopback regex, or `[::1]`.  
- `isSuccess`: checks 200‑299 status.  
- `errorMessage`: truncates body to 500 chars, appends ellipsis, formats message.  
- `timings`: extracts token counts from `ChatResponse`, computes decode rate, returns `AiGenerationTimings`.

#### Public API
- `requestBody(AiGenerationRequest) -> String` – build request body.  
- `endpoint() -> String` – compute endpoint URL.  
- `apiKey() -> @Nullable String` – retrieve API key.  
- `apiKeyFor(String) -> @Nullable String` – secure key for endpoint.  
- `isLoopbackHost(String) -> boolean` – check loopback host.  
- `isSuccess(int) -> boolean` – success status.  
- `errorMessage(String,int,String) -> String` – format error.  
- `timings(String,ChatResponse,double) -> AiGenerationTimings` – compute timings.

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
- `IOException` if API key sent over plain `http` to non‑loopback host or URL parsing fails.  
- `IOException` from `ObjectMapper` during body serialization.

#### Concurrency
- Immutable fields (`config`, `promptSupport`, `environment`, `compatibilityHelper`).  
- Thread‑safe; no mutable shared state.
