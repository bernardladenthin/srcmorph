### AiConditionEvaluator.java
- H: 1.0
- C: 529FFBC4
- D: 2026-10-28T21:24:22Z
- T: 2026-10-28T22:39:29Z
- G: 
- A: 0.0.0
- X: file
---
> Evaluates and validates AI source-exclusion condition trees (and/or/not + leaf predicates on extensions, size, lines, modification time, path glob) against a file context to decide whether a file should be excluded from AI indexing.

#### Purpose
- Walks an `AiCondition` tree to determine if an `AiFileContext` matches (include/exclude decision for AI source filtering).
- Validates tree structure and leaf values at configuration time; reports whether any `lines` predicate exists so the indexer can skip reading file contents when unnecessary.

#### Type
- `public final class AiConditionEvaluator`
- No extends/implements; no annotations.

#### Input
- `AiCondition` – the condition tree (and/or/not groups + leaf predicates).
- `AiFileContext` – record providing `fileName()`, `sizeBytes()`, `lineCount()`, `lastModifiedEpochMilli()`, `relativePath()`.
- `AiConditionGroup` – holds `List<AiCondition>` children for and/or combinators.
- `AiRangeCondition` – range with `contains(long)` and `hasBound()`.
- `AiSourceExcludeFilter` – glob-matching utility (reused for `pathGlob`).

#### Output
- `boolean` – match result or `usesLines` flag.
- `void` – `validate` throws on misconfiguration (no return value).
- No mutable state; no side effects beyond exception throwing.

#### Core logic
- **`matches`**: recursive tree walk – checks `and` (all children must match), `or` (any child matches), `not` (negation), then leaf predicates in order: `extensions` (suffix match on `fileName`), `size` (range contains `sizeBytes`), `lines` (range contains `lineCount`), `modifiedAfter`/`modifiedBefore` (epoch-milli comparison), `pathGlob` (delegates to `AiSourceExcludeFilter`). Falls through to `IllegalStateException` if no branch is set.
- **`validate`**: asserts exactly one branch/leaf is non-null via `branchCount`; then per-branch checks – group must have ≥1 child (recursive), extensions non-empty, size/lines must have a bound, dates must parse as ISO-8601, pathGlob must not be blank.
- **`usesLines`**: recursive check for any `lines` leaf in the tree; used by the indexer to decide whether to read file contents for line counting.
- **`parseEpochMilli`**: `Instant.parse` → `toEpochMilli`; wraps `DateTimeParseException` in `IllegalArgumentException`.
- **`globMatches`**: wraps a single glob in `Collections.singletonList` and delegates to `AiSourceExcludeFilter.isExcluded`.

#### Public API
- `matches(AiCondition, AiFileContext) -> boolean` – evaluates tree against file facts.
- `validate(AiCondition) -> void` – fails fast on misconfigured nodes.
- `usesLines(AiCondition) -> boolean` – reports if any lines predicate exists.

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
- `IllegalArgumentException` – thrown by `validate` for: wrong branch count, empty group, empty extensions list, unbounded size/lines range, unparseable ISO-8601 date, blank pathGlob.
- `IllegalStateException` – thrown by `matches` if no branch/leaf is set (should not occur after `validate`).
- `DateTimeParseException` – caught internally, rethrown as `IllegalArgumentException`.

#### Concurrency
- Stateless; all methods are pure functions of their arguments. Thread-safe by construction (no mutable fields).
