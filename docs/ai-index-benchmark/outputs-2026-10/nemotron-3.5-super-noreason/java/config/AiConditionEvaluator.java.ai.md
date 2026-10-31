### AiConditionEvaluator.java
- H: 1.0
- C: 529FFBC4
- D: 2026-10-31T10:00:28Z
- T: 2026-10-31T10:00:29Z
- G: 
- A: 0.0.0
- X: file
---
> Evaluates and validates AiCondition trees against an AiFileContext to determine whether a source file matches a configured condition.

#### Purpose
- Provides boolean matching of AiCondition trees (and/or/not + leaves) against file facts.
- Validates condition tree configuration and detects whether line counting is required.

#### Type
- `public final class AiConditionEvaluator`
- No extends or implements.
- No generics.

#### Input
- `AiCondition condition` — condition tree to evaluate or validate.
- `AiFileContext context` — file facts including fileName, sizeBytes, lineCount, lastModifiedEpochMilli, relativePath.
- `AiConditionGroup group` — combinator group for validation.
- `List<AiCondition> children` — child conditions for recursive traversal.
- `String glob`, `String relativePath` — glob pattern and path for matching.
- `String isoInstant` — ISO-8601 instant string for parsing.

#### Output
- `boolean` — match result from `matches`.
- `void` — `validate` throws on misconfiguration; no return.
- `boolean` — `usesLines` indicates if any node needs line count.
- Mutates nothing; no written resources.

#### Core logic
- `AiConditionEvaluator.matches` recursively walks the condition tree: AND requires all children match, OR requires any child match, NOT inverts the child result.
- Leaf checks: extensions via `fileName.endsWith`, size via `AiRangeCondition.contains`, lines via `AiRangeCondition.contains`, modifiedAfter/before via epoch millisecond comparison, pathGlob via `AiSourceExcludeFilter`.
- `AiConditionEvaluator.validate` fails fast: exactly one branch per node, non-empty and/or groups, bounded size/lines ranges, parseable ISO-8601 dates, non-blank pathGlob, non-empty extensions list.
- `AiConditionEvaluator.usesLines` traverses the tree to detect any `lines` condition so the indexer can avoid reading file contents when unnecessary.
- `AiConditionEvaluator.parseEpochMilli` parses ISO-8601 instants and wraps `DateTimeParseException` as `IllegalArgumentException`.

#### Public API
- `matches(AiCondition condition, AiFileContext context) -> boolean` — returns whether the file matches the condition.
- `validate(AiCondition condition) -> void` — validates the condition tree, throwing on misconfiguration.
- `usesLines(AiCondition condition) -> boolean` — returns true if any node is a lines condition.

#### Dependencies
- `java.time.Instant`
- `java.time.format.DateTimeParseException`
- `java.util.Collections`
- `java.util.List`
- `net.ladenthin.srcmorph.config.AiCondition`
- `net.ladenthin.srcmorph.config.AiConditionGroup`
- `net.ladenthin.srcmorph.config.AiFileContext`
- `net.ladenthin.srcmorph.config.AiRangeCondition`
- `net.ladenthin.srcmorph.support.AiSourceExcludeFilter`
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- `IllegalArgumentException` from `validate` for misconfigured nodes (wrong branch count, empty combinator, unbounded range, unparseable date, blank glob, empty extensions).
- `IllegalStateException` from `matches` if no branch is set (should not occur after validation).
- `IllegalArgumentException` from `parseEpochMilli` on invalid ISO-8601 instant, wrapping `DateTimeParseException`.
- Null children lists are tolerated in `matches` and `anyUsesLines`.

#### Concurrency
- Stateless instance; no mutable shared state; thread-safe for concurrent use.
