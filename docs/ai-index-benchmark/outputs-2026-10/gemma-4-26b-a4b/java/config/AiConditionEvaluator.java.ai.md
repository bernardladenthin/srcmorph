### AiConditionEvaluator.java
- H: 1.0
- C: 529FFBC4
- D: 2026-10-31T10:06:17Z
- T: 2026-10-31T13:26:44Z
- G: 
- A: 0.0.0
- X: file
---
> Evaluates and validates hierarchical condition trees against file metadata to determine if files match specific criteria.

#### Purpose
* Determines if a file satisfies complex logical conditions (AND, OR, NOT, extensions, size, line counts, timestamps, or path globs).
* Validates the structural integrity and correctness of condition trees.
* Identifies if a condition tree requires file content reading (line counts) to optimize indexing.

#### Type
final class; no implements/extends.

#### Input
* `AiCondition` tree structures.
* `AiFileContext` containing file metadata (name, size, line count, relative path, last modified timestamp).
* ISO-8601 formatted date strings.

#### Output
* Boolean match results.
* Validation status (via exceptions).
* Boolean indicating if line counts are required.

#### Core logic
* **Recursive Matching**: Traverses `AiCondition` trees using logical operators (AND/OR/NOT) and leaf predicates (extensions, size, lines, dates, globs).
* **Strict Validation**: Ensures every `AiCondition` node has exactly one branch/leaf set and that values (like dates or ranges) are well-formed.
* **Optimization Check**: Recursively scans the tree via `usesLines` to signal whether the indexer must perform expensive file reads for line counts.
* **Predicate Evaluation**: 
    * Extensions: Checks if filename ends with provided strings.
    * Size/Lines: Uses `AiRangeCondition` to check bounds.
    * Timestamps: Parses ISO-8601 strings to compare against file epoch milliseconds.
    * Path: Uses `AiSourceExcludeFilter` for glob pattern matching.

#### Public API
* `AiConditionEvaluator()` -> `void` (Constructor)
* `matches(AiCondition, AiFileContext)` -> `boolean` (Checks if context satisfies condition)
* `validate(AiCondition)` -> `void` (Validates tree integrity; throws exception on error)
* `usesLines(AiCondition)` -> `boolean` (Checks if tree requires line count data)

#### Dependencies
* `java.time.Instant`
* `java.time.format.DateTimeParseException`
* `java.util.List`
* `net.ladenthin.srcmorph.support.AiSourceExcludeFilter`
* `org.jspecify.annotations.Nullable`
* `AiCondition`
* `AiConditionGroup`
* `AiFileContext`
* `AiRangeCondition`

#### Exceptions / Errors
* `IllegalArgumentException`: Thrown during `validate` for empty groups, unbounded ranges, invalid ISO-8601 dates, blank globs, or nodes with multiple/zero branches.
* `IllegalStateException`: Thrown during `matches` if a node is encountered that violates the single-branch rule.

#### Concurrency
* Stateless implementation; thread-safe for concurrent use.
