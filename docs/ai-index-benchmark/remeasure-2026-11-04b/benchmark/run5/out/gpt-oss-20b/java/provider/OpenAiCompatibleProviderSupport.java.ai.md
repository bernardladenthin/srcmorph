### OpenAiCompatibleProviderSupport.java
- H: 1.0
- C: 92F0B02B
- D: 2026-11-04T12:53:23Z
- T: 2026-11-04T12:54:21Z
- G: 
- A: 0.0.0
- X: file
- P: 0B812A3C
---
> Builds the request body, endpoint, API key handling, and error/timing logic for an OpenAI‑compatible chat‑completion provider.

#### Purpose
- Constructs JSON request bodies for chat‑completion calls.
- Validates and normalises endpoint URLs.
- Retrieves and secures API keys.
- Generates error messages and timing metrics.

#### Type
- `final class OpenAiCompatibleProviderSupport` (class, final)  
  extends `Object`  
  annotations: `@ToString`  
  fields:  
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
- `isSuccess(int status)` reads `status`.
- `errorMessage(String endpoint, int status, String body)` reads all three.
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
- `requestBody`: builds `ObjectNode` with model, messages, sampling knobs, optional fields (`top_k`, `min_p`, `seed`, `stop`, `reasoning.effort`), sets `stream=false`, serialises with `MAPPER`.
- `endpoint`: trims `config.baseUrl()`, ensures it starts with `http://` or `https://`, removes trailing slashes, appends `CHAT_COMPLETIONS_PATH`.
- `apiKey`: returns trimmed environment variable value if `config.apiKeyEnv()` is non‑blank; otherwise `null`.
- `apiKeyFor`: if key exists and endpoint starts with `http://`, parses host via `URL`; if host not loopback (`isLoopbackHost`), throws `IOException`; otherwise returns key.
- `isLoopbackHost`: checks host equals `localhost`, matches IPv4 loopback regex, or equals `[::1]`.
- `isSuccess`: checks status in [200,300).
- `errorMessage`: truncates body to `ERROR_BODY_EXCERPT`, appends ellipsis if truncated, formats message with endpoint and status.
- `timings`: extracts `completionTokens`, `promptTokens`, `cachedTokens` from `Usage`; computes `decodeRate = completionTokens / seconds` if seconds>0; constructs `AiGenerationTimings` with prefillRate=0.0.

#### Public API
- `String requestBody(AiGenerationRequest request) -> String` builds request body.  
- `String endpoint() -> String` returns chat‑completions URL.  
- `@Nullable String apiKey() -> String` retrieves API key.  
- `@Nullable String apiKeyFor(String endpoint) -> String` secures key for endpoint.  
- `static boolean isLoopbackHost(String host) -> boolean` checks loopback.  
- `static boolean isSuccess(int status) -> boolean` checks 2xx.  
- `static String errorMessage(String endpoint, int status, String body) -> String` formats error.  
- `static AiGenerationTimings timings(String text, ChatResponse response, double seconds) -> AiGenerationTimings` computes timings.

#### Dependencies
- `com.fasterxml.jackson.databind.ObjectMapper`, `ArrayNode`, `ObjectNode`
- `java.io.IOException`, `java.net.URL`, `java.util.List`, `java.util.Objects`, `java.util.function.Function`, `java.util.regex.Pattern`
- `lombok.ToString`, `lombok.ToString.Exclude`
- `net.ladenthin.llama.value.ChatResponse`, `net.ladenthin.llama.value.Usage`
- `net.ladenthin.srcmorph.document.AiGenerationRequest`
- `net.ladenthin.srcmorph.prompt.AiPromptSupport`
- `net.ladenthin.srcmorph.support.Java8CompatibilityHelper`
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- `requestBody` throws `IOException` if JSON serialization fails.
- `endpoint` throws `IllegalArgumentException` if baseUrl not http(s).
- `apiKeyFor` throws `IOException` if plain http to non‑loopback host or URL parsing fails.
- `errorMessage` no exceptions.

#### Concurrency
- All methods are stateless or use immutable fields; thread‑safe. No synchronization or mutable shared state.
