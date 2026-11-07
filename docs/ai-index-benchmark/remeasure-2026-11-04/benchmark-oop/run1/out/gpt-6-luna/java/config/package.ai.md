### java/config
- H: 1.0
- C: 6E389E7C
- D: 2026-11-04T07:19:50Z
- T: 2026-11-04T07:22:52Z
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
> Defines source-file eligibility, factual extraction, generation settings, and oversize handling for producing AI-indexed source summaries.

#### Purpose
- Centralizes configuration and utility logic for selecting source files, routing AI generation, and formatting exact source facts.
- Supplies shared JavaBean settings for indexing and generation, including provider, model, prompt, and output metadata.

#### Responsibilities
- **File-condition evaluation:** validates and evaluates `AiCondition` trees against file metadata, including extensions, size, line count, modification time, and relative-path globs.
- **Fact counting:** validates regex-based `AiFactCounter` definitions and formats whole-source match counts into a facts block.
- **Generation configuration:** stores per-model inference, prompt-sizing, provider-routing, runtime, sampling, stop-string, and calibration settings with defaults.
- **Index configuration:** `SrcMorphConfiguration` holds source roots, exclusions, extensions, size bounds, field-generation rules, prompt/model/fact definitions, llama.cpp fallbacks, and output metadata.
- **Oversize policy and binding:** `AiOversizeStrategy` parses oversize handling tokens; `AiConditionGroup` supports Maven binding of `and`/`or` condition children.

#### Key units
- `AiConditionEvaluator` — validates condition trees and matches them against `AiFileContext`.
- `AiConditionGroup` — stores child `AiCondition` values for combinator configuration.
- `AiFactExtractor` — counts regex matches in complete source text and emits the formatted facts block.
- `AiGenerationConfig` — mutable per-model generation settings with defaults and JavaBean accessors.
- `AiOversizeStrategy` — defines `FAIL`, `SAMPLE`, `MAP_REDUCE`, and `DETERMINISTIC` policies and parses their config tokens.
- `SrcMorphConfiguration` — shared configuration for source discovery, AI routing, generation fallbacks, and generated index metadata.

#### Data flow
- `AiConditionEvaluator` receives an `AiCondition` and `AiFileContext`, validates the condition structure, and returns whether the file qualifies.
- `AiFactExtractor` receives configured counters and full source text, then returns exact counts in a facts block.
- `SrcMorphConfiguration` supplies source-selection and routing settings, prompts, model definitions, generation rules, and output metadata to indexing and generation consumers.
- `AiGenerationConfig` carries model-specific settings toward AI providers; `AiOversizeStrategy` supplies the configured policy for files exceeding context limits.

#### Dependencies
- Condition evaluation uses `AiCondition`, `AiConditionGroup`, `AiRangeCondition`, `AiFileContext`, and `AiSourceExcludeFilter`.
- Fact extraction uses `AiFactCounter` and Java regex utilities.
- Configuration references `AiCalibration`, `AiPromptDefinition`, `AiModelDefinition`, `AiFieldGenerationConfig`, and `AiFactDefinition`.
- Java standard libraries provide collections, file and time handling, and regex support; Lombok `ToString` and JSpecify `Nullable` appear in configuration types.

#### Cross-cutting
- `AiConditionEvaluator` and `AiFactExtractor` are stateless utilities; configuration objects are mutable JavaBeans without thread-safety guarantees.
- Nullable collections and references are common; `AiConditionGroup` copies assigned child collections, while configuration list getters generally expose stored values or unmodifiable views.
- Validation is explicit in condition and fact utilities; invalid condition structure, regexes, or unknown nonblank oversize tokens produce `IllegalArgumentException`.
