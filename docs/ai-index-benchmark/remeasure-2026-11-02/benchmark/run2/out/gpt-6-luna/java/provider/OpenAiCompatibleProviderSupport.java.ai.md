### OpenAiCompatibleProviderSupport.java
- H: 1.0
- C: 92F0B02B
- D: 2026-11-01T23:42:40Z
- T: 2026-11-01T23:44:11Z
- G: 
- A: 0.0.0
- X: file
- P: C721BDEF
---
> Builds secure OpenAI-compatible chat-completion requests and converts remote responses and errors into generation results.

#### Purpose
- Supports the network-independent parts of OpenAI-compatible generation: request JSON, endpoint and API-key handling, HTTP status checks, error messages, and timing calculations.
- Uses `LlamaCppJniConfig` and `AiPromptSupport` to prepare requests for a configured model.

#### Type
- `final class OpenAiCompatibleProviderSupport` — package-private; annotated `@ToString`.
- No superclass or implemented interfaces.

#### Input
- Constructor receives `LlamaCppJniConfig`, `AiPromptSupport`, and an environment-variable lookup function.
- `requestBody` consumes an `AiGenerationRequest`, prompt definitions, and configured model and sampling options.
- `endpoint` and `apiKey` read base URL and API-key environment name from config.
- `apiKeyFor` receives the destination endpoint; helper methods receive host, HTTP status, error body, or response and timing data.

#### Output
- Produces serialized chat-completions JSON and an endpoint URL.
- Returns an API key when configured and safe to send; otherwise returns `null`.
- Produces HTTP success checks, bounded error messages, and `AiGenerationTimings`.
- Stores config, prompt support, environment lookup, and a Java 8 compatibility helper.

#### Core logic
- `requestBody` creates system and user messages from the prompt support, adds required sampling options, and conditionally includes model, `top_k`, `min_p`, seed, stop strings, and reasoning effort; disables streaming.
- `endpoint` trims the configured base URL, accepts only `http://` or `https://`, removes trailing slashes, and appends `/chat/completions`.
- `apiKey` resolves the configured environment variable and returns its trimmed, nonblank value.
- `apiKeyFor` permits a configured key over HTTPS or plain HTTP to loopback only; rejects cleartext transmission to other hosts.
- `isLoopbackHost` recognizes exact lowercase `localhost`, IPv4 `127.x.x.x` literals, and bracketed `[::1]`.
- `isSuccess` accepts HTTP statuses 200–299; `errorMessage` includes endpoint, status, and at most 500 body characters plus `...`.
- `timings` uses response token counts and whole-call seconds for decode rate; prefill rate is zero.

#### Public API
- No public or protected members; the type and its members are package-private.

#### Dependencies
- `LlamaCppJniConfig`, `OpenAiCompatibleAiGenerationProvider`, `LlamaCppJniProviderSupport`
- `AiGenerationRequest`, `AiPromptSupport`, `AiGenerationTimings`
- `ChatResponse`, `Usage`
- Jackson `ObjectMapper`, `ArrayNode`, `ObjectNode`
- `Java8CompatibilityHelper`
- `IOException`, `URL`, `List`, `Objects`, `Function`, `Pattern`
- Lombok `ToString`; JSpecify `Nullable`

#### Exceptions / Errors
- Constructor rejects null config, prompt support, or environment lookup via `Objects.requireNonNull`.
- `requestBody` propagates `IOException` from JSON serialization.
- `endpoint` throws `IllegalArgumentException` for non-HTTP(S) base URLs.
- `apiKeyFor` throws `IOException` for unparsable endpoints or keys sent over plain HTTP to non-loopback hosts.
- `isSuccess` treats non-2xx responses as failures; `errorMessage` truncates long bodies.

#### Concurrency
- No synchronization or mutable shared request state; the `ObjectMapper` is shared as a static field.
