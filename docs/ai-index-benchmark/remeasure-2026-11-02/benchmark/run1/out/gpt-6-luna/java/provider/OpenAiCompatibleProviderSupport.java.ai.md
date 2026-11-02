### OpenAiCompatibleProviderSupport.java
- H: 1.0
- C: 92F0B02B
- D: 2026-11-01T23:26:17Z
- T: 2026-11-01T23:27:43Z
- G: 
- A: 0.0.0
- X: file
- P: C721BDEF
---
> Builds authenticated OpenAI-compatible chat-completion requests for source files, validates safe API-key delivery, and converts remote responses and failures into generation results.

#### Purpose
- Supports the network-independent request, endpoint, authentication, status, error-message, and timing logic for OpenAI-compatible generation.
- Formats prompts and sampling settings for a remote chat-completions server.

#### Type
- `final class OpenAiCompatibleProviderSupport` — package-private final class.
- Annotated `@ToString`; excludes `promptSupport` and `environment` from its generated string representation.

#### Input
- Constructor receives `LlamaCppJniConfig`, `AiPromptSupport`, and an environment-variable lookup function.
- `requestBody` consumes an `AiGenerationRequest`, configuration sampling options, and prompt definitions.
- `endpoint` and `apiKey` read URL and API-key environment configuration.
- `apiKeyFor` receives the request endpoint; helper methods receive host, HTTP status, error body, or response timing data.

#### Output
- Produces serialized JSON request bodies and chat-completions endpoint URLs.
- Returns a trimmed API key or `null`; rejects sending keys over non-loopback plain HTTP.
- Produces status/error-message helpers and `AiGenerationTimings`.
- Holds no mutable request state; constructor dependencies are stored in final fields.

#### Core logic
- Builds a non-streaming chat request with system and user messages, standard sampling settings, and optional model, `top_k`, `min_p`, seed, stop strings, and reasoning effort.
- Trims `baseUrl`, accepts only `http://` or `https://`, removes trailing slashes, and appends `/chat/completions`.
- Looks up the configured API-key environment variable; trims the value and treats missing or blank names/values as no key.
- Allows a configured key over HTTPS; for HTTP, parses the endpoint and permits only exact `localhost`, IPv4 `127.x.x.x`, or bracketed IPv6 `[::1]`.
- Defines success as HTTP 200–299; formats failures with endpoint, status, and at most 500 body characters plus `...` when truncated.
- Calculates completion-token decode rate from whole-call seconds; reports prefill rate as zero.

#### Public API
- No public or protected members; members are package-private.

#### Dependencies
- Jackson: `ObjectMapper`, `ArrayNode`, `ObjectNode`.
- Java: `IOException`, `URL`, `List`, `Objects`, `Function`, `Pattern`.
- Project: `LlamaCppJniConfig`, `ChatResponse`, `Usage`, `AiGenerationRequest`, `AiPromptSupport`, `Java8CompatibilityHelper`, `AiGenerationTimings`.
- Annotations: Lombok `@ToString`, JSpecify `@Nullable`.

#### Exceptions / Errors
- Constructor rejects null dependencies via `Objects.requireNonNull`.
- `requestBody` propagates `IOException` from JSON serialization.
- `endpoint` throws `IllegalArgumentException` for non-HTTP(S) base URLs.
- `apiKeyFor` may throw `IOException` for an unparsable URL or a configured key sent over plain HTTP to a non-loopback host.

#### Concurrency
- No synchronization or mutable per-call fields; dependencies and configuration references are final.
