### AiConditionEvaluator.java
- H: 1.0
- C: 529FFBC4
- D: 2026-10-30T16:34:08Z
- T: 2026-10-30T16:34:11Z
- G: 
- A: 0.0.0
- X: file
---
> Evaluates AI source-filter condition trees (and/or/not combinators plus extension, size, lines, modification-date, and path-glob leaves) against file facts to decide inclusion, validates tree configuration, and signals when file line counts must be read.

#### Purpose
- Evaluates `AiCondition` trees against `AiFileContext` to determine whether a file matches source-morph inclusion rules
- Validates condition-tree structure to fail fast on misconfiguration before evaluation
- Reports whether any node requires line counts so the indexer can avoid reading file contents unnecessarily

#### Type
- Final class `AiConditionEvaluator` in package `net.ladenthin.srcmorph.config`
- No superclass or interfaces; stateless with public no-arg constructor
- Uses `@Nullable` (jspecify) on private helper parameter

#### Input
- `AiCondition` tree nodes: combinators (`and`, `or`, `not`) and leaves (`extensions`, `size`, `lines`, `modifiedAfter`, `modifiedBefore`, `pathGlob`)
- `AiFileContext` facts: `fileName()`, `sizeBytes()`, `lineCount()`, `lastModifiedEpochMilli()`, `relativePath()`
- ISO-8601 instant strings for `modifiedAfter`/`modifiedBefore`
- Glob patterns for `pathGlob` delegated to `AiSourceExcludeFilter`

#### Output
- `boolean` from `matches` indicating whether the file satisfies the tree
- `void` from `validate` (throws on invalid configuration)
- `boolean` from `usesLines` indicating presence of any `lines` condition
- No mutable state or side effects; pure evaluation with exceptions on invalid input

#### Core logic
- `matches` recursively walks the tree: `and` requires all children true, `or` requires any child true, `not` negates its child; leaves compare `AiFileContext` facts against thresholds or patterns
- Leaf evaluation order: `extensions` (suffix match via `String.endsWith`), `size`/`lines` (`AiRangeCondition.contains`), `modifiedAfter`/`modifiedBefore` (epoch-milli comparison via `Instant.parse`), `pathGlob` (delegated to `AiSourceExcludeFilter.isExcluded`)
- `validate` enforces exactly one branch per node via `branchCount`, non-empty `and`/`or` groups, non-empty `extensions`, bounded `size`/`lines` ranges, parseable ISO-8601 dates, and non-blank `pathGlob`
- `usesLines` traverses the tree returning `true` if any `lines` node is found, enabling lazy file-content reading
- `parseEpochMilli` converts ISO-8601 strings to epoch milliseconds, wrapping `DateTimeParseException` in `IllegalArgumentException`

#### Public API
- `AiConditionEvaluator() -> void` — constructs stateless evaluator
- `matches(AiCondition condition, AiFileContext context) -> boolean` — evaluates tree against file facts
- `validate(AiCondition condition) -> void` — validates tree, throws IllegalArgumentException
- `usesLines(AiCondition condition) -> boolean` — detects lines conditions in tree

#### Dependencies
- `java.time.Instant`, `java.time.format.DateTimeParseException`
- `java.util.Collections`, `java.util.List`
- `net.ladenthin.srcmorph.support.AiSourceExcludeFilter`
- `org.jspecify.annotations.Nullable`
- `AiCondition`, `AiConditionGroup`, `AiFileContext`, `AiRangeCondition` (same package)

#### Exceptions / Errors
- `IllegalStateException` from `matches` if a node sets zero branches (defensive; prevented by prior `validate`)
- `IllegalArgumentException` from `validate` for: multiple or zero branches set, empty `and`/`or` group, empty `extensions` list, unbounded `size`/`lines` range, blank `pathGlob`
- `IllegalArgumentException` from `parseEpochMilli` wrapping `DateTimeParseException` for invalid ISO-8601 instants
- Null-tolerant: null child lists in groups treated as empty; null entries in `extensions` skipped during matching

#### Concurrency
- Stateless (only immutable `static final String` constant); thread-safe for concurrent `matches`, `validate`, and `usesLines` calls
