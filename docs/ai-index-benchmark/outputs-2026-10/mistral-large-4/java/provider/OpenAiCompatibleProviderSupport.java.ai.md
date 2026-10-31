### OpenAiCompatibleProviderSupport.java
- H: 1.0
- C: 92F0B02B
- D: 2026-10-30T16:34:09Z
- T: 2026-10-30T16:41:52Z
- G: 
- A: 0.0.0
- X: file
---
> Builds OpenAI chat-completion request bodies, validates endpoints, guards API keys against cleartext transmission to non-loopback hosts, and computes generation timings for remote AI model calls.

#### Purpose
- Serializes `AiGenerationRequest` into OpenAI-compatible JSON with conditional sampling parameters (top_k, min_p, seed, stop, reasoning effort).
- Enforces security policy preventing API key leakage over plain HTTP to remote hosts; computes `AiGenerationTimings` from `ChatResponse` usage and wall-clock duration.

#### Type
- `final class` (package-private), annotated `@ToString` (Lombok).
- Holds `LlamaCppJniConfig config`, `AiPromptSupport promptSupport`, `Function<String, @Nullable String> environment`, `Java8CompatibilityHelper compatibilityHelper`; static `ObjectMapper MAPPER`.

#### Input
- Constructor: `LlamaCppJniConfig config`, `AiPromptSupport promptSupport`, `Function<String, @Nullable String> environment` (all null-checked).
- `requestBody`: `AiGenerationRequest` (exposes `promptKey()`, `sourceFile()`, `sourceText()`).
- `apiKeyFor`: endpoint `String`.
- `timings`: completion `String text`, `ChatResponse response`, wall-clock `double seconds`.
- `errorMessage`: endpoint `String`, HTTP `int status`, body `String`.
- Static helpers: host `String`, status `int`.

#### Output
- `requestBody` → JSON `String` of chat-completions payload.
- `endpoint` → normalized URL `String` ending in `/chat/completions`.
- `apiKey` / `apiKeyFor` → trimmed API key `String` or `null`.
- `isLoopbackHost` / `isSuccess` → `boolean`.
- `errorMessage` → formatted error `String` with ≤500-char body excerpt.
- `timings` → `AiGenerationTimings` with token counts and decode rate.

#### Core logic
- `requestBody` builds `ObjectNode`: always sets `max_tokens`, `temperature`, `top_p`, `repetition_penalty`, `stream=false`; conditionally sets `model` (non-blank), `top_k` (>0), `min_p` (>0.0), `seed` (≥0), `stop` array (non-empty), `reasoning.effort` (non-blank); adds system+user messages from `promptSupport`.
- `endpoint` trims `baseUrl`, rejects non-`http(s)` schemes with `IllegalArgumentException`, strips trailing `/`, appends `CHAT_COMPLETIONS_PATH`.
- `apiKey` returns `null` for blank `apiKeyEnv` or unset/blank env var; else trimmed value.
- `apiKeyFor` returns key unchanged for HTTPS or no key; for HTTP endpoints parses host via `URL` and throws `IOException` if host is not loopback (`localhost`, `127.x.x.x`, `[::1]`).
- `isSuccess` checks `200 ≤ status < 300`.
- `errorMessage` concatenates endpoint, status, and first 500 chars of body plus `"..."` if truncated.
- `timings` extracts `Usage` from `ChatResponse`; `decodeRate = completionTokens / seconds` (0 if `seconds ≤ 0`); prefill rate fixed at `0.0`; returns `AiGenerationTimings`.

#### Public API
- `requestBody(AiGenerationRequest) -> String` — serializes chat request JSON
- `endpoint() -> String` — builds validated chat-completions URL
- `apiKey() -> String` — looks up env-named API key
- `apiKeyFor(String endpoint) -> String` — key with loopback-only cleartext guard
- `isLoopbackHost(String host) -> boolean` — checks localhost/127.x/[::1]
- `isSuccess(int status) -> boolean` — checks 2xx range
- `errorMessage(String, int, String) -> String` — formats HTTP error excerpt
- `timings(String, ChatResponse, double) -> AiGenerationTimings` — computes token rates
- Constants: `CHAT_COMPLETIONS_PATH`, `ERROR_BODY_EXCERPT`, `HTTP_SCHEME`, `HTTPS_SCHEME`

#### Dependencies
- `com.fasterxml.jackson.databind.ObjectMapper`, `ArrayNode`, `ObjectNode`
- `java.io.IOException`, `java.net.URL`, `java.util.List`, `Objects`, `function.Function`, `regex.Pattern`
- `lombok.ToString`
- `net.ladenthin.llama.value.ChatResponse`, `Usage`
- `net.ladenthin.srcmorph.document.AiGenerationRequest`
- `net.ladenthin.srcmorph.prompt.AiPromptSupport`
- `net.ladenthin.srcmorph.support.Java8CompatibilityHelper`
- `org.jspecify.annotations.Nullable`
- `LlamaCppJniConfig`, `AiGenerationTimings` (same package)

#### Exceptions / Errors
- `IOException`: thrown by `requestBody` (serialization failure) and `apiKeyFor` (API key over plain HTTP to non-loopback host, or URL parse failure).
- `IllegalArgumentException`: thrown by `endpoint()` when `baseUrl` lacks `http://` or `https://` prefix.
- `NullPointerException`: constructor rejects null `config`, `promptSupport`, or `environment` via `Objects.requireNonNull`.
- Null returns: `apiKey`/`apiKeyFor` return `null` when no key configured or env var unset/blank.

#### Concurrency
- Immutable after construction: all instance fields `final`.
- Static `MAPPER` is thread-safe for concurrent reads.
- Static methods are pure functions; no mutable shared state or synchronization.
