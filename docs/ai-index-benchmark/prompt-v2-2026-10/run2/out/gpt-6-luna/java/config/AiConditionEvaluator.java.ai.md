### AiConditionEvaluator.java
- H: 1.0
- C: 529FFBC4
- D: 2026-10-31T14:44:17Z
- T: 2026-10-31T14:44:18Z
- G: 
- A: 0.0.0
- X: file
---
> Evaluates file conditions for source indexing, including metadata filters and recursive logical combinations.

#### Purpose
- Matches an `AiFileContext` against an `AiCondition` tree.
- Validates condition configuration and identifies trees that require line counts.

#### Type
- `public final class AiConditionEvaluator` — final class.
- Package: `net.ladenthin.srcmorph.config`.

#### Input
- `AiCondition` nodes and their `and`, `or`, `not`, extension, size, line, modification-time, and path-glob values.
- `AiFileContext` file name, size, line count, last-modified epoch milliseconds, and relative path.
- ISO-8601 instant strings for modification-time conditions.

#### Output
- Boolean condition-match and line-count-usage results.
- Validation has no return value; throws `IllegalArgumentException` for invalid conditions.
- No persistent state or resource writes.

#### Core logic
- `matches` recursively evaluates `and` (all children), `or` (any child), and `not`; evaluates leaf conditions against file context.
- Extension leaves match when the file name ends with any non-null listed extension.
- Size and line leaves delegate range checks to `AiRangeCondition.contains`.
- Modification-time leaves compare context epoch milliseconds strictly after or before the parsed instant.
- Path-glob leaves pass a singleton glob to `AiSourceExcludeFilter.isExcluded` for the relative path.
- `validate` requires exactly one non-null branch/leaf per node; recursively validates combinator children.
- `validate` rejects empty extension lists, unbounded size/line ranges, empty combinator groups, and blank path globs; parses modification-time instants.
- `usesLines` recursively detects line conditions under combinators and negation.

#### Public API
- `AiConditionEvaluator()` -> constructor; creates evaluator.
- `matches(AiCondition, AiFileContext)` -> `boolean`; evaluates file condition.
- `validate(AiCondition)` -> `void`; checks condition configuration.
- `usesLines(AiCondition)` -> `boolean`; detects line-count requirements.

#### Dependencies
- `AiCondition`, `AiConditionGroup`, `AiFileContext`, `AiRangeCondition`.
- `AiSourceExcludeFilter`.
- `Instant`, `DateTimeParseException`, `Collections`, `List`, `@Nullable`.

#### Exceptions / Errors
- `validate` throws `IllegalArgumentException` for invalid branch counts, empty groups/extensions, unbounded ranges, blank globs, or invalid instants.
- `parseEpochMilli` wraps `DateTimeParseException` in `IllegalArgumentException`.
- `matches` throws `IllegalStateException` when no condition branch is set.
- Null group child lists are tolerated by matching and `usesLines`; validation rejects them.

#### Concurrency
- No mutable instance state; operations use local variables.
