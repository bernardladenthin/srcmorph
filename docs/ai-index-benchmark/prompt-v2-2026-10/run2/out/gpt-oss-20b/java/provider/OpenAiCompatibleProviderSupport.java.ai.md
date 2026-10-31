### OpenAiCompatibleProviderSupport.java
- H: 1.0
- C: 92F0B02B
- D: 2026-10-31T14:44:17Z
- T: 2026-10-31T14:52:28Z
- G: 
- A: 0.0.0
- X: file
---
> Provides core logic for building OpenAI‑compatible chat‑completion requests, validating endpoints, handling API keys securely, and interpreting responses.

#### Purpose
- Build JSON request bodies for chat‑completion APIs.
- Validate and construct endpoint URLs.
- Resolve and protect API keys.
- Interpret HTTP responses and compute generation timings.

#### Type
`final class OpenAiCompatibleProviderSupport` – class, final, no inheritance, no interfaces.

#### Input
- `LlamaCppJniConfig config` – model settings (remoteModel, baseUrl, apiKeyEnv, sampling knobs).
- `AiPromptSupport promptSupport` – prompt templates.
- `Function<String, @Nullable String> environment` – environment variable lookup.
- `AiGenerationRequest request` – source file and prompt key.
- `String endpoint` – URL for key validation.
- `String body` – error body for message construction.

#### Output
- JSON string body (`String`).
- Endpoint URL (`String`).
- API key (`String` or `null`).
- Boolean flags for loopback host and success status.
- Error message string.
- `AiGenerationTimings` instance.

#### Core logic
- `requestBody`: assemble messages, model, tokens, temperature, top‑p, top‑k, min‑p, repetition penalty, seed, stop strings, reasoning effort, stream flag; serialize with Jackson.
- `endpoint`: trim baseUrl, enforce http/https scheme, strip trailing slashes, append `/chat/completions`.
- `apiKey`: read env var, trim, return `null` if unset or blank.
- `apiKeyFor`: ensure key sent only over https or http to loopback hosts; otherwise throw `IOException`.
- `isLoopbackHost`: check host against `localhost`, IPv4 127.x.x.x, or `[::1]`.
- `isSuccess`: 200‑299 status range.
- `errorMessage`: truncate body to 500 chars, append ellipsis, format with endpoint and status.
- `timings`: compute decode rate from completion tokens and elapsed seconds; construct `AiGenerationTimings`.

#### Public API
- `String requestBody(AiGenerationRequest) -> JSON body` – build request payload.
- `String endpoint() -> URL` – construct chat‑completion endpoint.
- `String apiKey() -> key` – resolve API key.
- `String apiKeyFor(String) -> key` – secure key transmission.
- `static boolean isLoopbackHost(String) -> flag` – loopback host check.
- `static boolean isSuccess(int) -> flag` – HTTP success check.
- `static String errorMessage(String, int, String) -> message` – error formatting.
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
- `IOException` if API key sent over insecure HTTP to non‑loopback host or URL parsing fails.
- `IOException` from Jackson during body serialization.

#### Concurrency
- Immutable fields (`config`, `promptSupport`, `environment`, `compatibilityHelper`, `MAPPER`) ensure thread safety. No mutable shared state.
