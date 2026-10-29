### java
- H: 1.0
- C: 914221A0
- D: 2026-10-28T21:19:32Z
- T: 2026-10-28T21:22:40Z
- G: 
- A: 0.0.0
- X: package
- F: [config/](config/package.ai.md)
- F: [provider/](provider/package.ai.md)
---
> Configures AI-assisted source documentation and routes generation requests to local, mock, or OpenAI-compatible models.

#### Purpose
- Defines source-selection, generation, provider, and documentation metadata settings.
- Supplies provider selection and backend support for generating text from AI requests.

#### Responsibilities
- **Source and generation configuration:** `SrcMorphConfiguration` and `AiGenerationConfig` hold source scopes, file eligibility, model routing, inference options, and output metadata.
- **Condition and fact utilities:** `AiConditionEvaluator` matches file metadata against condition trees; `AiFactExtractor` validates counters and formats source match counts.
- **Oversized-file policy:** `AiOversizeStrategy` parses `FAIL`, `SAMPLE`, `MAP_REDUCE`, and `DETERMINISTIC` policies.
- **Provider selection and inference:** `AiGenerationProviderFactory` selects a backend; `LlamaCppJniAiGenerationProvider` supports local GGUF inference, while mock and OpenAI-compatible providers support other configured routes.
- **Request configuration and completion handling:** `LlamaCppJniConfig` captures immutable local inference settings; `OpenAiCompatibleProviderSupport` builds requests and interprets responses; `AiCompletionParser` cleans generated text.

#### Key units
- `SrcMorphConfiguration` — shared source-discovery, generation-routing, and documentation-metadata configuration.
- `AiGenerationConfig` — per-step inference defaults and provider-specific options.
- `AiConditionEvaluator` — validates condition trees and matches them against file context.
- `AiFactExtractor` — validates fact counters and produces formatted regex-count facts.
- `AiOversizeStrategy` — parses oversized-file policy tokens, defaulting to `FAIL`.
- `AiGenerationProvider` — backend contract for generation, optional timings, and resource closure.
- `AiGenerationProviderFactory` — selects and instantiates a provider using model-over-run provider precedence.
- `LlamaCppJniAiGenerationProvider` — lazily loads a GGUF model and performs local chat completion.
- `LlamaCppJniConfig` / `LlamaCppJniConfig.Builder` — hold and construct local generation and provider settings.
- `OpenAiCompatibleProviderSupport` — prepares compatible chat requests and handles endpoint, error, and timing data.
- `AiCompletionParser` — trims completion text and removes a leading Gemma-style reasoning block.
- `AiGenerationTimings` — carries generated text and timing/count data.

#### Data flow
- Source metadata and `AiCondition` trees are evaluated for file eligibility; configured fact counters and source text produce a facts block.
- `SrcMorphConfiguration` and `AiGenerationConfig` provide source and inference settings; `AiOversizeStrategy` parses the configured file-size policy.
- `AiGenerationRequest` is routed by `AiGenerationProviderFactory` to the selected provider. The llama.cpp path uses `AiPromptSupport` and `LlamaCppJniConfig`; the OpenAI-compatible path builds a JSON chat request and converts response usage to timing data.
- Provider completions pass through `AiCompletionParser`; an unclosed leading reasoning block raises `IOException`.

#### Dependencies
- Internal configuration types include `AiCondition`, `AiRangeCondition`, `AiFileContext`, `AiFactCounter`, `AiPromptDefinition`, `AiModelDefinition`, and `AiFieldGenerationConfig`.
- Provider components use `AiGenerationRequest`, `AiGenerationTimings`, `AiPromptSupport`, `LlamaCppJniProviderSupport`, `MockAiGenerationProvider`, and the OpenAI-compatible provider.
- Path-glob matching delegates to `AiSourceExcludeFilter`; provider support uses llama.cpp JNI and Jackson JSON APIs.
- Uses Java collections, regex, time, I/O, and URL APIs, plus Lombok and JSpecify annotations.

#### Cross-cutting
- Configuration beans are generally mutable and unsynchronized; `LlamaCppJniConfig` is immutable after construction.
- Condition and fact utilities centralize validation; many configuration setters do not validate values.
- The llama.cpp provider caches a lazily initialized model without synchronization, so concurrent first calls may initialize multiple models.
- Provider generation may declare `IOException`; the default timing method delegates to generation with zero timings.
- OpenAI-compatible support validates URL schemes and blocks API keys over cleartext HTTP to non-loopback hosts.
