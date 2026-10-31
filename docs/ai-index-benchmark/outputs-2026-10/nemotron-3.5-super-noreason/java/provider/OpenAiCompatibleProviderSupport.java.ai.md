### OpenAiCompatibleProviderSupport.java
- H: 1.0
- C: 92F0B02B
- D: 2026-10-31T10:00:28Z
- T: 2026-10-31T10:02:15Z
- G: 
- A: 0.0.0
- X: file
---
> Builds OpenAI-compatible chat-completions request bodies, endpoints, API keys, error messages, and timings without performing any network I/O.

#### Purpose
- Encapsulates all non-network logic for `OpenAiCompatibleAiGenerationProvider`: JSON request construction, endpoint validation, API key lookup with cleartext protection, HTTP status checks, error message formatting, and call timing calculation.

#### Type
- `final class OpenAiCompatibleProviderSupport`
- Package-private (no modifier)
- Lombok `@ToString` with `@ToString.Exclude` on `promptSupport` and `environment`
- Fields: `config` (`LlamaCppJniConfig`), `promptSupport` (`AiPromptSupport`), `environment` (`Function<String, @Nullable String>`), `compatibilityHelper` (`Java8CompatibilityHelper`)

#### Input
- Constructor: `LlamaCppJniConfig config`, `AiPromptSupport promptSupport`, `Function<String, @Nullable String> environment`
- `requestBody`: `AiGenerationRequest request`
- `endpoint`: reads `config.baseUrl()`
- `apiKey`: reads `config.apiKeyEnv()`, calls `environment.apply(String)`
- `apiKeyFor`: `String endpoint`
- `isLoopbackHost`: `String host`
- `isSuccess`: `int status`
- `errorMessage`: `String endpoint`, `int status`, `String body`
- `timings`: `String text`, `ChatResponse response`, `double seconds`

#### Output
- `requestBody`: `String` JSON body (throws `IOException`)
- `endpoint`: `String` URL (throws `IllegalArgumentException`)
- `apiKey`: `@Nullable String` trimmed key or `null`
- `apiKeyFor`: `@Nullable String` key or `null` (throws `IOException`)
- `isLoopbackHost`: `boolean`
- `isSuccess`: `boolean`
- `errorMessage`: `String`
- `timings`: `AiGenerationTimings`
- Mutates no fields; `compatibilityHelper` is instantiated once and never reassigned.

#### Core logic
- `requestBody` builds a Jackson `ObjectNode` with `model`, `messages` (system + user), `max_tokens`, `temperature`, `top_p`, `repetition_penalty`, `stream=false`, and conditionally `top_k` (>0), `min_p` (>0.0f), `seed` (>=0), `stop` array (non-empty), and `reasoning.effort` (non-blank); serializes to string.
- `endpoint` trims `baseUrl`, rejects non-http(s) schemes with `IllegalArgumentException`, strips trailing slashes, appends `/chat/completions`.
- `apiKey` returns `null` if `apiKeyEnv` is blank or the env lookup returns null/blank; otherwise returns trimmed key.
- `apiKeyFor` returns the key unchanged for https or when no key; for plain http, parses the host via `URL` and throws `IOException` unless the host is loopback (`localhost`, `127.x.x.x`, `[::1]`).
- `isLoopbackHost` checks exact `localhost`, IPv4 loopback regex `127.\d{1,3}.\d{1,3}.\d{1,3}`, or `[::1]`.
- `isSuccess` returns true for status 200–299.
- `errorMessage` truncates body at 500 chars appending `...` if longer, returns `endpoint answered HTTP status: excerpt`.
- `timings` extracts `Usage` from `ChatResponse`, computes decode rate as completion tokens / seconds (0.0 if seconds <= 0), prefill rate fixed at 0.0, returns `AiGenerationTimings`.

#### Public API
- `OpenAiCompatibleProviderSupport(LlamaCppJniConfig, AiPromptSupport, Function<String, @Nullable String>) -> OpenAiCompatibleProviderSupport` — constructs support for one model
- `requestBody(AiGenerationRequest) -> String` — serializes chat-completions JSON request body
- `endpoint() -> String` — returns validated chat-completions URL
- `apiKey() -> @Nullable String` — returns trimmed API key or null
- `apiKeyFor(String) -> @Nullable String` — returns key safe to send to endpoint, throws on cleartext http to non-loopback
- `isLoopbackHost(String) -> boolean` — checks if host is loopback
- `isSuccess(int) -> boolean` — checks if HTTP status is 2xx
- `errorMessage(String, int, String) -> String` — formats endpoint, status, and body excerpt
- `timings(String, ChatResponse, double) -> AiGenerationTimings` — computes call timings from usage and wall-clock seconds

#### Dependencies
- `com.fasterxml.jackson.databind.ObjectMapper`
- `com.fasterxml.jackson.databind.node.ArrayNode`
- `com.fasterxml.jackson.databind.node.ObjectNode`
- `java.io.IOException`
- `java.net.URL`
- `java.util.List`
- `java.util.Objects`
- `java.util.function.Function`
- `java.util.regex.Pattern`
- `lombok.ToString`
- `net.ladenthin.llama.value.ChatResponse`
- `net.ladenthin.llama.value.Usage`
- `net.ladenthin.srcmorph.document.AiGenerationRequest`
- `net.ladenthin.srcmorph.prompt.AiPromptSupport`
- `net.ladenthin.srcmorph.provider.LlamaCppJniConfig`
- `net.ladenthin.srcmorph.support.Java8CompatibilityHelper`
- `org.jspecify.annotations.Nullable`
- `net.ladenthin.srcmorph.provider.AiGenerationTimings`

#### Exceptions / Errors
- `requestBody` throws `IOException` if JSON serialization fails.
- `endpoint` throws `IllegalArgumentException` if `baseUrl` does not start with `http://` or `https://`.
- `apiKeyFor` throws `IOException` if a key is configured and endpoint is plain http to a non-loopback host, or if `endpoint` cannot be parsed by `URL`.
- Constructor uses `Objects.requireNonNull` on all three parameters, throwing `NullPointerException` if any is null.
- `apiKey` returns `null` (not an exception) when `apiKeyEnv` is blank or unset/blank.

#### Concurrency
- No synchronization, locks, or volatile fields.
- Instances are effectively immutable after construction (all fields are `final`).
- `MAPPER` is a static shared `ObjectMapper`; Jackson `ObjectMapper` is thread-safe for reading/writing once configured.
- Static constants and stateless static methods (`isLoopbackHost`, `isSuccess`, `errorMessage`, `timings`) are thread-safe.
- `environment` function is caller-provided; thread safety depends on the implementation.
