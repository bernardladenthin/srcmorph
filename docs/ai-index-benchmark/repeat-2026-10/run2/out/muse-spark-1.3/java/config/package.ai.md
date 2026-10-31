### java/config
- H: 1.0
- C: 75B26684
- D: 2026-10-31T15:13:57Z
- T: 2026-10-31T15:18:28Z
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
> Configures AI-driven source documentation runs by selecting files, budgeting LLM generation, and handling oversized sources.

#### Purpose
- Central configuration holder and helpers for `srcmorph` AI summarization runs bindable from Maven / JSON / YAML.

#### Responsibilities
- File selection: evaluate `AiCondition` trees against file facts — extensions, size/lines ranges, modified dates, path globs.
- Condition grouping: `AiCondition` `<and>`/`<or>` child-list wrapper for Maven XML binding.
- Deterministic facts: build exact whole-file regex-count `FACTS` block and validate `AiFactCounter` labels/patterns.
- Generation tuning: single-step LLM parameters — context/trimming, sampling, cache/GPU, remote provider, DRY, calibration.
- Oversize policy: `FAIL` / `SAMPLE` / `MAP_REDUCE` / `DETERMINISTIC` routing with token parsing.
- Run scope: base/output directories, subtrees/excludes/extensions/size bounds, prompts/models/field routing/facts, llama.cpp fallback, version headers, force/planOnly.

#### Key units
- `SrcMorphConfiguration` — mutable JavaBean for entire run; directories, filters, providers, prompts, models, routing, fallback.
- `AiGenerationConfig` — mutable JavaBean for one generation step; sampling, budgeting, caching, GPU, remote, DRY, `AiCalibration`.
- `AiConditionEvaluator` — stateless final evaluator; `matches`, `validate`, `usesLines` over `AiFileContext`.
- `AiConditionGroup` — `List<AiCondition>` holder for `<and>`/`<or>` combinators with defensive copy.
- `AiFactExtractor` — static utility; `factsBlock`, `validate`, `countMatches` via regex.
- `AiOversizeStrategy` — enum with `configValue`, `fromConfig`; default `FAIL`.

#### Data flow
- `SrcMorphConfiguration` supplies scope + `fieldGenerations` routing + `factDefinitions` + `AiGenerationConfig`-style fallback to engine.
- `AiConditionEvaluator.matches` filters `AiFileContext` (fileName, relativePath, sizeBytes, lineCount, lastModified) with `AiRangeCondition`, `Instant.parse`, `AiSourceExcludeFilter`.
- `usesLines` signals when file reads needed; `validate`/`validateGroup` fail-fast before matching.
- Oversized sources branch by `AiOversizeStrategy.fromConfig`; `DETERMINISTIC` path uses `AiFactExtractor.factsBlock` counts prepended to summary.

#### Dependencies
- Internal: `AiCondition`, `AiRangeCondition`, `AiFileContext`, `AiSourceExcludeFilter`, `AiFactCounter`, `AiCalibration`, `AiPromptDefinition`, `AiModelDefinition`, `AiFieldGenerationConfig`, `AiFactDefinition`.
- External: `java.time.Instant`, `java.util.regex.Pattern/Matcher`, `java.io.File`, `java.util.List/Collections`, Lombok `@ToString`, `org.jspecify.annotations.Nullable`.

#### Cross-cutting
- Mutable non-synchronized JavaBeans (`SrcMorphConfiguration`, `AiGenerationConfig`, `AiConditionGroup`) with `DEFAULT_*` constants, null-coalescing setters, unmodifiable list views; assume populate-then-read-only.
- Stateless thread-safe utilities (`AiConditionEvaluator`, `AiFactExtractor`, immutable `AiOversizeStrategy`).
- Fail-fast validation: `IllegalArgumentException` on bad conditions/facts/oversize tokens, `IllegalStateException` on unvalidated match; null-tolerant empties mean unset/vacuous.
