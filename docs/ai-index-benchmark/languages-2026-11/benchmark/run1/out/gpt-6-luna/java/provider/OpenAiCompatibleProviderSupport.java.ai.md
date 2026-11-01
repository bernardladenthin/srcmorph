### OpenAiCompatibleProviderSupport.java
- H: 1.0
- C: 92F0B02B
- D: 2026-11-01T01:13:43Z
- T: 2026-11-01T01:15:05Z
- G: 
- A: 0.0.0
- X: file
---
> Builds OpenAI-compatible chat-completions requests and safely handles endpoint, API-key, HTTP-status, error-message, and timing data for remote model calls.

#### Purpose
- Supports `OpenAiCompatibleAiGenerationProvider` with network-independent request construction and response-related helpers.
- Restricts cleartext API-key transmission to loopback HTTP endpoints.

#### Type
- `final class OpenAiCompatibleProviderSupport` — package-private class; annotated `@ToString`.

#### Input
- Constructor: `LlamaCppJniConfig`, `AiPromptSupport`, and environment-variable lookup function.
- Request generation: `AiGenerationRequest`, model configuration, and prompt definitions.
- API-key lookup: configured environment-variable name and endpoint URL.
- Response helpers: endpoint, HTTP status, error body, completion text, `ChatResponse`, and elapsed seconds.

#### Output
- Produces serialized JSON request bodies, chat-completions endpoint URLs, optional API keys, error messages, and `AiGenerationTimings`.
- Does not perform network I/O or mutate configuration.

#### Core logic
- Builds a non-streaming chat-completions JSON body with system and user messages, token limits, and sampling settings.
- Includes `model` and `reasoning.effort` only when non-blank; includes `top_k` and `min_p` only when positive, `seed` only when non-negative, and `stop` only when non-empty.
- Trims `baseUrl`, accepts only `http://` or `https://`, removes trailing slashes, then appends `/chat/completions`.
- Trims the configured API key; returns `null` if no variable is configured, unset, or blank.
- Permits a configured key over HTTPS or plain HTTP to `localhost`, an IPv4 loopback-form host, or `[::1]`; rejects other HTTP hosts.
- Treats HTTP statuses 200–299 as success; formats failures with endpoint, status, and at most 500 body characters plus `...` when truncated.
- Calculates completion decode rate as completion tokens divided by positive elapsed seconds; sets prefill rate to zero.

#### Dependencies
- `LlamaCppJniConfig`, `AiPromptSupport`, `AiGenerationRequest`, `AiGenerationTimings`
- `ChatResponse`, `Usage`
- Jackson `ObjectMapper`, `ArrayNode`, `ObjectNode`
- `Java8CompatibilityHelper`
- Java `IOException`, `URL`, `List`, `Objects`, `Function`, `Pattern`
- Lombok `@ToString`; JSpecify `@Nullable`

#### Exceptions / Errors
- Constructor rejects null configuration, prompt support, or environment lookup via `Objects.requireNonNull`.
- `requestBody` propagates `IOException` if JSON serialization fails.
- `endpoint` throws `IllegalArgumentException` for non-HTTP(S) base URLs.
- `apiKeyFor` throws `IOException` when a key would be sent over HTTP to a non-loopback host or the URL cannot be parsed.
- `errorMessage` truncates bodies longer than 500 characters.

#### Concurrency
- No synchronization or shared mutable per-call state; configuration and helper dependencies are held in final fields.
