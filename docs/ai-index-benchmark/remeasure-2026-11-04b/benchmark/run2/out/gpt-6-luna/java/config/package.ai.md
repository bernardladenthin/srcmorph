### java/config
- H: 1.0
- C: 6E389E7C
- D: 2026-11-04T11:56:04Z
- T: 2026-11-04T11:59:28Z
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
> Defines source-file selection, AI generation settings, and factual metadata used to produce indexed source documentation.

#### Purpose
- Centralizes `srcmorph` run configuration, including source scope, model routing, generation controls, and output metadata.
- Provides condition evaluation and fact-count formatting utilities for indexing and AI-generated summaries.

#### Responsibilities
- **Source selection:** `AiConditionEvaluator` validates and evaluates nested conditions over file extension, size, line count, modification time, and relative-path glob; `AiConditionGroup` supports Maven binding of `<and>` and `<or>` child conditions.
- **Generation configuration:** `SrcMorphConfiguration` holds run-wide settings, while `AiGenerationConfig` carries per-model inference, sampling, provider, caching, hardware, reasoning, and stopping options.
- **Oversize handling:** `AiOversizeStrategy` parses the configured policy for files larger than a routed model’s context window.
- **Facts:** `AiFactExtractor` validates regex-based counters and formats whole-source match counts as a facts block.

#### Key units
- `SrcMorphConfiguration` — stores source-selection rules, generation definitions, llama.cpp fallbacks, and generated-documentation metadata.
- `AiGenerationConfig` — mutable per-model settings with defaults for inference and provider behavior.
- `AiConditionEvaluator` — validates condition trees, detects line-count requirements, and tests conditions against `AiFileContext`.
- `AiConditionGroup` — stores a defensive copy of child conditions for configuration binding.
- `AiFactExtractor` — validates `AiFactCounter` definitions and formats their regex match counts.
- `AiOversizeStrategy` — represents `FAIL`, `SAMPLE`, `MAP_REDUCE`, or `DETERMINISTIC` handling policies.

#### Data flow
- Source-file facts in `AiFileContext` are checked against validated `AiCondition` trees; path globs are evaluated through `AiSourceExcludeFilter`.
- Configured `AiFactCounter` patterns are matched against full source text and rendered as a labeled facts block.
- Maven or Java configuration populates `SrcMorphConfiguration` and per-model `AiGenerationConfig`; `AiOversizeStrategy.fromConfig` converts the configured oversize token to a strategy.

#### Dependencies
- Configuration types: `AiPromptDefinition`, `AiModelDefinition`, `AiFieldGenerationConfig`, `AiFactDefinition`, `AiCalibration`.
- Condition evaluation types: `AiCondition`, `AiConditionGroup`, `AiRangeCondition`, `AiFileContext`, `AiSourceExcludeFilter`.
- Fact extraction type: `AiFactCounter`; Java regex and time APIs support counting and date parsing.
- Uses Java collections, `Nullable` annotations, and Lombok `@ToString`.

#### Cross-cutting
- Configuration classes are mutable JavaBeans with no synchronization or thread-safety guarantees.
- `AiConditionEvaluator` and `AiFactExtractor` validate configuration explicitly; configuration holders generally store values without range validation.
- Nullable collection and string inputs are handled differently by type: condition groups preserve `null`, while several generation-setting setters substitute defaults or empty lists.
