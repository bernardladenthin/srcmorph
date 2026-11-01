### java/config
- H: 1.0
- C: A55858BE
- D: 2026-11-01T01:13:43Z
- T: 2026-11-01T01:16:34Z
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
> Configures source-file selection, AI generation and routing, code-fact extraction, and oversized-file handling for `.ai.md` documentation.

#### Purpose
- Defines the settings and helper logic used to select source files and configure AI-backed documentation generation.
- Validates file-condition and fact-counter definitions, and provides an explicit policy for files exceeding model context limits.

#### Responsibilities
- **File selection:** `AiConditionEvaluator` validates and evaluates nested metadata conditions; `AiConditionGroup` stores Maven-bound AND/OR child conditions.
- **Generation and run configuration:** `AiGenerationConfig` holds per-model inference, prompt, provider, hardware, and sampling settings; `SrcMorphConfiguration` collects project-wide selection, routing, model, output, and metadata settings.
- **Facts and oversized inputs:** `AiFactExtractor` counts configured regex matches in source text and formats a facts block; `AiOversizeStrategy` parses policies for oversized files.

#### Key units
- `AiConditionEvaluator` — validates condition trees, matches `AiFileContext` metadata, and detects line-count requirements.
- `AiConditionGroup` — holds a defensive copy of child `AiCondition` items for Maven `<and>` and `<or>` configuration.
- `AiFactExtractor` — validates regex counters and formats exact whole-source match counts.
- `AiGenerationConfig` — mutable per-model configuration with defaults for inference, prompt sizing, provider routing, and generation controls.
- `AiOversizeStrategy` — defines `FAIL`, `SAMPLE`, `MAP_REDUCE`, and `DETERMINISTIC` handling, defaulting to `FAIL`.
- `SrcMorphConfiguration` — mutable run-level configuration for source selection, AI definitions, output, fallback llama.cpp settings, and version metadata.

#### Data flow
- `SrcMorphConfiguration` carries source-selection settings and AI prompt, model, field-generation, and fact definitions into the broader run.
- `AiConditionEvaluator` evaluates validated `AiCondition` trees against `AiFileContext`; path-glob checks delegate to `AiSourceExcludeFilter`.
- `AiFactExtractor` applies configured `AiFactCounter` regexes to full source text and returns a formatted facts block.
- `AiOversizeStrategy.fromConfig(...)` converts configured policy text to a strategy; `AiGenerationConfig` supplies model-generation settings. These configuration classes do not themselves perform inference or file I/O.

#### Dependencies
- Condition handling uses `AiCondition`, `AiConditionGroup`, `AiRangeCondition`, `AiFileContext`, and `AiSourceExcludeFilter`.
- Fact extraction uses `AiFactCounter` and Java regex APIs.
- Run configuration references `AiPromptDefinition`, `AiModelDefinition`, `AiFieldGenerationConfig`, `AiFactDefinition`, `AiGenerationConfig`, and `AiCalibration`.
- Uses Java collections, `Instant`, `File`, Lombok `ToString`, and JSpecify `Nullable`.

#### Cross-cutting
- Most configuration types are mutable JavaBeans with no synchronization or thread-safety guarantees; they generally store settings rather than execute processing.
- Validation is concentrated in `AiConditionEvaluator` and `AiFactExtractor`; invalid conditions, ranges, timestamps, globs, labels, patterns, or regexes produce `IllegalArgumentException`.
- Nullable inputs are supported selectively; `AiGenerationConfig` normalizes selected null strings and lists, while `AiConditionGroup` defensively copies child collections and `AiGenerationConfig` exposes unmodifiable list views.
