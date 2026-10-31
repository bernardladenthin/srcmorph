### java/provider
- H: 1.0
- C: 96A709F9
- D: 2026-10-31T15:13:57Z
- T: 2026-10-31T15:18:38Z
- G: 
- A: 0.0.0
- X: package
- F: [AiCompletionParser.java](AiCompletionParser.java.ai.md)
- F: [AiGenerationProvider.java](AiGenerationProvider.java.ai.md)
- F: [AiGenerationProviderFactory.java](AiGenerationProviderFactory.java.ai.md)
- F: [LlamaCppJniAiGenerationProvider.java](LlamaCppJniAiGenerationProvider.java.ai.md)
- F: [LlamaCppJniConfig.java](LlamaCppJniConfig.java.ai.md)
- F: [OpenAiCompatibleProviderSupport.java](OpenAiCompatibleProviderSupport.java.ai.md)
---
> Pluggable LLM backends that generate replacement text for source-file headers via local llama.cpp or OpenAI-compatible servers.
#### Purpose
- Define pluggable text-generation contract for `AiGenerationRequest` and route to mock / local / remote backends.
- Isolate pure request-building, response-cleaning, and configuration logic from native/socket execution.
#### Responsibilities
- Provider contract: `AiGenerationProvider` text + timed generation with default no-op `close`.
- Backend selection: `AiGenerationProviderFactory` resolves `effectiveProvider` (model-over-run) and instantiates `mock`, `llamacpp-jni`, `openai-compatible`.
- Local inference: `LlamaCppJniAiGenerationProvider` lazily loads GGUF `LlamaModel`, runs `chatComplete`, logs cache-reuse/truncation, returns `AiGenerationTimings`.
- Generation knobs: `LlamaCppJniConfig` immutable 41-field value object + `Builder` seeded from `AiGenerationConfig.DEFAULT_*`.
- Output cleaning: `AiCompletionParser` strips leading `<|channel>thought ... <channel|>` reasoning block before persistence.
- Remote helper: `OpenAiCompatibleProviderSupport` builds chat-completions JSON, resolves endpoint/API key, checks status, derives timings.
#### Key units
- `AiGenerationProvider`: pluggable backend interface, `generate`, `generateWithTimings`, `close`.
- `AiGenerationProviderFactory`: backend router, `create`, `effectiveProvider`, `PROVIDER_MOCK` / `PROVIDER_LLAMACPP_JNI` / `PROVIDER_OPENAI_COMPATIBLE`.
- `LlamaCppJniAiGenerationProvider`: local GGUF executor backed by `LlamaModel`, `ModelParameters`, `ReasoningFormat.AUTO`.
- `LlamaCppJniConfig` + `Builder`: model/sampling/KV-cache/GPU/DRY/remote fields, `builder(modelPath)`, `build`.
- `AiCompletionParser`: thinking-block stripper, `parseCompletion`, `THINKING_BLOCK_START_MARKER` / `END_MARKER`.
- `OpenAiCompatibleProviderSupport`: offline remote logic, `requestBody`, `endpoint`, `apiKey` / `apiKeyFor`, `isLoopbackHost`, `isSuccess`, `timings`, `errorMessage`.
#### Data flow
- `AiGenerationRequest` (promptKey, sourceFile, sourceText) → `AiGenerationProviderFactory.create` → selected `AiGenerationProvider`.
- Local path: `AiPromptSupport` prompts + `LlamaCppJniProviderSupport` inference params → `LlamaModel.chatComplete` → `ChatResponseParser` → `AiCompletionParser.parseCompletion` → `String` / `AiGenerationTimings` with prompt/cache/predicted counts.
- Remote path: `AiPromptSupport.systemPrompt/userMessage` + `LlamaCppJniConfig` sampling fields → Jackson `ObjectMapper` JSON body → `baseUrl + /chat/completions` → `ChatResponse/Usage` → `AiGenerationTimings`.
#### Dependencies
- Internal: `AiGenerationRequest`, `AiPromptSupport`, `AiGenerationConfig`, `AiGenerationTimings`, `Java8CompatibilityHelper`, `MockAiGenerationProvider`, `OpenAiCompatibleAiGenerationProvider`, `LlamaCppJniProviderSupport`.
- External: `net.ladenthin.llama` — `LlamaModel`, `ModelParameters`, `FlashAttn`, `ReasoningFormat`, `ChatResponseParser`, `ChatResponse`, `Timings`, `Usage`.
- External: Jackson `ObjectMapper/ArrayNode/ObjectNode`, Lombok `@ToString/@EqualsAndHashCode`, JSpecify `@Nullable`, `java.net.URL`, regex `Pattern`.
#### Cross-cutting
- Lombok `@ToString` with `@Exclude` on native handles, prompt support, environment lookup.
- Null/blank-tolerant routing: blank provider → mock; blank `remoteModel/reasoningEffort/apiKeyEnv` omitted; null lists → empty.
- Sentinel conventions: `-1` keep binding default, `0` disable, empty string omit kwarg; tri-state `chatTemplateEnableThinking` defers to template.
- Error handling: `IOException` on provider failure and truncated thinking block; `IllegalArgumentException` on unknown provider or non-http(s) `baseUrl`; cleartext API-key refusal except loopback `localhost/127.x/[::1]`.
- Concurrency: `LlamaCppJniConfig` immutable with unmodifiable list views; `Builder` and lazy `LlamaModel` init unsynchronized/not thread-safe; shared static `ObjectMapper MAPPER`.
