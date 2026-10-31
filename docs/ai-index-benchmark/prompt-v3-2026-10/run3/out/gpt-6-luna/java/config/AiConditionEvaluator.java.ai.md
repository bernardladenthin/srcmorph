### AiConditionEvaluator.java
- H: 1.0
- C: 529FFBC4
- D: 2026-10-31T20:34:56Z
- T: 2026-10-31T20:34:57Z
- G: 
- A: 0.0.0
- X: file
---
> Evaluates file-filter conditions to select files by extension, size, line count, modification time, and relative-path glob.

#### Purpose
- Matches `AiCondition` trees against file facts in `AiFileContext`.
- Validates condition configuration and detects whether line counts are required.

#### Type
- `public final class AiConditionEvaluator` — final class.
- No superclass or implemented interfaces.

#### Input
- `matches(AiCondition, AiFileContext)` consumes a condition tree and file name, size, line count, modification timestamp, and relative path.
- `validate(AiCondition)` and `usesLines(AiCondition)` consume condition trees.

#### Output
- `matches` returns whether the context satisfies the condition.
- `usesLines` returns whether the tree contains a line-count condition.
- No stored state or resource writes.

#### Core logic
- Evaluates `and` as all children matching, `or` as any child matching, and `not` as the inverse of its child.
- Evaluates leaves using extension suffixes, byte-size and line-count ranges, strict timestamp comparisons, or path-glob filtering through `AiSourceExcludeFilter`.
- Validates that each node sets exactly one branch or leaf; requires nonempty `and`/`or` children and nonempty extensions, bounded size/line ranges, valid ISO-8601 timestamps, and nonblank path globs.
- Recursively scans combinator and `not` nodes to detect `lines` conditions.

#### Public API
- `AiConditionEvaluator()` → constructor; creates evaluator.
- `matches(AiCondition, AiFileContext)` → `boolean` — evaluates file condition.
- `validate(AiCondition)` → `void` — validates condition tree.
- `usesLines(AiCondition)` → `boolean` — detects line-count dependency.

#### Dependencies
- `AiCondition`, `AiConditionGroup`, `AiRangeCondition`, `AiFileContext`.
- `AiSourceExcludeFilter`.
- `Instant`, `DateTimeParseException`, `Collections`, `List`, `@Nullable`.

#### Exceptions / Errors
- `validate` throws `IllegalArgumentException` for malformed nodes, empty groups/extensions, unbounded ranges, blank globs, or invalid timestamps.
- Invalid timestamps wrap `DateTimeParseException`.
- `matches` throws `IllegalStateException` if no recognized branch is set; callers are expected to validate first.
- Null group child lists are treated as empty during matching and line-use detection; validation rejects them for groups.
