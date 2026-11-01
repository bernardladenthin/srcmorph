### java/config
- H: 1.0
- C: 6E389E7C
- D: 2026-11-01T14:58:32Z
- T: 2026-11-01T15:02:27Z
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
> Defines the configuration and rule utilities for selecting source files, routing AI generation, counting source facts, and controlling oversized-file handling for generated documentation.

#### Purpose
- Provides shared Maven-bindable settings for source scope, output metadata, model/provider routing, prompts, facts, and generation controls.
- Supplies validation and evaluation helpers for conditional file selection and deterministic regex-based source facts.

#### Responsibilities
- **Run and model configuration:** `SrcMorphConfiguration` stores source filters, output paths, prompt/model/fact definitions, fallback llama.cpp settings, versions, and project metadata; `AiGenerationConfig` holds detailed per-model sampling, token, hardware, cache, and API settings.
- **Conditional source selection:** `AiConditionGroup` represents Maven-bindable nested condition collections; `AiConditionEvaluator` validates and evaluates logical conditions against file metadata, paths, sizes, line counts, and modification times.
- **Fact extraction:** `AiFactExtractor` validates regex counters and formats exact whole-source match counts.
- **Oversize policy:** `AiOversizeStrategy` parses configured policies for failing, sampling, map-reduce, or deterministic handling of oversized files.

#### Key units
- `SrcMorphConfiguration` — shared mutable run configuration and defaults for source processing, generation routing, and documentation metadata.
- `AiGenerationConfig` — per-model configuration for generation limits, sampling, provider routing, inference hardware, and caching.
- `AiConditionEvaluator` — stateless condition-tree validator and matcher, including detection of line-count requirements.
- `AiConditionGroup` — nullable collection wrapper for child `AiCondition` entries in Maven configuration.
- `AiFactExtractor` — validates `AiFactCounter` definitions and formats regex match counts.
- `AiOversizeStrategy` — enum mapping configuration tokens to oversized-file policies; defaults to `FAIL`.

#### Data flow
- `SrcMorphConfiguration` supplies source filters and generation definitions; `AiGenerationConfig` carries model-specific settings to AI providers.
- `AiConditionEvaluator` validates a condition tree, detects whether line counts are needed, then matches it against an `AiFileContext`; glob checks delegate to `AiSourceExcludeFilter`.
- `AiFactExtractor` accepts configured `AiFactCounter` entries and source text, counts non-overlapping regex matches, and returns a formatted facts block.
- `AiOversizeStrategy.fromConfig` converts an `<onOversize>` token into a handling policy.

#### Dependencies
- Internal: `AiCondition`, `AiConditionGroup`, `AiRangeCondition`, `AiFileContext`, `AiSourceExcludeFilter`, `AiFactCounter`, `AiCalibration`, `AiPromptDefinition`, `AiModelDefinition`, `AiFieldGenerationConfig`, `AiFactDefinition`, and `AiGenerationConfig.DEFAULT_THREADS`.
- External: Java collections, regex, and time APIs; Lombok `@ToString`; JSpecify `@Nullable`; llama.cpp parameter concepts and OpenAI-compatible API settings.

#### Cross-cutting
- Configuration classes are mutable, unsynchronized JavaBeans; no thread-safety guarantees are stated.
- `AiConditionEvaluator`, `AiFactExtractor`, and `AiOversizeStrategy` are stateless utilities or value definitions.
- Validation is explicit for condition trees and fact-counter definitions; the configuration beans do not generally validate values or numeric ranges.
- Defaults are defined across generation and run configuration; nullable lists may be reset or represented as empty collections depending on the class.
