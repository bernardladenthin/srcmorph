### AiConditionEvaluator.java
- H: 1.0
- C: 529FFBC4
- D: 2026-11-01T01:35:52Z
- T: 2026-11-01T01:35:52Z
- G: 
- A: 0.0.0
- X: file
---
> Evaluates file conditions to include or exclude files based on metadata, paths, extensions, and nested logic.

#### Purpose
- Matches `AiCondition` trees against file facts in `AiFileContext`.
- Validates condition configuration and detects whether line counts are required.

#### Type
- `public final class AiConditionEvaluator` — class; no superclass or implemented interfaces.
- Stateless evaluator; has a public no-op constructor.

#### Input
- `matches(AiCondition, AiFileContext)` consumes condition branches and file name, relative path, size, line count, and modification time.
- `validate(AiCondition)` and `usesLines(AiCondition)` consume condition trees.
- Reads ISO-8601 instant strings, extension lists, range conditions, and combinator children.

#### Output
- Returns match and line-count-needed booleans; validation returns no value.
- Does not mutate input or write resources.
- Throws errors for invalid conditions or invalid date strings.

#### Core logic
- `matches` evaluates the first non-null branch in order: `and`, `or`, `not`, extensions, size, lines, modified-after, modified-before, path glob.
- `and` requires every child to match; `or` requires at least one; `not` negates its child.
- Extension matching uses `fileName.endsWith(extension)`; size and line leaves delegate to range `contains`.
- Modification-time leaves parse instants to epoch milliseconds and use strict `>` or `<` comparisons.
- Path glob matching delegates to `AiSourceExcludeFilter.isExcluded(relativePath)`.
- `validate` requires exactly one branch or leaf per node; recursively validates combinators, requiring nonempty child lists.
- Validation rejects empty extension lists, unbounded size/line ranges, invalid instants, and blank path globs.
- `usesLines` recursively checks the `lines` leaf and children of `and`, `or`, or `not`.

#### Public API
- `AiConditionEvaluator()` -> constructor; creates evaluator.
- `matches(AiCondition, AiFileContext)` -> `boolean`; evaluates file conditions.
- `validate(AiCondition)` -> `void`; validates condition tree.
- `usesLines(AiCondition)` -> `boolean`; detects line-count conditions.

#### Dependencies
- `AiCondition`, `AiConditionGroup`, `AiRangeCondition`, `AiFileContext`
- `AiSourceExcludeFilter`
- `Instant`, `DateTimeParseException`
- `Collections`, `List`
- JSpecify `Nullable`

#### Exceptions / Errors
- `validate` throws `IllegalArgumentException` for branch-count errors, empty groups or extension lists, unbounded ranges, blank globs, and invalid instants.
- `parseEpochMilli` wraps `DateTimeParseException` in `IllegalArgumentException`.
- `matches` throws `IllegalStateException` if no branch is set; it assumes prior validation.

#### Concurrency
- No mutable instance state or synchronization; evaluation state is local to each call.
