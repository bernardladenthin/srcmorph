### java/config
- H: 1.0
- C: D38DF5EA
- D: 2026-10-28T22:56:50Z
- T: 2026-10-28T23:38:45Z
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
> Validates and evaluates AI file filtering conditions and configuration trees for source morphing indexing.

#### Purpose
*   Validates and evaluates `AiCondition` trees for file filtering.
*   Determines if file indexing requires line counting.

#### Responsibilities
*   Evaluate `AiCondition` trees against `AiFileContext` to determine matches.
*   Manage logical groups (`AiConditionGroup`) for Maven plugin binding.
*   Validate condition tree structure and constraints (dates, globs, bounds).
*   Detect `lines` condition presence to optimize file reading.

#### Key units
*   `AiConditionEvaluator`: Evaluates file filtering conditions against `AiFileContext`.
*   `AiConditionGroup`: Container for child `AiCondition` instances.
*   `AiCondition`: Condition tree node (referenced in dependencies).
*   `AiFileContext`: File facts (name, size, lines, modified time, path).
*   `AiRangeCondition`: Size/lines bounds (referenced in dependencies).

#### Data flow
*   Input `AiCondition` tree + `AiFileContext` -> `AiConditionEvaluator.matches()`.
*   Output `boolean` match result or `void` validation error.
*   `AiConditionGroup` holds `List<AiCondition>` for logical combination.
*   `AiConditionEvaluator.usesLines()` reports line counting requirement.

#### Dependencies
*   Internal: `net.ladenthin.srcmorph.config.AiCondition`, `AiFileContext`, `AiSourceExcludeFilter`.
*   External: `java.time.Instant`, `java.util.List`, `org.jspecify.annotations.Nullable`, `lombok.ToString`.

#### Cross-cutting
*   `AiConditionEvaluator` is stateless and thread-safe.
*   `AiConditionGroup` is mutable and not thread-safe.
*   Validation throws `IllegalArgumentException` for invalid condition structure.
*   `DateTimeParseException` caught and wrapped in `IllegalArgumentException`.
