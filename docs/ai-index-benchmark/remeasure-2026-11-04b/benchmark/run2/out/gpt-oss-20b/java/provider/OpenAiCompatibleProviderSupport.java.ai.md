### OpenAiCompatibleProviderSupport.java
- H: 1.0
- C: 92F0B02B
- D: 2026-11-04T11:56:04Z
- T: 2026-11-04T12:02:07Z
- G: 
- A: 0.0.0
- X: file
- P: 0B812A3C
---
> Builds JSON request bodies, validates endpoints, secures API keys, and interprets responses for an OpenAI‑compatible chat‑completion provider.

#### Purpose
- Constructs request payloads for chat‑completion calls.
- Validates and normalizes endpoint URLs.
- Secures API key transmission to loopback hosts only.
- Parses success status and error messages.
- Calculates generation timings from response usage.

#### Type
- `final class OpenAiCompatibleProviderSupport`  
  - extends `Object`  
  - annotations: `@ToString`  
  - fields:  
    - `static final String CHAT_COMPLETIONS_PATH` – endpoint suffix  
    - `static final int ERROR_BODY_EXCERPT` – error body limit  
    - `private static final int HTTP_OK_FIRST` – 200  
    - `private static final int HTTP_OK_END` – 300  
    - `static final String HTTP_SCHEME` – "http://"  
    - `static final String HTTPS_SCHEME` – "https://"  
    - `private static final String LOOPBACK_HOST_NAME` – "localhost"  
    - `private static final Pattern IPV4_LOOPBACK` – regex for 127.x.x.x  
    - `private static final String IPV6_LOOPBACK` – "[::1]"  
    - `private static final String ELLIPSIS` – "..."  
    - `private static final ObjectMapper MAPPER` – Jackson mapper  
    - `private final LlamaCppJniConfig config` – model config  
    - `private final AiPromptSupport promptSupport` – prompt definitions (`@ToString.Exclude`)  
    - `private final Function<String, @Nullable String> environment` – env lookup (`@ToString.Exclude`)  
    - `private final Java8CompatibilityHelper compatibilityHelper` – helper instance  

#### Input
- Constructor parameters: `LlamaCppJniConfig config`, `AiPromptSupport promptSupport`, `Function<String, @Nullable String> environment`.
- `requestBody(AiGenerationRequest request)` consumes `AiGenerationRequest` fields: `promptKey()`, `sourceFile()`, `sourceText()`.
- `endpoint()` reads `config.baseUrl()`.
- `apiKey()` reads `config.apiKeyEnv()`.
- `apiKeyFor(String endpoint)` reads `endpoint` and `config.apiKeyEnv()`.
- `isLoopbackHost(String host)` reads `host`.
- `isSuccess(int status)` reads `status`.
- `errorMessage(String endpoint, int status, String body)` reads all parameters.
- `timings(String text, ChatResponse response, double seconds)` reads `response.getUsage()`.

#### Output
- `requestBody` returns JSON string.
- `endpoint` returns full URL string.
- `apiKey` returns trimmed key or `null`.
- `apiKeyFor` returns key or `null`; throws `IOException` on insecure endpoint.
- `isLoopbackHost` returns boolean.
- `isSuccess` returns boolean.
- `errorMessage` returns formatted string.
- `timings` returns `AiGenerationTimings` instance.

#### Core logic
- `requestBody`:  
  - Create `ObjectNode` body.  
  - Add `model` if non‑blank.  
  - Add `messages` array with system and user messages from `promptSupport`.  
  - Set `max_tokens`, `temperature`, `top_p`.  
  - Conditionally add `top_k`, `min_p`, `seed`, `stop` array, `reasoning.effort`.  
  - Set `stream` to `false`.  
  - Serialize with `MAPPER`.
- `endpoint`:  
  - Trim `config.baseUrl()`.  
  - Validate starts with `http://` or `https://`.  
  - Strip trailing slashes.  
  - Append `CHAT_COMPLETIONS_PATH`.
- `apiKey`:  
  - Return `null` if `apiKeyEnv` blank.  
  - Lookup env var; return trimmed key or `null` if unset/blank.
- `apiKeyFor`:  
  - If key `null` or endpoint not `http://`, return key.  
  - Parse host from `endpoint`.  
  - If host not loopback (`isLoopbackHost`), throw `IOException`.  
  - Return key.
- `isLoopbackHost`:  
  - Return true if host equals `localhost`, matches `IPV4_LOOPBACK`, or equals `IPV6_LOOPBACK`.
- `isSuccess`:  
  - Return true if status in [200,300).
- `errorMessage`:  
  - Truncate body to `ERROR_BODY_EXCERPT` + `ELLIPSIS` if longer.  
  - Return formatted string with endpoint, status, excerpt.
- `timings`:  
  - Extract `completionTokens`, `promptTokens`, `cachedTokens` from `response.getUsage()`.  
  - Compute `decodeRate = completionTokens / seconds` if seconds > 0.  
  - Return new `AiGenerationTimings(text, promptTokens, cachedTokens, 0.0, completionTokens, decodeRate)`.

#### Public API
- `String requestBody(AiGenerationRequest request) -> JSON body`  
- `String endpoint() -> full URL`  
- `@Nullable String apiKey() -> trimmed key or null`  
- `@Nullable String apiKeyFor(String endpoint) -> key or null`  
- `static boolean isLoopbackHost(String host) -> true if loopback`  
- `static boolean isSuccess(int status) -> true if 2xx`  
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
- `IllegalArgumentException` in `endpoint()` if baseUrl not http(s).
- `IOException` in `apiKeyFor()` if plain http to non‑loopback host or URL parsing fails.
- `IOException` in `requestBody()` if JSON serialization fails.

#### Concurrency
- No mutable shared state beyond final fields; thread‑safe. No synchronization or coroutines.
