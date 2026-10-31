### OpenAiCompatibleProviderSupport.java
- H: 1.0
- C: 92F0B02B
- D: 2026-10-31T19:47:56Z
- T: 2026-10-31T19:49:26Z
- G: 
- A: 0.0.0
- X: file
---
> Builds OpenAI-compatible chat-completion requests, protects API keys from cleartext transmission to non-local hosts, and derives response errors and generation timings.

#### Purpose
- Provides network-independent support logic for OpenAI-compatible generation using model configuration and prompt definitions.
- Constructs request payloads and endpoint URLs; resolves API keys safely; interprets HTTP status, errors, and usage metrics.

#### Type
- `final class OpenAiCompatibleProviderSupport` — package-private; annotated `@ToString`.
- No superclass or implemented interfaces.

#### Input
- Constructor: `LlamaCppJniConfig`, `AiPromptSupport`, and environment lookup `Function<String, @Nullable String>`.
- `requestBody`: `AiGenerationRequest` fields and configured model, sampling, stop, and reasoning settings.
- `endpoint`: configured base URL.
- `apiKey` / `apiKeyFor`: configured environment-variable name, environment lookup, and endpoint.
- Static helpers: host, HTTP status, endpoint/status/body, or completion text/response/wall-clock seconds.

#### Output
- JSON request body string; chat-completions endpoint URL; nullable trimmed API key.
- Static helpers return loopback/success booleans, formatted error message, or `AiGenerationTimings`.
- No network calls or writes; constructor stores validated dependencies.

#### Core logic
- `requestBody` creates two chat messages (system prompt and user source), adds configured token and sampling values, and sets `stream` to `false`.
- Includes `model` and `reasoning.effort` only when non-blank; includes `top_k` and `min_p` only when positive, `seed` when nonnegative, and `stop` when non-empty.
- `endpoint` trims the base URL, requires `http://` or `https://`, removes trailing slashes, then appends `/chat/completions`.
- `apiKey` looks up the configured environment variable and returns its trimmed non-blank value.
- `apiKeyFor` returns no key unchanged; HTTPS keys are allowed, while HTTP keys are allowed only for exact `localhost`, IPv4 `127.x.x.x` literals, or `[::1]`.
- `isSuccess` accepts HTTP 200–299; `errorMessage` includes endpoint, status, and up to 500 body characters, appending `...` when truncated.
- `timings` derives prompt, cached, and completion token counts from response usage; computes decode rate as completion tokens / positive elapsed seconds and sets prefill rate to zero.

#### Public API
- No public or protected members; all declared members are package-private or private.

#### Dependencies
- Jackson: `ObjectMapper`, `ArrayNode`, `ObjectNode`.
- Java: `IOException`, `URL`, `List`, `Objects`, `Function`, `Pattern`.
- Lombok: `@ToString`.
- JSpecify: `@Nullable`.
- `ChatResponse`, `Usage`, `AiGenerationRequest`, `AiPromptSupport`, `Java8CompatibilityHelper`, `LlamaCppJniConfig`, `AiGenerationTimings`, `OpenAiCompatibleAiGenerationProvider`.

#### Exceptions / Errors
- Constructor rejects null configuration, prompt support, or environment lookup via `Objects.requireNonNull`.
- `requestBody` propagates JSON serialization `IOException`.
- `endpoint` throws `IllegalArgumentException` for non-HTTP(S) base URLs.
- `apiKeyFor` propagates URL parsing `IOException`; refuses a configured key over plain HTTP to a non-loopback host with `IOException`.
- `timings` assumes response usage is available; no explicit null handling.

#### Concurrency
- No synchronization or mutable shared request state; configuration and dependencies are final.
