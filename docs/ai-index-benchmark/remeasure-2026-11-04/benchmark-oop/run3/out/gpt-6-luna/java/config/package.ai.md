### java/config
- H: 1.0
- C: 6E389E7C
- D: 2026-11-04T07:50:25Z
- T: 2026-11-04T07:53:09Z
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
> Configures source-file selection, AI generation and model routing, exact source facts, and oversized-file handling for generated `.ai.md` summaries.

#### Purpose
- Defines the settings and rules that govern which source files are processed and how AI summaries are generated.
- Supplies condition evaluation, regex-based fact extraction, model-generation parameters, and oversize strategies.

#### Responsibilities
- **Run configuration and routing:** `SrcMorphConfiguration` holds source scope, output location, prompt and model definitions, per-field routing, fact definitions, fallback llama.cpp settings, and generated-file metadata.
- **Generation parameters:** `AiGenerationConfig` carries defaults and overrides for input limits, sampling, provider selection, runtime tuning, stop strings, reasoning, and calibration.
- **File selection:** `AiConditionGroup` stores nested condition children; `AiConditionEvaluator` validates and evaluates condition trees against file facts.
- **Facts and oversize behavior:** `AiFactExtractor` validates regex counters and formats exact whole-source counts; `AiOversizeStrategy` selects failure, sampling, map-reduce, or deterministic handling.

#### Key units
- `SrcMorphConfiguration` — mutable run-level settings for source selection, AI routing, fallback models, and output metadata.
- `AiGenerationConfig` — mutable per-generation settings with defaults for provider, sampling, token limits, and inference runtime.
- `AiConditionEvaluator` — validates and evaluates nested file conditions, including extension, size, line count, modification time, and path glob.
- `AiConditionGroup` — Maven-bindable collection of child `AiCondition` items.
- `AiFactExtractor` — counts regex matches in source text and formats the results as a facts block.
- `AiOversizeStrategy` — enum and configuration parser for oversized-file handling (`FAIL`, `SAMPLE`, `MAP_REDUCE`, `DETERMINISTIC`).

#### Data flow
- `SrcMorphConfiguration` supplies file-selection and generation settings; `AiConditionEvaluator` checks condition trees against `AiFileContext` facts.
- Configured fact counters are validated and applied by `AiFactExtractor` to full source text, producing an optional facts block.
- Oversize routing tokens are parsed by `AiOversizeStrategy`; the selected strategy describes how oversized input is handled.
- Generation workflows consume `AiGenerationConfig` values; this package stores settings but does not itself execute generation.

#### Dependencies
- Internal configuration and domain types: `AiCondition`, `AiFileContext`, `AiRangeCondition`, `AiFactCounter`, `AiCalibration`, `AiModelDefinition`, `AiFieldGenerationConfig`, `AiFactDefinition`, and `AiPromptDefinition`.
- `AiConditionEvaluator` delegates path-glob exclusion checks to `AiSourceExcludeFilter`.
- Uses Java collections, regex, and time APIs; `SrcMorphConfiguration` also uses `java.io.File`.

#### Cross-cutting
- Configuration is primarily mutable JavaBeans with getters and setters; `AiConditionEvaluator`, `AiFactExtractor`, and `AiOversizeStrategy` provide stateless rule or utility behavior.
- Nullable settings represent omitted options; defaults and sentinel values are used for generation parameters and run configuration.
- Validation is explicit for condition trees and fact counters; generation and run configuration beans do not perform general validation.
- No synchronization is described; mutable configuration beans are not thread-safe by implementation.
