### OpenAiCompatibleProviderSupport.java
- H: 1.0
- C: 92F0B02B
- D: 2026-10-29T09:50:36Z
- T: 2026-10-29T09:52:46Z
- G: 
- A: 0.0.0
- X: file
---
> Handles non-network logic for OpenAI-compatible API providers, including request body serialization, endpoint construction, and API key security validation.

#### Purpose
* Prepares JSON payloads and URLs for OpenAI-compatible chat-completion endpoints.
* Validates that API keys are only transmitted over secure (HTTPS) or local (loopback) connections.

#### Type
Final class.

#### Input
* `LlamaCppJniConfig`: Model configuration (baseUrl, remoteModel, apiKeyEnv, sampling parameters).
* `AiPromptSupport`: Prompt definitions for system and user messages.
* `Function<String, @Nullable String>`: Environment variable lookup mechanism.
* `AiGenerationRequest`: Request details including prompt keys and source text.

#### Output
* `String`: JSON request body, formatted endpoint URL, or API key.
* `AiGenerationTimings`: Calculated performance metrics based on `ChatResponse` and wall-clock time.
* `boolean`: Success status of HTTP codes or loopback host verification.

#### Core logic
* **Request Body Generation**: Constructs a JSON object containing `messages` (system and user roles), `max_tokens`, `temperature`, `top_p`, `repetition_penalty`, and `stream: false`. Conditionally adds `model`, `top_k`, `min_p`, `seed`, `stop` sequences, and `reasoning.effort` if configured.
* **Endpoint Formatting**: Trims `baseUrl`, validates it starts with `http://` or `https://`, removes trailing slashes, and appends `/chat/completions`.
* **API Key Security**: 
    * Retrieves key from environment variable named in `config.apiKeyEnv()`.
    * Blocks transmission of keys over plain `http` unless the host is identified as a loopback address (`localhost`, `127.x.x.x`, or `[::1]`).
* **Error Handling**: Truncates error response bodies to 500 characters for exception messages.
* **Timing Calculation**: Derives `decodeRate` by dividing completion tokens from `Usage` by the total wall-clock seconds; sets prefill rate to 0.0.

#### Public API
* `requestBody(AiGenerationRequest) -> String`: Generates JSON for chat-completions.
* `endpoint() -> String`: Returns the full target URL.
* `apiKey() -> @Nullable String`: Retrieves trimmed key from environment.
* `apiKeyFor(String) -> @Nullable String`: Returns key if transmission is secure/local.
* `isLoopbackHost(String) -> boolean`: Checks if host is local loopback.
* `isSuccess(int) -> boolean`: Checks if HTTP status is 2xx.
* `errorMessage(String, int, String) -> String`: Formats error message with body excerpt.
* `timings(String, ChatResponse, double) -> AiGenerationTimings`: Calculates token metrics and decode rate.

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
* `IOException`: Thrown during JSON serialization or when API key security validation fails.
* `IllegalArgumentException`: Thrown if `baseUrl` does not use an http/https scheme.
