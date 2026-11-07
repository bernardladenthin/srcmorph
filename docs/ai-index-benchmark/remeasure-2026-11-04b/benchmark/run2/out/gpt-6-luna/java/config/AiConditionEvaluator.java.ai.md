### AiConditionEvaluator.java
- H: 1.0
- C: 529FFBC4
- D: 2026-11-04T11:56:04Z
- T: 2026-11-04T11:56:05Z
- G: 
- A: 0.0.0
- X: file
- P: 0B812A3C
---
> File-condition evaluation lets the indexer select source files by extension, size, line count, modification time, and relative-path glob.

#### Purpose
- Evaluates `AiCondition` trees against file facts in `AiFileContext`.
- Validates condition configuration and detects whether line counts are required.

#### Type
- `public final class AiConditionEvaluator` — class; no supertypes declared.

#### Input
- `AiConditionEvaluator()` takes no arguments.
- `matches(AiCondition condition, AiFileContext context)` consumes a condition tree and file name, size, line count, modification time, and relative path from `context`.
- `validate(AiCondition condition)` and `usesLines(AiCondition condition)` consume a condition tree.
- Reads condition branches and values through getters, including nullable group child lists.

#### Output
- `matches(...)` returns whether the file satisfies the selected condition.
- `validate(...)` returns `void`; throws on invalid configuration.
- `usesLines(...)` returns whether a `lines` condition appears in the tree.
- No persistent state or resources are written; glob evaluation creates an `AiSourceExcludeFilter`.

#### Core logic
- `matches` checks branches in order: `and`, `or`, `not`, `extensions`, `size`, `lines`, `modifiedAfter`, `modifiedBefore`, `pathGlob`.
  - `and`: recursively matches every child; null children list behaves as an empty list and matches.
  - `or`: recursively matches until a child matches; null or empty children list does not match.
  - `not`: negates recursive `matches`.
  - `extensions`: `matchesExtension` checks whether `fileName` ends with any non-null extension.
  - `size` and `lines`: calls `AiRangeCondition.contains` with `context.sizeBytes()` and `context.lineCount()`.
  - `modifiedAfter` / `modifiedBefore`: compares `context.lastModifiedEpochMilli()` strictly greater / less than `parseEpochMilli(...)`.
  - `pathGlob`: calls `globMatches(pathGlob, context.relativePath())`.
  - If no branch is set, throws `IllegalStateException` using `ERROR_ONE_BRANCH`.
- `validate` requires `branchCount(condition) == 1`, then validates the selected branch:
  - `and` / `or`: `validateGroup` requires at least one child and recursively validates each.
  - `not`: recursively validates its child.
  - `extensions`: rejects an empty list.
  - `size` / `lines`: requires `AiRangeCondition.hasBound()`.
  - Modification dates: parses with `parseEpochMilli`.
  - `pathGlob`: rejects a trimmed blank string.
- `usesLines` returns true for a node with `getLines() != null`; otherwise searches `and`, `or`, or `not` recursively via `anyUsesLines`.
- `branchCount` counts non-null values among `and`, `or`, `not`, `extensions`, `size`, `lines`, `modifiedAfter`, `modifiedBefore`, and `pathGlob`.
- `globMatches` builds `new AiSourceExcludeFilter(Collections.singletonList(glob))` and calls `.isExcluded(relativePath)`.
- `parseEpochMilli` uses `Instant.parse(isoInstant).toEpochMilli()`; converts `DateTimeParseException` to `IllegalArgumentException`.
- `ERROR_ONE_BRANCH` contains the invalid-node message prefix.

#### Public API
- `AiConditionEvaluator()` — creates evaluator.
- `matches(AiCondition, AiFileContext) -> boolean` — tests file condition.
- `validate(AiCondition) -> void` — validates condition tree.
- `usesLines(AiCondition) -> boolean` — detects line-count conditions.

#### Dependencies
- `AiCondition`, `AiConditionGroup`, `AiRangeCondition`, `AiFileContext`
- `AiSourceExcludeFilter`
- `Instant`, `DateTimeParseException`, `Collections`, `List`
- `Nullable`

#### Exceptions / Errors
- `validate` throws `IllegalArgumentException` for branch-count errors, empty combinators or extensions, unbounded ranges, invalid dates, and blank path globs.
- `parseEpochMilli` wraps `DateTimeParseException` in `IllegalArgumentException`.
- `matches` throws `IllegalStateException` if no branch is set; it assumes prior validation.
