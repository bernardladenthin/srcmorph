### OpenAiCompatibleProviderSupport.java
- H: 1.0
- C: 92F0B02B
- D: 2026-10-31T15:20:26Z
- T: 2026-10-31T15:22:42Z
- G: 
- A: 0.0.0
- X: file
---
> Builds OpenAI chat-completion requests and safely routes API keys for remote LLM code generation.
#### Purpose
- Offline helper for OpenAiCompatibleAiGenerationProvider: request body, endpoint, key lookup, status check, error text, timings.
- PIT-gated pure logic split from socket handling, mirroring LlamaCppJniProviderSupport.
#### Type
- final class OpenAiCompatibleProviderSupport, package-private.
- Annotations: @ToString with @ToString.Exclude on promptSupport, environment.
#### Input
- Constructor: LlamaCppJniConfig config (remoteModel, baseUrl, apiKeyEnv, maxOutputTokens, temperature, topP, topK, minP, repeatPenalty, seed, stopStrings, reasoningEffort), AiPromptSupport promptSupport, Function<String, @Nullable String> environment.
- AiGenerationRequest request (promptKey, sourceFile, sourceText) in requestBody.
- String endpoint in apiKeyFor, String host in isLoopbackHost, int status in isSuccess, String endpoint/status/body in errorMessage, String text/ChatResponse response/double seconds in timings.
#### Output
- JSON String chat-completions body via ObjectMapper/ArrayNode/ObjectNode with system/user messages, sampling knobs, stream false.
- String endpoint: trimmed baseUrl without trailing slashes + CHAT_COMPLETIONS_PATH.
- @Nullable String apiKey / apiKeyFor: trimmed env key or null.
- AiGenerationTimings from Usage promptTokens/cachedTokens/completionTokens and decode rate.
- boolean isLoopbackHost, boolean isSuccess; String errorMessage.
#### Core logic
- requestBody serializes model only when non-blank, always max_tokens/temperature/top_p/repetition_penalty/stream false, conditionally top_k when >0, min_p when >0.0f, seed when >=0, stop array when non-empty, reasoning.effort when non-blank.
- endpoint enforces http:// or https:// prefix, strips trailing slashes, appends /chat/completions.
- apiKey returns null when apiKeyEnv blank/unset/blank, else trimmed key.
- apiKeyFor allows key over https or http-to-loopback only; parses URL host and checks isLoopbackHost.
- isLoopbackHost matches localhost exactly, 127.x.x.x via IPV4_LOOPBACK Pattern, [::1] via IPV6_LOOPBACK.
- isSuccess checks 200 <= status < 300 via HTTP_OK_FIRST/HTTP_OK_END.
- errorMessage truncates body to ERROR_BODY_EXCERPT 500 chars + ... ELLIPSIS.
- timings computes decodeRate = completionTokens/seconds (0 when seconds<=0), prefill rate 0.0d unknown.
#### Dependencies
- com.fasterxml.jackson.databind.ObjectMapper, node.ArrayNode, node.ObjectNode.
- java.io.IOException, java.net.URL, java.util.List, java.util.Objects, java.util.function.Function, java.util.regex.Pattern.
- lombok.ToString, org.jspecify.annotations.Nullable.
- net.ladenthin.llama.value.ChatResponse, net.ladenthin.llama.value.Usage, net.ladenthin.srcmorph.document.AiGenerationRequest, net.ladenthin.srcmorph.prompt.AiPromptSupport, net.ladenthin.srcmorph.support.Java8CompatibilityHelper, LlamaCppJniConfig, AiGenerationTimings.
#### Exceptions / Errors
- IllegalArgumentException when baseUrl not http(s) in endpoint.
- IOException when cleartext http key to non-loopback host or URL parse fails in apiKeyFor; when serialization fails in requestBody.
- NullPointerException via Objects.requireNonNull on null config/promptSupport/environment.
