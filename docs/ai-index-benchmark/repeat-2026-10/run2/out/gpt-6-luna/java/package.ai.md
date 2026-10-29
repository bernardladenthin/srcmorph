### java
- H: 1.0
- C: 5BF387FF
- D: 2026-10-29T13:48:14Z
- T: 2026-10-29T13:51:39Z
- G: 
- A: 0.0.0
- X: package
- F: [config/](config/package.ai.md)
- F: [provider/](provider/package.ai.md)
---
> Configures source-summary generation and provides selectable AI backends that turn generation requests into cleaned completion text.

#### Purpose
- Defines source-selection, generation, and output settings for project summaries.
- Routes AI generation requests to mock, local llama.cpp, or OpenAI-compatible providers.

#### Responsibilities
- **Source and output configuration (`config/`):** Defines file filters, condition evaluation, size limits, output metadata, generation settings, and oversize policies.
- **Generation and response handling (`provider/`):** Selects inference backends, configures requests, produces completions and timing metrics, and removes leading reasoning blocks.
- **Fact extraction (`config/`):** Validates regex counters and formats exact source-text match totals for generated summaries.

#### Key units
- `SrcMorphConfiguration` — central JavaBean for source scopes, generation definitions, model settings, and output metadata.
- `AiGenerationConfig` — per-step provider, model, inference, and optional calibration settings.
- `AiConditionEvaluator` / `AiConditionGroup` — validate and evaluate configured file-condition trees.
- `AiFactExtractor` — validates fact counters and formats extracted match totals.
- `AiOversizeStrategy` — parses `FAIL`, `SAMPLE`, `MAP_REDUCE`, and `DETERMINISTIC` policies.
- `AiGenerationProvider` — common generation, timing, and resource-close contract.
- `AiGenerationProviderFactory` — selects a provider using model/run configuration.
- `LlamaCppJniAiGenerationProvider` — performs local llama.cpp JNI inference with lazy model reuse.
- `OpenAiCompatibleProviderSupport` — builds chat-completion requests, resolves endpoints and API keys, interprets responses, and computes timings.
- `LlamaCppJniConfig` — builder-created inference configuration for local and compatible API settings.
- `AiCompletionParser` — trims completions and strips a leading Gemma-compatible thinking block.

#### Data flow
- Source filters and optional conditions select files; fact-counter regexes scan full source text and produce a formatted facts block.
- Generation configuration and oversize policy guide downstream summary generation; output settings provide destination and project/version metadata.
- `AiGenerationProviderFactory` resolves the provider, which consumes an `AiGenerationRequest` and prompt support; local inference configures or reuses a model, while compatible API support builds and interprets a non-streaming chat-completion exchange.
- Provider responses yield cleaned completion text and, when available, token and rate timing metrics.

#### Dependencies
- Configuration units reference `AiCondition`, `AiRangeCondition`, `AiFileContext`, `AiSourceExcludeFilter`, `AiCalibration`, `AiPromptDefinition`, `AiModelDefinition`, `AiFieldGenerationConfig`, `AiFactDefinition`, and `AiFactCounter`.
- Provider units use `AiGenerationRequest`, `AiPromptSupport`, `AiGenerationTimings`, and `AiGenerationConfig`, plus mock, llama.cpp JNI, and OpenAI-compatible implementations.
- External support includes Java collections, regex, time and file APIs; llama.cpp JNI model/response types; Jackson JSON nodes; Lombok; and JSpecify.

#### Cross-cutting
- Configuration objects are mutable JavaBeans; `LlamaCppJniConfig` uses final fields and a builder.
- Condition, fact, and oversize validation generally reports `IllegalArgumentException`; provider generation and resource-close failures use `IOException`.
- No general thread-safety guarantee is stated; local model initialization/access is unsynchronized and the configuration builder is not thread-safe.
- Defaults include `FAIL` for oversize handling and `mock` for generation; API-key handling avoids sending credentials over plain HTTP to non-loopback hosts.
