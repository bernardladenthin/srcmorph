### java
- H: 1.0
- C: 2FB01B39
- D: 2026-10-31T14:30:30Z
- T: 2026-10-31T14:33:58Z
- G: 
- A: 0.0.0
- X: package
- F: [config/](config/package.ai.md)
- F: [provider/](provider/package.ai.md)
---
> Configures source-documentation runs and routes source-aware text generation through local or OpenAI-compatible AI backends.

#### Purpose
- Centralizes source selection, generation settings, model routing, output metadata, and file-handling policies.
- Provides configurable AI provider selection and support for generating and cleaning completion text.

#### Responsibilities
- **Run configuration and filtering:** `SrcMorphConfiguration`, `AiGenerationConfig`, and condition types define source scope, model settings, and file-selection rules; evaluators check file facts such as extension, size, line count, modification time, and path.
- **Facts and oversized files:** `AiFactExtractor` validates regex counters and formats source match counts; `AiOversizeStrategy` resolves configured handling choices.
- **Provider dispatch and local inference:** Provider contracts and the factory select mock, llama.cpp JNI, or OpenAI-compatible backends; the llama.cpp provider builds inference parameters and parses model responses.
- **Compatible API support and cleanup:** Utilities construct chat-completion requests, validate endpoints and API-key transport, derive timing metrics, and strip a leading Gemma-4 reasoning block.

#### Key units
- `SrcMorphConfiguration` — shared run settings for source scope, model routes, outputs, prompts, and generated-document metadata.
- `AiGenerationConfig` — per-model provider, sampling, inference, hardware, caching, reasoning, and stopping options.
- `AiConditionEvaluator` / `AiConditionGroup` — validate and evaluate nested file conditions, with Maven-bindable AND/OR grouping.
- `AiFactExtractor` / `AiOversizeStrategy` — format configured source counts and resolve oversized-file handling modes.
- `AiGenerationProvider` / `AiGenerationProviderFactory` — define generation and resource lifecycle, and select a backend using model- or run-level provider settings.
- `LlamaCppJniAiGenerationProvider` / `LlamaCppJniConfig` — run local inference using immutable model settings built through a mutable builder.
- `OpenAiCompatibleProviderSupport` — build compatible chat requests and centralize endpoint, key, error, and timing handling.
- `AiCompletionParser` — removes a leading Gemma-4 thinking block from completion text.

#### Data flow
- Run and model configuration supplies source-selection, generation, routing, and output settings; file facts are evaluated against configured condition trees.
- Source text and configured regex counters produce a labeled facts block; oversize configuration resolves to a handling strategy.
- Provider settings select a backend; generation requests and model configuration flow into inference or compatible chat requests, and parsed completion text is returned.
- Timing-aware generation packages available token and cache metrics; the llama.cpp path reports zero metrics when response timings are unavailable.

#### Dependencies
- Internal configuration and request types include `AiModelDefinition`, `AiFieldGenerationConfig`, `AiFactDefinition`, `AiPromptDefinition`, `AiCalibration`, `AiFileContext`, `AiGenerationRequest`, `AiGenerationTimings`, and `AiPromptSupport`.
- File-path conditions delegate glob matching to `AiSourceExcludeFilter`.
- Local inference uses `net.ladenthin:llama`; compatible API support uses Jackson, `ChatResponse`, and `Usage`.
- Provider dispatch references `MockAiGenerationProvider` and `OpenAiCompatibleAiGenerationProvider`; their implementations are not described in the supplied summaries.

#### Cross-cutting
- Configuration beans are generally mutable and not synchronized; condition evaluation and fact extraction are stateless.
- Condition trees and fact counters have explicit validation; generation and run-setting beans generally store values without numeric-range validation.
- Nullable options are common; `AiGenerationConfig` exposes unmodifiable list views and resets null list inputs to empty lists.
- Provider implementations may throw `IOException`; the provider interface’s default timing method delegates to generation with zero timing values.
- The llama.cpp provider lazily caches a model without synchronization; concurrent first calls may initialize more than one model.
- Compatible API support centralizes HTTP(S) validation and rejects API keys sent over plain HTTP to non-loopback hosts.
