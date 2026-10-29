### AiConditionEvaluator.java
- H: 1.0
- C: 529FFBC4
- D: 2026-10-29T04:50:45Z
- T: 2026-10-29T04:50:46Z
- G: 
- A: 0.0.0
- X: file
---
> Evaluates and validates AiCondition trees against an AiFileContext for file matching.

#### Purpose
- Evaluates `AiCondition` trees to determine if a file matches the specified conditions.
- Validates the condition tree to ensure proper configuration.
- Reports if any node requires line count information for indexing.

#### Type
- Class: `AiConditionEvaluator`
- Final class with no inheritance or interfaces.

#### Input
- `AiCondition` tree: The condition structure to evaluate.
- `AiFileContext` context: Provides file metadata such as size, last modified time, and path.

#### Output
- `boolean` matches: Indicates if the file matches the condition.
- `void` validate: Throws `IllegalArgumentException` if the condition tree is misconfigured.
- `boolean` usesLines: Indicates if any node in the tree requires line count information.

#### Core logic
- Recursively evaluates `AiCondition` nodes using logical operators (`and`, `or`, `not`).
- Matches file extensions, size, line count, modification time, and path glob patterns.
- Validates each node to ensure exactly one branch/leaf is set and required fields are non-empty.

#### Public API
- `boolean matches(AiCondition condition, AiFileContext context)`: Checks if the file matches the condition.
- `void validate(AiCondition condition)`: Validates the condition tree.
- `boolean usesLines(AiCondition condition)`: Checks if any node requires line count information.

#### Dependencies
- `AiCondition`: Represents the condition tree structure.
- `AiFileContext`: Provides file metadata for evaluation.
- `AiRangeCondition`: Represents range conditions for size and line count.
- `AiSourceExcludeFilter`: Used for path glob matching.

#### Exceptions / Errors
- `IllegalArgumentException`: Thrown if the condition tree is misconfigured or invalid.

#### Concurrency
- No concurrency considerations; methods are stateless and thread-safe.
