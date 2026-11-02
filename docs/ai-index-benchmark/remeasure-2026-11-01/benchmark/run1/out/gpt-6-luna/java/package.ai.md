### java
- H: 1.0
- C: 643999B1
- D: 2026-11-01T14:19:41Z
- T: 2026-11-01T14:23:24Z
- G: 
- A: 0.0.0
- X: package
- F: [config/](config/package.ai.md)
- F: [provider/](provider/package.ai.md)
---
> Configures source-file selection and AI generation, then routes requests to local or OpenAI-compatible backends to produce indexed-document text.

#### Purpose
- Provides shared configuration, file-condition evaluation, and source-fact formatting for AI index generation.
- Selects and runs generation providers with configurable inference settings and completion cleanup.

#### Responsibilities
- **Selection and generation configuration:** `SrcMorphConfiguration` and `AiGenerationConfig` hold file filters, conditions, prompts, models, facts, provider routing, and generation parameters.
- **Condition and fact processing:** `AiConditionEvaluator` checks file facts against condition trees; `AiFactExtractor` validates counters and formats source-match counts.
- **Provider routing and inference:** `AiGenerationProviderFactory` selects a backend; provider implementations support mock, llama.cpp JNI, and OpenAI-compatible generation.
- **Provider request support and cleanup:** `OpenAiCompatibleProviderSupport` prepares endpoints, credentials, requests, and response data; `AiCompletionParser` removes a leading reasoning block.

#### Key units
- `SrcMorphConfiguration` — shared configuration for source selection, output, models, prompts, facts, and provider routing.
- `AiGenerationConfig` — per-generation inference and sampling settings.
- `AiConditionEvaluator` / `AiConditionGroup` — validate and evaluate nested file conditions.
- `AiFactExtractor` — validate `AiFactCounter` definitions and format regex match counts.
- `AiOversizeStrategy` — parse `FAIL`, `SAMPLE`, `MAP_REDUCE`, or `DETERMINISTIC` policies.
- `AiGenerationProvider` — generation contract with optional timing results and lifecycle closure.
- `AiGenerationProviderFactory` — resolves model-level or run-level provider selection, defaulting to mock.
- `LlamaCppJniAiGenerationProvider` — generates completions using a lazily initialized llama.cpp model.
- `OpenAiCompatibleProviderSupport` — builds and validates OpenAI-compatible request, endpoint, credential, error, and timing data.
- `LlamaCppJniConfig` — immutable local-inference configuration with a chained builder.
- `AiCompletionParser` — trims generated text and strips a leading Gemma-compatible thinking block.

#### Data flow
- `SrcMorphConfiguration` supplies selection and generation settings; `AiConditionEvaluator` matches `AiFileContext` facts against configured conditions.
- Source text and configured `AiFactCounter` entries pass through validation and counting to produce formatted facts.
- The provider factory resolves model-level configuration before run-level configuration and passes generation requests to the selected backend.
- The llama.cpp backend builds inference parameters, generates a chat completion, and parses its text; OpenAI-compatible support prepares request and response data for remote calls.

#### Dependencies
- Configuration uses `AiCondition`, `AiConditionGroup`, `AiRangeCondition`, `AiFileContext`, `AiSourceExcludeFilter`, `AiPromptDefinition`, `AiModelDefinition`, `AiFieldGenerationConfig`, `AiFactDefinition`, and `AiCalibration`.
- Providers share `AiGenerationRequest`, `AiGenerationTimings`, `AiPromptSupport`, and `AiGenerationConfig`.
- Integrations include llama.cpp JNI types, `OpenAiCompatibleAiGenerationProvider`, `MockAiGenerationProvider`, `ChatResponseParser`, `Timings`, `Usage`, and Jackson JSON nodes.

#### Cross-cutting
- Configuration objects are generally mutable, unsynchronized JavaBeans; `LlamaCppJniConfig` is immutable and its builder is intended for single-threaded use.
- Condition and fact validation is explicit; configuration objects generally store values without automatic validation.
- The llama.cpp model is lazily initialized and reused without synchronization, so concurrent first calls may race.
- Provider generation failures use `IOException`; implementations without timing data receive zero timings by default.
- OpenAI-compatible endpoints must use HTTP or HTTPS, and API keys are rejected for non-loopback plain-HTTP destinations.
