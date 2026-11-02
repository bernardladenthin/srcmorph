### OpenAiCompatibleProviderSupport.java
- H: 1.0
- C: 92F0B02B
- D: 2026-10-31T20:34:56Z
- T: 2026-10-31T20:42:35Z
- G: 
- A: 0.0.0
- X: file
---
> Builds and validates the JSON request, endpoint, and API key handling for an OpenAI‑compatible chat‑completion provider, and computes call timings.

#### Purpose
- Construct chat‑completion request body.
- Resolve endpoint URL.
- Retrieve and validate API key.
- Compute success status and error messages.
- Calculate generation timings.

#### Type
`final class OpenAiCompatibleProviderSupport` – class, final.  
Fields: `config`, `promptSupport`, `environment`, `compatibilityHelper`, `MAPPER`.  
Annotations: `@ToString`, `@ToString.Exclude`.

#### Input
- `config` (`LlamaCppJniConfig`): model settings, base URL, API key env, sampling knobs.  
- `promptSupport` (`AiPromptSupport`): prompt templates.  
- `environment` (`Function<String, @Nullable String>`): environment variable lookup.  
- `request` (`AiGenerationRequest`): source file and prompt key.  
- `endpoint` (`String`): URL for request.  
- `text`, `response`, `seconds` for timing calculation.

#### Output
- JSON string for request body.  
- Endpoint URL string.  
- API key string or `null`.  
- Boolean success flag.  
- Error message string.  
- `AiGenerationTimings` instance.

#### Core logic
- **requestBody**: build JSON with system/user messages, sampling knobs, optional fields.  
- **endpoint**: trim base URL, enforce `http(s)`, strip trailing slashes, append `/chat/completions`.  
- **apiKey**: read env var, trim, return `null` if unset or blank.  
- **apiKeyFor**: allow key only over HTTPS or plain HTTP to loopback hosts; otherwise throw `IOException`.  
- **isLoopbackHost**: check host against `localhost`, IPv4 `127.x.x.x`, or IPv6 `[::1]`.  
- **isSuccess**: status in 200–299.  
- **errorMessage**: truncate body to 500 chars, append ellipsis.  
- **timings**: compute decode rate from usage tokens and elapsed seconds; prefill rate set to 0.

#### Public API
- `requestBody(AiGenerationRequest) -> String` – build request JSON.  
- `endpoint() -> String` – compute endpoint URL.  
- `apiKey() -> @Nullable String` – fetch API key.  
- `apiKeyFor(String) -> @Nullable String` – validate key usage.  
- `isLoopbackHost(String) -> boolean` – check loopback host.  
- `isSuccess(int) -> boolean` – check HTTP success.  
- `errorMessage(String,int,String) -> String` – format error message.  
- `timings(String,ChatResponse,double) -> AiGenerationTimings` – compute timings.

#### Dependencies
`com.fasterxml.jackson.databind.ObjectMapper`, `ArrayNode`, `ObjectNode`;  
`java.io.IOException`, `java.net.URL`;  
`java.util.List`, `java.util.Objects`, `java.util.function.Function`;  
`java.util.regex.Pattern`;  
`lombok.ToString`;  
`net.ladenthin.llama.value.ChatResponse`, `Usage`;  
`net.ladenthin.srcmorph.document.AiGenerationRequest`;  
`net.ladenthin.srcmorph.prompt.AiPromptSupport`;  
`net.ladenthin.srcmorph.support.Java8CompatibilityHelper`;  
`org.jspecify.annotations.Nullable`.

#### Exceptions / Errors
- `IllegalArgumentException` if `baseUrl` not `http(s)`.  
- `IOException` if API key sent over plain HTTP to non‑loopback host or URL parsing fails.  
- `IOException` from `requestBody` if JSON serialization fails.

#### Concurrency
- Immutable fields; thread‑safe.  
- No mutable shared state.
