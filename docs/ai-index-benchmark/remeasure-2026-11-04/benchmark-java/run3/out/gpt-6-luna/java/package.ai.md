### java
- H: 1.0
- C: 643999B1
- D: 2026-11-04T07:43:16Z
- T: 2026-11-04T07:46:29Z
- G: 
- A: 0.0.0
- X: package
- F: [config/](config/package.ai.md)
- F: [provider/](provider/package.ai.md)
---
> Selects and prepares source material for AI documentation, then generates text through configurable local or OpenAI-compatible model backends.

#### Purpose
- Define source-selection and generation settings for AI documentation runs.
- Route generation requests to supported providers and normalize completion output.

#### Responsibilities
- **Source selection and prompt preparation:** Evaluate file-metadata conditions, extract configured regex-based source facts, and represent policies for oversized inputs.
- **Run and model configuration:** Store source scope, output and documentation metadata, prompts, model and field-routing settings, and generation parameters.
- **Generation backends:** Select mock, llama.cpp JNI, or OpenAI-compatible providers; construct requests and return completion text with timing data where available.
- **Response and transport handling:** Parse local-model responses, clean leading Gemma 4 thinking blocks, and support OpenAI-compatible endpoint, API-key, HTTP-error, and usage handling.

#### Key units
- `SrcMorphConfiguration` — stores run-wide source selection, routing, fallback-model, output, and documentation metadata settings.
- `AiConditionEvaluator` — validates and evaluates `AiCondition` trees against `AiFileContext`, and identifies line-count requirements.
- `AiFactExtractor` — validates fact-counter definitions and formats regex counts from source text.
- `AiGenerationConfig` — stores model, sampling, reasoning, hardware, caching, provider, and stop-behavior settings.
- `AiOversizeStrategy` — represents `FAIL`, `SAMPLE`, `MAP_REDUCE`, and `DETERMINISTIC` oversized-file policies.
- `AiGenerationProvider` — defines generation, optional timing results, and resource cleanup.
- `AiGenerationProviderFactory` — selects a mock, llama.cpp JNI, or OpenAI-compatible backend from effective provider settings.
- `LlamaCppJniConfig` — holds immutable local-inference and compatible-provider settings, constructed through a fluent builder.
- `LlamaCppJniAiGenerationProvider` — performs local llama.cpp JNI inference and exposes response text and native timing metrics.
- `AiCompletionParser` — removes an initial Gemma 4 thinking block while preserving answer text.
- `OpenAiCompatibleProviderSupport` — provides request construction and endpoint, API-key, HTTP-error, and timing utilities.

#### Data flow
- File metadata and an `AiCondition` tree pass to `AiConditionEvaluator` for validation and matching; `usesLines` signals when source contents are needed for line counts.
- Source text and `AiFactCounter` entries pass to `AiFactExtractor`, which formats exact counts for prompt facts.
- `SrcMorphConfiguration` supplies source scope and routing definitions; generation settings configure the step and oversized-input policy.
- Provider settings flow through `AiGenerationProviderFactory`; the selected backend consumes `AiGenerationRequest` and `AiPromptSupport` to produce text and, where supported, timing data.
- The llama.cpp backend builds inference parameters, invokes chat completion, and parses and cleans response content; OpenAI-compatible support builds non-streaming chat-completion requests and converts usage and elapsed time to timings.

#### Dependencies
- Internal configuration and domain types include `AiCondition`, `AiFileContext`, `AiFactCounter`, `AiModelDefinition`, `AiFieldGenerationConfig`, `AiPromptDefinition`, `AiGenerationRequest`, and `AiPromptSupport`.
- Generation support includes `AiGenerationTimings`, `ChatResponseParser`, `ChatResponse`, and `Usage`.
- Local inference uses llama.cpp JNI types and `LlamaCppJniProviderSupport`; OpenAI-compatible request serialization uses Jackson and API-key resolution uses a supplied environment lookup function.
- Configuration utilities include Java collections, regex APIs, `Instant`, and `File`; configuration annotations include JSpecify `Nullable` and Lombok `ToString`.

#### Cross-cutting
- Configuration beans are generally mutable and unsynchronized; `LlamaCppJniConfig` is immutable after construction, while its builder is not thread-safe.
- Condition trees and fact counters have explicit validation; generation-setting setters generally do not validate ranges.
- Providers implement `AutoCloseable`; the default timing method delegates to generation with zero timings, while the llama.cpp provider closes its cached native model.
- llama.cpp lazy initialization and close/generation operations are unsynchronized; OpenAI-compatible support restricts API-key transmission to HTTPS or HTTP loopback.
