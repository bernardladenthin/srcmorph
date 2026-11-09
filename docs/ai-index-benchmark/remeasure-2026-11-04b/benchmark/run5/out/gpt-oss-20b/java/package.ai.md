### java
- H: 1.0
- C: 643999B1
- D: 2026-11-04T12:53:23Z
- T: 2026-11-04T12:55:19Z
- G: 
- A: 0.0.0
- X: package
- F: [config/](config/package.ai.md)
- F: [provider/](provider/package.ai.md)
---
> A framework that orchestrates AI‑driven source‑code transformations, from configuration‑driven file selection and fact extraction to pluggable text‑generation backends.

#### Purpose
- Central hub for AI‑based source‑code morphing in Maven builds.
- Validates file‑selection rules, extracts language‑agnostic facts, and drives generation engines.

#### Responsibilities
- **Configuration & Validation** – `SrcMorphConfiguration` aggregates all runtime parameters and validates them.
- **File Selection** – `AiConditionEvaluator` evaluates `AiCondition` trees against `AiFileContext`.
- **Fact Extraction** – `AiFactExtractor` counts constructs via regex and builds fact blocks.
- **Oversize Handling** – `AiOversizeStrategy` dictates how oversized files are treated.
- **Provider Management** – `AiGenerationProviderFactory` resolves the correct `AiGenerationProvider`.
- **Generation Execution** – Concrete providers (`LlamaCppJniAiGenerationProvider`, `OpenAiCompatibleAiGenerationProvider`, `MockAiGenerationProvider`) build requests, invoke models, parse responses, and return text or timings.

#### Key units
- `SrcMorphConfiguration` – mutable bean for Maven/JSON/YAML binding.
- `AiConditionEvaluator` – stateless validator for `AiCondition` trees.
- `AiFactExtractor` – static utility for regex‑based fact counting.
- `AiOversizeStrategy` – enum (`FAIL`, `SAMPLE`, `MAP_REDUCE`, `DETERMINISTIC`).
- `AiGenerationProvider` – interface (`generate`, `generateWithTimings`, `close`).
- `AiGenerationProviderFactory` – resolves provider based on config.
- `LlamaCppJniAiGenerationProvider` – local GGUF inference with timing extraction.
- `OpenAiCompatibleAiGenerationProvider` – HTTP chat‑completion endpoint support.
- `MockAiGenerationProvider` – deterministic test provider.
- `LlamaCppJniConfig` – immutable builder for 37 llama.cpp knobs.
- `AiCompletionParser` – strips internal thinking blocks from raw completions.

#### Data flow
1. **Load configuration** → `SrcMorphConfiguration` from Maven or external files.
2. **Select files** → `AiConditionEvaluator.matches` against each `AiFileContext`.
3. **Generate facts** → `AiFactExtractor.factsBlock` creates a facts section.
4. **Handle oversize** → `AiOversizeStrategy` decides action.
5. **Create provider** → `AiGenerationProviderFactory.create` returns concrete provider.
6. **Generate text** → provider receives `AiGenerationRequest`, builds request body, calls model, parses response, extracts completion text.
7. **Return result** → plain `String` or `AiGenerationTimings` with metrics.

#### Dependencies
- **Internal**: `AiCondition`, `AiConditionGroup`, `AiRangeCondition`, `AiFileContext`, `AiFactCounter`, `AiPromptDefinition`, `AiModelDefinition`, `AiFieldGenerationConfig`, `AiFactDefinition`, `AiCalibration`, `AiPromptSupport`, `AiGenerationRequest`, `AiGenerationTimings`.
- **External**: `java.time.Instant`, `java.util.regex`, `org.jspecify.annotations.Nullable`, Lombok (`@ToString`), Jackson `ObjectMapper`, `net.ladenthin.llama` (local inference), `java.net.URL`.

#### Cross-cutting
- **Immutability & Thread‑safety** – `LlamaCppJniConfig`, provider support, and `AiOversizeStrategy` are immutable; evaluator and extractor are stateless.
- **Validation** – `AiConditionEvaluator.validate` and `AiFactExtractor.validate` enforce correct configuration.
- **Error handling** – Misconfigurations throw `IllegalArgumentException`; malformed responses raise `IOException`.
- **Timing metrics** – Providers expose `generateWithTimings` returning `AiGenerationTimings` with throughput and token rates.
- **Configuration knobs** – 37 llama.cpp parameters in `LlamaCppJniConfig` allow fine‑grained control over model loading, GPU usage, caching, and sampling.
