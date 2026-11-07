### java
- H: 1.0
- C: 643999B1
- D: 2026-11-04T12:50:13Z
- T: 2026-11-04T12:52:31Z
- G: 
- A: 0.0.0
- X: package
- F: [config/](config/package.ai.md)
- F: [provider/](provider/package.ai.md)
---
> A framework that orchestrates AI‑driven code generation by selecting source files, extracting deterministic facts, and delegating prompt execution to configurable local or cloud providers.

#### Purpose
- Centralize AI‑generation configuration for Maven/CLI workflows.
- Provide a pluggable provider system that supports llama.cpp, OpenAI‑compatible APIs, and mocks.

#### Responsibilities
- **Configuration & Fact Extraction** – `SrcMorphConfiguration`, `AiConditionEvaluator`, `AiFactExtractor`, `AiOversizeStrategy`.
- **Provider Abstraction & Factory** – `AiGenerationProvider`, `AiGenerationProviderFactory`.
- **Local Inference** – `LlamaCppJniAiGenerationProvider`, `LlamaCppJniConfig`.
- **OpenAI Compatibility** – `OpenAiCompatibleProviderSupport`.
- **Response Handling** – `AiCompletionParser`.

#### Key units
- `SrcMorphConfiguration` – mutable bean holding directories, filters, model specs, prompt templates, fact groups, and llama settings.
- `AiConditionEvaluator` – stateless validator/evaluator for file‑matching trees (`AiCondition`, `AiConditionGroup`).
- `AiFactExtractor` – static utility that builds deterministic fact blocks from source text via regex counters.
- `AiOversizeStrategy` – enum (`FAIL`, `SAMPLE`, `MAP_REDUCE`, `DETERMINISTIC`) controlling oversized‑file handling.
- `AiGenerationProvider` – interface exposing `generate`, `generateWithTimings`, `close`.
- `AiGenerationProviderFactory` – resolves provider keys and instantiates the appropriate provider.
- `LlamaCppJniAiGenerationProvider` – lazy‑loaded llama.cpp JNI provider that runs chat completions, parses responses, and reports metrics.
- `LlamaCppJniConfig` – immutable 37‑parameter config with a fluent builder for llama.cpp tuning.
- `OpenAiCompatibleProviderSupport` – builds request bodies, validates endpoints, retrieves API keys, and formats errors/timings.
- `AiCompletionParser` – strips Gemma‑4 thinking blocks from raw completions.

#### Data flow
1. **Configuration** – `SrcMorphConfiguration` is populated via Maven/CLI or programmatically.
2. **File Selection** – `AiConditionEvaluator` evaluates an `AiCondition` tree against each `AiFileContext`; matched files proceed.
3. **Fact Generation** – `AiFactExtractor.factsBlock` produces a deterministic facts block from the source.
4. **Oversize Decision** – `AiOversizeStrategy.fromConfig` selects a strategy; routing logic applies it.
5. **Prompt Assembly** – `AiPromptSupport` (internal) supplies system/user messages.
6. **Provider Selection** – `AiGenerationProviderFactory.effectiveProvider` chooses a key; `create` instantiates the provider.
7. **Generation** –  
   - *Local*: `LlamaCppJniAiGenerationProvider.generate` calls `model.chatComplete`, parses `ChatResponse`, strips thinking blocks, logs cache reuse, and returns text.  
   - *OpenAI*: provider builds request body via `OpenAiCompatibleProviderSupport`, sends it, parses the response similarly.
8. **Timing & Metrics** – `generateWithTimings` captures token counts, TPS, and decode rates.
9. **Cleanup** – `close` releases native resources.

#### Dependencies
- **Internal** – `AiCondition`, `AiConditionGroup`, `AiFileContext`, `AiSourceExcludeFilter`, `AiFactCounter`, `AiPromptDefinition`, `AiModelDefinition`, `AiFieldGenerationConfig`, `AiFactDefinition`, `AiCalibration`, `AiPromptSupport`, `Java8CompatibilityHelper`, `MockAiGenerationProvider`, `OpenAiCompatibleAiGenerationProvider`.
- **External** – `net.ladenthin.llama` (model, parameters, JSON), `com.fasterxml.jackson.databind` (JSON), Lombok, `org.jspecify`, standard Java (`java.time`, `java.util`, `java.io`, `java.net`).

#### Cross-cutting
- **Immutability & Thread Safety** – `LlamaCppJniConfig`, provider support classes, and `AiOversizeStrategy` are immutable; providers are stateless except for lazy `LlamaModel`.
- **Error Handling** – `AiCompletionParser` throws `IOException` on truncated blocks; `OpenAiCompatibleProviderSupport` throws `IllegalArgumentException` for bad URLs and `IOException` for key/host issues.
- **Timing & Logging** – `AiGenerationTimings` captures prompt/cache tokens, TPS, and decode rates; providers log cache reuse and truncated answers.
- **Configuration Knobs** – `LlamaCppJniConfig` exposes 37 tunable parameters (threads, GPU layers, cache types, DRY settings, etc.) for fine‑grained inference control.
