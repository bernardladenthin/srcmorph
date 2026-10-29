### AiConditionEvaluator.java
- H: 1.0
- C: 529FFBC4
- D: 2026-10-29T09:50:36Z
- T: 2026-10-29T09:50:37Z
- G: 
- A: 0.0.0
- X: file
---
> Evaluates and validates recursive condition trees to determine if a file context matches specific criteria (e.g., size, extension, date, or path glob).

#### Purpose
* Determine if an `AiFileContext` satisfies the logic defined in an `AiCondition` tree.
* Validate the structural integrity and value correctness of `AiCondition` configurations.
* Detect if a condition tree requires file line counts to optimize indexer performance.

#### Type
Final class.

#### Input
* `AiCondition`: The condition tree to evaluate or validate.
* `AiFileContext`: The file metadata (filename, size, line count, modification date, relative path) used for matching.

#### Output
* `boolean`: Result of the match or whether line counts are required.
* `void`: Successful validation (otherwise throws exception).

#### Core logic
* **Recursive Matching**: Walks the `AiCondition` tree.
    * `and` groups: Returns true if all children match.
    * `or` groups: Returns true if any child matches.
    * `not` nodes: Inverts the result of the child match.
    * **Leaf Evaluation**: Checks specific file attributes:
        * `extensions`: Matches if `fileName` ends with any listed extension.
        * `size`/`lines`: Checks if value is within `AiRangeCondition` bounds.
        * `modifiedAfter`/`modifiedBefore`: Compares `lastModifiedEpochMilli` against parsed ISO-8601 instants.
        * `pathGlob`: Uses `AiSourceExcludeFilter` to check if `relativePath` matches the glob.
* **Validation**: Ensures each `AiCondition` node has exactly one active branch/leaf. Validates that groups are not empty, ranges have at least one bound, and date strings are parseable.
* **Line Requirement Detection**: Recursively scans the tree for any `lines` condition to signal that the indexer must read file contents.

#### Public API
* `AiConditionEvaluator() -> void`: Constructor.
* `matches(AiCondition, AiFileContext) -> boolean`: Checks if context satisfies the condition.
* `validate(AiCondition) -> void`: Validates condition tree configuration.
* `usesLines(AiCondition) -> boolean`: Checks if any node requires line counts.

#### Dependencies
* `AiCondition`
* `AiConditionGroup`
* `AiFileContext`
* `AiRangeCondition`
* `AiSourceExcludeFilter`
* `java.time.Instant`
* `java.time.format.DateTimeParseException`
* `java.util.List`
* `java.util.Collections`

#### Exceptions / Errors
* `IllegalArgumentException`: Thrown during `validate` or `parseEpochMilli` for misconfigured nodes, empty groups, unbounded ranges, blank globs, or invalid ISO-8601 dates.
* `IllegalStateException`: Thrown by `matches` if a node does not set exactly one branch/leaf.
