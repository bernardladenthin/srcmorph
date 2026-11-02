### OpenAiCompatibleProviderSupport.java
- H: 1.0
- C: 92F0B02B
- D: 2026-11-01T23:42:40Z
- T: 2026-11-01T23:48:14Z
- G: 
- A: 0.0.0
- X: file
- P: C721BDEF
---
> Builds JSON request bodies, validates endpoints, secures API keys, and interprets responses for an OpenAI‑compatible chat‑completion provider.

#### Purpose
- Construct request payloads for chat‑completion calls.
- Validate and build endpoint URLs.
- Securely provide API keys.
- Interpret HTTP status and error bodies.
- Compute generation timings.

#### Type
`final class OpenAiCompatibleProviderSupport` – class, final.  
Extends `Object`.  
Annotations: `@ToString`.

#### Input
- `LlamaCppJniConfig config` – model config (remoteModel, baseUrl, apiKeyEnv, sampling knobs).  
- `AiPromptSupport promptSupport` – prompt definitions.  
- `Function<String, @Nullable String> environment` – environment variable lookup.  
- `AiGenerationRequest request` – source file and prompt key.  
- `String endpoint` – URL for request.  
- `String body` – error body string.

#### Output
- JSON string body (`String`).  
- Endpoint URL (`String`).  
- API key (`String` or `null`).  
- Boolean success flag.  
- Error message string.  
- `AiGenerationTimings` instance.

#### Core logic
- `requestBody`: builds JSON with system/user messages, sampling knobs, optional fields (`top_k`, `min_p`, `seed`, `stop`, `reasoning.effort`).  
- `endpoint`: trims baseUrl, ensures http/https scheme, removes trailing slashes, appends `/chat/completions`.  
- `apiKey`: retrieves and trims key from environment, returns `null` if unset or blank.  
- `apiKeyFor`: validates that a plain‑http endpoint is loopback; otherwise throws `IOException`.  
- `isLoopbackHost`: checks host against `localhost`, IPv4 127.x.x.x, or `[::1]`.  
- `isSuccess`: true for 200–299.  
- `errorMessage`: truncates body to 500 chars, appends ellipsis, formats message.  
- `timings`: calculates completion tokens, decode rate, returns `AiGenerationTimings`.

#### Public API
- `String requestBody(AiGenerationRequest) -> JSON body` – build request payload.  
- `String endpoint() -> URL` – construct chat‑completion endpoint.  
- `String apiKey() -> key` – fetch API key.  
- `String apiKeyFor(String) -> key` – secure key for endpoint.  
- `static boolean isLoopbackHost(String) -> flag` – loopback host check.  
- `static boolean isSuccess(int) -> flag` – HTTP success check.  
- `static String errorMessage(String, int, String) -> message` – format error.  
- `static AiGenerationTimings timings(String, ChatResponse, double) -> timings` – compute timings.

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
- `IllegalArgumentException` if `baseUrl` lacks http/https scheme.  
- `IOException` if API key sent over plain http to non‑loopback host or URL parsing fails.  
- `IOException` from `ObjectMapper` during JSON serialization.

#### Concurrency
- Immutable fields (`config`, `promptSupport`, `environment`, `compatibilityHelper`).  
- Thread‑safe; no mutable shared state.
