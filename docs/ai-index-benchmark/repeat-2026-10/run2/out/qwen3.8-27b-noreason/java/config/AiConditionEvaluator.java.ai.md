### AiConditionEvaluator.java
- H: 1.0
- C: 529FFBC4
- D: 2026-10-29T13:48:14Z
- T: 2026-10-29T14:06:23Z
- G: 
- A: 0.0.0
- X: file
---
> Evaluates and validates AI source-exclusion condition trees (and/or/not, extensions, size, lines, modified dates, path globs) against file metadata to decide whether a file should be excluded from AI indexing.

#### Purpose
- Walks an `AiCondition` tree to determine if an `AiFileContext` matches exclusion criteria.
- Validates the tree structure and leaf values, and reports whether line-count data is required.

#### Type
- `public final class AiConditionEvaluator`
- No inheritance; no interfaces implemented.
- Package: `net.ladenthin.srcmorph.config`

#### Input
- `AiCondition` – the condition tree to evaluate/validate.
- `AiFileContext` – file facts: `fileName()`, `sizeBytes()`, `lineCount()`, `lastModifiedEpochMilli()`, `relativePath()`.
- `AiConditionGroup` – `and`/`or` combinator holding `List<AiCondition>` children.
- `AiRangeCondition` – `size`/`lines` range with `contains(long)` and `hasBound()`.
- ISO-8601 instant strings for `modifiedAfter`/`modifiedBefore`.
- Glob pattern string for `pathGlob`.

#### Output
- `boolean` – match result (`matches`), line-usage flag (`usesLines`).
- `void` – `validate` throws on misconfiguration.
- No mutable state; no side effects beyond exception throwing.

#### Core logic
- `matches`: recursive tree walk – `and` requires all children true, `or` requires any child true, `not` negates child; leaves check extension suffix, size/lines range containment, epoch-milli comparison, or glob match via `AiSourceExcludeFilter`.
- `validate`: enforces exactly one branch/leaf per node via `branchCount`; recursively validates groups (non-empty children), non-empty extension lists, bounded ranges, parseable ISO-8601 dates, non-blank glob.
- `usesLines`: recursive check for any `lines` leaf; used by indexer to decide whether to read file contents for line counting.
- `globMatches`: delegates to `AiSourceExcludeFilter` with a single-element list.
- `parseEpochMilli`: `Instant.parse` → `toEpochMilli`; wraps `DateTimeParseException` in `IllegalArgumentException`.

#### Public API
- `matches(AiCondition, AiFileContext) -> boolean` – evaluates tree against file context.
- `validate(AiCondition) -> void` – fails fast on misconfigured nodes.
- `usesLines(AiCondition) -> boolean` – reports if line count is needed.

#### Dependencies
- `net.ladenthin.srcmorph.config.AiCondition`
- `net.ladenthin.srcmorph.config.AiConditionGroup`
- `net.ladenthin.srcmorph.config.AiFileContext`
- `net.ladenthin.srcmorph.config.AiRangeCondition`
- `net.ladenthin.srcmorph.support.AiSourceExcludeFilter`
- `java.time.Instant`
- `java.time.format.DateTimeParseException`
- `java.util.Collections`
- `java.util.List`
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- `IllegalArgumentException` – thrown by `validate` for: wrong branch count, empty group, empty extension list, unbounded range, blank glob, unparseable ISO-8601 date.
- `IllegalStateException` – thrown by `matches` if a node has no branch/leaf set (should not occur after validation).
- `DateTimeParseException` – caught internally, rethrown as `IllegalArgumentException`.
- Null-safe: `children` lists and extension entries are null-checked before iteration.

#### Concurrency
- Stateless; all methods are pure functions of their arguments. Thread-safe by design.
