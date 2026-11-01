### OpenAiCompatibleProviderSupport.java
- H: 1.0
- C: 92F0B02B
- D: 2026-11-01T14:41:21Z
- T: 2026-11-01T14:43:00Z
- G: 
- A: 0.0.0
- X: file
---
> Builds secure OpenAI-compatible chat-completion requests and converts remote responses and HTTP errors into generation results.

#### Purpose
- `OpenAiCompatibleProviderSupport` supplies request, endpoint, API-key, status, error-message, and timing logic for OpenAI-compatible generation.
- Keeps network-independent provider behavior testable separately from socket handling.

#### Type
- `final class OpenAiCompatibleProviderSupport` — package-private class.
- Annotated `@ToString`; excludes `promptSupport` and `environment` from generated string output.

#### Input
- Constructor receives `LlamaCppJniConfig`, `AiPromptSupport`, and an environment-variable lookup function.
- `requestBody` consumes an `AiGenerationRequest`, prompt definitions, and model/sampling configuration.
- `apiKeyFor` receives the request endpoint and consults the configured environment variable.
- `timings` consumes completion text, `ChatResponse` usage counts, and elapsed seconds.
- HTTP helpers receive host, status, endpoint, and error-body values.

#### Output
- Serializes chat-completion request JSON with system/user messages and configured sampling options.
- Produces the `/chat/completions` endpoint and an optional trimmed API key.
- Produces status checks, bounded error messages, and `AiGenerationTimings`.
- Does not perform network I/O.

#### Core logic
- Builds a non-streaming JSON request; includes model and reasoning effort only when non-blank.
- Always includes `max_tokens`, `temperature`, `top_p`, and `repetition_penalty`; includes `top_k` and `min_p` only when positive.
- Includes seed only when non-negative and stop strings only when non-empty.
- Trims `baseUrl`, accepts only `http://` or `https://`, removes trailing slashes, and appends `/chat/completions`.
- Resolves the configured API key; allows it over HTTPS or plain HTTP only for exact `localhost`, IPv4 `127.x.x.x` literals, or bracketed `[::1]`.
- Treats HTTP 200–299 as success; error messages include endpoint, status, and at most 500 body characters plus `...` when truncated.
- Computes decode rate as completion tokens divided by positive elapsed seconds; uses zero otherwise. Sets prefill rate to zero.

#### Dependencies
- Jackson: `ObjectMapper`, `ArrayNode`, `ObjectNode`.
- Java: `IOException`, `URL`, `List`, `Objects`, `Function`, `Pattern`.
- Lombok: `@ToString`.
- Domain: `ChatResponse`, `Usage`, `AiGenerationRequest`, `AiPromptSupport`, `LlamaCppJniConfig`, `AiGenerationTimings`.
- Support: `Java8CompatibilityHelper`, `@Nullable`.

#### Exceptions / Errors
- Constructor rejects null config, prompt support, and environment lookup via `Objects.requireNonNull`.
- `requestBody` propagates `IOException` from JSON serialization.
- `endpoint` throws `IllegalArgumentException` for non-HTTP(S) base URLs.
- `apiKeyFor` throws `IOException` for an unparsable endpoint or when a configured key would be sent over plain HTTP to a non-loopback host.
- `errorMessage` assumes a non-null body.

#### Concurrency
- No synchronization or mutable per-call state; uses a shared static `ObjectMapper` and immutable instance references.
