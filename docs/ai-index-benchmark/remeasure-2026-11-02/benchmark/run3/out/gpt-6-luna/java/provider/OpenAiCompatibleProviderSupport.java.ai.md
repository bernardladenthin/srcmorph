### OpenAiCompatibleProviderSupport.java
- H: 1.0
- C: 92F0B02B
- D: 2026-11-01T23:56:56Z
- T: 2026-11-01T23:58:15Z
- G: 
- A: 0.0.0
- X: file
- P: C721BDEF
---
> Builds OpenAI-compatible chat-completion requests for source-file generation while protecting API keys and translating responses into generation timings.

#### Purpose
- Prepares request data and validates endpoint, API-key handling, HTTP status, error messages, and timings for OpenAI-compatible generation.
- Keeps non-network request and response support separate from provider connection handling.

#### Type
- `OpenAiCompatibleProviderSupport` — final class; package-private.
- Annotated `@ToString`; config, prompt support, and environment lookup are excluded from generated string output where marked.

#### Input
- Constructor receives `LlamaCppJniConfig`, `AiPromptSupport`, and an environment-variable lookup function.
- `requestBody` consumes an `AiGenerationRequest` and configured model, prompt, and sampling values.
- `apiKeyFor` consumes an endpoint; `errorMessage` consumes endpoint, status, and body; `timings` consumes completion text, `ChatResponse`, and elapsed seconds.
- Reads environment values through the injected lookup function.

#### Output
- Produces serialized JSON request bodies, chat-completions endpoint URLs, and optional trimmed API keys.
- Produces HTTP success checks, error-message strings, loopback-host checks, and `AiGenerationTimings`.
- Does not write files or perform network requests.

#### Core logic
- `requestBody` emits system and user messages, standard sampling settings, and `stream: false`; conditionally includes nonblank model and reasoning effort, positive `top_k`/`min_p`, nonnegative seed, and nonempty stop strings.
- `endpoint` trims `baseUrl`, requires an `http://` or `https://` prefix, removes trailing slashes, then appends `/chat/completions`.
- `apiKey` looks up the configured environment-variable name and returns a trimmed key, or `null` for missing, unset, or blank values.
- `apiKeyFor` permits a configured key over HTTPS or plain HTTP only when the parsed host is `localhost`, an IPv4 `127.x.x.x` literal, or `[::1]`.
- `isSuccess` recognizes HTTP statuses 200–299. `errorMessage` includes endpoint, status, and up to 500 body characters, adding `...` when truncated.
- `timings` uses response usage counts; decode rate is completion tokens per elapsed second when positive, otherwise zero; prefill rate is always zero.

#### Public API
- No public or protected members; the type and its members are package-private.

#### Dependencies
- `LlamaCppJniConfig`, `AiGenerationRequest`, `AiPromptSupport`, `Java8CompatibilityHelper`
- `ChatResponse`, `Usage`, `AiGenerationTimings`
- Jackson `ObjectMapper`, `ArrayNode`, `ObjectNode`
- `IOException`, `URL`, `List`, `Objects`, `Function`, `Pattern`
- Lombok `@ToString`; JSpecify `@Nullable`

#### Exceptions / Errors
- Constructor rejects null config, prompt support, or environment lookup with `NullPointerException`.
- `requestBody` declares `IOException` for JSON serialization failure.
- `endpoint` throws `IllegalArgumentException` when `baseUrl` is not HTTP(S).
- `apiKeyFor` declares `IOException` for endpoint parsing failure or when a configured key would be sent over plain HTTP to a non-loopback host.
- `timings` assumes response usage is available; no null handling is present.

#### Concurrency
- Fields are final; no synchronization or mutable shared request state is present.
