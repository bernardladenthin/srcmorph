### AiConditionEvaluator.java
- H: 1.0
- C: 529FFBC4
- D: 2026-11-04T07:35:21Z
- T: 2026-11-04T07:35:22Z
- G: 
- A: 0.0.0
- X: file
- P: 1879E12B
---
> Evaluates file-selection conditions using extension, size, line-count, modification-time, and path-glob rules, while validating condition trees and identifying when line counts are required.

#### Purpose
- Match `AiFileContext` file facts against `AiCondition` trees.
- Validate condition configuration and detect whether any condition requires line counts.

#### Type
- `public final class AiConditionEvaluator` — class; no supertypes or annotations.

#### Input
- `AiConditionEvaluator()` — no arguments.
- `matches(AiCondition condition, AiFileContext context)` — condition tree and file facts.
- `validate(AiCondition condition)` and `usesLines(AiCondition condition)` — condition tree.
- Reads condition branches and group children; file name, size, line count, modification time, and relative path from `AiFileContext`.

#### Output
- `matches` returns whether the file matches the condition.
- `validate` returns no value; rejects invalid condition trees.
- `usesLines` returns whether a `lines` condition occurs in the tree.
- No persistent state or resource writes.

#### Core logic
- `matches` evaluates `and` children with all-match semantics, `or` children with any-match semantics, and `not` by negating its child.
- Leaf matching checks extension suffixes, size and line-count ranges, strict modification-time bounds, or a path glob through `AiSourceExcludeFilter`.
- `validate` requires exactly one branch or leaf per node; recursively validates combinator children.
- Rejects empty `and`/`or` groups and extension lists, unbounded size/line ranges, blank path globs, and invalid ISO-8601 timestamps.
- `usesLines` recursively searches combinator children and `not` descendants for a line-count condition.

#### Public API
- `AiConditionEvaluator()` -> `AiConditionEvaluator` — create evaluator.
- `matches(AiCondition, AiFileContext)` -> `boolean` — test file against condition.
- `validate(AiCondition)` -> `void` — validate condition tree.
- `usesLines(AiCondition)` -> `boolean` — detect line-count conditions.

#### Dependencies
- `AiCondition`, `AiConditionGroup`, `AiRangeCondition`, `AiFileContext`
- `AiSourceExcludeFilter`
- `Instant`, `DateTimeParseException`, `Collections`, `List`
- `@Nullable`

#### Exceptions / Errors
- `validate` throws `IllegalArgumentException` for invalid branch counts, empty groups or extensions, unbounded ranges, blank globs, and invalid timestamps.
- Invalid timestamp parsing wraps `DateTimeParseException` in `IllegalArgumentException`.
- `matches` throws `IllegalStateException` if no condition branch matches the expected branch selection.

#### Concurrency
- Stateless evaluator; no synchronization or mutable instance state.
