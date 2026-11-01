### OpenAiCompatibleProviderSupport.java
- H: 1.0
- C: 92F0B02B
- D: 2026-11-01T14:58:32Z
- T: 2026-11-01T15:00:26Z
- G: 
- A: 0.0.0
- X: file
---
> Builds OpenAI-compatible chat-completion requests and validates endpoints, API-key transport, HTTP responses, and generation timings.

#### Purpose
- Provides non-network support for `OpenAiCompatibleAiGenerationProvider`.
- Converts model configuration and prompt data into request JSON; supplies endpoint, API-key, status, error-message, and timing helpers.

#### Type
- `final class OpenAiCompatibleProviderSupport` — package-private final class.
- Annotated with `@ToString`; excludes `promptSupport` and `environment` from generated string output.

#### Input
- Constructor receives `LlamaCppJniConfig`, `AiPromptSupport`, and an environment-variable lookup function.
- `requestBody` consumes `AiGenerationRequest` fields and configured model, sampling, stop-string, and reasoning settings.
- Endpoint and API-key helpers consume configured URL and environment-variable name; `apiKeyFor` also receives the endpoint.
- Static helpers consume host names, HTTP statuses, error bodies, and response text/usage/timing data.

#### Output
- Produces serialized JSON for a non-streaming chat-completions request.
- Produces a chat-completions endpoint URL and an optional trimmed API key.
- Produces HTTP success decisions, error messages, and `AiGenerationTimings`.
- Holds final references to configuration, prompt support, environment lookup, and a compatibility helper.

#### Core logic
- Builds a JSON body with system and user messages from `AiPromptSupport`; always includes token limit, temperature, top-p, repetition penalty, and `stream: false`.
- Adds model and reasoning effort only when non-blank; adds top-k and min-p only when positive, seed when nonnegative, and stop strings when non-empty.
- Trims `baseUrl`, accepts only `http://` or `https://`, removes trailing slashes, then appends `/chat/completions`.
- Looks up the configured API key, returning `null` if its environment-variable name or value is blank.
- Allows a configured key over HTTPS; for plain HTTP, parses the endpoint and permits the key only for `localhost`, an IPv4 `127.x.x.x` literal, or `[::1]`.
- Treats statuses 200–299 as successful; formats failure text with endpoint, status, and at most 500 body characters plus `...` when truncated.
- Calculates decode rate as completion tokens divided by positive wall-clock seconds; reports prefill rate as zero and carries prompt and cached token counts into timings.

#### Dependencies
- `LlamaCppJniConfig`, `AiPromptSupport`, `AiGenerationRequest`, `AiGenerationTimings`
- `ChatResponse`, `Usage`
- `ObjectMapper`, `ArrayNode`, `ObjectNode`
- `Java8CompatibilityHelper`
- `Function`, `List`, `Objects`, `Pattern`, `URL`, `IOException`
- Lombok `@ToString`; JSpecify `@Nullable`

#### Exceptions / Errors
- Constructor rejects null configuration, prompt support, or environment lookup with `Objects.requireNonNull`.
- `requestBody` propagates `IOException` from JSON serialization.
- `endpoint` throws `IllegalArgumentException` for non-HTTP(S) base URLs.
- `apiKeyFor` propagates URL-parsing `IOException`; refuses to send a configured key over plain HTTP to a non-loopback host.
- `errorMessage` truncates long bodies; no explicit null handling for its inputs.

#### Concurrency
- Instance dependencies are final; no synchronization or mutable per-call state is present.
- Uses a shared static `ObjectMapper`.
