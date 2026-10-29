### AiConditionEvaluator.java
- H: 1.0
- C: 529FFBC4
- D: 2026-10-28T21:24:22Z
- T: 2026-10-28T21:24:23Z
- G: 
- A: 0.0.0
- X: file
---
> Evaluates and validates conditional logic trees to determine if a file matches specific criteria based on metadata and content.

#### Purpose
* Evaluates `AiCondition` trees against `AiFileContext` to determine file inclusion/exclusion.
* Validates `AiCondition` configuration to ensure nodes are correctly defined.
* Detects if a condition tree requires file line counts to avoid unnecessary file reading.

#### Type
final class

#### Input
* `AiCondition` condition trees.
* `AiFileContext` containing file metadata (filename, size, modification date, relative path, line count).
* `AiConditionGroup` and `AiRangeCondition` objects.

#### Output
* `boolean`: Result of match evaluation or line-usage check.
* Side effects: Throws exceptions on invalid configurations.

#### Core logic
* **Recursive Matching**:
    * `and` groups: Returns `false` if any child fails to match.
    * `or` groups: Returns `true` if any child matches.
    * `not` nodes: Inverts the result of the child match.
* **Leaf Evaluation**:
    * Extensions: Checks if `AiFileContext.fileName()` ends with any provided extension.
    * Size/Lines: Uses `AiRangeCondition.contains()` against `AiFileContext` size or line count.
    * Modification Dates: Parses ISO-8601 strings to epoch milliseconds and compares against `AiFileContext.lastModifiedEpochMilli()`.
    * Path Glob: Uses `AiSourceExcludeFilter` to check if `AiFileContext.relativePath()` matches the glob.
* **Validation**:
    * Ensures exactly one branch/leaf field is set per `AiCondition` node.
    * Verifies `and`/`or` groups contain at least one child.
    * Verifies `extensions` lists are not empty.
    * Verifies `AiRangeCondition` (size/lines) has at least one bound.
    * Validates ISO-8601 date formats.
    * Ensures `pathGlob` is not blank.
* **Line Usage Detection**: Recursively traverses the tree to find any `lines` condition.

#### Public API
* `matches(AiCondition, AiFileContext) -> boolean` Evaluates if context satisfies condition.
* `validate(AiCondition) -> void` Validates condition tree configuration.
* `usesLines(AiCondition) -> boolean` Checks if line count is needed.

#### Dependencies
* `AiCondition`
* `AiFileContext`
* `AiConditionGroup`
* `AiRangeCondition`
* `AiSourceExcludeFilter`
* `java.time.Instant`
* `java.time.format.DateTimeParseException`
* `java.util.List`
* `java.util.Collections`

#### Exceptions / Errors
* `IllegalArgumentException`: Thrown during `validate` for misconfigured nodes or during `parseEpochMilli` for invalid ISO-8601 dates.
* `IllegalStateException`: Thrown by `matches` if a node does not set exactly one branch/leaf.
