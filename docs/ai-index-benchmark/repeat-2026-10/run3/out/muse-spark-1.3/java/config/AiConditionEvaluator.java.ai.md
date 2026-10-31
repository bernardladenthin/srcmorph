### AiConditionEvaluator.java
- H: 1.0
- C: 529FFBC4
- D: 2026-10-31T15:20:26Z
- T: 2026-10-31T15:20:27Z
- G: 
- A: 0.0.0
- X: file
---
> Filters AI-indexed source files by extension, size, line count, modification date and path patterns.
#### Purpose
- Evaluates AiCondition trees against AiFileContext file facts for AI source selection.
- Validates AiCondition configuration and detects lines-condition usage for indexer optimization.
#### Type
- final class AiConditionEvaluator, no extends/implements, default constructor.
#### Input
- AiCondition condition tree with and/or/not/extensions/size/lines/modifiedAfter/modifiedBefore/pathGlob branches.
- AiFileContext context providing fileName, sizeBytes, lineCount, lastModifiedEpochMilli, relativePath.
- ISO-8601 instant strings for modifiedAfter/modifiedBefore, glob string for pathGlob.
#### Output
- boolean match result from matches, boolean lines-needed flag from usesLines.
- Validation side-effect only via IllegalArgumentException on misconfiguration.
- AiSourceExcludeFilter evaluation for pathGlob matching.
#### Core logic
- matches walks AiCondition tree: and requires all children true, or requires any child true, not negates child.
- matchesExtension checks fileName endsWith any listed extension.
- size/lines delegate to AiRangeCondition.contains against sizeBytes/lineCount.
- modifiedAfter/modifiedBefore compare lastModifiedEpochMilli against Instant.parse epoch millis with > / <.
- pathGlob delegates to AiSourceExcludeFilter.isExcluded on relativePath.
- validate enforces exactly one branch per node via branchCount, non-empty and/or children, non-empty extensions, bounded size/lines via hasBound, parseable dates, non-blank pathGlob.
- usesLines/anyUsesLines recursively scans and/or/not children for lines presence.
- validateGroup rejects null/empty conditions list then recursively validates children.
#### Public API
- AiConditionEvaluator() -> AiConditionEvaluator creates new evaluator.
- matches(condition, context) -> boolean checks file satisfies condition.
- validate(condition) -> void fails fast on misconfiguration.
- usesLines(condition) -> boolean reports lines counting needed.
#### Dependencies
- java.time.Instant, java.time.format.DateTimeParseException, java.util.Collections, java.util.List.
- net.ladenthin.srcmorph.config.AiCondition, AiConditionGroup, AiRangeCondition, AiFileContext.
- net.ladenthin.srcmorph.support.AiSourceExcludeFilter.
- org.jspecify.annotations.Nullable.
#### Exceptions / Errors
- IllegalStateException when matches hits node without exactly one branch.
- IllegalArgumentException for branch count !=1, empty and/or, empty extensions, unbounded size/lines, blank pathGlob, invalid ISO-8601 instant.
- DateTimeParseException caught in parseEpochMilli and wrapped with expected format message.
