### AiConditionEvaluator.java
- H: 1.0
- C: 529FFBC4
- D: 2026-10-31T10:13:39Z
- T: 2026-10-31T10:13:40Z
- G: 
- A: 0.0.0
- X: file
---
> Filters source files for AI indexing by matching extension, size, line count, modification date, and path rules.
#### Purpose
- Evaluates `AiCondition` trees against `AiFileContext` file facts.
- Validates `AiCondition` configuration and detects `lines` dependency for indexer.
#### Type
- Final class `AiConditionEvaluator`, no extends/implements.
#### Input
- `AiCondition` condition tree with and/or/not, extensions/size/lines/modifiedAfter/modifiedBefore/pathGlob.
- `AiFileContext` context providing fileName, sizeBytes, lineCount, lastModifiedEpochMilli, relativePath.
- ISO-8601 instant strings for modifiedAfter/modifiedBefore.
- Glob string for pathGlob matching.
#### Output
- Boolean match result from `matches`.
- Boolean lines-requirement flag from `usesLines`.
- Validation side-effect only via thrown `IllegalArgumentException`.
#### Core logic
- `matches` walks tree: `and` requires all children true, `or` requires any child true, `not` negates child.
- `matches` leaf checks: extension suffix match, `AiRangeCondition.contains` for size/lines, epoch-milli comparison for modifiedAfter/modifiedBefore, `AiSourceExcludeFilter` glob match for pathGlob.
- `validate` enforces exactly one branch per node via `branchCount`, recurses into and/or/not groups.
- `validate` rejects empty extensions, unbounded size/lines via `hasBound`, blank pathGlob, unparseable dates.
- `usesLines` recursively scans and/or/not children for presence of `lines` condition.
- `parseEpochMilli` converts ISO-8601 via `Instant.parse` to epoch milliseconds.
#### Public API
- `AiConditionEvaluator()` -> `AiConditionEvaluator` — creates new evaluator.
- `matches(AiCondition, AiFileContext)` -> `boolean` — tests file against condition.
- `validate(AiCondition)` -> `void` — checks condition configuration validity.
- `usesLines(AiCondition)` -> `boolean` — reports line-count dependency.
#### Dependencies
- `java.time.Instant`, `java.time.format.DateTimeParseException`, `java.util.Collections`, `java.util.List`.
- `net.ladenthin.srcmorph.support.AiSourceExcludeFilter`, `net.ladenthin.srcmorph.config.AiCondition`, `AiConditionGroup`, `AiFileContext`, `AiRangeCondition`.
- `org.jspecify.annotations.Nullable`.
#### Exceptions / Errors
- `IllegalStateException` on `matches` when node sets not exactly one branch.
- `IllegalArgumentException` on invalid node, empty and/or group, empty extensions, unbounded size/lines, blank pathGlob, invalid ISO-8601 instant.
- `DateTimeParseException` caught and wrapped as `IllegalArgumentException` in `parseEpochMilli`.
- Null-tolerant: null children lists treated as empty/vacuous, null extensions skipped.
#### Concurrency
- Stateless with only `ERROR_ONE_BRANCH` constant, no synchronization needed.
