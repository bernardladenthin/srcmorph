### OpenAiCompatibleProviderSupport.java
- H: 1.0
- C: 92F0B02B
- D: 2026-11-04T07:27:53Z
- T: 2026-11-04T07:33:29Z
- G: 
- A: 0.0.0
- X: file
- P: 36159BF2
---
> Builds the JSON request, endpoint, and key handling for an OpenAI‑compatible chat‑completion provider, and interprets responses and errors.

#### Purpose
- Constructs chat‑completion request bodies.
- Validates and builds endpoint URLs.
- Retrieves and secures API keys.
- Parses success status and error messages.
- Computes generation timings.

#### Type
`final class OpenAiCompatibleProviderSupport` – class, final, no inheritance, no interfaces.

#### Input
- `LlamaCppJniConfig config` – model URL, key env, sampling knobs.
- `AiPromptSupport promptSupport` – system/user prompt templates.
- `Function<String, @Nullable String> environment` – env var lookup.
- `AiGenerationRequest request` – source file and text.
- `String endpoint` – URL for key validation.
- `String endpoint, int status, String body` – for error message.
- `String text, ChatResponse response, double seconds` – for timings.

#### Output
- JSON string body (`requestBody`).
- Endpoint URL (`endpoint`).
- Optional trimmed API key (`apiKey`, `apiKeyFor`).
- Boolean success flag (`isSuccess`).
- Error message string (`errorMessage`).
- `AiGenerationTimings` object (`timings`).

#### Core logic
- `requestBody`: builds JSON with model, messages, token limits, temperature, top‑p, top‑k, min‑p, repetition penalty, seed, stop strings, reasoning effort, stream flag.
- `endpoint`: trims base URL, ensures http/https scheme, removes trailing slashes, appends `/chat/completions`.
- `apiKey`: reads env var, trims, returns null if unset or blank.
- `apiKeyFor`: ensures key is only sent over https or to loopback hosts; otherwise throws `IOException`.
- `isLoopbackHost`: checks host against `localhost`, IPv4 127.x.x.x, or `[::1]`.
- `isSuccess`: true for 200–299.
- `errorMessage`: truncates body to 500 chars, appends ellipsis, formats message.
- `timings`: calculates completion tokens, decode rate, constructs `AiGenerationTimings`.

#### Public API
- `requestBody(AiGenerationRequest) -> String` – build request JSON.
- `endpoint() -> String` – build chat‑completion URL.
- `apiKey() -> @Nullable String` – fetch API key.
- `apiKeyFor(String) -> @Nullable String` – secure key for endpoint.
- `isLoopbackHost(String) -> boolean` – check loopback host.
- `isSuccess(int) -> boolean` – check HTTP success.
- `errorMessage(String, int, String) -> String` – format error.
- `timings(String, ChatResponse, double) -> AiGenerationTimings` – compute timings.

#### Dependencies
- `com.fasterxml.jackson.databind.ObjectMapper`, `ArrayNode`, `ObjectNode`
- `java.io.IOException`, `java.net.URL`
- `java.util.List`, `java.util.Objects`, `java.util.function.Function`, `java.util.regex.Pattern`
- `lombok.ToString`
- `net.ladenthin.llama.value.ChatResponse`, `net.ladenthin.llama.value.Usage`
- `net.ladenthin.srcmorph.document.AiGenerationRequest`
- `net.ladenthin.srcmorph.prompt.AiPromptSupport`
- `net.ladenthin.srcmorph.support.Java8CompatibilityHelper`
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- `IllegalArgumentException` if `baseUrl` lacks http/https scheme.
- `IOException` if API key sent over insecure non‑loopback HTTP or URL parsing fails.
- `IOException` in `requestBody` if JSON serialization fails.

#### Concurrency
- All fields are immutable after construction; thread‑safe. No mutable shared state.
