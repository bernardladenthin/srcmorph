### AiConditionEvaluator.java
- H: 1.0
- C: 529FFBC4
- D: 2026-11-01T01:56:59Z
- T: 2026-11-01T01:57:00Z
- G: 
- A: 0.0.0
- X: file
---
> Evaluates file-selection conditions so source indexing can include files by metadata, path, extension, and line-count rules.

#### Purpose
- Matches `AiCondition` trees against file facts in `AiFileContext`.
- Validates condition configuration and detects whether line counts are needed.

#### Type
- `public final class AiConditionEvaluator` — final class; no superclass or implemented interfaces.

#### Input
- `AiCondition` trees with `and`, `or`, `not`, extension, size, line-count, modification-time, or path-glob branches.
- `AiFileContext` values: file name, size in bytes, line count, last-modified epoch milliseconds, and relative path.

#### Output
- `matches` returns whether a file satisfies a condition.
- `validate` and constructor return no value; validation may throw.
- `usesLines` returns whether the tree contains a line-count condition.
- No fields or external resources are mutated or written.

#### Core logic
- `matches` evaluates `and` children with all-match semantics, `or` children with any-match semantics, and `not` by negating its child.
- Leaf checks match file names by extension suffix, size and line count via range containment, modification times by strict epoch-millisecond comparison, and paths through `AiSourceExcludeFilter`.
- `validate` requires exactly one of nine branch/leaf fields per node; recursively validates groups and negations.
- Group validation requires at least one child; extension lists must be nonempty; size and line ranges must have a bound; path globs must not be blank.
- Modification-time values are parsed as ISO-8601 instants.
- `usesLines` recursively searches combinator children and negations for a line-count condition.

#### Public API
- `AiConditionEvaluator()` -> `AiConditionEvaluator` — creates evaluator.
- `matches(AiCondition, AiFileContext)` -> `boolean` — evaluates file condition.
- `validate(AiCondition)` -> `void` — validates condition tree.
- `usesLines(AiCondition)` -> `boolean` — detects line-count requirements.

#### Dependencies
- `AiCondition`, `AiConditionGroup`, `AiRangeCondition`, `AiFileContext` — condition and file-fact types.
- `AiSourceExcludeFilter` — path-glob matching.
- `Instant`, `DateTimeParseException` — ISO-8601 parsing.
- `Collections`, `List` — collection handling.
- `Nullable` — nullable child and extension values.

#### Exceptions / Errors
- `validate` throws `IllegalArgumentException` for nodes with other than one branch, empty groups or extension lists, unbounded ranges, blank globs, or invalid timestamps.
- Invalid ISO-8601 values wrap `DateTimeParseException` as the cause.
- `matches` throws `IllegalStateException` if no branch is set; it assumes prior validation.

#### Concurrency
- Stateless after construction; no synchronization or mutable instance state.
