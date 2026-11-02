### java/config
- H: 1.0
- C: 6E389E7C
- D: 2026-11-01T23:26:17Z
- T: 2026-11-01T23:29:10Z
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
> Configures AI-assisted source indexing through file-selection conditions, model and prompt routing, generation settings, exact source facts, and oversized-file policies.

> Configures AI-assisted source indexing through file-selection conditions, model and prompt routing, generation settings, exact source facts, and oversized-file policies.

#### Purpose
- Centralizes Maven-bindable settings for selecting source files, routing AI summaries, and locating generated `.ai.md` output.
- Provides supporting condition evaluation, fact extraction, and oversized-file strategy parsing.

#### Responsibilities
- **Run and generation configuration:** `SrcMorphConfiguration` holds project scope, output metadata, prompts, model definitions, per-field routing, reusable facts, and llama.cpp fallback settings; `AiGenerationConfig` holds provider, sampling, prompt-limit, reasoning, and runtime options.
- **File selection:** `AiConditionEvaluator` validates and evaluates condition trees against file metadata; `AiConditionGroup` stores child conditions for AND/OR configuration.
- **Source facts:** `AiFactExtractor` validates regex counters and formats exact whole-source match counts.
- **Oversize policy:** `AiOversizeStrategy` parses the configured action for files exceeding a routed model’s context window.

#### Key units
- `SrcMorphConfiguration` — mutable run-level configuration for source scope, AI routing, output, and metadata.
- `AiGenerationConfig` — per-model inference and provider settings, including llama.cpp runtime controls and OpenAI-compatible endpoint options.
- `AiConditionEvaluator` — validates condition trees and matches file extensions, size, line count, modification time, or relative-path globs.
- `AiConditionGroup` — stores a defensive copy of nullable child-condition collections.
- `AiFactExtractor` — counts non-overlapping regex matches and formats labeled exact-facts entries.
- `AiOversizeStrategy` — defines `FAIL`, `SAMPLE`, `MAP_REDUCE`, and `DETERMINISTIC` strategies with config-token parsing.

#### Data flow
- `AiConditionEvaluator` receives an `AiCondition` tree and `AiFileContext`; it evaluates combinators and leaf constraints, delegating path-glob checks to `AiSourceExcludeFilter`.
- `AiFactExtractor` receives configured counters and full source text, then returns a formatted facts block for inclusion with an AI summary.
- `SrcMorphConfiguration` supplies source scope, prompts, model definitions, field-generation rules, and fallback settings; `AiGenerationConfig` carries inference options to providers.
- `AiOversizeStrategy.fromConfig` converts a configured token to an oversize handling strategy; null or blank values default to `FAIL`.

#### Dependencies
- Condition evaluation uses `AiCondition`, `AiConditionGroup`, `AiRangeCondition`, `AiFileContext`, and `AiSourceExcludeFilter`.
- Configuration references `AiPromptDefinition`, `AiModelDefinition`, `AiFieldGenerationConfig`, `AiFactDefinition`, and `AiCalibration`.
- Fact extraction uses `AiFactCounter` and Java regex utilities; timestamps use `Instant`.
- Java collections and `File`, Lombok `@ToString`, and JSpecify `@Nullable` support the configuration types.

#### Cross-cutting
- Configuration classes are mutable, unsynchronized JavaBeans; `AiConditionEvaluator` and `AiFactExtractor` are stateless utilities.
- Condition and fact configuration provide explicit validation; generation and run-level configuration setters generally do not validate ranges or values.
- Nullable configuration is common: list setters may normalize null to empty lists, while other optional fields remain nullable or reset to declared defaults.
- `AiGenerationConfig` defines defaults for inference and runtime options; `SrcMorphConfiguration` defines defaults for output location, provider, llama.cpp fallback, and AI version.
