### java/config
- H: 1.0
- C: FE9A68C7
- D: 2026-10-31T19:47:56Z
- T: 2026-10-31T19:51:05Z
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
> Defines source-file selection, AI generation, and documentation metadata settings, while providing condition evaluation and source-fact formatting for code documentation workflows.

#### Purpose
- Centralizes mutable configuration for source indexing, model/provider routing, prompts, per-field generation, and output metadata.
- Supplies utilities to validate and evaluate file-selection conditions and to extract regex-count facts from source text.

#### Responsibilities
- **Source scope and routing:** `SrcMorphConfiguration` configures source subtrees, excludes, extensions, size bounds, model definitions, field-generation rules, and fallback llama.cpp settings.
- **Generation settings and oversized inputs:** `AiGenerationConfig` holds model, sampling, inference, provider, caching, reasoning, and stop settings; `AiOversizeStrategy` parses policies for files beyond a routed model’s context window.
- **Conditional file selection:** `AiConditionEvaluator` validates and evaluates nested conditions over file facts; `AiConditionGroup` stores child conditions for Maven-bound `<and>` and `<or>` rules.
- **Source facts:** `AiFactExtractor` validates configured regex counters and formats whole-source match counts for generated summaries.

#### Key units
- `SrcMorphConfiguration` — binding-neutral settings for source scope, AI definitions and routing, fallback inference, and generated-document metadata.
- `AiGenerationConfig` — mutable per-model generation configuration with defaults and nullable or sentinel-valued options.
- `AiConditionEvaluator` — validates condition trees and matches them against `AiFileContext` facts.
- `AiConditionGroup` — holds condition children for logical combinations.
- `AiFactExtractor` — counts regex matches across source text and formats labeled fact entries.
- `AiOversizeStrategy` — represents `FAIL`, `SAMPLE`, `MAP_REDUCE`, and `DETERMINISTIC` oversized-source policies; defaults to `FAIL`.

#### Data flow
- `SrcMorphConfiguration` supplies source-selection and generation settings to consumers; `AiGenerationConfig` carries per-model inference and provider options.
- `AiConditionEvaluator` consumes an `AiCondition` and `AiFileContext`, validates the condition tree, and returns a match result; path globs delegate to `AiSourceExcludeFilter`.
- `AiFactExtractor` consumes configured `AiFactCounter` entries and source text, then returns a formatted facts block.

#### Dependencies
- Condition evaluation uses `AiCondition`, `AiConditionGroup`, `AiRangeCondition`, and `AiFileContext`; path matching uses `AiSourceExcludeFilter`.
- Configuration references `AiPromptDefinition`, `AiModelDefinition`, `AiFieldGenerationConfig`, `AiFactDefinition`, and `AiCalibration`.
- Uses Java collections, regex and time parsing utilities, Lombok `@ToString`, and JSpecify `@Nullable`.

#### Cross-cutting
- Configuration classes are mutable, unsynchronized JavaBeans; `AiConditionEvaluator` and `AiFactExtractor` are stateless.
- Validation is explicit for condition trees and fact counters; `SrcMorphConfiguration` and `AiGenerationConfig` do not validate ranges or values.
- Nullable values and sentinel defaults represent optional or unset generation settings; `AiGenerationConfig` exposes unmodifiable list views.
