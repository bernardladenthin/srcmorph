### AiConditionEvaluator.java
- H: 1.0
- C: 529FFBC4
- D: 2026-11-04T07:27:53Z
- T: 2026-11-04T07:27:54Z
- G: 
- A: 0.0.0
- X: file
- P: 36159BF2
---
> File indexing conditions select source files by extension, size, line count, modification time, and relative-path glob, with nested AND/OR/NOT logic.

#### Purpose
- Evaluates `AiCondition` trees against file facts in `AiFileContext`.
- Validates condition configuration and detects whether line counts are required.

#### Type
- `public final class AiConditionEvaluator` — class; no superclass or interfaces.

#### Input
- `AiCondition` tree nodes and their branch values: combinators, extensions, size/line ranges, modification timestamps, and path glob.
- `AiFileContext` facts: file name, size in bytes, line count, last-modified epoch milliseconds, and relative path.

#### Output
- `matches(...)` returns whether a file satisfies a condition.
- `validate(...)` throws on invalid configuration.
- `usesLines(...)` returns whether a tree contains a line-count condition.
- No persistent state or resource writes.

#### Core logic
- Evaluates `and` children with all-match semantics, `or` children with any-match semantics, and `not` by negating its child.
- Evaluates leaves by filename suffix, size/line range containment, strict timestamp comparison, or relative-path glob matching.
- Validates that each node sets exactly one of nine branch/leaf fields; recursively validates combinator children.
- Requires nonempty `and`/`or` groups and extension lists; requires size/line bounds; rejects blank path globs.
- Parses modification timestamps as ISO-8601 instants and converts them to epoch milliseconds.
- Recursively checks `and`, `or`, and `not` trees to find line-count conditions.
- Delegates glob matching to `AiSourceExcludeFilter`.

#### Public API
- `AiConditionEvaluator()` -> constructs evaluator.
- `matches(AiCondition, AiFileContext) -> boolean` — evaluates file condition.
- `validate(AiCondition) -> void` — validates condition tree.
- `usesLines(AiCondition) -> boolean` — detects line-count requirements.

#### Dependencies
- `AiCondition`, `AiConditionGroup`, `AiRangeCondition`, `AiFileContext`
- `AiSourceExcludeFilter`
- `Instant`, `DateTimeParseException`, `Collections`, `List`
- `@Nullable`

#### Exceptions / Errors
- `validate(...)` throws `IllegalArgumentException` for nodes with other than one branch, empty groups/extensions, unbounded ranges, blank globs, or invalid timestamps.
- Invalid timestamp errors wrap `DateTimeParseException`.
- `matches(...)` throws `IllegalStateException` if no branch is set; it assumes prior validation.

#### Concurrency
- Stateless evaluator; no synchronization or mutable instance fields.
