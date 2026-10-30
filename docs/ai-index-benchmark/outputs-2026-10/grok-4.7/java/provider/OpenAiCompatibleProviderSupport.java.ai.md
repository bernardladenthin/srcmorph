### OpenAiCompatibleProviderSupport.java
- H: 1.0
- C: 92F0B02B
- D: 2026-10-30T00:29:58Z
- T: 2026-10-30T00:42:34Z
- G: 
- A: 0.0.0
- X: file
---
I will index the provided Java source file and produce a structured markdown summary.

The file is a final class that builds OpenAI-compatible request bodies and endpoints. It handles API key checks, error messages, and generation timings without network calls.

The class is package-private and final. No public or protected members exist. The summary will detail inputs, outputs, and core logic.

The Public API section will be omitted, as no public or protected members exist. All fields are private or package-private static final constants.

The summary will list imports and referenced types under Dependencies. IOException, IllegalArgumentException, and Objects.requireNonNull are the key exceptions. Concurrency is noted.

The shared ObjectMapper is thread-safe after configuration. The class uses immutable fields and no synchronization. I am counting constants and instance fields precisely.

The class contains one constructor and eight package-private methods. These are listed in the core logic section.

The Type section will note the package-private final class. The Dependencies list includes Jackson ObjectMapper, ArrayNode, ObjectNode, java.io.IOException, java.net.URL, java.util.List, java.util.Objects, java.util.function.Function, java.util.regex.Pattern, and lombok.ToString.

The Dependencies list also includes net.ladenthin.llama.value.ChatResponse, Usage, AiGenerationRequest, AiPromptSupport, Java8CompatibilityHelper, and org.jspecify.annotations.Nullable. Several referenced types are not imported directly: OpenAiCompatibleAiGenerationProvider, LlamaCppJniProviderSupport, LlamaCppJniConfig, AiGenerationTimings, and AiGenerationConfig.

The requestBody method constructs a JSON ObjectNode using the shared ObjectMapper. It conditionally includes model, system and user messages from promptSupport, and fixed fields such as max_tokens, temperature, top_p, repetition_penalty, and stream=false. Optional parameters top_k and min_p are added only when their configuration values exceed zero.

The requestBody method also serializes seed, stop strings, and reasoning effort when configured. The endpoint method validates the base URL starts with http or https, strips trailing slashes, and appends /chat/completions. The apiKey method returns null for blank environment variables or trims the result. The apiKeyFor method rejects cleartext requests to loopback hosts with an IOException.

isLoopbackHost matches localhost, 127.x.x.x, and [::1] exactly. isSuccess returns true for HTTP status codes between 200 and 299. errorMessage truncates the error body to 500 characters and appends an ellipsis if longer. timings computes decodeRate as completion tokens divided by seconds, with prefill rate fixed at zero. The constructor throws NullPointerException via requireNonNull, requestBody throws IOException, endpoint throws IllegalArgumentException, and apiKeyFor throws IOException.> Builds the OpenAI-compatible chat-completions request, endpoint, API-key cleartext guard, HTTP error excerpt, and generation timings without opening a socket.

#### Purpose
- Package-private support split from `OpenAiCompatibleAiGenerationProvider`: everything that needs no network and is on the 100% PIT gate.
- Turns `LlamaCppJniConfig`, `AiPromptSupport`, and an environment lookup into a chat-completions JSON body, URL, optional API key, success check, error text, and `AiGenerationTimings`.

#### Type
- `final class` `OpenAiCompatibleProviderSupport` (package-private); no superclass or interfaces.
- `@ToString`; `@ToString.Exclude` on `promptSupport` and `environment`.
- `@Nullable` on the environment function result and on `apiKey` / `apiKeyFor`.

