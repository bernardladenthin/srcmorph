### OpenAiCompatibleProviderSupport.java
- H: 1.0
- C: 92F0B02B
- D: 2026-10-31T14:30:30Z
- T: 2026-10-31T14:38:10Z
- G: 
- A: 0.0.0
- X: file
---
> Provides the core logic for building OpenAI‑compatible chat‑completion requests, validating endpoints, handling API keys securely, and interpreting responses.

#### Purpose
- Builds JSON request bodies for chat‑completion APIs.
- Validates and constructs endpoint URLs.
- Retrieves and secures API keys.
- Interprets HTTP responses and calculates timings.

#### Type
`final class OpenAiCompatibleProviderSupport`  
`final` class, no inheritance, no interfaces.  
Fields: `config`, `promptSupport`, `environment`, `compatibilityHelper`, `MAPPER`.  
Constructor: package‑private.

#### Input
- `AiGenerationRequest` for request body construction.
- `LlamaCppJniConfig` for model, endpoint, key, and sampling settings.
- `AiPromptSupport` for system/user prompts.
- `Function<String, @Nullable String>` environment lookup.
- `String endpoint` for key validation.
- `String host` for loopback checks.

#### Output
- JSON string request body (`requestBody`).
- Endpoint URL (`endpoint`).
- Trimmed API key or `null` (`apiKey`, `apiKeyFor`).
- Boolean flags (`isLoopbackHost`, `isSuccess`).
- Error message string (`errorMessage`).
- `AiGenerationTimings` object (`timings`).

#### Core logic
- **requestBody**: builds a JSON object with model, messages, token limits, temperature, top‑p, optional top‑k, min‑p, repetition penalty, seed, stop strings, reasoning effort, and stream flag; serialises with Jackson.
- **endpoint**: trims `baseUrl`, ensures it starts with `http://` or `https://`, removes trailing slashes, appends `/chat/completions`.
- **apiKey**: retrieves environment variable, trims, returns `null` if unset or blank.
- **apiKeyFor**: ensures key is only sent over HTTPS or to a loopback host; otherwise throws `IOException`.
- **isLoopbackHost**: checks host against `localhost`, IPv4 127.x.x.x, or `[::1]`.
- **isSuccess**: checks if HTTP status is 200‑299.
- **errorMessage**: truncates body to 500 chars, appends ellipsis, formats message with endpoint and status.
- **timings**: calculates decode rate from completion tokens and elapsed seconds; constructs `AiGenerationTimings`.

#### Public API
- `requestBody(AiGenerationRequest) -> String` – build request JSON.  
- `endpoint() -> String` – construct chat‑completion URL.  
- `apiKey() -> @Nullable String` – get trimmed key.  
- `apiKeyFor(String) -> @Nullable String` – secure key for endpoint.  
- `isLoopbackHost(String) -> boolean` – check loopback host.  
- `isSuccess(int) -> boolean` – check 2xx status.  
- `errorMessage(String,int,String) -> String` – format error.  
- `timings(String,ChatResponse,double) -> AiGenerationTimings` – compute timings.

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
- `IllegalArgumentException` if `baseUrl` lacks `http(s)` scheme.
- `IOException` if API key is sent over plain HTTP to non‑loopback host or URL parsing fails.
- `IOException` from `requestBody` if JSON serialization fails.

#### Concurrency
- All fields are immutable after construction; no synchronization needed.  
- `MAPPER` is thread‑safe.
