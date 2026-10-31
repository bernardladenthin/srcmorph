### java
- H: 1.0
- C: 9B496A0B
- D: 2026-10-31T14:57:29Z
- T: 2026-10-31T15:00:34Z
- G: 
- A: 0.0.0
- X: package
- F: [config/](config/package.ai.md)
- F: [provider/](provider/package.ai.md)
---
> Configures source selection and generates AI-assisted project-index content through mock, local llama.cpp, or OpenAI-compatible providers.

#### Purpose
- Defines shared settings for choosing source files, routing generation, and shaping AI-generated index content.
- Provides provider selection and backend support for turning generation requests into text, optionally with timing data.

#### Responsibilities
- **Configuration and source selection:** `SrcMorphConfiguration`, `AiGenerationConfig`, condition groups and evaluators, fact extraction, and oversize-file policies.
- **Provider routing and contract:** `AiGenerationProviderFactory` selects a backend; `AiGenerationProvider` defines generation, timing, and closure operations.
- **Generation backends and support:** local llama.cpp JNI inference, shared OpenAI-compatible request utilities, and completion cleanup.

#### Key units
- `SrcMorphConfiguration` — source filters, AI definitions, fallback inference settings, and generated-index metadata.
- `AiGenerationConfig` — provider/model, inference, sampling, prompt, cache, and calibration settings.
- `AiConditionEvaluator` — validates and matches configured conditions against file facts.
- `AiFactExtractor` — validates fact counters and formats regex match counts from source text.
- `AiOversizeStrategy` — parses `FAIL`, `SAMPLE`, `MAP_REDUCE`, or `DETERMINISTIC` routing policy.
- `AiGenerationProvider` — common generation interface with optional timing results and resource closure.
- `AiGenerationProviderFactory` — resolves model-level and run-level provider keys, defaulting to mock generation.
- `LlamaCppJniAiGenerationProvider` — runs local llama.cpp inference and exposes available timing and cache metrics.
- `OpenAiCompatibleProviderSupport` — builds chat-completions JSON and handles shared endpoint, API-key, HTTP-error, and timing utilities.
- `LlamaCppJniConfig` — immutable local inference settings assembled through a builder.
- `AiCompletionParser` — removes leading Gemma-4 reasoning blocks and reports unclosed blocks.

#### Data flow
- `SrcMorphConfiguration` supplies source and generation definitions; `AiConditionEvaluator` checks file facts, delegating path-glob matching to `AiSourceExcludeFilter`.
- `AiFactExtractor` validates counters and formats facts from complete source text.
- `AiGenerationProviderFactory` resolves provider configuration, then the selected provider consumes `AiGenerationRequest` and returns generated text, optionally with `AiGenerationTimings`.
- Local inference builds llama parameters, runs chat completion, and parses the response; OpenAI-compatible support builds remote chat-completions requests.

#### Dependencies
- Configuration uses `AiPromptDefinition`, `AiModelDefinition`, `AiFieldGenerationConfig`, `AiFactDefinition`, `AiCalibration`, `AiCondition`, `AiRangeCondition`, `AiFileContext`, and `AiFactCounter`.
- Provider components use `AiGenerationRequest`, `AiGenerationTimings`, `AiGenerationConfig`, `AiPromptSupport`, `MockAiGenerationProvider`, and `Java8CompatibilityHelper`.
- Local inference depends on the `net.ladenthin:llama` JNI binding and `LlamaCppJniProviderSupport`; remote request support uses Jackson and environment-variable lookup for API keys.
- The factory references `OpenAiCompatibleAiGenerationProvider`; its implementation is not described in the supplied summaries.

#### Cross-cutting
- Configuration classes are mutable JavaBeans; `LlamaCppJniConfig` is immutable after construction. Thread safety is not established for mutable config, the builder, or local model initialization and closure.
- Invalid condition or fact configuration raises `IllegalArgumentException`; unsupported provider keys also raise `IllegalArgumentException`.
- Provider generation and closure use `IOException`; completion parsing reports malformed unclosed reasoning blocks with `IOException`.
- Defaults include mock generation and `FAIL` oversize handling; OpenAI-compatible support restricts API-key transmission over plain HTTP to recognized loopback hosts.
