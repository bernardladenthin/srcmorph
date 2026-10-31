### OpenAiCompatibleProviderSupport.java
- H: 1.0
- C: 92F0B02B
- D: 2026-10-31T10:13:39Z
- T: 2026-10-31T10:15:47Z
- G: 
- A: 0.0.0
- X: file
---
> Builds OpenAI-compatible chat-completion requests and safely resolves API keys for AI code generation.
#### Purpose
- Encapsulates network-free logic for `OpenAiCompatibleAiGenerationProvider`: request body, endpoint, key lookup, status check, errors, timings.
- Keeps 100% PIT-testable logic separate from socket handling in provider.
#### Type
- final class `OpenAiCompatibleProviderSupport`, package-private, `@ToString` with `@ToString.Exclude` on `promptSupport`, `environment`.
#### Input
- Constructor: `LlamaCppJniConfig config` with `remoteModel`, `baseUrl`, `apiKeyEnv`, `maxOutputTokens`, `temperature`, `topP`, `topK`, `minP`, `repeatPenalty`, `seed`, `stopStrings`, `reasoningEffort`.
- Constructor: `AiPromptSupport promptSupport` for `systemPrompt`, `userMessage`; `Function<String,String> environment` for env lookup.
- Method inputs: `AiGenerationRequest request` with `promptKey`, `sourceFile`, `sourceText`; `endpoint` URL string; `status` int; `body` error string; `text`, `ChatResponse response`, `seconds` for timings.
- Read resources: environment variables via `environment.apply`, `URL.getHost()` parsing.
#### Output
- Returns: JSON request body string, chat-completions endpoint URL, trimmed API key or null, error message string, `AiGenerationTimings`.
- Produced state: Jackson `ObjectNode`/`ArrayNode` with `model`, `messages`, `max_tokens`, `temperature`, `top_p`, `top_k`, `min_p`, `repetition_penalty`, `seed`, `stop`, `reasoning.effort`, `stream=false`.
#### Core logic
- `requestBody` builds two-message `system`+`user` chat payload via `AiPromptSupport`, conditionally emits `model`, `top_k`, `min_p`, `seed>=0`, non-empty `stop`, non-blank `reasoning.effort`.
- `endpoint` validates `baseUrl` starts with `http://`/`https://`, trims, strips trailing `/`, appends `/chat/completions`.
- `apiKey` returns null when `apiKeyEnv` blank/unset/blank, else trimmed key.
- `apiKeyFor` allows key over `https` or plain `http` to loopback only; blocks cleartext key to remote host.
- `isLoopbackHost` matches `localhost` exact, `127.x.x.x` via `IPV4_LOOPBACK` Pattern, `[::1]`.
- `isSuccess` checks 200-299 via `HTTP_OK_FIRST`/`HTTP_OK_END`.
- `errorMessage` formats `endpoint + HTTP status + body` truncated to `ERROR_BODY_EXCERPT=500` plus `...`.
- `timings` derives `AiGenerationTimings` from `ChatResponse.getUsage` `promptTokens`/`completionTokens`/`cachedTokens`, `decodeRate=completionTokens/seconds`, prefill rate zero.
#### Public API
- `OpenAiCompatibleProviderSupport(config,promptSupport,environment)` -> ctor creates support for one model.
- `requestBody(request) -> String` builds chat-completions JSON body.
- `endpoint() -> String` resolves chat-completions URL.
- `apiKey() -> String` looks up trimmed env key.
- `apiKeyFor(endpoint) -> String` enforces loopback/https key policy.
- `isLoopbackHost(host) -> boolean` checks local-machine host.
- `isSuccess(status) -> boolean` tests 2xx status.
- `errorMessage(endpoint,status,body) -> String` formats non-2xx failure.
- `timings(text,response,seconds) -> AiGenerationTimings` computes token throughput.
#### Dependencies
- `com.fasterxml.jackson.databind.ObjectMapper`, `node.ArrayNode`, `node.ObjectNode`.
- `java.io.IOException`, `java.net.URL`, `java.util.List`, `java.util.Objects`, `java.util.function.Function`, `java.util.regex.Pattern`.
- `lombok.ToString`, `net.ladenthin.llama.value.ChatResponse`, `net.ladenthin.llama.value.Usage`, `net.ladenthin.srcmorph.document.AiGenerationRequest`, `net.ladenthin.srcmorph.prompt.AiPromptSupport`, `net.ladenthin.srcmorph.support.Java8CompatibilityHelper`, `org.jspecify.annotations.Nullable`, `LlamaCppJniConfig`, `LlamaCppJniProviderSupport`, `OpenAiCompatibleAiGenerationProvider`, `AiGenerationTimings`.
#### Exceptions / Errors
- `IllegalArgumentException` when `baseUrl` not `http(s)` URL.
- `IOException` when `apiKeyFor` refuses cleartext key to non-loopback `http` host or endpoint unparsable; when `requestBody` serialization fails.
- Null-handling: `Objects.requireNonNull` on `config`, `promptSupport`, `environment`; `@Nullable` key returns; blank checks via `Java8CompatibilityHelper.isBlank`.
