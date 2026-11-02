### java
- H: 1.0
- C: 643999B1
- D: 2026-11-01T23:26:17Z
- T: 2026-11-01T23:29:27Z
- G: 
- A: 0.0.0
- X: package
- F: [config/](config/package.ai.md)
- F: [provider/](provider/package.ai.md)
---
> Configures source-analysis runs and generates AI-assisted documentation text using local or OpenAI-compatible models.

#### Purpose
- Defines run-level and per-model settings for source selection, prompt routing, output, and inference.
- Selects AI generation backends and standardizes request, response cleanup, timing, and resource handling.

#### Responsibilities
- **Run configuration and source selection:** `SrcMorphConfiguration`, `AiGenerationConfig`, and condition utilities configure source scope, generation settings, file filters, and oversized-file handling.
- **Facts and prompt inputs:** `AiFactExtractor` produces exact source match counts for summaries; configuration references prompt definitions, reusable facts, and field-generation rules.
- **Provider selection and generation:** Provider factory and implementations route requests to mock, local llama.cpp, or OpenAI-compatible backends.
- **Inference and response support:** Local and remote provider utilities apply model settings, construct requests, report timing data, and clean generated completions.

#### Key units
- `SrcMorphConfiguration` — run-level source scope, output metadata, prompts, model definitions, routing, and fallback settings.
- `AiGenerationConfig` — per-model provider, sampling, prompt-limit, reasoning, and runtime options.
- `AiConditionEvaluator` / `AiConditionGroup` — validate and evaluate file-selection condition trees.
- `AiFactExtractor` — count configured regex matches in source text and format exact-facts entries.
- `AiOversizeStrategy` — parses `FAIL`, `SAMPLE`, `MAP_REDUCE`, or `DETERMINISTIC` handling.
- `AiGenerationProvider` / `AiGenerationProviderFactory` — define the generation contract and select a backend.
- `LlamaCppJniAiGenerationProvider` — runs local llama.cpp inference and exposes completion, timing, and cache data.
- `LlamaCppJniConfig` — holds local inference, hardware, cache, remote-provider, reasoning, and stop settings.
- `OpenAiCompatibleProviderSupport` — builds remote chat-completion requests and provides endpoint, authentication, status, error, and timing helpers.
- `AiCompletionParser` — removes a leading Gemma-style thinking block and rejects an unterminated block.

#### Data flow
- Run configuration supplies file scope, prompts, model definitions, and field routing; condition evaluation matches `AiFileContext` against configured constraints.
- Configured fact counters are applied to full source text to produce a facts block for AI summaries.
- Provider selection resolves model-level versus run-level provider keys and constructs the matching backend.
- Backends consume `AiGenerationRequest`; local inference invokes `LlamaModel.chatComplete` and parses and cleans the response, while OpenAI-compatible support constructs serialized chat-completion requests.
- Timing-aware generation returns `AiGenerationTimings`; implementations without metrics can use the interface’s zero-valued defaults.

#### Dependencies
- Configuration and selection use `AiCondition`, `AiRangeCondition`, `AiFileContext`, `AiSourceExcludeFilter`, prompt/model/field definitions, and fact types.
- Providers use `AiGenerationRequest`, `AiPromptSupport`, `AiGenerationConfig`, and `AiGenerationTimings`.
- Local inference depends on llama.cpp types including `LlamaModel`, `ModelParameters`, `ChatResponseParser`, and `Timings`; remote request support uses Jackson JSON types and `ObjectMapper`.

#### Cross-cutting
- Configuration types are generally mutable JavaBeans; `LlamaCppJniConfig` is immutable after building, with a mutable builder.
- Condition and fact utilities validate inputs; most generation and run-level setters do not validate ranges or values.
- Local model initialization is lazy and unsynchronized; concurrent first use or close is not guarded.
- Provider operations may throw `IOException`; unsupported provider keys and unsafe plain-HTTP API-key delivery produce errors.
