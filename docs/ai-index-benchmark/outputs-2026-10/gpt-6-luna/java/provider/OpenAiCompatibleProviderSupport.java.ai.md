### OpenAiCompatibleProviderSupport.java
- H: 1.0
- C: 92F0B02B
- D: 2026-10-28T21:19:32Z
- T: 2026-10-28T21:20:52Z
- G: 
- A: 0.0.0
- X: file
---
> Builds secure OpenAI-compatible chat-completion requests and converts remote responses into generation timings.

#### Purpose
- `OpenAiCompatibleProviderSupport` handles the non-network logic for OpenAI-compatible generation: request JSON, endpoint and API-key handling, HTTP status checks, error messages, and timings.
- Separates testable request and response support from provider socket handling.

#### Type
- `final` package-private class; no superclass or implemented interfaces.
- Annotated with Lombok `@ToString`; excludes `promptSupport` and `environment` from generated output.

#### Input
- Constructor receives `LlamaCppJniConfig`, `AiPromptSupport`, and an environment-variable lookup function.
- `requestBody` consumes an `AiGenerationRequest` and configuration sampling settings.
- `apiKeyFor` consumes an endpoint; `isLoopbackHost`, `isSuccess`, `errorMessage`, and `timings` consume host, status, error-body, or response data.
- Uses a shared Jackson `ObjectMapper` to construct and serialize request JSON.

#### Output
- Produces serialized chat-completion JSON with system and user messages and configured sampling options.
- Produces a normalized `/chat/completions` endpoint and an optional trimmed API key.
- Produces status classification, error text, and `AiGenerationTimings`.
- Throws on invalid endpoint schemes, unsafe cleartext API-key destinations, or JSON serialization failure.

#### Core logic
- `requestBody`: includes `model` and `reasoning.effort` only when non-blank; always adds system/user messages, `max_tokens`, `temperature`, `top_p`, `repetition_penalty`, and `stream: false`.
- Adds `top_k` and `min_p` only when positive, `seed` only when non-negative, and `stop` only when the configured list is non-empty.
- `endpoint`: trims `baseUrl`, requires an `http://` or `https://` prefix, strips trailing slashes, then appends `/chat/completions`.
- `apiKey`: looks up the configured environment variable and returns its trimmed value, or `null` if the variable name or value is blank or unset.
- `apiKeyFor`: returns no key or any key over HTTPS; for a configured key over HTTP, parses the host and permits only `localhost`, a regex-matching `127.x.x.x` literal, or `[::1]`.
- `isSuccess` recognizes HTTP statuses from 200 through 299; `errorMessage` includes the endpoint, status, and error-body excerpt, truncating bodies over 500 characters and appending `...`.
- `timings` takes token counts from `ChatResponse.usage`; calculates decode rate as completion tokens divided by positive elapsed seconds, otherwise zero, and sets prefill rate to zero.

#### Public API
- No public or protected members; the class constructor and methods are package-private.

#### Dependencies
- Configuration and prompt: `LlamaCppJniConfig`, `AiPromptSupport`, `AiGenerationRequest`.
- Response and timings: `ChatResponse`, `Usage`, `AiGenerationTimings`.
- JSON: Jackson `ObjectMapper`, `ArrayNode`, `ObjectNode`.
- Utilities: `Java8CompatibilityHelper`, `Function`, `Objects`, `List`, `Pattern`, `URL`, `IOException`.
- Annotations: Lombok `@ToString`, JSpecify `@Nullable`.

#### Exceptions / Errors
- Constructor rejects null `config`, `promptSupport`, or `environment` with `Objects.requireNonNull`.
- `endpoint` throws `IllegalArgumentException` when `baseUrl` does not start with `http://` or `https://`.
- `apiKeyFor` may throw `IOException` for an unparsable URL or when sending a configured key over HTTP to a non-loopback host.
- `requestBody` declares `IOException` for serialization failure.
- Error-body truncation uses a 500-character excerpt limit.

#### Concurrency
- No synchronization or mutable per-call state; configuration, prompt support, environment lookup, and mapper are held in fields.
