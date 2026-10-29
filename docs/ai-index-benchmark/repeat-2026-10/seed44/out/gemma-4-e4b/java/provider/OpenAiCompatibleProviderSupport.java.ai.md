### OpenAiCompatibleProviderSupport.java
- H: 1.0
- C: 92F0B02B
- D: 2026-10-29T14:01:55Z
- T: 2026-10-29T14:04:44Z
- G: 
- A: 0.0.0
- X: file
---
> This class provides the non-network specific logic for constructing OpenAI-compatible AI generation requests, determining endpoints, and handling API key security checks.

#### Purpose
* Implements support logic for OpenAI-compatible AI generation providers.
* Encapsulates request body construction, endpoint derivation, and security checks.

#### Type
final class OpenAiCompatibleProviderSupport

#### Input
* Constructor: LlamaCppJniConfig config, AiPromptSupport promptSupport, Function<String, @Nullable String> environment.
* Method `requestBody`: AiGenerationRequest request.
* Method `apiKeyFor`: String endpoint.

#### Output
* Method `requestBody`: String (JSON request body).
* Method `endpoint`: String (full chat-completions URL).
* Method `apiKey`: @Nullable String (API key).
* Method `apiKeyFor`: @Nullable String (API key, potentially throws IOException).
* Method `errorMessage`: String (formatted error message).
* Method `timings`: AiGenerationTimings (call timings).

#### Core logic
* `requestBody`: Constructs JSON request body using `ObjectMapper`, populating fields like `model`, `messages` (system/user roles), `max_tokens`, `temperature`, and optional sampling knobs based on `LlamaCppJniConfig`.
* `endpoint`: Validates `baseUrl` starts with `http://` or `https://`, trims trailing slashes, and appends `/chat/completions`.
* `apiKeyFor`: Checks if the target `endpoint` is plain HTTP to a non-loopback host; throws IOException if insecure transmission is detected when an API key is present.
* `isLoopbackHost`: Checks if a given host string matches localhost, IPv4 loopback, or IPv6 loopback.
* `timings`: Calculates `decodeRate` based on completion tokens and total call seconds from `ChatResponse` usage data.

#### Public API
requestBody(AiGenerationRequest) -> String: Serializes request parameters into JSON body.
endpoint() -> String: Returns the full chat-completions endpoint URL.
apiKey() -> @Nullable String: Retrieves the API key from environment lookup.
apiKeyFor(String) -> @Nullable String: Returns API key, enforcing HTTPS or loopback for security.
isLoopbackHost(String) -> boolean: Checks if host is a loopback address.
isSuccess(int) -> boolean: Determines if an HTTP status is in the 2xx range.
errorMessage(String, int, String) -> String: Formats error message from endpoint, status, and body.
timings(String, ChatResponse, double) -> AiGenerationTimings: Calculates generation timing metrics.

#### Dependencies
ObjectMapper, ArrayNode, ObjectNode, IOException, URL, List, Objects, Function, Pattern, Lombok.ToString, LlamaCppJniConfig, AiPromptSupport, Java8CompatibilityHelper, ChatResponse, Usage, AiGenerationRequest, AiGenerationTimings.

#### Exceptions / Errors
* `requestBody`: IOException (serialization failure).
* `endpoint`: IllegalArgumentException (baseUrl not http(s)).
* `apiKeyFor`: IOException (insecure API key transmission detected).

#### Concurrency
final class structure suggests thread-safety for instance fields, though internal use of `ObjectMapper` (MAPPER) is static and thread-safe. The class itself is not explicitly designed for concurrent state mutation.
