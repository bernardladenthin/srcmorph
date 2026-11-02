### OpenAiCompatibleProviderSupport.java
- H: 1.0
- C: 92F0B02B
- D: 2026-11-01T23:56:56Z
- T: 2026-11-02T00:02:07Z
- G: 
- A: 0.0.0
- X: file
- P: C721BDEF
---
> Builds the HTTP request body, endpoint, API key handling, and response parsing for an OpenAI‑compatible chat‑completion provider.

#### Purpose
- Constructs JSON request payload for chat completions.
- Validates and builds endpoint URL.
- Retrieves and secures API key usage.
- Parses success status and error messages.
- Computes generation timings from response.

#### Type
`final class OpenAiCompatibleProviderSupport`  
class, final, no generics, annotated with `@ToString`.

#### Input
- `LlamaCppJniConfig config` – model URL, key env, sampling knobs.  
- `AiPromptSupport promptSupport` – system/user prompt templates.  
- `Function<String, @Nullable String> environment` – env‑var lookup.  
- `AiGenerationRequest request` – source file and text.  
- `String endpoint` – URL for key validation.  
- `String host` – host part of a URL.  
- `int status` – HTTP status code.  
- `String body` – error response body.  
- `String text` – generated completion text.  
- `ChatResponse response` – parsed server reply.  
- `double seconds` – wall‑clock duration.

#### Output
- JSON string body (`requestBody`).  
- Endpoint URL (`endpoint`).  
- Optional trimmed API key (`apiKey`, `apiKeyFor`).  
- Boolean success flag (`isSuccess`).  
- Error message string (`errorMessage`).  
- `AiGenerationTimings` object (`timings`).

#### Core logic
- `requestBody`: builds JSON with model, messages, token limits, temperature, top‑p, optional top‑k, min‑p, repetition penalty, seed, stop strings, reasoning effort, and disables streaming.  
- `endpoint`: trims `baseUrl`, ensures `http(s)://`, removes trailing slashes, appends `/chat/completions`.  
- `apiKey`: reads env var, trims, returns null if unset or blank.  
- `apiKeyFor`: allows key only over HTTPS or plain HTTP to a loopback host; otherwise throws `IOException`.  
- `isLoopbackHost`: checks host against `localhost`, IPv4 127.x.x.x, or `[::1]`.  
- `isSuccess`: true for 200‑299.  
- `errorMessage`: truncates body to 500 chars, appends ellipsis, formats message.  
- `timings`: extracts token counts from `Usage`, computes decode rate, returns `AiGenerationTimings`.

#### Public API
- `String requestBody(AiGenerationRequest) -> JSON body` – build request payload.  
- `String endpoint() -> URL` – construct chat‑completions endpoint.  
- `String apiKey() -> key` – retrieve optional API key.  
- `String apiKeyFor(String) -> key` – secure key for endpoint.  
- `static boolean isLoopbackHost(String) -> host check` – loopback detection.  
- `static boolean isSuccess(int) -> status check` – success flag.  
- `static String errorMessage(String,int,String) -> message` – error formatting.  
- `static AiGenerationTimings timings(String,ChatResponse,double) -> timings` – compute metrics.

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
- `IllegalArgumentException` if `baseUrl` lacks `http(s)://`.  
- `IOException` if API key sent over plain HTTP to non‑loopback host or URL parsing fails.  
- `IOException` from `ObjectMapper` during body serialization.

#### Concurrency
- Immutable fields (`config`, `promptSupport`, `environment`, `compatibilityHelper`).  
- Thread‑safe; no mutable shared state.
