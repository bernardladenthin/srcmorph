### java
- H: 1.0
- C: 55485E7D
- D: 2026-11-01T01:35:52Z
- T: 2026-11-01T01:39:13Z
- G: 
- A: 0.0.0
- X: package
- F: [config/](config/package.ai.md)
- F: [provider/](provider/package.ai.md)
---
> Configures and runs AI-assisted source documentation by selecting project files, preparing generation settings, and routing completion requests to supported inference backends.

#### Purpose
- Defines source-selection, fact-extraction, prompt, model, and generation settings for `.ai.md` documentation workflows.
- Provides provider selection and completion handling for local llama.cpp, OpenAI-compatible, and mock generation.

#### Responsibilities
- **Source configuration and filtering:** `SrcMorphConfiguration` holds project scope and documentation-generation settings; condition rules and `AiSourceExcludeFilter` support file selection.
- **Condition evaluation and facts:** `AiConditionEvaluator` validates and matches file conditions; `AiFactExtractor` validates regex counters and formats source match counts.
- **Generation policy and settings:** `AiGenerationConfig` carries per-step provider and inference options; `AiOversizeStrategy` parses oversized-file handling choices.
- **Provider routing and inference:** `AiGenerationProviderFactory` resolves provider keys; provider implementations handle local llama.cpp, OpenAI-compatible, or mock generation.
- **Completion preparation and cleanup:** `OpenAiCompatibleProviderSupport` prepares request and endpoint/auth metadata; `AiCompletionParser` removes a leading Gemma-4 thinking block.

#### Key units
- `SrcMorphConfiguration` — project source scope, generation definitions, fallback inference settings, and output metadata.
- `AiGenerationConfig` — per-step provider, prompt-limit, sampling, hardware, and stopping settings.
- `AiConditionEvaluator` / `AiConditionGroup` — condition-tree validation, file-context matching, and Maven-bindable condition grouping.
- `AiFactExtractor` — regex fact-counter validation and formatted source match counts.
- `AiOversizeStrategy` — strategy enum and configuration-token parser, defaulting to `FAIL`.
- `AiGenerationProvider` — generation contract with optional timing results and resource cleanup.
- `AiGenerationProviderFactory` — provider-key resolution and backend construction.
- `LlamaCppJniAiGenerationProvider` — local inference, completion parsing, timing metrics, and native model lifecycle.
- `LlamaCppJniConfig` / `LlamaCppJniConfig.Builder` — local generation and provider settings with fluent construction.
- `OpenAiCompatibleProviderSupport` — chat-completions request JSON, endpoint/auth values, error messages, and timing metadata.
- `AiCompletionParser` — leading reasoning-block cleanup for generated text.

#### Data flow
- `SrcMorphConfiguration` supplies file-selection rules and prompt, model, field-generation, and fact definitions to consumers.
- `AiConditionEvaluator` validates condition trees and matches them against `AiFileContext`; `AiFactExtractor` counts configured regex matches and formats nonempty facts.
- `AiGenerationConfig` and model/run provider keys feed `AiGenerationProviderFactory`, which selects a backend for an `AiGenerationRequest`.
- The llama.cpp provider combines request data, `AiPromptSupport`, and `LlamaCppJniConfig` to generate and parse text; OpenAI-compatible support prepares request metadata and timing data.
- Generated text is cleaned by `AiCompletionParser`; the local provider may return model timing and cache metrics.

#### Dependencies
- Configuration types include `AiCondition`, `AiRangeCondition`, `AiFileContext`, `AiFactCounter`, `AiCalibration`, `AiPromptDefinition`, `AiModelDefinition`, `AiFieldGenerationConfig`, and `AiFactDefinition`.
- Provider workflows use `AiGenerationRequest`, `AiGenerationTimings`, `AiPromptSupport`, `Java8CompatibilityHelper`, `LlamaCppJniProviderSupport`, and llama JNI types.
- OpenAI-compatible support uses Jackson JSON types and llama response `ChatResponse` / `Usage`; the factory references mock and OpenAI-compatible provider implementations.
- Configuration and filtering use Java collections, regex, `Instant`, Lombok, and JSpecify; inference settings reference `net.ladenthin.llama.parameters.ModelParameters`.

#### Cross-cutting
- Configuration beans are generally mutable and unsynchronized; `LlamaCppJniConfig` uses final fields, while its builder is not thread-safe and does not copy supplied lists.
- Configuration validation reports invalid conditions and fact counters with `IllegalArgumentException`; provider implementations may throw `IOException`.
- The provider interface’s default timing method wraps generated text with zero-valued timings.
- Local model initialization and cached access are unsynchronized; concurrent first calls may race.
- OpenAI-compatible endpoint validation requires HTTP(S); API keys are allowed over HTTPS or over HTTP only for loopback hosts.
