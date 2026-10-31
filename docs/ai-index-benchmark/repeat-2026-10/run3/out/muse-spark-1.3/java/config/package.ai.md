### java/config
- H: 1.0
- C: 8C53276B
- D: 2026-10-31T15:20:26Z
- T: 2026-10-31T15:24:41Z
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
> AI source indexing configuration — selects files, tunes LLM summarization, and controls oversized-file and fact handling.
#### Purpose
- Centralizes AI documentation run configuration: file scope, model/sampling knobs, routing, prompts, facts, oversize handling.
- Provides evaluation/validation helpers for file-selection conditions and deterministic fact blocks.
#### Responsibilities
- File selection: `AiConditionEvaluator` + `AiConditionGroup` evaluate `and/or/not/extensions/size/lines/modifiedAfter/modifiedBefore/pathGlob` against `AiFileContext`.
- Deterministic facts: `AiFactExtractor` builds `Facts (exact, whole file)` block from `AiFactCounter` regex counts.
- Generation tuning: `AiGenerationConfig` holds 45-field LLM knobs — context, sampling, KV-cache, GPU offload, remote endpoint, trimming, DRY, stop/retry.
- Run root: `SrcMorphConfiguration` holds base/output dirs, subtrees/excludes/extensions, size bounds, providers, prompt/model/field/fact definitions, llama.cpp fallback, provenance versions.
- Oversize policy: `AiOversizeStrategy` — `FAIL/SAMPLE/MAP_REDUCE/DETERMINISTIC` via `<onOversize>` token.
#### Key units
- `AiConditionEvaluator` — matches/validates `AiCondition` trees, reports `usesLines` optimization flag.
- `AiConditionGroup` — wraps `List<AiCondition>` for Maven `<conditions><condition>` binding of `and`/`or`.
- `AiFactExtractor` — `factsBlock`, `validate`, `countMatches`; `FACTS_HEADER/SUFFIX`, `ENTRY_SEPARATOR` formatting.
- `AiGenerationConfig` — mutable JavaBean with `DEFAULT_*` constants, tri-state `chatTemplateEnableThinking`, `-1`/empty do-not-set sentinels.
- `AiOversizeStrategy` — `configValue()`, `fromConfig()` case-insensitive parse with `DEFAULT=FAIL`.
- `SrcMorphConfiguration` — root bean with `DEFAULT_OUTPUT_DIRECTORY`, `DEFAULT_GENERATION_PROVIDER`, `DEFAULT_LLAMA_*`, `DEFAULT_AI_VERSION`.
#### Data flow
- `AiCondition` + `AiFileContext(fileName,sizeBytes,lineCount,lastModifiedEpochMilli,relativePath)` -> `AiConditionEvaluator.matches` -> boolean selection; `validate` fail-fast, `usesLines` gates line counting.
- `List<AiFactCounter>(label,pattern)` + full `source:String` -> `Pattern.compile` + `Matcher.find()` -> `label: count` facts block prepended to AI body.
- `SrcMorphConfiguration` -> file-scope filtering + `AiGenerationConfig`/`AiModelDefinition`/`AiFieldGenerationConfig`/`AiPromptDefinition`/`AiFactDefinition` -> routing plan, model resolution, `.ai.md` generation.
- Oversized source vs routed context window -> `AiOversizeStrategy.fromConfig` -> fail / head-sample / chunk map-reduce / deterministic size+lines+head/tail fallback.
#### Dependencies
- Internal: `AiCondition`, `AiConditionGroup`, `AiRangeCondition`, `AiFileContext`, `AiFactCounter`, `AiCalibration`, `AiModelDefinition`, `AiFieldGenerationConfig`, `AiFactDefinition`, `AiPromptDefinition`, `AiSourceExcludeFilter`.
- External: `java.time.Instant`, `java.util.regex.Pattern/Matcher`, `java.util.List/Collections/ArrayList`, `java.io.File`, `lombok.ToString`, `org.jspecify.annotations.Nullable`.
#### Cross-cutting
- Mutable JavaBeans with defaults + null-tolerant setters; `List` getters return unmodifiable views, setters defensive-copy.
- Fail-fast validation: `IllegalArgumentException` for bad branches/extensions/bounds/dates/globs/labels/patterns/strategies; `IllegalStateException` for branchless match.
- Stateless thread-safe utilities (`AiConditionEvaluator`, `AiFactExtractor`) vs non-synchronized mutable configs (`AiGenerationConfig`, `SrcMorphConfiguration`, `AiConditionGroup`).
