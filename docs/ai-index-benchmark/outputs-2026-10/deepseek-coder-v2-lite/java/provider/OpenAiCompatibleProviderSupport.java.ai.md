### OpenAiCompatibleProviderSupport.java
- H: 1.0
- C: 92F0B02B
- D: 2026-10-29T00:30:34Z
- T: 2026-10-29T01:53:12Z
- G: 
- A: 0.0.0
- X: file
---
> Calculates VAT for invoices by processing request bodies, endpoints, API key handling, and error messages for OpenAI-compatible AI generation providers.

#### Purpose
- Provide support for configuring and interacting with OpenAI-compatible AI generation providers.
- Handle request bodies, endpoints, API keys, and error messages.

#### Type
- Class (`final class`)
- Modifiers: `@ToString`
- Extends: None
- Implements: None
- Key Generics: None
- Notable Annotations: `@ToString`

#### Input
- Constructor parameters:
  - `config`: LlamaCppJniConfig
  - `promptSupport`: AiPromptSupport
  - `environment`: Function<String, @Nullable String>

#### Output
- Method `requestBody(AiGenerationRequest request) -> String`: Creates and returns the JSON request body for chat-completions.
- Method `endpoint() -> String`: Constructs and returns the chat-completions URL.
- Method `apiKey() -> @Nullable String`: Retrieves the API key from the environment variable.
- Method `apiKeyFor(String endpoint) -> @Nullable String` throws IOException: Retrieves the API key for the given endpoint, ensuring it is not sent over cleartext HTTP to non-loopback hosts.
- Method `isLoopbackHost(String host) -> boolean`: Checks if a host is a loopback interface.
- Method `isSuccess(int status) -> boolean`: Determines if an HTTP status is within the success range.
- Method `errorMessage(String endpoint, int status, String body) -> String`: Constructs an error message for failed API calls.
- Method `timings(String text, ChatResponse response, double seconds) -> AiGenerationTimings`: Calculates and returns the timings of a call.

#### Core logic
- **requestBody**: Constructs a JSON request body for chat-completions with necessary parameters.
- **endpoint**: Constructs the chat-completions endpoint URL from the base URL.
- **apiKey**: Retrieves the API key from the environment variable, trimming it if necessary.
- **apiKeyFor**: Retrieves the API key and ensures it is not sent over cleartext HTTP to non-loopback hosts.
- **isLoopbackHost**: Checks if a host is a loopback interface.
- **isSuccess**: Determines if an HTTP status is within the success range.
- **errorMessage**: Constructs an error message for failed API calls.
- **timings**: Calculates and returns the timings of a call.

#### Public API
- `requestBody(AiGenerationRequest request) -> String`: Creates and returns the JSON request body for chat-completions.
- `endpoint() -> String`: Constructs and returns the chat-completions URL.
- `apiKey() -> @Nullable String`: Retrieves the API key from the environment variable.
- `apiKeyFor(String endpoint) -> @Nullable String` throws IOException: Retrieves the API key for the given endpoint, ensuring it is not sent over cleartext HTTP to non-loopback hosts.
- `isLoopbackHost(String host) -> boolean`: Checks if a host is a loopback interface.
- `isSuccess(int status) -> boolean`: Determines if an HTTP status is within the success range.
- `errorMessage(String endpoint, int status, String body) -> String`: Constructs an error message for failed API calls.
- `timings(String text, ChatResponse response, double seconds) -> AiGenerationTimings`: Calculates and returns the timings of a call.

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
- `IOException`: Thrown when there is an issue with API key retrieval or endpoint construction.
- `IllegalArgumentException`: Thrown when the base URL is not an HTTP(s) URL.

#### Concurrency
- The class does not explicitly handle threading or synchronization.
- The `environment` function is assumed to be thread-safe.
