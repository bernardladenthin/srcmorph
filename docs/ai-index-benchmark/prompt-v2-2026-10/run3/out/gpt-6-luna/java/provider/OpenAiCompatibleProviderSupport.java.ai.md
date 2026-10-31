### OpenAiCompatibleProviderSupport.java
- H: 1.0
- C: 92F0B02B
- D: 2026-10-31T14:57:29Z
- T: 2026-10-31T14:58:46Z
- G: 
- A: 0.0.0
- X: file
---
> Builds OpenAI-compatible chat-completions requests and safely handles endpoint, API-key, response-status, and timing data for remote AI generation.

#### Purpose
- Supports `OpenAiCompatibleAiGenerationProvider` operations that do not require network access.
- Formats chat requests, validates endpoint and API-key handling, and derives response errors and generation timings.

#### Type
- `final class OpenAiCompatibleProviderSupport` — package-private; notable annotation: Lombok `@ToString`.
- No superclass or implemented interfaces.
- Holds `LlamaCppJniConfig`, `AiPromptSupport`, an environment lookup function, and a `Java8CompatibilityHelper`.

#### Input
- Constructor receives model configuration, prompt support, and an environment-variable lookup function.
- `requestBody` consumes an `AiGenerationRequest` and config sampling options.
- `apiKeyFor` consumes an endpoint; `isLoopbackHost` consumes a host; `isSuccess` consumes an HTTP status.
- `errorMessage` consumes endpoint, status, and response body; `timings` consumes completion text, `ChatResponse`, and elapsed seconds.

#### Output
- Produces serialized JSON for a non-streaming chat-completions request.
- Produces the chat-completions endpoint, a trimmed API key or `null`, status/error-message results, and `AiGenerationTimings`.
- Does not mutate stored fields or perform network I/O.

#### Core logic
- Builds `messages` with system and user prompts; includes configured model and sampling options.
- Omits `top_k` and `min_p` unless positive; omits seed if negative, stop if empty, and model or reasoning effort if blank.
- Trims `baseUrl`, permits only `http://` or `https://`, removes trailing slashes, then appends `/chat/completions`.
- Looks up and trims the configured API key; refuses to send it over plain HTTP unless the parsed host is `localhost`, matches the IPv4 loopback pattern, or is `[::1]`.
- Treats HTTP statuses 200–299 as success; truncates error-body excerpts after 500 characters and adds `...`.
- Computes decode rate as completion tokens divided by positive elapsed seconds; sets prefill rate to zero.

#### Public API
- No public or protected members; the following members are package-private:
- `OpenAiCompatibleProviderSupport(LlamaCppJniConfig, AiPromptSupport, Function<String, @Nullable String>)` → constructor; validates and stores dependencies.
- `requestBody(AiGenerationRequest)` → `String`; serializes chat request.
- `endpoint()` → `String`; validates and builds endpoint.
- `apiKey()` → `@Nullable String`; resolves configured environment key.
- `apiKeyFor(String)` → `@Nullable String`; enforces cleartext key policy.
- `isLoopbackHost(String)` → `boolean`; recognizes loopback hosts.
- `isSuccess(int)` → `boolean`; checks 2xx status.
- `errorMessage(String, int, String)` → `String`; formats HTTP failure.
- `timings(String, ChatResponse, double)` → `AiGenerationTimings`; derives token rates.

#### Dependencies
- Jackson: `ObjectMapper`, `ArrayNode`, `ObjectNode`.
- Java: `IOException`, `URL`, `List`, `Objects`, `Function`, `Pattern`.
- Project: `LlamaCppJniConfig`, `ChatResponse`, `Usage`, `AiGenerationRequest`, `AiPromptSupport`, `Java8CompatibilityHelper`, `AiGenerationTimings`.
- Annotations: Lombok `@ToString`, JSpecify `@Nullable`.

#### Exceptions / Errors
- Constructor rejects null config, prompt support, or environment lookup with `NullPointerException`.
- `requestBody` propagates `IOException` from JSON serialization.
- `endpoint` throws `IllegalArgumentException` for non-HTTP(S) base URLs.
- `apiKeyFor` can throw `IOException` for an unparsable endpoint or a configured key sent over non-loopback plain HTTP.
- `errorMessage` truncates bodies longer than 500 characters.

#### Concurrency
- No synchronization or explicit concurrency control; fields are final, but the supplied function and dependencies’ thread-safety is unspecified.
