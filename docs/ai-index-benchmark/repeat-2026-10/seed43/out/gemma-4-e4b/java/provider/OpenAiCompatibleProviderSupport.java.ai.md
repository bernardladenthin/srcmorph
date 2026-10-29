### OpenAiCompatibleProviderSupport.java
- H: 1.0
- C: 92F0B02B
- D: 2026-10-29T13:54:46Z
- T: 2026-10-29T13:57:37Z
- G: 
- A: 0.0.0
- X: file
---
> This class provides the non-network specific logic for constructing OpenAI-compatible AI generation requests, determining endpoints, and handling API key security checks.

#### Purpose
*   Manages request body construction for OpenAI-compatible AI generation.
*   Handles endpoint URL generation and API key security validation.

#### Type
final class OpenAiCompatibleProviderSupport; @ToString

#### Input
*   Constructor: LlamaCppJniConfig config, AiPromptSupport promptSupport, Function<String, @Nullable String> environment.
*   requestBody(AiGenerationRequest request): AiGenerationRequest request.
*   apiKeyFor(String endpoint): String endpoint.

#### Output
*   requestBody(AiGenerationRequest request): String (JSON request body).
*   endpoint(): String (Chat completions URL).
*   apiKey(): @Nullable String (API key).
*   apiKeyFor(String endpoint): @Nullable String (API key, potentially throwing IOException).
*   errorMessage(String endpoint, int status, String body): String (Error message).
*   timings(String text, ChatResponse response, double seconds): AiGenerationTimings (Timing metrics).

#### Core logic
*   Constructs the JSON request body using ObjectNode, including system/user messages from AiPromptSupport.
*   Conditionally adds sampling knobs (top\_k, min\_p, seed, stop, reasoning.effort) based on configuration values.
*   Validates that the base URL starts with http:// or https:// in endpoint().
*   Checks if the host in the endpoint is loopback before allowing API key transmission over plain HTTP in apiKeyFor().
*   Calculates decode rate in timings() based on tokens and wall-clock seconds.

#### Public API
endpoint() -> String: Returns the chat-completions URL.
apiKey() -> @Nullable String: Retrieves the API key from environment.
apiKeyFor(String endpoint) -> @Nullable String: Returns key, enforcing HTTPS/loopback for security.
isLoopbackHost(String host) -> boolean: Checks if a host is loopback.
isSuccess(int status) -> boolean: Checks if HTTP status is 2xx.
errorMessage(String endpoint, int status, String body) -> String: Formats error message from response.
timings(String text, ChatResponse response, double seconds) -> AiGenerationTimings: Calculates generation timing metrics.

#### Dependencies
ObjectMapper, ArrayNode, ObjectNode, IOException, URL, List, Objects, Function, Pattern, Lombok.ToString, LlamaCppJniConfig, AiPromptSupport, Java8CompatibilityHelper, ChatResponse, Usage, AiGenerationRequest, AiGenerationTimings.

#### Exceptions / Errors
*   IllegalArgumentException: Thrown by endpoint() if baseUrl lacks http(s) scheme.
*   IOException: Thrown by apiKeyFor() if API key is sent over non-loopback HTTP.

#### Concurrency
*   final class structure suggests immutability of state after construction.
*   Uses static final ObjectMapper MAPPER, which is thread-safe for read/write operations on its internal state if used correctly (though typically, a single instance is fine for serialization).
*   No explicit synchronization mechanisms are present.
