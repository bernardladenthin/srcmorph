### AiConditionEvaluator.java
- H: 1.0
- C: 529FFBC4
- D: 2026-10-31T14:30:30Z
- T: 2026-10-31T14:30:31Z
- G: 
- A: 0.0.0
- X: file
---
> Filters files against validated AI conditions such as extension, size, line count, modification time, and path glob.

#### Purpose
- Evaluates `AiCondition` trees against file facts in `AiFileContext`.
- Validates condition configuration and detects whether line counts are required.

#### Type
- `public final class AiConditionEvaluator` — final class.
- No superclass or implemented interfaces.

#### Input
- `AiCondition` trees containing combinators or leaf criteria.
- `AiFileContext` values: filename, size, line count, last-modified epoch milliseconds, and relative path.
- ISO-8601 instant strings and extension lists from condition nodes.

#### Output
- `matches` returns whether the file satisfies a condition.
- `validate` throws on invalid condition configuration.
- `usesLines` indicates whether any condition requires line counts.
- No fields are mutated; glob evaluation delegates to `AiSourceExcludeFilter`.

#### Core logic
- `matches` recursively evaluates `and` (all children), `or` (any child), and `not`; leaf conditions check extensions, byte-size and line-count ranges, strict modification-time comparisons, or path globs.
- Extension matching uses `String.endsWith`; path-glob matching delegates to `AiSourceExcludeFilter.isExcluded`.
- `validate` requires exactly one non-null branch or leaf per node, then checks nonempty combinator groups, nonempty extension lists, bounded size/line ranges, parseable timestamps, and nonblank path globs.
- `usesLines` recursively searches the active `and`, `or`, or `not` subtree for a line-count condition.
- Parses timestamps with `Instant.parse` and converts them to epoch milliseconds.

#### Public API
- `AiConditionEvaluator()` -> `AiConditionEvaluator` — creates evaluator.
- `matches(AiCondition, AiFileContext)` -> `boolean` — evaluates file condition.
- `validate(AiCondition)` -> `void` — validates condition tree.
- `usesLines(AiCondition)` -> `boolean` — detects line-count criteria.

#### Dependencies
- `AiCondition`, `AiConditionGroup`, `AiRangeCondition`, `AiFileContext` — condition model and file facts.
- `AiSourceExcludeFilter` — path-glob evaluation.
- `Instant`, `DateTimeParseException` — timestamp parsing.
- `Collections`, `List` — collection handling.
- `@Nullable` — nullable child list and extensions.

#### Exceptions / Errors
- `validate` throws `IllegalArgumentException` for invalid branch counts, empty groups/extensions, unbounded ranges, blank globs, or invalid ISO-8601 instants.
- `matches` throws `IllegalStateException` when no condition branch is set.
- Invalid timestamp parsing wraps `DateTimeParseException` in `IllegalArgumentException`.

#### Concurrency
- Stateless; no synchronization or mutable instance state.
