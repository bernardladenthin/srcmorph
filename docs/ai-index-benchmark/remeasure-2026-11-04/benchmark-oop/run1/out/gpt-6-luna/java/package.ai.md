### java
- H: 1.0
- C: 643999B1
- D: 2026-11-04T07:19:50Z
- T: 2026-11-04T07:23:09Z
- G: 
- A: 0.0.0
- X: package
- F: [config/](config/package.ai.md)
- F: [provider/](provider/package.ai.md)
---
> Selects source files and prepares their facts and settings for AI indexing, then generates summaries through mock, local llama.cpp, or OpenAI-compatible backends.

#### Purpose
- Coordinates configuration for source eligibility, fact extraction, model generation, and oversize handling.
- Provides shared provider selection and generation support for source-document requests.

#### Responsibilities
- **Indexing configuration:** `SrcMorphConfiguration` and related settings define source selection, exclusions, prompts, models, generation rules, fallbacks, and output metadata.
- **Eligibility and facts:** `AiConditionEvaluator` checks file metadata against configured conditions; `AiFactExtractor` counts regex matches in complete source text.
- **Provider selection and generation:** `AiGenerationProviderFactory` resolves provider settings; `AiGenerationProvider` defines the common generation and lifecycle contract.
- **Generation backends and support:** Local llama.cpp inference and OpenAI-compatible request support handle backend-specific configuration, inference or HTTP requests, and timing data.
- **Oversize and completion handling:** `AiOversizeStrategy` defines policies for files exceeding context limits; `AiCompletionParser` strips a leading thinking block or rejects an unterminated one.

#### Key units
- `SrcMorphConfiguration` — shared source-indexing, routing, generation, and output settings.
- `AiConditionEvaluator` / `AiConditionGroup` — validate and evaluate file-condition trees and store combinator children.
- `AiFactExtractor` — formats exact regex-match counts from source text.
- `AiGenerationConfig` — mutable per-model generation settings; `AiOversizeStrategy` — supported policies and config-token parsing.
- `AiGenerationProviderFactory` / `AiGenerationProvider` — select a backend and expose generation, timing, and close operations.
- `LlamaCppJniAiGenerationProvider` / `LlamaCppJniConfig` — run local inference and configure model/runtime settings.
- `OpenAiCompatibleProviderSupport` — builds chat-completion requests and handles endpoint, credentials, HTTP results, and timing.
- `AiCompletionParser` — cleans completion text by removing a leading Gemma-4 thinking block.

#### Data flow
- `SrcMorphConfiguration` supplies discovery, condition, prompt, model, routing, and output settings to indexing and generation consumers.
- `AiConditionEvaluator` matches an `AiCondition` against `AiFileContext`; `AiFactExtractor` processes configured counters against full source text.
- `AiGenerationProviderFactory` resolves the effective provider key and creates a backend; providers receive `AiGenerationRequest` and return completion text, optionally with timing metrics.
- Local generation uses `AiPromptSupport` and llama.cpp inference; OpenAI-compatible support builds chat-completion JSON and handles the HTTP response. Completion parsing cleans returned text before use.

#### Dependencies
- Shared project types include `AiCondition`, `AiFileContext`, `AiFactCounter`, `AiGenerationRequest`, `AiGenerationTimings`, `AiPromptSupport`, and `AiGenerationConfig`.
- Configuration references `AiModelDefinition`, `AiPromptDefinition`, `AiFieldGenerationConfig`, `AiFactDefinition`, and `AiCalibration`.
- Local inference uses llama.cpp JNI types; remote request support uses Jackson; fact extraction uses Java regex utilities.

#### Cross-cutting
- Configuration objects are generally mutable JavaBeans; `LlamaCppJniConfig` is immutable and built with a fluent builder.
- Condition and fact utilities validate inputs; invalid structures, regexes, or unknown nonblank oversize tokens produce `IllegalArgumentException`.
- Local model loading is lazy and cached but unsynchronized; concurrent initialization may race, and inference and close are not synchronized.
- Providers without timing support report zero metrics; OpenAI-compatible support withholds API keys from non-loopback HTTP endpoints.
