### java
- H: 1.0
- C: 643999B1
- D: 2026-11-01T14:41:21Z
- T: 2026-11-01T14:44:53Z
- G: 
- A: 0.0.0
- X: package
- F: [config/](config/package.ai.md)
- F: [provider/](provider/package.ai.md)
---
> Configures AI-assisted source documentation generation and routes completion requests to local or OpenAI-compatible models.

#### Purpose
- Defines source-selection, prompt, model, fact-extraction, and generation settings for AI-assisted indexing.
- Selects and runs an AI generation backend, with completion cleanup and optional timing metrics.

#### Responsibilities
- **Indexing configuration and selection:** `SrcMorphConfiguration` and `AiGenerationConfig` hold run, model, prompt, filter, and generation settings; `AiConditionEvaluator` evaluates file conditions, while `AiFactExtractor` derives configured facts from source text.
- **Oversized-file handling:** `AiOversizeStrategy` parses configured `FAIL`, `SAMPLE`, `MAP_REDUCE`, or `DETERMINISTIC` behavior.
- **Provider selection and contract:** `AiGenerationProviderFactory` resolves provider keys; `AiGenerationProvider` defines generation, timing, and lifecycle operations.
- **Generation backends and request support:** `LlamaCppJniAiGenerationProvider` runs local inference; `OpenAiCompatibleProviderSupport` prepares OpenAI-compatible requests and handles endpoint, credential, status, and timing rules.
- **Completion processing:** `AiCompletionParser` removes a leading Gemma-compatible thinking block and rejects an unterminated block.

#### Key units
- `SrcMorphConfiguration` — shared mutable configuration for source indexing and AI documentation generation.
- `AiGenerationConfig` — provider and model-generation settings, including sampling, runtime, prompt limits, and stop options.
- `AiConditionEvaluator` / `AiConditionGroup` — validate and evaluate nested file-selection conditions.
- `AiFactExtractor` — counts configured regex matches and formats a facts block.
- `AiOversizeStrategy` — parses the configured oversized-file behavior.
- `AiGenerationProvider` / `AiGenerationProviderFactory` — define the provider contract and select `mock`, `llamacpp-jni`, or `openai-compatible`.
- `LlamaCppJniConfig` / `LlamaCppJniAiGenerationProvider` — configure and run local llama.cpp JNI inference.
- `OpenAiCompatibleProviderSupport` — builds compatible chat-completion JSON and applies request-support rules.
- `AiCompletionParser` — cleans completion text by stripping a leading thinking block.

#### Data flow
- `AiFileContext` is evaluated against configured `AiCondition` trees; `usesLines` identifies whether line-count data is needed.
- `AiFactExtractor` matches configured `AiFactCounter` patterns against full source text and formats reportable counts.
- Run and model configuration determine the effective provider key; the factory selects a backend, which consumes an `AiGenerationRequest`.
- Local generation uses `LlamaCppJniConfig` and `AiPromptSupport`; OpenAI-compatible support builds request JSON from the request, prompt definitions, and configuration.
- Completion text is cleaned by `AiCompletionParser`; timing-capable paths provide token counts and throughput metrics where available.

#### Dependencies
- Configuration references `AiCondition`, `AiConditionGroup`, `AiRangeCondition`, `AiFileContext`, `AiSourceExcludeFilter`, `AiFactCounter`, `AiCalibration`, `AiPromptDefinition`, `AiModelDefinition`, `AiFieldGenerationConfig`, and `AiFactDefinition`.
- Provider paths use `AiGenerationRequest`, `AiGenerationTimings`, `AiPromptSupport`, and generation defaults from `AiGenerationConfig`.
- Local inference depends on llama.cpp JNI types (`LlamaModel`, `ModelParameters`, `ChatResponse`, `Timings`); OpenAI-compatible request support uses Jackson JSON types and injected environment lookup for API keys.
- Provider selection also references `MockAiGenerationProvider` and `OpenAiCompatibleAiGenerationProvider`.

#### Cross-cutting
- Configuration types are mutable JavaBeans; `AiGenerationConfig` and `SrcMorphConfiguration` are unsynchronized and not thread-safe.
- Condition and fact-counter validation report invalid configuration with `IllegalArgumentException`; providers declare `IOException` where applicable.
- `AiGenerationProvider` extends `AutoCloseable` with a default no-op `close`; local model caching and lazy initialization are unsynchronized.
- `LlamaCppJniConfig` is immutable after construction, while its builder is not thread-safe; API keys over plain HTTP are restricted to loopback hosts.
