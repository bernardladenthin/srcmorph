### AiConditionEvaluator.java
- H: 1.0
- C: 529FFBC4
- D: 2026-11-01T23:42:40Z
- T: 2026-11-01T23:42:42Z
- G: 
- A: 0.0.0
- X: file
- P: C721BDEF
---
> File selection rules can combine extension, size, line-count, modification-time, and path-glob filters to decide which source files match.

#### Purpose
- Evaluates `AiCondition` trees against `AiFileContext` facts.
- Validates condition configuration and detects whether line counts are needed.

#### Type
- `public final class AiConditionEvaluator` — final class.
- No superclass or implemented interfaces.

#### Input
- `matches(AiCondition, AiFileContext)` receives a condition tree and file facts.
- `validate(AiCondition)` and `usesLines(AiCondition)` receive a condition tree.
- Reads file name, byte size, line count, modification timestamp, and relative path from `AiFileContext`.

#### Output
- `matches` returns whether the file satisfies the condition.
- `validate` returns no value; throws for invalid condition configuration.
- `usesLines` returns whether any node requires a line count.
- No persistent state or external resources are written.

#### Core logic
- Evaluates `and` children with all-match semantics, `or` children with any-match semantics, and `not` by negating its child.
- Matches leaves against filename suffixes, byte-size and line-count ranges, strict modification-time comparisons, or relative-path globs.
- Uses `AiSourceExcludeFilter` to evaluate path globs.
- Validates that each node sets exactly one of nine branch/leaf fields; recursively validates combinator children.
- Rejects empty `and`/`or` groups, empty extension lists, unbounded size/line ranges, invalid ISO-8601 instants, and blank path globs.
- Recursively checks `and`, `or`, and `not` trees for `lines` conditions.

#### Public API
- `AiConditionEvaluator()` -> `void` — creates evaluator.
- `matches(AiCondition, AiFileContext)` -> `boolean` — test file conditions.
- `validate(AiCondition)` -> `void` — validate condition tree.
- `usesLines(AiCondition)` -> `boolean` — detect line-count requirement.

#### Dependencies
- `AiCondition`, `AiConditionGroup`, `AiRangeCondition`, `AiFileContext`
- `AiSourceExcludeFilter`
- `Instant`, `DateTimeParseException`, `Collections`, `List`
- `@Nullable`

#### Exceptions / Errors
- `validate` throws `IllegalArgumentException` for invalid nodes, groups, ranges, extensions, dates, or globs.
- Invalid ISO-8601 instants are wrapped in `IllegalArgumentException` with the parse exception as cause.
- `matches` throws `IllegalStateException` if no recognized branch is set; documented to assume prior validation.
- Null group children are treated as empty during matching and line-use detection; null extensions are skipped.

#### Concurrency
- No mutable instance state or synchronization; evaluation uses local variables.
