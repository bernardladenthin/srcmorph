### OpenAiCompatibleProviderSupport.java
- H: 1.0
- C: 92F0B02B
- D: 2026-10-29T04:50:45Z
- T: 2026-10-29T05:03:04Z
- G: 
- A: 0.0.0
- X: file
---
> Provides core logic for OpenAI-compatible AI generation providers, handling request construction, endpoint resolution, API key management, and error handling without network access.

#### Purpose
- Implements core functionality for OpenAI-compatible AI generation providers.
- Handles request body creation, endpoint resolution, API key lookup, status checks, error message generation, and timing calculations.
- Designed to be used without network access for testing and validation purposes.

#### Type
- Class: `OpenAiCompatibleProviderSupport`
- Final class with no inheritance or interfaces.
- Contains static final fields for constants and an `ObjectMapper` instance.
- Dependencies: `LlamaCppJniConfig`, `AiPromptSupport`, `Function<String, @Nullable String>`

#### Input
- `LlamaCppJniConfig config`: Configuration for the model, including remote model, base URL, API key environment, and sampling knobs.
- `AiPromptSupport promptSupport`: Prompt definitions for the AI generation.
- `Function<String, @Nullable String> environment`: Function to look up environment variables by name.

#### Output
- `String requestBody(AiGenerationRequest request)`: JSON body of the chat-completions request.
- `String endpoint()`: The endpoint URL for chat-completions requests.
- `@Nullable String apiKey()`: The API key from the environment variable.
- `@Nullable String apiKeyFor(String endpoint)`: The API key to send to the endpoint, ensuring it is not sent in cleartext to non-loopback hosts.
- `static String errorMessage(String endpoint, int status, String body)`: Error message for non-2xx HTTP statuses.
- `static AiGenerationTimings timings(String text, ChatResponse response, double seconds)`: Timings of the API call, including decode rate.

#### Core logic
- Constructs the JSON request body for chat-completions, including model, messages, and sampling parameters.
- Resolves the endpoint URL by trimming and appending the chat-completions path to the base URL.
- Retrieves the API key from the environment, ensuring it is not sent in cleartext to non-loopback hosts.
- Generates error messages for non-2xx HTTP statuses, including endpoint, status, and error body excerpt.
- Calculates timings for API calls, including decode rate based on generated tokens and wall-clock seconds.

#### Public API
- `String requestBody(AiGenerationRequest request) -> String`: Creates the JSON request body.
- `String endpoint() -> String`: Returns the chat-completions endpoint URL.
- `@Nullable String apiKey() -> @Nullable String`: Retrieves the API key from the environment.
- `@Nullable String apiKeyFor(String endpoint) -> @Nullable String`: Returns the API key for the given endpoint, ensuring it is not sent in cleartext to non-loopback hosts.
- `static String errorMessage(String endpoint, int status, String body) -> String`: Generates an error message for non-2xx HTTP statuses.
- `static AiGenerationTimings timings(String text, ChatResponse response, double seconds) -> AiGenerationTimings`: Calculates timings for API calls.

#### Dependencies
- `com.fasterxml.jackson.databind.ObjectMapper`
- `com.fasterxml.jackson.databind.node.ArrayNode`
- `com.fasterxml.jackson.databind.node.ObjectNode`
- `java.io.IOException`
- `java.net.URL`
- `java.util.List`
- `java.util.Objects`
- `java.util.function.Function`
- `java.util.regex.Pattern`
- `lombok.ToString`
- `net.ladenthin.llama.value.ChatResponse`
- `net.ladenthin.llama.value.Usage`
- `net.ladenthin.srcmorph.document.AiGenerationRequest`
- `net.ladenthin.srcmorph.prompt.AiPromptSupport`
- `net.ladenthin.srcmorph.support.Java8CompatibilityHelper`
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- `IllegalArgumentException`: Thrown if the base URL is not an `http(s)` URL.
- `IOException`: Thrown if the API key is configured and the endpoint is plain `http` to a non-loopback host, or if the endpoint cannot be parsed.

#### Concurrency
- No concurrency considerations mentioned in the provided code.
