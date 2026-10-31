### OpenAiCompatibleProviderSupport.java
- H: 1.0
- C: 92F0B02B
- D: 2026-10-31T10:06:17Z
- T: 2026-10-31T10:39:56Z
- G: 
- A: 0.0.0
- X: file
---
> <one-sentence domain essence>
> Generates request bodies, endpoints, and safely handles API keys for OpenAI-compatible providers without network calls.

#### Purpose
- Constructs JSON request bodies for chat completions with model-specific and sampling parameters.
- Computes the correct endpoint URL from `baseUrl` and the chat completions path.
- Retrieves and validates API keys from environment variables, refusing to send them over plain HTTP to non-loopback hosts.
- Checks HTTP status codes for success (2xx), extracts error excerpts, and computes token-based timings from server responses.

#### Type
final class OpenAiCompatibleProviderSupport; extends none; implements none.  
Modifiers: final.  
Annotations: @ToString.  
Key fields: static final strings (paths, schemes, host), static final ints (status bounds), static final Pattern, static final ObjectMapper, private static finals (host patterns, ellipsis).  
Generics: Function<String, @Nullable String> (used for environment lookup).

#### Input
- `LlamaCppJniConfig config`: provides `remoteModel`, `baseUrl`, `apiKeyEnv`, sampling values (`maxOutputTokens`, `temperature`, `topP`, `topK`, `minP`, `repeatPenalty`, `seed`, `stopStrings`, `reasoningEffort`).
- `AiPromptSupport promptSupport`: provides `systemPrompt` and `userMessage` for messages.
- `Function<String, @Nullable String> environment`: maps environment variable names to values (may return null or blank).
- Method parameters: `AiGenerationRequest request` (for `requestBody`), `String endpoint` (for `apiKeyFor`), `String host` (for `isLoopbackHost`), `String endpoint`, `int status`, `String body` (for `errorMessage`), `String text`, `ChatResponse response`, `double seconds` (for `timings`).

#### Output
- `String requestBody(...)`: serialized JSON request body.
- `String endpoint()`: trimmed `baseUrl` + `/chat/completions` without trailing slash.
- `@Nullable String apiKey()`: trimmed API key from environment, or null if not set/blank.
- `@Nullable String apiKeyFor(String endpoint)`: same as `apiKey()` but throws `IOException` if endpoint is plain `http` to a non-loopback host.
- `boolean isLoopbackHost(String host)`: true for `localhost`, `127.x.x.x`, or `[::1]`.
- `boolean isSuccess(int status)`: true for HTTP 200–299.
- `String errorMessage(String endpoint, int status, String body)`: formatted error message with excerpt.
- `AiGenerationTimings timings(String text, ChatResponse response, double seconds)`: timings object with prompt/cached tokens, zero prefill rate, completion tokens, and decode rate.

#### Core logic
- `requestBody`: builds an `ObjectNode` with `model` (if non-blank), `messages` array (system + user), `max_tokens`, `temperature`, `top_p`, conditionally `top_k` (if >0), conditionally `min_p` (if >0), `repetition_penalty`, conditionally `seed` (if ≥0), conditionally `stop` array (if non-empty), conditionally `reasoning.effort` (if non-blank), and `stream=false`. Serializes to JSON.
- `endpoint`: trims `baseUrl`, validates it starts with `http://` or `https://`, removes trailing slashes, appends `/chat/completions`.
- `apiKey`: if `apiKeyEnv` is blank → null; otherwise calls `environment.apply(apiKeyEnv)` and returns trimmed key if non-blank, else null.
- `apiKeyFor`: calls `apiKey()`; if key is null or endpoint does not start with `http://` → return key; otherwise parse host; if host is not loopback → throw `IOException`; else return key.
- `isLoopbackHost`: returns true if host equals `localhost`, matches `IPV4_LOOPBACK` pattern, or equals `IPV6_LOOPBACK`.
- `isSuccess`: returns true if status ≥ 200 and < 300.
- `errorMessage`: if body longer than `ERROR_BODY_EXCERPT`, returns first `EXCERPT` chars + `ELLIPSIS`; else returns body; formats as `endpoint answered HTTP status: excerpt`.
- `timings`: extracts `Usage` from `response`, gets `completionTokens` and `promptTokens`/`cachedTokens`, computes `decodeRate = completionTokens / seconds` (0 if seconds ≤ 0), returns `AiGenerationTimings` with prefill rate 0.0.

#### Public API
- `String requestBody(AiGenerationRequest request) throws IOException` -> `String`: builds and returns JSON request body.
- `String endpoint()` -> `String`: returns trimmed `baseUrl` + `/chat/completions`.
- `@Nullable String apiKey()` -> `@Nullable String`: returns API key from environment or null.
- `@Nullable String apiKeyFor(String endpoint) throws IOException` -> `@Nullable String`: returns key if safe (loopback/https), throws if plain HTTP to non-loopback.
- `static boolean isLoopbackHost(String host)` -> `boolean`: true for loopback hosts (`localhost`, `127.x.x.x`, `[::1]`).
- `static boolean isSuccess(int status)` -> `boolean`: true for HTTP 200–299.
- `static String errorMessage(String endpoint, int status, String body)` -> `String`: returns formatted error message with excerpt.
- `static AiGenerationTimings timings(String text, ChatResponse response, double seconds)` -> `AiGenerationTimings`: computes timings from usage and wall-clock seconds.

#### Dependencies
- `com.fasterxml.jackson.databind.ObjectMapper`
- `com.fasterxml.jackson.databind.node.ArrayNode`, `ObjectNode`
- `java.io.IOException`
- `java.net.URL`
- `java.util.List`, `java.util.Objects`
- `java.util.function.Function`
- `java.util.regex.Pattern`
- `lombok.ToString`
- `net.ladenthin.llama.value.ChatResponse`, `Usage`
- `net.ladenthin.srcmorph.document.AiGenerationRequest`
- `net.ladenthin.srcmorph.prompt.AiPromptSupport`
- `net.ladenthin.srcmorph.support.Java8CompatibilityHelper`
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- `IllegalArgumentException` in `endpoint()` if `baseUrl` does not start with `http://` or `https://`.
- `IOException` in `requestBody()` (from `MAPPER.writeValueAsString`), `apiKeyFor()` (thrown when host is non-loopback with plain HTTP), and `timings()` indirectly via usage.
- `null` handling: `environment` function may return null; `apiKey()` and `apiKeyFor()` return null when no key; `isBlank` checks prevent adding empty values; `Objects.requireNonNull` enforces non-null constructor args.
- Error excerpt limited to `ERROR_BODY_EXCERPT` (500 chars) with `ELLIPSIS` (`...`) suffix.

#### Concurrency
- No instance fields modified after construction; all fields are final or static.
- No synchronization, locks, or shared mutable state.
- Thread-safe by design (immutable after init).

> Note: The class is `final` and has no instance mutability after construction, ensuring thread safety. No concurrency annotations are present.
