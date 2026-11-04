### OpenAiCompatibleProviderSupport.java
- H: 1.0
- C: 92F0B02B
- D: 2026-11-04T07:19:50Z
- T: 2026-11-04T07:25:53Z
- G: 
- A: 0.0.0
- X: file
- P: 1879E12B
---
> Builds the JSON request, endpoint, API key handling, and error/timing logic for an OpenAI‑compatible chat‑completion provider.

#### Purpose
- Constructs chat‑completion request bodies with sampling knobs.
- Validates and builds the endpoint URL.
- Retrieves and secures the API key.
- Provides utilities for success checks, error messages, and timing calculations.

#### Type
- `final class OpenAiCompatibleProviderSupport` (class, final)  
  - Extends `Object`  
  - Annotations: `@ToString`  
  - Fields:  
    - `static final String CHAT_COMPLETIONS_PATH` (String)  
    - `static final int ERROR_BODY_EXCERPT` (int)  
    - `private static final int HTTP_OK_FIRST` (int)  
    - `private static final int HTTP_OK_END` (int)  
    - `static final String HTTP_SCHEME` (String)  
    - `static final String HTTPS_SCHEME` (String)  
    - `private static final String LOOPBACK_HOST_NAME` (String)  
    - `private static final Pattern IPV4_LOOPBACK` (Pattern)  
    - `private static final String IPV6_LOOPBACK` (String)  
    - `private static final String ELLIPSIS` (String)  
    - `private static final ObjectMapper MAPPER` (ObjectMapper)  
    - `private final LlamaCppJniConfig config` (LlamaCppJniConfig)  
    - `@ToString.Exclude private final AiPromptSupport promptSupport` (AiPromptSupport)  
    - `@ToString.Exclude private final Function<String, @Nullable String> environment` (Function)  
    - `private final Java8CompatibilityHelper compatibilityHelper` (Java8CompatibilityHelper)

#### Input
- Constructor parameters: `LlamaCppJniConfig config`, `AiPromptSupport promptSupport`, `Function<String, @Nullable String> environment`.
- `requestBody(AiGenerationRequest request)` consumes `AiGenerationRequest` fields: `promptKey()`, `sourceFile()`, `sourceText()`.
- `endpoint()` consumes `config.baseUrl()`.
- `apiKey()` consumes `config.apiKeyEnv()`, `environment.apply(...)`.
- `apiKeyFor(String endpoint)` consumes `endpoint` and `apiKey()`.
- `isLoopbackHost(String host)` consumes `host`.
- `isSuccess(int status)` consumes `status`.
- `errorMessage(String endpoint, int status, String body)` consumes `endpoint`, `status`, `body`.
- `timings(String text, ChatResponse response, double seconds)` consumes `text`, `ChatResponse.getUsage()`, `seconds`.

#### Output
- `requestBody` returns JSON string.
- `endpoint` returns URL string.
- `apiKey` returns trimmed key or `null`.
- `apiKeyFor` returns key or `null`; throws `IOException` on insecure HTTP to non‑loopback.
- `isLoopbackHost` returns boolean.
- `isSuccess` returns boolean.
- `errorMessage` returns formatted message string.
- `timings` returns `AiGenerationTimings` instance with decoded rate.

#### Core logic
- Build JSON body with optional fields based on config values.
- Trim and validate base URL scheme; remove trailing slashes; append chat‑completions path.
- Resolve API key from environment; trim; return `null` if absent or blank.
- For plain HTTP endpoints, ensure host is loopback; otherwise throw `IOException`.
- Determine loopback hosts via exact match, IPv4 regex, or IPv6 literal.
- Check HTTP status in 200–299 range.
- Construct error message with body excerpt up to 500 chars.
- Compute decode rate: `completionTokens / seconds`; return `AiGenerationTimings`.

#### Public API
- `String requestBody(AiGenerationRequest request) -> request body`  
- `String endpoint() -> chat‑completions URL`  
- `@Nullable String apiKey() -> API key`  
- `@Nullable String apiKeyFor(String endpoint) -> API key or null`  
- `static boolean isLoopbackHost(String host) -> true if loopback`  
- `static boolean isSuccess(int status) -> true if 2xx`  
- `static String errorMessage(String endpoint, int status, String body) -> message`  
- `static AiGenerationTimings timings(String text, ChatResponse response, double seconds) -> timings`

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
- `net.ladenthin.srcmorph.support.Java8CompatibilityHelper`
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- `IllegalArgumentException` in `endpoint()` if base URL not HTTP(S).
- `IOException` in `apiKeyFor()` if plain HTTP to non‑loopback host or URL parsing fails.
- `IOException` in `requestBody()` if JSON serialization fails.

#### Concurrency
- No explicit synchronization; all fields are immutable after construction.  
- Thread‑safe usage assumed.
