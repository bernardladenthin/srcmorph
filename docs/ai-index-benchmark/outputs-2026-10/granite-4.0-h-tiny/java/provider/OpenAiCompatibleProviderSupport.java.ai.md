### OpenAiCompatibleProviderSupport.java
- H: 1.0
- C: 92F0B02B
- D: 2026-10-29T00:07:37Z
- T: 2026-10-29T00:08:38Z
- G: 
- A: 0.0.0
- X: file
---
Here is the markdown summary of the Java source file OpenAiCompatibleProviderSupport.java:

```markdown
## Purpose
- Calculates VAT for invoices

## Type
- Class
- Final
- Extends: Java8CompatibilityHelper
- Implements: None
- Key generics and type bounds: None
- Annotations: Lombok.ToString, net.ladenthin.llama.value.ChatResponse, net.ladenthin.llama.value.Usage, net.ladenthin.srcmorph.document.AiGenerationRequest, net.ladenthin.srcmorph.prompt.AiPromptSupport, net.ladenthin.srcmorph.support.Java8CompatibilityHelper, org.jspecify.annotations.Nullable

## Input
- Constructor and method parameters: LlamaCppJniConfig config, AiPromptSupport promptSupport, Function<String, @Nullable String> environment
- Injected dependencies: None
- Consumed fields: None
- Read resources: None

## Output
- Return types: String, boolean, String, AiGenerationTimings
- Produced state: None
- Mutated fields: None
- Written resources: None
- Side effects: None

## Core Logic
- Creates the request body for chat-completions requests
- Determines the endpoint for requests
- Retrieves the API key from the environment
- Checks if a host is loopback
- Determines if an HTTP status is a success
- Generates the message of an exception for non-2xx responses
- Calculates timings for a call

## Public API
- requestBody(request) -> String: Generates the request body for chat-completions requests
- endpoint() -> String: Determines the endpoint for requests
- apiKey() -> @Nullable String: Retrieves the API key from the environment
- apiKeyFor(endpoint) -> @Nullable String: Retrieves the API key for a specific endpoint
- isLoopbackHost(host) -> boolean: Checks if a host is loopback
- isSuccess(status) -> boolean: Determines if an HTTP status is a success
- errorMessage(endpoint, status, body) -> String: Generates the message of an exception for non-2xx responses
- timings(text, response, seconds) -> AiGenerationTimings: Calculates timings for a call

## Dependencies
- com.fasterxml.jackson.databind.ObjectMapper
- com.fasterxml.jackson.databind.node.ArrayNode
- com.fasterxml.jackson.databind.node.ObjectNode
- java.io.IOException
- java.net.URL
- java.util.List
- java.util.Objects
- java.util.function.Function
- java.util.regex.Pattern
- lombok.ToString
- net.ladenthin.llama.value.ChatResponse
- net.ladenthin.llama.value.Usage
- net.ladenthin.srcmorph.document.AiGenerationRequest
- net.ladenthin.srcmorph.prompt.AiPromptSupport
- net.ladenthin.srcmorph.support.Java8CompatibilityHelper
- org.jspecify.annotations.Nullable

## Exceptions / Errors
- IllegalArgumentException if baseUrl is not an http(s) URL
- IOException if a key is configured and endpoint is plain http to a host that is not loopback

## Concurrency
- None
```
