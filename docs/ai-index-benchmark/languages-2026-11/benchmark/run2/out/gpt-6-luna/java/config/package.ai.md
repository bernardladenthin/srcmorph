### java/config
- H: 1.0
- C: A0F2D5E6
- D: 2026-11-01T01:35:52Z
- T: 2026-11-01T01:38:57Z
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
> Configures source-file selection, fact extraction, AI model routing, prompt generation, and oversized-file handling for AI-generated source documentation.

#### Purpose
- Defines the shared settings and rule types for selecting source files and producing `.ai.md` documentation.
- Supports condition-based file filtering, regex fact counts, per-generation provider and inference settings, and oversize strategies.

#### Responsibilities
- **Source and generation configuration:** `SrcMorphConfiguration` holds project paths, file-selection bounds and filters, prompt/model/fact definitions, field-routing rules, fallback llama.cpp settings, and generated-document metadata; `AiGenerationConfig` carries per-step model, prompt-limit, sampling, hardware, provider, and stopping options.
- **File-condition rules:** `AiConditionGroup` stores Maven-bindable condition children; `AiConditionEvaluator` validates and evaluates condition trees against file metadata and paths, including whether line counts are needed.
- **Fact counting:** `AiFactExtractor` validates regex-based fact counters and formats whole-source match counts into a facts block.
- **Oversize policy:** `AiOversizeStrategy` parses routing-rule choices for failing, sampling, map-reduce, or deterministic handling.

#### Key units
- `SrcMorphConfiguration` — shared mutable configuration for source scope, generation definitions, fallback inference, and output metadata.
- `AiGenerationConfig` — mutable settings for a single AI generation step, including provider routing, inference parameters, prompt limits, and stop conditions.
- `AiConditionEvaluator` — stateless condition-tree validator and file-context matcher.
- `AiConditionGroup` — nullable child-condition list for Maven `<and>` and `<or>` configuration.
- `AiFactExtractor` — utility for validating regex counters and formatting exact match counts.
- `AiOversizeStrategy` — enum and configuration-token parser; defaults to `FAIL`.

#### Data flow
- `SrcMorphConfiguration` supplies source-selection settings and prompt, model, field-generation, and fact definitions to consumers.
- `AiConditionEvaluator` validates configured condition trees, then matches them against `AiFileContext` metadata; `usesLines` identifies conditions that require line counts.
- `AiFactExtractor` validates fact-counter patterns, counts matches across source text, and formats nonempty results as a facts block.
- `AiGenerationConfig` provides generation settings to AI-provider consumers; `AiOversizeStrategy.fromConfig` maps an oversize setting to a strategy.

#### Dependencies
- Internal configuration types: `AiCondition`, `AiRangeCondition`, `AiFileContext`, `AiFactCounter`, `AiCalibration`, `AiPromptDefinition`, `AiModelDefinition`, `AiFieldGenerationConfig`, and `AiFactDefinition`.
- File-path filtering delegates path-glob matching to `AiSourceExcludeFilter`.
- Uses Java collections, regex, and `Instant` parsing; Lombok `@ToString` and JSpecify `@Nullable` annotations appear across configuration types.
- Generation settings reference llama.cpp inference concepts and `net.ladenthin.llama.parameters.ModelParameters`.

#### Cross-cutting
- Configuration beans are mutable and unsynchronized; no thread-safety guarantees are declared.
- Condition validation and fact-counter validation report invalid configuration with `IllegalArgumentException`; evaluator matching assumes conditions have been validated.
- Nullable inputs are supported selectively: condition groups may have no child list, fact-counter lists may be null, and generation settings include nullable thinking mode and calibration.
- `AiGenerationConfig` exposes unmodifiable list views, while `SrcMorphConfiguration` is a mutable settings holder.
