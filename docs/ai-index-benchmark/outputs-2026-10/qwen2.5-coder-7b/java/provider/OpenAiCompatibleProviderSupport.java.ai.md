### OpenAiCompatibleProviderSupport.java
- H: 1.0
- C: 92F0B02B
- D: 2026-10-29T08:10:04Z
- T: 2026-10-29T08:17:29Z
- G: 
- A: 0.0.0
- X: file
---
> Manages support for OpenAI-compatible AI generation providers without network interaction.

#### Purpose
- Provides support for OpenAI-compatible AI generation providers.
- Handles request body, endpoint, API key lookup, status checks, error messages, and timings.

#### Type
- `final class`
- `@ToString`

#### Input
- Constructor parameters:
  - `config` (`LlamaCppJniConfig`)
  - `promptSupport` (`AiPromptSupport`)
  - `environment` (`Function<String, @Nullable String>`)
- Consumed fields:
  - `config.remoteModel()`
  - `config.baseUrl()`
  - `config.apiKeyEnv()`
  - `config.maxOutputTokens()`
  - `config.temperature()`
  - `config.topP()`
  - `config.topK()`
  - `config.minP()`
  - `config.repeatPenalty()`
  - `config.seed()`
  - `config.stopStrings()`
  - `config.reasoningEffort()`
- Injected types:
  - `LlamaCppJniConfig`
  - `AiPromptSupport`
  - `Function<String, @Nullable String>`

#### Output
- Produced state:
  - `endpoint()` (`String`)
- Mutated fields:
  - None
- Written resources:
  - None
- Side effects:
  - Throws `IllegalArgumentException` and `IOException`

#### Core logic
- Creates the request body for AI generation.
- Determines the endpoint URL for AI generation requests.
- Retrieves the API key from the environment.
- Validates the API key for secure transmission.
- Checks if a host is a loopback interface.
- Determines if an HTTP status is a success.
- Constructs an error message for non-success HTTP responses.
- Calculates AI generation timings.

#### Public API
- `requestBody(final AiGenerationRequest request) -> String`: Generates the request body for AI generation.
- `endpoint() -> String`: Returns the endpoint URL for AI generation requests.
- `apiKey() -> @Nullable String`: Retrieves the API key from the environment.
- `apiKeyFor(final String endpoint) -> @Nullable String`: Validates and returns the API key for secure transmission.
- `isLoopbackHost(final String host) -> boolean`: Checks if a host is a loopback interface.
- `isSuccess(final int status) -> boolean`: Determines if an HTTP status is a success.
- `errorMessage(final String endpoint, final int status, final String body) -> String`: Constructs an error message for non-success HTTP responses.
- `timings(final String text, final ChatResponse response, final double seconds) -> AiGenerationTimings`: Calculates AI generation timings.

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
- Throws `IllegalArgumentException` if `baseUrl` is not an `http(s)` URL.
- Throws `IOException` if a key is configured and `endpoint` is plain `http` to a non-loopback host.

#### Concurrency
- None
