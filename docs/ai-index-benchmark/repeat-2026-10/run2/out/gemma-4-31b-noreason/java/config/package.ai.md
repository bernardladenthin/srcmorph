### java/config
- H: 1.0
- C: 2C867AC0
- D: 2026-10-29T13:48:14Z
- T: 2026-10-29T13:58:47Z
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
> Provides configuration models and evaluation logic for AI-driven source code analysis, including file filtering, regex-based fact extraction, and LLM inference parameters.

#### Purpose
* Define the configuration schema for AI generation runs (via Maven/JSON/YAML).
* Implement logic to filter files and extract deterministic metadata for AI prompts.

#### Responsibilities
* **Configuration Management**: Maintains mutable state for engine parameters, AI provider settings, and file system boundaries.
* **File Filtering & Validation**: Evaluates recursive boolean logic trees to determine if files match specific criteria (size, date, path).
* **Deterministic Fact Extraction**: Uses regular expressions to count specific code elements (e.g., functions, tables) to supplement AI summaries.
* **Inference Strategy**: Defines how to handle files that exceed the AI model's context window.

#### Key units
* `SrcMorphConfiguration`: Root JavaBean containing all engine parameters, file boundaries, and AI definitions.
* `AiGenerationConfig`: Detailed settings for AI inference, including sampling (temperature, top-p), hardware offloading (GPU layers), and provider-specific API keys.
* `AiConditionEvaluator`: Logic engine that validates and evaluates `AiCondition` trees against `AiFileContext`.
* `AiFactExtractor`: Utility that generates a formatted "facts block" by applying `AiFactCounter` regexes to source text.
* `AiOversizeStrategy`: Enum defining behaviors for oversized files: `FAIL`, `SAMPLE`, `MAP_REDUCE`, or `DETERMINISTIC`.
* `AiConditionGroup`: Wrapper for `AiCondition` lists to facilitate Maven XML binding.

#### Data flow
1. **Configuration**: `SrcMorphConfiguration` and `AiGenerationConfig` are populated via Maven/JSON.
2. **Filtering**: `AiConditionEvaluator` processes `AiCondition` trees against `AiFileContext` to decide which files to index.
3. **Extraction**: `AiFactExtractor` scans source code using configured regexes to produce a deterministic summary block.
4. **Inference**: The system uses `AiGenerationConfig` and `AiOversizeStrategy` to route the source and facts to the AI provider.

#### Dependencies
* **Internal**: `AiPromptDefinition`, `AiModelDefinition`, `AiFieldGenerationConfig`, `AiFactDefinition`, `AiCalibration`, `AiSourceExcludeFilter`.
* **External**: `lombok` (ToString), `jspecify` (Nullable), `java.time` (ISO-8601 parsing).

#### Cross-cutting
* **Configuration Pattern**: Heavy use of mutable JavaBeans with getters/setters for framework compatibility (Maven).
* **Validation**: Explicit `validate()` methods in `AiConditionEvaluator` and `AiFactExtractor` to catch configuration errors (e.g., invalid regex or date formats) before execution.
* **Error Handling**: Use of `IllegalArgumentException` for misconfigured nodes or invalid enum tokens.
