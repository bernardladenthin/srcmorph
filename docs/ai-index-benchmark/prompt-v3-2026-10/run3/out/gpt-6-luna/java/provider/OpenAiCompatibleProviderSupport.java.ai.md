### OpenAiCompatibleProviderSupport.java
- H: 1.0
- C: 92F0B02B
- D: 2026-10-31T20:34:56Z
- T: 2026-10-31T20:36:27Z
- G: 
- A: 0.0.0
- X: file
---
> Builds OpenAI-compatible chat requests and safely handles endpoint selection, API-key delivery, HTTP errors, and generation timings.

#### Purpose
- Supports the non-network logic for OpenAI-compatible AI generation.
- Converts configured prompts and sampling settings into chat-completions requests.

#### Type
- `final class OpenAiCompatibleProviderSupport` — package-private final class.
- Annotated with Lombok `@ToString`; prompt support and environment lookup are excluded from generated output.

#### Input
- Constructor: `LlamaCppJniConfig`, `AiPromptSupport`, and an environment-variable lookup function.
- `requestBody`: `AiGenerationRequest`, including prompt key, source file, and source text.
- `apiKeyFor`: endpoint URL.
- `isLoopbackHost`: URL host; `isSuccess`: HTTP status; `errorMessage`: endpoint, status, and response body; `timings`: completion text, `ChatResponse`, and elapsed seconds.
- Reads configuration for model, base URL, API-key variable name, and sampling settings.

#### Output
- Produces serialized JSON request bodies and chat-completions endpoint URLs.
- Returns a trimmed API key or `null`; rejects unsafe cleartext key delivery.
- Produces HTTP success checks, error messages, and `AiGenerationTimings`.

#### Core logic
- `requestBody` builds system and user messages from `AiPromptSupport`, adds configured sampling parameters, and sets `"stream": false`.
- Omits blank model and reasoning effort; includes `top_k` and `min_p` only when positive, seed only when nonnegative, and stop strings only when nonempty.
- `endpoint` trims the configured base URL, requires `http://` or `https://`, removes trailing slashes, and appends `/chat/completions`.
- `apiKey` looks up the configured environment variable and returns its trimmed nonblank value.
- `apiKeyFor` allows a configured key over HTTPS or HTTP to a recognized loopback host; otherwise rejects cleartext delivery.
- `isLoopbackHost` accepts exact lowercase `localhost`, IPv4 literals matching `127.x.x.x`, and `[::1]`.
- `isSuccess` accepts HTTP statuses 200–299.
- `errorMessage` includes the endpoint and status, truncating response bodies beyond 500 characters and appending `...`.
- `timings` uses response token usage; decode rate is completion tokens divided by positive elapsed seconds, while prefill rate is zero.

#### Public API
- No public or protected members; the class and its members are package-private.

#### Dependencies
- `LlamaCppJniConfig`, `AiGenerationRequest`, `AiPromptSupport`, `AiGenerationTimings`
- `ChatResponse`, `Usage`, `Java8CompatibilityHelper`
- Jackson `ObjectMapper`, `ArrayNode`, `ObjectNode`
- `Function`, `Objects`, `List`, `Pattern`, `URL`, `IOException`
- Lombok `@ToString`; JSpecify `@Nullable`

#### Exceptions / Errors
- Constructor rejects null configuration, prompt support, or environment lookup via `Objects.requireNonNull`.
- `requestBody` may throw `IOException` during JSON serialization.
- `endpoint` throws `IllegalArgumentException` for a non-HTTP(S) base URL.
- `apiKeyFor` may throw `IOException` for an unparsable endpoint or configured key sent over HTTP to a non-loopback host.
