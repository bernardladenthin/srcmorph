### java
- H: 1.0
- C: CDD4BECC
- D: 2026-11-01T01:13:43Z
- T: 2026-11-01T01:16:47Z
- G: 
- A: 0.0.0
- X: package
- F: [config/](config/package.ai.md)
- F: [provider/](provider/package.ai.md)
---
> Configures source-to-documentation generation and routes requests through local or OpenAI-compatible AI text-generation backends.

#### Purpose
- Collects project and model settings for source selection, prompts, facts, output, and generation.
- Selects and runs AI providers, with support for local llama.cpp inference and OpenAI-compatible request preparation.

#### Responsibilities
- **Configuration and selection:** Defines run-level and per-model settings; validates and evaluates file metadata conditions and path exclusions.
- **Facts and oversized files:** Counts configured regex matches and formats source facts; parses oversized-file policies (`FAIL`, `SAMPLE`, `MAP_REDUCE`, `DETERMINISTIC`).
- **Provider routing:** Resolves effective provider settings and selects mock, `llamacpp-jni`, or `openai-compatible` generation.
- **Generation and completion handling:** Runs local inference, prepares remote chat-completion request data, supplies timing helpers, and cleans leading reasoning blocks from output.

#### Key units
- `SrcMorphConfiguration` — run-level source-selection, AI-definition, output, fallback, and version settings.
- `AiGenerationConfig` — per-model inference, prompt, provider, hardware, and sampling configuration.
- `AiConditionEvaluator` — validates condition trees and matches `AiFileContext` metadata.
- `AiFactExtractor` — validates `AiFactCounter` regexes and formats whole-source match counts.
- `AiOversizeStrategy` — represents and parses oversized-file handling policies.
- `AiGenerationProvider` — provider contract for text generation, timing, and resource closure.
- `AiGenerationProviderFactory` — selects a provider from effective configuration, defaulting to mock when selection is absent or blank.
- `LlamaCppJniAiGenerationProvider` — performs local llama.cpp inference and parses model output and timing.
- `LlamaCppJniConfig` — immutable local-provider and model settings built through a fluent builder.
- `OpenAiCompatibleProviderSupport` — builds chat request JSON and resolves endpoints, API keys, errors, and timing calculations.
- `AiCompletionParser` — removes a leading reasoning block and reports an unclosed block as `IOException`.

#### Data flow
- `SrcMorphConfiguration` and `AiGenerationConfig` carry run and model settings into the broader generation flow; `AiConditionEvaluator` applies selection conditions to `AiFileContext`.
- `AiFactExtractor` applies configured counters to source text and returns a formatted facts block; `AiOversizeStrategy` converts configured policy text into a strategy.
- `AiGenerationProviderFactory` resolves the provider; implementations consume `AiGenerationRequest` and produce text, with timing data available where supported.
- Local generation builds llama.cpp inference parameters and parses the response; remote support prepares OpenAI-compatible request data and provides response-related helpers.
- Completion text may pass through `AiCompletionParser`; missing local timing data is represented with zero-valued timings.

#### Dependencies
- Configuration references `AiCondition`, `AiConditionGroup`, `AiRangeCondition`, `AiFileContext`, `AiSourceExcludeFilter`, `AiFactCounter`, `AiPromptDefinition`, `AiModelDefinition`, `AiFieldGenerationConfig`, `AiFactDefinition`, and `AiCalibration`.
- Providers use `AiGenerationRequest`, `AiPromptSupport`, `AiGenerationTimings`, `AiGenerationConfig`, `LlamaCppJniProviderSupport`, and llama JNI types.
- OpenAI-compatible support uses Jackson JSON nodes and `ChatResponse`/`Usage`; the summarized support code prepares requests but does not perform network I/O.
- `Java8CompatibilityHelper` is used by provider selection and provider support.

#### Cross-cutting
- Configuration types are generally mutable JavaBeans without synchronization guarantees; validation is concentrated in condition and fact extraction logic.
- Invalid configuration values can raise `IllegalArgumentException`; provider failures use `IOException` where declared, and unsupported provider keys raise `IllegalArgumentException`.
- Local model initialization and close are unsynchronized and may race under concurrent use; the `LlamaCppJniConfig` builder is not thread-safe.
