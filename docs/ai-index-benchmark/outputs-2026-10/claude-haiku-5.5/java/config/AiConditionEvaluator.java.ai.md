### AiConditionEvaluator.java
- H: 1.0
- C: 529FFBC4
- D: 2026-10-30T00:29:58Z
- T: 2026-10-30T00:29:58Z
- G: 
- A: 0.0.0
- X: file
---
> Evaluates and validates nested file-filter conditions (and/or/not, extensions, size, lines, dates, path glob) against file facts.

#### Purpose
- Decides whether a file matches a configured `AiCondition` tree during AI source indexing.
- Validates condition trees for misconfiguration and reports whether line counting is needed.

#### Type
- `public final` class `AiConditionEvaluator`; no superclass, no interfaces.
- Public no-arg constructor (no-op).
- No annotations.

#### Input
- `matches(AiCondition condition, AiFileContext context)`: condition tree and file facts (`fileName()`, `sizeBytes()`, `lineCount()`, `lastModifiedEpochMilli()`, `relativePath()`).
- `validate(AiCondition condition)`: condition tree.
- `usesLines(AiCondition condition)`: condition tree.
- Reads `AiConditionGroup` children, `AiRangeCondition` bounds, ISO-8601 date strings, glob strings, extension lists.

#### Output
- `matches` returns `boolean`.
- `validate` returns `void`; throws on invalid input.
- `usesLines` returns `boolean`.
- No mutated state, no I/O.

#### Core logic
- `matches` checks branches in fixed order: `and` (all children match), `or` (any child matches), `not` (negation), `extensions` (filename suffix), `size` and `lines` (range contains value), `modifiedAfter` and `modifiedBefore` (epoch-millis comparison), `pathGlob` (via `AiSourceExcludeFilter`).
- Missing branch in `matches` throws `IllegalStateException`.
- `validate` requires exactly one non-null branch (`branchCount`), then validates per branch:
  - `and`/`or`: non-empty children, recursive validation (`validateGroup`).
  - `not`: recursive validation.
  - `extensions`: non-empty list.
  - `size`/`lines`: at least one bound (`hasBound()`).
  - `modifiedAfter`/`modifiedBefore`: parseable ISO-8601.
  - `pathGlob`: not blank.
- `usesLines` returns true if a `lines` node exists anywhere in the tree (recursive through `and`, `or`, `not`).
- `matchesExtension` skips null entries and uses `String.endsWith`.
- `parseEpochMilli` wraps `Instant.parse` and rethrows `DateTimeParseException` as `IllegalArgumentException`.

#### Public API
- `AiConditionEvaluator()` -> constructor; no-op.
- `matches(AiCondition, AiFileContext) -> boolean` — tests file against condition tree.
- `validate(AiCondition) -> void` — throws on invalid tree.
- `usesLines(AiCondition) -> boolean` — detects any `lines` node.

#### Dependencies
- `java.time.Instant`, `java.time.format.DateTimeParseException`
- `java.util.Collections`, `java.util.List`
- `net.ladenthin.srcmorph.support.AiSourceExcludeFilter`
- `org.jspecify.annotations.Nullable`
- `AiCondition`, `AiConditionGroup`, `AiFileContext`, `AiRangeCondition` (referenced, not shown)

#### Exceptions / Errors
- `IllegalArgumentException`: invalid branch count, empty combinator, empty extensions, unbounded size or lines range, unparseable instant, blank glob.
- `IllegalStateException`: `matches` reached a node with no branch set.
- `DateTimeParseException`: caught and wrapped.
- `@Nullable` on list elements and child lists; null children list treated as empty or non-matching.

#### Concurrency
- Stateless; no fields. Thread-safe as long as referenced types are not mutated concurrently.
