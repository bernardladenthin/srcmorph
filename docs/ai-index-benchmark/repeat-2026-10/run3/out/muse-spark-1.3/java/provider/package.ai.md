### java/provider
- H: 1.0
- C: 50D80C65
- D: 2026-10-31T15:20:26Z
- T: 2026-10-31T15:24:51Z
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
> Local and remote LLM backends that generate source-file header text with timing, parsing, and configuration.

#### Purpose
- Pluggable AI text-generation layer for srcmorph header generation with calibration timing and clean shutdown.

#### Responsibilities
- Provider contract: `AiGenerationProvider` defines `generate` / `generateWithTimings` / `close` over `AiGenerationRequest` to `String` / `AiGenerationTimings`.
- Backend selection: `AiGenerationProviderFactory` resolves `effectiveProvider` (model-over-run override) and instantiates mock vs `llamacpp-jni` vs `openai-compatible` providers.
- Local inference: `LlamaCppJniAiGenerationProvider` lazily loads GGUF `LlamaModel`, builds `ModelParameters`, runs `chatComplete`, parses via `ChatResponseParser` + `AiCompletionParser`.
- Remote request building: `OpenAiCompatibleProviderSupport` builds chat-completions JSON body, endpoint URL, API-key lookup/routing, success/error and `Usage`-based timings.
- Configuration snapshot: `LlamaCppJniConfig` + `Builder` holds 41-field immutable sampling, KV-cache, batch, GPU/device, DRY, reasoning, remote-provider settings.
- Output cleaning: `AiCompletionParser` strips leading Gemma-4 `<|channel>thought` … `<channel|>` thinking block to storable answer.

#### Key units
- `AiGenerationProvider`: generation interface extending `AutoCloseable`.
- `AiGenerationProviderFactory`: `create`, `effectiveProvider` dispatch with `PROVIDER_MOCK` / `PROVIDER_LLAMACPP_JNI` / `PROVIDER_OPENAI_COMPATIBLE`.
- `LlamaCppJniAiGenerationProvider`: local GGUF execution, `model()`, `chatResponse`, `generate`, `generateWithTimings`, `close`.
- `LlamaCppJniConfig`: immutable config with `builder(modelPath)`, accessors for `modelPath`, `contextSize`, `maxOutputTokens`, `temperature`, `topP/topK/minP/topNSigma`, `repeatPenalty`, `seed`, `cachePrompt`, `swaFull`, `cacheReuse`, `gpuLayers`, `cpuMoeLayers/cpuFfnLayers`, `kvUnifiedPerSlot`, `lazyMode`, `cacheTypeK/cacheTypeV`, `flashAttn`, `batchSize/ubatchSize/threadsBatch`, `mainGpu/devices`, `provider/remoteModel/baseUrl/apiKeyEnv`, `reasoningEffort/reasoningBudgetTokens`, `dryMultiplier/dryBase/dryAllowedLength/dryPenaltyLastN/drySequenceBreakers`, `stopStrings`, `chatTemplateEnableThinking`.
- `AiCompletionParser`: `parseCompletion`, `THINKING_BLOCK_START_MARKER`, `THINKING_BLOCK_END_MARKER`.
- `OpenAiCompatibleProviderSupport`: `requestBody`, `endpoint`, `apiKey/apiKeyFor`, `isLoopbackHost/isSuccess`, `errorMessage`, `timings`.

#### Data flow
- `AiGenerationRequest` → `AiGenerationProviderFactory.create` → selected `AiGenerationProvider` → `AiPromptSupport.buildInferenceParameters` / `buildChatTemplateKwargs` or `requestBody` → `LlamaModel.chatComplete` or remote chat-completions endpoint → `ChatResponseParser.parseResponse` → `AiCompletionParser.parseCompletion` → `String` + `AiGenerationTimings` with truncation/cache logging.

#### Dependencies
- Internal: `AiGenerationRequest`, `AiGenerationTimings`, `AiPromptSupport`, `AiGenerationConfig.DEFAULT_*`, `LlamaCppJniProviderSupport`, `MockAiGenerationProvider`, `OpenAiCompatibleAiGenerationProvider`, `Java8CompatibilityHelper`, `ConvertToRecord`.
- External: `net.ladenthin.llama` — `LlamaModel`, `ModelParameters`, `FlashAttn`, `ReasoningFormat`, `ChatResponseParser`, `ChatResponse`, `Timings`, `Usage`; `com.fasterxml.jackson.databind` `ObjectMapper/ArrayNode/ObjectNode`; `lombok.ToString/EqualsAndHashCode`; `org.jspecify.annotations.Nullable`.

#### Cross-cutting
- Lombok `@ToString` with `@ToString.Exclude` on model/support handles; `@EqualsAndHashCode` on config.
- Null/blank-tolerant config and factory: null String/List setters coalesce to defaults, blank provider defaults to mock, null completion to empty string.
- Error signaling via `IOException` for truncated thinking block, provider failure, insecure cleartext key, serialization failure; `IllegalArgumentException` for unknown provider and non-http(s) baseUrl.
- Secure key routing: API key sent only over https or http-to-loopback (`localhost`, `127.x`, `[::1]`).
- Concurrency: `LlamaCppJniConfig` immutable shareable, `Builder` single-threaded; lazy `LlamaModel` init unsynchronized non-thread-safe.
