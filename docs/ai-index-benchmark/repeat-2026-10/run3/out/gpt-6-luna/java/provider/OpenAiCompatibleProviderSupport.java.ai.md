### OpenAiCompatibleProviderSupport.java
- H: 1.0
- C: 92F0B02B
- D: 2026-10-29T14:19:17Z
- T: 2026-10-29T14:21:10Z
- G: 
- A: 0.0.0
- X: file
---
> Builds OpenAI-compatible chat-completion requests and applies endpoint, API-key safety, HTTP-status, error-message, and timing rules for remote model calls.

#### Purpose
- Supports `OpenAiCompatibleAiGenerationProvider` with request preparation and response-related calculations that do not require network access.
- Keeps provider support logic independently testable.

#### Type
- Package-private `final` class `OpenAiCompatibleProviderSupport`.
- Annotated `@ToString`; excludes `promptSupport` and `environment` from generated output.

#### Input
- Constructor receives `LlamaCppJniConfig`, `AiPromptSupport`, and an environment-variable lookup function.
- `requestBody` consumes an `AiGenerationRequest`, prompt definitions, and sampling settings from config.
- `endpoint`, `apiKey`, and `apiKeyFor` consume configured URL and API-key environment-variable name.
- `errorMessage` consumes endpoint, HTTP status, and response body.
- `timings` consumes completion text, `ChatResponse`, and elapsed seconds.

#### Output
- Produces serialized chat-completions JSON, endpoint URL, optional API key, error-message text, and `AiGenerationTimings`.
- Rejects unsafe cleartext API-key transmission and unsupported base URL schemes.

#### Core logic
- Builds JSON with system and user messages; always includes `max_tokens`, `temperature`, `top_p`, `repetition_penalty`, and `stream: false`.
- Adds `model` and `reasoning.effort` only when non-blank; adds `top_k` and `min_p` only when positive, `seed` only when nonnegative, and `stop` only when non-empty.
- Trims `baseUrl`, requires an `http://` or `https://` prefix, removes trailing slashes, and appends `/chat/completions`.
- Looks up and trims the configured API key; returns `null` if the variable name or value is blank or unset.
- Allows a configured key over HTTPS or plain HTTP to exact `localhost`, an IPv4 `127.x.x.x` literal, or `[::1]`; rejects other plain-HTTP hosts.
- Classifies statuses from 200 through 299 as successful; formats errors with endpoint, status, and at most 500 body characters plus `...` when truncated.
- Computes completion decode rate as completion tokens divided by positive elapsed seconds; uses zero otherwise, and sets prefill rate to zero.

#### Public API
- No public or protected members; operational methods and constants are package-private.

#### Dependencies
- `LlamaCppJniConfig`, `AiPromptSupport`, `AiGenerationRequest`, `AiGenerationTimings`
- `ChatResponse`, `Usage`
- Jackson `ObjectMapper`, `ObjectNode`, `ArrayNode`
- `Java8CompatibilityHelper`, `Function`, `URL`, `Pattern`
- Lombok `@ToString`; JSpecify `@Nullable`

#### Exceptions / Errors
- Constructor rejects null config, prompt support, or environment lookup via `Objects.requireNonNull`.
- `requestBody` propagates `IOException` from JSON serialization.
- `endpoint` throws `IllegalArgumentException` for non-HTTP(S) base URLs.
- `apiKeyFor` propagates URL parsing `IOException` and throws `IOException` when a key would be sent over plain HTTP to a non-loopback host.
- Error-body truncation applies only when body length exceeds 500 characters.

#### Concurrency
- No explicit synchronization or mutable per-call state; uses a shared static `ObjectMapper` and immutable instance fields.
