### AiConditionEvaluator.java
- H: 1.0
- C: 529FFBC4
- D: 2026-10-31T14:57:29Z
- T: 2026-10-31T14:57:29Z
- G: 
- A: 0.0.0
- X: file
---
> Evaluates file conditions to select source files by path, extension, size, line count, and modification time.

#### Purpose
- Matches `AiCondition` trees against file facts in `AiFileContext`.
- Validates condition configuration and detects whether line counts are required.

#### Type
- `public final class AiConditionEvaluator` — class; no superclass or interfaces.
- Public no-argument constructor; no annotations.

#### Input
- `matches` receives an `AiCondition` tree and `AiFileContext` file facts.
- `validate` and `usesLines` receive an `AiCondition` tree.
- Reads condition branches, combinator children, file name, relative path, byte size, line count, and last-modified epoch milliseconds.

#### Output
- `matches` returns whether the file satisfies the condition.
- `validate` returns normally for valid trees; otherwise throws `IllegalArgumentException`.
- `usesLines` returns whether a `lines` condition occurs in the traversed tree.
- No fields are mutated; glob evaluation delegates to `AiSourceExcludeFilter`.

#### Core logic
- `matches` evaluates `and` children until one fails; evaluates `or` children until one matches; negates `not`; then evaluates a leaf.
- Leaf matching checks filename suffixes for extensions, range containment for size/lines, strict timestamp comparisons, or a path glob via `AiSourceExcludeFilter`.
- `validate` requires exactly one non-null branch or leaf per node; recursively validates combinators and rejects empty groups, empty extension lists, unbounded ranges, invalid timestamps, and blank path globs.
- `usesLines` checks the current node, then recursively searches `and`, `or`, or `not` children.
- Timestamp parsing uses `Instant.parse` and converts to epoch milliseconds.

#### Public API
- `AiConditionEvaluator()` -> `AiConditionEvaluator` — creates evaluator.
- `matches(AiCondition, AiFileContext)` -> `boolean` — evaluates file conditions.
- `validate(AiCondition)` -> `void` — checks condition configuration.
- `usesLines(AiCondition)` -> `boolean` — detects line-count requirements.

#### Dependencies
- `AiCondition`, `AiConditionGroup`, `AiRangeCondition`, `AiFileContext`
- `AiSourceExcludeFilter`
- `Instant`, `DateTimeParseException`, `Collections`, `List`
- `@Nullable`

#### Exceptions / Errors
- `validate` throws `IllegalArgumentException` for invalid branch count, empty groups/extensions, unbounded ranges, invalid timestamps, or blank globs.
- Invalid ISO-8601 instants are wrapped in `IllegalArgumentException`.
- `matches` throws `IllegalStateException` when no condition branch is set; it assumes prior validation.
- Null combinator child lists are treated as empty during matching and line-use detection.

#### Concurrency
- No mutable instance state or synchronization; evaluator behavior is stateless.
