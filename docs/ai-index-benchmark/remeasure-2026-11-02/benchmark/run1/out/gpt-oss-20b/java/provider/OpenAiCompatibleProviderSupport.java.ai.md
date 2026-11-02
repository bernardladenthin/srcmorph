### OpenAiCompatibleProviderSupport.java
- H: 1.0
- C: 92F0B02B
- D: 2026-11-01T23:26:17Z
- T: 2026-11-01T23:31:45Z
- G: 
- A: 0.0.0
- X: file
- P: C721BDEF
---
> Builds JSON request bodies, validates endpoints, secures API keys, and interprets responses for OpenAI‑compatible chat‑completion services.

#### Purpose
- Constructs request payloads for chat‑completion APIs.
- Validates URLs and API key safety.
- Parses and reports HTTP errors.
- Calculates generation timings.

#### Type
`final class OpenAiCompatibleProviderSupport` – class, final.  
Fields: `config`, `promptSupport`, `environment`, `compatibilityHelper`.  
Constructor: `OpenAiCompatibleProviderSupport(LlamaCppJniConfig, AiPromptSupport, Function<String, @Nullable String>)`.

#### Input
- `AiGenerationRequest request` → JSON body.
- `String endpoint` → URL validation and key check.
- `String baseUrl` from `config` → endpoint construction.
- `String apiKeyEnv` from `config` → environment lookup via `environment`.

#### Output
- JSON string body (`requestBody`).
- Endpoint URL (`endpoint`).
- API key string or `null` (`apiKey`, `apiKeyFor`).
- Error message string (`errorMessage`).
- `AiGenerationTimings` object (`timings`).

#### Core logic
- `requestBody`: builds `ObjectNode` with system/user messages, sampling knobs, optional fields (`top_k`, `min_p`, `seed`, `stop`, `reasoning.effort`), serialises to JSON.
- `endpoint`: trims `baseUrl`, ensures `http://` or `https://`, removes trailing slashes, appends `/chat/completions`.
- `apiKey`: retrieves env var, trims, returns `null` if unset or blank.
- `apiKeyFor`: if endpoint is plain `http` and host is not loopback, throws `IOException`; otherwise returns key.
- `isLoopbackHost`: checks host against `localhost`, IPv4 loopback regex, or `[::1]`.
- `isSuccess`: checks status 200–299.
- `errorMessage`: truncates body to 500 chars, appends ellipsis, formats message.
- `timings`: extracts token counts from `ChatResponse`, computes decode rate, returns `AiGenerationTimings`.

#### Public API
- `requestBody(AiGenerationRequest) -> String` – build JSON body.  
- `endpoint() -> String` – compute chat‑completions URL.  
- `apiKey() -> @Nullable String` – fetch API key.  
- `apiKeyFor(String) -> @Nullable String` – secure key for endpoint.  
- `isLoopbackHost(String) -> boolean` – check loopback host.  
- `isSuccess(int) -> boolean` – success status check.  
- `errorMessage(String,int,String) -> String` – format error.  
- `timings(String,ChatResponse,double) -> AiGenerationTimings` – compute timings.

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
- `IllegalArgumentException` if `baseUrl` lacks `http(s)` scheme.  
- `IOException` if plain `http` endpoint is non‑loopback while key is set.  
- `IOException` from JSON serialization (`requestBody`).  

#### Concurrency
- Immutable fields after construction; thread‑safe.  
- No mutable shared state; all methods stateless except field reads.
