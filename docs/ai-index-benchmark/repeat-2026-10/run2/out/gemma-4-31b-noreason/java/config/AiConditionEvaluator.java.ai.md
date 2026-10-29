### AiConditionEvaluator.java
- H: 1.0
- C: 529FFBC4
- D: 2026-10-29T13:48:14Z
- T: 2026-10-29T13:52:46Z
- G: 
- A: 0.0.0
- X: file
---
> Evaluates and validates recursive condition trees to determine if a file matches specific criteria (size, date, path, etc.) based on its context.

#### Purpose
* Evaluates boolean logic trees (`and`, `or`, `not`) and leaf conditions against file metadata.
* Validates the structural integrity and value correctness of `AiCondition` configurations.
* Detects if a condition tree requires reading file line counts to optimize indexing.

#### Type
Final class.

#### Input
* `AiCondition`: The condition tree to evaluate or validate.
* `AiFileContext`: Metadata about the file (filename, size, line count, modification date, relative path).
* `String`: ISO-8601 formatted timestamps for date comparisons.
* `List<String>`: File extensions or glob patterns.

#### Output
* `boolean`: Result of the match evaluation or whether line counts are required.
* `void`: Successful validation (otherwise throws exception).

#### Core logic
* **Recursive Evaluation**: Walks the `AiCondition` tree. `and` requires all children to match; `or` requires at least one; `not` inverts the result of the child.
* **Leaf Matching**:
    * `extensions`: Checks if `AiFileContext.fileName()` ends with any provided extension.
    * `size`/`lines`: Uses `AiRangeCondition.contains()` to check if file size or line count falls within bounds.
    * `modifiedAfter`/`modifiedBefore`: Parses ISO-8601 strings to epoch milliseconds and compares against `AiFileContext.lastModifiedEpochMilli()`.
    * `pathGlob`: Uses `AiSourceExcludeFilter` to check if the relative path matches a glob pattern.
* **Validation**: Ensures exactly one condition type is set per node. Checks for empty lists in groups/extensions, unbounded ranges in size/lines, and valid ISO-8601 date formats.
* **Line Requirement Detection**: Recursively scans the tree for any `lines` condition to signal that the indexer must perform a costly line-count operation.

#### Public API
* `AiConditionEvaluator() -> void`: No-op constructor.
* `matches(AiCondition, AiFileContext) -> boolean`: Evaluates if context satisfies the condition.
* `validate(AiCondition) -> void`: Validates condition tree configuration.
* `usesLines(AiCondition) -> boolean`: Checks if tree contains a line-count condition.

#### Dependencies
* `AiCondition`
* `AiConditionGroup`
* `AiFileContext`
* `AiRangeCondition`
* `AiSourceExcludeFilter`
* `java.time.Instant`
* `java.time.format.DateTimeParseException`
* `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
* `IllegalArgumentException`: Thrown during `validate` or date parsing for misconfigured nodes, empty combinators, unbounded ranges, blank globs, or invalid ISO-8601 dates.
* `IllegalStateException`: Thrown by `matches` if a node does not set exactly one branch/leaf.
