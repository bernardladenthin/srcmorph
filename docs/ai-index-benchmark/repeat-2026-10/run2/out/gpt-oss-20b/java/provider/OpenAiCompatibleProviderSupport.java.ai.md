### OpenAiCompatibleProviderSupport.java
- H: 1.0
- C: 92F0B02B
- D: 2026-10-29T13:48:14Z
- T: 2026-10-29T14:13:48Z
- G: 
- A: 0.0.0
- X: file
---
> Provides JSON request construction, endpoint resolution, API key handling, and response timing for OpenAI‑compatible chat‑completion providers.

#### Purpose
- Builds request bodies for chat‑completion APIs.
- Resolves endpoint URLs and validates schemes.
- Retrieves and secures API keys.
- Computes success status and error messages.
- Calculates generation timings.

#### Type
- `final class OpenAiCompatibleProviderSupport`  
  - Extends `Object`  
  - No interfaces or annotations except `@ToString`.

#### Input
- `LlamaCppJniConfig config` – model settings (remoteModel, baseUrl, apiKeyEnv, sampling knobs).  
- `AiPromptSupport promptSupport` – prompt templates.  
- `Function<String, @Nullable String> environment` – environment variable lookup.  
- `AiGenerationRequest request` – source file and prompt key.  
- `String endpoint` – URL for API call.  
- `String body` – error response body.  
- `String text, ChatResponse response, double seconds` – for timing calculation.

#### Output
- JSON string request body.  
- Endpoint URL string.  
- API key string or `null`.  
- Boolean success flag.  
- Error message string.  
- `AiGenerationTimings` object.

#### Core logic
- **requestBody**: builds JSON with system/user messages, sampling knobs, optional fields (`top_k`, `min_p`, `seed`, `stop`, `reasoning.effort`).  
- **endpoint**: trims `baseUrl`, ensures `http(s)`, removes trailing slashes, appends `/chat/completions`.  
- **apiKey**: fetches env var, trims, returns `null` if unset or blank.  
- **apiKeyFor**: validates that plain `http` endpoints are only to loopback hosts; otherwise throws `IOException`.  
- **isLoopbackHost**: checks host against `localhost`, IPv4 127.x.x.x, or `[::1]`.  
- **isSuccess**: true for 200–299.  
- **errorMessage**: truncates body to 500 chars, appends ellipsis, formats message.  
- **timings**: derives token counts from `ChatResponse.usage`, computes decode rate, returns `AiGenerationTimings`.

#### Public API
- `requestBody(AiGenerationRequest) -> String` – build request body.  
- `endpoint() -> String` – compute endpoint URL.  
- `apiKey() -> @Nullable String` – get API key.  
- `apiKeyFor(String) -> @Nullable String` – secure key for endpoint.  
- `isLoopbackHost(String) -> boolean` – loopback check.  
- `isSuccess(int) -> boolean` – success status.  
- `errorMessage(String, int, String) -> String` – error message.  
- `timings(String, ChatResponse, double) -> AiGenerationTimings` – timing calculation.

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
- `IllegalArgumentException` – non‑http(s) `baseUrl`.  
- `IOException` – API key sent over insecure HTTP to non‑loopback host, or URL parsing failure.  
- `IOException` – request body serialization failure.

#### Concurrency
- Immutable fields (`config`, `promptSupport`, `environment`, `compatibilityHelper`).  
- Thread‑safe; no mutable shared state.
