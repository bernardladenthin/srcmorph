### java/config
- H: 1.0
- C: 6E389E7C
- D: 2026-11-04T12:56:06Z
- T: 2026-11-04T12:57:58Z
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
> A Maven‑style configuration and runtime engine for AI‑driven source‑code generation, handling file selection, fact extraction, and model‑specific generation settings.

#### Purpose
- Orchestrate AI code generation jobs from source trees.
- Provide flexible file‑selection, fact extraction, and model configuration.

#### Responsibilities
- **Configuration** – `SrcMorphConfiguration` holds all runtime parameters; `AiGenerationConfig` stores per‑model generation options.
- **File selection** – `AiConditionEvaluator` validates and evaluates `AiCondition` trees against file metadata.
- **Fact extraction** – `AiFactExtractor` builds deterministic fact blocks from source text.
- **Oversize handling** – `AiOversizeStrategy` defines how to process files that exceed model context limits.
- **Condition grouping** – `AiConditionGroup` supplies defensive lists of child conditions for logical combinators.

#### Key units
- `SrcMorphConfiguration` – mutable JavaBean for Maven plugin parameters.
- `AiGenerationConfig` – mutable holder for model‑specific generation flags.
- `AiConditionEvaluator` – stateless validator/evaluator for `AiCondition` trees.
- `AiFactExtractor` – static utility for regex‑based fact counting.
- `AiOversizeStrategy` – enum mapping config tokens to handling strategies.
- `AiConditionGroup` – defensive container for child `AiCondition` objects.

#### Data flow
1. **Configuration**: Maven plugin populates `SrcMorphConfiguration` → `AiGenerationConfig`.
2. **File selection**: `AiConditionEvaluator` checks each file against `AiCondition` trees from the configuration.
3. **Fact extraction**: For selected files, `AiFactExtractor.factsBlock` generates fact strings.
4. **Oversize handling**: `AiOversizeStrategy.fromConfig` selects strategy for large files.
5. **Generation**: `AiGenerationConfig` values drive the AI model invocation (not shown in summaries).

#### Dependencies
- Java standard libraries (`java.util`, `java.io`, `java.time`, `java.util.regex`).
- Lombok (`@ToString`).
- `org.jspecify.annotations.Nullable`.
- `net.ladenthin.srcmorph.support.AiSourceExcludeFilter` for glob matching.
- Configuration classes: `AiCondition`, `AiFactCounter`, `AiPromptDefinition`, `AiModelDefinition`, `AiFieldGenerationConfig`, `AiFactDefinition`, `AiCalibration`.

#### Cross‑cutting
- **Immutability**: `AiConditionEvaluator` and `AiFactExtractor` are stateless; thread‑safe.
- **Defensive copying**: `AiConditionGroup.setConditions` copies input collections.
- **Validation**: `AiConditionEvaluator.validate` and `AiFactExtractor.validate` enforce configuration correctness.
- **Error handling**: `IllegalArgumentException` used for misconfiguration; `IllegalStateException` for structural errors.
- **Configuration defaults**: `SrcMorphConfiguration` and `AiGenerationConfig` expose constants for default values.
- **Concurrency**: Mutable configuration classes are not thread‑safe; evaluation and extraction utilities are.
- **Logging/diagnostics**: `@ToString` annotations provide readable state dumps for debugging.
