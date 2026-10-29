### OpenAiCompatibleProviderSupport.java
- H: 1.0
- C: 92F0B02B
- D: 2026-10-29T08:10:04Z
- T: 2026-10-29T08:12:58Z
- G: 
- A: 0.0.0
- X: file
---
> Builds and validates OpenAI-compatible API requests for AI model calls, including body construction, endpoint resolution, secure key handling, and error messaging

#### Purpose
- Constructs JSON request bodies for chat completions with configurable sampling parameters  
- Validates and sanitizes API endpoints to ensure secure transmission of API keys  
- Determines whether an API key should be sent in cleartext (only for loopback hosts)  
- Formats error messages from HTTP responses with status and body excerpts  
- Computes performance metrics (token rates) from response timing and usage data  

#### Type
final class with @ToString annotation  
implements no interfaces  
extends no classes  
generics: none  
notable annotations: @ToString, @Nullable, @ToString.Exclude  

#### Input
- LlamaCppJniConfig (model settings: remoteModel, baseUrl, apiKeyEnv, sampling knobs)  
- AiPromptSupport (system and user prompts for the request)  
- Function<String, @Nullable String> (environment variable lookup)  
- AiGenerationRequest (source file, source text, prompt key)  
- String endpoint (from endpoint() method)  
- int status (HTTP response code)  
- String body (error response body)  
- String text (generated completion)  
- ChatResponse (response with usage data)  
- double seconds (wall-clock duration of call)  

#### Output
- String (request body in JSON format)  
- String (full chat-completions endpoint URL)  
- @Nullable String (API key, or null if not configured)  
- @Nullable String (secure API key for loopback-only endpoints)  
- String (error message with status and body excerpt)  
- AiGenerationTimings (token counts and decode rate)  

#### Core logic
- Builds a standardized JSON request body with model, messages, and sampling parameters  
- Validates that baseUrl starts with http(s) and trims trailing slashes  
- Extracts API key from environment variable, only if configured and non-blank  
- Checks if endpoint uses plain HTTP and if host is loopback (127.x.x.x or localhost or [::1])  
- Refuses to send API key over plain HTTP to non-loopback hosts  
- Formats error messages by truncating large response bodies to 500 characters  
- Computes decode rate as tokens generated per wall-clock second  
- Returns zero prefill rate as no prefill timing is available  

#### Public API
requestBody(AiGenerationRequest) -> String (builds JSON request body)  
endpoint() -> String (returns sanitized endpoint URL)  
apiKey() -> @Nullable String (gets API key from env var)  
apiKeyFor(String) -> @Nullable String (securely returns key for loopback only)  
isLoopbackHost(String) -> boolean (checks if host is loopback)  
isSuccess(int) -> boolean (checks if HTTP status is 2xx)  
errorMessage(String, int, String) -> String (formats error message)  
timings(String, ChatResponse, double) -> AiGenerationTimings (computes token rates)  

#### Dependencies
com.fasterxml.jackson.databind.ObjectMapper  
com.fasterxml.jackson.databind.node.ArrayNode  
com.fasterxml.jackson.databind.node.ObjectNode  
java.net.URL  
java.util.List  
java.util.Objects  
java.util.function.Function  
java.util.regex.Pattern  
net.ladenthin.llama.value.ChatResponse  
net.ladenthin.llama.value.Usage  
net.ladenthin.srcmorph.document.AiGenerationRequest  
net.ladenthin.srcmorph.prompt.AiPromptSupport  
net.ladenthin.srcmorph.support.Java8CompatibilityHelper  

#### Exceptions / Errors
- IllegalArgumentException if baseUrl is not http(s)  
- IOException if API key is sent over plain HTTP to non-loopback host  
- No checked exceptions thrown in public methods beyond IOException  

#### Concurrency
- Immutable state; no shared mutable fields  
- Thread-safe due to final fields and no shared state  
- No synchronization or concurrency control needed
