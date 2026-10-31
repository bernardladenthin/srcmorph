### java/provider
- H: 1.0
- C: F745AFD2
- D: 2026-10-31T10:13:39Z
- T: 2026-10-31T10:17:52Z
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
> Pluggable AI text-generation backends that produce source-file headers via local llama.cpp or OpenAI-compatible servers with thinking-block cleanup.

#### Purpose
- Provide selectable AI generation backends for source-code morphing, from request to cleaned completion text plus timings.

#### Responsibilities
- Provider contract and selection:
  - `AiGenerationProvider` interface with `generate` / `generateWithTimings` / `close`.
  - `AiGenerationProviderFactory` resolves `PROVIDER_MOCK` / `PROVIDER_LLAMACPP_JNI` / `PROVIDER_OPENAI_COMPATIBLE` via `effectiveProvider`.
- Local inference execution:
  - `LlamaCppJniAiGenerationProvider` lazily loads `LlamaModel` from GGUF and runs `chatComplete`.
  - `LlamaCppJniConfig` immutable 41-field value object plus `Builder` for sampling, KV-cache, GPU-offload, reasoning, DRY, remote-provider settings.
- Completion post-processing and remote request building:
  - `AiCompletionParser` strips leading Gemma-4 `<|channel>thought` … `<channel|>` block.
  - `OpenAiCompatibleProviderSupport` builds chat-completions JSON, endpoint URL, API-key lookup, status/error and `AiGenerationTimings`.

#### Key units
- `AiGenerationProvider` — backend contract for `AiGenerationRequest` to text plus timings.
- `AiGenerationProviderFactory` — instantiates `MockAiGenerationProvider`, `LlamaCppJniAiGenerationProvider`, `OpenAiCompatibleAiGenerationProvider`.
- `LlamaCppJniAiGenerationProvider` — local GGUF inference with lazy `LlamaModel`, `ReasoningFormat.AUTO`, cache-reuse diagnostics.
- `LlamaCppJniConfig` — immutable inference config with `builder(modelPath)`, record-style accessors, `build()`.
- `AiCompletionParser` — `parseCompletion` thinking-block stripper with `THINKING_BLOCK_START_MARKER` / `THINKING_BLOCK_END_MARKER`.
- `OpenAiCompatibleProviderSupport` — `requestBody`, `endpoint`, `apiKey` / `apiKeyFor`, `isLoopbackHost`, `isSuccess`, `errorMessage`, `timings`.

#### Data flow
- `AiGenerationProviderFactory.create` resolves model-over-run provider key, constructs selected provider with `LlamaCppJniConfig` and `AiPromptSupport`.
- `generate` / `generateWithTimings` builds system+user prompt via `AiPromptSupport`, calls `LlamaModel.chatComplete` or remote chat-completions endpoint, parses via `ChatResponseParser` then `AiCompletionParser.parseCompletion`.
- Timings from native `Timings` or `ChatResponse.getUsage` wrapped as `AiGenerationTimings`; `close` releases native model handle.

#### Dependencies
- Internal: `AiGenerationRequest`, `AiPromptSupport`, `AiGenerationConfig`, `AiGenerationTimings`, `LlamaCppJniProviderSupport`, `MockAiGenerationProvider`, `OpenAiCompatibleAiGenerationProvider`, `Java8CompatibilityHelper`.
- External: `net.ladenthin.llama` — `LlamaModel`, `ModelParameters`, `FlashAttn`, `ReasoningFormat`, `ChatResponseParser`, `ChatResponse`, `Timings`, `Usage`; `com.fasterxml.jackson.databind` `ObjectMapper` / `ObjectNode` / `ArrayNode`; `lombok.ToString` / `EqualsAndHashCode`; `org.jspecify.annotations.Nullable`.

#### Cross-cutting
- Lombok `@ToString` on all types, with `@ToString.Exclude` for model/support/environment handles.
- Immutable config vs mutable single-use `Builder`; `Collections.unmodifiableList` for `drySequenceBreakers`, `stopStrings`.
- Error handling: `IOException` for truncated thinking block, provider failure, insecure cleartext API key; `IllegalArgumentException` for bad `baseUrl` or unknown provider; blank/null fallback to mock provider.
- Timing-aware calibration via `generateWithTimings`, zero-rate fallback when native timings missing.
- Threading note: lazy `model` init unsynchronized non-volatile, not safe for concurrent first use; `LlamaCppJniConfig` thread-safe after publication, `Builder` not thread-safe.
