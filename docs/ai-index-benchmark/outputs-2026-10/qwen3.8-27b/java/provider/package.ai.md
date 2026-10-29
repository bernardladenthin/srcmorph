### java/provider
- H: 1.0
- C: 61221DB3
- D: 2026-10-28T21:24:22Z
- T: 2026-10-28T22:45:34Z
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
> Pluggable AI text-generation backend layer that selects, configures, and executes LLM inference (local GGUF via llama.cpp JNI or remote OpenAI-compatible) to produce code or documentation completions for the srcmorph pipeline.

#### Purpose
- Provides the SPI, factory, configuration, and concrete implementations for AI text generation in srcmorph.
- Supports three backends: mock (default), local llama.cpp JNI (GGUF), and OpenAI-compatible remote endpoints.

#### Responsibilities
- **Provider SPI & factory**: `AiGenerationProvider` interface + `AiGenerationProviderFactory` for key-based instantiation with model-level override over run-level default.
- **Local inference**: `LlamaCppJniAiGenerationProvider` drives GGUF model loading, chat completion, KV-cache reuse, GPU offload, and timing extraction via the `net.ladenthin:llama` JNI binding.
- **Remote inference support**: `OpenAiCompatibleProviderSupport` builds request bodies, resolves endpoints, guards API-key transmission security, and derives timings for OpenAI-compatible HTTP providers.
- **Output parsing**: `AiCompletionParser` strips Gemma-4 chain-of-thought reasoning blocks before the answer is persisted.
- **Configuration**: `LlamaCppJniConfig` is the immutable 40-field value object carrying all sampling, GPU, KV-cache, DRY, and endpoint parameters.

#### Key units
- `AiGenerationProvider` — SPI interface (`generate`, `generateWithTimings`, `close`); all backends implement this.
- `AiGenerationProviderFactory` — resolves effective provider key (model override > run default) and instantiates the matching concrete provider.
- `LlamaCppJniAiGenerationProvider` — final class; lazy-loads `LlamaModel`, executes `chatComplete`, parses via `ChatResponseParser`, returns cleaned text or `AiGenerationTimings`.
- `LlamaCppJniConfig` — immutable builder-pattern config; single source of truth for all inference knobs; defaults from `AiGenerationConfig`.
- `OpenAiCompatibleProviderSupport` — package-private final class; Jackson serialization of chat-completions body, URL construction, loopback-only cleartext-key guard, 2xx status check, timing derivation from `ChatResponse.Usage`.
- `AiCompletionParser` — stateless utility; strips leading `THINKING_BLOCK_START_MARKER`…`THINKING_BLOCK_END_MARKER` span; throws `IOException` on unterminated block.
- `AiGenerationTimings` — value object carrying generated text plus prompt/predicted token counts, cache-hit count, and per-second throughput rates.

#### Data flow
1. Caller invokes `AiGenerationProviderFactory.create(runProvider, llamaConfig, promptSupport)`.
2. Factory resolves effective key via `effectiveProvider(runProvider, llamaConfig.provider())`; instantiates the matching provider.
3. Provider receives `AiGenerationRequest` (prompt key, source file, source text, current header).
4. **Local path**: `LlamaCppJniAiGenerationProvider` lazily builds `ModelParameters` from `LlamaCppJniConfig`, calls `LlamaModel.chatComplete(...)`, parses `ChatResponse` via `ChatResponseParser`, then `AiCompletionParser.parseCompletion` strips thinking blocks.
5. **Remote path**: `OpenAiCompatibleProviderSupport.requestBody` serializes JSON; provider sends HTTP POST to `endpoint()`; response parsed and timings derived from `ChatResponse.Usage`.
6. Output: clean `String` or `AiGenerationTimings` returned to caller.

#### Dependencies
- **Internal**: `AiGenerationRequest` (document), `AiPromptSupport` (prompt), `AiGenerationConfig` (config defaults), `Java8CompatibilityHelper` (support), `AiGenerationTimings` (provider), `LlamaCppJniProviderSupport` (provider).
- **External**: `net.ladenthin:llama` JNI binding (`LlamaModel`, `ModelParameters`, `ChatResponseParser`, `ChatResponse`, `Timings`, `FlashAttn`, `ReasoningFormat`), Jackson (`ObjectMapper`, `ObjectNode`, `ArrayNode`), Lombok, `org.jspecify.annotations`.

#### Cross-cutting
- **Builder pattern**: `LlamaCppJniConfig.Builder` is the sole construction path; all 39 optional fields default to `AiGenerationConfig.DEFAULT_*`.
- **Immutability**: `LlamaCppJniConfig` and `OpenAiCompatibleProviderSupport` are fully immutable (final fields); thread-safe by construction.
- **Lazy initialization**: `LlamaCppJniAiGenerationProvider.model()` uses check-then-act (not thread-safe; assumes single-threaded use per instance).
- **Null-safety**: `AiCompletionParser` handles null input; `LlamaCppJniConfig.Builder` guards null strings/lists by restoring defaults; `OpenAiCompatibleProviderSupport` uses `Objects.requireNonNull` in constructor.
- **Security guard**: `OpenAiCompatibleProviderSupport.apiKeyFor` enforces loopback-only for cleartext HTTP key transmission; HTTPS passes unconditionally.
- **Timing degradation**: Both local and remote paths degrade to zero-rate `AiGenerationTimings` when timing data is absent.
- **Lombok**: `@ToString` on all classes; `@EqualsAndHashCode` on config; `@ConvertToRecord` migration marker on config.
