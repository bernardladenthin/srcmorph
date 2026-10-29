### OpenAiCompatibleProviderSupport.java
- H: 1.0
- C: 92F0B02B
- D: 2026-10-28T23:55:11Z
- T: 2026-10-28T23:57:45Z
- G: 
- A: 0.0.0
- X: file
---
> This class provides the non-network related components for generating AI responses compatible with the OpenAI API structure.

#### Purpose
* Supports request body construction for OpenAI-compatible AI generation.
* Encapsulates configuration logic for API endpoints, keys, and error handling.

#### Type
final class OpenAiCompatibleProviderSupport

#### Input
* Constructor: LlamaCppJniConfig config, AiPromptSupport promptSupport, Function<String, @Nullable String> environment.
* Method `requestBody`: AiGenerationRequest request.
* Method `apiKeyFor`: String endpoint.

#### Output
* Method `requestBody`: String (JSON request body).
* Method `endpoint`: String (full API endpoint URL).
* Method `apiKey`: @Nullable String (API key).
* Method `apiKeyFor`: @Nullable String (API key, potentially throws IOException).
* Method `errorMessage`: String (formatted error message).
* Method `timings`: AiGenerationTimings (timing metrics).

#### Core logic
* `requestBody`: Constructs JSON request body using ObjectMapper, populating fields like `model`, `messages` (system/user roles), `max_tokens`, `temperature`, and optional sampling knobs based on `LlamaCppJniConfig`.
* `endpoint`: Validates `baseUrl` starts with `http://` or `https://` and appends the fixed `CHAT_COMPLETIONS_PATH`.
* `apiKey`: Retrieves API key by looking up the environment variable specified in `LlamaCppJniConfig`.
* `apiKeyFor`: Checks if the `endpoint` uses plain HTTP to a non-loopback host; throws IOException if cleartext key transmission is insecure.
* `isLoopbackHost`: Checks if a given host string matches localhost, IPv4 loopback, or IPv6 loopback.
* `timings`: Calculates decode rate based on completion tokens and total call seconds from `ChatResponse` usage.

#### Public API
requestBody(AiGenerationRequest) -> String: Generates the JSON request body.
endpoint() -> String: Returns the full chat-completions URL.
apiKey() -> @Nullable String: Retrieves the configured API key.
apiKeyFor(String) -> @Nullable String: Returns API key, enforcing secure transmission.
isLoopbackHost(String) -> boolean: Checks if host is a loopback address.
isSuccess(int) -> boolean: Determines if HTTP status is in 2xx range.
errorMessage(String, int, String) -> String: Formats error message from HTTP response.
timings(String, ChatResponse, double) -> AiGenerationTimings: Calculates AI generation timing metrics.

#### Dependencies
ObjectMapper, ArrayNode, ObjectNode, IOException, URL, List, Function, Pattern, Lombok.ToString, LlamaCppJniConfig, AiPromptSupport, Java8CompatibilityHelper, ChatResponse, Usage, AiGenerationRequest, AiGenerationTimings.

#### Exceptions / Errors
Throws IOException in `apiKeyFor` if insecure HTTP transmission is detected.
Throws IllegalArgumentException in `endpoint` if `baseUrl` lacks http(s) scheme.
Handles null/blank checks for configuration values using `Java8CompatibilityHelper`.

#### Concurrency
final class structure suggests thread-safety for instance fields, though internal usage of ObjectMapper is generally safe for concurrent read/write if not shared across threads without external synchronization. The class itself is designed to be stateless regarding network operations, focusing on data preparation.
