### java/config
- H: 1.0
- C: 6E389E7C
- D: 2026-11-04T07:27:53Z
- T: 2026-11-04T07:31:09Z
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
> Configures source-file selection, AI fact extraction, model generation, and oversized-file handling for producing project indexes.

#### Purpose
- Defines shared Maven-bindable settings for source discovery, AI routing and generation, and generated index metadata.
- Validates and evaluates file conditions, and formats configured source facts for AI summaries.

#### Responsibilities
- **Source selection:** `AiConditionEvaluator` validates and evaluates nested file conditions; `AiConditionGroup` stores Maven-bound `<and>` and `<or>` children.
- **Fact preparation:** `AiFactExtractor` validates regex counters and formats whole-source match counts into a facts block.
- **Generation and run configuration:** `AiGenerationConfig` holds provider, model, sampling, prompt-limit, hardware, cache, reasoning, and stop settings; `SrcMorphConfiguration` collects run-wide source filters, prompts, models, routing rules, fact definitions, llama.cpp fallbacks, and output metadata.
- **Oversize policy:** `AiOversizeStrategy` parses the configured handling choice: fail, sample, map-reduce, or deterministic output.

#### Key units
- `SrcMorphConfiguration` — shared mutable configuration for source indexing and generated `.ai.md` output.
- `AiGenerationConfig` — per-generation settings for local and OpenAI-compatible providers, including optional calibration.
- `AiConditionEvaluator` — condition-tree validation, file matching, and detection of line-count requirements.
- `AiConditionGroup` — nullable collection of child `AiCondition` entries for Maven combinators.
- `AiFactExtractor` — validation and formatting of regex-based source facts.
- `AiOversizeStrategy` — enum and config-token parser for oversized source handling (`FAIL`, `SAMPLE`, `MAP_REDUCE`, `DETERMINISTIC`).

#### Data flow
- Run configuration supplies source scope, extensions, excludes, size bounds, prompt/model definitions, field-generation rules, and reusable fact definitions.
- `AiConditionEvaluator` evaluates `AiCondition` trees against `AiFileContext` facts; path globs are delegated to `AiSourceExcludeFilter`.
- `AiFactExtractor` counts configured regex matches against full source text and produces a facts block.
- Generation settings and the selected `AiOversizeStrategy` configure model execution and handling of files beyond a routed model’s context window.

#### Dependencies
- Condition evaluation uses `AiCondition`, `AiConditionGroup`, `AiRangeCondition`, `AiFileContext`, and `AiSourceExcludeFilter`.
- Run and generation configuration refer to `AiPromptDefinition`, `AiModelDefinition`, `AiFieldGenerationConfig`, `AiFactDefinition`, and `AiCalibration`.
- Uses Java collections, regex, and time parsing; Lombok `@ToString` and JSpecify nullability annotations appear across configuration types.

#### Cross-cutting
- Most configuration types are mutable JavaBeans with no synchronization; `AiConditionEvaluator` and `AiFactExtractor` are stateless.
- Defaults and unset sentinels defer some generation choices to provider or native bindings; `SrcMorphConfiguration` supplies fallback llama.cpp settings.
- Nullable values represent optional configuration; `AiGenerationConfig` preserves tri-state chat-template thinking mode and exposes unmodifiable views for selected lists.
