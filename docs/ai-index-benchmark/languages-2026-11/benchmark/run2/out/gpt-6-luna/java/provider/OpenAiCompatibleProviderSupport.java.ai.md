### OpenAiCompatibleProviderSupport.java
- H: 1.0
- C: 92F0B02B
- D: 2026-11-01T01:35:52Z
- T: 2026-11-01T01:37:27Z
- G: 
- A: 0.0.0
- X: file
---
> Builds OpenAI-compatible chat-completion requests and validates endpoints, API-key delivery, HTTP responses, and generation timings.

#### Purpose
- Supports OpenAI-compatible generation without network I/O, separating request and response handling from socket operations.
- Converts model configuration and prompt data into request JSON and generation metadata.

#### Type
- `final class OpenAiCompatibleProviderSupport` — package-private final class.
- Annotated `@ToString`; excludes `promptSupport` and `environment` from generated string output.

#### Input
- Constructor receives `LlamaCppJniConfig`, `AiPromptSupport`, and an environment-variable lookup function.
- `requestBody` consumes an `AiGenerationRequest`, prompt definitions, and configured model and sampling values.
- `endpoint` and `apiKeyFor` use the configured base URL and API-key environment-variable name.
- `timings` consumes completion text, `ChatResponse` usage counts, and elapsed seconds.
- `errorMessage` consumes endpoint, HTTP status, and response body.

#### Output
- Produces serialized chat-completions JSON, endpoint URLs, optional trimmed API keys, HTTP error messages, and `AiGenerationTimings`.
- Does not perform network I/O or mutate configuration.

#### Core logic
- Builds a non-streaming request with system and user messages, token limit, temperature, top-p, repetition penalty, and optional model, top-k, min-p, seed, stop strings, and reasoning effort.
- Trims the base URL, accepts only `http://` or `https://`, removes trailing slashes, and appends `/chat/completions`.
- Looks up and trims the configured API key; permits it over HTTPS or plain HTTP only for `localhost`, IPv4 `127.x.x.x`, or `[::1]`.
- Classifies HTTP 200–299 as successful; formats non-success errors with the body truncated to 500 characters plus `...`.
- Derives completion-token decode rate from whole-call elapsed seconds; reports prefill rate as zero.

#### Dependencies
- Jackson: `ObjectMapper`, `ArrayNode`, `ObjectNode`.
- Java: `IOException`, `URL`, `List`, `Objects`, `Function`, `Pattern`.
- Lombok: `ToString`.
- Llama values: `ChatResponse`, `Usage`.
- Project types: `AiGenerationRequest`, `AiPromptSupport`, `Java8CompatibilityHelper`, `LlamaCppJniConfig`, `AiGenerationTimings`.
- JSpecify: `Nullable`.

#### Exceptions / Errors
- Constructor rejects null configuration, prompt support, or environment lookup via `Objects.requireNonNull`.
- `requestBody` propagates `IOException` from JSON serialization.
- `endpoint` throws `IllegalArgumentException` for non-HTTP(S) base URLs.
- `apiKeyFor` throws `IOException` for an unparsable endpoint or a configured key sent over plain HTTP to a non-loopback host.
- Blank or unset API-key configuration yields `null`.

#### Concurrency
- No synchronization or mutable shared state; configuration, prompt support, and environment lookup are held in final fields.
- Shared `ObjectMapper` is static and final.
