### AiConditionEvaluator.java
- H: 1.0
- C: 529FFBC4
- D: 2026-11-01T23:56:56Z
- T: 2026-11-01T23:56:58Z
- G: 
- A: 0.0.0
- X: file
- P: C721BDEF
---
> File selection rules determine which source files qualify by extension, size, line count, modification time, and relative-path glob.

#### Purpose
- Evaluates `AiCondition` trees against file facts in `AiFileContext`.
- Validates condition configuration and detects whether line counts are needed.

#### Type
- `public final class AiConditionEvaluator` — final class.
- No superclass or implemented interfaces.
- No notable annotations.

#### Input
- `matches(AiCondition, AiFileContext)` consumes a condition tree and filename, size, line count, modification timestamp, and relative path from the file context.
- `validate(AiCondition)` and `usesLines(AiCondition)` consume condition trees.
- No injected dependencies or directly read resources.

#### Output
- `matches` returns whether the file satisfies the condition.
- `usesLines` returns whether any traversed condition requires line-count data.
- `validate` returns no value; throws on invalid configuration.
- No persistent state or resource writes.

#### Core logic
- Evaluates `and` children with short-circuit false; evaluates `or` children with short-circuit true; negates `not`.
- Matches leaves by filename suffix, byte-size range, line-count range, strict timestamp comparison, or relative-path glob.
- Uses strict `>` for `modifiedAfter` and strict `<` for `modifiedBefore`; parses timestamps as ISO-8601 `Instant`.
- Requires exactly one of 9 branch/leaf fields per node during validation.
- Requires nonempty `and`/`or` groups and recursively validates their children.
- Rejects empty extension lists, unbounded size/line ranges, invalid timestamps, and blank path globs.
- Recursively searches combinator children and `not` to determine whether line counts are used.

#### Public API
- `AiConditionEvaluator()` -> `void` — creates evaluator.
- `matches(AiCondition, AiFileContext)` -> `boolean` — evaluates file conditions.
- `validate(AiCondition)` -> `void` — validates condition tree.
- `usesLines(AiCondition)` -> `boolean` — detects line-count conditions.

#### Dependencies
- `AiCondition`, `AiConditionGroup`, `AiRangeCondition`, `AiFileContext`
- `AiSourceExcludeFilter`
- `Instant`, `DateTimeParseException`, `Collections`, `List`, `Nullable`

#### Exceptions / Errors
- `validate` throws `IllegalArgumentException` for invalid nodes, empty groups or extension lists, unbounded ranges, invalid timestamps, and blank globs.
- Invalid ISO-8601 instants wrap `DateTimeParseException` in `IllegalArgumentException`.
- `matches` throws `IllegalStateException` if no condition branch matches its dispatch fields.
- Null group-child lists are treated as empty during matching and as invalid during validation; null extensions are ignored.

#### Concurrency
- Stateless evaluator; no synchronization or mutable instance fields.
