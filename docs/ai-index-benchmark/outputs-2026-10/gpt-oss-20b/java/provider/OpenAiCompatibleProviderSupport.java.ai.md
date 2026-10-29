### OpenAiCompatibleProviderSupport.java
- H: 1.0
- C: 92F0B02B
- D: 2026-10-28T21:24:22Z
- T: 2026-10-28T22:50:11Z
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
`final class` with `@ToString`.  
Fields: `config`, `promptSupport`, `environment`, `compatibilityHelper`, `MAPPER`.  
No inheritance or interfaces.

#### Input
- `AiGenerationRequest` for `requestBody`.
- `config` (model config) for all methods.
- `promptSupport` for prompt texts.
- `environment` function for env‑var lookup.
- `String endpoint` for `apiKeyFor`.

#### Output
- JSON string body (`requestBody`).
- Endpoint URL (`endpoint`).
- API key string or `null` (`apiKey`, `apiKeyFor`).
- Boolean success flag (`isSuccess`).
- Error message string (`errorMessage`).
- `AiGenerationTimings` object (`timings`).

#### Core logic
- `requestBody`: constructs JSON with system/user messages, sampling knobs, optional fields (`top_k`, `min_p`, `seed`, `stop`, `reasoning.effort`), and `stream=false`.
- `endpoint`: trims `baseUrl`, ensures `http://` or `https://`, removes trailing slashes, appends `/chat/completions`.
- `apiKey`: fetches env var, trims, returns `null` if unset or blank.
- `apiKeyFor`: validates that a plain `http://` endpoint is loopback; otherwise throws `IOException`.
- `isLoopbackHost`: checks host against `localhost`, IPv4 127.x.x.x, or `[::1]`.
- `isSuccess`: checks 200‑299 status.
- `errorMessage`: truncates body to 500 chars, appends ellipsis if truncated.
- `timings`: calculates decode rate from usage and wall‑clock seconds, returns `AiGenerationTimings`.

#### Public API
- `String requestBody(AiGenerationRequest) -> JSON body`  
- `String endpoint() -> URL`  
- `String apiKey() -> key or null`  
- `String apiKeyFor(String) -> key or null`  
- `boolean isLoopbackHost(String) -> true/false`  
- `boolean isSuccess(int) -> true/false`  
- `String errorMessage(String,int,String) -> message`  
- `AiGenerationTimings timings(String,ChatResponse,double) -> timings`

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
- `IllegalArgumentException` if `baseUrl` lacks `http://` or `https://`.
- `IOException` if API key sent over plain `http` to non‑loopback host or URL parsing fails.
- `IOException` from `ObjectMapper` during body serialization.

#### Concurrency
- Immutable fields; thread‑safe. No mutable shared state.
