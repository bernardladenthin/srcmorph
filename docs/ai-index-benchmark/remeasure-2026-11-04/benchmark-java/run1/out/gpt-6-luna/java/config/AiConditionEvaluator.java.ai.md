### AiConditionEvaluator.java
- H: 1.0
- C: 529FFBC4
- D: 2026-11-04T07:09:59Z
- T: 2026-11-04T07:10:00Z
- G: 
- A: 0.0.0
- X: file
- P: 36159BF2
---
> Evaluates file-selection rules using extensions, size, line count, modification time, and relative-path globs, and validates rule trees before indexing.

#### Purpose
- Matches `AiCondition` trees against `AiFileContext` file facts.
- Validates rule configuration and detects whether line counting is required.

#### Type
- `public final class AiConditionEvaluator` — final class; no superclass or implemented interfaces.
- No notable annotations.

#### Input
- `AiCondition` tree and `AiFileContext` supplied to evaluation methods.
- Reads condition branches and group children; reads context filename, size, line count, modification time, and relative path.

#### Output
- `matches` returns a boolean match result.
- `usesLines` returns whether a tree contains a line-count condition.
- `validate` returns no value; throws on invalid configuration.
- No persistent state or resource writes.

#### Core logic
- `matches` evaluates `and` children all-true, `or` children any-true, and `not` by negation.
- Leaf checks compare filename suffixes against extensions, size and line count against `AiRangeCondition`, timestamps against parsed ISO-8601 instants, or relative paths against a glob.
- `validate` requires exactly one of nine branch/leaf fields per node; recursively validates combinator children.
- Rejects empty `and`/`or` groups, empty extension lists, unbounded size/line ranges, invalid timestamps, and blank path globs.
- `usesLines` recursively searches `and`, `or`, and `not` trees for a `lines` leaf.

#### Public API
- `AiConditionEvaluator()` -> create evaluator
- `matches(AiCondition, AiFileContext) -> boolean` — test file against condition
- `validate(AiCondition) -> void` — reject invalid condition tree
- `usesLines(AiCondition) -> boolean` — detect line-count requirement

#### Dependencies
- `AiCondition`, `AiConditionGroup`, `AiFileContext`, `AiRangeCondition`
- `AiSourceExcludeFilter`
- `Instant`, `DateTimeParseException`
- `Collections`, `List`, `@Nullable`

#### Exceptions / Errors
- `validate` throws `IllegalArgumentException` for invalid branch counts, groups, leaves, or timestamps.
- Timestamp parsing wraps `DateTimeParseException` in `IllegalArgumentException`.
- `matches` throws `IllegalStateException` if no condition branch is selected; documented to assume prior validation.
- Extension matching ignores null entries; null group child lists are treated as empty by matching and `usesLines`.

#### Concurrency
- Stateless after construction; no synchronization or mutable instance state.
