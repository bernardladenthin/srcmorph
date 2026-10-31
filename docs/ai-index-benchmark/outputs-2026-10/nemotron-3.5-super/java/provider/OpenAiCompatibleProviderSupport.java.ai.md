### OpenAiCompatibleProviderSupport.java
- H: 1.0
- C: 92F0B02B
- D: 2026-10-31T09:32:09Z
- T: 2026-10-31T09:46:49Z
- G: 
- A: 0.0.0
- X: file
---
> Builds OpenAI-compatible chat-completions request bodies, endpoints, API key rules, error messages, and timings for local or remote model providers without performing network I/O.

#### Purpose
- Supports `OpenAiCompatibleAiGenerationProvider` by preparing request payloads, validating endpoints, gating cleartext API keys, and formatting errors/timings.
- Contains no socket or network code; split for full PIT mutation coverage.

#### Type
- `final class`, package-private, annotated `@ToString` (Lombok) with `@ToString.Exclude` on `config`, `promptSupport`, and `environment`.
- No `extends` or `implements`.
- Instance fields: `LlamaCppJniConfig config`, `AiPromptSupport promptSupport`, `Function<String, @Nullable String> environment`, `Java8CompatibilityHelper compatibilityHelper`.

#### Input
- Constructor: `LlamaCppJniConfig config`, `AiPromptSupport promptSupport`, `Function<String, @Nullable String> environment` (all null-checked via `Objects.requireNonNull`).
- `requestBody`: `AiGenerationRequest request`.
- `endpoint`: reads `config.baseUrl()`.
- `apiKey`: reads `config.apiKeyEnv()` and applies `environment`.
- `apiKeyFor`: `String endpoint`.
- `isLoopbackHost`: `String host`.
- `isSuccess`: `int status`.
- `errorMessage`: `String endpoint`, `int status`, `String body`.
- `timings`: `String text`, `ChatResponse response`, `double seconds`.

#### Output
- `requestBody`: `String` JSON body; throws `IOException` on serialization failure.
- `endpoint`: `String` chat-completions URL; throws `IllegalArgumentException` for non-http(s) `baseUrl`.
- `apiKey`: `@Nullable String` trimmed key or `null`.
- `apiKeyFor`: `@Nullable String` key or `null`; throws `IOException` if cleartext key to non-loopback plain http.
- `isLoopbackHost`: `boolean`.
- `isSuccess`: `boolean`.
- `errorMessage`: `String` message.
- `timings`: `AiGenerationTimings` with prompt/cached/completion tokens and decode rate.

#### Core logic
- `requestBody` serializes `model`, system/user messages, `max_tokens`, `temperature`, `top_p`, optional `top_k`/`min_p`/`seed`/`stop`/`reasoning.effort`, `repetition_penalty`, and `stream=false`.
- `endpoint` trims `baseUrl`, enforces `http://` or `https://`, strips trailing slashes, appends `/chat/completions`.
- `apiKey` returns `null` when `apiKeyEnv` is blank or env lookup yields null/blank; otherwise returns trimmed key.
- `apiKeyFor` returns the key for https or null; for plain http it verifies the host is loopback (`localhost`, `127.x.x.x`, `[::1]`) else throws `IOException`.
- `isLoopbackHost` matches `localhost` exactly, IPv4 loopback pattern, or `[::1]`.
- `isSuccess` returns true for HTTP 2
