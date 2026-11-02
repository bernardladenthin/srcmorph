### AiConditionEvaluator.java
- H: 1.0
- C: 529FFBC4
- D: 2026-10-31T19:47:56Z
- T: 2026-10-31T19:47:57Z
- G: 
- A: 0.0.0
- X: file
---
> Filters files against configurable extension, size, line-count, modification-time, and path-glob conditions, including nested logical combinations.

#### Purpose
- Evaluates `AiCondition` trees against file facts in `AiFileContext`.
- Validates condition configuration and detects whether line counts are required.

#### Type
- `public final class AiConditionEvaluator` — final class; no superclass or implemented interfaces.
- No notable annotations.

#### Input
- `matches(AiCondition, AiFileContext)` consumes a condition tree and filename, size, line count, modification time, and relative path from the file context.
- `validate(AiCondition)` and `usesLines(AiCondition)` consume condition trees.
- No external resources or injected dependencies.

#### Output
- `matches` returns whether the file satisfies the condition.
- `validate` returns no value; throws on invalid configuration.
- `usesLines` returns whether a `lines` condition occurs in the traversed tree.
- No persistent state or resource writes.

#### Core logic
- `matches` evaluates the first non-null branch in order: `and`, `or`, `not`, extensions, size, lines, modified-after, modified-before, path glob.
- `and` requires every child to match; `or` requires any child; `not` negates its child.
- Extension matching checks whether the filename ends with any non-null listed extension.
- Size and line conditions delegate range checks to `AiRangeCondition.contains`.
- Modification comparisons are strict: after means `lastModifiedEpochMilli > parsed instant`; before means `< parsed instant`.
- Path-glob matching delegates to `AiSourceExcludeFilter.isExcluded` with a singleton glob.
- `validate` requires exactly one of the nine branch/leaf fields; recursively validates combinator children.
- Validation rejects empty `and`/`or` groups, empty extension lists, unbounded size/line ranges, invalid ISO-8601 instants, and blank path globs.
- `usesLines` detects a lines condition, recursively traversing `and`, `or`, and `not` nodes.

#### Public API
- `AiConditionEvaluator()` -> `void` — creates evaluator.
- `matches(AiCondition, AiFileContext)` -> `boolean` — evaluates file conditions.
- `validate(AiCondition)` -> `void` — validates condition tree.
- `usesLines(AiCondition)` -> `boolean` — detects line-count requirements.

#### Dependencies
- `AiCondition`, `AiConditionGroup`, `AiRangeCondition`, `AiFileContext` — condition model and file facts.
- `AiSourceExcludeFilter` — path-glob matching.
- `Instant`, `DateTimeParseException` — ISO instant parsing.
- `Collections`, `List`, `@Nullable` — collection and nullability support.

#### Exceptions / Errors
- `validate` throws `IllegalArgumentException` for invalid branch counts, empty groups or extension lists, unbounded ranges, invalid instants, and blank path globs.
- Invalid instants wrap `DateTimeParseException` in `IllegalArgumentException`.
- `matches` throws `IllegalStateException` if no branch is set; its contract assumes prior validation.

#### Concurrency
- Stateless after construction; no synchronization or mutable instance fields.
