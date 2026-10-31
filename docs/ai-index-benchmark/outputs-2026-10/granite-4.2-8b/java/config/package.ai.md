### java/config
- H: 1.0
- C: A3C47B7D
- D: 2026-10-31T10:06:17Z
- T: 2026-10-31T11:01:17Z
- G: 
- A: 0.0.0
- X: package
- F: [AiConditionEvaluator.java](AiConditionEvaluator.java.ai.md)
- F: [AiConditionGroup.java](AiConditionGroup.java.ai.md)
- F: [AiFactExtractor.java](AiFactExtractor.java.ai.md)
- F: [AiGenerationConfig.java](AiGenerationConfig.java.ai.md)
- F: [AiOversizeStrategy.java](AiOversizeStrategy.java.ai.md)
- F: [SrcMorphConfiguration.java](SrcMorphConfiguration.java.ai.md)
---
> Evaluates {@link AiCondition} trees against {@link AiFileContext} to determine file inclusion, validates condition structure, and checks if line counting is required.

#### Purpose
- Walks the condition tree (and/or/not branches) to test whether a file matches.
- Validates that exactly one branch/leaf is set per node and that required fields (bounds, non-empty glob) are present.
- Determines whether any node uses a {@code lines} condition to decide if file contents need to be read.

#### Responsibilities
- Condition evaluation and validation (`AiConditionEvaluator`)
- Logical grouping of conditions (`AiConditionGroup`)
- Extraction of deterministic structural facts from source text (`AiFactExtractor`)
- Configuration management for AI generation (`AiGenerationConfig`)
- Oversize strategy selection for large files (`AiOversizeStrategy`)
- Top-level srcmorph runtime configuration (`SrcMorphConfiguration`)

#### Key units
- `AiConditionEvaluator`: evaluates condition trees, validates structure, detects lines usage.
- `AiConditionGroup`: container for child {@link AiCondition}s in logical groups.
- `AiFactExtractor`: computes exact whole-file counts via regex patterns; produces "Facts" header.
- `AiGenerationConfig`: holds mutable settings for AI model inference (sampling, context size, provider, etc.).
- `AiOversizeStrategy`: enum defining handling of files larger than model context window.
- `SrcMorphConfiguration`: top-level mutable configuration for srcmorph run (paths, AI definitions, flags).

#### Data flow
- `SrcMorphConfiguration` provides routing rules and AI settings.
- `AiConditionEvaluator` receives a `condition` (tree from `AiConditionGroup`) and `AiFileContext` (file facts) to compute `matches` and `usesLines`.
- `AiFactExtractor` receives `counters` and source text to emit `factsBlock`; `validate` ensures regex safety.
- `AiGenerationConfig` stores generation parameters used by AI provider implementations.
- `AiOversizeStrategy` selects a strategy (`FAIL`, `SAMPLE`, `MAP_REDUCE`, `DETERMINISTIC`) based on config token.

#### Dependencies
- Internal: `AiCondition`, `AiConditionGroup`, `AiFileContext`, `AiRangeCondition`, `AiSourceExcludeFilter`; `AiCalibration` (via `AiGenerationConfig`); `AiPromptDefinition`, `AiModelDefinition`, etc. (via `SrcMorphConfiguration`).
- Java standard: `java.time`, `java.util`, `java.io`, `java.lang`.
- External annotations: `org.jspecify.annotations.Nullable`; Lombok `@ToString`.

#### Cross-cutting
- Defensive copying in `AiConditionGroup.setConditions`.
- Private helper methods (`anyUsesLines`, `branchCount`, `matchesExtension`, `globMatches`, `parseEpochMilli`, `countMatches`) used internally but excluded from public API.
- Null safety via `@Nullable` annotations; getters return nullable types; setters accept nullable parameters.
- Immutable views (`Collections.unmodifiableList`) returned for shared lists (`drySequenceBreakers`, `stopStrings`).
- No shared mutable state across threads; all units are stateless or locally mutable; safe for concurrent use when not shared.
- Configuration defaults stored in static final fields (`DEFAULT_*` constants).
- Error handling: `IllegalArgumentException` for misconfiguration (empty extensions, null label/pattern, invalid ISO-8601, unknown oversize token); `IllegalStateException` for unmatched condition tree; `PatternSyntaxException` caught internally in `AiFactExtractor`; `DateTimeParseException` caught internally in `AiConditionEvaluator`.

> Note: No threading primitives or explicit synchronization; concurrency relies on lack of shared mutable state.

#### Exceptions / Errors (summary)
- `IllegalArgumentException`: thrown by `validate` methods (`AiConditionEvaluator`, `AiFactExtractor`, `AiOversizeStrategy.fromConfig`) on misconfiguration (e.g., null label, invalid regex, unknown oversize token).
- `IllegalStateException`: thrown by `matches` in `AiConditionEvaluator` if no branch is set.
- Internal catches of `PatternSyntaxException` and `DateTimeParseException`, rethrowing as `IllegalArgumentException`.

#### Concurrency (summary)
- All units have no internal locks, volatile fields, or shared mutable state. Methods are pure or use only local variables. Safe for concurrent reads; writers must not share the same instance without external synchronization (typical for single-threaded tool usage).
