### OpenAiCompatibleProviderSupport.java
- H: 1.0
- C: 92F0B02B
- D: 2026-10-29T13:48:14Z
- T: 2026-10-29T14:01:57Z
- G: 
- A: 0.0.0
- X: file
---
> Calculates request body, endpoint, API key handling, error messages, and timings for OpenAI-compatible model calls without network I/O.

#### Purpose
Support core request construction and validation for OpenAI-compatible model calls that do not involve network I/O.

#### Type
Class: final, @ToString. Extends/implements: none. Key generics: none. Notable annotations: @ToString, @ToString.Exclude.

#### Input
- LlamaCppJniConfig config (remoteModel, baseUrl, apiKeyEnv, maxOutputTokens, temperature, topP, topK, minP, repeatPenalty, seed, stopStrings, reasoningEffort)
- AiPromptSupport promptSupport
- Function<String, @Nullable String> environment (looks up env vars)
- AiGenerationRequest request (promptKey, sourceFile, sourceText)

#### Output
- String requestBody(AiGenerationRequest) -> JSON body for chat completions
- String endpoint() -> chat-completions URL
- @Nullable String apiKey() -> trimmed API key or null
- @Nullable String apiKeyFor(String endpoint) -> key validated for loopback/HTTPS, or null
- static boolean isLoopbackHost(String host) -> loopback check
- static boolean isSuccess(int status) -> 2xx check
- static String errorMessage(String endpoint, int status, String body) -> formatted error
- static AiGenerationTimings timings(String text, ChatResponse, double seconds) -> timing object

#### Core logic
- Builds JSON request body with model, messages (system + user), max_tokens, temperature, top_p, top_k (if >0), min_p (if >0.0f), repetition_penalty, seed (if >=0), stop array (if non-empty), reasoning.effort (if non-blank), stream=false
- Constructs endpoint by trimming baseUrl, stripping trailing slashes, appending /chat/completions; validates baseUrl is http(s)
- Retrieves API key from environment via function; returns null when env var blank/unset
- Validates API key transmission: allows cleartext only over HTTPS or to loopback hosts (localhost, 127.x.x.x, [::1]); throws IOException otherwise
- Checks loopback host via exact match on "localhost", IPv4 pattern 127.x.x.x, or IPv6 [::1]
- Determines success via 200 <= status < 300
- Formats error message as "endpoint answered HTTP status: body excerpt" (max 500 chars + "...")
- Extracts token counts from ChatResponse usage: prompt tokens, cached tokens (always 0), completion tokens, decode rate = completionTokens / seconds (0 if seconds <= 0)

#### Public API
- requestBody(AiGenerationRequest) -> String
- endpoint() -> String
- apiKey() -> @Nullable String
- apiKeyFor(String endpoint) -> @Nullable String
- isLoopbackHost(String host) -> boolean
- isSuccess(int status) -> boolean
- errorMessage(String endpoint, int status, String body) -> String
- timings(String text, ChatResponse response, double seconds) -> AiGenerationTimings

#### Dependencies
com.fasterxml.jackson.databind.ObjectMapper, com.fasterxml.jackson.databind.node.ArrayNode, com.fasterxml.jackson.databind.node.ObjectNode
net.ladenthin.llama.value.ChatResponse, net.ladenthin.llama.value.Usage
net.ladenthin.srcmorph.document.AiGenerationRequest
net.ladenthin.srcmorph.prompt.AiPromptSupport
net.ladenthin.srcmorph.support.Java8CompatibilityHelper
org.jspecify.annotations.Nullable

#### Exceptions / Errors
- IOException thrown by requestBody if JSON serialization fails
- IllegalArgumentException thrown by endpoint if baseUrl is not http(s)
- IOException thrown by apiKeyFor if API key is configured over plain http to a non-loopback host
- NullPointerGuard: config, promptSupport, environment validated for non-null in constructor

#### Concurrency
No threading or synchronization concerns; class is final with immutable state after construction.
