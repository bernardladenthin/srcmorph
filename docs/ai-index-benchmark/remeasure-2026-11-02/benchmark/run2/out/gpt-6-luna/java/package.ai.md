### java
- H: 1.0
- C: 643999B1
- D: 2026-11-01T23:42:40Z
- T: 2026-11-01T23:45:58Z
- G: 
- A: 0.0.0
- X: package
- F: [config/](config/package.ai.md)
- F: [provider/](provider/package.ai.md)
---
> Configures AI-assisted source analysis and routes generation requests to local or OpenAI-compatible models, producing cleaned completions and optional inference timings.

#### Purpose
- Combines source-selection, prompt, model, and generation settings with provider selection for AI-assisted indexing and generation.
- Supplies configuration helpers for matching source files, extracting source facts, and handling inputs beyond model context limits.

#### Responsibilities
- **Run and generation configuration (`config/`):** Stores source filters, prompts, model routing, sampling and runtime settings, field-generation rules, facts, and output metadata.
- **Source conditions and facts (`config/`):** Validates and evaluates file-metadata condition trees; counts configured regex matches and formats whole-source facts.
- **Oversize policies (`config/`):** Represents `FAIL`, `SAMPLE`, `MAP_REDUCE`, and `DETERMINISTIC` handling for oversized inputs.
- **Provider routing and inference (`provider/`):** Selects mock, `llamacpp-jni`, or `openai-compatible` generation and supports local inference, request construction, and timing reporting.
- **Completion normalization (`provider/`):** Removes a leading Gemma-4 thinking block and reports an unterminated block as an `IOException`.

#### Key units
- `SrcMorphConfiguration` — shared run settings for source filters, prompts, models, generation rules, facts, fallback inference, and output metadata.
- `AiGenerationConfig` — per-model generation, provider-routing, reasoning, and llama.cpp runtime options.
- `AiConditionEvaluator` / `AiConditionGroup` — validate and evaluate configured condition trees against `AiFileContext`.
- `AiFactExtractor` — counts configured regex matches across source text and formats a facts block.
- `AiOversizeStrategy` — maps configuration tokens to oversize-input policies.
- `AiGenerationProvider` — `generate` / `generateWithTimings` backend contract with default zero timings and no-op `close`.
- `AiGenerationProviderFactory` — resolves run-level and model-level provider precedence and instantiates a backend.
- `LlamaCppJniAiGenerationProvider` / `LlamaCppJniConfig` — lazily initializes local llama.cpp inference and holds its immutable settings.
- `OpenAiCompatibleProviderSupport` — builds chat-completion requests, resolves endpoints and API keys, checks HTTP outcomes, and converts timings.
- `AiCompletionParser` — removes a leading reasoning block while preserving markers inside answer text.

#### Data flow
- Configured conditions are validated and matched against `AiFileContext` metadata; configured counters and source text produce a whole-file facts block.
- Run and model settings guide `AiGenerationProviderFactory`, which selects a backend for an `AiGenerationRequest`.
- Local inference builds parameters through `LlamaCppJniProviderSupport`, calls `LlamaModel`, parses the response, and cleans completion text; remote support builds OpenAI-compatible chat requests and converts usage into `AiGenerationTimings`.
- Generation returns text with timings where supported; providers without timing support use the contract’s zero-timing default.

#### Dependencies
- Shared generation and prompt types: `AiGenerationRequest`, `AiGenerationTimings`, `AiPromptSupport`, and `AiGenerationConfig`.
- Configuration types: `AiCondition`, `AiConditionGroup`, `AiRangeCondition`, `AiFileContext`, `AiSourceExcludeFilter`, `AiFactCounter`, `AiCalibration`, `AiPromptDefinition`, `AiModelDefinition`, `AiFieldGenerationConfig`, and `AiFactDefinition`.
- Local inference: llama.cpp JNI types, `LlamaCppJniProviderSupport`, and `Java8CompatibilityHelper`.
- Remote support: Jackson JSON nodes and mapper, `ChatResponse`, and `Usage`.

#### Cross-cutting
- Configuration objects are mutable JavaBeans with defaults; condition and fact helpers perform validation, while most setters do not validate values.
- Providers report failures through `IOException`; unsupported provider keys produce `IllegalArgumentException`.
- Local model initialization is lazy and cached, but concurrent first access is not explicitly synchronized; the local provider closes an initialized model.
- OpenAI-compatible API keys may be sent over HTTPS or to loopback hosts over HTTP; non-loopback cleartext transmission is rejected.
