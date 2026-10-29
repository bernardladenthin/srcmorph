### AiConditionEvaluator.java
- H: 1.0
- C: 529FFBC4
- D: 2026-10-28T22:56:50Z
- T: 2026-10-28T23:16:31Z
- G: 
- A: 0.0.0
- X: file
---
> Evaluates file filtering conditions against file context to determine matches and validate structure.

#### Purpose
*   Validates and evaluates `AiCondition` trees for file filtering.
*   Determines if file indexing requires line counting.

#### Type
*   `final class AiConditionEvaluator`
*   No extends or implements
*   No annotations

#### Input
*   `AiCondition` condition tree
*   `AiFileContext` file facts (name, size, lines, modified time, path)
*   `AiConditionGroup` groups (and/or)
*   `AiRangeCondition` size/lines bounds
*   `String` extensions, dates, globs
*   `List<String>` extensions list

#### Output
*   `boolean` match result
*   `void` validation (throws on error)
*   `boolean` lines usage flag
*   `long` epoch milliseconds (internal)

#### Core logic
*   Recursively traverse `AiCondition` tree (and/or/not/combinators)
*   Check exactly one branch set per node via `branchCount`
*   Evaluate leaf conditions: extensions, size, lines, dates, path glob
*   Validate constraints: non-empty groups, valid ISO-8601 dates, non-blank globs
*   Detect `lines` condition presence to optimize file reading
*   Parse ISO-8601 dates to epoch milliseconds
*   Match path globs using `AiSourceExcludeFilter`

#### Public API
*   `AiConditionEvaluator() -> void` Constructor
*   `matches(AiCondition, AiFileContext) -> boolean` Checks if file satisfies condition tree
*   `validate(AiCondition) -> void` Ensures condition tree structure is valid
*   `usesLines(AiCondition) -> boolean` Reports if line counting is required

#### Dependencies
*   `net.ladenthin.srcmorph.config.AiCondition`
*   `net.ladenthin.srcmorph.config.AiFileContext`
*   `net.ladenthin.srcmorph.config.AiConditionGroup`
*   `net.ladenthin.srcmorph.config.AiRangeCondition`
*   `net.ladenthin.srcmorph.support.AiSourceExcludeFilter`
*   `java.time.Instant`
*   `java.time.format.DateTimeParseException`
*   `java.util.List`
*   `java.util.Collections`
*   `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
*   `IllegalArgumentException` for invalid condition structure (empty groups, missing bounds, invalid dates)
*   `IllegalStateException` if `matches` finds no branch set (logic error)
*   `DateTimeParseException` caught and wrapped in `IllegalArgumentException`

#### Concurrency
*   Stateless class (no fields)
*   Thread-safe for concurrent evaluation
