### OpenAiCompatibleProviderSupport.java
- H: 1.0
- C: 92F0B02B
- D: 2026-10-31T15:13:57Z
- T: 2026-10-31T15:16:19Z
- G: 
- A: 0.0.0
- X: file
---
> Builds OpenAI-compatible chat-completion requests and guards API-key transmission while deriving timings and error messages.
#### Purpose
- Offline helper for OpenAiCompatibleAiGenerationProvider: request body, endpoint, key lookup, status check, timings.
- Keeps 100% PIT-gated pure logic separate from socket handling.
#### Type
- final package-private class OpenAiCompatibleProviderSupport, @ToString.
- Fields: 15 total; 11 static constants/helpers + 4 instance fields.
- Instance fields: LlamaCppJniConfig config, AiPromptSupport promptSupport (@ToString.Exclude), Function<String,@Nullable String> environment (@ToString.Exclude), Java8CompatibilityHelper compatibilityHelper.
#### Input
- LlamaCppJniConfig config: remoteModel, baseUrl, apiKeyEnv, maxOutputTokens, temperature, topP, topK, minP, repeatPenalty, seed, stopStrings, reasoningEffort.
- AiPromptSupport promptSupport: systemPrompt(promptKey), userMessage(sourceFile, sourceText).
- Function<String,@Nullable String> environment: env-var lookup by apiKeyEnv.
- AiGenerationRequest request: promptKey, sourceFile, sourceText.
- String endpoint, int status, String body, ChatResponse response, double seconds, String host/text.
#### Output
- JSON String chat-completions body via ObjectMapper/ArrayNode/ObjectNode.
- String endpoint: trimmed baseUrl + CHAT_COMPLETIONS_PATH.
- @Nullable String apiKey/apiKeyFor: trimmed key or null.
- boolean isLoopbackHost/isSuccess; String errorMessage; AiGenerationTimings timings.
#### Core logic
- requestBody builds system+user messages matching llamacpp-jni provider, always sets model if non-blank, max_tokens, temperature, top_p, repetition_penalty, stream=false.
- requestBody conditionally adds top_k if >0, min_p if >0.0f, seed if >=0, stop array if non-empty, reasoning.effort if non-blank.
- endpoint trims baseUrl, rejects non-http:// and non-https:// schemes, strips trailing slashes, appends /chat/completions.
- apiKey returns null when apiKeyEnv blank/unset/blank, else trimmed key.
- apiKeyFor returns key directly for https or no key; for http parses URL host and allows only loopback else fails.
- isLoopbackHost matches localhost exactly, 127.x.x.x regex, [::1].
- isSuccess checks 200<=status<300; errorMessage truncates body to ERROR_BODY_EXCERPT 500 chars + ... .
- timings extracts Usage prompt/completion/cached tokens, decodeRate=completionTokens/seconds, prefillRate=0.0d unknown.
#### Dependencies
- com.fasterxml.jackson.databind.ObjectMapper, node.ArrayNode, node.ObjectNode.
- java.io.IOException, java.net.URL, java.util.List, java.util.Objects, java.util.function.Function, java.util.regex.Pattern.
- lombok.ToString, org.jspecify.annotations.Nullable.
- net.ladenthin.llama.value.ChatResponse, net.ladenthin.llama.value.Usage.
- net.ladenthin.srcmorph.document.AiGenerationRequest, net.ladenthin.srcmorph.prompt.AiPromptSupport, net.ladenthin.srcmorph.support.Java8CompatibilityHelper.
- LlamaCppJniConfig, AiGenerationTimings, OpenAiCompatibleAiGenerationProvider, LlamaCppJniProviderSupport.
#### Exceptions / Errors
- IllegalArgumentException when baseUrl not http(s) URL in endpoint.
- IOException from MAPPER.writeValueAsString in requestBody, from new URL + cleartext-key refusal in apiKeyFor.
- NullPointerException via Objects.requireNonNull for config/promptSupport/environment.
- Null/blank-tolerant: blank remoteModel/reasoningEffort omitted, blank apiKeyEnv/key yields null key.
#### Concurrency
- No threading/synchronization; shared static final ObjectMapper MAPPER; instance fields final.
