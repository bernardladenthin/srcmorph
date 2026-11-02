### AiConditionEvaluator.java
- H: 1.0
- C: 529FFBC4
- D: 2026-11-01T23:26:17Z
- T: 2026-11-01T23:26:19Z
- G: 
- A: 0.0.0
- X: file
- P: C721BDEF
---
> File-selection conditions let the indexer include or exclude files by extension, size, line count, modification time, and relative-path glob.

#### Purpose
- Evaluate `AiCondition` trees against file metadata in `AiFileContext`.
- Validate condition configuration and detect whether line counting is required.

#### Type
- `public final class AiConditionEvaluator` — final class.
- No superclass or implemented interfaces; no type parameters or notable annotations.

#### Input
- `matches(AiCondition, AiFileContext)` consumes a condition tree and file name, size, line count, modification time, and relative path.
- `validate(AiCondition)` and `usesLines(AiCondition)` consume condition trees.
- Path-glob matching constructs an `AiSourceExcludeFilter` from the glob.

#### Output
- `matches` returns whether the file satisfies the condition.
- `validate` returns no value; throws for invalid configuration.
- `usesLines` returns whether any tree node has a `lines` condition.
- No fields or external resources are mutated or written.

#### Core logic
- `matches` evaluates `and` children with all-match semantics, `or` children with any-match semantics, and `not` by negating its child.
- Leaf matching checks extension suffixes, byte-size and line-count ranges, strict modification-time comparisons, or relative-path glob exclusion.
- `validate` requires exactly one of nine branch/leaf fields per node; recursively validates groups and `not`.
- Rejects empty `and`/`or` groups, empty extension lists, unbounded size/line ranges, invalid ISO-8601 timestamps, and blank path globs.
- `usesLines` recursively searches combinators and negation to detect line-count conditions.
- Glob matching delegates to `AiSourceExcludeFilter.isExcluded`; timestamps parse with `Instant.parse` and convert to epoch milliseconds.

#### Public API
- `AiConditionEvaluator()` -> `AiConditionEvaluator` — creates evaluator.
- `matches(AiCondition, AiFileContext)` -> `boolean` — evaluate file condition.
- `validate(AiCondition)` -> `void` — reject invalid condition trees.
- `usesLines(AiCondition)` -> `boolean` — detect line-count requirements.

#### Dependencies
- `AiCondition`, `AiConditionGroup`, `AiRangeCondition`, `AiFileContext`
- `AiSourceExcludeFilter`
- `Instant`, `DateTimeParseException`, `Collections`, `List`
- `Nullable`

#### Exceptions / Errors
- `validate` throws `IllegalArgumentException` for invalid branch count, empty groups/extensions, unbounded ranges, blank globs, and invalid dates.
- `parseEpochMilli` wraps `DateTimeParseException` in `IllegalArgumentException`.
- `matches` throws `IllegalStateException` if no branch matches the node’s configured fields; documented to assume prior validation.
- `matches` and `usesLines` do not explicitly handle null condition or context arguments.

#### Concurrency
- No mutable instance state or synchronization; evaluator operations use local variables.
