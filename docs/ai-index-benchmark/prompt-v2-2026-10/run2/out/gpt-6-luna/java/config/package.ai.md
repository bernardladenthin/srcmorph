### java/config
- H: 1.0
- C: E192320D
- D: 2026-10-31T14:44:17Z
- T: 2026-10-31T14:47:10Z
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
> Configures source-file selection and AI summary generation, including condition-based filtering, exact source facts, model/provider settings, and oversize-file policies.

#### Purpose
- Defines shared settings for a `srcmorph` run: source scope, output location, prompts, model routing, fallback inference, and generated metadata.
- Provides reusable condition evaluation and fact-count formatting for indexing and summarization.

#### Responsibilities
- **Source selection and conditions:** `SrcMorphConfiguration` stores subtree, extension, size, and exclude-glob settings; `AiConditionEvaluator` validates and evaluates recursive file conditions against `AiFileContext`; `AiConditionGroup` holds `<and>` / `<or>` children.
- **Generation and routing configuration:** `AiGenerationConfig` stores per-model sampling, prompt limits, runtime tuning, provider options, and calibration; `SrcMorphConfiguration` holds prompts, model definitions, field-generation rules, and llama.cpp fallback settings.
- **Facts and oversize handling:** `AiFactExtractor` validates regex counters and formats exact whole-source counts; `AiOversizeStrategy` parses fail, sample, map-reduce, or deterministic handling.

#### Key units
- `SrcMorphConfiguration` — shared mutable run configuration for source scope, AI generation, fallback settings, and output metadata.
- `AiGenerationConfig` — per-model generation parameters for sampling, trimming, provider routing, and inference runtimes.
- `AiConditionEvaluator` — validates condition trees and matches file context against metadata and path conditions.
- `AiConditionGroup` — stores child `AiCondition` values for logical combinators.
- `AiFactExtractor` — counts configured regex matches across full source text and formats a facts block.
- `AiOversizeStrategy` — defines and parses the strategy for files exceeding model context limits.

#### Data flow
- `SrcMorphConfiguration` supplies source-selection and generation settings; condition trees are validated and evaluated against `AiFileContext` to determine file matches.
- `AiFactExtractor` consumes configured `AiFactCounter` values and full source text, returning a formatted facts block when counts are available.
- Generation settings are carried by `AiGenerationConfig`; `AiOversizeStrategy.fromConfig` resolves configured oversize tokens to a strategy.

#### Dependencies
- Condition evaluation uses `AiCondition`, `AiConditionGroup`, `AiFileContext`, `AiRangeCondition`, and `AiSourceExcludeFilter`.
- Configuration references `AiPromptDefinition`, `AiModelDefinition`, `AiFieldGenerationConfig`, `AiFactDefinition`, `AiGenerationConfig`, and `AiCalibration`.
- Fact extraction uses `AiFactCounter` and Java regex facilities; configuration types use Java collections, Lombok `@ToString`, and JSpecify nullability annotations.

#### Cross-cutting
- Configuration classes are mutable JavaBeans without synchronization; `AiConditionEvaluator` and `AiFactExtractor` are stateless.
- Validation reports invalid conditions, fact counters, or unknown oversize strategy tokens with `IllegalArgumentException`; generation and run configuration classes do not provide explicit validation.
- Nullable values and defaults are handled per type: oversize null/blank tokens resolve to `FAIL`, while several generation and run settings have defined defaults.
