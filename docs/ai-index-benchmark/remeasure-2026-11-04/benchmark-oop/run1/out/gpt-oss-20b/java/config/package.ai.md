### java/config
- H: 1.0
- C: 6E389E7C
- D: 2026-11-04T07:19:50Z
- T: 2026-11-04T07:26:58Z
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
> A Java library that configures, validates, and orchestrates AI‑driven source‑code indexing and generation for Maven projects.

#### Purpose
- Central configuration holder for AI‑based source‑code processing.
- Validates file‑selection rules and fact‑counter definitions.
- Provides enums and utilities for oversize handling and prompt generation.

#### Responsibilities
- **Configuration Management** – `SrcMorphConfiguration` aggregates all runtime parameters (directories, model paths, prompt definitions, field routing, fact groups, Llama settings).
- **Condition Evaluation** – `AiConditionEvaluator` checks whether a source file satisfies complex logical trees of `AiCondition` nodes (extensions, size, lines, timestamps, glob paths).
- **Fact Extraction** – `AiFactExtractor` builds deterministic fact blocks from source text using regex counters.
- **Oversize Strategy** – `AiOversizeStrategy` enumerates how to handle files larger than the model context window.
- **Condition Grouping** – `AiConditionGroup` stores child conditions for logical combinators.

#### Key units
- `SrcMorphConfiguration` – mutable JavaBean with 23 fields for Maven/CLI use.
- `AiConditionEvaluator` – stateless validator/evaluator for `AiCondition` trees.
- `AiFactExtractor` – static utility for generating fact strings.
- `AiOversizeStrategy` – enum with `FAIL`, `SAMPLE`, `MAP_REDUCE`, `DETERMINISTIC`.
- `AiConditionGroup` – container for child `AiCondition` objects.

#### Data flow
1. **Setup** – `SrcMorphConfiguration` is populated via Maven plugin or CLI.
2. **Routing** – `AiConditionEvaluator` receives a file’s `AiFileContext` and a condition tree; it recursively evaluates the tree to decide inclusion.
3. **Fact Generation** – For included files, `AiFactExtractor.factsBlock` produces a facts block from configured `AiFactCounter` objects.
4. **Oversize Handling** – If a file exceeds the model context, `AiOversizeStrategy.fromConfig` selects the strategy to apply.

#### Dependencies
- Java core (`java.io.File`, `java.util.*`, `java.time.*`, `java.util.regex.*`).
- `net.ladenthin.srcmorph.support.AiSourceExcludeFilter` for glob matching.
- `net.ladenthin.srcmorph.config.AiCondition`, `AiPromptDefinition`, `AiModelDefinition`, `AiFieldGenerationConfig`, `AiFactDefinition`, `AiCalibration`.
- Lombok (`@ToString`), JSPECIFY (`@Nullable`).

#### Cross-cutting
- Defensive copying in `AiConditionGroup.setConditions`.
- Stateless, thread‑safe utilities (`AiConditionEvaluator`, `AiFactExtractor`).
- Consistent null handling: setters replace `null` with defaults or empty collections.
- Validation throws `IllegalArgumentException` for misconfigured conditions or fact counters.
- Immutable enum `AiOversizeStrategy` with a `fromConfig` parser that defaults to `FAIL`.
