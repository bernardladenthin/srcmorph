### OpenAiCompatibleProviderSupport.java
- H: 1.0
- C: 92F0B02B
- D: 2026-10-29T13:48:14Z
- T: 2026-10-29T13:55:53Z
- G: 
- A: 0.0.0
- X: file
---
> Handles non-network logic for OpenAI-compatible API providers, including request body serialization, endpoint validation, and API key security.

#### Purpose
* Encapsulates the logic for constructing OpenAI-compatible chat-completion requests.
* Manages secure API key retrieval and validates that keys are only sent over HTTPS or to loopback addresses.

#### Type
Final class.

#### Input
* `LlamaCppJniConfig`: Model configuration (baseUrl, apiKeyEnv, sampling parameters).
* `AiPromptSupport`: Prompt definitions for system and user messages.
* `Function<String, @Nullable String>`: Environment variable lookup mechanism.
* `AiGenerationRequest`: Request details including prompt keys and source text.
* `ChatResponse`: API response containing token usage.

#### Output
* `String`: JSON request body, formatted endpoint URL, or API key.
* `AiGenerationTimings`: Calculated timing metrics based on token usage and wall-clock time.
* `boolean`: Validation results for HTTP status codes and loopback hosts.

#### Core logic
* **Request Construction**: Builds a JSON object containing `model`, `messages` (system and user), `max_tokens`, `temperature`, `top_p`, `repetition_penalty`, and optional fields (`top_k`, `min_p`, `seed`, `stop` strings, and `reasoning.effort`).
* **Endpoint Formatting**: Trims the `baseUrl`, removes trailing slashes, and appends `/chat/completions`.
* **Security Validation**: 
    * Checks if the `baseUrl` uses `http` or `https`.
    * Prevents sending API keys over plain `http` unless the host is a loopback address (`localhost`, `127.x.x.x`, or `[::1]`).
* **Error Handling**: Generates error messages by truncating the HTTP response body to 500 characters.
* **Timing Calculation**: Computes `decodeRate` as `completionTokens / seconds` and sets `prefillRate` to 0.0.

#### Public API
* `requestBody(AiGenerationRequest) -> String`: Serializes request to OpenAI JSON.
* `endpoint() -> String`: Returns the full chat-completions URL.
* `apiKey() -> @Nullable String`: Retrieves trimmed key from environment.
* `apiKeyFor(String) -> @Nullable String`: Returns key if transmission is secure.
* `isLoopbackHost(String) -> boolean`: Validates if host is local.
* `isSuccess(int) -> boolean`: Checks if status is 2xx.
* `errorMessage(String, int, String) -> String`: Formats HTTP error details.
* `timings(String, ChatResponse, double) -> AiGenerationTimings`: Calculates token-based timings.

#### Dependencies
* `com.fasterxml.jackson.databind.ObjectMapper`
* `net.ladenthin.llama.value.ChatResponse`
* `net.ladenthin.llama.value.Usage`
* `net.ladenthin.srcmorph.document.AiGenerationRequest`
* `net.ladenthin.srcmorph.prompt.AiPromptSupport`
* `net.ladenthin.srcmorph.support.Java8CompatibilityHelper`
* `java.net.URL`
* `java.util.regex.Pattern`

#### Exceptions / Errors
* `IOException`: Thrown during JSON serialization or when refusing to send an API key over insecure HTTP.
* `IllegalArgumentException`: Thrown if `baseUrl` does not start with `http://` or `https://`.

#### Concurrency
* Uses a static final `ObjectMapper` (thread-safe).
* The class is final and maintains immutable references to its dependencies.
