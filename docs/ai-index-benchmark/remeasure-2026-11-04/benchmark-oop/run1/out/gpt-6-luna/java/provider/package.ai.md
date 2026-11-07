### java/provider
- H: 1.0
- C: 8CDF0C9A
- D: 2026-11-04T07:19:50Z
- T: 2026-11-04T07:23:01Z
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
> Provides configurable AI text generation for source-document requests through mock, local llama.cpp, or OpenAI-compatible backends, with completion cleanup and optional timing metrics.

#### Purpose
- Selects and configures AI generation providers, then exposes a common contract for generating text and optionally reporting timing data.
- Supports local GGUF inference and OpenAI-compatible request preparation, including prompt rendering, response handling, and completion parsing.

#### Responsibilities
- **Provider contract and selection:** `AiGenerationProvider` defines generation, timing, and close operations; `AiGenerationProviderFactory` resolves model-level overrides and selects mock, llama.cpp JNI, or OpenAI-compatible providers.
- **Local inference:** `LlamaCppJniAiGenerationProvider` lazily loads and caches a llama.cpp model, applies runtime settings, runs inference, and reports token/cache metrics.
- **Remote request support:** `OpenAiCompatibleProviderSupport` builds chat-completion JSON, resolves endpoints and API keys, checks HTTP results, and derives timing data.
- **Configuration:** `LlamaCppJniConfig` collects immutable model, sampling, hardware, cache, reasoning, provider, DRY, and stop-string settings through a builder.
- **Completion cleanup:** `AiCompletionParser` removes a leading Gemma-4 thinking block and rejects an unterminated block.

#### Key units
- `AiGenerationProvider` — shared `generate`, `generateWithTimings`, and `close` contract, with default zero-valued timing metrics.
- `AiGenerationProviderFactory` — chooses a provider using the effective run/model provider key; defaults blank keys to mock generation.
- `LlamaCppJniAiGenerationProvider` — performs local inference with lazy model initialization, completion parsing, and timing/cache reporting.
- `LlamaCppJniConfig` / `LlamaCppJniConfig.Builder` — immutable generation and runtime settings plus fluent configuration construction.
- `OpenAiCompatibleProviderSupport` — prepares remote chat-completion requests and handles endpoint, API-key, HTTP-status, and timing support.
- `AiCompletionParser` — strips a leading thinking block from completion text or throws `IOException` if it is not closed.

#### Data flow
- `AiGenerationProviderFactory` resolves the provider key and creates a backend using `LlamaCppJniConfig` and `AiPromptSupport`.
- A provider receives `AiGenerationRequest`; local inference uses prompt support to build chat-template arguments and inference parameters, while OpenAI-compatible support builds JSON with system/user messages and configured sampling options.
- Generated completion text is parsed before return; timing-capable paths package text and available token/timing metrics as `AiGenerationTimings`.
- The provider contract’s default timing method delegates to generation and supplies zero-valued metrics when a backend does not override it.

#### Dependencies
- Project types: `AiGenerationRequest`, `AiGenerationTimings`, `AiPromptSupport`, `AiGenerationConfig`, `LlamaCppJniProviderSupport`, `ChatResponseParser`, `ChatResponse`, and `Usage`.
- Local inference: `net.ladenthin:llama` JNI types including `LlamaModel`, `ModelParameters`, `FlashAttn`, and `ReasoningFormat`.
- Remote request support: Jackson JSON node and mapper types.
- Supporting utilities and annotations: `Java8CompatibilityHelper`, `IOException`, Lombok, and JSpecify `Nullable`.

#### Cross-cutting
- `LlamaCppJniConfig` is immutable; its builder is not thread-safe, and list accessors expose unmodifiable views.
- Local model initialization and cached access are unsynchronized; concurrent first generation calls may race, and inference/close are not synchronized.
- Local inference loads the model lazily and closes it only if initialized.
- OpenAI-compatible API keys are withheld from non-loopback HTTP endpoints; HTTPS and HTTP loopback endpoints are permitted.
- Missing generation timing data yields zero metrics; the default provider timing implementation also reports zero values.
