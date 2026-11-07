### AiConditionEvaluator.java
- H: 1.0
- C: 529FFBC4
- D: 2026-11-04T07:19:50Z
- T: 2026-11-04T07:19:50Z
- G: 
- A: 0.0.0
- X: file
- P: 1879E12B
---
> File conditions determine which source files qualify by extension, size, line count, modification time, and relative-path glob.

#### Purpose
- Evaluates `AiCondition` trees against file metadata in `AiFileContext`.
- Validates condition structure and reports whether a tree requires line counts.

#### Type
- `public final class AiConditionEvaluator` — class; no supertypes; no annotations.

#### Input
- `AiConditionEvaluator()` — no parameters.
- `matches(AiCondition condition, AiFileContext context)` — condition tree and file facts, including name, size, line count, modification time, and relative path.
- `validate(AiCondition condition)` and `usesLines(AiCondition condition)` — condition tree.

#### Output
- `matches(...)` returns whether the file satisfies the condition.
- `validate(...)` returns `void`; rejects invalid condition trees.
- `usesLines(...)` returns whether any tree node tests line count.
- No persistent state or resource writes.

#### Core logic
- `matches` evaluates the first configured branch: `and` requires all children; `or` requires any child; `not` negates its child.
- Leaf checks match filename suffixes in `extensions`, byte size and line count through `AiRangeCondition.contains`, strict modification-time comparisons against parsed ISO-8601 instants, or `pathGlob`.
- `pathGlob` delegates to `AiSourceExcludeFilter` with a singleton glob and calls `isExcluded(relativePath)`.
- `validate` requires exactly one of nine branch/leaf fields; recursively validates combinator children.
- `and`/`or` groups must have at least one child. Extension lists must be non-empty; size and line ranges must have a bound; path globs must not be blank.
- `usesLines` recursively checks `and`, `or`, and `not` trees for a line-count condition.

#### Public API
- `AiConditionEvaluator()` -> `AiConditionEvaluator` — create evaluator.
- `matches(AiCondition, AiFileContext)` -> `boolean` — test file against condition.
- `validate(AiCondition)` -> `void` — validate condition tree.
- `usesLines(AiCondition)` -> `boolean` — detect line-count conditions.

#### Dependencies
- `AiCondition`, `AiConditionGroup`, `AiRangeCondition`, `AiFileContext`
- `AiSourceExcludeFilter`
- `Instant`, `DateTimeParseException`, `Collections`, `List`, `Nullable`

#### Exceptions / Errors
- `validate` throws `IllegalArgumentException` for invalid branch count, empty groups/extensions, unbounded ranges, blank globs, or invalid ISO-8601 instants.
- Invalid instants wrap `DateTimeParseException` in `IllegalArgumentException`.
- `matches` throws `IllegalStateException` when no branch is set; assumes prior validation.
- Nullable group child lists are treated as empty by matching and `usesLines`; null extension entries are skipped.

#### Concurrency
- Stateless evaluator; no synchronization or mutable instance fields.
