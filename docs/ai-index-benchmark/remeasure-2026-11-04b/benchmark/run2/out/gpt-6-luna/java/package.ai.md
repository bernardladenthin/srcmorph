### java
- H: 1.0
- C: 643999B1
- D: 2026-11-04T11:56:04Z
- T: 2026-11-04T11:59:44Z
- G: 
- A: 0.0.0
- X: package
- F: [config/](config/package.ai.md)
- F: [provider/](provider/package.ai.md)
---
> Configures source analysis and turns selected source files into AI-generated documentation using local or OpenAI-compatible text-generation backends.

#### Purpose
- Defines source-selection, generation, and metadata settings for indexed source documentation.
- Selects AI completion providers and supports response cleanup, timing, and lifecycle management.

#### Responsibilities
- **Configuration and source selection:** `SrcMorphConfiguration`, `AiGenerationConfig`, condition evaluation, oversize policies, and fact-count formatting.
- **Provider selection and local inference:** provider contract and factory, mock fallback, and lazy llama.cpp model execution.
- **Remote generation support:** OpenAI-compatible chat-request construction, endpoint and API-key handling, error formatting, and timing metrics.
- **Completion normalization:** trims generated text and removes a leading Gemma-4 thinking block.

#### Key units
- `SrcMorphConfiguration` — stores run-wide source rules, generation definitions, fallback settings, and documentation metadata.
- `AiGenerationConfig` — holds per-model inference, sampling, provider, caching, hardware, reasoning, and stopping settings.
- `AiConditionEvaluator` — validates and evaluates file conditions using `AiFileContext`.
- `AiFactExtractor` — validates fact-counter definitions and formats regex match counts from source text.
- `AiOversizeStrategy` — represents `FAIL`, `SAMPLE`, `MAP_REDUCE`, and `DETERMINISTIC` policies.
- `AiGenerationProvider` — defines completion generation, optional timing reporting, and close behavior.
- `AiGenerationProviderFactory` — resolves model-level or run-level provider settings and creates the selected backend.
- `LlamaCppJniAiGenerationProvider` — runs local llama.cpp inference and exposes generation timings.
- `LlamaCppJniConfig` — immutable local-generation settings constructed through a fluent builder.
- `OpenAiCompatibleProviderSupport` — prepares remote chat-completions requests and handles endpoint, API-key, error, and timing support.
- `AiCompletionParser` — trims completion text and removes a leading thinking block.

#### Data flow
- Run and model settings populate `SrcMorphConfiguration` and `AiGenerationConfig`; source facts are checked against validated conditions, with path globs evaluated through `AiSourceExcludeFilter`.
- Fact-counter patterns match full source text and produce a labeled facts block.
- The provider factory selects a backend from the model provider key or run-level key, defaulting to mock when selection is absent or blank.
- Providers consume `AiGenerationRequest`: local inference uses `LlamaCppJniProviderSupport` and parses its response; remote support builds a chat request from configured options and prompt messages.
- Local completion text passes through `AiCompletionParser`; providers may also report generation timing metrics.

#### Dependencies
- Configuration and domain types include `AiPromptDefinition`, `AiModelDefinition`, `AiFieldGenerationConfig`, `AiFactDefinition`, `AiCalibration`, `AiCondition`, `AiRangeCondition`, `AiFileContext`, and `AiFactCounter`.
- Provider collaboration includes `AiGenerationRequest`, `AiGenerationTimings`, `AiPromptSupport`, `ChatResponseParser`, and `LlamaCppJniProviderSupport`.
- Local inference uses `LlamaModel`, `ModelParameters`, `FlashAttn`, and `ReasoningFormat`; remote support uses Jackson JSON nodes and `ChatResponse` usage data.
- Java collections, regex and time APIs, `Java8CompatibilityHelper`, JSpecify `Nullable`, and Lombok annotations support configuration and provider code.

#### Cross-cutting
- Configuration holders are generally mutable JavaBeans without synchronization; `LlamaCppJniConfig` is immutable, while its builder is mutable and not thread-safe.
- Condition and fact configuration receive explicit validation; most configuration holders do not validate ranges.
- Local model creation is lazy and cached, with no declared thread-safety guarantee; missing timing data yields zero metrics.
- OpenAI-compatible API keys are trimmed and are not sent to non-loopback hosts; endpoints must use HTTP or HTTPS.
- `AiGenerationProvider` extends `AutoCloseable`, with default no-op close and zero-valued timing behavior; an unclosed leading thinking block causes `AiCompletionParser` to throw `IOException`.
