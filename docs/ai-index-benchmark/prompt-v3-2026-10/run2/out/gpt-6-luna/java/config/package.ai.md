### java/config
- H: 1.0
- C: 386EAE55
- D: 2026-10-31T20:10:35Z
- T: 2026-10-31T20:13:18Z
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
> Configures source-file selection and AI-generated documentation, including routing, prompt budgets, file facts, and oversized-file handling.

#### Purpose
- Provides Maven-bindable settings and helper types for selecting source files and preparing AI generation.
- Defines condition evaluation, exact source-fact formatting, generation parameters, and oversized-file strategy parsing.

#### Responsibilities
- **Source selection:** `AiConditionEvaluator` validates and evaluates metadata conditions; `AiConditionGroup` stores child rules for `and`/`or` configuration.
- **Generation configuration:** `SrcMorphConfiguration` collects source scope, output, prompts, models, routing, reusable facts, and fallback llama.cpp settings; `AiGenerationConfig` carries per-step provider and inference parameters.
- **Fact extraction:** `AiFactExtractor` validates regex counters and formats whole-source match counts for generated summaries.
- **Oversize policy:** `AiOversizeStrategy` parses configured tokens for fail, sample, map-reduce, or deterministic handling.

#### Key units
- `SrcMorphConfiguration` — central mutable configuration for source indexing, generated `.ai.md` output, routing, and metadata.
- `AiGenerationConfig` — per-generation model, sampling, prompt-budget, provider, hardware, cache, and calibration settings.
- `AiConditionEvaluator` — validates condition trees and matches them against `AiFileContext` metadata.
- `AiConditionGroup` — Maven-bindable collection of child `AiCondition` rules.
- `AiFactExtractor` — counts configured regex matches across source text and renders a facts block.
- `AiOversizeStrategy` — enum and configuration-token parser for oversized-file policies.

#### Data flow
- `SrcMorphConfiguration` supplies source filters and routing-related definitions; `AiConditionEvaluator` applies configured conditions to file metadata and can identify conditions requiring line counts.
- `AiFactExtractor` takes configured `AiFactCounter` entries and source text, then returns formatted exact whole-file counts.
- `AiGenerationConfig` carries settings to provider implementations; it stores prompt-budget parameters but does not calculate input limits or perform inference.
- `AiOversizeStrategy.fromConfig` converts a configured token to the selected strategy, defaulting to `FAIL` for null or blank input.

#### Dependencies
- Internal configuration and domain types include `AiCondition`, `AiRangeCondition`, `AiFileContext`, `AiFactCounter`, `AiCalibration`, `AiPromptDefinition`, `AiModelDefinition`, `AiFieldGenerationConfig`, and `AiFactDefinition`.
- Path-glob condition matching delegates to `AiSourceExcludeFilter`.
- Uses Java collections, regex and time APIs; Lombok `@ToString`; JSpecify `@Nullable`.

#### Cross-cutting
- Configuration classes and `AiConditionGroup` are mutable JavaBeans without synchronization; `AiGenerationConfig` exposes unmodifiable list views, while its setters retain supplied list instances.
- Validation is explicit in `AiConditionEvaluator.validate` and `AiFactExtractor.validate`; configuration holder classes generally do not validate their fields.
- `AiConditionEvaluator` and `AiFactExtractor` are stateless; fact extraction performs no I/O.
