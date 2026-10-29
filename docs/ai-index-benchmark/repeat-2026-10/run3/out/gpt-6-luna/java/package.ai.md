### java
- H: 1.0
- C: 2170A0D2
- D: 2026-10-29T14:19:17Z
- T: 2026-10-29T14:23:39Z
- G: 
- A: 0.0.0
- X: package
- F: [config/](config/package.ai.md)
- F: [provider/](provider/package.ai.md)
---
> Selects eligible source files for AI-assisted documentation, configures generation behavior, and routes requests to local or remote model backends.

#### Purpose
- Provides shared configuration and validation for source selection, prompts, model parameters, facts, and oversized inputs.
- Supplies a common generation-provider contract with mock, llama.cpp JNI, and OpenAI-compatible backend selection and support.

#### Responsibilities
- **Source selection and run metadata:** define file conditions and filters, source roots and exclusions, output location, and regeneration or plan-only settings.
- **Generation configuration:** carry provider, model, prompt, field routing, sampling, hardware, reasoning, caching, fallback, and calibration settings.
- **Facts and oversized inputs:** validate regex-based fact counters, format whole-file match counts, and parse `FAIL`, `SAMPLE`, `MAP_REDUCE`, or `DETERMINISTIC` strategies.
- **Provider selection and local inference:** resolve run-level versus model-level provider settings and configure llama.cpp JNI model inference.
- **OpenAI-compatible support and output cleanup:** prepare request values, resolve endpoints and API keys, enforce key-transport safety, format errors and timings, and strip leading reasoning blocks from completions.

#### Key units
- `SrcMorphConfiguration` — shared configuration for source selection, generation settings, routing, fallback inference, and output metadata.
- `AiGenerationConfig` — provider and inference-parameter carrier for sampling, hardware, reasoning, stop strings, and calibration.
- `AiConditionEvaluator` — validates and evaluates `AiCondition` trees against `AiFileContext`.
- `AiFactExtractor` — validates `AiFactCounter` definitions and formats regex-match counts over source text.
- `AiOversizeStrategy` — defines and parses oversized-input handling choices.
- `AiGenerationProvider` — backend contract for text generation, optional timings, and resource closure.
- `AiGenerationProviderFactory` — selects mock, llama.cpp JNI, or OpenAI-compatible providers with model-over-run provider precedence.
- `LlamaCppJniAiGenerationProvider` — runs local inference and exposes available prompt-cache and generation metrics.
- `LlamaCppJniConfig` — immutable configuration for local inference and OpenAI-compatible request settings.
- `AiCompletionParser` — removes a leading Gemma-compatible thinking block and trims answer text.
- `OpenAiCompatibleProviderSupport` — prepares and validates OpenAI-compatible request, endpoint, API-key, error, and timing values.

#### Data flow
- Source-selection settings provide conditions and file filters; `AiConditionEvaluator` checks them against file facts.
- Generation settings flow to consumers, with `SrcMorphConfiguration` providing fallback llama.cpp parameters when field-generation rules are empty.
- Fact-counter definitions are validated and applied to whole source strings by `AiFactExtractor`; oversized-input tokens are parsed by `AiOversizeStrategy`.
- `AiGenerationProviderFactory` resolves provider settings and constructs a backend; requests then flow to that provider.
- The llama.cpp backend builds inference parameters, calls `chatComplete`, and passes completion content through `AiCompletionParser`; timing paths return available metrics.
- OpenAI-compatible support combines request data, prompt definitions, and sampling values into JSON and derives endpoint, API-key, error, and timing information.

#### Dependencies
- Shared types include `AiCondition`, `AiConditionGroup`, `AiRangeCondition`, `AiFileContext`, `AiFactCounter`, `AiCalibration`, `AiPromptDefinition`, `AiModelDefinition`, `AiFieldGenerationConfig`, `AiFactDefinition`, `AiGenerationRequest`, `AiGenerationTimings`, and `AiPromptSupport`.
- Path-glob matching delegates to `AiSourceExcludeFilter`; generation defaults use `AiGenerationConfig`.
- Local inference uses `net.ladenthin:llama` JNI types; OpenAI-compatible request construction uses Jackson.
- Provider selection references `MockAiGenerationProvider` and `OpenAiCompatibleAiGenerationProvider`; their implementation details are not covered by the summaries.

#### Cross-cutting
- Configuration objects are generally mutable JavaBeans or data carriers; `LlamaCppJniConfig` is immutable after building, and its builder is not thread-safe.
- Validation is explicit in `AiConditionEvaluator` and `AiFactExtractor`; generation configuration setters do not validate assigned values.
- Provider operations declare `IOException` where specified; the contract’s default timing method wraps generated text with zero timings.
- Provider model initialization and closure are unsynchronized; no thread-safety guarantees are stated for configuration objects.
- API keys are allowed over HTTPS, or plain HTTP only for recognized loopback hosts; HTTP error messages include at most 500 response-body characters.
