### OpenAiCompatibleProviderSupport.java
- H: 1.0
- C: 92F0B02B
- D: 2026-11-04T07:27:53Z
- T: 2026-11-04T07:29:21Z
- G: 
- A: 0.0.0
- X: file
- P: 36159BF2
---
> OpenAI-compatible chat requests are built from source files and model settings, while API keys are protected from cleartext transmission to non-local servers and response metadata is converted into generation timings.

#### Purpose
- Prepares network-independent request data and policy checks for OpenAI-compatible model calls.
- Formats HTTP errors and derives token counts and decode rates from chat responses.

#### Type
- `final class OpenAiCompatibleProviderSupport` — package-private; no superclass or implemented interfaces.
- `@ToString`; `promptSupport` and `environment` are excluded from generated `toString`.
- Static Jackson `ObjectMapper`; instance fields hold `LlamaCppJniConfig`, prompt support, environment lookup, and Java 8 compatibility helper.

#### Input
- Constructor: `LlamaCppJniConfig`, `AiPromptSupport`, and environment-variable lookup `Function<String, @Nullable String>`.
- `requestBody`: `AiGenerationRequest`; reads prompt key, source file/text, and model/sampling settings from config.
- `endpoint`, `apiKey`: config base URL and API-key environment-variable name; `apiKey` invokes the injected environment lookup.
- `apiKeyFor`: endpoint URL.
- `isLoopbackHost`: host string; `isSuccess`: HTTP status.
- `errorMessage`: endpoint, status, error-body text.
- `timings`: completion text, `ChatResponse`, elapsed seconds.

#### Output
- Produces serialized JSON for a non-streaming chat-completions request.
- Produces normalized chat-completions endpoint and trimmed API key, or `null` when no key is configured.
- Produces loopback and HTTP-success predicates, formatted error text, and `AiGenerationTimings`.
- No network calls or resource writes.

#### Core logic
- `requestBody` creates system and user messages from `AiPromptSupport`, then writes `max_tokens`, `temperature`, `top_p`, `repetition_penalty`, and `stream=false`.
- Adds `model` and `reasoning.effort` only when non-blank; adds `top_k` and `min_p` only when positive; adds `seed` when nonnegative; adds `stop` only when configured list is non-empty.
- `endpoint` trims `baseUrl`, accepts only `http://` or `https://`, strips trailing slashes, then appends `/chat/completions`.
- `apiKey` resolves the configured environment variable and returns its trimmed non-blank value.
- `apiKeyFor` allows a configured key over HTTPS or plain HTTP to loopback only; rejects plain HTTP to other hosts before transmission.
- `isLoopbackHost` accepts exact lowercase `localhost`, IPv4 literals matching `127.d.d.d` with 1–3 digits per octet, and `[::1]`.
- `isSuccess` accepts statuses 200–299; `errorMessage` includes endpoint, status, and at most 500 body characters, appending `...` when truncated.
- `timings` reads completion, prompt, and cached token counts; computes completion tokens per elapsed second when seconds are positive, and sets prefill rate to zero.

#### Public API
- No public or protected members; all declared members are package-private or private.

#### Dependencies
- `LlamaCppJniConfig`, `AiPromptSupport`, `AiGenerationRequest`, `AiGenerationTimings`.
- `ChatResponse`, `Usage`, `Java8CompatibilityHelper`.
- Jackson `ObjectMapper`, `ObjectNode`, `ArrayNode`; Lombok `@ToString`; JSpecify `@Nullable`.
- Java `IOException`, `URL`, `List`, `Objects`, `Function`, `Pattern`.

#### Exceptions / Errors
- Constructor rejects null config, prompt support, or environment lookup via `Objects.requireNonNull`.
- `requestBody` propagates `IOException` from JSON serialization.
- `endpoint` throws `IllegalArgumentException` for non-HTTP(S) base URLs.
- `apiKeyFor` propagates URL parsing `IOException`; rejects a key over non-loopback plain HTTP with `IOException`.
- Error excerpt truncation uses body length; assumes non-null body.

