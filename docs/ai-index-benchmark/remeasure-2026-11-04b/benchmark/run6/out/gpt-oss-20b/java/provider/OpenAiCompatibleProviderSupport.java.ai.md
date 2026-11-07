### OpenAiCompatibleProviderSupport.java
- H: 1.0
- C: 92F0B02B
- D: 2026-11-04T12:56:06Z
- T: 2026-11-04T12:57:09Z
- G: 
- A: 0.0.0
- X: file
- P: 0B812A3C
---
> Builds the JSON request, validates endpoints, secures API keys, and interprets responses for an OpenAI‑compatible chat model.

#### Purpose
- Constructs chat‑completion request bodies with sampling knobs.
- Validates HTTP endpoints and API key safety.
- Parses error messages and computes generation timings.

#### Type
- `final class OpenAiCompatibleProviderSupport` (class, final)  
  - Extends `Object`  
  - Annotations: `@ToString`  
  - Fields:  
    - `static final String CHAT_COMPLETIONS_PATH` (class)  
    - `static final int ERROR_BODY_EXCERPT` (class)  
    - `private static final int HTTP_OK_FIRST` (class)  
    - `private static final int HTTP_OK_END` (class)  
    - `static final String HTTP_SCHEME` (class)  
    - `static final String HTTPS_SCHEME` (class)  
    - `private static final String LOOPBACK_HOST_NAME` (class)  
    - `private static final Pattern IPV4_LOOPBACK` (class)  
    - `private static final String IPV6_LOOPBACK` (class)  
    - `private static final String ELLIPSIS` (class)  
    - `private static final ObjectMapper MAPPER` (class)  
    - `private final LlamaCppJniConfig config` (instance)  
    - `@ToString.Exclude private final AiPromptSupport promptSupport` (instance)  
    - `@ToString.Exclude private final Function<String, @Nullable String> environment` (instance)  
    - `private final Java8CompatibilityHelper compatibilityHelper` (instance)

#### Input
- Constructor parameters: `LlamaCppJniConfig config`, `AiPromptSupport promptSupport`, `Function<String, @Nullable String> environment`.
- `requestBody(AiGenerationRequest request)` consumes `AiGenerationRequest` fields: `promptKey()`, `sourceFile()`, `sourceText()`.
- `endpoint()` reads `config.baseUrl()`.
- `apiKey()` reads `config.apiKeyEnv()` and `environment.apply(...)`.
- `apiKeyFor(String endpoint)` reads `endpoint` and `apiKey()`.
- `isLoopbackHost(String host)` reads `host`.
- `errorMessage(String endpoint, int status, String body)` reads `endpoint`, `status`, `body`.
- `timings(String text, ChatResponse response, double seconds)` reads `response.getUsage()`.

#### Output
- `requestBody` returns JSON string.
- `endpoint` returns URL string.
- `apiKey` returns trimmed key or `null`.
- `apiKeyFor` returns key or `null`; may throw `IOException`.
- `isLoopbackHost` returns boolean.
- `isSuccess` returns boolean.
- `errorMessage` returns formatted string.
- `timings` returns `AiGenerationTimings` instance.

#### Core logic
- **requestBody**  
  - Create `ObjectNode body`.  
  - Add `"model"` if `remoteModel` non‑blank.  
  - Build `"messages"` array with system and user messages from `promptSupport`.  
  - Set `"max_tokens"`, `"temperature"`, `"top_p"`.  
  - Conditionally add `"top_k"`, `"min_p"`, `"seed"`, `"stop"` array, `"reasoning.effort"`.  
  - Set `"stream"` to `false`.  
  - Serialize with `MAPPER.writeValueAsString(body)`.
- **endpoint**  
  - Trim `config.baseUrl()`.  
  - Validate starts with `http://` or `https://`.  
  - Strip trailing slashes.  
  - Append `CHAT_COMPLETIONS_PATH`.
- **apiKey**  
  - Return `null` if `apiKeyEnv` blank.  
  - Lookup env var; return trimmed key or `null` if unset/blank.
- **apiKeyFor**  
  - If key `null` or endpoint not `http://`, return key.  
  - Parse host from `endpoint`.  
  - If host not loopback (`isLoopbackHost`), throw `IOException`.  
  - Return key.
- **isLoopbackHost**  
  - Return true if host equals `"localhost"`, matches `IPV4_LOOPBACK`, or equals `"[::1]"`.
- **isSuccess**  
  - Return true if status in [200,300).
- **errorMessage**  
  - Truncate body to `ERROR_BODY_EXCERPT` with ellipsis if longer.  
  - Return formatted string.
- **timings**  
  - Extract `completionTokens`, `promptTokens`, `cachedTokens` from `response.getUsage()`.  
  - Compute `decodeRate = completionTokens / seconds` if seconds > 0.  
  - Return new `AiGenerationTimings(text, promptTokens, cachedTokens, 0.0, completionTokens, decodeRate)`.

#### Public API
- `String requestBody(AiGenerationRequest request) -> JSON body`  
- `String endpoint() -> URL`  
- `@Nullable String apiKey() -> API key`  
- `@Nullable String apiKeyFor(String endpoint) -> API key or null`  
- `static boolean isLoopbackHost(String host) -> true if loopback`  
- `static boolean isSuccess(int status) -> true if 2xx`  
- `static String errorMessage(String endpoint, int status, String body) -> message`  
- `static AiGenerationTimings timings(String text, ChatResponse response, double seconds) -> timings`

#### Dependencies
- `com.fasterxml.jackson.databind.ObjectMapper`, `ArrayNode`, `ObjectNode`  
- `java.io.IOException`, `java.net.URL`, `java.util.List`, `java.util.Objects`, `java.util.function.Function`, `java.util.regex.Pattern`  
- `lombok.ToString`  
- `net.ladenthin.llama.value.ChatResponse`, `Usage`  
- `net.ladenthin.srcmorph.document.AiGenerationRequest`  
- `net.ladenthin.srcmorph.prompt.AiPromptSupport`  
- `net.ladenthin.srcmorph.support.Java8CompatibilityHelper`  
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- `IllegalArgumentException` in `endpoint()` if baseUrl not http(s).  
- `IOException` in `apiKeyFor()` if plain http to non‑loopback host or URL parsing fails.  
- `IOException` in `requestBody()` if JSON serialization fails.

#### Concurrency
- All fields are immutable after construction; no synchronization needed.  
- Methods are stateless except for reading immutable fields.
