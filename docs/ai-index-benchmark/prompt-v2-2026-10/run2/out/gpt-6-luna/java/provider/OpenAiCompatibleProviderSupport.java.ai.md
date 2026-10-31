### OpenAiCompatibleProviderSupport.java
- H: 1.0
- C: 92F0B02B
- D: 2026-10-31T14:44:17Z
- T: 2026-10-31T14:45:38Z
- G: 
- A: 0.0.0
- X: file
---
> Builds OpenAI-compatible chat-completions requests and safely handles endpoint, API-key, HTTP-status, error-message, and timing data for remote model calls.

#### Purpose
- `OpenAiCompatibleProviderSupport` contains network-independent support for `OpenAiCompatibleAiGenerationProvider`.
- Creates request JSON, validates endpoints and API-key destinations, and derives response status, error, and timing values.

#### Type
- `final class OpenAiCompatibleProviderSupport` — package-private final class.
- Annotated `@ToString`; excludes `promptSupport` and `environment` from generated string output.

#### Input
- Constructor receives `LlamaCppJniConfig`, `AiPromptSupport`, and an environment lookup `Function<String, @Nullable String>`.
- `requestBody` consumes an `AiGenerationRequest` and configuration values for model, sampling, stop strings, and reasoning effort.
- `apiKeyFor` consumes an endpoint; `errorMessage` consumes endpoint, status, and body; `timings` consumes text, `ChatResponse`, and elapsed seconds.
- Reads environment variables through the supplied lookup function.

#### Output
- Produces serialized chat-completions JSON, an endpoint URL, or a nullable trimmed API key.
- Produces success checks, HTTP error messages, and `AiGenerationTimings`.
- No network calls or external resource writes.

#### Core logic
- Builds a non-streaming request with system and user messages; includes optional model, positive `top_k` / `min_p`, nonnegative seed, nonempty stop strings, and nonblank reasoning effort.
- Trims `baseUrl`, requires `http://` or `https://`, removes trailing slashes, and appends `/chat/completions`.
- Looks up and trims the configured API key; refuses to send it over plain HTTP unless the parsed host is `localhost`, an IPv4 `127.x.x.x` literal, or `[::1]`.
- Treats HTTP statuses 200–299 as successful; truncates error bodies over 500 characters and appends `...`.
- Calculates decode rate as completion tokens divided by positive elapsed seconds; reports prefill rate as zero.

#### Public API
- No public or protected members; members are package-private.

#### Dependencies
- Jackson: `ObjectMapper`, `ArrayNode`, `ObjectNode`.
- Java: `IOException`, `URL`, `List`, `Objects`, `Function`, `Pattern`.
- Lombok: `ToString`.
- Llama values: `ChatResponse`, `Usage`.
- Application types: `AiGenerationRequest`, `AiPromptSupport`, `Java8CompatibilityHelper`, `LlamaCppJniConfig`, `AiGenerationTimings`.
- Annotation: `Nullable`.
- Provider reference: `OpenAiCompatibleAiGenerationProvider`; related support reference: `LlamaCppJniProviderSupport`.

#### Exceptions / Errors
- Constructor rejects null `config`, `promptSupport`, or `environment` with `NullPointerException`.
- `requestBody` declares `IOException` for JSON serialization failure.
- `endpoint` throws `IllegalArgumentException` for non-HTTP(S) base URLs.
- `apiKeyFor` declares `IOException` for malformed endpoints or configured keys targeting non-loopback plain-HTTP hosts.
- `isLoopbackHost` uses exact lowercase `localhost`, an IPv4 loopback regex, or exact `[::1]`; no DNS resolution.
- No explicit null handling for `errorMessage` body or `timings` response/usage.

#### Concurrency
- Configuration, prompt support, and environment lookup references are final; no synchronization or mutable per-call state is defined.
- Uses a shared static Jackson `ObjectMapper`.
