### AiConditionEvaluator.java
- H: 1.0
- C: 529FFBC4
- D: 2026-10-30T00:29:58Z
- T: 2026-10-30T00:33:56Z
- G: 
- A: 0.0.0
- X: file
---
> Determines whether files satisfy nested inclusion conditions based on extensions, size, line count, modification time, and path patterns.

#### Purpose
- Evaluates `AiCondition` trees against file facts in `AiFileContext`.
- Validates condition trees and detects whether they require line counts.

#### Type
- `public final` class `AiConditionEvaluator`.
- No superclass or implemented interfaces.

#### Input
- `matches(AiCondition, AiFileContext)` consumes a condition tree and file name, size, line count, modification time, and relative path.
- `validate(AiCondition)` and `usesLines(AiCondition)` consume condition trees.

#### Output
- `matches` returns whether the file satisfies the condition.
- `validate` throws on invalid conditions; `usesLines` returns whether a `lines` condition occurs in the tree.
- No persistent state or external resources are written.

#### Core logic
- `matches` evaluates `and` as all children, `or` as any child, and `not` as negation; leaf checks cover extension suffixes, size and line ranges, strict modification-time bounds, and path globs.
- `validate` requires exactly one branch or leaf per node; recursively checks combinator children and validates nonempty extensions, bounded ranges, parseable timestamps, and nonblank path globs.
- `usesLines` recursively searches `and`, `or`, and `not` nodes for a `lines` condition.
- Path-glob matching delegates to `AiSourceExcludeFilter`; ISO-8601 instants are parsed to epoch milliseconds.

#### Public API
- `AiConditionEvaluator()` -> `void` — creates evaluator.
- `matches(AiCondition, AiFileContext)` -> `boolean` — evaluates file conditions.
- `validate(AiCondition)` -> `void` — validates condition tree.
- `usesLines(AiCondition)` -> `boolean` — detects line-count conditions.

#### Dependencies
- `AiCondition`, `AiConditionGroup`, `AiRangeCondition`, `AiFileContext`.
- `AiSourceExcludeFilter`, `Instant`, `DateTimeParseException`, `Collections`, `List`, `Nullable`.

#### Exceptions / Errors
- `validate` throws `IllegalArgumentException` for invalid branch counts, empty combinators or extensions, unbounded ranges, invalid timestamps, and blank globs.
- Invalid timestamps wrap `DateTimeParseException`.
- `matches` throws `IllegalStateException` if no branch is set.

#### Concurrency
- No mutable instance state; methods use local variables.
