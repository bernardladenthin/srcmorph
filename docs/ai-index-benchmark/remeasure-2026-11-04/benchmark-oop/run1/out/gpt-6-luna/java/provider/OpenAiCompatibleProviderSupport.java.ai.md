### OpenAiCompatibleProviderSupport.java
- H: 1.0
- C: 92F0B02B
- D: 2026-11-04T07:19:50Z
- T: 2026-11-04T07:21:16Z
- G: 
- A: 0.0.0
- X: file
- P: 1879E12B
---
> Builds OpenAI-compatible chat-completion requests, protects API keys from cleartext transmission to non-local servers, and converts responses into generation timings.

#### Purpose
- `OpenAiCompatibleProviderSupport` prepares request JSON and endpoint details for OpenAI-compatible model calls.
- It resolves and safeguards API keys, evaluates HTTP results, formats error messages, and calculates generation timings.

#### Type
- `final class OpenAiCompatibleProviderSupport` — final class; package-private; no declared supertypes; annotated `@ToString`.

#### Input
- Constructor: `LlamaCppJniConfig config`, `AiPromptSupport promptSupport`, and `Function<String, @Nullable String> environment`.
- `requestBody(AiGenerationRequest request)` reads prompt key, source file, and source text, plus model and sampling settings from `config`.
- `endpoint()` reads `config.baseUrl()`.
- `apiKey()` reads `config.apiKeyEnv()` and queries the environment function.
- `apiKeyFor(String endpoint)` parses the endpoint host when a key is configured and the endpoint uses HTTP.
- `isLoopbackHost(String host)`, `isSuccess(int status)`, `errorMessage(String endpoint, int status, String body)`, and `timings(String text, ChatResponse response, double seconds)` consume their named arguments.

#### Output
- `requestBody` returns serialized JSON with system/user messages and configured sampling options.
- `endpoint` returns the normalized base URL plus `/chat/completions`.
- `apiKey` and `apiKeyFor` return a trimmed key or `null`.
- `isLoopbackHost` and `isSuccess` return boolean checks; `errorMessage` returns an exception-message string.
- `timings` returns `AiGenerationTimings`; no fields are mutated.
- Uses a static Jackson `ObjectMapper`; no network calls or resource writes.

#### Core logic
- `requestBody` includes `model` and `reasoning.effort` only when non-blank; always adds system and user messages, token limit, temperature, top-p, repetition penalty, and `stream: false`.
- Adds `top_k` and `min_p` only when positive, `seed` only when nonnegative, and `stop` only when the configured stop-string list is non-empty.
- `endpoint` trims `baseUrl`, accepts only `http://` or `https://`, removes trailing slashes, then appends `/chat/completions`.
- `apiKey` returns `null` if the environment-variable name is blank, the variable is unset, or its value is blank; otherwise returns the trimmed value.
- `apiKeyFor` permits a configured key over HTTPS or HTTP to `localhost`, an IPv4 `127.x.x.x` literal, or `[::1]`; rejects other HTTP hosts before sending the key.
- `isSuccess` accepts status codes 200–299. `errorMessage` appends the response body, truncated to 500 characters plus `...` when longer.
- `timings` uses response usage counts; decode rate is completion tokens divided by positive wall-clock seconds, otherwise zero. Prefill rate is always zero.

#### Dependencies
- Jackson: `ObjectMapper`, `ArrayNode`, `ObjectNode`.
- Java: `IOException`, `URL`, `List`, `Objects`, `Function`, `Pattern`.
- Lombok: `ToString`.
- JSpecify: `Nullable`.
- Project types: `LlamaCppJniConfig`, `ChatResponse`, `Usage`, `AiGenerationRequest`, `AiPromptSupport`, `Java8CompatibilityHelper`, `AiGenerationTimings`.

#### Exceptions / Errors
- Constructor uses `Objects.requireNonNull` for `config`, `promptSupport`, and `environment`.
- `requestBody` propagates `IOException` during serialization.
- `endpoint` throws `IllegalArgumentException` for non-HTTP(S) base URLs.
- `apiKeyFor` propagates `IOException` for an unparsable URL or a configured key destined for a non-loopback HTTP host.
