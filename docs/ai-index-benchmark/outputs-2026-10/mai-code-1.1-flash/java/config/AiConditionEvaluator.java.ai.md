### AiConditionEvaluator.java
- H: 1.0
- C: 529FFBC4
- D: 2026-10-29T23:43:20Z
- T: 2026-10-29T23:43:21Z
- G: 
- A: 0.0.0
- X: file
---
> Evaluates file-selection conditions against file metadata to decide whether a file matches inclusive/exclusive filters and whether line counting is required.

#### Purpose
- Validates `AiCondition` trees for structural correctness and semantically valid values.
- Evaluates file match decisions and detects if any condition requires line counts.

#### Type
- `final class AiConditionEvaluator`
- `public final class AiConditionEvaluator`
- Extends: none
- Implements: none
- No generics or type bounds
- Notable annotations: none

#### Input
- Constructor: `AiConditionEvaluator()`
- Method params:
  - `matches(AiCondition condition, AiFileContext context)`
  - `validate(AiCondition condition)`
  - `usesLines(AiCondition condition)`
- Consumed fields:
  - `AiCondition` tree branches: `and`, `or`, `not`, `extensions`, `size`, `lines`, `modifiedAfter`, `modifiedBefore`, `pathGlob`
  - `AiFileContext` values: `fileName()`, `sizeBytes()`, `lineCount()`, `lastModifiedEpochMilli()`, `relativePath()`
- Reads:
  - `AiSourceExcludeFilter` glob matching logic
  - `Instant.parse` for ISO-8601 timestamps

#### Output
- `matches(...) -> boolean`: match result for a file against a condition.
- `validate(...) -> void`: throws on invalid configuration.
- `usesLines(...) -> boolean`: reports whether line counting is required anywhere in the tree.
- No persistent state mutation; no file writes or external side effects.

#### Core logic
- `matches` recursively traverses `AiCondition` trees for `and`, `or`, and `not`.
- `matches` resolves leaves in order: extensions, size, lines, modifiedAfter, modifiedBefore, pathGlob.
- `validate` enforces exactly one active branch/leaf per condition node via `branchCount`.
- `validateGroup` requires non-null, non-empty child lists for `and`/`or`; each child is validated recursively.
- `validate` rejects empty extension lists, unbounded `AiRangeCondition`, blank `pathGlob`, and invalid ISO-8601 dates.
- `usesLines` walks the tree and returns true if any descendant uses a `lines` condition so callers can avoid unnecessary file reads.
- `matchesExtension` checks filename suffix matches among configured extensions.
- `globMatches` delegates path glob checks to `AiSourceExcludeFilter`.
- `parseEpochMilli` converts ISO-8601 strings to epoch milliseconds with `Instant.parse`.

#### Public API
- `AiConditionEvaluator() -> void` - constructs evaluator
- `matches(AiCondition, AiFileContext) -> boolean` - checks file against condition
- `validate(AiCondition) -> void` - validates condition tree
- `usesLines(AiCondition) -> boolean` - detects line counting use

#### Dependencies
- Imported:
  - `java.time.Instant`
  - `java.time.format.DateTimeParseException`
  - `java.util.Collections`
  - `java.util.List`
  - `net.ladenthin.srcmorph.support.AiSourceExcludeFilter`
  - `org.jspecify.annotations.Nullable`
- Referenced types:
  - `AiCondition`
  - `AiConditionGroup`
  - `AiFileContext`
  - `AiRangeCondition`
  - `AiSourceExcludeFilter`
  - `IllegalArgumentException`
  - `IllegalStateException`
  - `String`
  - `List`
  - `Instant`
  - `DateTimeParseException`

#### Exceptions / Errors
- `IllegalStateException` in `matches` if no branch/leaf is set at a node.
- `IllegalArgumentException` in `validate` for:
  - not exactly one active condition field
  - empty `<and>` / `<or>` group
  - empty `extensions`
  - unbounded `size` or `lines`
  - blank `pathGlob`
  - invalid ISO-8601 instant in `modifiedAfter` / `modifiedBefore`
- Null-handling:
  - `matches` checks `List` child collections for null before iteration
  - `anyUsesLines` short-circuits on null child lists
- `parseEpochMilli` wraps `DateTimeParseException` in `IllegalArgumentException`

#### Concurrency
- No threading, synchronization, or shared mutable state.
- `AiConditionEvaluator` is stateless aside from compile-time constants.
