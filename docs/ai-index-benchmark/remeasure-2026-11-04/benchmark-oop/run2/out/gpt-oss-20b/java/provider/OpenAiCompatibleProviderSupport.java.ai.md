### OpenAiCompatibleProviderSupport.java
- H: 1.0
- C: 92F0B02B
- D: 2026-11-04T07:35:21Z
- T: 2026-11-04T07:40:46Z
- G: 
- A: 0.0.0
- X: file
- P: 1879E12B
---
> Builds JSON request bodies, validates endpoints, and handles API key security for OpenAI‑compatible chat‑completion services.

#### Purpose
- Constructs chat‑completion request payloads with sampling knobs.
- Validates and normalises endpoint URLs.
- Secures API key transmission over HTTP.
- Provides helper utilities for status checks and error messaging.

#### Type
- `final class OpenAiCompatibleProviderSupport` (class, final)  
  - Extends `Object`.  
  - Annotations: `@ToString`.  
  - Fields:  
    - `private final LlamaCppJniConfig config`  
    - `@ToString.Exclude private final AiPromptSupport promptSupport`  
    - `@ToString.Exclude private final Function<String, @Nullable String> environment`  
    - `private final Java8CompatibilityHelper compatibilityHelper`  
  - Static fields:  
    - `static final String CHAT_COMPLETIONS_PATH`  
    - `static final int ERROR_BODY_EXCERPT`  
    - `private static final int HTTP_OK_FIRST`  
    - `private static final int HTTP_OK_END`  
    - `static final String HTTP_SCHEME`  
    - `static final String HTTPS_SCHEME`  
    - `private static final String LOOPBACK_HOST_NAME`  
    - `private static final Pattern IPV4_LOOPBACK`  
    - `private static final String IPV6_LOOPBACK`  
    - `private static final String ELLIPSIS`  
    - `private static final ObjectMapper MAPPER`

#### Input
- Constructor parameters: `LlamaCppJniConfig config`, `AiPromptSupport promptSupport`, `Function<String, @Nullable String> environment`.  
- `requestBody(AiGenerationRequest request)` consumes request data, config sampling knobs, and prompt definitions.  
- `endpoint()` uses `config.baseUrl()`.  
- `apiKey()` reads `config.apiKeyEnv()` and environment lookup.  
- `apiKeyFor(String endpoint)` uses `apiKey()` and parses `endpoint`.  
- `isLoopbackHost(String host)` receives host string.  
- `isSuccess(int status)` receives HTTP status.  
- `errorMessage(String endpoint, int status, String body)` receives endpoint, status, and body.  
- `timings(String text, ChatResponse response, double seconds)` consumes parsed text, response usage, and elapsed time.

#### Output
- `requestBody` returns JSON string.  
- `endpoint` returns full chat‑completions URL.  
- `apiKey` returns trimmed key or `null`.  
- `apiKeyFor` returns key or `null`; throws `IOException` on insecure HTTP.  
- `isLoopbackHost` returns boolean.  
- `isSuccess` returns boolean.  
- `errorMessage` returns formatted message string.  
- `timings` returns `AiGenerationTimings` instance.

#### Core logic
- **requestBody**: builds `ObjectNode` with model, messages, token limits, temperature, top‑p, optional top‑k, min‑p, repetition penalty, seed, stop strings, reasoning effort, and disables streaming; serialises to JSON.  
- **endpoint**: trims `baseUrl`, ensures it starts with `http://` or `https://`, removes trailing slashes, appends `/chat/completions`.  
- **apiKey**: retrieves env var, trims, returns `null` if unset or blank.  
- **apiKeyFor**: if key present and endpoint uses plain `http`, parses host; if host not loopback, throws `IOException`; otherwise returns key.  
- **isLoopbackHost**: checks host against `localhost`, IPv4 loopback regex, or `[::1]`.  
- **isSuccess**: checks status in 200–299 range.  
- **errorMessage**: truncates body to 500 chars, appends ellipsis if truncated, formats message.  
- **timings**: extracts token counts from `ChatResponse.usage`, computes decode rate, constructs `AiGenerationTimings`.

#### Public API
- `String requestBody(AiGenerationRequest request) -> JSON body`  
- `String endpoint() -> full URL`  
- `@Nullable String apiKey() -> key or null`  
- `@Nullable String apiKeyFor(String endpoint) -> key or null`  
- `static boolean isLoopbackHost(String host) -> true if loopback`  
- `static boolean isSuccess(int status) -> true if 2xx`  
- `static String errorMessage(String endpoint, int status, String body) -> message`  
- `static AiGenerationTimings timings(String text, ChatResponse response, double seconds) -> timings`

#### Dependencies
- `com.fasterxml.jackson.databind.ObjectMapper`, `ArrayNode`, `ObjectNode`  
- `java.io.IOException`, `java.net.URL`, `java.util.List`, `java.util.Objects`, `java.util.function.Function`, `java.util.regex.Pattern`  
- `lombok.ToString`, `org.jspecify.annotations.Nullable`  
- `net.ladenthin.llama.value.ChatResponse`, `net.ladenthin.llama.value.Usage`  
- `net.ladenthin.srcmorph.document.AiGenerationRequest`  
- `net.ladenthin.srcmorph.prompt.AiPromptSupport`  
- `net.ladenthin.srcmorph.support.Java8CompatibilityHelper`  
- `net.ladenthin.srcmorph.provider.LlamaCppJniConfig`  
- `net.ladenthin.srcmorph.provider.AiGenerationTimings`

#### Exceptions / Errors
- `IllegalArgumentException` in `endpoint()` if `baseUrl` not HTTP(S).  
- `IOException` in `apiKeyFor()` if plain HTTP to non‑loopback host or URL parsing fails.  
- `IOException` in `requestBody()` if JSON serialization fails.

#### Concurrency
- All fields are immutable after construction; no mutable shared state.  
- Thread‑safe by design.
