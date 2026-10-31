### AiConditionEvaluator.java
- H: 1.0
- C: 529FFBC4
- D: 2026-10-31T15:13:57Z
- T: 2026-10-31T15:13:58Z
- G: 
- A: 0.0.0
- X: file
---
> Filters AI-indexed source files by extension, size, line count, modification date and path glob.
#### Purpose
- Evaluates `AiCondition` trees against `AiFileContext` file facts for AI source selection.
- Validates `AiCondition` configuration and detects `lines` dependency to avoid unnecessary file reads.
#### Type
- Final class `AiConditionEvaluator`, no extends/implements, no generics.
#### Input
- `condition: AiCondition` condition tree with and/or/not/extensions/size/lines/modifiedAfter/modifiedBefore/pathGlob branches.
- `context: AiFileContext` file facts: `fileName()`, `relativePath()`, `sizeBytes()`, `lineCount()`, `lastModifiedEpochMilli()`.
- ISO-8601 instant strings for `modifiedAfter`/`modifiedBefore`, glob string for `pathGlob`, extension list for `extensions`.
- `AiConditionGroup.getConditions(): List<AiCondition>`, `AiRangeCondition` bounds via `contains()`/`hasBound()`.
#### Output
- `boolean` match result from `matches`, `boolean` line-content requirement from `usesLines`.
- No retained state; stateless evaluation with `AiSourceExcludeFilter` glob check per call.
- Validation side effect: throws on misconfiguration, no mutation.
#### Core logic
- `matches` priority walk: `and` requires all children true else true on empty/null; `or` requires any child true else false; `not` negates child.
- `matches` leaves: `extensions` via suffix `endsWith` match; `size`/`lines` via `AiRangeCondition.contains`; `modifiedAfter` via `>` parsed epoch milli; `modifiedBefore` via `<`; `pathGlob` via `AiSourceExcludeFilter.isExcluded`.
- `matches` throws `IllegalStateException` when no branch matches, assuming prior `validate`.
- `validate` enforces exactly one non-null branch per node via `branchCount`; recurses into `and`/`or` children and `not` child.
- `validateGroup` rejects null/empty `<and>`/`<or>` child lists; `extensions` rejects empty list; `size`/`lines` require `hasBound()`; `pathGlob` rejects blank; dates parsed via `Instant.parse`.
- `usesLines`/`anyUsesLines` recursively scans `and`/`or`/`not` subtrees for non-null `getLines()`.
- Helpers: `matchesExtension` null-safe suffix loop; `globMatches` wraps singleton glob in `AiSourceExcludeFilter`; `parseEpochMilli` converts ISO-8601 instant via `Instant.parse().toEpochMilli()`.
#### Public API
- `AiConditionEvaluator() -> AiConditionEvaluator` creates new evaluator.
- `matches(AiCondition, AiFileContext) -> boolean` checks file satisfies condition.
- `validate(AiCondition) -> void` fails fast on misconfiguration.
- `usesLines(AiCondition) -> boolean` reports line-count dependency.
#### Dependencies
- `java.time.Instant`, `java.time.format.DateTimeParseException`, `java.util.Collections`, `java.util.List`.
- `net.ladenthin.srcmorph.support.AiSourceExcludeFilter`, `org.jspecify.annotations.Nullable`.
- `net.ladenthin.srcmorph.config.AiCondition`, `AiConditionGroup`, `AiRangeCondition`, `AiFileContext`.
#### Exceptions / Errors
- `IllegalStateException` in `matches` when node sets zero/multiple branches.
- `IllegalArgumentException` in `validate`: wrong branch count, empty and/or group, empty extensions, unbounded size/lines, blank pathGlob, invalid ISO-8601 instant wrapping `DateTimeParseException`.
- Null-tolerant: null children lists treated as vacuous true for `and`, false for `or`/`anyUsesLines`; null extensions skipped in suffix check.
