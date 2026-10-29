### AiConditionEvaluator.java
- H: 1.0
- C: 529FFBC4
- D: 2026-10-29T03:15:31Z
- T: 2026-10-29T03:15:32Z
- G: 
- A: 0.0.0
- X: file
---
> Evaluates and validates complex nested condition trees against file metadata to determine inclusion or exclusion.

#### Purpose
*   Provides logic to evaluate `AiCondition` trees against `AiFileContext` facts.
*   Validates the structural integrity of condition trees (e.g., ensuring exactly one branch is set).
*   Determines if a condition tree requires file content access (line counting).

#### Type
final class

#### Input
*   `AiCondition`: The condition tree to evaluate or validate.
*   `AiFileContext`: The file metadata (name, size, lines, timestamps, path).
*   `List<AiCondition>`: Children of combinator groups.
*   `String`: ISO-8601 dates, file extensions, and glob patterns.

#### Output
*   `boolean`: Result of `matches` (true if file satisfies condition).
*   `boolean`: Result of `usesLines` (true if line count is required).
*   `void`: `validate` performs side-effect validation by throwing exceptions on failure.

#### Core logic
*   **Recursive Matching**: Traverses `AiCondition` trees:
    *   `and`: Returns true if all children match.
    *   `or`: Returns true if any child matches.
    *   `not`: Inverts the result of the child condition.
*   **Leaf Evaluation**:
    *   `extensions`: Checks if file name ends with any provided extension.
    *   `size`: Checks if `context.sizeBytes()` is within `AiRangeCondition`.
    *   `lines`: Checks if `context.lineCount()` is within `AiRangeCondition`.
    *   `modifiedAfter`/`modifiedBefore`: Compares `context.lastModifiedEpochMilli()` against parsed ISO-8601 timestamps.
    *   `pathGlob`: Uses `AiSourceExcludeFilter` to check if the relative path matches the glob.
*   **Validation**:
    *   Enforces "exactly one branch" rule per node.
    *   Ensures `and`/`or` groups are non-empty.
    *   Verifies `AiRangeCondition` has at least one bound.
    *   Verifies `pathGlob` is not blank.
    *   Validates ISO-8601 date formats.
*   **Optimization**: `usesLines` performs a pre-scan of the tree to identify if the indexer needs to perform expensive file reads for line counts.

#### Public API
*   `AiConditionEvaluator()` -> void (Constructor)
*   `matches(AiCondition, AiFileContext)` -> boolean (Evaluates condition tree against file facts)
*   `validate(AiCondition)` -> void (Validates tree structure and constraints)
*   `usesLines(AiCondition)` -> boolean (Checks if any node requires line count)

#### Dependencies
*   `net.ladenthin.srcmorph.config.AiCondition`
*   `net.ladenthin.srcmorph.config.AiConditionGroup`
*   `net.ladenthin.srcmorph.config.AiFileContext`
*   `net.ladenthin.srcmorph.config.AiRangeCondition`
*   `net.ladenthin.srcmorph.support.AiSourceExcludeFilter`
*   `java.time.Instant`
*   `java.time.format.DateTimeParseException`
*   `java.util.Collections`
*   `java.util.List`
*   `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
*   `IllegalArgumentException`: Thrown if a node has zero or multiple branches, if a group is empty, if a range is unbounded, if a date is unparseable, or if a glob is blank.
*   `IllegalStateException`: Thrown if `matches` encounters a node with no valid branch set (should be caught by `validate` first).

#### Concurrency
*   The class is stateless and thread-safe.
