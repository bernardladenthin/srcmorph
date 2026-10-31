### OpenAiCompatibleProviderSupport.java
- H: 1.0
- C: 92F0B02B
- D: 2026-10-31T10:06:17Z
- T: 2026-10-31T13:14:20Z
- G: 
- A: 0.0.0
- X: file
---
> Generates chat‑completion request bodies and validates OpenAI‑compatible endpoints for AI generation models.

#### Purpose
- Builds JSON request bodies for chat‑completion APIs.
- Validates and constructs endpoint URLs.
- Retrieves and secures API keys.
- Provides helper utilities for status checks and error messaging.

#### Type
final class with Lombok `@ToString`; no inheritance or interfaces.

#### Input
- `LlamaCppJniConfig config` – model settings (remoteModel, baseUrl, apiKeyEnv, sampling knobs).
- `AiPromptSupport promptSupport` – prompt templates.
- `Function<String, @Nullable String> environment` – environment variable lookup.
- `AiGenerationRequest request` – source file and prompt key for request body.
- `String endpoint` – URL for API key validation.

#### Output
- JSON string body (`requestBody`).
- Endpoint URL (`endpoint`).
- API key string or `null` (`apiKey`, `apiKeyFor`).
- Boolean flags (`isLoopbackHost`, `isSuccess`).
- Error message string (`errorMessage`).
- `AiGenerationTimings` object (`timings`).

#### Core logic
- `requestBody`: builds an `ObjectNode` with system/user messages, sampling knobs, optional fields (`top_k`, `min_p`, `seed`, `stop`, `reasoning.effort`), and serialises to JSON.
- `endpoint`: trims `baseUrl`, ensures it starts with `http://` or `https://`, removes trailing slashes, appends `/chat/completions`.
- `apiKey`: fetches env var named by `config.apiKeyEnv()`, trims, returns `null` if unset or blank.
- `apiKeyFor`: if key exists and endpoint is plain `http`, parses host; throws `IOException` unless host is loopback; otherwise returns key.
- `isLoopbackHost`: checks host against `localhost`, IPv4 127.x.x.x, or `[::1]`.
- `isSuccess`: true for 200–299 status codes.
- `errorMessage`: truncates body to 500 chars, appends ellipsis, formats message with endpoint and status.
- `timings`: extracts token counts from `ChatResponse.usage`, computes decode rate, constructs `AiGenerationTimings`.

#### Public API
- `String requestBody(AiGenerationRequest) -> JSON body` – builds request payload.
- `String endpoint() -> URL` – constructs chat‑completion endpoint.
- `String apiKey() -> key` – retrieves API key.
- `String apiKeyFor(String) -> key` – validates key transmission.
- `boolean isLoopbackHost(String) -> flag` – checks loopback host.
- `boolean isSuccess(int) -> flag` – checks HTTP success.
- `String errorMessage(String,int,String) -> message` – formats error.
- `AiGenerationTimings timings(String,ChatResponse,double) -> timings` – computes timings.

#### Dependencies
- Jackson `ObjectMapper`, `ArrayNode`, `ObjectNode`.
- `AiGenerationRequest`, `AiPromptSupport`, `LlamaCppJniConfig`.
- `ChatResponse`, `Usage`.
- `Java8CompatibilityHelper`.
- `java.net.URL`, `java.util.List`, `java.util.Objects`, `java.util.function.Function`, `java.util.regex.Pattern`.
- Lombok `@ToString`, `@ToString.Exclude`.
- `org.jspecify.annotations.Nullable`.

#### Exceptions / Errors
- `IOException` from `requestBody` (JSON serialization) and `apiKeyFor` (URL parsing or key safety).
- `IllegalArgumentException` from `endpoint` if `baseUrl` lacks HTTP/HTTPS scheme.
- `IOException` from `apiKeyFor` if plain HTTP to non‑loopback host.

#### Concurrency
- Immutable fields; thread‑safe. No mutable shared state.
