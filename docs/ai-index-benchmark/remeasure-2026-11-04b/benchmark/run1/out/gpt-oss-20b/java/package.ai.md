### java
- H: 1.0
- C: 643999B1
- D: 2026-11-04T11:46:43Z
- T: 2026-11-04T11:55:06Z
- G: 
- A: 0.0.0
- X: package
- F: [config/](config/package.ai.md)
- F: [provider/](provider/package.ai.md)
---
> A framework that orchestrates AI‑driven source‑code analysis, generation, and provider abstraction for Maven‑based projects.

#### Purpose
- Supply a configuration‑driven AI source‑morphing engine.
- Abstract multiple LLM back‑ends (local llama.cpp, OpenAI‑compatible APIs, mock) for text generation.
- Provide utilities for request construction, response parsing, and performance metrics.

#### Responsibilities
- **Configuration & rule evaluation** – `SrcMorphConfiguration`, `AiConditionEvaluator`, `AiFactExtractor`, `AiOversizeStrategy`.
- **Provider abstraction & factory** – `AiGenerationProvider`, `AiGenerationProviderFactory`, concrete providers (`MockAiGenerationProvider`, `LlamaCppJniAiGenerationProvider`, `OpenAiCompatibleAiGenerationProvider`).
- **Request/response handling** – `OpenAiCompatibleProviderSupport`, `AiCompletionParser`, `AiGenerationTimings`.
- **Local model orchestration** – `LlamaCppJniConfig`, `LlamaCppJniAiGenerationProvider`.
- **Utility & cross‑cutting** – defensive copying, stateless utilities, error handling, timing extraction.

#### Key units
- `SrcMorphConfiguration` – mutable bean for Maven/JSON/YAML binding.
- `AiConditionEvaluator` – stateless validator/matcher for `AiCondition` trees.
- `AiFactExtractor` – static regex‑based fact counter.
- `AiOversizeStrategy` – enum (`FAIL`, `SAMPLE`, `MAP_REDUCE`, `DETERMINISTIC`).
- `AiGenerationProvider` – interface for `generate`, `generateWithTimings`, `close`.
- `AiGenerationProviderFactory` – resolves provider key and constructs provider.
- `LlamaCppJniConfig` – immutable builder for llama.cpp JNI parameters.
- `LlamaCppJniAiGenerationProvider` – lazy‑loads llama.cpp model, parses chat, returns timings.
- `OpenAiCompatibleProviderSupport` – builds JSON payload, resolves endpoint, parses `ChatResponse`, computes `AiGenerationTimings`.
- `AiCompletionParser` – strips Gemma‑4 thinking blocks from completions.
- `AiGenerationTimings` – DTO for generated text and performance metrics.

#### Data flow
1. **Configuration** – `SrcMorphConfiguration` is populated from Maven/JSON/YAML.
2. **File selection** – `AiConditionEvaluator` traverses `AiCondition` trees to decide processing.
3. **Oversize handling** – `AiOversizeStrategy` selects policy for files beyond context limits.
4. **Fact extraction** – `AiFactExtractor` produces a facts block for the prompt.
5. **Provider resolution** – `AiGenerationProviderFactory` creates the appropriate provider.
6. **Generation request** – client calls `generate`/`generateWithTimings` with `AiGenerationRequest`.
7. **Provider execution** – builds request (via `OpenAiCompatibleProviderSupport` or llama.cpp chat template), sends to backend.
8. **Response parsing** – provider parses JSON, extracts text, optionally parses timings.
9. **Cleaning** – `AiCompletionParser` removes internal reasoning blocks.
10. **Result** – `AiGenerationTimings` returned to caller.

#### Dependencies
- **Internal** – `AiCondition`, `AiConditionGroup`, `AiRangeCondition`, `AiFileContext`, `AiSourceExcludeFilter`, `AiFactCounter`, `AiPromptDefinition`, `AiModelDefinition`, `AiFieldGenerationConfig`, `AiFactDefinition`, `AiCalibration`, `AiPromptSupport`, `Java8CompatibilityHelper`, `ChatResponseParser`.
- **External** – `java.time`, `java.util.regex`, `org.jspecify.annotations.Nullable`, Lombok `@ToString`, `net.ladenthin.llama` (model, parameters, JSON parsing), `com.fasterxml.jackson` (JSON), `java.net.URL`, `java.io.IOException`.

#### Cross-cutting
- Defensive copying in `AiConditionGroup.setConditions`.
- Stateless, thread‑safe utilities (`AiFactExtractor`, `AiConditionEvaluator`).
- Immutable configuration (`LlamaCppJniConfig`, provider support classes).
- Centralized error handling: `IllegalArgumentException` for malformed configs, `IOException` for HTTP/response issues.
- Timing metrics unified via `AiGenerationTimings`; local provider parses native timings, remote provider derives from `ChatResponse.usage`.
- Configuration lookup uses `Java8CompatibilityHelper.isBlank` for string validation.
