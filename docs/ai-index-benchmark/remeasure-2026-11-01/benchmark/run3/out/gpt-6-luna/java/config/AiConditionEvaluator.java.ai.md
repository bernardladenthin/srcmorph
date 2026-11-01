### AiConditionEvaluator.java
- H: 1.0
- C: 529FFBC4
- D: 2026-11-01T14:58:32Z
- T: 2026-11-01T14:58:34Z
- G: 
- A: 0.0.0
- X: file
---
> Evaluates file-selection conditions so source files can be included based on metadata, paths, and nested logical rules.

#### Purpose
- Matches an `AiFileContext` against an `AiCondition` tree.
- Validates condition configuration and detects whether line counts are needed.

#### Type
- `public final class AiConditionEvaluator` — class; no superclass or implemented interfaces.

#### Input
- `matches(AiCondition, AiFileContext)` consumes a condition tree and file facts: filename, relative path, size, line count, and modification time.
- `validate(AiCondition)` and `usesLines(AiCondition)` consume condition trees.
- No injected dependencies or external resources.

#### Output
- `matches` returns whether the file satisfies the condition.
- `usesLines` returns whether any visited condition requires a line count.
- No persistent state or external writes.

#### Core logic
- Evaluates `and` children all-match, `or` children any-match, and `not` by negating its child.
- Evaluates leaves using extension suffixes, size and line ranges, strict modification-time bounds, or a path glob.
- Validates exactly one branch or leaf per node; requires nonempty combinator groups, nonempty extension lists, bounded size/line ranges, parseable dates, and nonblank path globs.
- Recursively checks for `lines` conditions through combinator groups and negation.
- Delegates glob matching to `AiSourceExcludeFilter`; parses ISO-8601 instants into epoch milliseconds.

#### Public API
- `AiConditionEvaluator()` -> `void` — creates evaluator.
- `matches(AiCondition, AiFileContext)` -> `boolean` — evaluates condition tree.
- `validate(AiCondition)` -> `void` — rejects invalid configuration.
- `usesLines(AiCondition)` -> `boolean` — detects line-count requirements.

#### Dependencies
- `AiCondition`, `AiConditionGroup`, `AiRangeCondition`, `AiFileContext`
- `AiSourceExcludeFilter`
- `Instant`, `DateTimeParseException`, `Collections`, `List`
- JSpecify `Nullable`

#### Exceptions / Errors
- `validate` throws `IllegalArgumentException` for invalid nodes, groups, bounds, blank globs, or invalid ISO-8601 dates; date errors retain the `DateTimeParseException` cause.
- `matches` throws `IllegalStateException` when no condition branch is set; assumes prior validation.
- Null extension entries are ignored. Null group child lists are tolerated by matching and `usesLines`, but rejected during validation.

#### Concurrency
- Stateless; no synchronization or mutable instance state.
