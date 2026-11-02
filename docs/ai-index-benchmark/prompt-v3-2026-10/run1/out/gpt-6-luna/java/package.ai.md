### java
- H: 1.0
- C: 2B1E2C14
- D: 2026-10-31T19:47:56Z
- T: 2026-10-31T19:51:19Z
- G: 
- A: 0.0.0
- X: package
- F: [config/](config/package.ai.md)
- F: [provider/](provider/package.ai.md)
---
> Configures source selection and AI documentation generation, then routes generation requests to local or OpenAI-compatible providers.

#### Purpose
- Centralizes source-scope, model, prompt, generation, and documentation metadata settings alongside utilities for condition evaluation and source facts.
- Defines provider selection and execution for generated text, including completion cleanup and optional inference timings.

#### Responsibilities
- **Configuration and source selection (`config/`):** `SrcMorphConfiguration` and `AiGenerationConfig` hold source filters, model routing, generation options, and output metadata; `AiOversizeStrategy` represents policies for oversized sources.
- **Conditions and source facts (`config/`):** `AiConditionEvaluator` validates and evaluates file-selection rules against `AiFileContext`; `AiFactExtractor` formats configured regex match counts.
- **Provider selection and contract (`provider/`):** `AiGenerationProvider` defines generation and lifecycle operations; `AiGenerationProviderFactory` chooses a backend using model- and run-level settings.
- **Inference backends (`provider/`):** Supports mock generation, local llama.cpp JNI inference, and OpenAI-compatible generation; `OpenAiCompatibleProviderSupport` prepares request data and interprets response details.
- **Completion handling (`provider/`):** `AiCompletionParser` strips a leading Gemma-4 thinking block and rejects an unterminated one.

#### Key units
- `SrcMorphConfiguration` — source scope, exclusions, model definitions and routing, field-generation rules, fallback settings, and documentation metadata.
- `AiGenerationConfig` — mutable per-model generation and provider options, including sampling, inference, caching, reasoning, and stop settings.
- `AiConditionEvaluator` / `AiConditionGroup` — validation and evaluation of nested file-selection conditions.
- `AiFactExtractor` — regex-based source match counting and labeled fact formatting.
- `AiOversizeStrategy` — `FAIL`, `SAMPLE`, `MAP_REDUCE`, and `DETERMINISTIC` policies, defaulting to `FAIL`.
- `AiGenerationProvider` / `AiGenerationProviderFactory` — provider contract and configuration-driven backend construction.
- `LlamaCppJniConfig` / `LlamaCppJniConfig.Builder` — immutable local-backend settings and fluent construction.
- `LlamaCppJniAiGenerationProvider` — local llama.cpp chat inference with parsed output and available timing metrics.
- `OpenAiCompatibleProviderSupport` — chat-completion payload and endpoint support, API-key transport checks, and error and usage interpretation.
- `AiCompletionParser` — removal of a leading `<|channel>thought` block while preserving markers elsewhere.

#### Data flow
- `SrcMorphConfiguration` supplies source-selection and generation settings; condition evaluation matches an `AiCondition` against `AiFileContext`, with path globs delegated to `AiSourceExcludeFilter`.
- `AiFactExtractor` consumes configured `AiFactCounter` entries and source text to produce a formatted facts block.
- Provider selection prefers a nonblank model-level provider over the run-level setting; absent or `"mock"` selection uses mock generation.
- The selected provider uses `AiPromptSupport` and `AiGenerationRequest`; local inference parses completion content and may return token counts and rates, while compatible response usage can yield `AiGenerationTimings`.
- `AiCompletionParser` removes leading thinking content; an unterminated leading block raises `IOException`.

#### Dependencies
- Configuration refers to `AiCondition`, `AiConditionGroup`, `AiRangeCondition`, `AiFileContext`, `AiSourceExcludeFilter`, `AiPromptDefinition`, `AiModelDefinition`, `AiFieldGenerationConfig`, `AiFactDefinition`, and `AiCalibration`.
- Providers collaborate with `AiGenerationRequest`, `AiGenerationTimings`, `AiPromptSupport`, `AiGenerationConfig`, `LlamaCppJniProviderSupport`, and `MockAiGenerationProvider`; the factory also references the OpenAI-compatible provider.
- Local inference uses `net.ladenthin:llama` JNI types; compatible request support uses Jackson JSON nodes and `ChatResponse` / `Usage` data.

#### Cross-cutting
- Configuration objects are mutable, unsynchronized JavaBeans; condition and fact utilities are stateless. Configuration and generation settings do not generally validate ranges or values, while conditions and fact counters have explicit validation.
- Nullable values and sentinels represent unset generation options; `AiGenerationConfig` exposes unmodifiable list views.
- Provider generation and close failures use `IOException`; the default timing method wraps generated text with zero metrics.
- `LlamaCppJniConfig` is immutable, while its builder is intended for single-threaded use; local model initialization is lazy and unsynchronized, so concurrent first calls may race.
- OpenAI-compatible support performs no network calls itself; API keys are allowed over HTTPS or plain HTTP to loopback hosts.
