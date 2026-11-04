### OpenAiCompatibleProviderSupport.java
- H: 1.0
- C: 92F0B02B
- D: 2026-11-04T07:43:16Z
- T: 2026-11-04T07:44:37Z
- G: 
- A: 0.0.0
- X: file
- P: 36159BF2
---
> OpenAI-compatible chat generation builds portable completion requests, protects API keys on HTTP transports, and converts responses and usage into generation timings.

#### Purpose
- Supports the non-network behavior for OpenAI-compatible AI generation: request JSON, endpoint and API-key handling, HTTP status/error helpers, and timing conversion.
- Provides reusable loopback-host and success-status checks for remote model calls.

#### Type
- `final class OpenAiCompatibleProviderSupport` — package-private final class.
- Annotated `@ToString`; prompt support and environment lookup are excluded from generated string output.

#### Input
- Constructor: `LlamaCppJniConfig`, `AiPromptSupport`, environment lookup `Function<String, @Nullable String>`.
- Request construction reads `AiGenerationRequest` prompt key, source file, and source text; configuration supplies model, URL, API-key variable name, and sampling options.
- API-key lookup reads the configured environment variable; transport checks accept an endpoint string.
- Timing conversion consumes completion text, `ChatResponse` usage, and wall-clock seconds.
- Error formatting consumes endpoint, HTTP status, and response body.

#### Output
- Serializes chat-completions request JSON with non-streaming mode.
- Produces `/chat/completions` endpoint, nullable trimmed API key, status/error helpers, and `AiGenerationTimings`.
- Rejects cleartext API-key transmission to non-loopback HTTP hosts.

#### Core logic
- Builds `messages` with system prompt and user source content; includes required sampling fields and conditionally includes model, `top_k`, `min_p`, seed, stop strings, and reasoning effort.
- Trims `baseUrl`, requires `http://` or `https://`, strips trailing slashes, then appends `/chat/completions`.
- Resolves `apiKeyEnv`; returns null if unconfigured, missing, or blank, otherwise returns the trimmed value.
- Allows a configured API key on HTTPS; on HTTP, parses the host and permits only exact `localhost`, IPv4 `127.x.x.x` matching the literal pattern, or `[::1]`.
- Classifies status codes 200–299 as success; formats failures with endpoint, status, and up to 500 body characters plus `...` when truncated.
- Converts response usage counts to integers; decode rate is completion tokens divided by positive wall-clock seconds, otherwise zero; prefill rate is zero.

#### Public API
- No public or protected members; all declared methods and constructor are package-private.

#### Dependencies
- `LlamaCppJniConfig`, `AiGenerationRequest`, `AiPromptSupport`, `Java8CompatibilityHelper`
- `ChatResponse`, `Usage`, `AiGenerationTimings`
- Jackson `ObjectMapper`, `ArrayNode`, `ObjectNode`
- `Function`, `Objects`, `List`, `URL`, `IOException`, `Pattern`
- Lombok `@ToString`; JSpecify `@Nullable`

#### Exceptions / Errors
- Constructor rejects null config, prompt support, or environment function via `Objects.requireNonNull`.
- `requestBody()` propagates `IOException` from JSON serialization.
- `endpoint()` throws `IllegalArgumentException` for non-HTTP(S) base URLs.
- `apiKeyFor()` may throw `IOException` for invalid URL parsing or configured keys sent to non-loopback HTTP hosts.
- `errorMessage()` truncates bodies longer than 500 characters.

#### Concurrency
- No synchronization or mutable per-call state; configuration, prompt support, environment function, and compatibility helper are instance fields.  
  
DISCONTINUE

> OpenAI-compatible chat generation builds completion requests, protects API keys on HTTP transports, and converts response usage into generation timings.

#### Purpose
- Provides non-network support for OpenAI-compatible generation: request JSON, endpoint and API-key handling, status and error helpers, and timing conversion.
- Centralizes loopback-host validation for API-key transport safety.

#### Type
- `final class OpenAiCompatibleProviderSupport` — package-private final class.
- Annotated `@ToString`; `promptSupport` and `environment` are excluded from generated string output.

#### Input
- Constructor receives `LlamaCppJniConfig`, `AiPromptSupport`, and an environment lookup function.
- Request creation reads `AiGenerationRequest` prompt key, source file, and source text, plus model and sampling options from `LlamaCppJniConfig`.
- API-key resolution reads the configured environment variable; transport validation receives an endpoint URL.
- Timing conversion receives completion text, `ChatResponse`, and elapsed seconds; error formatting receives endpoint, status, and body.

#### Output
- JSON chat-completions request body and `/chat/completions` endpoint.
- Nullable trimmed API key, HTTP success classification, formatted error message, loopback-host predicate, and `AiGenerationTimings`.
- Refuses configured API keys over plain HTTP to non-loopback hosts.

#### Core logic
- Builds system and user messages from `AiPromptSupport`; emits `stream: false` and configured sampling values.
- Includes `model` and `reasoning.effort` only when non-blank; includes `top_k` and `min_p` only when positive, `seed` when nonnegative, and `stop` when non-empty.
- Trims `baseUrl`, accepts only `http://` or `https://`, removes trailing slashes, then appends `/chat/completions`.
- Resolves `apiKeyEnv` through the supplied environment function; returns null if unset, missing, or blank, otherwise returns the trimmed key.
- For HTTP endpoints with a key, allows only exact `localhost`, matching `127.x.x.x`, or `[::1]`; HTTPS does not require this check.
- Treats statuses 200–299 as success; error messages include endpoint, status, and at most 500 body characters, with `...` for longer bodies.
- Derives token counts from `ChatResponse.Usage`; decode rate is completion tokens divided by positive elapsed seconds, or zero; prefill rate is zero.

#### Public API
- No public or protected members; constructor and methods are package-private.

#### Dependencies
- `LlamaCppJniConfig`, `AiGenerationRequest`, `AiPromptSupport`, `Java8CompatibilityHelper`
- `ChatResponse`, `Usage`, `AiGenerationTimings`
- Jackson `ObjectMapper`, `ArrayNode`, `ObjectNode`
- Java `Function`, `Objects`, `List`, `URL`, `IOException`, `Pattern`
- Lombok `@ToString`; JSpecify `@Nullable`

#### Exceptions / Errors
- Constructor rejects null config, prompt support, or environment function.
- `requestBody()` may throw `IOException` during JSON serialization.
- `endpoint()` throws `IllegalArgumentException` for a non-HTTP(S) base URL.
- `apiKeyFor()` may throw `IOException` for URL parsing failures or an API key sent to a non-loopback HTTP host.

#### Concurrency
- No synchronization; fields are final, while request and response-processing data are local to each call.
