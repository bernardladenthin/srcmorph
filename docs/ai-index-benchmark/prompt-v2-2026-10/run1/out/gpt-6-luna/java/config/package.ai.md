### java/config
- H: 1.0
- C: AB725D96
- D: 2026-10-31T14:30:30Z
- T: 2026-10-31T14:33:43Z
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
> Defines source selection, AI model routing and generation settings, file-condition filtering, factual count formatting, and oversized-file handling for source documentation runs.

#### Purpose
- Centralizes mutable `srcmorph` run configuration and reusable AI-generation settings.
- Provides condition evaluation, facts-block generation, and strategy selection for source-processing workflows.

#### Responsibilities
- **Run and model configuration:** `SrcMorphConfiguration` sets source scope, output and metadata, prompt and field-generation rules, model definitions, and fallback llama.cpp settings; `AiGenerationConfig` carries per-model provider, sampling, inference, hardware, caching, reasoning, and stopping options.
- **File selection:** `AiConditionEvaluator` validates and evaluates nested file conditions over extension, size, line count, modification time, and path; `AiConditionGroup` stores Maven-bindable AND/OR child conditions.
- **Fact reporting:** `AiFactExtractor` validates configured regex counters and formats whole-source match counts into a facts block.
- **Oversize handling:** `AiOversizeStrategy` parses configured choices for failing, sampling, map-reduce summarization, or deterministic output.

#### Key units
- `SrcMorphConfiguration` — shared mutable configuration for source scope, routing, outputs, and generated-document metadata.
- `AiGenerationConfig` — per-model generation and provider settings with defaults for inference and prompt trimming.
- `AiConditionEvaluator` — validates condition trees and matches them against `AiFileContext` facts.
- `AiConditionGroup` — nullable child-condition collection for Maven `<and>` and `<or>` binding.
- `AiFactExtractor` — counts configured regex matches and formats labeled results.
- `AiOversizeStrategy` — maps configuration tokens to oversized-file handling modes; defaults to `FAIL`.

#### Data flow
- `SrcMorphConfiguration` supplies source-selection, generation, model, and output settings; `AiGenerationConfig` carries the settings used for individual model routes.
- File facts in `AiFileContext` are checked against validated `AiCondition` trees; line-count needs can be detected with `usesLines`.
- Source text and configured `AiFactCounter` entries produce a formatted facts block.
- Oversize configuration tokens resolve to an `AiOversizeStrategy` for downstream handling.

#### Dependencies
- Internal configuration models: `AiModelDefinition`, `AiFieldGenerationConfig`, `AiFactDefinition`, `AiPromptDefinition`, `AiCalibration`, and condition types including `AiCondition`, `AiRangeCondition`, and `AiFileContext`.
- `AiConditionEvaluator` delegates path-glob matching to `AiSourceExcludeFilter`.
- Uses Java collections, regex, `Instant`, and `File`; annotations and generated representations use JSpecify `@Nullable` and Lombok `@ToString`.

#### Cross-cutting
- Configuration classes are mutable beans with no synchronization or thread-safety guarantees; evaluator and fact extractor are stateless.
- Validation is explicit for condition trees and fact counters; generation and run configuration beans generally store settings without numeric-range validation.
- Nullable lists and optional settings are common; `AiGenerationConfig` returns unmodifiable list views and resets null list inputs to empty lists.
