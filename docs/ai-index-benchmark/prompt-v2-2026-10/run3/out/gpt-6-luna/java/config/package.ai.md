### java/config
- H: 1.0
- C: 18A9ED9C
- D: 2026-10-31T14:57:29Z
- T: 2026-10-31T15:00:17Z
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
> Defines source-selection, AI-generation, routing, and metadata settings for producing AI-assisted project indexes.

#### Purpose
- Provides shared configuration objects for selecting source files, routing generation requests, and setting provider/model behavior.
- Supports condition evaluation and exact regex-based fact extraction for source summaries.

#### Responsibilities
- **Project and generation configuration:** `SrcMorphConfiguration` holds source filters, model and prompt definitions, field-generation rules, fallback llama.cpp settings, and output metadata; `AiGenerationConfig` holds per-generation inference, sampling, provider, prompt, cache, and calibration settings.
- **Source condition handling:** `AiConditionGroup` represents Maven-bindable condition groups; `AiConditionEvaluator` validates and matches condition trees against file facts.
- **Fact reporting:** `AiFactExtractor` validates configured fact counters and formats exact whole-file regex match counts.
- **Oversize policy:** `AiOversizeStrategy` parses routing configuration for handling files that exceed a model’s context window.

#### Key units
- `SrcMorphConfiguration` — shared mutable configuration for source scope, AI definitions and routing inputs, fallback inference settings, and generated-index metadata.
- `AiGenerationConfig` — defaults and accessors for model/provider, inference, sampling, prompt limits, and calibration options.
- `AiConditionEvaluator` — condition-tree validation and file matching by path, extension, size, line count, or modification time.
- `AiConditionGroup` — nullable child-condition collection for `<and>` and `<or>` configuration.
- `AiFactExtractor` — validation and formatting of regex-counted facts from complete source text.
- `AiOversizeStrategy` — `FAIL`, `SAMPLE`, `MAP_REDUCE`, and `DETERMINISTIC` policies, with `FAIL` as the default.

#### Data flow
- `SrcMorphConfiguration` supplies source-selection and generation definitions; `AiConditionEvaluator` applies configured conditions to `AiFileContext` facts, with path-glob matching delegated to `AiSourceExcludeFilter`.
- `AiFactExtractor` validates `AiFactCounter` definitions, counts matches in source text, and emits a formatted facts block.
- `AiOversizeStrategy.fromConfig` converts a configured token into a policy; `AiGenerationConfig` exposes generation settings to provider consumers.

#### Dependencies
- Internal configuration types include `AiPromptDefinition`, `AiModelDefinition`, `AiFieldGenerationConfig`, `AiFactDefinition`, `AiCalibration`, `AiCondition`, `AiRangeCondition`, `AiFileContext`, and `AiFactCounter`.
- Uses `AiSourceExcludeFilter` for glob matching; Java collections, regex, and time APIs for storage, counting, and timestamp parsing.
- Uses Lombok `ToString` and JSpecify nullability annotations.

#### Cross-cutting
- Configuration classes are mutable JavaBeans and are not documented as thread-safe; `AiConditionEvaluator` and `AiFactExtractor` are stateless.
- Condition and fact configuration validation reports invalid values with `IllegalArgumentException`; condition matching assumes prior validation.
- Nullable lists and fields represent omitted configuration; defaults include mock generation, standard llama.cpp fallback settings, and `FAIL` for oversize handling.
