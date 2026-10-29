### AiConditionEvaluator.java
- H: 1.0
- C: 529FFBC4
- D: 2026-10-29T13:48:14Z
- T: 2026-10-29T13:48:15Z
- G: 
- A: 0.0.0
- X: file
---
> Filters files against nested include conditions using extension, size, line-count, modification-time, and path-glob criteria.

#### Purpose
- Evaluates `AiCondition` trees against `AiFileContext` file facts.
- Validates condition configuration and detects whether line counts are needed.

#### Type
- `final` class: `AiConditionEvaluator`.

#### Input
- `matches(condition, context)` consumes an `AiCondition` tree and file name, size, line count, modification time, and relative path from `AiFileContext`.
- `validate(condition)` and `usesLines(condition)` consume condition trees.
- Path-glob matching creates an `AiSourceExcludeFilter` from the glob.

#### Output
- `matches` returns a boolean; `usesLines` returns a boolean.
- `validate` returns no value and throws on invalid conditions.
- No persistent state or file resources are written.

#### Core logic
- Evaluates the first non-null branch in order: `and`, `or`, `not`, extensions, size, lines, modified-after, modified-before, path glob.
- `and` requires every child to match; `or` requires at least one; `not` negates its child.
- Extension leaves match when the filename ends with any non-null listed extension.
- Range leaves delegate to `AiRangeCondition.contains`; time leaves compare epoch milliseconds strictly (`>` after, `<` before).
- Path-glob leaves delegate matching to `AiSourceExcludeFilter.isExcluded`.
- Validation requires exactly one branch/leaf per node; recursively validates combinators, requires nonempty groups and extension lists, bounded size/line ranges, parseable timestamps, and nonblank path globs.
- `usesLines` recursively searches combinators and negation for a line-count condition.

#### Public API
- `AiConditionEvaluator()` -> `AiConditionEvaluator` — creates evaluator.
- `matches(AiCondition, AiFileContext)` -> `boolean` — tests file conditions.
- `validate(AiCondition)` -> `void` — checks condition configuration.
- `usesLines(AiCondition)` -> `boolean` — detects line-count requirements.

#### Dependencies
- `AiCondition`, `AiConditionGroup`, `AiRangeCondition`, `AiFileContext`
- `AiSourceExcludeFilter`
- `Instant`, `DateTimeParseException`, `Collections`, `List`
- `@Nullable`

#### Exceptions / Errors
- `validate` throws `IllegalArgumentException` for invalid branch counts, empty groups or extension lists, unbounded ranges, invalid timestamps, and blank globs.
- Invalid ISO-8601 instants are wrapped in `IllegalArgumentException`.
- `matches` throws `IllegalStateException` if no branch is set; it assumes prior validation.

#### Concurrency
- Stateless; no synchronization or mutable instance state.
