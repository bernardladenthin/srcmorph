### OpenAiCompatibleProviderSupport.java
- H: 1.0
- C: 92F0B02B
- D: 2026-11-01T14:19:41Z
- T: 2026-11-01T14:21:24Z
- G: 
- A: 0.0.0
- X: file
---
> Builds OpenAI-compatible chat-completion requests and safely handles endpoint, API-key, response-status, error-message, and timing data for remote model calls.

#### Purpose
- Provides network-independent support logic for `OpenAiCompatibleAiGenerationProvider`.
- Converts generation inputs and model configuration into request data and validates how credentials may be sent.

#### Type
- `final class OpenAiCompatibleProviderSupport` — package-private final class.
- Annotated `@ToString`; excludes `promptSupport` and `environment` from generated string output.

#### Input
- Constructor receives `LlamaCppJniConfig`, `AiPromptSupport`, and an environment-variable lookup function.
- Request construction consumes `AiGenerationRequest` fields and configured model, sampling, stop-string, and reasoning settings.
- Helpers consume endpoint URLs, API-key configuration, HTTP status/body, and chat response usage and elapsed seconds.

#### Output
- Produces serialized JSON request bodies, chat-completions endpoint URLs, and optional trimmed API keys.
- Produces status/error-message results and `AiGenerationTimings`.
- Throws on invalid endpoint schemes, unsafe cleartext API-key destinations, and serialization or URL parsing failures.

#### Core logic
- Builds a non-streaming JSON body with system and user messages; includes `model` and `reasoning.effort` only when non-blank.
- Always includes `max_tokens`, `temperature`, `top_p`, and `repetition_penalty`; includes `top_k` and `min_p` only when positive, `seed` when nonnegative, and `stop` when non-empty.
- Trims `baseUrl`, requires an `http://` or `https://` prefix, removes trailing slashes, and appends `/chat/completions`.
- Looks up the configured API-key environment variable; returns `null` when the variable name or value is blank or unset.
- Permits a configured API key over HTTPS or plain HTTP to `localhost`, an IPv4-shaped `127.x.x.x` host, or `[::1]`; rejects other plain-HTTP hosts.
- Classifies statuses 200–299 as successful and formats error messages with at most 500 body characters, followed by `...` when truncated.
- Computes decode rate as completion tokens divided by positive elapsed seconds; sets prefill rate to zero and carries prompt, cached, and completion token counts into timings.

#### Public API
- No public or protected members; class members are package-private or private.

#### Dependencies
- Configuration and domain: `LlamaCppJniConfig`, `AiGenerationRequest`, `AiPromptSupport`, `AiGenerationTimings`.
- Response and usage: `ChatResponse`, `Usage`.
- JSON: Jackson `ObjectMapper`, `ArrayNode`, `ObjectNode`.
- Utilities: `Java8CompatibilityHelper`, `Objects`, `Function`, `Pattern`, `URL`, `IOException`.
- Annotation: `Nullable`, Lombok `ToString`.

#### Exceptions / Errors
- Constructor rejects null configuration, prompt support, or environment lookup via `Objects.requireNonNull`.
- `endpoint()` throws `IllegalArgumentException` unless `baseUrl` starts with `http://` or `https://`.
- `apiKeyFor()` throws `IOException` for a configured key sent to a non-loopback plain-HTTP host; URL parsing may also throw `IOException`.
- `requestBody()` declares `IOException` for JSON serialization failure.
- Loopback matching is case-sensitive for `localhost`; IPv4 matching checks the literal pattern `127.<1–3 digits>.<1–3 digits>.<1–3 digits>`.

#### Concurrency
- No synchronization or mutable per-call state; configuration, prompt support, environment lookup, and mapper are retained as fields.
