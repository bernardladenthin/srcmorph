### OpenAiCompatibleProviderSupport.java
- H: 1.0
- C: 92F0B02B
- D: 2026-11-01T01:56:59Z
- T: 2026-11-01T01:58:46Z
- G: 
- A: 0.0.0
- X: file
---
> Builds OpenAI-compatible chat-completion requests and safeguards API-key delivery while formatting response errors and generation timings.

#### Purpose
- Supports `OpenAiCompatibleAiGenerationProvider` operations that do not require network access.
- Prepares request data and validates endpoint, credentials, HTTP status, errors, and timing calculations.

#### Type
- `OpenAiCompatibleProviderSupport` — final class; package-private.
- Annotated `@ToString`; `promptSupport` and `environment` are excluded from generated `toString()`.

#### Input
- Constructor receives `LlamaCppJniConfig`, `AiPromptSupport`, and an environment-variable lookup function.
- `requestBody` consumes an `AiGenerationRequest` and configured model and sampling values.
- `apiKeyFor` consumes an endpoint; `errorMessage` consumes endpoint, status, and response body.
- `timings` consumes completion text, `ChatResponse`, and elapsed seconds.

#### Output
- Produces serialized JSON for a chat-completions request and the normalized chat-completions endpoint.
- Returns an optional trimmed API key, HTTP status classification, error message, and `AiGenerationTimings`.
- Does not perform network I/O.

#### Core logic
- Builds a non-streaming JSON request with system and user messages from `AiPromptSupport`.
- Adds configured sampling options; omits blank model or reasoning effort, nonpositive `top_k`/`min_p`, negative seed, and empty stop list.
- Trims `baseUrl`, accepts only `http://` or `https://`, removes trailing slashes, then appends `/chat/completions`.
- Looks up and trims the configured API key; refuses to send it over plain HTTP unless the URL host is `localhost`, an IPv4 `127.x.x.x` literal, or `[::1]`.
- Treats statuses 200–299 as successful; truncates longer error bodies to 500 characters and appends `...`.
- Calculates decode rate as completion tokens divided by positive elapsed seconds; sets prefill rate to zero.

#### Dependencies
- Jackson: `ObjectMapper`, `ArrayNode`, `ObjectNode`.
- Java: `IOException`, `URL`, `List`, `Objects`, `Function`, `Pattern`.
- Lombok: `@ToString`.
- Llama values: `ChatResponse`, `Usage`.
- Project types: `LlamaCppJniConfig`, `AiGenerationRequest`, `AiPromptSupport`, `Java8CompatibilityHelper`, `AiGenerationTimings`.
- JSpecify: `@Nullable`.

#### Exceptions / Errors
- Constructor rejects null config, prompt support, or environment lookup via `Objects.requireNonNull`.
- `requestBody` propagates `IOException` if JSON serialization fails.
- `endpoint` throws `IllegalArgumentException` for non-HTTP(S) base URLs.
- `apiKeyFor` propagates URL parsing `IOException` and throws `IOException` when a configured key would go to a non-loopback plain-HTTP host.
- `timings` assumes `response.getUsage()` is non-null; no explicit null handling.
