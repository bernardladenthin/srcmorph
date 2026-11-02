### java/config
- H: 1.0
- C: 6E389E7C
- D: 2026-11-01T23:56:56Z
- T: 2026-11-02T00:03:15Z
- G: 
- A: 0.0.0
- X: package
- F: [AiConditionEvaluator.java](AiConditionEvaluator.java.ai.md)
- F: [AiConditionGroup.java](AiConditionGroup.java.ai.md)
- F: [AiFactExtractor.java](AiFactExtractor.java.ai.md)
- F: [AiGenerationConfig.java](AiGenerationConfig.java.ai.md)
- F: [AiOversizeStrategy.java](AiOversizeStrategy.java.ai.md)
- F: [SrcMorphConfiguration.java](SrcMorphConfiguration.java.ai.md)
---
> A configuration‑driven AI‑powered source‑morphing engine that indexes code files, evaluates selection rules, extracts deterministic facts, and orchestrates AI generation for code summaries and transformations.

#### Purpose
- Central configuration holder for the srcmorph AI code‑generation tool.
- Provides utilities to evaluate file‑selection conditions and extract deterministic facts for AI summarisation.
- Defines strategies for handling oversized source files.

#### Responsibilities
- **Configuration Management** – `SrcMorphConfiguration` aggregates all runtime parameters (file paths, AI providers, model settings, prompt definitions, field‑generation rules, fact definitions).
- **Condition Evaluation** – `AiConditionEvaluator` validates and evaluates logical trees (`and`, `or`, `not`, `extensions`, `size`, `lines`, `modifiedAfter`, `modifiedBefore`, `pathGlob`) against `AiFileContext`.
- **Condition Grouping** – `AiConditionGroup` stores child conditions for combinators.
- **Fact Extraction** – `AiFactExtractor` builds a deterministic facts block from regex counters and validates counter configurations.
- **Oversize Handling** – `AiOversizeStrategy` enumerates routing behaviours for files exceeding model context limits.
- **Generation Configuration** – `AiGenerationConfig` holds AI provider‑specific parameters (model path, sampling, retry, stop strings, caching, GPU/CPU settings, etc.).

#### Key units
- `SrcMorphConfiguration` – mutable bean for all srcmorph settings.
- `AiConditionEvaluator` – stateless validator/evaluator of `AiCondition` trees.
- `AiConditionGroup` – container for child `AiCondition` objects.
- `AiFactExtractor` – static utility for building facts blocks.
- `AiOversizeStrategy` – enum of routing strategies (`FAIL`, `SAMPLE`, `MAP_REDUCE`, `DETERMINISTIC`).
- `AiGenerationConfig` – data holder for AI generation parameters.

#### Data flow
1. **Configuration Load** – `SrcMorphConfiguration` is populated from Maven, JSON/YAML, or programmatic input.
2. **File Routing** – `AiConditionEvaluator` checks each source file against configured `AiCondition` trees to decide inclusion.
3. **Fact Generation** – For included files, `AiFactExtractor` produces a deterministic facts block used to control AI summarisation size.
4. **Oversize Decision** – If a file exceeds context limits, `AiOversizeStrategy` determines whether to fail, sample, map‑reduce, or deterministically truncate.
5. **AI Generation** – `AiGenerationConfig` supplies the AI provider with model path, sampling settings, and other runtime options to generate summaries or transformations.

#### Dependencies
- **Internal** – `AiCondition`, `AiFileContext`, `AiFactCounter`, `AiPromptDefinition`, `AiModelDefinition`, `AiFieldGenerationConfig`, `AiFactDefinition`, `AiCalibration`.
- **External** – `java.time`, `java.util`, `java.util.regex`, `net.ladenthin.srcmorph.support.AiSourceExcludeFilter`, Lombok (`@ToString`), `org.jspecify.annotations.Nullable`.

#### Cross-cutting
- **Thread Safety** – `AiConditionEvaluator` and `AiFactExtractor` are stateless and thread‑safe; other configuration classes are mutable and not thread‑safe.
- **Validation** – `AiConditionEvaluator.validate` and `AiFactExtractor.validate` enforce configuration correctness, throwing `IllegalArgumentException` on errors.
- **Configuration Defaults** – `AiGenerationConfig` and `SrcMorphConfiguration` provide sensible defaults and allow `null` to reset optional fields.
- **Error Handling** – All validation errors surface as unchecked exceptions; no checked exceptions are declared.
