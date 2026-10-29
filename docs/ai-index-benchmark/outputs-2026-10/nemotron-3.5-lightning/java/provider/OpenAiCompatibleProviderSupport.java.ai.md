### OpenAiCompatibleProviderSupport.java
- H: 1.0
- C: 92F0B02B
- D: 2026-10-28T22:56:50Z
- T: 2026-10-28T23:04:45Z
- G: 
- A: 0.0.0
- X: file
---
> Provides the non-network infrastructure for OpenAI-compatible AI model interactions: request bodies, endpoint resolution, API key lookup, status checks, and call timings

#### Purpose
- Final class providing non-network infrastructure for OpenAI-compatible AI model interactions
- Constructs JSON request bodies, resolves endpoints, manages API keys, validates HTTP statuses, and extracts call timings

#### Type
- Kind: class
- Modifiers: final
- Annotations: `@ToString` on class; `@ToString.Exclude` on `promptSupport` and `environment` fields
- No extended classes or implemented interfaces
- No generic type parameters

#### Input
- Constructor accepts `LlamaCppJniConfig config`, `AiPromptSupport promptSupport`, `Function<String, @Nullable String> environment`
- Method parameters: `AiGenerationRequest request` (in `requestBody`), `String endpoint` (in `apiKeyFor`), `String text`, `ChatResponse response`, `double seconds` (in `timings`)
- Consumed fields: `config`, `promptSupport`, `environment`, `compatibilityHelper`

#### Output
- `requestBody` returns `String` (serialized JSON request body)
- `endpoint` returns `String` (chat-completions URL)
- `apiKey` returns `@Nullable String` (trimmed API key or null)
- `apiKeyFor` returns `@Nullable String` (key validated for loopback transmission)
- `isLoopbackHost` returns `boolean` (matches localhost, 127.x.x.x, [::1])
- `isSuccess` returns `boolean` (status in 200-299)
- `errorMessage` returns `String` (endpoint, status, truncated error body)
- `timings` returns `AiGenerationTimings` (prompt/cached/completion tokens and decode rate)
- Side effects: JSON serialization via `ObjectMapper`, exception throwing, timing object construction

#### Core logic
- Builds JSON request body with model, system/user prompts, max_tokens, temperature, top_p, top_k (when >0), min_p (when >0.0f), repetition_penalty, seed (when >=0), stop strings, reasoning.effort (when non-blank), and stream false
- Resolves endpoint by trimming baseUrl, validating http(s) scheme, stripping trailing slashes, and appending CHAT_COMPLETIONS_PATH
- Looks up API key via environment function; permits transmission only over https or to loopback hosts (localhost, 127.x.x.x, [::1]), throwing IOException otherwise
- Validates HTTP status success as 200-299, and constructs error messages combining endpoint, status, and beginning of error body (excerpt length 500 chars with ellipsis)
- Extracts token usage from chat response: prompt tokens, cached tokens (fixed at 0), completion tokens, and computes decode rate as completion tokens divided by wall-clock seconds (zero if seconds non-positive)

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
- `net.ladenthin.llama.value.ChatResponse`
- `net.ladenthin.llama.value.Usage`
- `net.ladenthin.srcmorph.document.AiGenerationRequest`
- `net.ladenthin.srcmorph.prompt.AiPromptSupport`
- `net.ladenthin.srcmorph.support.Java8CompatibilityHelper`
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- `IOException` thrown by `requestBody` and `apiKeyFor`
- `IllegalArgumentException` thrown by `endpoint` when baseUrl is not http(s)
- `apiKey` returns null when environment variable is unset, blank, or function returns null/blank
- API key transmission blocked for non-loopback hosts; IOException thrown if key would be sent in cleartext over plain http
- Error messages constructed for non-2xx statuses, including endpoint, status, and truncated error body (max 500 chars with ellipsis)
- Null-handling via `@Nullable` annotations and `Objects.requireNonNull` in constructor for `config`, `promptSupport`, `environment`
