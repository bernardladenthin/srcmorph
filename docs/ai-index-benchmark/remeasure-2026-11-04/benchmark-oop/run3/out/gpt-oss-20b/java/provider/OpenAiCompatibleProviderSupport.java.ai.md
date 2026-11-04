### OpenAiCompatibleProviderSupport.java
- H: 1.0
- C: 92F0B02B
- D: 2026-11-04T07:50:25Z
- T: 2026-11-04T07:55:30Z
- G: 
- A: 0.0.0
- X: file
- P: 1879E12B
---
> Builds the HTTP request body, endpoint, and key handling for an OpenAI‑compatible chat‑completion provider, and interprets responses and errors.

#### Purpose
- Constructs JSON bodies for chat‑completion requests.
- Validates and builds endpoint URLs.
- Retrieves and secures API keys.
- Interprets HTTP status and error bodies.
- Calculates generation timings.

#### Type
`final class OpenAiCompatibleProviderSupport` – class  
- Extends `Object`.  
- Modifiers: `final`.  
- Fields:  
  - `private final LlamaCppJniConfig config` – model configuration.  
  - `@ToString.Exclude private final AiPromptSupport promptSupport` – prompt definitions.  
  - `@ToString.Exclude private final Function<String, @Nullable String> environment` – env lookup.  
  - `private final Java8CompatibilityHelper compatibilityHelper` – null/blank checks.  
  - `private static final ObjectMapper MAPPER` – JSON serializer.  
- Static constants: `CHAT_COMPLETIONS_PATH`, `ERROR_BODY_EXCERPT`, `HTTP_OK_FIRST`, `HTTP_OK_END`, `HTTP_SCHEME`, `HTTPS_SCHEME`, `LOOPBACK_HOST_NAME`, `IPV4_LOOPBACK`, `IPV6_LOOPBACK`, `ELLIPSIS`.

#### Input
- Constructor parameters: `LlamaCppJniConfig config`, `AiPromptSupport promptSupport`, `Function<String, @Nullable String> environment`.  
- `requestBody(AiGenerationRequest request)` consumes `AiGenerationRequest` fields: `promptKey()`, `sourceFile()`, `sourceText()`.  
- `endpoint()` uses `config.baseUrl()`.  
- `apiKey()` uses `config.apiKeyEnv()` and `environment`.  
- `apiKeyFor(String endpoint)` uses `endpoint` and `apiKey()`.  
- `isLoopbackHost(String host)` receives host string.  
- `isSuccess(int status)` receives status code.  
- `errorMessage(String endpoint, int status, String body)` receives endpoint, status, body.  
- `timings(String text, ChatResponse response, double seconds)` receives parsed text, `ChatResponse`, and elapsed seconds.

#### Output
- `requestBody` returns JSON string.  
- `endpoint` returns URL string.  
- `apiKey` returns trimmed key or `null`.  
- `apiKeyFor` returns key or `null`; throws `IOException` on insecure HTTP to non‑loopback.  
- `isLoopbackHost` returns boolean.  
- `isSuccess` returns boolean.  
- `errorMessage` returns formatted message string.  
- `timings` returns `AiGenerationTimings` with token counts and decode rate.

#### Core logic
- **requestBody**: builds `ObjectNode` with model, messages, sampling knobs, stop strings, reasoning effort, and `stream=false`; serialises to JSON.  
- **endpoint**: trims `baseUrl`, ensures `http://` or `https://`, removes trailing slashes, appends `/chat/completions`.  
- **apiKey**: retrieves env var, trims, returns `null` if unset or blank.  
- **apiKeyFor**: if key present and endpoint uses plain `http`, parses host; if host not loopback, throws `IOException`; otherwise returns key.  
- **isLoopbackHost**: checks host against `localhost`, IPv4 loopback regex, or `[::1]`.  
- **isSuccess**: status in [200,300).  
- **errorMessage**: truncates body to 500 chars, appends ellipsis, formats message.  
- **timings**: extracts token counts from `ChatResponse.usage`, computes decode rate, constructs `AiGenerationTimings`.

#### Public API
- `String requestBody(AiGenerationRequest request) -> JSON body`  
- `String endpoint() -> URL`  
- `String apiKey() -> key or null`  
- `String apiKeyFor(String endpoint) -> key or null`  
- `boolean isLoopbackHost(String host) -> true if loopback`  
- `boolean isSuccess(int status) -> true if 2xx`  
- `String errorMessage(String endpoint, int status, String body) -> message`  
- `AiGenerationTimings timings(String text, ChatResponse response, double seconds) -> timings`

#### Dependencies
- `com.fasterxml.jackson.databind.ObjectMapper`, `ArrayNode`, `ObjectNode`  
- `java.io.IOException`, `java.net.URL`, `java.util.List`, `java.util.Objects`, `java.util.function.Function`, `java.util.regex.Pattern`  
- `lombok.ToString`  
- `net.ladenthin.llama.value.ChatResponse`, `net.ladenthin.llama.value.Usage`  
- `net.ladenthin.srcmorph.document.AiGenerationRequest`  
- `net.ladenthin.srcmorph.prompt.AiPromptSupport`  
- `net.ladenthin.srcmorph.support.Java8CompatibilityHelper`  
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- `IllegalArgumentException` in `endpoint()` if `baseUrl` not HTTP(S).  
- `IOException` in `apiKeyFor()` if plain HTTP to non‑loopback host or URL parsing fails.  
- `IOException` in `requestBody()` if JSON serialization fails.

#### Concurrency
- No mutable shared state beyond final fields; thread‑safe.  
- `MAPPER` is thread‑safe.
