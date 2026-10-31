### OpenAiCompatibleProviderSupport.java
- H: 1.0
- C: 92F0B02B
- D: 2026-10-31T14:30:30Z
- T: 2026-10-31T14:32:05Z
- G: 
- A: 0.0.0
- X: file
---
> Builds OpenAI-compatible chat-completion requests and validates endpoints, API-key transport, HTTP results, and generation timings.

#### Purpose
- Supplies the network-independent request and response support for OpenAI-compatible AI generation.
- Formats chat-completion JSON, protects API keys from cleartext remote transport, and converts responses into generation timings.

#### Type
- `final class OpenAiCompatibleProviderSupport` — final package-private class.
- Annotated `@ToString`; excludes `promptSupport` and `environment` from generated string output.
- Holds per-model configuration, prompt support, environment lookup, and a Java 8 compatibility helper.

#### Input
- Constructor: `LlamaCppJniConfig`, `AiPromptSupport`, and environment-variable lookup function.
- `requestBody`: `AiGenerationRequest`; reads prompt key, source file, and source text, plus model and sampling settings from config.
- `endpoint`, `apiKey`: base URL and API-key environment-variable name from config.
- `apiKeyFor`: endpoint URL; `environment` supplies the configured key.
- `isLoopbackHost`: URL host string.
- `isSuccess`: HTTP status integer.
- `errorMessage`: endpoint, status, and error-body string.
- `timings`: completion text, `ChatResponse`, and elapsed seconds.

#### Output
- `requestBody` returns serialized JSON with system/user messages and configured generation options.
- `endpoint` returns the normalized chat-completions URL.
- `apiKey` and `apiKeyFor` return a trimmed key or `null`.
- `isLoopbackHost` and `isSuccess` return booleans.
- `errorMessage` returns an endpoint/status/body-excerpt message.
- `timings` returns `AiGenerationTimings` with token counts and computed decode rate.
- No network calls or external resource writes.

#### Core logic
- Creates two chat messages: system prompt from `promptKey`, then user message from source file and text.
- Adds `max_tokens`, `temperature`, `top_p`, `repetition_penalty`, and `stream: false`; conditionally includes nonblank model/reasoning effort, positive `top_k`/`min_p`, nonnegative seed, and nonempty stop strings.
- Trims `baseUrl`, accepts only `http://` or `https://`, removes trailing slashes, and appends `/chat/completions`.
- Resolves the API key only when `apiKeyEnv` and its environment value are nonblank; trims the returned key.
- Allows a configured key over HTTPS or plain HTTP to exact lowercase `localhost`, matching IPv4 `127.x.x.x`, or bracketed `[::1]`; rejects other plain-HTTP hosts.
- Treats HTTP statuses 200–299 as success; truncates error bodies beyond 500 characters and appends `...`.
- Computes decode rate as completion tokens divided by positive elapsed seconds; uses zero otherwise. Sets prefill rate to zero and includes prompt, cached, and completion token counts.

#### Public API
- `OpenAiCompatibleProviderSupport(config, promptSupport, environment)` -> constructs model support (package-private).
- `requestBody(request)` -> serializes chat-completion request (package-private).
- `endpoint()` -> builds chat-completions URL (package-private).
- `apiKey()` -> resolves configured environment key (package-private).
- `apiKeyFor(endpoint)` -> validates key transport and returns key (package-private).
- `isLoopbackHost(host)` -> checks recognized loopback literals (package-private static).
- `isSuccess(status)` -> checks 2xx status (package-private static).
- `errorMessage(endpoint, status, body)` -> formats failure message (package-private static).
- `timings(text, response, seconds)` -> calculates generation timings (package-private static).

#### Dependencies
- `LlamaCppJniConfig`, `AiGenerationRequest`, `AiPromptSupport`, `AiGenerationTimings`.
- `ChatResponse`, `Usage`, `Java8CompatibilityHelper`.
- Jackson `ObjectMapper`, `ObjectNode`, `ArrayNode`.
- Java `IOException`, `URL`, `List`, `Objects`, `Function`, `Pattern`.
- Lombok `@ToString`; JSpecify `@Nullable`.

#### Exceptions / Errors
- Constructor rejects null config, prompt support, or environment function via `Objects.requireNonNull`.
- `requestBody` propagates `IOException` from JSON serialization.
- `endpoint` throws `IllegalArgumentException` when the trimmed base URL lacks an HTTP(S) prefix.
- `apiKeyFor` propagates URL parsing `IOException`; throws `IOException` when a configured key would be sent over plain HTTP to a non-loopback host.
- `errorMessage` assumes a non-null body.

#### Concurrency
- No synchronization or mutable shared state; configuration and collaborators are held in final fields.
- Uses a static `ObjectMapper`; thread-safety is not specified in this file.
