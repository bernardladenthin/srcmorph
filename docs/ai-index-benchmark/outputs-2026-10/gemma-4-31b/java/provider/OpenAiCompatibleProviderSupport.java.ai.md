### OpenAiCompatibleProviderSupport.java
- H: 1.0
- C: 92F0B02B
- D: 2026-10-28T21:24:22Z
- T: 2026-10-28T21:32:43Z
- G: 
- A: 0.0.0
- X: file
---
> Manages request construction, endpoint validation, and API key security for OpenAI-compatible AI providers.

#### Purpose
* Encapsulates non-network logic for OpenAI-compatible AI generation.
* Ensures API keys are not leaked over non-secure connections to non-loopback hosts.

#### Type
Final class.

#### Input
* `LlamaCppJniConfig`: Model configuration (baseUrl, apiKeyEnv, sampling parameters).
* `AiPromptSupport`: Prompt definitions.
* `Function<String, @Nullable String>`: Environment variable lookup function.

#### Output
* JSON request bodies for chat completions.
* Normalized endpoint URLs.
* Validated API keys.
* `AiGenerationTimings` objects.
* Formatted error messages.

#### Core logic
* **Request Construction**: Builds a JSON body using `ObjectMapper` containing `model`, `messages` (system and user), `max_tokens`, `temperature`, `top_p`, `repetition_penalty`, and `stream: false`. Conditionally adds `top_k`, `min_p`, `seed`, `stop` strings, and `reasoning.effort`.
* **Endpoint Resolution**: Trims `baseUrl`, validates `http://` or `https://` schemes, removes trailing slashes, and appends `/chat/completions`.
* **API Key Security**: Retrieves keys from environment variables. `apiKeyFor` prevents sending keys over plain HTTP unless the host is identified as a loopback address.
* **Loopback Detection**: Identifies loopback hosts via `localhost`, IPv4 `127.0.0.0/8` regex, or IPv6 `[::1]`.
* **Response Validation**: Checks if HTTP status is in the 200-299 range.
* **Error Formatting**: Truncates error response bodies to 500 characters for exception messages.
* **Timing Calculation**: Computes decode rate as `completionTokens / seconds`; sets prefill rate to 0.0.

#### Public API
* `requestBody(AiGenerationRequest) -> String`: Generates JSON request body.
* `endpoint() -> String`: Resolves full API endpoint URL.
* `apiKey() -> String`: Retrieves API key from environment.
* `apiKeyFor(String) -> String`: Validates key security for specific endpoint.
* `isLoopbackHost(String) -> boolean`: Checks if host is loopback.
* `isSuccess(int) -> boolean`: Validates HTTP success status.
* `errorMessage(String, int, String) -> String`: Formats error response message.
* `timings(String, ChatResponse, double) -> AiGenerationTimings`: Calculates generation metrics.

#### Dependencies
* `com.fasterxml.jackson.databind.ObjectMapper`
* `net.ladenthin.llama.value.ChatResponse`
* `net.ladenthin.llama.value.Usage`
* `net.ladenthin.srcmorph.document.AiGenerationRequest`
* `net.ladenthin.srcmorph.prompt.AiPromptSupport`
* `net.ladenthin.srcmorph.support.Java8CompatibilityHelper`
* `net.ladenthin.srcmorph.provider.LlamaCppJniConfig`
* `net.ladenthin.srcmorph.provider.AiGenerationTimings`

#### Exceptions / Errors
* `IOException`: Thrown during JSON serialization or when refusing to send API keys over insecure connections.
* `IllegalArgumentException`: Thrown if `baseUrl` does not use an `http(s)` scheme.
