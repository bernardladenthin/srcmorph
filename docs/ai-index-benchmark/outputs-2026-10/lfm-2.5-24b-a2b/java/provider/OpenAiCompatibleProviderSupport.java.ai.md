### OpenAiCompatibleProviderSupport.java
- H: 1.0
- C: 92F0B02B
- D: 2026-10-31T10:06:17Z
- T: 2026-10-31T12:04:31Z
- G: 
- A: 0.0.0
- X: file
---
> Generates OpenAI-compatible request bodies, endpoints, API key handling, and timing utilities for local AI generation providers without network calls.

#### Purpose
- Provides the offline portion of the provider support: request bodies, endpoints, API key lookup, status checks, error messages, and timings for OpenAI-compatible local models.
- Keeps stub-server tests self-contained by avoiding network calls.

#### Type
- final class (no inheritance or implementation)
- final fields, no extends/implements; package-private final fields

#### Input
- Constructor: LlamaCppJniConfig config, AiPromptSupport promptSupport, Function<String, @Nullable String> environment
- Methods consume AiGenerationRequest, String, ChatResponse, double, and URL parameters

#### Output
- requestBody: String (JSON body)
- endpoint: String (URL)
- apiKey: String (nullable)
- apiKeyFor: String (nullable) or throws IOException
- isLoopbackHost: boolean
- isSuccess: boolean
- errorMessage: String
- timings: AiGenerationTimings

#### Core logic
- requestBody: serializes AiGenerationRequest into JSON with model, messages, sampling knobs, and config parameters
- endpoint: trims baseUrl, validates http(s) scheme, appends chat completions path
- apiKey: looks up apiKeyEnv in environment, trims, returns null if unset or blank
- apiKeyFor: validates loopback host before sending key, throws IOException for non-loopback plain http
- isLoopbackHost: checks localhost, IPv4 loopback, IPv6 loopback
- isSuccess: checks 2xx range
- errorMessage: truncates body to 500 chars, appends ellipsis
- timings: computes decode rate from usage tokens and seconds, returns AiGenerationTimings

#### Public API
- OpenAiCompatibleProviderSupport(LlamaCppJniConfig, AiPromptSupport, Function) -> constructor
- requestBody(AiGenerationRequest) -> String: builds JSON request body
- endpoint() -> String: returns chat completions URL
- isLoopbackHost(String) -> boolean: checks loopback host
- isSuccess(int) -> boolean: checks 2xx success
- errorMessage(String, int, String) -> String: formats error message
- timings(String, ChatResponse, double) -> AiGenerationTimings: computes timings
- apiKey..apiKeyFor (2 total)

#### Dependencies
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

#### Exceptions / Errors
- IOException thrown by requestBody on serialization failure, and by apiKeyFor when sending key to non-loopback plain http
- apiKey() returns null when no key is configured or environment variable is unset or blank
- No other checked exceptions; null handling via @Nullable

#### Concurrency
- Final class with final fields; immutable after construction
- No explicit synchronization or thread-safety guarantees; methods are not marked volatile