#### Input
- Constructor: `LlamaCppJniConfig config`, `AiPromptSupport promptSupport`, `Function<String, @Nullable String> environment` (all required).
- `requestBody`: `AiGenerationRequest` (`promptKey`, `sourceFile`, `sourceText`).
- Config consumed: `remoteModel`, `maxOutputTokens`, `temperature`, `topP`, `topK`, `minP`, `repeatPenalty`, `seed`, `stopStrings`, `reasoningEffort`, `baseUrl`, `apiKeyEnv`.
- `apiKeyFor`: endpoint string from `endpoint()`.
- `errorMessage`: endpoint, HTTP status, error body.
- `timings`: completion text, `ChatResponse` (`Usage`), wall-clock seconds.
- Static `ObjectMapper MAPPER`; `Java8CompatibilityHelper` for blank checks.

#### Output
- `requestBody` -> JSON `String` (`stream` always `false`).
- `endpoint` -> `baseUrl` trimmed, trailing `/` stripped, plus `/chat/completions`.
- `apiKey` / `apiKeyFor` -> trimmed key or `null`.
- `isLoopbackHost` / `isSuccess` -> `boolean`.
- `errorMessage` -> endpoint, status, and body excerpt (at most `ERROR_BODY_EXCERPT` 500 chars, then `...`).
- `timings` -> `AiGenerationTimings` (text, prompt tokens, cached tokens, prefill rate `0.0d`, completion tokens, decode rate).
- No instance mutation after construction.

#### Core logic
- `requestBody` builds an `ObjectNode`: optional non-blank `model`; `messages` with system `promptSupport.systemPrompt(promptKey)` and user `promptSupport.userMessage(sourceFile, sourceText)`; always `max_tokens`, `temperature`, `top_p`, `repetition_penalty`; `top_k` only if `topK() > 0`; `min_p` only if `minP() > 0.0f`; `seed` only if `>= 0`; `stop` array only if `stopStrings` non-empty; `reasoning.effort` only if `reasoningEffort` non-blank; serialize with `MAPPER`.
- `endpoint` rejects a `baseUrl` that does not start with `http://` or `https://`, then appends `CHAT_COMPLETIONS_PATH`.
- `apiKey` returns `null` when `apiKeyEnv` is blank, unset, or blank; otherwise the trimmed environment value.
- `apiKeyFor` returns that key immediately when absent or when the endpoint is not plain `http://`; on plain HTTP it parses `URL.getHost()` and sends the key only to loopback, else throws before any byte is sent.
- `isLoopbackHost` is true only for exact `localhost`, a full `127.x.x.x` match (`IPV4_LOOPBACK`), or `[::1]` (`IPV6_LOOPBACK`). Not a prefix, so `127.evil.com` is not loopback.
- `isSuccess` is HTTP status in `[200, 300)`.
- `timings` casts `Usage` prompt, cached, and completion tokens to `int`; decode rate is completion tokens over seconds when `seconds > 0.0d`, else `0.0d`; prefill rate is always `0.0d` (unknown).

#### Dependencies
- `ObjectMapper`, `ArrayNode`, `ObjectNode`, `IOException`, `URL`, `List`, `Objects`, `Function`, `Pattern`, `ToString`, `Nullable`.
- `ChatResponse`, `Usage`, `AiGenerationRequest`, `AiPromptSupport`, `Java8CompatibilityHelper`, `LlamaCppJniConfig`, `AiGenerationTimings`.
- Javadoc-only: `OpenAiCompatibleAiGenerationProvider`, `LlamaCppJniProviderSupport`, `AiGenerationConfig`.

#### Exceptions / Errors
- Constructor: `NullPointerException` via `Objects.requireNonNull` on `config`, `promptSupport`, `environment`.
- `requestBody`: `IOException` if JSON serialization fails.
- `endpoint`: `IllegalArgumentException` when `baseUrl` is not an `http(s)` URL.
- `apiKeyFor`: `IOException` when a key would be sent in cleartext to a non-loopback host; `URL` parse failures surface as `IOException`.
- `apiKey` treats null, unset, and blank as no key (no throw).

#### Concurrency
- Instance fields are `final`; methods do not mutate them.
- Static `MAPPER` and `IPV4_LOOPBACK` are shared; no locks or synchronization in this class.
