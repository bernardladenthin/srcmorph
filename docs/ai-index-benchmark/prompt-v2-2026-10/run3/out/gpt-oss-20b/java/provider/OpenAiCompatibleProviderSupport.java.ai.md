### OpenAiCompatibleProviderSupport.java
- H: 1.0
- C: 92F0B02B
- D: 2026-10-31T14:57:29Z
- T: 2026-10-31T15:07:27Z
- G: 
- A: 0.0.0
- X: file
---
> Provides JSON request construction, endpoint resolution, API key handling, and response timing for OpenAI‑compatible chat‑completion providers.

#### Purpose
- Builds request bodies for chat‑completion APIs.
- Validates and constructs endpoint URLs.
- Retrieves and secures API keys.
- Determines success status and error messages.
- Calculates generation timings.

#### Type
`final class OpenAiCompatibleProviderSupport`  
modifiers: final  
extends: none  
implements: none  
annotations: `@ToString`

#### Input
- `LlamaCppJniConfig config` – model settings (remoteModel, baseUrl, apiKeyEnv, sampling knobs).  
- `AiPromptSupport promptSupport` – prompt templates.  
- `Function<String, @Nullable String> environment` – environment variable lookup.  
- `AiGenerationRequest request` – source file and prompt key.  
- `String endpoint` – URL for API call.  
- `String body` – error response body.

#### Output
- JSON string body for chat‑completion request.  
- Endpoint URL string.  
- API key string (trimmed or `null`).  
- Boolean success flag.  
- Error message string.  
- `AiGenerationTimings` instance.

#### Core logic
- **requestBody**: builds JSON with system/user messages, model, tokens, temperature, top‑p, top‑k, min‑p, repetition penalty, seed, stop strings, reasoning effort, and stream flag.  
- **endpoint**: trims `baseUrl`, ensures `http(s)://`, removes trailing slashes, appends `/chat/completions`.  
- **apiKey**: fetches env var, trims, returns `null` if unset or blank.  
- **apiKeyFor**: validates that a plain `http` key is only sent to loopback hosts; otherwise throws `IOException`.  
- **isLoopbackHost**: checks host against `localhost`, IPv4 127.x.x.x, or `[::1]`.  
- **isSuccess**: checks status in 200–299 range.  
- **errorMessage**: truncates body to 500 chars, appends ellipsis, formats message.  
- **timings**: computes completion tokens, decode rate, constructs `AiGenerationTimings`.

#### Public API
- `String requestBody(AiGenerationRequest) -> JSON body` – builds request payload.  
- `String endpoint() -> URL` – resolves endpoint.  
- `String apiKey() -> key` – retrieves API key.  
- `String apiKeyFor(String) -> key` – secures key for endpoint.  
- `static boolean isLoopbackHost(String) -> flag` – host loopback check.  
- `static boolean isSuccess(int) -> flag` – success status check.  
- `static String errorMessage(String,int,String) -> message` – error formatting.  
- `static AiGenerationTimings timings(String,ChatResponse,double) -> timings` – timing calculation.

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
- `IllegalArgumentException` – invalid `baseUrl` scheme.  
- `IOException` – key sent over insecure HTTP to non‑loopback host or URL parsing failure.  
- `IOException` – JSON serialization failure in `requestBody`.

#### Concurrency
- All fields are immutable after construction.  
- No mutable shared state; thread‑safe.
