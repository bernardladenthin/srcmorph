### java/config
- H: 1.0
- C: C1D5CCFB
- D: 2026-10-29T00:30:34Z
- T: 2026-10-29T02:37:13Z
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
> Evaluates and validates {@link AiCondition} trees against an {@link AiFileContext}.

#### Purpose
- To determine if a file matches a set of conditions defined in an {@link AiCondition} tree.
- To validate the conditions to ensure they are correctly configured.
- To identify if any condition requires line count information.

#### Responsibilities
- **AiConditionEvaluator**:
  - Evaluates condition trees against file contexts.
  - Validates condition trees for correctness.
  - Checks if any condition requires line count information.

#### Key Units
- **AiConditionEvaluator**:
  - **matches**: Recursively evaluates condition trees.
  - **validate**: Ensures conditions are correctly configured.
  - **usesLines**: Checks for line count requirements.

#### Data flow
- **Input**: {@link AiCondition} tree and {@link AiFileContext}.
- **Output**: Boolean result and boolean indicating line count requirement.
- **Process**:
  - `matches` method processes the condition tree.
  - `validate` method ensures conditions are valid.
  - `usesLines` method checks for line count conditions.

#### Dependencies
- `java.time.Instant`
- `java.time.format.DateTimeParseException`
- `java.util.Collections`
- `org.jspecify.annotations.Nullable`
- `net.ladenthin.srcmorph.support.AiSourceExcludeFilter`

#### Cross-cutting
- **Shared base types**: Utilizes `java.time` for date handling.
- **Exception handling**: Throws `IllegalArgumentException` for invalid conditions.
- **Threading**: Methods do not rely on shared mutable state.

#### EOF

This summary captures the core functionality and structure of the `AiConditionEvaluator` class, emphasizing its role in evaluating and validating condition trees within the context of file processing.
