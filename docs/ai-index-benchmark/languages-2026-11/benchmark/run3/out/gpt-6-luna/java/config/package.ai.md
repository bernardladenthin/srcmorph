### java/config
- H: 1.0
- C: 626FB0FF
- D: 2026-11-01T01:56:59Z
- T: 2026-11-01T02:00:47Z
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
> Configures source-file selection and AI-assisted summarization, including generation settings, oversized-file policies, and whole-source facts.

#### Purpose
- Defines shared configuration and utility logic for selecting source files and preparing AI-generated summaries.
- Supports Maven-bound condition rules, per-model generation settings, and deterministic source-fact extraction.

#### Responsibilities
- **File-selection rules:** evaluate `AiCondition` trees against file metadata and validate configured branches, ranges, globs, and timestamps.
- **Generation and run configuration:** store model/provider options and run-wide source, output, prompt, routing, and fallback settings.
- **Oversize handling:** parse policies for failing, sampling, map-reduce, or deterministic processing of files beyond the model context.
- **Fact extraction:** count configured regex matches across complete source text and format nonempty counts for summary input.

#### Key units
- `AiConditionEvaluator` — validates and matches composable conditions using `AiFileContext` facts.
- `AiConditionGroup` — holds child conditions in a Maven-bindable `<conditions><condition>` collection.
- `AiFactExtractor` — validates `AiFactCounter` patterns and formats whole-source match counts.
- `AiGenerationConfig` — stores per-model sampling, hardware, cache, provider, reasoning, and prompt-limit settings.
- `AiOversizeStrategy` — defines and parses the `FAIL`, `SAMPLE`, `MAP_REDUCE`, and `DETERMINISTIC` policies.
- `SrcMorphConfiguration` — holds run-wide source filters, output settings, prompts, model definitions, field-generation rules, facts, and llama.cpp fallback options.

#### Data flow
- `SrcMorphConfiguration` carries source scope, exclusions, extensions, generation provider, model and prompt definitions, and fallback settings.
- `AiConditionEvaluator` applies validated `AiCondition` trees to file facts; `usesLines` identifies conditions requiring line counts.
- `AiFactExtractor` counts configured regex patterns over source text and returns a formatted facts block.
- `AiOversizeStrategy.fromConfig` converts configured policy tokens into a strategy value for oversized-file routing.

#### Dependencies
- Selection uses `AiCondition`, `AiRangeCondition`, `AiFileContext`, and `AiSourceExcludeFilter`; timestamp conditions use ISO-8601 `Instant` parsing.
- Configuration references `AiCalibration`, `AiPromptDefinition`, `AiModelDefinition`, `AiFieldGenerationConfig`, `AiFactDefinition`, and `AiFactCounter`.
- Uses Java collections and regex APIs, Lombok `@ToString`, and JSpecify nullable annotations.

#### Cross-cutting
- `AiGenerationConfig` and `SrcMorphConfiguration` are mutable, unsynchronized JavaBeans; neither performs general configuration validation.
- `AiConditionEvaluator` and `AiFactExtractor` are stateless; condition validation reports malformed rules with `IllegalArgumentException`.
- `AiGenerationConfig` provides defaults and unmodifiable list views, but retains setter-supplied list references; `AiConditionGroup` defensively copies its child collection.
- `AiOversizeStrategy` defaults missing or blank tokens to `FAIL`; unknown nonblank tokens are rejected.
