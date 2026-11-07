### OpenAiCompatibleProviderSupport.java
- H: 1.0
- C: 92F0B02B
- D: 2026-11-04T12:04:37Z
- T: 2026-11-04T12:10:07Z
- G: 
- A: 0.0.0
- X: file
- P: 0B812A3C
---
> Builds and validates the JSON request, endpoint, and API key handling for an OpenAI‑compatible chat‑completion provider, and computes call timings and error messages.

#### Purpose
- Constructs chat‑completion request bodies with sampling knobs.
- Validates and builds the endpoint URL.
- Retrieves and secures the API key.
- Provides utilities for status checks, error messages, and timing calculations.

#### Type
- `final class OpenAiCompatibleProviderSupport`  
  - extends `Object`  
  - fields:  
    - `private final LlamaCppJniConfig config`  
    - `@ToString.Exclude private final AiPromptSupport promptSupport`  
    - `@ToString.Exclude private final Function<String, @Nullable String> environment`  
    - `private final Java8CompatibilityHelper compatibilityHelper`  
    - `private static final ObjectMapper MAPPER`  
  - static constants: `CHAT_COMPLETIONS_PATH`, `ERROR_BODY_EXCERPT`, `HTTP_OK_FIRST`, `HTTP_OK_END`, `HTTP_SCHEME`, `HTTPS_SCHEME`, `LOOPBACK_HOST_NAME`, `IPV4_LOOPBACK`, `IPV6_LOOPBACK`, `ELLIPSIS`.

#### Input
- Constructor parameters: `LlamaCppJniConfig config`, `AiPromptSupport promptSupport`, `Function<String, @Nullable String> environment`.
- `requestBody(AiGenerationRequest request)` consumes `AiGenerationRequest` fields and config knobs.
- `endpoint()` reads `config.baseUrl()`.
- `apiKey()` reads `config.apiKeyEnv()` and environment lookup.
- `apiKeyFor(String endpoint)` reads `apiKey()` and parses `endpoint`.
- `isLoopbackHost(String host)` receives host string.
- `isSuccess(int status)` receives HTTP status.
- `errorMessage(String endpoint, int status, String body)` receives endpoint, status, body.
- `timings(String text, ChatResponse response, double seconds)` receives parsed text, response, and elapsed seconds.

#### Output
- `requestBody` returns JSON string.
- `endpoint` returns full URL string.
- `apiKey` returns trimmed key or `null`.
- `apiKeyFor` returns key or `null`; throws `IOException` on insecure HTTP to non‑loopback.
- `isLoopbackHost` returns boolean.
- `isSuccess` returns boolean.
- `errorMessage` returns formatted message string.
- `timings` returns `AiGenerationTimings` instance.

#### Core logic
- **requestBody**:  
  - Create `ObjectNode body`.  
  - Add `"model"` if `remoteModel` non‑blank.  
  - Build `"messages"` array with system and user prompts.  
  - Set `"max_tokens"`, `"temperature"`, `"top_p"`.  
  - Conditionally add `"top_k"`, `"min_p"`, `"seed"`, `"stop"` array, `"reasoning.effort"`.  
  - Set `"stream"` to `false`.  
  - Serialize with `MAPPER`.
- **endpoint**:  
  - Trim `config.baseUrl()`.  
  - Validate starts with `http://` or `https://`.  
  - Strip trailing slashes.  
  - Append `CHAT_COMPLETIONS_PATH`.
- **apiKey**:  
  - Return `null` if `apiKeyEnv` blank.  
  - Lookup env var; return trimmed key or `null` if unset/blank.
- **apiKeyFor**:  
  - If key `null` or endpoint not plain `http://`, return key.  
  - Parse host from `endpoint`.  
  - If host not loopback (`isLoopbackHost`), throw `IOException`.  
  - Return key.
- **isLoopbackHost**:  
  - Return true if host equals `"localhost"`, matches `IPV4_LOOPBACK`, or equals `"[::1]"`.
- **isSuccess**:  
  - Return true if status in `[200,300)`.
- **errorMessage**:  
  - Truncate body to `ERROR_BODY_EXCERPT` with ellipsis if longer.  
  - Return formatted string with endpoint, status, excerpt.
- **timings**:  
  - Extract `Usage` from `ChatResponse`.  
  - Compute `completionTokens`, `decodeRate = completionTokens / seconds` (or `0.0`).  
  - Return new `AiGenerationTimings` with prompt, cached, decode, completion, rate.

#### Public API
- `String requestBody(AiGenerationRequest request) -> JSON body`  
- `String endpoint() -> URL`  
- `@Nullable String apiKey() -> key or null`  
- `@Nullable String apiKeyFor(String endpoint) -> key or null`  
- `static boolean isLoopbackHost(String host) -> true/false`  
- `static boolean isSuccess(int status) -> true/false`  
- `static String errorMessage(String endpoint, int status, String body) -> message`  
- `static AiGenerationTimings timings(String text, ChatResponse response, double seconds) -> timings`

#### Dependencies
- `com.fasterxml.jackson.databind.ObjectMapper`, `ArrayNode`, `ObjectNode`  
- `java.io.IOException`, `java.net.URL`  
- `java.util.List`, `java.util.Objects`, `java.util.function.Function`, `java.util.regex.Pattern`  
- `lombok.ToString`  
- `net.ladenthin.llama.value.ChatResponse`, `Usage`  
- `net.ladenthin.srcmorph.document.AiGenerationRequest`  
- `net.ladenthin.srcmorph.prompt.AiPromptSupport`  
- `net.ladenthin.srcmorph.support.Java8CompatibilityHelper`  
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- `IllegalArgumentException` in `endpoint()` if baseUrl not HTTP(S).  
- `IOException` in `apiKeyFor()` if plain HTTP to non‑loopback host or URL parsing fails.  
- `IOException` in `requestBody()` if JSON serialization fails.

#### Concurrency
- All fields are immutable after construction; no synchronization required.  
- `MAPPER` is thread‑safe for read operations.
