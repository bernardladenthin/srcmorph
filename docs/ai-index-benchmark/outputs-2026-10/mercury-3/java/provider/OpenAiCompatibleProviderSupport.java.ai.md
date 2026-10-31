### OpenAiCompatibleProviderSupport.java
- H: 1.0
- C: 92F0B02B
- D: 2026-10-31T09:32:09Z
- T: 2026-10-31T09:32:42Z
- G: 
- A: 0.0.0
- X: file
---
> Builds the network-free half of an OpenAI-compatible LLM call: request JSON, endpoint URL, leak-safe API-key lookup, success/error reporting, and throughput timings.

#### Purpose
- Splits offline responsibilities out of OpenAiCompatibleAiGenerationProvider so they sit on the 100% PIT gate; socket handling stays in the provider (mirrors LlamaCppJniProviderSupport split).
- Supports one model via LlamaCppJniConfig, prompt text via AiPromptSupport, and environment lookup via a Function<String, @Nullable String>.

#### Type
- final class, package-private; no interface or superclass; no generics.
- Annotated @ToString (Lombok); @ToString.Exclude on promptSupport and environment fields.
- Member fields: 11 static constants/helpers (incl. MAPPER, Pattern, HTTP bounds) and 4 final instance fields.

#### Input
- Constructor: LlamaCppJniConfig config, AiPromptSupport promptSupport, Function<String, @Nullable String> environment — each wrapped in Objects.requireNonNull.
- requestBody(AiGenerationRequest): consumes config remoteModel/baseUrl knobs and promptSupport systemPrompt/userMessage.
- endpoint(): reads config.baseUrl().
- apiKey(): reads config.apiKeyEnv(), applies environment.
- apiKeyFor(String): parses endpoint via new URL(...).
- errorMessage(String, int, String) and timings(String, ChatResponse, double) take call results.

#### Output
- requestBody -> String JSON body; endpoint -> String URL; apiKey / apiKeyFor -> @Nullable String; isLoopbackHost / isSuccess -> boolean; errorMessage -> String; timings -> AiGenerationTimings.
- No mutated fields, no file or stream writes, no side effects beyond returned values.

#### Core logic
- requestBody builds an ObjectNode: adds model only when non-blank; adds system and user messages from promptSupport; always writes max_tokens, temperature, top_p, repetitionitionpenalty, stream=false; writes top_k/min_p only when positive, seed only when >= 0, stop array only when non-empty, reasoning.effort only when non-blank; serializes with static MAPPER.
- endpoint trims baseUrl, rejects any scheme other than http:// or https:// via IllegalArgumentException, strips trailing slashes, appends CHAT_COMPLETIONS_PATH ("/chat/completions").
- apiKey returns null when apiKeyEnv is blank, env var unset, or value blank; otherwise returns trimmed key.
- apiKeyFor returns the key unchanged unless endpoint is plain http and the extracted host is not loopback — then throws IOException before any transmission; https and loopback http are allowed.
- isLoopbackHost: exact match "localhost", regex IPV4_LOOPBACK (127.x.x.x), or "[::1]".
- isSuccess: 200 <= status < 300 (HTTP_OK_FIRST..HTTP_OK_END).
- errorMessage clips body at ERROR_BODY_EXCERPT (500) chars, appends "...", formats "endpoint answered HTTP status: excerpt".
- timings: decodeRate = completionTokens / seconds when seconds > 0 else 0.0; prefill rate hardcoded 0.0 (treated as unknown); builds AiGenerationTimings from Usage counts.

#### Public API
- No public or protected members. Package-private surface (all 8 methods, searchable):
- requestBody(AiGenerationRequest) -> String — serializes chat-completions JSON body.
- endpoint() -> String — builds chat-completions URL.
- apiKey() -> @Nullable String — env lookup, trimmed or null.
- apiKeyFor(String endpoint) -> @Nullable String — key gated by transport safety.
- isLoopbackHost(String) -> boolean — detects local interfaces.
- isSuccess(int) -> boolean — true for 2xx.
- errorMessage(String, int, String) -> String — formats failure text.
- timings(String, ChatResponse, double) -> AiGenerationTimings — derives rates from usage.

#### Dependencies
- com.fasterxml.jackson.databind.ObjectMapper, ObjectNode, ArrayNode; java.io.IOException; java.net.URL; java.util.List, Objects; java.util.function.Function; java.util.regex.Pattern; lombok.ToString; org.jspecify.annotations.Nullable.
- net.ladenthin.llama.value.ChatResponse, Usage; net.ladenthin.srcmorph.document.AiGenerationRequest; net.ladenthin.srcmorph.prompt.AiPromptSupport; net.ladenthin.srcmorph.support.Java8CompatibilityHelper; net.ladenthin.srcmorph.provider.LlamaCppJniConfig, AiGenerationTimings, OpenAiCompatibleAiGenerationProvider, LlamaCppJniProviderSupport.

#### Exceptions / Errors
- requestBody throws IOException on Jackson serialization failure.
- endpoint throws IllegalArgumentException when baseUrl is not http(s).
- apiKeyFor throws IOException for cleartext key to non-loopback host, or when endpoint URL parse fails.
- Blank/null handling via Java8CompatibilityHelper.isBlank; constructor guards via Objects.requireNonNull.

#### Concurrency
- Single shared static ObjectMapper MAPPER; Jackson ObjectMapper is thread-safe after configuration, so concurrent requestBody calls are safe.
- Instance is effectively immutable: all fields final, no mutable state, no synchronization needed.
