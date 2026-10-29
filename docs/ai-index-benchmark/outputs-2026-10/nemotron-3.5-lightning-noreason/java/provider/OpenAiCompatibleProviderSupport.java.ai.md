### OpenAiCompatibleProviderSupport.java
- H: 1.0
- C: 92F0B02B
- D: 2026-10-29T08:50:27Z
- T: 2026-10-29T08:52:56Z
- G: 
- A: 0.0.0
- X: file
---
> Calculates the request body, endpoint, API key handling, error messages, and timings for OpenAI-compatible model requests without network I/O.

#### Purpose
The support class that provides pure logic for OpenAI-compatible model requests: constructing the JSON request body, resolving the endpoint URL, looking up the API key from environment variables, checking HTTP success status, formatting error messages, and computing generation timings. It is extracted from the provider so that stub-server tests can cover the provider independently of network I/O.

#### Type
final class. extends Object; implements No other types. Key generics: None. Notable annotations: @ToString (with @ToString.Exclude on two fields).

#### Input
- LlamaCppJniConfig config (provided via constructor): fields accessed include remoteModel, maxOutputTokens, temperature, topP, topK, minP, repeatPenalty, seed, stopStrings, reasoningEffort, apiKeyEnv.
- AiPromptSupport promptSupport (provided via constructor): methods accessed include systemPrompt(String) and userMessage(String, String).
- Function<String, @Nullable String> environment: used to look up API key environment variables.
- AiGenerationRequest request: passed to requestBody method; contains promptKey, sourceFile, sourceText.

#### Output
- String: requestBody returns the serialized JSON body of the chat-completions request.
- String: endpoint returns the full URL path (baseUrl + "/chat/completions"), throws IllegalArgumentException if baseUrl is not http(s).
- @Nullable String: apiKey returns the trimmed API key or null; apiKeyFor returns the key if endpoint is http to a loopback host, otherwise throws IOException.
- static String: errorMessage returns a formatted error string combining endpoint, status, and body excerpt.
- static AiGenerationTimings: timings returns a new timings object with prompt tokens, cached tokens, completion tokens, decode rate, etc.

#### Core logic
- requestBody: builds a JSON ObjectNode with model (if non-blank), two messages (system from promptSupport, user from promptSupport and request text), max_tokens, temperature, top_p, top_k (if > 0), min_p (if > 0.0f), repetition_penalty, seed (if >= 0), stop array (if non-empty), reasoning.effort (if non-blank), and stream set to false.
- endpoint: trims baseUrl, validates it starts with http:// or https://, strips trailing slashes, appends CHAT_COMPLETIONS_PATH ("/chat/completions").
- apiKey: checks if apiKeyEnv is blank via Java8CompatibilityHelper; if not, applies the environment function and trims the result, returning null if the key is null or blank after trimming.
- apiKeyFor: retrieves apiKey; if key is null or endpoint does not start with http://, returns key; otherwise parses the endpoint URL and checks isLoopbackHost; throws IOException if key is sent in cleartext to a non-loopback host.
- isLoopbackHost: returns true if host equals "localhost" (case-sensitive), matches IPv4 pattern 127.x.x.x, or equals "[::1]".
- isSuccess: returns true if status is between 200 inclusive and 300 exclusive.
- errorMessage: truncates body to ERROR_BODY_EXCERPT (500) characters with "..." ellipsis if longer, returns "endpoint HTTP status: excerpt".
- timings: extracts completionTokens, promptTokens, cachedTokens from Usage; computes decodeRate as completionTokens / seconds (0.0d if seconds <= 0); returns new AiGenerationTimings.

#### Public API
- requestBody(AiGenerationRequest request) -> String
- endpoint() -> String
- apiKey() -> @Nullable String
- apiKeyFor(String endpoint) -> @Nullable String
- isLoopbackHost(String host) -> boolean
- isSuccess(int status) -> boolean
- errorMessage(String endpoint, int status, String body) -> String
- timings(String text, ChatResponse response, double seconds) -> AiGenerationTimings

#### Dependencies
- com.fasterxml.jackson.databind.ObjectMapper MAPPER
- com.fasterxml.jackson.databind.node.ArrayNode
- com.fasterxml.jackson.databind.node.ObjectNode
- java.io.IOException
- java.net.URL
- java.util.List
- java.util.Objects
- java.util.function.Function
- java.util.regex.Pattern
- net.ladenthin.llama.value.ChatResponse
- net.ladenthin.llama.value.Usage
- net.ladenthin.srcmorph.document.AiGenerationRequest
- net.ladenthin.srcmorph.prompt.AiPromptSupport
- net.ladenthin.srcmorph.support.Java8CompatibilityHelper
- org.jspecify.annotations.Nullable

#### Exceptions / Errors
- requestBody throws IOException if the body cannot be serialised.
- endpoint throws IllegalArgumentException if baseUrl is not an http(s) URL.
- apiKeyFor throws IOException if a key is configured and endpoint is plain http to a non-loopback host, or if endpoint cannot be parsed.
- apiKey returns null when apiKeyEnv is unset, blank, or the looked-up key is null/blank.

#### Concurrency
No threading or synchronization concerns; the class is final with no mutable shared state across threads.
