### java/config
- H: 1.0
- C: 6E389E7C
- D: 2026-11-01T14:41:21Z
- T: 2026-11-01T14:48:01Z
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
> Provides AI‑powered source‑code analysis and transformation configuration, validation, and execution support for a Maven‑based code‑morphing tool.

#### Purpose
- Central configuration holder for AI‑driven source‑code transformations.
- Validates AI filter conditions and evaluates file eligibility.
- Generates deterministic fact blocks for oversized files.
- Enumerates handling strategies for oversized source files.

#### Responsibilities
- **Configuration Management** – `SrcMorphConfiguration` aggregates all runtime settings, including AI provider, model paths, prompt definitions, field routing, and fact definitions.
- **Condition Validation & Evaluation** – `AiConditionEvaluator` checks the structure of `AiCondition` trees and determines if a file satisfies the specified filters.
- **Fact Extraction** – `AiFactExtractor` builds a deterministic facts header from regex counters for inclusion in AI summaries.
- **Oversize Strategy Handling** – `AiOversizeStrategy` defines and parses strategies for oversized source files.

#### Key units
- `SrcMorphConfiguration` – mutable JavaBean for all AI‑morph settings.
- `AiConditionEvaluator` – stateless validator/evaluator for `AiCondition` trees.
- `AiFactExtractor` – utility for generating facts blocks from `AiFactCounter` lists.
- `AiOversizeStrategy` – enum of oversize handling modes (`FAIL`, `SAMPLE`, `MAP_REDUCE`, `DETERMINISTIC`).
- `AiConditionGroup` – container for child conditions in logical groups.

#### Data flow
1. **Configuration**: `SrcMorphConfiguration` is populated via Maven, JSON/YAML, or code.
2. **Routing**: `AiConditionEvaluator` validates and evaluates `AiCondition` trees against `AiFileContext` to decide file routing.
3. **Fact Generation**: For oversized files, `AiFactExtractor` creates a facts block using configured `AiFactCounter` regexes.
4. **Strategy Application**: `AiOversizeStrategy.fromConfig` selects the appropriate handling mode for oversized files.

#### Dependencies
- Core Java (`java.util`, `java.time`, `java.io`, `java.util.regex`).
- External libraries: Lombok (`@ToString`), JSPECIFY (`@Nullable`), `net.ladenthin.srcmorph.support.AiSourceExcludeFilter`.
- Configuration domain classes: `AiCondition`, `AiConditionGroup`, `AiRangeCondition`, `AiFileContext`, `AiFactCounter`, `AiPromptDefinition`, `AiModelDefinition`, `AiFieldGenerationConfig`, `AiFactDefinition`, `AiGenerationConfig`.

#### Cross-cutting
- **Thread safety**: `AiConditionEvaluator` and `AiFactExtractor` are stateless and thread‑safe; configuration beans are not synchronized.
- **Validation**: `AiConditionEvaluator.validate` ensures logical groups have at least one child and leaf nodes are correctly formed; `AiFactExtractor.validate` checks regex patterns.
- **Error handling**: Misconfigurations raise `IllegalArgumentException` or `IllegalStateException`; date parsing errors are wrapped in `IllegalArgumentException`.
- **Configuration defaults**: `AiGenerationConfig` and `SrcMorphConfiguration` provide sensible defaults and null‑safe setters.
- **Immutable enums**: `AiOversizeStrategy` is thread‑safe and provides a `DEFAULT` value.
