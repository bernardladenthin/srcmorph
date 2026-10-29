### java/config
- H: 1.0
- C: 3811BE6C
- D: 2026-10-28T21:19:32Z
- T: 2026-10-28T21:22:20Z
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
> Defines source-selection, AI generation, and metadata settings for producing AI-assisted source documentation.

#### Purpose
- Centralizes configuration for source discovery, file eligibility, model/provider routing, prompt and fact setup, and generated documentation metadata.
- Supplies condition evaluation, fact counting, and oversized-file policy utilities used by that configuration.

#### Responsibilities
- **Source and generation configuration:** `SrcMorphConfiguration` holds source scopes and exclusions, file-size limits, provider and model definitions, field-generation rules, fallback llama.cpp parameters, and output metadata.
- **Per-model inference settings:** `AiGenerationConfig` carries defaults and mutable options for context and output limits, sampling, hardware, caching, remote providers, reasoning, and stopping behavior.
- **File-condition matching:** `AiConditionEvaluator` validates and evaluates condition trees against file metadata, including extensions, ranges, timestamps, and path globs; `AiConditionGroup` supports Maven binding of `and`/`or` children.
- **Source facts and oversize handling:** `AiFactExtractor` validates regex counters and formats whole-source match counts; `AiOversizeStrategy` parses policies for oversized files (`FAIL`, `SAMPLE`, `MAP_REDUCE`, `DETERMINISTIC`).

#### Key units
- `SrcMorphConfiguration` — shared configuration bean for source scope, generation routing, fallback model settings, and documentation metadata.
- `AiGenerationConfig` — per-generation-step settings with inference defaults and provider-specific options.
- `AiConditionEvaluator` — stateless condition-tree validator and file-context matcher.
- `AiConditionGroup` — mutable child-condition container for combinator configuration.
- `AiFactExtractor` — utility for validating fact counters and generating formatted regex-count facts.
- `AiOversizeStrategy` — enum and configuration-token parser; defaults to `FAIL`.

#### Data flow
- Source metadata and an `AiCondition` tree go to `AiConditionEvaluator`, which validates the tree and determines whether the file matches.
- Configured `AiFactCounter` entries and full source text go to `AiFactExtractor`, which produces a facts block; validation can check counters before use.
- Generation and source-discovery settings are held by `SrcMorphConfiguration` and `AiGenerationConfig`; oversized-file policy tokens are parsed by `AiOversizeStrategy`.

#### Dependencies
- Internal configuration types include `AiCondition`, `AiRangeCondition`, `AiFileContext`, `AiFactCounter`, `AiCalibration`, `AiPromptDefinition`, `AiModelDefinition`, `AiFieldGenerationConfig`, and `AiFactDefinition`.
- Path-glob matching delegates to `AiSourceExcludeFilter`; `AiGenerationConfig` references llama.cpp integration concepts.
- Uses Java collections, regex, and time APIs; Lombok `ToString` and JSpecify `Nullable` annotations.

#### Cross-cutting
- Configuration classes are mutable, unsynchronized JavaBeans; `AiConditionEvaluator` and `AiFactExtractor` are stateless utilities, while `AiOversizeStrategy` is immutable.
- Validation is concentrated in condition and fact utilities; generation and shared configuration setters generally do not validate values.
- Nullable fields and collections are common; `AiGenerationConfig` provides defaults, and selected setters normalize null values.
