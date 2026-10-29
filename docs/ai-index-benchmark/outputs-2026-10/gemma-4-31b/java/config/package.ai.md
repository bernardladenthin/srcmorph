### java/config
- H: 1.0
- C: 693B5E2E
- D: 2026-10-28T21:24:22Z
- T: 2026-10-28T21:39:11Z
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
> Provides configuration management and evaluation logic for an AI-driven source code summarization engine, handling file filtering, inference parameters, and content extraction strategies.

#### Purpose
* Centralize configuration for the `srcmorph` engine and its AI provider integrations.
* Provide mechanisms to filter source files and manage AI model context constraints.

#### Responsibilities
* **Configuration Management**: Managing project-level settings, AI routing, and model-specific inference parameters via `SrcMorphConfiguration` and `AiGenerationConfig`.
* **File Filtering & Validation**: Evaluating complex conditional logic trees to determine file inclusion/exclusion using `AiConditionEvaluator` and `AiConditionGroup`.
* **Content Analysis**: Extracting deterministic regex-based counts (facts) from source text to augment AI summaries via `AiFactExtractor`.
* **Context Window Handling**: Defining strategies for processing files that exceed AI model limits via `AiOversizeStrategy`.

#### Key units
* `SrcMorphConfiguration`: Central DTO for project paths, AI routing, and model definitions.
* `AiGenerationConfig`: Data carrier for AI inference knobs such as `temperature`, `topP`, and `gpuLayers`.
* `AiConditionEvaluator`: Logic engine that matches `AiCondition` trees against `AiFileContext` metadata.
* `AiFactExtractor`: Utility for generating formatted blocks of regex-based pattern counts from source code.
* `AiOversizeStrategy`: Enum defining behaviors (`FAIL`, `SAMPLE`, `MAP_REDUCE`, `DETERMINISTIC`) for oversized files.
* `AiConditionGroup`: Container for `AiCondition` elements to facilitate Maven XML mapping.

#### Data flow
* **Configuration**: Maven/JSON/YAML $\rightarrow$ `SrcMorphConfiguration` $\rightarrow$ `AiGenerationConfig`.
* **Filtering**: `AiFileContext` $\rightarrow$ `AiConditionEvaluator` $\rightarrow$ Boolean match result.
* **Fact Extraction**: Source Text $\rightarrow$ `AiFactExtractor` $\rightarrow$ Formatted facts string.

#### Dependencies
* **Internal**: `AiPromptDefinition`, `AiModelDefinition`, `AiFieldGenerationConfig`, `AiFactDefinition`, `AiCalibration`, `AiFileContext`, `AiCondition`, `AiRangeCondition`, `AiSourceExcludeFilter`.
* **External**: `lombok.ToString`, `org.jspecify.annotations.Nullable`, `java.util.regex`, `java.time`.

#### Cross-cutting
* **Mutable DTOs**: `SrcMorphConfiguration` and `AiGenerationConfig` are implemented as mutable JavaBeans for population by the Maven plugin framework.
* **Explicit Validation**: `AiConditionEvaluator` and `AiFactExtractor` provide `validate()` methods to ensure configuration integrity (e.g., regex compilation, logic tree structure).
* **Sentinel Pattern**: `AiGenerationConfig` uses specific values (e.g., `-1`) to signal that the AI provider should use internal defaults.
