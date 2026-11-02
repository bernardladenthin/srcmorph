### AiConditionEvaluator.java
- H: 1.0
- C: 529FFBC4
- D: 2026-11-01T01:13:43Z
- T: 2026-11-01T01:13:44Z
- G: 
- A: 0.0.0
- X: file
---
> Evaluates file-selection conditions against file metadata, validating condition trees and detecting when line counts are needed.

#### Purpose
- Matches files against nested AND, OR, and NOT conditions and metadata predicates.
- Validates condition configuration and identifies trees that require line counts.

#### Type
- `public final class AiConditionEvaluator` — final class.
- No superclass or implemented interfaces.

#### Input
- `AiCondition` trees and `AiFileContext` file facts.
- Condition branches for extensions, size, lines, modification timestamps, and path globs.

#### Output
- `matches(...)` returns whether a file satisfies a condition.
- `usesLines(...)` returns whether a tree contains a line-count condition.
- `validate(...)` throws `IllegalArgumentException` for invalid configurations.

#### Core logic
- Evaluates AND children until one fails; evaluates OR children until one matches; negates NOT recursively.
- Leaf predicates check filename suffixes, byte-size and line-count ranges, strict timestamp comparisons, or relative-path glob filtering.
- Validation requires exactly one branch or leaf per node; recursively validates combinators and requires nonempty AND/OR groups.
- Rejects empty extension lists, unbounded size/lines ranges, invalid ISO-8601 instants, and blank path globs.
- `usesLines` recursively checks line predicates through AND, OR, and NOT nodes.
- Path glob evaluation delegates to `AiSourceExcludeFilter.isExcluded`.

#### Public API
- `AiConditionEvaluator()` — creates evaluator.
- `matches(AiCondition, AiFileContext) -> boolean` — tests file against condition.
- `validate(AiCondition) -> void` — validates condition tree.
- `usesLines(AiCondition) -> boolean` — detects line-count requirement.

#### Dependencies
- `AiCondition`, `AiConditionGroup`, `AiRangeCondition`, `AiFileContext`
- `AiSourceExcludeFilter`
- `Instant`, `DateTimeParseException`, `Collections`, `List`
- `@Nullable`

#### Exceptions / Errors
- `validate` throws `IllegalArgumentException` for invalid nodes, groups, ranges, extensions, dates, or globs.
- Invalid ISO-8601 timestamps are wrapped in `IllegalArgumentException` with the parse exception as cause.
- `matches` throws `IllegalStateException` if no condition branch is set; assumes prior validation.
