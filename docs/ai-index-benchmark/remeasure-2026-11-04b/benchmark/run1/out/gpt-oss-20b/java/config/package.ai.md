### java/config
- H: 1.0
- C: 6E389E7C
- D: 2026-11-04T11:46:43Z
- T: 2026-11-04T11:54:56Z
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
> A configuration‑driven AI‑powered source‑code analysis and generation framework that evaluates file‑selection rules, extracts deterministic language facts, and orchestrates model‑based code generation.

#### Purpose
- Provide a Maven‑plugin‑friendly configuration bean for AI‑based source‑morphing.
- Evaluate file‑selection conditions and handle oversized files.
- Extract deterministic language facts via regex counters.
- Supply AI generation parameters for various providers.

#### Responsibilities
- **Configuration aggregation** – `SrcMorphConfiguration` holds all runtime options.
- **Condition evaluation** – `AiConditionEvaluator` validates and matches `AiCondition` trees.
- **Fact extraction** – `AiFactExtractor` builds fact blocks from regex counters.
- **Oversize handling** – `AiOversizeStrategy` defines policies for files beyond context limits.
- **Condition grouping** – `AiConditionGroup` stores child conditions for logical combinators.

#### Key units
- `SrcMorphConfiguration` – mutable bean for Maven/JSON/YAML binding.
- `AiConditionEvaluator` – stateless validator/matcher for `AiCondition` trees.
- `AiFactExtractor` – static utility for regex‑based fact counting.
- `AiOversizeStrategy` – enum with `FAIL`, `SAMPLE`, `MAP_REDUCE`, `DETERMINISTIC`.
- `AiConditionGroup` – holds a defensive‑copy list of child `AiCondition`s.

#### Data flow
1. **Configuration**: `SrcMorphConfiguration` is populated from Maven/JSON/YAML.
2. **File selection**: `AiConditionEvaluator` traverses `AiCondition` trees (possibly nested via `AiConditionGroup`) to decide if a file should be processed.
3. **Oversize decision**: `AiOversizeStrategy.fromConfig` selects a policy for files exceeding the model’s context window.
4. **Fact extraction**: `AiFactExtractor.factsBlock` consumes a list of `AiFactCounter`s to produce a facts block appended to the prompt.
5. **Generation**: `AiGenerationConfig` supplies model, sampling, and runtime options to the AI provider.

#### Dependencies
- **Internal**: `AiCondition`, `AiConditionGroup`, `AiRangeCondition`, `AiFileContext`, `AiSourceExcludeFilter`, `AiFactCounter`, `AiPromptDefinition`, `AiModelDefinition`, `AiFieldGenerationConfig`, `AiFactDefinition`, `AiCalibration`.
- **External**: `java.time.Instant`, `java.time.format.DateTimeParseException`, `java.util.regex.*`, `org.jspecify.annotations.Nullable`, Lombok `@ToString`.

#### Cross-cutting
- Defensive copying in `AiConditionGroup.setConditions`.
- Stateless, thread‑safe utilities (`AiFactExtractor`, `AiConditionEvaluator`).
- Validation logic throws `IllegalArgumentException` for malformed configurations.
- Enum `AiOversizeStrategy` provides a single source of truth for oversize policies.
- Configuration beans (`SrcMorphConfiguration`, `AiGenerationConfig`) expose getters/setters for Maven binding and default values for all options.
