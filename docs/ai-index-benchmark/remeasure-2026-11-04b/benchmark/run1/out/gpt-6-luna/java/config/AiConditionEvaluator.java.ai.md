### AiConditionEvaluator.java
- H: 1.0
- C: 529FFBC4
- D: 2026-11-04T11:46:43Z
- T: 2026-11-04T11:46:45Z
- G: 
- A: 0.0.0
- X: file
- P: 0B812A3C
---
> Evaluates file-indexing conditions for extensions, size, line count, modification time, and relative-path globs.

#### Purpose
- `AiConditionEvaluator` validates condition trees, tests them against file facts, and detects whether a tree requires line counts.

#### Type
- `public final class AiConditionEvaluator` — class; no supertypes or annotations.

#### Input
- `AiConditionEvaluator()` — no inputs.
- `matches(AiCondition condition, AiFileContext context)` consumes a condition tree and file name, size, line count, modification time, and relative path from `context`.
- `validate(AiCondition condition)` and `usesLines(AiCondition condition)` consume condition trees.
- Condition fields read: `and`, `or`, `not`, `extensions`, `size`, `lines`, `modifiedAfter`, `modifiedBefore`, `pathGlob`; combinator children are read from `AiConditionGroup.getConditions()`.

#### Output
- `matches(...) -> boolean` reports whether the context satisfies the condition.
- `validate(...) -> void` validates the tree; does not mutate it.
- `usesLines(...) -> boolean` reports whether any reachable condition uses `lines`.

#### Core logic
- `matches` evaluates the first non-null branch in order: `and`, `or`, `not`, `extensions`, `size`, `lines`, `modifiedAfter`, `modifiedBefore`, `pathGlob`.
  - `and` requires every child to match; null children list acts as an empty list and matches.
  - `or` requires any child to match; null children list does not match.
  - `not` negates recursive `matches`.
  - Extension leaves use `matchesExtension`, which accepts any non-null extension for which `fileName.endsWith(extension)`.
  - Size and line leaves call `AiRangeCondition.contains(...)` with `context.sizeBytes()` and `context.lineCount()`.
  - Modification-time leaves compare `context.lastModifiedEpochMilli()` strictly greater than `parseEpochMilli(modifiedAfter)` or strictly less than `parseEpochMilli(modifiedBefore)`.
  - Path-glob leaves call `globMatches`, which constructs `new AiSourceExcludeFilter(Collections.singletonList(glob))` and calls `.isExcluded(relativePath)`.
  - No recognized branch throws `IllegalStateException` with `ERROR_ONE_BRANCH`.
- `validate` requires `branchCount(condition) == 1`, then validates the selected branch:
  - `and` / `or`: `validateGroup` requires a non-null, non-empty child list and recursively validates each child.
  - `not`: recursively calls `validate`.
  - `extensions`: rejects an empty list.
  - `size` / `lines`: requires `hasBound()`.
  - `modifiedAfter` / `modifiedBefore`: parses the ISO-8601 instant with `parseEpochMilli`.
  - `pathGlob`: rejects a trimmed blank string.
- `branchCount` counts non-null branch and leaf fields: `and`, `or`, `not`, `extensions`, `size`, `lines`, `modifiedAfter`, `modifiedBefore`, `pathGlob`.
- `usesLines` returns true for a node with `lines`; otherwise searches `and`, `or`, or `not` recursively. `anyUsesLines` treats a null child list as false.
- `parseEpochMilli` calls `Instant.parse(isoInstant).toEpochMilli()` and wraps `DateTimeParseException` in `IllegalArgumentException`.

#### Public API
- `AiConditionEvaluator()` — constructs evaluator.
- `matches(AiCondition condition, AiFileContext context) -> boolean` — tests file conditions.
- `validate(AiCondition condition) -> void` — validates condition tree.
- `usesLines(AiCondition condition) -> boolean` — detects line-count conditions.

#### Dependencies
- `AiCondition`, `AiConditionGroup`, `AiFileContext`, `AiRangeCondition`
- `AiSourceExcludeFilter`
- `Instant`, `DateTimeParseException`, `Collections`, `List`, `Nullable`

#### Exceptions / Errors
- `validate` throws `IllegalArgumentException` for nodes with other than one branch, empty combinators or extension lists, unbounded size/line ranges, invalid instants, or blank path globs.
- `parseEpochMilli` wraps `DateTimeParseException` in `IllegalArgumentException`.
- `matches` throws `IllegalStateException` if no branch is set; assumes prior validation.
- `matches` does not guard against null `condition` or `context`.
