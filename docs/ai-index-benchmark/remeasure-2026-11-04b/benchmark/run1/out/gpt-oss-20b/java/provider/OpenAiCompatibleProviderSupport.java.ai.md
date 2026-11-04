### OpenAiCompatibleProviderSupport.java
- H: 1.0
- C: 92F0B02B
- D: 2026-11-04T11:46:43Z
- T: 2026-11-04T11:53:41Z
- G: 
- A: 0.0.0
- X: file
- P: 0B812A3C
---
> Builds the request payload, endpoint, API‑key handling, and response parsing for an OpenAI‑compatible chat‑completion provider.

#### Purpose
- Construct JSON body for chat‑completion requests.
- Resolve endpoint URL and validate scheme.
- Retrieve and secure API key.
- Interpret HTTP status and error bodies.
- Compute generation timings from response.

#### Type
- `final class OpenAiCompatibleProviderSupport` (class, final)
  - Extends `Object`.
  - Annotations: `@ToString`.
  - Fields:
    - `static final String CHAT_COMPLETIONS_PATH` – endpoint suffix.
    - `static final int ERROR_BODY_EXCERPT` – error body length.
    - `private static final int HTTP_OK_FIRST` – 200.
    - `private static final int HTTP_OK_END` – 300.
    - `static final String HTTP_SCHEME` – `"http://"`
    - `static final String HTTPS_SCHEME` – `"https://"`
    - `private static final String LOOPBACK_HOST_NAME` – `"localhost"`
    - `private static final Pattern IPV4_LOOPBACK` – regex for `127.*.*.*`
    - `private static final String IPV6_LOOPBACK` – `"[::1]"`
    - `private static final String ELLIPSIS` – `"..."`
    - `private static final ObjectMapper MAPPER` – Jackson mapper.
    - `private final LlamaCppJniConfig config` – model config.
    - `private final AiPromptSupport promptSupport` – prompt definitions (`@ToString.Exclude`).
    - `private final Function<String, @Nullable String> environment` – env lookup (`@ToString.Exclude`).
    - `private final Java8CompatibilityHelper compatibilityHelper` – helper for blank checks.

#### Input
- Constructor parameters: `LlamaCppJniConfig config`, `AiPromptSupport promptSupport`, `Function<String, @Nullable String> environment`.
- `requestBody(AiGenerationRequest)` – request object.
- `endpoint()` – uses `config.baseUrl()`.
- `apiKey()` – uses `config.apiKeyEnv()` and `environment`.
- `apiKeyFor(String endpoint)` – uses `apiKey()` and `endpoint`.
- `isLoopbackHost(String host)` – host string.
- `isSuccess(int status)` – status code.
- `errorMessage(String endpoint, int status, String body)` – endpoint, status, body.
- `timings(String text, ChatResponse response, double seconds)` – text, response, seconds.

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
- **requestBody**: builds `ObjectNode` with `model`, `messages`, `max_tokens`, `temperature`, `top_p`, optional `top_k`, `min_p`, `repetition_penalty`, optional `seed`, optional `stop` array, optional `reasoning.effort`, `stream=false`; serialises with `MAPPER`.
- **endpoint**: trims `config.baseUrl()`, validates `http://` or `https://`, removes trailing slashes, appends `CHAT_COMPLETIONS_PATH`.
- **apiKey**: if `config.apiKeyEnv()` blank → `null`; else lookup via `environment`; if null or blank → `null`; else return trimmed key.
- **apiKeyFor**: if key absent or endpoint not `http://` → return key; else parse host from `endpoint`; if not loopback → throw `IOException`; else return key.
- **isLoopbackHost**: checks host equals `"localhost"` or matches IPv4 loopback regex or equals `"[::1]"`.
- **isSuccess**: status in [200,300).
- **errorMessage**: truncates body to `ERROR_BODY_EXCERPT` + ellipsis if longer; formats `"endpoint answered HTTP status: excerpt"`.
- **timings**: extracts `usage` from `ChatResponse`; computes `completionTokens`, `decodeRate = completionTokens / seconds` (or 0 if seconds ≤0); constructs `AiGenerationTimings(text, promptTokens, cachedTokens, 0.0, completionTokens, decodeRate)`.

#### Public API
- `String requestBody(AiGenerationRequest request) -> String` – build request JSON.
- `String endpoint() -> String` – compute endpoint URL.
- `@Nullable String apiKey() -> String` – get API key.
- `@Nullable String apiKeyFor(String endpoint) -> String` – secure key for endpoint.
- `static boolean isLoopbackHost(String host) -> boolean` – host check.
- `static boolean isSuccess(int status) -> boolean` – status check.
- `static String errorMessage(String endpoint, int status, String body) -> String` – error formatting.
- `static AiGenerationTimings timings(String text, ChatResponse response, double seconds) -> AiGenerationTimings` – compute timings.

#### Dependencies
- `com.fasterxml.jackson.databind.ObjectMapper`, `ArrayNode`, `ObjectNode`.
- `java.io.IOException`, `java.net.URL`.
- `java.util.List`, `java.util.Objects`, `java.util.function.Function`, `java.util.regex.Pattern`.
- `lombok.ToString`, `lombok.ToString.Exclude`.
- `net.ladenthin.llama.value.ChatResponse`, `net.ladenthin.llama.value.Usage`.
- `net.ladenthin.srcmorph.document.AiGenerationRequest`.
- `net.ladenthin.srcmorph.prompt.AiPromptSupport`.
- `net.ladenthin.srcmorph.support.Java8CompatibilityHelper`.
- `org.jspecify.annotations.Nullable`.

#### Exceptions / Errors
- `IllegalArgumentException` in `endpoint()` if baseUrl not http(s).
- `IOException` in `apiKeyFor()` if key present and endpoint is plain http to non‑loopback host or URL parsing fails.
- `IOException` in `requestBody()` if JSON serialization fails.

#### Concurrency
- No explicit synchronization; all fields are immutable after construction. Thread‑safe by design.
