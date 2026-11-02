### java
- H: 1.0
- C: 643999B1
- D: 2026-11-01T23:56:56Z
- T: 2026-11-02T00:03:26Z
- G: 
- A: 0.0.0
- X: package
- F: [config/](config/package.ai.md)
- F: [provider/](provider/package.ai.md)
---
> A configuration‑driven AI source‑morphing engine that indexes code, evaluates file‑selection rules, extracts deterministic facts, and orchestrates AI generation for code summaries and transformations.

#### Purpose
- Central configuration holder for the srcmorph AI code‑generation tool.
- Provides utilities to evaluate file‑selection conditions and extract deterministic facts for AI summarisation.
- Defines strategies for handling oversized source files.

#### Responsibilities
- **Configuration Management** – `SrcMorphConfiguration` aggregates runtime parameters (paths, AI providers, model settings, prompts, field‑generation rules, fact definitions).
- **Condition Evaluation** – `AiConditionEvaluator` validates and evaluates logical trees (`and`, `or`, `not`, `extensions`, `size`, `lines`, `modifiedAfter`, `modifiedBefore`, `pathGlob`) against `AiFileContext`.
- **Fact Extraction** – `AiFactExtractor` builds deterministic facts blocks from regex counters and validates counter configurations.
- **Oversize Handling** – `AiOversizeStrategy` enumerates routing behaviours for files exceeding model context limits.
- **Generation Configuration** – `AiGenerationConfig` holds AI provider‑specific parameters (model path, sampling, retry, stop strings, caching, GPU/CPU settings, etc.).

#### Key units
- `SrcMorphConfiguration` – mutable bean for all srcmorph settings.
- `AiConditionEvaluator` – stateless validator/evaluator of `AiCondition` trees.
- `AiFactExtractor` – static utility for building facts blocks.
- `AiOversizeStrategy` – enum of routing strategies (`FAIL`, `SAMPLE`, `MAP_REDUCE`, `DETERMINISTIC`).
- `AiGenerationConfig` – data holder for AI generation parameters.
- `AiGenerationProvider` – interface for generating text from LLMs.
- `AiGenerationProviderFactory` – resolves provider keys and builds concrete providers.
- `LlamaCppJniConfig` – immutable config for llama.cpp provider.
- `LlamaCppJniAiGenerationProvider` – local GGUF inference implementation.
- `OpenAiCompatibleProviderSupport` – HTTP request/response handling for OpenAI‑compatible APIs.
- `AiCompletionParser` – cleans raw LLM output.

#### Data flow
1. **Configuration Load** – `SrcMorphConfiguration` is populated from Maven, JSON/YAML, or programmatic input.
2. **File Routing** – `AiConditionEvaluator` checks each source file against configured `AiCondition` trees to decide inclusion.
3. **Fact Generation** – For included files, `AiFactExtractor` produces a deterministic facts block used to control AI summarisation size.
4. **Oversize Decision** – If a file exceeds context limits, `AiOversizeStrategy` determines whether to fail, sample, map‑reduce, or deterministically truncate.
5. **Provider Selection** – `AiGenerationProviderFactory.effectiveProvider` resolves the provider key; `create` returns the matching provider.
6. **AI Generation** – Provider’s `generate` or `generateWithTimings` is invoked:
   - *Local*: `LlamaCppJniAiGenerationProvider` loads a GGUF model via JNI, runs chat completions, parses JSON, and reports timings.
   - *Cloud*: `OpenAiCompatibleProviderSupport` builds JSON body, endpoint, API key, sends HTTP request, parses `ChatResponse`, and extracts metrics.
7. **Post‑processing** – `AiCompletionParser.parseCompletion` removes internal reasoning blocks from the raw completion before persisting.

#### Dependencies
- **Internal** – `AiCondition`, `AiFileContext`, `AiFactCounter`, `AiPromptDefinition`, `AiModelDefinition`, `AiFieldGenerationConfig`, `AiFactDefinition`, `AiCalibration`, `AiPromptSupport`, `AiGenerationTimings`, `AiGenerationRequest`, `Java8CompatibilityHelper`.
- **External** – `java.time`, `java.util`, `java.util.regex`, `net.ladenthin.srcmorph.support.AiSourceExcludeFilter`, Lombok (`@ToString`), `org.jspecify.annotations.Nullable`, `net.ladenthin.llama` (model, parameters, JSON parsing), `com.fasterxml.jackson` (JSON), `java.net`, `java.io`.

#### Cross‑cutting
- **Thread safety** – `AiConditionEvaluator` and `AiFactExtractor` are stateless and thread‑safe; providers are stateless or lazily initialized; factories are thread‑safe.
- **Validation** – `AiConditionEvaluator.validate` and `AiFactExtractor.validate` enforce configuration correctness, throwing `IllegalArgumentException` on errors.
- **Immutability** – `LlamaCppJniConfig` and `OpenAiCompatibleProviderSupport` are immutable; builders are single‑threaded.
- **Error handling** – Validation errors surface as unchecked exceptions; I/O errors propagate as `IOException`; unknown provider keys trigger `IllegalArgumentException`.
- **Logging & metrics** – Providers log cache reuse, truncation warnings, and timing metrics; `AiGenerationTimings` captures token usage and latency.
