### AiConditionEvaluator.java
- H: 1.0
- C: 529FFBC4
- D: 2026-11-04T12:04:37Z
- T: 2026-11-04T12:04:37Z
- G: 
- A: 0.0.0
- X: file
- P: 0B812A3C
---
> Filters files against nested AI conditions on extensions, size, line count, modification time, and relative-path globs.

#### Purpose
- Evaluates `AiCondition` trees against `AiFileContext` file facts.
- Validates condition configuration and detects whether a tree requires line counts.

#### Type
- `public final class AiConditionEvaluator` — class; no explicit supertypes.

#### Input
- `AiConditionEvaluator()` — no parameters.
- `matches(AiCondition condition, AiFileContext context)` — condition tree and file facts.
- `validate(AiCondition condition)` — condition tree.
- `usesLines(AiCondition condition)` — condition tree.

#### Output
- `matches` returns whether the file satisfies the condition; `validate` and `usesLines` return no state.
- `validate` may throw `IllegalArgumentException` for invalid conditions.
- No fields or resources are mutated or written.

#### Core logic
- `matches` checks node branches in order: `and`, `or`, `not`, `extensions`, `size`, `lines`, `modifiedAfter`, `modifiedBefore`, `pathGlob`.
  - `and` recursively requires all children to match; a null child list yields `true`.
  - `or` recursively accepts any matching child; a null child list yields `false`.
  - `not` negates recursive matching.
  - `extensions` calls `matchesExtension(extensions, context.fileName())`, accepting a non-null suffix match.
  - `size` and `lines` call `AiRangeCondition.contains` with `context.sizeBytes()` and `context.lineCount()`.
  - `modifiedAfter` and `modifiedBefore` compare `context.lastModifiedEpochMilli()` strictly against `parseEpochMilli`.
  - `pathGlob` calls `globMatches(pathGlob, context.relativePath())`.
  - No recognized branch throws `IllegalStateException` with `ERROR_ONE_BRANCH`.
- `validate` requires `branchCount(condition) == 1`, then validates the selected branch:
  - `and`/`or`: `validateGroup` requires at least one child and recursively calls `validate`.
  - `not`: recursively calls `validate`.
  - `extensions`: rejects an empty list.
  - `size`/`lines`: require `AiRangeCondition.hasBound()`.
  - Modification dates: parsed by `parseEpochMilli`.
  - `pathGlob`: rejects blank strings.
- `usesLines` checks the node’s `lines` branch, then recursively traverses `and`, `or`, or `not`; `anyUsesLines` returns false for a null child list and otherwise tests each child.
- `branchCount` counts non-null values from `getAnd`, `getOr`, `getNot`, `getExtensions`, `getSize`, `getLines`, `getModifiedAfter`, `getModifiedBefore`, and `getPathGlob`.
- `globMatches` constructs `new AiSourceExcludeFilter(Collections.singletonList(glob))` and calls `isExcluded(relativePath)`.
- `parseEpochMilli` parses `Instant.parse(isoInstant).toEpochMilli()`; converts `DateTimeParseException` to `IllegalArgumentException`.

#### Public API
- `AiConditionEvaluator()` — creates evaluator.
- `matches(AiCondition, AiFileContext) -> boolean` — tests file condition.
- `validate(AiCondition) -> void` — validates condition tree.
- `usesLines(AiCondition) -> boolean` — detects line-count conditions.

#### Dependencies
- `AiCondition`, `AiConditionGroup`, `AiRangeCondition`, `AiFileContext`
- `AiSourceExcludeFilter`
- `Instant`, `DateTimeParseException`, `Collections`, `List`
- `@Nullable`

#### Exceptions / Errors
- `validate` throws `IllegalArgumentException` for nodes with other than one branch, empty combinators, empty extensions, unbounded size/line ranges, invalid ISO-8601 instants, or blank path globs.
- `matches` throws `IllegalStateException` if no branch is set; documented to assume prior validation.
- `parseEpochMilli` wraps `DateTimeParseException` in `IllegalArgumentException`.

#### Concurrency
- No synchronization or concurrency mechanisms; evaluator declares no mutable instance state.
