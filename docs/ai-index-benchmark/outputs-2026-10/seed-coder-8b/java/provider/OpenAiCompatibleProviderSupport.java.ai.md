### OpenAiCompatibleProviderSupport.java
- H: 1.0
- C: 92F0B02B
- D: 2026-10-29T00:10:55Z
- T: 2026-10-29T00:17:30Z
- G: 
- A: 0.0.0
- X: file
---
> Manages OpenAI-compatible AI generation requests, handling request body construction, endpoint management, API key retrieval, and error processing.

#### Purpose
- Supports AI generation requests for OpenAI-compatible models
- Manages network-related operations for AI interactions

#### Type
- final class
- Implements core logic for AI generation provider support

#### Input
- Configuration parameters (model, base URL, API key environment)
- AI generation request details
- Environment variable lookup function

#### Output
- JSON request body for AI generation
- Endpoint URL for AI requests
- API key for authentication
- Error messages and timings information

#### Core logic
- Constructs JSON request body with sampling parameters
- Manages endpoint URL construction and validation
- Handles API key retrieval and security
- Processes HTTP responses and error messages
- Calculates AI generation timings

#### Public API
- `OpenAiCompatibleProviderSupport(LlamaCppJniConfig, AiPromptSupport, Function<String, String>)`
- `requestBody(AiGenerationRequest) -> String`
- `endpoint() -> String`
- `apiKey() -> String`
- `apiKeyFor(String) -> String`
- `isLoopbackHost(String) -> boolean`
- `isSuccess(int) -> boolean`
- `errorMessage(String, int, String) -> String`
- `timings(String, ChatResponse, double) -> AiGenerationTimings`

#### Dependencies
- `ObjectMapper` from Jackson library
- `URL` class from Java standard library
- `Pattern` class from Java standard library
- `Function` interface from Java standard library
- Custom classes: `LlamaCppJniConfig`, `AiPromptSupport`, `Java8CompatibilityHelper`, `ChatResponse`, `AiGenerationTimings`

#### Exceptions / Errors
- `IOException` for serialization/deserialization errors
- `IllegalArgumentException` for invalid base URL format
- Null handling for API key and environment variable lookups

#### Concurrency
- Thread-safe due to immutable configuration and stateless methods
- No explicit synchronization required
