### AiConditionEvaluator.java
- H: 1.0
- C: 529FFBC4
- D: 2026-10-31T09:32:09Z
- T: 2026-10-31T09:34:01Z
- G: 
- A: 0.0.0
- X: file
---
> Evaluates, validates, and inspects nested AI file-selection conditions (and/or/not, extensions, size, lines, modification dates, path globs) against file facts for source indexing.

#### Purpose
- Decides whether a file's facts (name, size, line count, mtime, relative path) satisfy a configured `AiCondition` tree.
- Validates condition configuration fail-fast and reports whether line counting is required.

#### Type
- Class, `public final`, no superclass, no interfaces.
- Public no-arg constructor (empty body).
- Annotations: `@Nullable` (JSpecify) on `anyUsesLines` parameter and `matchesExtension` loop element.

#### Input
- `matches(AiCondition condition, AiFileContext context)`: condition tree plus file facts.
- `validate(AiCondition condition)`: condition tree.
- `usesLines(AiCondition condition)`: condition tree.
- Consumed from `AiCondition`: `getAnd`, `getOr`, `getNot`, `getExtensions`, `getSize`, `getLines`, `getModifiedAfter`, `getModifiedBefore`, `getPathGlob`.
- Consumed from `AiConditionGroup`: `getConditions`.
- Consumed from `AiRangeCondition`: `contains(long)`, `hasBound()`.
- Consumed from `AiFileContext`: `fileName()`, `sizeBytes()`, `lineCount()`, `lastModifiedEpochMilli()`, `relativePath()`.

#### Output
- `matches` returns boolean.
- `validate` returns void; throws on invalid tree.
- `usesLines` returns boolean.
- No fields mutated; no I/O; stateless.

#### Core logic
- `matches` evaluates branches in fixed priority: and, or, not, extensions, size, lines, modifiedAfter, modifiedBefore, pathGlob.
- `and`: returns false on first failing child; true if none fail or children null.
- `or`: returns true on first matching child; false otherwise or if children null.
- `not`: negates recursive result.
- `extensions`: true if `fileName` ends with any non-null extension.
- `size`/`lines`: delegate to `AiRangeCondition.contains`.
- `modifiedAfter`/`modifiedBefore`: strict epoch-millis comparison against parsed ISO-8601 instant.
- `pathGlob`: delegates to `AiSourceExcludeFilter` with singleton glob, tested on `relativePath`.
- Falls through to `IllegalStateException` when no branch is set.
- `validate`:
  - Requires exactly one branch via `branchCount`.
  - `and`/`or`: `validateGroup` requires non-empty children, recursively validates each.
  - `not`: recursive validate.
  - `extensions`: must be non-empty.
  - `size`/`lines`: must have a bound (min or max).
  - `modifiedAfter`/`modifiedBefore`: must parse.
  - `pathGlob`: must not be blank after trim.
- `usesLines`: true if node sets `lines`, or any descendant in and/or/not sets it; otherwise false.
- `parseEpochMilli`: `Instant.parse` to epoch millis; wraps `DateTimeParseException` into `IllegalArgumentException`.
- `branchCount`: counts non-null among nine fields.

#### Public API
- `AiConditionEvaluator()` -> constructor; no-op.
- `matches(AiCondition, AiFileContext) -> boolean` ; evaluates condition tree against file facts.
- `validate(AiCondition) -> void` ; throws on misconfigured node.
- `usesLines(AiCondition) -> boolean` ; detects any lines condition in tree.

#### Dependencies
- `java.time.Instant`
- `java.time.format.DateTimeParseException`
- `java.util.Collections`
- `java.util.List`
- `net.ladenthin.srcmorph.support.AiSourceExcludeFilter`
- `org.jspecify.annotations.Nullable`
- Project types: `AiCondition`, `AiConditionGroup`, `AiRangeCondition`, `AiFileContext`

#### Exceptions / Errors
- `IllegalStateException` from `matches` when no branch is set on a node (unvalidated tree).
- `IllegalArgumentException` from `validate`: wrong branch count; empty and/or; empty extensions; unbounded size or lines; unparseable modifiedAfter/modifiedBefore; blank pathGlob.
- `IllegalArgumentException` (wrapping `DateTimeParseException`) from `parseEpochMilli` on invalid ISO-8601.
- Null handling: children lists and extension entries may be null; skipped or treated as no match.
- Null branches are treated as absent, not errors, in `matches`.

#### Concurrency
- Stateless; no fields besides a constant; safe for concurrent use given thread-safe inputs.
