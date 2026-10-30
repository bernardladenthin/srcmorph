### OpenAiCompatibleProviderSupport.java
- H: 1.0
- C: 92F0B02B
- D: 2026-10-30T00:29:58Z
- T: 2026-10-30T00:35:43Z
- G: 
- A: 0.0.0
- X: file
---
> Builds OpenAI-compatible chat-completion requests and safely handles endpoint, API-key, status, error, and timing data for remote model generation.

#### Purpose
- `OpenAiCompatibleProviderSupport` supplies network-independent support for OpenAI-compatible generation.
- Constructs request JSON and validates endpoint/key handling; derives errors and generation timings.

#### Type
- Package-private `final` class; no `extends` or `implements`.
- Annotated `@ToString`; excludes `promptSupport` and `environment` from generated `toString`.

#### Input
- Constructor receives `LlamaCppJniConfig`, `AiPromptSupport`, and an environment-variable lookup function.
- `requestBody` consumes `AiGenerationRequest` and configured model/sampling settings.
- `apiKeyFor` receives an endpoint; `timings` receives completion text, `ChatResponse`, and elapsed seconds.
- Reads prompt definitions, config values, and environment variables.

#### Output
- Produces serialized chat-completions JSON, endpoint URLs, optional API keys, error messages, and `AiGenerationTimings`.
- Does not write resources or mutate fields after construction.

#### Core logic
- Builds system and user messages from `AiPromptSupport`; includes configured sampling fields, conditionally adding model, `top_k`, `min_p`, seed, stop strings, and reasoning effort.
- Trims `baseUrl`, permits only HTTP(S), removes trailing slashes, and appends `/chat/completions`.
- Retrieves and trims configured API keys; allows sending keys over HTTPS or HTTP to recognized loopback hosts only.
- Classifies HTTP 2xx responses as successful; truncates error bodies beyond 500 characters and appends `...`.
- Computes completion-token decode rate from whole-call seconds; reports prefill rate as zero.

#### Public API
- `OpenAiCompatibleProviderSupport(config, promptSupport, environment) -> constructor` — validates and stores dependencies.
- `requestBody(request) -> String` — serializes chat request.
- `endpoint() -> String` — constructs chat-completions URL.
- `apiKey() -> @Nullable String` — looks up configured API key.
- `apiKeyFor(endpoint) -> @Nullable String` — enforces cleartext key restriction.
- `isLoopbackHost(host) -> boolean` — recognizes localhost and loopback literals.
- `isSuccess(status) -> boolean` — checks for HTTP 2xx.
- `errorMessage(endpoint, status, body) -> String` — formats HTTP error details.
- `timings(text, response, seconds) -> AiGenerationTimings` — calculates usage and decode rate.

#### Dependencies
- Jackson: `ObjectMapper`, `ArrayNode`, `ObjectNode`.
- Java: `IOException`, `URL`, `List`, `Objects`, `Function`, `Pattern`.
- Lombok: `@ToString`; JSpecify: `@Nullable`.
- Project types: `LlamaCppJniConfig`, `ChatResponse`, `Usage`, `AiGenerationRequest`, `AiPromptSupport`, `Java8CompatibilityHelper`, `AiGenerationTimings`.

#### Exceptions / Errors
- Constructor rejects null config, prompt support, or environment lookup via `Objects.requireNonNull`.
- `requestBody` may throw `IOException` during serialization.
- `endpoint` throws `IllegalArgumentException` for non-HTTP(S) base URLs.
- `apiKeyFor` may throw `IOException` for invalid endpoints or configured keys sent over non-loopback plain HTTP.

#### Concurrency
- No synchronization or explicit threading; configuration and dependencies are held in final fields.
