### AiConditionEvaluator.java
- H: 1.0
- C: 529FFBC4
- D: 2026-10-29T14:19:17Z
- T: 2026-10-29T14:45:52Z
- G: 
- A: 0.0.0
- X: file
---
> Evaluates and validates AI source file inclusion/exclusion condition trees (and/or/not, extensions, size, lines, modified dates, path globs) against file metadata.

#### Purpose
- Recursively evaluates an `AiCondition` tree to determine if an `AiFileContext` matches inclusion/exclusion rules.
- Validates condition tree structure and leaf values, and detects whether line-count data is required.

#### Type
- `public final class AiConditionEvaluator`
- No inheritance; no interfaces implemented.

#### Input
- `AiCondition` tree (nodes with `and`, `or`, `not`, `extensions`, `size`, `lines`, `modifiedAfter`, `modifiedBefore`, `pathGlob`).
- `AiFileContext` record providing `fileName()`, `sizeBytes()`, `lineCount()`, `lastModifiedEpochMilli()`, `relativePath()`.
- `AiConditionGroup` holding `List<AiCondition>` children.
- `AiRangeCondition` with `contains(long)` and `hasBound()`.

#### Output
- `boolean` match result from `matches`.
- `void` with `IllegalArgumentException` on invalid configuration from `validate`.
- `boolean` flag from `usesLines` indicating whether file contents must be read for line counting.

#### Core logic
- `matches`: walks tree recursively; `and` requires all children true, `or` requires any child true, `not` negates child; leaf checks: extension suffix match, size/lines range containment, ISO-8601 date comparison, glob path match via `AiSourceExcludeFilter`.
- `validate`: enforces exactly one branch/leaf per node via `branchCount`; recursively validates groups (non-empty children), non-empty extensions, bounded ranges, parseable ISO-8601 dates, non-blank glob.
- `usesLines`: recursively checks if any node has a `lines` condition, enabling lazy file-content reading.
- `parseEpochMilli`: converts ISO-8601 string to epoch millis via `Instant.parse`.
- `globMatches`: delegates to `AiSourceExcludeFilter` with a single-glob list.

#### Public API
- `matches(AiCondition, AiFileContext) -> boolean` — evaluates condition tree against file context.
- `validate(AiCondition) -> void` — throws on misconfigured condition nodes.
- `usesLines(AiCondition) -> boolean` — checks if line count is needed.

#### Dependencies
- `net.ladenthin.srcmorph.config.AiCondition`
- `net.ladenthin.srcmorph.config.AiConditionGroup`
- `net.ladenthin.srcmorph.config.AiRangeCondition`
- `net.ladenthin.srcmorph.config.AiFileContext`
- `net.ladenthin.srcmorph.support.AiSourceExcludeFilter`
- `java.time.Instant`
- `java.time.format.DateTimeParseException`
- `java.util.Collections`
- `java.util.List`
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- `IllegalArgumentException` thrown by `validate` for: multiple/zero branches, empty group, empty extensions, unbounded range, blank glob, unparseable ISO-8601 date.
- `IllegalStateException` thrown by `matches` if no branch/leaf is set (should be caught by prior `validate`).
- `DateTimeParseException` caught and re-wrapped as `IllegalArgumentException` in `parseEpochMilli`.

#### Concurrency
- Stateless; all methods are pure functions of their arguments. Thread-safe by design.
