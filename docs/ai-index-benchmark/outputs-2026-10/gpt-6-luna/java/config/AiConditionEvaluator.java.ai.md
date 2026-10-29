### AiConditionEvaluator.java
- H: 1.0
- C: 529FFBC4
- D: 2026-10-28T21:19:32Z
- T: 2026-10-28T21:19:33Z
- G: 
- A: 0.0.0
- X: file
---
> Evaluates file conditions against metadata and paths, including boolean groups, size and line ranges, timestamps, extensions, and path globs.

#### Purpose
- Matches an `AiCondition` tree against an `AiFileContext`.
- Validates condition structure and reports whether a tree requires line counts.

#### Type
- `public final class AiConditionEvaluator`.
- No superclass or implemented interfaces; no type parameters.

#### Input
- `matches(condition, context)` consumes a condition tree and file facts: name, relative path, size, line count, and modification time.
- `validate(condition)` and `usesLines(condition)` consume condition trees.

#### Output
- `matches` returns whether the file satisfies the condition.
- `validate` throws on invalid configuration; otherwise returns normally.
- `usesLines` returns whether a `lines` condition occurs in the tree.
- No persistent state or resource writes.

#### Core logic
- Evaluates `and` children with all-match semantics; evaluates `or` children with any-match semantics; negates `not`.
- Matches extensions by `fileName.endsWith(extension)`.
- Tests size and line values with `AiRangeCondition.contains`.
- Compares modification time strictly greater than `modifiedAfter` or strictly less than `modifiedBefore`, parsing ISO-8601 instants to epoch milliseconds.
- Delegates path-glob matching to `AiSourceExcludeFilter.isExcluded`.
- Validates exactly one non-null branch/leaf per node; requires nonempty combinator groups and extensions, bounded size/line ranges, parseable timestamps, and nonblank path globs.
- Recursively detects line conditions through `and`, `or`, and `not`.

#### Public API
- `AiConditionEvaluator()` -> `void` — creates evaluator
- `matches(AiCondition, AiFileContext)` -> `boolean` — evaluates file condition
- `validate(AiCondition)` -> `void` — validates condition tree
- `usesLines(AiCondition)` -> `boolean` — detects line-count requirement

#### Dependencies
- `AiCondition`, `AiConditionGroup`, `AiFileContext`, `AiRangeCondition`
- `AiSourceExcludeFilter`
- `Instant`, `DateTimeParseException`
- `Collections`, `List`
- `Nullable`

#### Exceptions / Errors
- `validate` throws `IllegalArgumentException` for invalid branch counts, empty groups or extensions, unbounded ranges, invalid timestamps, or blank globs.
- Invalid timestamps wrap `DateTimeParseException` in `IllegalArgumentException`.
- `matches` throws `IllegalStateException` if no branch is set; it assumes prior validation.

#### Concurrency
- Stateless evaluator; no synchronization or mutable instance state.
