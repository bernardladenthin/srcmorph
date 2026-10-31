### java/config
- H: 1.0
- C: B56E33BA
- D: 2026-10-31T09:32:09Z
- T: 2026-10-31T09:33:14Z
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
> Configured source-file inclusion/exclusion and AI-generation parameter plumbing for the srcmorph Maven plugin's code-indexer pipeline.

#### Purpose
- Defines Maven-plugin configuration beans for AI generation (model, sampling, caching, batching, provider endpoints) and per-rule over-size handling.
- Provides condition-tree evaluation and fact extraction logic that decides which source files are read and what metadata gets prepended to AI summaries.

#### Responsibilities
- **Configuration beans for AI generation**: `AiGenerationConfig` (40+ parameters with defaults) and `SrcMorphConfiguration` (root run config binding Maven mojos, CLI/YAML, or Java).
- **Oversize strategy enum**: `AiOversizeStrategy` (FAIL, SAMPLE, MAP_REDUCE, DETERMINISTIC) selected via `<onOversize>` config token; default resolves to FAIL.
- **Include/exclude condition evaluation**: `AiConditionEvaluator` walks `AiCondition` trees (and/or/not + leaf predicates on extension, size, lines, dates, glob path) and validates well-formedness.
- **Condition tree data holder**: `AiConditionGroup` wraps child `AiCondition` lists enabling nested `<and>`/`<or>` combinators in Maven config.
- **Facts block generation**: `AiFactExtractor` counts exact whole-file regex matches to build the deterministic "facts" block prepended to AI summaries.

#### Key units
- `AiGenerationConfig` — mutable JavaBean; 40+ `DEFAULT_*` static constants; null-coercing setters; unmodifiable lists for dry sequence breakers and stop strings.
- `SrcMorphConfiguration` — root config bean; 22 field accessors; defaults for output dir (`src/site/ai`), provider (`mock`), llama context (`2048`), AI version (`0.0.0`); links `AiPromptDefinition`, `AiModelDefinition`, `AiFieldGenerationConfig`, `AiFactDefinition`.
- `AiOversizeStrategy` — enum with `fromConfig(@Nullable String)` parser; case-insensitive token matching; `IllegalArgumentException` listing valid tokens on unknown value.
- `AiConditionEvaluator` — stateless, immutable, not-safe; `matches`, `validate`, `usesLines`; `branchCount` enforces exactly one branch/leaf per node; `parseEpochMilli` wraps DateTimeParseException.
- `AiConditionGroup` — simple nullable `List<AiCondition>` holder with defensive copy in setter; not thread-safe.
- `AiFactExtractor` — static utility; `factsBlock` joins `label: count` entries under a header/suffix, returns `""` when no counters; `validate` checks nulls and regex compilability.

#### Data flow
- Maven plugin config → `SrcMorphConfiguration` (and `AiGenerationConfig` for generation parameters) → AI provider implementations consume the populated beans.
- Source indexer → `AiConditionEvaluator.matches(condition, context)` decides file inclusion/exclusion; `usesLines` gates whether file contents are read; `AiFactExtractor.factsBlock(counters, source)` runs over full source text to produce the deterministic facts block prepended to AI summaries.

#### Dependencies
- Internal: `AiCondition`, `AiFileContext`, `AiRangeCondition`, `AiSourceExcludeFilter`, `AiFactCounter`, `AiCalibration`, `AiPromptDefinition`, `AiModelDefinition`, `AiFieldGenerationConfig`, `AiFactDefinition` (same package/types).
- External: `java.util` (ArrayList, Collections, List), `java.time.Instant`/`DateTimeParseException`, `java.util.regex` (Pattern, Matcher, PatternSyntaxException), `java.io.File`, Lombok `@ToString`, `org.jspecify.annotations.Nullable`.
- Related: `net.ladenthin.srcmorph.prompt.AiPromptDefinition`; javadoc references `net.ladenthin.llama.parameters.ModelParameters`.

#### Cross-cutting
- All config beans carry `@SuppressWarnings({"NullAway.Init", "initialization.fields.uninitialized"})` and Lombok `@ToString`; mutable, not thread-safe, no synchronization.
- `@Nullable` annotations pervasive; many setters coerce null to defaults or fresh empty lists.
- Validation pattern: fail-fast `IllegalArgumentException` including context (label, valid tokens, branch count).
- Statelessness for evaluator/utility classes; enum constants immutable and thread-safe.
