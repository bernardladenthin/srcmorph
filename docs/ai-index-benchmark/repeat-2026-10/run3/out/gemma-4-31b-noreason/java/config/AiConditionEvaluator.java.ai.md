### AiConditionEvaluator.java
- H: 1.0
- C: 529FFBC4
- D: 2026-10-29T14:19:17Z
- T: 2026-10-29T14:24:55Z
- G: 
- A: 0.0.0
- X: file
---
> Evaluates and validates boolean condition trees to determine if a file matches specific criteria (e.g., size, extension, date, or path glob).

#### Purpose
* Evaluates `AiCondition` trees against `AiFileContext` to determine file inclusion/exclusion.
* Validates the structural integrity and value correctness of `AiCondition` configurations.
* Detects if a condition tree requires file line counts to optimize indexer performance.

#### Type
final class

#### Input
* `AiCondition`: The condition tree to evaluate or validate.
* `AiFileContext`: The file metadata (filename, size, line count, modification date, relative path).
* `String`: ISO-8601 formatted date strings.
* `List<String>`: File extensions or glob patterns.

#### Output
* `boolean`: Result of the match evaluation or whether line counts are required.
* Side effects: Throws `IllegalArgumentException` or `IllegalStateException` on invalid configurations.

#### Core logic
* **Recursive Evaluation**: Walks the `AiCondition` tree. Handles logical operators (`and`, `or`, `not`) recursively and evaluates leaf conditions (extensions, size, lines, dates, globs).
* **Validation**: Ensures each `AiCondition` node defines exactly one criterion. Checks for empty combinator groups, unbounded ranges in `AiRangeCondition`, blank globs, and malformed ISO-8601 dates.
* **Line Count Detection**: Recursively scans the tree to see if any `lines` condition exists, signaling the indexer to read file contents.
* **Leaf Matching**:
    * Extensions: Checks if `fileName` ends with any provided extension.
    * Range: Uses `AiRangeCondition.contains()` for size and line counts.
    * Dates: Compares `lastModifiedEpochMilli` against parsed ISO-8601 instants.
    * Globs: Delegates to `AiSourceExcludeFilter` for path matching.

#### Public API
* `AiConditionEvaluator()` -> void: Constructor.
* `matches(AiCondition, AiFileContext)` -> boolean: Evaluates if context satisfies condition.
* `validate(AiCondition)` -> void: Validates condition tree structure/values.
* `usesLines(AiCondition)` -> boolean: Checks if tree requires line counts.

#### Dependencies
* `java.time.Instant`
* `java.time.format.DateTimeParseException`
* `java.util.Collections`
* `java.util.List`
* `net.ladenthin.srcmorph.support.AiSourceExcludeFilter`
* `net.ladenthin.srcmorph.config.AiCondition`
* `net.ladenthin.srcmorph.config.AiConditionGroup`
* `net.ladenthin.srcmorph.config.AiFileContext`
* `net.ladenthin.srcmorph.config.AiRangeCondition`
* `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
* `IllegalArgumentException`: Thrown during `validate()` for misconfigured nodes, empty groups, unbounded ranges, blank globs, or invalid ISO-8601 dates.
* `IllegalStateException`: Thrown during `matches()` if a node does not set exactly one branch/leaf.
* `DateTimeParseException`: Caught and re-thrown as `IllegalArgumentException` during date parsing.
