### OpenAiCompatibleProviderSupport.java
- H: 1.0
- C: 92F0B02B
- D: 2026-10-28T21:24:22Z
- T: 2026-10-28T22:43:12Z
- G: 
- A: 0.0.0
- X: file
---
> Builds OpenAI-compatible chat-completions request bodies, resolves endpoints, validates API-key transmission security, and computes generation timings for remote LLM providers.

#### Purpose
- Non-network support layer for `OpenAiCompatibleAiGenerationProvider`: request serialization, URL construction, key lookup with cleartext-HTTP guard, status checking, error formatting, and timing derivation.
- Split from the provider to sit on the 100% PIT mutation-coverage gate; socket handling stays in the provider.

#### Type
- `final class`, package-private (no modifier)
- Annotated `@ToString` (Lombok); `promptSupport` and `environment` excluded via `@ToString.Exclude`
- No extends/implements

#### Input
- Constructor: `LlamaCppJniConfig config`, `AiPromptSupport promptSupport`, `Function<String, @Nullable String> environment`
- `requestBody`: `AiGenerationRequest` (carries `promptKey`, `sourceFile`, `sourceText`)
- `apiKeyFor`: `String endpoint` (the resolved URL)
- `timings`: `String text`, `ChatResponse response`, `double seconds`
- `errorMessage`: `String endpoint`, `int status`, `String body`
- `isLoopbackHost`: `String host`
- `isSuccess`: `int status`

#### Output
- `requestBody` → `String` (JSON body)
- `endpoint` → `String` (full chat-completions URL)
- `apiKey` / `apiKeyFor` → `@Nullable String`
- `isLoopbackHost` / `isSuccess` → `boolean`
- `errorMessage` → `String`
- `timings` → `AiGenerationTimings`

#### Core logic
- **requestBody**: Builds a Jackson `ObjectNode` with `messages` (system + user from `AiPromptSupport`), always sets `max_tokens`, `temperature`, `top_p`, `repetition_penalty`, `stream=false`; conditionally adds `model` (non-blank), `top_k` (>0), `min_p` (>0.0f), `seed` (>=0), `stop` (non-empty list), `reasoning.effort` (non-blank).
- **endpoint**: Trims `baseUrl`, strips trailing slashes, validates `http://` or `https://` prefix (throws `IllegalArgumentException` otherwise), appends `/chat/completions`.
- **apiKey**: Returns `null` if `apiKeyEnv` is blank, env var unset, or value blank; otherwise returns trimmed key.
- **apiKeyFor**: If key is non-null and endpoint is plain `http://`, parses the host and requires it to be loopback (`localhost`, `127.x.x.x`, `[::1]`); throws `IOException` on non-loopback cleartext. HTTPS endpoints pass through unconditionally.
- **isLoopbackHost**: Exact match on `localhost`, regex match on `127.\d{1,3}.\d{1,3}.\d{1,3}`, exact match on `[::1]`.
- **isSuccess**: `200 <= status < 300`.
- **errorMessage**: Truncates body at 500 chars with `...` suffix; formats as `endpoint + " answered HTTP " + status + ": " + excerpt`.
- **timings**: Extracts `completionTokens` and `promptTokens` from `ChatResponse.Usage`; computes `decodeRate = completionTokens / seconds` (0 if seconds ≤ 0); prefill rate hardcoded to `0.0d` (unknown for remote).

#### Public API
All members are package-private (class is package-private). 8 methods + 1 constructor:

- `OpenAiCompatibleProviderSupport(LlamaCppJniConfig, AiPromptSupport, Function<String,@Nullable String>)` → void — binds config, prompts, env lookup
- `requestBody(AiGenerationRequest)` → `String` — serializes chat-completions JSON
- `endpoint()` → `String` — resolves full chat URL
- `apiKey()` → `@Nullable String` — reads key from env var
- `apiKeyFor(String)` → `@Nullable String` — guards cleartext key transmission
- `isLoopbackHost(String)` → `boolean` — static; checks loopback identity
- `isSuccess(int)` → `boolean` — static; 2xx range check
- `errorMessage(String, int, String)` → `String` — static; formats failure message
- `timings(String, ChatResponse, double)` → `AiGenerationTimings` — static; derives rates from usage

#### Dependencies
- `com.fasterxml.jackson.databind.ObjectMapper`, `ObjectNode`, `ArrayNode`
- `java.net.URL`
- `java.util.regex.Pattern`
- `java.util.function.Function`
- `lombok.ToString`
- `net.ladenthin.llama.value.ChatResponse`, `Usage`
- `net.ladenthin.srcmorph.document.AiGenerationRequest`
- `net.ladenthin.srcmorph.prompt.AiPromptSupport`
- `net.ladenthin.srcmorph.support.Java8CompatibilityHelper`
- `org.jspecify.annotations.Nullable`
- Referenced: `LlamaCppJniConfig`, `AiGenerationTimings`, `OpenAiCompatibleAiGenerationProvider`, `LlamaCppJniProviderSupport`

#### Exceptions / Errors
- `IllegalArgumentException` from `endpoint()` when `baseUrl` lacks `http://` or `https://` prefix
- `IOException` from `requestBody()` on serialization failure
- `IOException` from `apiKeyFor()` when a key would be sent over plain HTTP to a non-loopback host
- `NullPointerException` guarded via `Objects.requireNonNull` in constructor for all three params

#### Concurrency
- All instance fields are `final`; `MAPPER` is a shared static `ObjectMapper` (thread-safe for serialization after configuration)
- No mutable state; class is effectively immutable after construction
- No synchronization needed
