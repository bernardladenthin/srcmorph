### OpenAiCompatibleProviderSupport.java
- H: 1.0
- C: 92F0B02B
- D: 2026-10-29T14:19:17Z
- T: 2026-10-29T14:28:44Z
- G: 
- A: 0.0.0
- X: file
---
> Handles non-network logic for OpenAI-compatible API providers, including request body serialization, endpoint validation, and API key security.

#### Purpose
* Centralizes the construction of JSON request bodies and URL endpoints for OpenAI-compatible LLM providers.
* Manages secure retrieval and validation of API keys to prevent cleartext transmission to non-loopback hosts.

#### Type
Final class.

#### Input
* `LlamaCppJniConfig`: Configuration for model, base URL, API key environment variable, and sampling parameters.
* `AiPromptSupport`: Provider for system and user prompt templates.
* `Function<String, @Nullable String>`: Environment variable lookup function.
* `AiGenerationRequest`: Request details including prompt key, source file, and source text.
* `String` (endpoint/host): URL strings for validation.
* `int` (status): HTTP response status codes.
* `String` (body): HTTP response bodies.
* `ChatResponse`: Parsed API response containing token usage.
* `double` (seconds): Wall-clock duration of the API call.

#### Output
* `String`: JSON request body, formatted endpoint URL, or API key.
* `String`: Formatted error messages for non-2xx responses.
* `AiGenerationTimings`: Calculated token counts and decode rates.
* `boolean`: Validation results for loopback hosts and HTTP success statuses.

#### Core logic
* **Request Construction**: Builds a JSON object containing `model`, `messages` (system and user roles), and sampling knobs (`max_tokens`, `temperature`, `top_p`, `top_k`, `min_p`, `repetition_penalty`, `seed`, `stop` sequences, and `reasoning.effort`).
* **Endpoint Formatting**: Trims the base URL, removes trailing slashes, and appends `/chat/completions`, ensuring the scheme is `http` or `https`.
* **API Key Security**: 
    * Retrieves keys from the environment.
    * Blocks transmission of keys over plain `http` unless the host is identified as a loopback address (`localhost`, `127.x.x.x`, or `[::1]`).
* **Response Analysis**: 
    * Determines success based on 2xx status codes.
    * Truncates error bodies to 500 characters for exception messages.
    * Calculates `decodeRate` as `completionTokens / seconds` and sets prefill rate to 0.0.

#### Public API
* `requestBody(AiGenerationRequest) -> String`: Serializes request to OpenAI JSON format.
* `endpoint() -> String`: Returns the full chat-completions URL.
* `apiKey() -> @Nullable String`: Retrieves trimmed key from environment.
* `apiKeyFor(String) -> @Nullable String`: Returns key if transmission is secure.
* `isLoopbackHost(String) -> boolean`: Checks if host is local loopback.
* `isSuccess(int) -> boolean`: Checks if status is 200-299.
* `errorMessage(String, int, String) -> String`: Formats HTTP error details.
* `timings(String, ChatResponse, double) -> AiGenerationTimings`: Computes token metrics.

#### Dependencies
* `com.fasterxml.jackson.databind.ObjectMapper`
* `net.ladenthin.llama.value.ChatResponse`
* `net.ladenthin.llama.value.Usage`
* `net.ladenthin.srcmorph.document.AiGenerationRequest`
* `net.ladenthin.srcmorph.prompt.AiPromptSupport`
* `net.ladenthin.srcmorph.support.Java8CompatibilityHelper`
* `LlamaCppJniConfig`
* `AiGenerationTimings`

#### Exceptions / Errors
* `IOException`: Thrown during JSON serialization or when refusing to send an API key over insecure HTTP.
* `IllegalArgumentException`: Thrown if `baseUrl` does not start with `http://` or `https://`.
