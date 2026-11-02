### OpenAiCompatibleProviderSupport.java
- H: 1.0
- C: 92F0B02B
- D: 2026-11-01T14:58:32Z
- T: 2026-11-01T15:06:13Z
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
- Calculates generation timings from responses.

#### Type
`final class OpenAiCompatibleProviderSupport` – class, final.  
Fields: `config`, `promptSupport`, `environment`, `compatibilityHelper`, `MAPPER`.  
Annotations: `@ToString`, `@ToString.Exclude`.

#### Input
- `AiGenerationRequest request` → request body JSON.  
- `String endpoint` → API key validation.  
- `String host` → loopback check.  
- `int status` → success check.  
- `String body` → error message extraction.  
- `String text, ChatResponse response, double seconds` → timing calculation.

#### Output
- JSON string for request body.  
- Endpoint URL string.  
- Optional trimmed API key.  
- Boolean success flag.  
- Error message string.  
- `AiGenerationTimings` object.

#### Core logic
- `requestBody`: constructs JSON with system/user messages, sampling knobs, optional fields, and serialises with `ObjectMapper`.  
- `endpoint`: trims `baseUrl`, ensures `http(s)`, removes trailing slashes, appends `/chat/completions`.  
- `apiKey`: fetches env var, trims, returns null if unset or blank.  
- `apiKeyFor`: forbids sending key over plain `http` unless host is loopback; parses URL host.  
- `isLoopbackHost`: matches `localhost`, IPv4 127.x.x.x, or `[::1]`.  
- `isSuccess`: checks 200–299 range.  
- `errorMessage`: truncates body to 500 chars, appends ellipsis, formats message.  
- `timings`: derives token counts from `Usage`, computes decode rate, constructs `AiGenerationTimings`.

#### Public API
- `requestBody(AiGenerationRequest) -> String` – build JSON body.  
- `endpoint() -> String` – compute chat‑completion URL.  
- `apiKey() -> @Nullable String` – retrieve API key.  
- `apiKeyFor(String) -> @Nullable String` – secure key for endpoint.  
- `isLoopbackHost(String) -> boolean` – check loopback host.  
- `isSuccess(int) -> boolean` – success status.  
- `errorMessage(String,int,String) -> String` – format error.  
- `timings(String,ChatResponse,double) -> AiGenerationTimings` – compute timings.

#### Dependencies
`com.fasterxml.jackson.databind.ObjectMapper`, `ArrayNode`, `ObjectNode`; `java.io.IOException`; `java.net.URL`; `java.util.List`; `java.util.Objects`; `java.util.function.Function`; `java.util.regex.Pattern`; `lombok.ToString`; `net.ladenthin.llama.value.ChatResponse`; `net.ladenthin.llama.value.Usage`; `net.ladenthin.srcmorph.document.AiGenerationRequest`; `net.ladenthin.srcmorph.prompt.AiPromptSupport`; `net.ladenthin.srcmorph.support.Java8CompatibilityHelper`; `org.jspecify.annotations.Nullable`.

#### Exceptions / Errors
- `IllegalArgumentException` if `baseUrl` lacks `http(s)` scheme.  
- `IOException` if API key sent over insecure `http` to non‑loopback host or URL parsing fails.  
- `IOException` during JSON serialization.

#### Concurrency
- Immutable configuration and helper objects; thread‑safe.  
- No shared mutable state beyond local variables.
