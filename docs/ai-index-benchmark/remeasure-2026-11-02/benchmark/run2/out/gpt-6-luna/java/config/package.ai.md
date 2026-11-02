### java/config
- H: 1.0
- C: 6E389E7C
- D: 2026-11-01T23:42:40Z
- T: 2026-11-01T23:45:42Z
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
> Configures AI-assisted source indexing and generation by selecting files, defining model and prompt behavior, and preparing source facts and oversize policies.

#### Purpose
- Centralizes `srcmorph` run settings for source selection, AI generation, model routing, and `.ai.md` output metadata.
- Provides reusable condition evaluation and regex fact-count formatting for source files.

#### Responsibilities
- **Source selection:** `AiConditionEvaluator` validates and evaluates condition trees over file metadata; `AiConditionGroup` holds Maven-bound condition children. `SrcMorphConfiguration` stores subtree, glob, extension, and size filters.
- **Generation configuration:** `AiGenerationConfig` stores sampling, input limits, stop conditions, provider routing, reasoning, and llama.cpp runtime settings; `SrcMorphConfiguration` provides run-level defaults and fallback model settings.
- **Facts and oversized inputs:** `AiFactExtractor` validates regex fact counters and formats whole-source counts; `AiOversizeStrategy` parses policies for handling files that exceed a model’s context window.

#### Key units
- `SrcMorphConfiguration` — mutable shared run configuration for file filters, prompts, models, field-generation rules, facts, fallback inference, and output metadata.
- `AiGenerationConfig` — per-model generation and runtime options, including remote-provider routing and llama.cpp controls.
- `AiConditionEvaluator` — validates condition trees and matches them against `AiFileContext` facts.
- `AiConditionGroup` — stores nullable child conditions for `<and>` and `<or>` configuration.
- `AiFactExtractor` — counts configured regex matches across source text and formats a facts block.
- `AiOversizeStrategy` — represents `FAIL`, `SAMPLE`, `MAP_REDUCE`, and `DETERMINISTIC` handling.

#### Data flow
- Condition configuration is validated, then evaluated against `AiFileContext` values such as filename, size, line count, modification time, and relative path.
- Fact-counter definitions and full source text produce a formatted whole-file facts block; invalid definitions or patterns are rejected by validation.
- Generation and run settings are populated through JavaBean setters; `AiOversizeStrategy.fromConfig` maps configuration tokens to a policy.

#### Dependencies
- Condition evaluation uses `AiCondition`, `AiConditionGroup`, `AiRangeCondition`, `AiFileContext`, and `AiSourceExcludeFilter`.
- Fact formatting uses `AiFactCounter`; generation settings reference `AiCalibration`.
- `SrcMorphConfiguration` stores `AiPromptDefinition`, `AiModelDefinition`, `AiFieldGenerationConfig`, and `AiFactDefinition`.
- Uses Java collections, regex and time APIs, Lombok `@ToString`, and JSpecify `@Nullable`.

#### Cross-cutting
- Configuration objects are mutable JavaBeans without synchronization or thread-safety guarantees; evaluator, fact extractor, and oversize enum are stateless or immutable.
- Defaults are provided for generation and run settings; several optional fields use nullable values or sentinel defaults.
- Validation is concentrated in condition and fact helpers; generation and run configuration setters generally do not validate ranges or values.
