### AiConditionEvaluator.java
- H: 1.0
- C: 529FFBC4
- D: 2026-11-01T14:19:41Z
- T: 2026-11-01T14:19:42Z
- G: 
- A: 0.0.0
- X: file
---
> Filters files against nested AI conditions using extension, size, line-count, modification-time, and path-glob facts.

#### Purpose
- Evaluates `AiCondition` trees against an `AiFileContext`.
- Validates condition structure and detects whether line counts are required.

#### Type
- `public final class AiConditionEvaluator` — final class.
- No superclass or implemented interfaces.
- `ERROR_ONE_BRANCH`: shared message for nodes that do not set exactly one branch or leaf.

#### Input
- `AiCondition` trees containing `and`, `or`, `not`, or a leaf condition.
- `AiFileContext` facts: file name, byte size, line count, last-modified epoch milliseconds, and relative path.
- `AiRangeCondition` bounds and ISO-8601 instant strings.
- Extension lists and path-glob strings.

#### Output
- `matches` returns whether a file satisfies a condition tree.
- `validate` throws on invalid condition configuration.
- `usesLines` returns whether any tree node tests line count.
- No fields are mutated; glob evaluation delegates to `AiSourceExcludeFilter`.

#### Core logic
- `matches` recursively evaluates `and` (all children), `or` (any child), and `not` (negated child).
- Leaf matching checks extension suffixes, byte-size and line-count ranges, strict modified-time comparisons, or relative-path glob matching.
- `validate` requires exactly one of nine branch/leaf fields per node; recursively validates combinators and rejects empty groups or extension lists, unbounded size/line ranges, invalid timestamps, and blank path globs.
- `usesLines` recursively scans combinator children and negations for a `lines` condition.
- Modification timestamps are parsed with `Instant.parse`; glob matching uses a singleton-pattern `AiSourceExcludeFilter`.

#### Public API
- `AiConditionEvaluator()` → constructor; creates evaluator.
- `matches(AiCondition, AiFileContext)` → `boolean` — evaluates file condition.
- `validate(AiCondition)` → `void` — checks tree configuration.
- `usesLines(AiCondition)` → `boolean` — detects line-count requirements.

#### Dependencies
- `AiCondition`, `AiConditionGroup`, `AiRangeCondition`, `AiFileContext`
- `AiSourceExcludeFilter`
- `Instant`, `DateTimeParseException`
- `Collections`, `List`, `@Nullable`

#### Exceptions / Errors
- `validate` throws `IllegalArgumentException` for invalid node branches, empty groups or extension lists, unbounded ranges, invalid timestamps, and blank globs.
- Invalid ISO-8601 instants wrap `DateTimeParseException` in `IllegalArgumentException`.
- `matches` throws `IllegalStateException` when no branch or leaf is set; it assumes prior validation.

#### Concurrency
- Stateless after construction; no synchronization or mutable shared state.
