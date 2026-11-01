### AiConditionEvaluator.java
- H: 1.0
- C: 529FFBC4
- D: 2026-11-01T14:41:21Z
- T: 2026-11-01T14:41:23Z
- G: 
- A: 0.0.0
- X: file
---
> Filters files by evaluating nested conditions on extension, size, line count, modification time, and relative path.

#### Purpose
- Evaluates `AiCondition` trees against file facts in `AiFileContext`.
- Validates condition configuration and detects whether line counts are needed.

#### Type
- `public final class AiConditionEvaluator` — final class.
- No superclass or implemented interfaces.
- `ERROR_ONE_BRANCH` is a private static final validation message.

#### Input
- `matches(AiCondition, AiFileContext)` consumes a condition tree and file name, size, line count, modification timestamp, and relative path.
- `validate(AiCondition)` consumes a condition tree, including child groups, ranges, extensions, timestamps, and path glob.
- `usesLines(AiCondition)` inspects the tree’s line-count and combinator branches.

#### Output
- `matches` returns whether the file satisfies the condition.
- `validate` returns no value; rejects invalid condition configuration.
- `usesLines` returns whether a `lines` condition occurs in the traversed tree.
- No fields are mutated; path matching delegates to `AiSourceExcludeFilter`.

#### Core logic
- Evaluates one condition branch: `and` requires all children; `or` requires any child; `not` negates its child.
- Leaf conditions match file-name suffixes, size or line-count ranges, strict modification-time comparisons, or a relative-path glob.
- Extension matching uses `String.endsWith`; modification comparisons use `>` for `modifiedAfter` and `<` for `modifiedBefore`.
- Validates that exactly one of nine branch/leaf fields is set per node; recursively validates combinators and requires each group to contain at least one child.
- Rejects empty extension lists, unbounded size/line ranges, invalid ISO-8601 instants, and blank path globs.
- `usesLines` recursively scans `and`, `or`, and `not` branches to identify line-count requirements.

#### Public API
- `AiConditionEvaluator()` → constructor; creates evaluator.
- `matches(AiCondition, AiFileContext) -> boolean` — tests file against conditions.
- `validate(AiCondition) -> void` — rejects malformed condition trees.
- `usesLines(AiCondition) -> boolean` — detects line-count requirements.

#### Dependencies
- `AiCondition`, `AiConditionGroup`, `AiRangeCondition`, `AiFileContext`
- `AiSourceExcludeFilter`
- `Instant`, `DateTimeParseException`
- `Collections`, `List`, `@Nullable`

#### Exceptions / Errors
- `validate` throws `IllegalArgumentException` for invalid branch counts, empty groups or extensions, missing range bounds, invalid timestamps, and blank path globs.
- Invalid ISO-8601 timestamps are reported as `IllegalArgumentException`, wrapping `DateTimeParseException`.
- `matches` throws `IllegalStateException` if no condition branch is present; documented as assuming prior validation.

#### Concurrency
- No mutable instance state or synchronization; evaluator operations use local state.
