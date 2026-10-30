### OpenAiCompatibleProviderSupport.java
- H: 1.0
- C: 92F0B02B
- D: 2026-10-30T00:29:58Z
- T: 2026-10-30T00:31:21Z
- G: 
- A: 0.0.0
- X: file
---
> Builds the OpenAI-compatible chat-completions request, endpoint, and API-key rules for a remote LLM, without performing network I/O.

#### Purpose
- Supplies the offline pieces of `OpenAiCompatibleAiGenerationProvider`: request JSON, endpoint URL, API key lookup, status check, error message, and timings.
- Split out so the provider's socket code stays outside the 100% PIT gate.

#### Type
- `final class`, package-private, no `extends`, no `implements`.
- Lombok `@ToString`; `@ToString.Exclude` on `promptSupport` and `environment`.
- Uses `@Nullable` (jspecify) on `apiKey()`, `apiKeyFor(...)`, and the `environment` function type.

#### Input
- Constructor: `LlamaCppJniConfig config`, `AiPromptSupport promptSupport`, `Function<String, @Nullable String> environment`.
- `requestBody(AiGenerationRequest)`: reads `promptKey()`, `sourceFile()`, `sourceText()`.
- Config reads: `remoteModel`, `maxOutputTokens`, `temperature`, `topP`, `topK`, `minP`, `repeatPenalty`, `seed`, `stopStrings`, `reasoningEffort`, `baseUrl`, `apiKeyEnv`.
- `endpoint()` reads `config.baseUrl()`.
- `apiKey()` reads an environment variable named by `config.apiKeyEnv()`.
- `apiKeyFor(String endpoint)` takes the endpoint URL.
- `isLoopbackHost(String host)`, `isSuccess(int status)`, `errorMessage(String, int, String)`, `timings(String, ChatResponse, double)` take their parameters directly.

#### Output
- `requestBody` returns a JSON string (`model`, `messages`, `max_tokens`, `temperature`, `top_p`, optional `top_k`, `min_p`, `seed`, `stop`, `reasoning.effort`, `stream=false`).
- `endpoint` returns `baseUrl` (trimmed, trailing slashes removed) + `/chat/completions`.
- `apiKey` / `apiKeyFor` return a trimmed key or `null`.
- `isLoopbackHost` / `isSuccess` return `boolean`.
- `errorMessage` returns a string: endpoint, HTTP status, body excerpt.
- `timings` returns `AiGenerationTimings`.
- No mutated state and no I/O beyond JSON serialisation.

#### Core logic
- Request body:
  - Adds `model` only when `remoteModel` is non-blank.
  - Adds a system message (`promptSupport.systemPrompt`) and a user message (`promptSupport.userMessage`).
  - Adds `top_k` only when > 0, `min_p` only when > 0.0, `seed` only when >= 0, `stop` only when non-empty, `reasoning.effort` only when non-blank.
  - Always sets `stream` to `false`.
- Endpoint: rejects any `baseUrl` not starting with `http://` or `https://`; strips trailing `/`; appends `/chat/completions`.
- API key:
  - Returns `null` when `apiKeyEnv` is blank or the variable is unset or blank.
  - Otherwise returns the trimmed value.
- Key transport guard (`apiKeyFor`):
  - Returns the key unchanged for `https` endpoints or when no key is set.
  - For `http` endpoints, allows the key only when the host is loopback.
  - Otherwise throws `IOException` before any send.
- Loopback check: exact `localhost`, IPv4 `127.x.x.x` (anchored regex), or `[::1]`.
- Success check: HTTP status 200 to 299 inclusive.
- Error message: truncates body to 500 characters and appends `...` when longer.
- Timings:
  - Maps `Usage` prompt, cached, and completion tokens to `int`.
  - Decode rate = completion tokens / wall-clock seconds (0.0 when seconds <= 0).
  - Prefill rate fixed at 0.0 (treated as unknown by callers).

#### Public API
No `public` or `protected` members. All members are package-private.

- Constructor `(LlamaCppJniConfig, AiPromptSupport, Function<String, @Nullable String>)` - null-checks and stores dependencies.
- `requestBody(AiGenerationRequest) -> String` - builds chat-completions JSON body.
- `endpoint() -> String` - builds validated chat-completions URL.
- `apiKey() -> @Nullable String` - reads trimmed key from env.
- `apiKeyFor(String endpoint) -> @Nullable String` - key, guarded against cleartext transport.
- `static isLoopbackHost(String host) -> boolean` - loopback host test.
- `static isSuccess(int status) -> boolean` - 2xx test.
- `static errorMessage(String endpoint, int status, String body) -> String` - error text with excerpt.
- `static timings(String text, ChatResponse response, double seconds) -> AiGenerationTimings` - token counts and rates.

Package-private constants: `CHAT_COMPLETIONS_PATH`, `ERROR_BODY_EXCERPT`, `HTTP_SCHEME`, `HTTPS_SCHEME`.

#### Dependencies
- `com.fasterxml.jackson.databind`: `ObjectMapper`, `ObjectNode`, `ArrayNode`
- `java.io.IOException`, `java.net.URL`, `java.util.List`, `java.util.Objects`, `java.util.function.Function`, `java.util.regex.Pattern`
- `lombok.ToString`
- `net.ladenthin.llama.value`: `ChatResponse`, `Usage`
- `net.ladenthin.srcmorph`: `document.AiGenerationRequest`, `prompt.AiPromptSupport`, `support.Java8CompatibilityHelper`, `AiGenerationTimings`, `AiGenerationConfig` (referenced in Javadoc)
- `LlamaCppJniConfig`, `LlamaCppJniProviderSupport`, `OpenAiCompatibleAiGenerationProvider` (referenced)
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- `Objects.requireNonNull` in constructor: `NullPointerException` for `config`, `promptSupport`, `environment`.
- `endpoint()`: `IllegalArgumentException` when `baseUrl` is not `http(s)`.
- `apiKeyFor`: `IOException` when a key would go over non-loopback plain `http`, or when `new URL(endpoint)` fails to parse.
- `requestBody`: `IOException` when serialisation fails.
- `apiKey()` returns `null` for missing, unset, or blank variables (no exception).
- `timings`: `seconds <= 0` yields decode rate 0.0 (no division).

#### Concurrency
- All instance fields are `final`; the class holds no mutable state.
- `MAPPER` is a shared static `ObjectMapper`, used only for creating and writing nodes.
- No synchronization. Thread safety depends on the injected `AiPromptSupport` and `environment` function.