#### Concurrency
- No synchronization or mutable request state; config and collaborators are final references.
- Shared static `ObjectMapper` is reused for request serialization.

> OpenAI-compatible chat requests are assembled from source files and model settings, API keys are restricted from cleartext transmission to non-local servers, and response metadata is converted into generation timings.

#### Purpose
- Prepares request bodies, endpoints, API-key policy, HTTP status/error handling, and timing data for OpenAI-compatible generation.
- Keeps these network-independent responsibilities separate from provider socket handling.

#### Type
- `final class OpenAiCompatibleProviderSupport` — package-private; no explicit superclass or implemented interfaces.
- Annotated `@ToString`; `promptSupport` and `environment` are excluded.
- Holds `LlamaCppJniConfig`, `AiPromptSupport`, environment lookup, and `Java8CompatibilityHelper`.

#### Input
- Constructor receives model config, prompt support, and an environment-variable lookup function.
- `requestBody` receives `AiGenerationRequest`; reads prompt key, source file/text, and model sampling settings.
- `endpoint` and `apiKey` read base URL and API-key environment-variable name from config.
- `apiKeyFor` receives an endpoint URL; `isLoopbackHost` receives a host; `isSuccess` receives an HTTP status.
- `errorMessage` receives endpoint, status, and response body.
- `timings` receives completion text, `ChatResponse`, and elapsed seconds.

#### Output
- Serializes a non-streaming chat-completions JSON request.
- Returns normalized endpoint and trimmed API key, or `null` when no key is configured.
- Returns HTTP/loopback predicates, formatted error text, and `AiGenerationTimings`.
- Performs no network I/O or resource writes.

#### Core logic
- `requestBody` creates system and user messages using `AiPromptSupport`, then writes `max_tokens`, `temperature`, `top_p`, `repetition_penalty`, and `stream=false`.
- Includes `model` and `reasoning.effort` when non-blank; `top_k` and `min_p` when positive; `seed` when nonnegative; and `stop` when non-empty.
- `endpoint` trims `baseUrl`, accepts only HTTP or HTTPS, removes trailing slashes, and appends `/chat/completions`.
- `apiKey` resolves the configured environment variable and returns its trimmed, non-blank value.
- `apiKeyFor` permits a key over HTTPS or plain HTTP to loopback only; rejects sending it over plain HTTP to other hosts.
- `isLoopbackHost` recognizes exact lowercase `localhost`, IPv4 addresses matching `127.d.d.d` with 1–3 digits per octet, and `[::1]`.
- `isSuccess` recognizes status codes 200–299. `errorMessage` includes up to 500 body characters and appends `...` when truncated.
- `timings` reads completion, prompt, and cached token counts; computes completion tokens per elapsed second when positive, and sets prefill rate to zero.

#### Public API
- No public or protected members.

#### Dependencies
- `LlamaCppJniConfig`, `AiPromptSupport`, `AiGenerationRequest`, `AiGenerationTimings`.
- `ChatResponse`, `Usage`, `Java8CompatibilityHelper`.
- Jackson `ObjectMapper`, `ObjectNode`, `ArrayNode`; Lombok `@ToString`; JSpecify `@Nullable`.
- Java `IOException`, `URL`, `List`, `Objects`, `Function`, `Pattern`.

#### Exceptions / Errors
- Constructor rejects null config, prompt support, or environment lookup.
- `requestBody` may throw `IOException` during serialization.
- `endpoint` throws `IllegalArgumentException` for a base URL that is not HTTP(S).
- `apiKeyFor` may throw `IOException` when URL parsing fails or a configured key would be sent over plain HTTP to a non-loopback host.
- `errorMessage` assumes a non-null body.

#### Concurrency
- No synchronization or mutable request state; config and collaborator references are final.
- Reuses a static `ObjectMapper`.
