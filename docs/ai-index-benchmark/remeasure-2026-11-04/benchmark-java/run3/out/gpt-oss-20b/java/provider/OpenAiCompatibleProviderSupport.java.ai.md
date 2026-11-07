### OpenAiCompatibleProviderSupport.java
- H: 1.0
- C: 92F0B02B
- D: 2026-11-04T07:43:16Z
- T: 2026-11-04T07:48:28Z
- G: 
- A: 0.0.0
- X: file
- P: 36159BF2
---
> Builds the JSON request, endpoint, and key handling for an OpenAI‑compatible chat‑completion provider, and interprets responses and errors.

#### Purpose
- Constructs request payloads for chat‑completion calls.
- Validates and builds endpoint URLs.
- Retrieves and secures API keys.
- Parses success status, error messages, and timing metrics.

#### Type
`final class OpenAiCompatibleProviderSupport` – class, final, no inheritance, implements no interfaces.

#### Input
- `LlamaCppJniConfig config` – model settings (remoteModel, baseUrl, apiKeyEnv, sampling knobs).
- `AiPromptSupport promptSupport` – prompt templates.
- `Function<String, @Nullable String> environment` – environment variable lookup.
- `AiGenerationRequest request` – file description for request body.
- `String endpoint` – URL for key validation.
- `String host` – host part of a URL for loopback check.
- `int status` – HTTP status code.
- `String body` – error response body.
- `String text, ChatResponse response, double seconds` – for timing calculation.

#### Output
- JSON string for request body.
- Endpoint URL string.
- Optional trimmed API key string.
- Boolean flags for loopback host and success status.
- Error message string.
- `AiGenerationTimings` object with token counts and decode rate.

#### Core logic
- `requestBody`: builds JSON with system/user messages, sampling knobs, optional fields (model, top_k, min_p, seed, stop, reasoning.effort), and disables streaming.
- `endpoint`: trims baseUrl, ensures http/https scheme, removes trailing slashes, appends `/chat/completions`.
- `apiKey`: reads env var, trims, returns null if unset or blank.
- `apiKeyFor`: validates that a plain http endpoint is only used with loopback hosts; otherwise throws `IOException`.
- `isLoopbackHost`: checks host against `localhost`, IPv4 127.x.x.x, or `[::1]`.
- `isSuccess`: true for 200–299 status codes.
- `errorMessage`: truncates body to 500 chars, appends ellipsis, formats message with endpoint and status.
- `timings`: computes completion token count, decode rate, and constructs `AiGenerationTimings`.

#### Public API
- `String requestBody(AiGenerationRequest) -> JSON body` – build request payload.
- `String endpoint() -> URL` – construct chat‑completion endpoint.
- `String apiKey() -> key` – retrieve optional API key.
- `String apiKeyFor(String) -> key` – secure key for endpoint.
- `static boolean isLoopbackHost(String) -> flag` – loopback host check.
- `static boolean isSuccess(int) -> flag` – success status check.
- `static String errorMessage(String,int,String) -> message` – error description.
- `static AiGenerationTimings timings(String,ChatResponse,double) -> timings` – compute call metrics.

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
- `IllegalArgumentException` if `baseUrl` lacks http/https scheme.
- `IOException` if API key sent over plain http to non‑loopback host or URL parsing fails.
- `IOException` from JSON serialization in `requestBody`.

#### Concurrency
- All fields are immutable after construction; no synchronization needed. The class is thread‑safe.
