### OpenAiCompatibleProviderSupport.java
- H: 1.0
- C: 92F0B02B
- D: 2026-11-04T07:35:21Z
- T: 2026-11-04T07:36:46Z
- G: 
- A: 0.0.0
- X: file
- P: 1879E12B
---
> Builds OpenAI-compatible chat-completion requests and protects API keys from being sent over cleartext HTTP to non-local servers.

#### Purpose
- Supports `OpenAiCompatibleAiGenerationProvider` with request serialization, endpoint and API-key handling, HTTP status/error formatting, and call timing calculations.

#### Type
- `final class OpenAiCompatibleProviderSupport` — final, package-private; no supertypes declared; annotated `@ToString`.

#### Input
- Constructor: `LlamaCppJniConfig config`, `AiPromptSupport promptSupport`, and `Function<String, @Nullable String> environment`.
- `requestBody` reads the generation request’s prompt key, source file, and source text; reads model, sampling, stop-string, and reasoning settings from `config`.
- `endpoint` and `apiKeyFor` read `baseUrl`, `apiKeyEnv`, and environment values.
- `timings` consumes completion text, `ChatResponse` usage counts, and elapsed seconds.

#### Output
- Serializes chat-completion request bodies as JSON; constructs chat-completions endpoint URLs.
- Returns a trimmed API key or `null`; refuses to return a key for non-loopback plain-HTTP endpoints.
- Produces HTTP success checks, error messages, and `AiGenerationTimings`.
- Holds final references to configuration, prompt support, environment lookup, and compatibility helper.

#### Core logic
- `requestBody` creates system and user messages from `AiPromptSupport`; adds required sampling fields, conditionally adds non-blank model and reasoning effort, positive `top_k`/`min_p`, non-negative seed, and non-empty stop strings; sets `stream` to `false`; serializes with Jackson.
- `endpoint` trims `baseUrl`, accepts only `http://` or `https://`, removes trailing slashes, and appends `/chat/completions`.
- `apiKey` returns `null` if no environment-variable name is configured or its value is unset/blank; otherwise returns the trimmed value.
- `apiKeyFor` permits a configured key over HTTPS or plain HTTP to a loopback host; otherwise throws before sending the key.
- `isLoopbackHost` recognizes exact lowercase `localhost`, IPv4 literals matching `127.x.x.x`, and `[::1]`.
- `isSuccess` accepts HTTP statuses from 200 through 299; `errorMessage` includes endpoint, status, and at most 500 body characters, followed by `...` when truncated.
- `timings` uses response usage counts; computes decode rate as completion tokens per positive elapsed second, otherwise zero; sets prefill rate to zero.

#### Dependencies
- Jackson: `ObjectMapper`, `ArrayNode`, `ObjectNode`
- Java: `IOException`, `URL`, `List`, `Objects`, `Function`, `Pattern`
- Lombok: `ToString`
- `ChatResponse`, `Usage`
- `AiGenerationRequest`, `AiPromptSupport`
- `Java8CompatibilityHelper`, `LlamaCppJniConfig`, `AiGenerationTimings`
- JSpecify: `Nullable`

#### Exceptions / Errors
- Constructor rejects null `config`, `promptSupport`, or `environment` via `Objects.requireNonNull`.
- `requestBody` propagates `IOException` from JSON serialization.
- `endpoint` throws `IllegalArgumentException` for schemes other than HTTP or HTTPS.
- `apiKeyFor` propagates URL parsing `IOException`; throws `IOException` when a key would be sent over plain HTTP to a non-loopback host.
- `errorMessage` truncates error bodies longer than 500 characters.
