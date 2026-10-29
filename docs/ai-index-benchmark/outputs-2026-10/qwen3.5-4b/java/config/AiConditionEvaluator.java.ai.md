### AiConditionEvaluator.java
- H: 1.0
- C: 529FFBC4
- D: 2026-10-29T00:01:31Z
- T: 2026-10-29T00:01:32Z
- G: 
- A: 0.0.0
- X: file
---
> Evaluates and validates AI condition trees against file contexts to determine file inclusion/exclusion based on complex logical rules.

#### Purpose
*   Validates AI condition trees for structural correctness (exactly one branch/leaf, non-empty groups, bounded ranges, valid dates, non-blank globs).
*   Evaluates conditions against file facts to determine matches, supporting optimization by reporting if line counts are required.

#### Type
`public final class AiConditionEvaluator` (no extends/implements, no annotations).

#### Input
*   `AiCondition` (condition tree)
*   `AiFileContext` (file facts: name, size, line count, last modified, relative path)
*   `String` (ISO-8601 instant for date parsing)
*   `List<String>` (file extensions)
*   `String` (glob pattern)

#### Output
*   `boolean` (match result from `matches`)
*   `void` (validation failure throws `IllegalArgumentException`)
*   `boolean` (line usage flag from `usesLines`)
*   `long` (epoch milliseconds from `parseEpochMilli`)

#### Core logic
*   `matches` traverses the condition tree recursively, handling `and`, `or`, `not` groups and leaf conditions (extensions, size, lines, modified dates, path glob).
*   `validate` enforces strict rules: exactly one branch/leaf per node, non-empty combinator groups, bounded size/line ranges, valid ISO-8601 dates, and non-blank globs.
*   `usesLines` performs a tree walk to determine if any node requires file content reading for line counting, optimizing indexer behavior.
*   `branchCount` counts non-null branch/leaf fields to ensure exactly one is set.
*   `matchesExtension` checks if a file name ends with any of the provided extensions.
*   `globMatches` delegates path exclusion logic to `AiSourceExcludeFilter`.
*   `parseEpochMilli` converts ISO-8601 strings to epoch milliseconds, throwing `IllegalArgumentException` on invalid format.

#### Public API
*   `matches(condition, context) -> boolean` (evaluates condition against file facts)
*   `validate(condition) -> void` (validates condition tree structure)
*   `usesLines(condition) -> boolean` (reports if line count is needed)

#### Dependencies
*   `AiCondition`
*   `AiConditionGroup`
*   `AiCondition not`
*   `AiRangeCondition`
*   `AiFileContext`
*   `AiSourceExcludeFilter`
*   `Instant`
*   `DateTimeParseException`
*   `List`
*   `Collections`

#### Exceptions / Errors
*   `IllegalArgumentException` thrown for: misconfigured nodes (branch count != 1), empty combinator groups, unbounded size/line ranges, invalid ISO-8601 dates, blank globs.
*   `DateTimeParseException` caught internally during date parsing.
*   `IllegalStateException` thrown in `matches` if validation was bypassed and a node has no branch/leaf set.

#### Concurrency
*   Thread-safe (stateless, no mutable fields).
*   No synchronization required.
