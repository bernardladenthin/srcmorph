### OpenAiCompatibleProviderSupport.java
- H: 1.0
- C: 92F0B02B
- D: 2026-11-04T07:09:59Z
- T: 2026-11-04T07:16:34Z
- G: 
- A: 0.0.0
- X: file
- P: 36159BF2
---
> Builds the JSON request, endpoint, and key handling for an OpenAI‑compatible chat‑completion provider, and interprets responses and errors.

#### Purpose
- Constructs request payload for chat‑completion calls.
- Validates and builds endpoint URLs.
- Retrieves and secures API keys.
- Parses success status and error messages.
- Calculates generation timings.

#### Type
`final class OpenAiCompatibleProviderSupport`  
class, final, no inheritance, no interfaces.

#### Input
- `LlamaCppJniConfig config` – model URL, key env, sampling knobs.  
- `AiPromptSupport promptSupport` – system/user prompt templates.  
- `Function<String, @Nullable String> environment` – env var lookup.  
- `AiGenerationRequest request` – source file and text.  
- `String endpoint` – URL for key validation.  
- `int status, String body` – HTTP response details.  
- `String text, ChatResponse response, double seconds` – parsed completion.

#### Output
- JSON string body (`requestBody`).  
- Endpoint URL (`endpoint`).  
- Optional trimmed API key (`apiKey`, `apiKeyFor`).  
- Boolean success flag (`isSuccess`).  
- Error message string (`errorMessage`).  
- `AiGenerationTimings` object (`timings`).

#### Core logic
- Build JSON with optional fields based on config values.  
- Trim and validate base URL scheme; append `/chat/completions`.  
- Resolve API key from env; return `null` if unset or blank.  
- For plain `http` endpoints, allow key only if host is loopback; otherwise throw `IOException`.  
- Determine loopback hosts via exact match, IPv4 regex, or IPv6 literal.  
- Check 2xx status range.  
- Construct error message with truncated body excerpt.  
- Compute decode rate from usage tokens and elapsed seconds; prefill rate set to 0.

#### Public API
- `requestBody(AiGenerationRequest) -> String` – build request JSON.  
- `endpoint() -> String` – construct chat‑completion URL.  
- `apiKey() -> @Nullable String` – fetch trimmed key.  
- `apiKeyFor(String) -> @Nullable String` – secure key for endpoint.  
- `isLoopbackHost(String) -> boolean` – check loopback host.  
- `isSuccess(int) -> boolean` – 2xx status check.  
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
- `IOException` if API key sent over non‑loopback plain `http`.  
- `IOException` if endpoint URL parsing fails.  
- `IOException` from JSON serialization in `requestBody`.

#### Concurrency
- All fields are immutable after construction; class is thread‑safe.  
- No mutable shared state; no synchronization required.
