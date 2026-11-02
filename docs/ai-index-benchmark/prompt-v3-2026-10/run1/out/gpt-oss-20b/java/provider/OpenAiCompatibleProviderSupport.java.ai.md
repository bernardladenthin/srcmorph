### OpenAiCompatibleProviderSupport.java
- H: 1.0
- C: 92F0B02B
- D: 2026-10-31T19:47:56Z
- T: 2026-10-31T19:54:06Z
- G: 
- A: 0.0.0
- X: file
---
> Provides JSON request construction, endpoint resolution, API key handling, and response timing for OpenAI‑compatible chat‑completion providers.

#### Purpose
- Builds request bodies for chat‑completion APIs.
- Validates and constructs endpoint URLs.
- Retrieves and secures API keys.
- Determines success status and formats error messages.
- Calculates generation timings.

#### Type
`final class OpenAiCompatibleProviderSupport`  
- Modifiers: final  
- Extends: none  
- Implements: none  
- Annotations: `@ToString`

#### Input
- `LlamaCppJniConfig config` – model settings (remoteModel, baseUrl, apiKeyEnv, sampling knobs).  
- `AiPromptSupport promptSupport` – prompt templates.  
- `Function<String, @Nullable String> environment` – environment variable lookup.  
- `AiGenerationRequest request` – source file and prompt key.  
- `String endpoint` – URL for request.  
- `String body` – error body.

#### Output
- JSON string for request body.  
- Endpoint URL string.  
- API key string or `null`.  
- Boolean success flag.  
- Error message string.  
- `AiGenerationTimings` instance.

#### Core logic
- **requestBody**: Assemble JSON with model, messages, token limits, temperature, top‑p, top‑k, min‑p, repetition penalty, seed, stop strings, reasoning effort, and stream flag.  
- **endpoint**: Trim `baseUrl`, ensure `http://` or `https://`, strip trailing slashes, append `/chat/completions`.  
- **apiKey**: Retrieve env var, trim, return `null` if unset or blank.  
- **apiKeyFor**: If key exists and endpoint uses plain `http`, ensure host is loopback; otherwise throw `IOException`.  
- **isLoopbackHost**: Check host against `localhost`, IPv4 loopback regex, or `[::1]`.  
- **isSuccess**: Return true for 200–299 status codes.  
- **errorMessage**: Truncate body to 500 chars, append ellipsis, format with endpoint and status.  
- **timings**: Compute decode rate from completion tokens and elapsed seconds; construct `AiGenerationTimings`.

#### Public API
- `String requestBody(AiGenerationRequest request) -> JSON body` – build request payload.  
- `String endpoint() -> URL` – construct chat‑completion endpoint.  
- `String apiKey() -> key or null` – fetch API key.  
- `String apiKeyFor(String endpoint) -> key or null` – secure key for endpoint.  
- `static boolean isLoopbackHost(String host) -> boolean` – loopback check.  
- `static boolean isSuccess(int status) -> boolean` – success status.  
- `static String errorMessage(String endpoint, int status, String body) -> message` – error formatting.  
- `static AiGenerationTimings timings(String text, ChatResponse response, double seconds) -> timings` – compute timings.

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
- `IllegalArgumentException` – malformed `baseUrl`.  
- `IOException` – key sent over plain `http` to non‑loopback host or URL parsing failure.  
- `IOException` – serialization errors in `requestBody`.

#### Concurrency
- All fields are immutable after construction.  
- No mutable shared state; thread‑safe.
