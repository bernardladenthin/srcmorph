### AiConditionEvaluator.java
- H: 1.0
- C: 529FFBC4
- D: 2026-10-29T14:19:17Z
- T: 2026-10-29T14:19:18Z
- G: 
- A: 0.0.0
- X: file
---
> Filters files against nested inclusion conditions using extensions, size, line count, modification time, and relative-path globs.

#### Purpose
- Evaluates `AiCondition` trees against file facts in `AiFileContext`.
- Validates condition configuration and detects whether line counting is required.

#### Type
- `public final` class `AiConditionEvaluator`; no superclass or implemented interfaces.
- No notable annotations.

#### Input
- `matches(AiCondition, AiFileContext)` consumes a condition tree and filename, size, line count, modification time, and relative path.
- `validate(AiCondition)` consumes a condition tree.
- `usesLines(AiCondition)` inspects a condition tree.

#### Output
- `matches` returns whether the file context satisfies the condition.
- `validate` returns no value; throws for invalid configuration.
- `usesLines` returns whether a `lines` condition occurs in the tree.

#### Core logic
- `matches` evaluates `and` children with all-match semantics, `or` children with any-match semantics, and `not` by negation.
- Leaf conditions check filename suffix against listed extensions, size/line ranges via `contains`, strict modification-time comparisons, or a relative-path glob.
- Modification timestamps are parsed as ISO-8601 instants and compared in epoch milliseconds; `modifiedAfter` and `modifiedBefore` use strict `>` and `<`.
- Path globs are delegated to `AiSourceExcludeFilter` as a singleton pattern.
- `validate` requires exactly one of nine branch/leaf fields per node; recursively validates groups and negations.
- `validate` rejects empty combinator groups, empty extension lists, unbounded size/line ranges, invalid timestamps, and blank path globs.
- `usesLines` recursively checks `and`, `or`, and `not` descendants.

#### Public API
- `AiConditionEvaluator()` -> `AiConditionEvaluator` — creates evaluator.
- `matches(AiCondition, AiFileContext)` -> `boolean` — evaluates file condition.
- `validate(AiCondition)` -> `void` — validates condition tree.
- `usesLines(AiCondition)` -> `boolean` — detects line-count conditions.

#### Dependencies
- `AiCondition`, `AiConditionGroup`, `AiRangeCondition`, `AiFileContext`
- `AiSourceExcludeFilter`
- `Instant`, `DateTimeParseException`, `Collections`, `List`
- `@Nullable`

#### Exceptions / Errors
- `validate` throws `IllegalArgumentException` for malformed nodes, invalid ranges/groups, blank globs, or unparsable timestamps.
- Timestamp parse errors wrap `DateTimeParseException` in `IllegalArgumentException`.
- `matches` throws `IllegalStateException` if no recognized condition branch is set; assumes prior validation.

#### Concurrency
- No mutable instance state or synchronization; evaluator methods use local state.
