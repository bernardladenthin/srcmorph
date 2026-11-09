### AiConditionEvaluator.java
- H: 1.0
- C: 529FFBC4
- D: 2026-11-04T07:50:25Z
- T: 2026-11-04T07:50:26Z
- G: 
- A: 0.0.0
- X: file
- P: 1879E12B
---
> File-condition rules select source files by extension, size, line count, modification time, and path pattern, including nested Boolean combinations.

#### Purpose
- Evaluates `AiCondition` trees against `AiFileContext` file facts.
- Validates condition configuration and detects whether a tree requires line counts.

#### Type
- `public final class AiConditionEvaluator` — class; no supertypes; no notable annotations.

#### Input
- `AiConditionEvaluator()` — no inputs.
- `matches(AiCondition condition, AiFileContext context)` — condition tree and file facts.
- `validate(AiCondition condition)` — condition tree.
- `usesLines(AiCondition condition)` — condition tree.
- Reads condition branches, groups, ranges, date strings and glob; reads context file name, size, line count, modification time and relative path.

#### Output
- `matches(...)` returns whether the file satisfies the selected condition.
- `validate(...)` returns no value; throws on invalid configuration.
- `usesLines(...)` returns whether any traversed node has a `lines` condition.
- No persistent state or resource writes.

#### Core logic
- `matches` evaluates the first set branch: `and` requires all children; `or` requires any child; `not` negates its child.
- Leaf matching checks extension suffixes, byte-size or line-count ranges, strict after/before epoch-millisecond comparisons, or path glob matching through `AiSourceExcludeFilter`.
- `validate` requires exactly one of 9 branch/leaf fields per node; recursively validates combinator children.
- Rejects empty `and`/`or` groups and empty extension lists; requires bounds for `size` and `lines`; parses date conditions as ISO-8601 instants; rejects blank path globs.
- `usesLines` searches the node and recursively traverses `and`, `or`, and `not` branches.
- Extension matching ignores null entries; glob matching delegates to `AiSourceExcludeFilter.isExcluded`.

#### Public API
- `AiConditionEvaluator()` -> `AiConditionEvaluator` — creates evaluator.
- `matches(AiCondition, AiFileContext)` -> `boolean` — test file against condition.
- `validate(AiCondition)` -> `void` — validate condition tree.
- `usesLines(AiCondition)` -> `boolean` — detect line-count requirements.

#### Dependencies
- `AiCondition`, `AiConditionGroup`, `AiRangeCondition`, `AiFileContext`
- `AiSourceExcludeFilter`
- `Instant`, `DateTimeParseException`, `Collections`, `List`
- `@Nullable`

#### Exceptions / Errors
- `validate` throws `IllegalArgumentException` for invalid branch counts, empty groups or extensions, unbounded ranges, invalid dates, or blank globs.
- Invalid ISO-8601 dates are wrapped in `IllegalArgumentException` with the parse exception as cause.
- `matches` throws `IllegalStateException` if no branch is set; it assumes prior validation.

#### Concurrency
- No synchronization or shared mutable state.
