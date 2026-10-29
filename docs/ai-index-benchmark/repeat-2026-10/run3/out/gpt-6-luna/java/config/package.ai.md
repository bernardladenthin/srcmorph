### java/config
- H: 1.0
- C: FDDD5FDA
- D: 2026-10-29T14:19:17Z
- T: 2026-10-29T14:23:21Z
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
> Configures which source files qualify for AI documentation, how generation is routed and tuned, and what factual or oversized-input handling applies.

#### Purpose
- Defines shared configuration and evaluation utilities for source selection, AI generation, and generated-documentation metadata.
- Validates file conditions and fact counters, and provides strategies for oversized source inputs.

#### Responsibilities
- **Source filtering:** evaluate nested file conditions using extensions, size, line count, modification time, and relative-path globs; identify whether line counts are needed.
- **Generation setup:** store provider, model, prompt, routing, sampling, hardware, reasoning, caching, and fallback llama.cpp settings.
- **Facts and oversized inputs:** validate regex-based fact counters, format exact whole-file counts, and parse oversized-file handling strategies.
- **Run metadata:** configure source roots and exclusions, output location, regeneration and plan-only flags, versions, and project name.

#### Key units
- `SrcMorphConfiguration` — shared mutable configuration for source selection, prompt/model definitions, field routing, fallback inference, and output metadata.
- `AiGenerationConfig` — detailed generation-parameter carrier with provider, sampling, hardware, reasoning, stop-string, and calibration settings.
- `AiConditionEvaluator` — validates and evaluates `AiCondition` trees against `AiFileContext`.
- `AiConditionGroup` — stores child conditions for Maven-compatible `<and>` and `<or>` configuration.
- `AiFactExtractor` — validates `AiFactCounter` definitions and formats regex-match counts over whole source strings.
- `AiOversizeStrategy` — defines and parses `FAIL`, `SAMPLE`, `MAP_REDUCE`, and `DETERMINISTIC` handling choices.

#### Data flow
- Source-selection configuration supplies conditions and file filters; `AiConditionEvaluator` validates condition trees and checks them against file facts.
- Generation configuration carries prompt, model, routing, and inference settings to consumers; `SrcMorphConfiguration` also provides fallback llama.cpp parameters when field-generation rules are empty.
- Configured fact counters are validated, then `AiFactExtractor` counts regex matches in source text and returns a formatted facts block.
- Oversize configuration tokens are parsed by `AiOversizeStrategy` into handling choices.

#### Dependencies
- Configuration types: `AiCondition`, `AiConditionGroup`, `AiRangeCondition`, `AiFileContext`, `AiFactCounter`, `AiCalibration`, `AiPromptDefinition`, `AiModelDefinition`, `AiFieldGenerationConfig`, and `AiFactDefinition`.
- Path-glob matching delegates to `AiSourceExcludeFilter`.
- Uses Java collections, regex, and time APIs; Lombok `@ToString` and JSpecify `@Nullable`.

#### Cross-cutting
- Configuration objects are mutable, unsynchronized JavaBeans or data carriers; no thread-safety guarantees are stated.
- Validation is explicit in `AiConditionEvaluator` and `AiFactExtractor`; `AiGenerationConfig` and `SrcMorphConfiguration` do not validate assigned values.
- Optional values commonly use `@Nullable`; defaults include fail-on-oversize, mock generation provider, and fallback llama.cpp settings.
