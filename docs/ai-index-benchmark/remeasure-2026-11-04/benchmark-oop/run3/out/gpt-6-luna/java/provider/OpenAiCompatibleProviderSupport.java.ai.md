### OpenAiCompatibleProviderSupport.java
- H: 1.0
- C: 92F0B02B
- D: 2026-11-04T07:50:25Z
- T: 2026-11-04T07:51:39Z
- G: 
- A: 0.0.0
- X: file
- P: 1879E12B
---
> Builds OpenAI-compatible chat-completion requests and protects API keys from being sent over non-local plain HTTP.

#### Purpose
- `OpenAiCompatibleProviderSupport` prepares request JSON, endpoint and API-key handling, HTTP response checks, error messages, and generation timings for an OpenAI-compatible provider.
- Keeps provider support logic independent of network I/O.

#### Type
- `final class OpenAiCompatibleProviderSupport` — final class; package-private; annotated `@ToString`.

#### Input
- Constructor: `LlamaCppJniConfig config`, `AiPromptSupport promptSupport`, and `Function<String, @Nullable String> environment`.
- `requestBody(AiGenerationRequest request)` reads the prompt key, source file and text, plus model and sampling settings from `config`.
- `endpoint()` reads and trims `config.baseUrl()`.
- `apiKey()` reads `config.apiKeyEnv()` and looks up that environment variable.
- `apiKeyFor(String endpoint)` parses the endpoint host when a key is configured for plain HTTP.
- `errorMessage(String endpoint, int status, String body)` consumes the response body.
- `timings(String text, ChatResponse response, double seconds)` reads usage counts from `response`.

#### Output
- `requestBody` returns serialized JSON with system and user messages, sampling settings, and `"stream": false`.
- `endpoint` returns the base URL with trailing slashes removed and `/chat/completions` appended.
- `apiKey` and `apiKeyFor` return a trimmed key or `null`.
- `errorMessage` returns a message containing the endpoint, HTTP status, and up to 500 body characters.
- `timings` returns `AiGenerationTimings` with usage counts, zero prefill rate, and decode rate calculated from completion tokens and elapsed seconds.
- Static helpers return loopback-host, success-status, and error-message results.
- Holds final `config`, `promptSupport`, `environment`, and `compatibilityHelper` fields.

#### Core logic
- `requestBody` omits blank `remoteModel` and `reasoningEffort`; always includes `max_tokens`, `temperature`, `top_p`, `repetition_penalty`, two prompt messages, and `stream: false`.
- Adds `top_k` and `min_p` only when positive, `seed` only when non-negative, and `stop` only when stop strings are present.
- `endpoint` accepts only `http://` or `https://` prefixes, strips trailing slashes, then appends `/chat/completions`.
- `apiKey` returns `null` if the environment-variable name is blank, the lookup is unset, or the value is blank; otherwise returns the trimmed value.
- `apiKeyFor` allows a configured key over HTTPS or plain HTTP to an accepted loopback host; rejects other plain-HTTP destinations.
- `isLoopbackHost` recognizes exactly `localhost`, matching IPv4 `127.x.x.x` literals, and `[::1]`.
- `isSuccess` recognizes HTTP statuses from 200 through 299.
- `errorMessage` truncates bodies longer than 500 characters and appends `...`.
- `timings` casts prompt, cached, and completion token counts to `int`; decode rate is zero when elapsed seconds are not positive.

#### Dependencies
- `LlamaCppJniConfig`, `AiPromptSupport`, `AiGenerationRequest`, `AiGenerationTimings`
- `ChatResponse`, `Usage`
- Jackson `ObjectMapper`, `ObjectNode`, `ArrayNode`
- `Java8CompatibilityHelper`
- `Function`, `List`, `Objects`, `Pattern`, `URL`, `IOException`
- Lombok `@ToString`; JSpecify `@Nullable`

#### Exceptions / Errors
- Constructor uses `Objects.requireNonNull` for all three arguments.
- `requestBody` declares `IOException` for JSON serialization failure.
- `endpoint` throws `IllegalArgumentException` for a base URL without an HTTP(S) prefix.
- `apiKeyFor` declares `IOException` for URL parsing failure or a configured key sent to a non-loopback plain-HTTP host.
- `errorMessage` truncates oversized error bodies; it does not throw.

#### Concurrency
- No synchronization or concurrency controls; fields are final.
