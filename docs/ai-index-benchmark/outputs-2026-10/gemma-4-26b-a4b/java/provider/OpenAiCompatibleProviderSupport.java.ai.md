### OpenAiCompatibleProviderSupport.java
- H: 1.0
- C: 92F0B02B
- D: 2026-10-31T10:06:17Z
- T: 2026-10-31T13:46:53Z
- G: 
- A: 0.0.0
- X: file
---
> Prepares and validates request data for OpenAI-compatible chat completion APIs, including JSON body construction, endpoint formatting, and secure API key handling.

#### Purpose
* Handles non-network logic for OpenAI-compatible providers, such as request body serialization, endpoint construction, and security checks for API keys.

#### Type
final class; implements no interfaces; uses Lombok `@ToString`.

#### Input
* `LlamaCppJniConfig` (configuration for model, URL, and sampling).
* `AiPromptSupport` (prompt definitions).
* `Function<String, @Nullable String>` (environment variable lookup).
* `AiGenerationRequest` (request details for body construction).
* `ChatResponse` and `Usage` (for timing calculations).

#### Output
* JSON request bodies as `String`.
* Formatted endpoint URLs.
* API keys (with security validation).
* `AiGenerationTimings` objects.

#### Core logic
* **Request Body Construction**: Serializes `AiGenerationRequest` into a JSON object containing `model`, `messages` (system and user roles), and sampling parameters (`max_tokens`, `temperature`, `top_p`, `top_k`, `min_p`, `repetition_penalty`, `seed`, `stop`, and `reasoning.effort`).
* **Endpoint Formatting**: Trims `baseUrl` and appends `/chat/completions`, enforcing `http` or `https` schemes.
* **Security Enforcement**: Prevents sending API keys over plain `http` unless the destination is a loopback host (`localhost`, `127.x.x.x`, or `[::1]`).
* **Error Formatting**: Truncates error response bodies to a 500-character excerpt for exception messages.
* **Performance Metrics**: Calculates decode rate by dividing completion tokens by total wall-clock seconds.

#### Public API
* `OpenAiCompatibleProviderSupport(config, promptSupport, environment)` -> `OpenAiCompatibleProviderSupport` (constructor).
* `requestBody(request)` -> `String` (serializes request to JSON).
* `endpoint()` -> `String` (returns formatted chat completions URL).
* `apiKey()` -> `@Nullable String` (retrieves trimmed API key from environment).
* `apiKeyFor(endpoint)` -> `@Nullable String` (returns key or throws `IOException` if insecure).
* `isLoopbackHost(host)` -> `boolean` (checks if host is local loopback).
* `isSuccess(status)` -> `boolean` (checks if HTTP status is 2xx).
* `errorMessage(endpoint, status, body)` -> `String` (formats error message with body excerpt).
* `timings(text, response, seconds)` -> `AiGenerationTimings` (calculates token usage and rates).

#### Dependencies
* `com.fasterxml.jackson.databind.ObjectMapper`
* `net.ladenthin.llama.value.ChatResponse`
* `net.ladenthin.llama.value.Usage`
* `net.ladenthin.srcmorph.document.AiGenerationRequest`
* `net.ladenthin.srcmorph.prompt.AiPromptSupport`
* `net.ladenthin.srcmorph.support.Java8CompatibilityHelper`
* `java.io.IOException`
* `java.net.URL`
* `java.util.function.Function`

#### Exceptions / Errors
* `IOException`: Thrown if JSON serialization fails or if an API key is being sent over insecure `http` to a non-loopback host.
* `IllegalArgumentException`: Thrown if the `baseUrl` does not use `http` or `https`.

#### Concurrency
* Class is `final` and relies on immutable configuration and stateless logic; thread-safe.
