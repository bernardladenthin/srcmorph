### java/config
- H: 1.0
- C: 6E389E7C
- D: 2026-11-01T14:19:41Z
- T: 2026-11-01T14:23:10Z
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
> Configures source-file selection and AI summarization policies, evaluates file conditions, and formats deterministic source facts for generated `.ai.md` indexes.

#### Purpose
- Provides the mutable configuration and condition-evaluation building blocks for selecting files and routing AI generation.
- Defines oversized-input strategies and utilities for validating and formatting configured whole-source facts.

#### Responsibilities
- **Run and generation settings:** `SrcMorphConfiguration` holds file filters, routing rules, prompt/model/fact definitions, fallback inference settings, and output metadata; `AiGenerationConfig` stores per-model sampling, inference, provider, and trimming parameters.
- **File-condition handling:** `AiConditionGroup` holds Maven-bindable condition children; `AiConditionEvaluator` validates and evaluates nested conditions against file facts.
- **Facts and size policies:** `AiFactExtractor` validates regex counters and formats whole-source match counts; `AiOversizeStrategy` parses policies for files exceeding a model’s context window.

#### Key units
- `SrcMorphConfiguration` — shared JavaBean for source selection, output, routing, models, prompts, facts, fallback settings, and generated-index metadata.
- `AiGenerationConfig` — per-generation settings with defaults for inference, sampling, trimming, caching, provider routing, reasoning, and repetition controls.
- `AiConditionEvaluator` — validates `AiCondition` trees and matches them against file facts.
- `AiConditionGroup` — stores a defensive copy of condition children for `<and>` or `<or>` configuration.
- `AiFactExtractor` — validates `AiFactCounter` definitions and formats non-overlapping regex counts as a facts block.
- `AiOversizeStrategy` — represents and parses `FAIL`, `SAMPLE`, `MAP_REDUCE`, and `DETERMINISTIC` policies; defaults to `FAIL`.

#### Data flow
- `SrcMorphConfiguration` supplies file-selection and generation settings; `AiConditionEvaluator` uses `AiFileContext` facts to decide whether a file matches a configured condition tree.
- Configured `AiFactCounter` entries and full source text pass through `AiFactExtractor` validation and counting to produce formatted facts text.
- An oversize configuration token is parsed by `AiOversizeStrategy.fromConfig` into a policy; `AiGenerationConfig` carries per-model settings for generation providers.

#### Dependencies
- Condition evaluation uses `AiCondition`, `AiConditionGroup`, `AiRangeCondition`, `AiFileContext`, and `AiSourceExcludeFilter`.
- Configuration references `AiPromptDefinition`, `AiModelDefinition`, `AiFieldGenerationConfig`, `AiFactDefinition`, `AiCalibration`, and `AiGenerationConfig`.
- Fact extraction uses `AiFactCounter` and Java regex APIs; configuration classes use Java collections, `File`, Lombok `@ToString`, and JSpecify nullability annotations.

#### Cross-cutting
- Configuration objects are mutable, unsynchronized JavaBeans; `AiConditionEvaluator` and `AiFactExtractor` are stateless.
- Validation is explicit rather than automatic: condition trees and fact counters have validation methods, while `SrcMorphConfiguration` and `AiGenerationConfig` generally store values without validating them.
- Nullable configuration values and list reset behavior are supported; `AiGenerationConfig` exposes unmodifiable views of its DRY sequence breakers and stop strings.
