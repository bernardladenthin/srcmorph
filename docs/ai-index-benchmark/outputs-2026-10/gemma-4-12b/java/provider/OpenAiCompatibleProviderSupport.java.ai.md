### OpenAiCompatibleProviderSupport.java
- H: 1.0
- C: 92F0B02B
- D: 2026-10-29T03:15:31Z
- T: 2026-10-29T03:56:30Z
- G: 
- A: 0.0.0
- X: file
---
> This class provides the non-network logic for OpenAI-compatible providers, including request body construction, endpoint resolution, API key security validation, and response timing calculations.

#### Purpose
* Provides the logic for preparing and validating requests to OpenAI-compatible APIs.
* Handles security checks for API keys over non-secure connections.
* Calculates generation timings and formats error messages.

#### Type
final class

#### Input
* `LlamaCppJniConfig`: Configuration containing model details, base URL, and sampling parameters.
* `AiPromptSupport`: Source for system and user prompt definitions.
* `Function<String, @Nullable String>`: Environment variable lookup function.
* `AiGenerationRequest`: The specific request data (prompt key, source file, source text).
* `String`: Endpoint URL (for key validation).
* `String`, `ChatResponse`, `double`: Data for timing calculations.

#### Output
* `String`: JSON request body.
* `String`: Formatted endpoint URL.
* `String`: API key (or null).
* `boolean`: Success status check.
* `String`: Formatted error message.
* `AiGenerationTimings`: Calculated performance metrics.

#### Core logic
* **Request Construction**: Builds a JSON body for `chat/completions` including model name, messages (system/user), sampling knobs (temperature, top_p, top_k, min_p, repetition_penalty, seed, stop strings), and reasoning effort.
* **Endpoint Resolution**: Trims the base URL and appends the `/chat/completions` path.
* **Security Validation**: Ensures API keys are only sent over HTTPS or to loopback hosts (localhost, 127.x.x.x, [::1]) to prevent cleartext transmission.
* **Error Handling**: Extracts a 500-character excerpt from error bodies to include in exception messages.
* **Timing Calculation**: Computes decode rate by dividing completion tokens by total wall-clock seconds.

#### Public API
* `requestBody(AiGenerationRequest)` -> `String` + Serializes request to JSON.
* `endpoint()` -> `String` + Returns the full chat-completions URL.
* `apiKey()` -> `String` + Retrieves and trims the API key from environment.
* `apiKeyFor(String)` -> `String` + Validates key safety for a specific endpoint.
* `isLoopbackHost(String)` -> `boolean` + Checks if host is a local loopback.
* `isSuccess(int)` -> `boolean` + Checks if status is 2xx.
* `errorMessage(String, int, String)` -> `String` + Formats error details with body excerpt.
* `timings(String, ChatResponse, double)` -> `AiGenerationTimings` + Calculates token rates.

#### Dependencies
* `com.fasterxml.jackson.databind.ObjectMapper`
* `com.fasterxml.jackson.databind.node.ArrayNode`
* `com.fasterxml.jackson.databind.node.ObjectNode`
* `net.ladenthin.llama.value.ChatResponse`
* `net.ladenthin.llama.value.Usage`
* `net.ladenthin.srcmorph.document.AiGenerationRequest`
* `net.ladenthin.srcmorph.prompt.AiPromptSupport`
* `net.ladenthin.srcmorph.support.Java8CompatibilityHelper`
* `net.ladenthin.llama.config.LlamaCppJniConfig`
* `net.ladenthin.srcmorph.model.AiGenerationTimings`

#### Exceptions / Errors
* `IOException`: Thrown if JSON serialization fails or if an API key is sent over insecure non-loopback HTTP.
* `IllegalArgumentException`: Thrown if the base URL does not use http or https schemes.
* `NullPointerException`: Handled via `Objects.requireNonNull` in the constructor.
