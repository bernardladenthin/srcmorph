### java/config
- H: 1.0
- C: 245AEBCF
- D: 2026-10-31T20:34:56Z
- T: 2026-10-31T20:37:56Z
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
> Defines source-selection, fact-counting, model-routing, and generation settings for producing AI-assisted source documentation.

#### Purpose
- Provides mutable configuration for source scope, AI providers and models, prompts, generation parameters, output metadata, and oversized-file policy.
- Evaluates configured file conditions and computes exact regex-based source facts for use by downstream generation.

#### Responsibilities
- **Source selection:** `AiConditionEvaluator` validates and evaluates condition trees against file facts; `AiConditionGroup` stores Maven-bindable `<and>` / `<or>` children.
- **Generation configuration:** `SrcMorphConfiguration` holds run-wide source filters, prompts, model definitions, routing rules, llama.cpp fallbacks, and output metadata; `AiGenerationConfig` carries per-model inference and provider options.
- **Fact extraction:** `AiFactExtractor` validates regex counters and formats whole-source match counts.
- **Oversize policy:** `AiOversizeStrategy` parses configured choices for oversized source files: fail, sample, map-reduce, or deterministic output.

#### Key units
- `SrcMorphConfiguration` — JavaBean configuration for source selection, routing, fallbacks, regeneration, and generated-document metadata.
- `AiGenerationConfig` — mutable per-model settings for sampling, prompt limits, llama.cpp hardware/cache options, remote providers, reasoning, and stopping.
- `AiConditionEvaluator` — validates condition trees and matches them against `AiFileContext` facts.
- `AiConditionGroup` — stores child `AiCondition` values for Maven condition combinators.
- `AiFactExtractor` — counts regex matches and formats labeled exact whole-file facts.
- `AiOversizeStrategy` — maps config tokens to oversize-policy enum values, defaulting to `FAIL`.

#### Data flow
- `SrcMorphConfiguration` supplies source filters, model and prompt definitions, field-generation routing, and fallback settings to callers; it does not itself run generation or write files.
- Condition trees are validated, then `AiConditionEvaluator` matches them against file context; `usesLines` identifies whether line counts are needed.
- Configured `AiFactCounter` patterns are applied to source text by `AiFactExtractor`, which returns a formatted facts block.
- Oversize configuration tokens are parsed by `AiOversizeStrategy`; the enum describes policy choices but does not execute them.

#### Dependencies
- Internal configuration types: `AiModelDefinition`, `AiFieldGenerationConfig`, `AiFactDefinition`, `AiPromptDefinition`, `AiCalibration`, `AiCondition`, `AiRangeCondition`, `AiFileContext`, and `AiFactCounter`.
- File-path matching uses `AiSourceExcludeFilter`; generation settings reference llama.cpp model parameters.
- Java collections, file and time APIs, regex utilities, Lombok `@ToString`, and JSpecify nullability annotations.

#### Cross-cutting
- Configuration classes are mutable, unsynchronized JavaBeans; `AiConditionEvaluator`, `AiFactExtractor`, and `AiOversizeStrategy` are stateless or immutable utilities.
- Validation is explicit for condition trees and fact-counter regexes; `AiGenerationConfig` and `SrcMorphConfiguration` do not validate settings in setters.
- Nullable fields and defaults are common: generation settings supply defaults, null list inputs in `AiGenerationConfig` reset to empty lists, and unknown nonblank oversize tokens throw `IllegalArgumentException`.
