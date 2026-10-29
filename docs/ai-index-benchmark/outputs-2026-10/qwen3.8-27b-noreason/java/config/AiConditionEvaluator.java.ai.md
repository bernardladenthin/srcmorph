### AiConditionEvaluator.java
- H: 1.0
- C: 529FFBC4
- D: 2026-10-29T09:50:36Z
- T: 2026-10-29T09:56:41Z
- G: 
- A: 0.0.0
- X: file
---
> Evaluates and validates AI file-exclusion condition trees (and/or/not, extensions, size, lines, modified dates, path globs) against a file context to decide whether a source file should be excluded from AI processing.

#### Purpose
- Walks an `AiCondition` tree to determine if an `AiFileContext` matches exclusion criteria.
- Validates condition structure (exactly one branch per node, non-empty groups, bounded ranges, parseable dates, non-blank globs) and reports whether line-count data is required.

#### Type
- `public final class AiConditionEvaluator`
- No extends/implements; no annotations.

#### Input
- `AiCondition` tree (nodes with `and`, `or`, `not`, `extensions`, `size`, `lines`, `modifiedAfter`, `modifiedBefore`, `pathGlob`).
- `AiFileContext` record: `fileName()`, `sizeBytes()`, `lineCount()`, `lastModifiedEpochMilli()`, `relativePath()`.
- `AiConditionGroup` children lists; `AiRangeCondition` bounds.

#### Output
- `boolean` match result from `matches`.
- `void` with `IllegalArgumentException` on invalid config from `validate`.
- `boolean` flag from `usesLines` indicating whether the indexer must read file contents.

#### Core logic
- `matches`: recursive tree walk — `and` requires all children true, `or` requires any child true, `not` inverts; leaf checks: extension suffix, `AiRangeCondition.contains` for size/lines, epoch-milli comparison for modifiedAfter/Before, glob via `AiSourceExcludeFilter`.
- `validate`: enforces exactly one non-null branch/leaf per node (`branchCount`); recursively validates groups (non-empty children), extensions (non-empty list), size/lines (at least one bound), dates (parseable ISO-8601), pathGlob (non-blank).
- `usesLines`: recursive check for any `lines` leaf in the tree; short-circuits on first hit.
- `parseEpochMilli`: `Instant.parse` → `toEpochMilli`; wraps `DateTimeParseException` in `IllegalArgumentException`.
- `globMatches`: delegates to `AiSourceExcludeFilter` with a single-element glob list.

#### Public API
- `matches(AiCondition, AiFileContext) -> boolean` — evaluates condition tree against file.
- `validate(AiCondition) -> void` — throws on misconfigured node.
- `usesLines(AiCondition) -> boolean` — checks if line count needed.

#### Dependencies
- `net.ladenthin.srcmorph.config.AiCondition`
- `net.ladenthin.srcmorph.config.AiConditionGroup`
- `net.ladenthin.srcmorph.config.AiRangeCondition`
- `net.ladenthin.srcmorph.config.AiFileContext`
- `net.ladenthin.srcmorph.support.AiSourceExcludeFilter`
- `java.time.Instant`, `java.time.format.DateTimeParseException`
- `java.util.Collections`, `java.util.List`
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- `IllegalArgumentException` thrown by `validate` for: multiple/zero branches, empty group, empty extensions list, unbounded size/lines, unparseable ISO-8601 date, blank pathGlob.
- `IllegalStateException` thrown by `matches` if no branch is set (defensive; should not occur after `validate`).
- `DateTimeParseException` caught and re-wrapped in `parseEpochMilli`.

#### Concurrency
- Stateless; all methods are pure functions of their arguments. Thread-safe by design.
