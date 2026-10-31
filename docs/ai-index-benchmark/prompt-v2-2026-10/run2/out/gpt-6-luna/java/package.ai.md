### java
- H: 1.0
- C: C8FB2D20
- D: 2026-10-31T14:44:17Z
- T: 2026-10-31T14:47:24Z
- G: 
- A: 0.0.0
- X: package
- F: [config/](config/package.ai.md)
- F: [provider/](provider/package.ai.md)
---
> Selects source files for AI-assisted processing and generates normalized summaries through local llama.cpp or OpenAI-compatible models.

#### Purpose
- Defines run-wide source-selection and generation settings, then routes generation requests to configured AI backends.
- Supports condition-based file filtering, exact source facts, oversize-file policies, and normalized completion output.

#### Responsibilities
- **Run configuration and filtering:** `SrcMorphConfiguration` and condition types configure source scope, exclusions, prompts, model definitions, and generation rules; `AiConditionEvaluator` validates and matches conditions against file context.
- **Generation settings and source facts:** `AiGenerationConfig` holds per-model parameters; `AiFactExtractor` counts configured regex matches across full source text; `AiOversizeStrategy` resolves policies for files exceeding context limits.
- **Provider routing and local inference:** `AiGenerationProviderFactory` selects a backend; `LlamaCppJniAiGenerationProvider` lazily loads and reuses a local model, parses completions, and optionally reports metrics.
- **Remote inference and response handling:** OpenAI-compatible support builds chat-completions requests, validates endpoints and API-key transport, interprets responses, and reports timing data; `AiCompletionParser` removes a leading Gemma thinking block.

#### Key units
- `SrcMorphConfiguration` — shared mutable settings for source scope, prompts, models, fallback behavior, and output metadata.
- `AiGenerationConfig` — per-model sampling, prompt limits, provider options, runtime tuning, and calibration settings.
- `AiConditionEvaluator` / `AiConditionGroup` — validate and evaluate file conditions, including logical groups.
- `AiFactExtractor` — formats configured whole-source regex counts as a facts block.
- `AiOversizeStrategy` — parses configured policies for handling oversized files.
- `AiGenerationProvider` — generation and lifecycle interface with default timing results and no-op `close`.
- `AiGenerationProviderFactory` — resolves effective run- and model-level provider settings and creates a backend.
- `LlamaCppJniAiGenerationProvider` / `LlamaCppJniConfig` — execute local inference and expose its provider and generation settings.
- `OpenAiCompatibleProviderSupport` — builds remote request bodies and handles endpoint validation, API-key checks, errors, and timings.
- `AiCompletionParser` — trims completion text and strips a leading Gemma thinking block.

#### Data flow
- `SrcMorphConfiguration` supplies selection and generation settings; condition trees are validated and evaluated against `AiFileContext` to select files.
- Configured `AiFactCounter` values and full source text flow through `AiFactExtractor` to produce a formatted facts block.
- `AiGenerationRequest` and effective provider settings flow through factory-selected providers: local inference uses prompt and JNI support, while remote support sends a non-streaming chat-completions request.
- Provider results are parsed and normalized; timing metrics are exposed when available, and an unterminated Gemma thinking block raises `IOException`.

#### Dependencies
- Configuration uses `AiCondition`, `AiConditionGroup`, `AiFileContext`, `AiRangeCondition`, `AiSourceExcludeFilter`, prompt/model definitions, field-generation rules, fact definitions, and calibration types.
- Providers use `AiGenerationRequest`, `AiGenerationTimings`, `AiGenerationConfig`, `AiPromptSupport`, `Java8CompatibilityHelper`, and `LlamaCppJniProviderSupport`.
- Local inference depends on llama.cpp JNI model, parameter, response, parser, and timing types; remote support uses Jackson and Java URL/HTTP utilities.

#### Cross-cutting
- Configuration classes are mutable JavaBeans without synchronization; condition evaluation and fact extraction are stateless.
- Invalid conditions, fact counters, unknown oversize tokens, and malformed completion blocks produce exceptions; null or blank oversize tokens resolve to `FAIL`.
- Providers implement `AutoCloseable` and declare `IOException` for generation; local model loading is lazy and cached, with `close` releasing an initialized model.
- OpenAI-compatible support obtains API keys through injected environment lookup and permits plain-HTTP key transmission only to recognized loopback hosts.
