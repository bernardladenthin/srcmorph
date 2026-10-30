### java/config
- H: 1.0
- C: F42640AF
- D: 2026-10-30T00:29:58Z
- T: 2026-10-30T00:37:06Z
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
> Defines source-file eligibility, AI generation settings, and run metadata for producing AI-assisted source summaries.

#### Purpose
- Configures source selection, generation providers and models, prompts, routing, facts, and output metadata.
- Evaluates per-file conditions and formats configured source facts for use in generation.

#### Responsibilities
- **File selection:** Evaluate nested `and`/`or`/`not` conditions for extensions, size, line count, modification time, and path globs; group child conditions for Maven binding.
- **Generation configuration:** Store model, prompt-limit, sampling, reasoning, cache, hardware, provider, and API settings in `AiGenerationConfig`.
- **Run configuration:** Store source filters, routing and fact definitions, model paths, fallback llama.cpp settings, output location, and run metadata in `SrcMorphConfiguration`.
- **Oversize policy:** Parse `AiOversizeStrategy` tokens for fail, sample, map-reduce, or deterministic handling.
- **Fact formatting:** Validate regex counters and produce labeled whole-source match counts.

#### Key units
- `AiConditionEvaluator` — validates and evaluates `AiCondition` trees against `AiFileContext`.
- `AiConditionGroup` — holds a defensive copy of child `AiCondition` entries.
- `AiFactExtractor` — validates `AiFactCounter` definitions and formats regex counts.
- `AiGenerationConfig` — mutable settings object for one AI generation step.
- `AiOversizeStrategy` — enum and configuration-token parser for oversized files.
- `SrcMorphConfiguration` — shared run configuration for source scope, generation, and output metadata.

#### Data flow
- `AiCondition` trees and `AiFileContext` facts flow into `AiConditionEvaluator` to determine file eligibility.
- Configured `AiFactCounter` entries and source text flow into `AiFactExtractor` to produce a formatted facts block.
- `SrcMorphConfiguration` supplies run-level definitions and fallback settings; `AiGenerationConfig` carries provider-ready generation settings.

#### Dependencies
- Uses project types including `AiCondition`, `AiRangeCondition`, `AiFileContext`, `AiFactCounter`, `AiCalibration`, and prompt, model, routing, and fact definition classes.
- Path-glob evaluation delegates to `AiSourceExcludeFilter`; configuration and fact formatting use Java collections and regex APIs.
- Uses Lombok `@ToString` and JSpecify `@Nullable`.

#### Cross-cutting
- Configuration classes are mutable JavaBeans and generally do not validate settings; `AiConditionEvaluator` and `AiFactExtractor` provide explicit validation.
- `AiGenerationConfig` initializes from `DEFAULT_*` constants; `SrcMorphConfiguration` supplies run defaults, including output directory `src/site/ai` and provider `mock`.
- Nullable inputs are supported in several configuration APIs; `AiGenerationConfig` normalizes selected null values, while list getters expose unmodifiable views.
- No thread-safety guarantees are provided for mutable configuration objects.
