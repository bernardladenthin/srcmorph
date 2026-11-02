### AiConditionEvaluator.java
- H: 1.0
- C: 529FFBC4
- D: 2026-10-31T20:10:35Z
- T: 2026-10-31T20:10:36Z
- G: 
- A: 0.0.0
- X: file
---
> Evaluates file-selection conditions against file metadata, including extensions, size, line count, modification time, and relative-path globs.

#### Purpose
- Matches `AiCondition` trees against `AiFileContext` file facts.
- Validates condition trees and detects whether they require line counts.

#### Type
- `public final class AiConditionEvaluator` — class; no superclass or implemented interfaces.
- Uses a private static final error-message constant; no instance state.

#### Input
- `AiCondition` trees containing one branch or leaf: `and`, `or`, `not`, `extensions`, `size`, `lines`, `modifiedAfter`, `modifiedBefore`, or `pathGlob`.
- `AiFileContext` facts: filename, size in bytes, line count, last-modified epoch milliseconds, and relative path.
- ISO-8601 instant strings for modification-time conditions.

#### Output
- `matches` returns whether the context satisfies the condition.
- `validate` throws on invalid condition configuration.
- `usesLines` returns whether the tree contains a line-count condition.
- No fields or external resources are mutated or written.

#### Core logic
- `matches` evaluates combinators recursively: `and` requires all children, `or` any child, and `not` negates its child.
- Leaf matching checks filename suffixes, size and line ranges, strict modification-time comparisons, or the relative-path glob.
- `validate` requires exactly one non-null branch/leaf per node; recursively validates combinators and rejects empty groups, empty extensions, unbounded ranges, invalid dates, and blank path globs.
- Modification dates are parsed with `Instant.parse` and converted to epoch milliseconds.
- Path-glob matching delegates to `AiSourceExcludeFilter` with a singleton glob.
- `usesLines` recursively checks combinator children and negated conditions.

#### Public API
- `AiConditionEvaluator()` -> `void` — creates evaluator
- `matches(AiCondition, AiFileContext)` -> `boolean` — evaluates condition against file
- `validate(AiCondition)` -> `void` — rejects invalid condition trees
- `usesLines(AiCondition)` -> `boolean` — detects line-count requirements

#### Dependencies
- `AiCondition`, `AiConditionGroup`, `AiRangeCondition`, `AiFileContext`
- `AiSourceExcludeFilter`
- `Instant`, `DateTimeParseException`
- `Collections`, `List`
- `@Nullable` from `org.jspecify.annotations`

#### Exceptions / Errors
- `validate` throws `IllegalArgumentException` for invalid branch counts, empty combinators or extensions, unbounded ranges, invalid ISO-8601 instants, or blank path globs.
- Invalid date parsing wraps `DateTimeParseException` in `IllegalArgumentException`.
- `matches` throws `IllegalStateException` if no branch/leaf is set; its contract assumes prior validation.

#### Concurrency
- Stateless after construction; no synchronization or mutable instance state.
