### AiConditionEvaluator.java
- H: 1.0
- C: 529FFBC4
- D: 2026-11-04T07:43:16Z
- T: 2026-11-04T07:43:17Z
- G: 
- A: 0.0.0
- X: file
- P: 36159BF2
---
> File indexing conditions select source files by extension, size, line count, modification time, and relative-path glob.

#### Purpose
- Evaluates `AiCondition` trees against `AiFileContext` file metadata.
- Validates condition configuration and detects whether line counts are required.

#### Type
- `public final class AiConditionEvaluator` — class; no superclass or implemented interfaces.

#### Input
- `AiCondition` tree with `and`, `or`, `not`, extensions, size, lines, modification-time, or path-glob fields.
- `AiFileContext` facts: file name, size in bytes, line count, last-modified epoch milliseconds, and relative path.
- ISO-8601 instant strings for modification-time conditions.

#### Output
- `matches(...)` returns whether the file satisfies the condition.
- `validate(...)` returns no value; throws for invalid conditions.
- `usesLines(...)` returns whether a `lines` condition occurs in the tree.

#### Core logic
- Evaluates `and` children all-match, `or` children any-match, and `not` by negating its child.
- Matches extension conditions when the file name ends with any non-null listed extension.
- Checks size and line-count ranges through `AiRangeCondition.contains(...)`.
- Compares modification time strictly after or before the parsed instant.
- Delegates path-glob matching to `AiSourceExcludeFilter` using the relative path.
- Validates that each node sets exactly one of nine branches; recursively validates combinators.
- Requires non-empty combinator groups and extension lists, bounded size/line ranges, parseable dates, and nonblank path globs.
- Recursively detects `lines` conditions so callers can avoid reading file contents when unnecessary.

#### Public API
- `AiConditionEvaluator()` -> constructs evaluator.
- `matches(AiCondition, AiFileContext) -> boolean` — evaluate file condition.
- `validate(AiCondition) -> void` — validate condition tree.
- `usesLines(AiCondition) -> boolean` — detect line-count requirement.

#### Dependencies
- `AiCondition`, `AiConditionGroup`, `AiRangeCondition`, `AiFileContext`
- `AiSourceExcludeFilter`
- `Instant`, `DateTimeParseException`
- `Collections`, `List`
- `Nullable`

#### Exceptions / Errors
- `validate(...)` throws `IllegalArgumentException` for invalid branch counts, empty groups or extensions, unbounded ranges, invalid ISO-8601 instants, or blank path globs.
- Invalid instants wrap `DateTimeParseException` in `IllegalArgumentException`.
- `matches(...)` throws `IllegalStateException` when no branch is set; assumes prior validation.
- Null child lists are tolerated during matching and line-condition detection; validation rejects null or empty group children.

#### Concurrency
- No mutable instance state; methods use local variables.
