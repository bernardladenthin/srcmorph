### java/config
- H: 1.0
- C: 6E389E7C
- D: 2026-11-04T11:46:43Z
- T: 2026-11-04T11:50:02Z
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
> Defines source-file selection, AI generation, and indexing configuration, including condition matching, exact source facts, model settings, and oversized-file policies.

#### Purpose
- Centralizes Maven-bindable settings for which source files are indexed and how AI summaries are generated.
- Provides utilities for evaluating file conditions and adding configured regex-count facts to generated summaries.

#### Responsibilities
- **File conditions:** `AiConditionEvaluator` validates and evaluates nested extension, size, line-count, modification-time, and path-glob rules; `AiConditionGroup` holds `<and>` / `<or>` child conditions.
- **Fact extraction:** `AiFactExtractor` validates configured labels and regexes, counts matches across source text, and formats a facts block.
- **Generation settings:** `AiGenerationConfig` stores per-model sampling, runtime, provider, token-budget, reasoning, and stopping settings.
- **Source-index configuration:** `SrcMorphConfiguration` stores source-selection rules, prompts, model and field-generation routing, fact definitions, llama.cpp fallback settings, and output metadata.
- **Oversize policy:** `AiOversizeStrategy` parses the configured action for files exceeding a routed model’s context window.

#### Key units
- `SrcMorphConfiguration` — mutable top-level configuration for source indexing, AI routing, fallback inference, and generated-index metadata.
- `AiGenerationConfig` — per-model generation options with defaults and Maven-settable properties.
- `AiConditionEvaluator` — validates condition trees, matches them against `AiFileContext`, and detects line-count requirements.
- `AiConditionGroup` — stores child `AiCondition` entries for combinators.
- `AiFactExtractor` — validates `AiFactCounter` definitions and formats whole-source regex match counts.
- `AiOversizeStrategy` — represents `FAIL`, `SAMPLE`, `MAP_REDUCE`, and `DETERMINISTIC` handling modes.

#### Data flow
- `SrcMorphConfiguration` supplies source filters, field/model routing, prompts, facts, and generation settings to the indexing workflow.
- `AiConditionEvaluator` validates an `AiCondition` tree, then compares its selected rules with file facts from `AiFileContext`.
- `AiFactExtractor` validates configured counters and produces a formatted facts block from the source text.
- `AiOversizeStrategy.fromConfig` converts a configured token into a strategy; the enum describes the available oversized-source behaviors.

#### Dependencies
- Condition matching uses `AiCondition`, `AiConditionGroup`, `AiFileContext`, `AiRangeCondition`, and `AiSourceExcludeFilter`.
- Configuration references `AiPromptDefinition`, `AiModelDefinition`, `AiFieldGenerationConfig`, `AiFactDefinition`, `AiFactCounter`, and `AiCalibration`.
- Uses Java collections, regex, and time APIs; uses Lombok `@ToString` and JSpecify `@Nullable`.

#### Cross-cutting
- Configuration objects are mutable JavaBeans without declared thread-safety; `AiGenerationConfig` exposes unmodifiable views of its stop-string and DRY-breaker lists.
- Defaults are defined for generation, provider routing, output location, llama.cpp fallback settings, and oversize handling (`FAIL`).
- Validation is explicit for condition trees and fact definitions; most configuration setters do not validate values.
