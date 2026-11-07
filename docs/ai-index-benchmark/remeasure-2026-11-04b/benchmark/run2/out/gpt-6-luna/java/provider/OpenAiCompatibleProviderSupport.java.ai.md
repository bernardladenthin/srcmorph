### OpenAiCompatibleProviderSupport.java
- H: 1.0
- C: 92F0B02B
- D: 2026-11-04T11:56:04Z
- T: 2026-11-04T11:57:52Z
- G: 
- A: 0.0.0
- X: file
- P: 0B812A3C
---
> Builds OpenAI-compatible chat-completion requests, protects API keys from cleartext transmission to remote hosts, and converts responses and HTTP errors into generation results.

#### Purpose
- `OpenAiCompatibleProviderSupport` supplies request construction, endpoint and API-key handling, HTTP status/error formatting, and response timing calculations for OpenAI-compatible generation.

#### Type
- `final class OpenAiCompatibleProviderSupport` — final package-private class; no declared supertypes; annotated `@ToString`.

#### Input
- Constructor takes `LlamaCppJniConfig config`, `AiPromptSupport promptSupport`, and `Function<String, @Nullable String> environment`; all are required via `Objects.requireNonNull`.
- `requestBody(AiGenerationRequest request)` reads prompt key, source file/text, and model/sampling settings from `config` and `request`.
- `endpoint()` reads `config.baseUrl()`.
- `apiKey()` reads `config.apiKeyEnv()` and uses `environment.apply(...)`.
- `apiKeyFor(String endpoint)` uses the endpoint URL and configured API-key environment name.
- `isLoopbackHost(String host)`, `isSuccess(int status)`, and `errorMessage(String endpoint, int status, String body)` consume their arguments directly.
- `timings(String text, ChatResponse response, double seconds)` reads usage counts from `response`.

#### Output
- `requestBody(...)` returns serialized JSON; may throw `IOException`.
- `endpoint()` returns the chat-completions URL.
- `apiKey()` and `apiKeyFor(...)` return a trimmed key or `null`; `apiKeyFor(...)` may throw `IOException`.
- `isLoopbackHost(...)` and `isSuccess(...)` return booleans; `errorMessage(...)` returns a formatted message.
- `timings(...)` returns `AiGenerationTimings`; no fields are mutated and no resources are written.

#### Core logic
- `requestBody(...)` creates JSON using `MAPPER`: adds non-blank `config.remoteModel()`, system and user messages from `promptSupport.systemPrompt(...)` and `promptSupport.userMessage(...)`, then sampling fields.
  - Always includes `max_tokens`, `temperature`, `top_p`, `repetition_penalty`, and `"stream": false`.
  - Adds `top_k` and `min_p` only when positive; `seed` only when non-negative; `stop` only when non-empty; `model` and `reasoning.effort` only when non-blank.
- `endpoint()` trims `config.baseUrl()`, requires the `HTTP_SCHEME` or `HTTPS_SCHEME` prefix, removes trailing slashes, and appends `CHAT_COMPLETIONS_PATH`.
- `apiKey()` returns `null` for a blank `config.apiKeyEnv()`, an unset environment value, or a blank value; otherwise returns the trimmed environment value.
- `apiKeyFor(...)` gets the key with `apiKey()`. HTTPS and no-key requests return it directly; for HTTP with a key, parses the host with `new URL(endpoint).getHost()` and requires `isLoopbackHost(host)`.
- `isLoopbackHost(...)` accepts exactly `localhost`, IPv4 literals matching `IPV4_LOOPBACK`, or `IPV6_LOOPBACK`.
- `isSuccess(...)` accepts statuses from `HTTP_OK_FIRST` inclusive to `HTTP_OK_END` exclusive.
- `errorMessage(...)` truncates bodies over `ERROR_BODY_EXCERPT` characters and appends `ELLIPSIS`; formats endpoint, status, and excerpt.
- `timings(...)` reads completion, prompt, and cached token counts from `response.getUsage()`. Decode rate is completion tokens divided by positive `seconds`, otherwise zero; prefill rate is zero.

#### Public API
- No public or protected members; the class and its members are package-private.

#### Dependencies
- Jackson: `ObjectMapper`, `ArrayNode`, `ObjectNode`.
- Java: `IOException`, `URL`, `List`, `Objects`, `Function`, `Pattern`.
- Lombok: `ToString`.
- Domain: `ChatResponse`, `Usage`, `AiGenerationRequest`, `AiPromptSupport`, `Java8CompatibilityHelper`, `LlamaCppJniConfig`, `AiGenerationTimings`.
- Annotation: `Nullable`.

#### Exceptions / Errors
- Constructor rejects null `config`, `promptSupport`, or `environment` with `Objects.requireNonNull`.
- `requestBody(...)` propagates serialization `IOException`.
- `endpoint()` throws `IllegalArgumentException` for base URLs not starting with `http://` or `https://`.
- `apiKeyFor(...)` may propagate URL parsing `IOException`; throws `IOException` if a configured key would be sent over HTTP to a non-loopback host.

#### Concurrency
- No synchronization or concurrency primitives; instance dependencies are final.
