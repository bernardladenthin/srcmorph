### OpenAiCompatibleProviderSupport.java
- H: 1.0
- C: 92F0B02B
- D: 2026-11-01T14:19:41Z
- T: 2026-11-01T14:26:39Z
- G: 
- A: 0.0.0
- X: file
---
> Provides JSON request construction, endpoint resolution, API key handling, and response timing for OpenAI‑compatible chat‑completion models.

#### Purpose
- Builds request bodies for chat‑completion APIs.
- Validates and constructs endpoint URLs.
- Retrieves and secures API keys.
- Determines success status and error messages.
- Calculates generation timings.

#### Type
`final class OpenAiCompatibleProviderSupport` – class, final.  
Extends `Object`.  
Annotations: `@ToString`.

#### Input
- `LlamaCppJniConfig config` – model config (remoteModel, baseUrl, apiKeyEnv, sampling knobs).  
- `AiPromptSupport promptSupport` – prompt definitions.  
- `Function<String, @Nullable String> environment` – environment variable lookup.  
- `AiGenerationRequest request` – source file and prompt key.  
- `String endpoint` – URL for API call.  
- `String host` – host part of a URL.  
- `int status` – HTTP status code.  
- `String body` – response body.  
- `String text` – generated text.  
- `ChatResponse response` – parsed chat response.  
- `double seconds` – wall‑clock duration.

#### Output
- JSON string for request body.  
- Endpoint URL string.  
- API key string or `null`.  
- Boolean success flag.  
- Error message string.  
- `AiGenerationTimings` instance.

#### Core logic
- `requestBody`: builds JSON with system/user messages, sampling knobs, optional fields (`top_k`, `min_p`, `seed`, `stop`, `reasoning.effort`).  
- `endpoint`: trims baseUrl, ensures http/https scheme, removes trailing slashes, appends `/chat/completions`.  
- `apiKey`: fetches and trims key from environment variable.  
- `apiKeyFor`: validates that a plain http endpoint is only used with loopback hosts; otherwise throws `IOException`.  
- `isLoopbackHost`: checks host against `localhost`, IPv4 127.x.x.x, or `[::1]`.  
- `isSuccess`: true for 200–299.  
- `errorMessage`: truncates body to 500 chars, appends ellipsis if truncated.  
- `timings`: computes decode rate from completion tokens and elapsed seconds; prefill rate set to 0.

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
`com.fasterxml.jackson.databind.ObjectMapper`, `ArrayNode`, `ObjectNode`; `java.io.IOException`; `java.net.URL`; `java.util.List`; `java.util.Objects`; `java.util.function.Function`; `java.util.regex.Pattern`; `lombok.ToString`; `net.ladenthin.llama.value.ChatResponse`; `net.ladenthin.llama.value.Usage`; `net.ladenthin.srcmorph.document.AiGenerationRequest`; `net.ladenthin.srcmorph.prompt.AiPromptSupport`; `net.ladenthin.srcmorph.support.Java8CompatibilityHelper`; `org.jspecify.annotations.Nullable`.

#### Exceptions / Errors
- `IllegalArgumentException` if `baseUrl` lacks http/https scheme.  
- `IOException` if API key sent over plain http to non‑loopback host or URL parsing fails.  
- `IOException` from `ObjectMapper` during JSON serialization.

#### Concurrency
- Immutable configuration fields; thread‑safe read‑only operations.  
- No mutable shared state beyond local variables.
