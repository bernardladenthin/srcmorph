### java/config
- H: 1.0
- C: 8DC39E9B
- D: 2026-10-29T09:50:36Z
- T: 2026-10-29T09:54:49Z
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
> Provides configuration and evaluation logic for AI-driven source code analysis, including file filtering, LLM parameter management, and deterministic metric extraction.

#### Purpose
* Centralize configuration for AI generation providers and model tuning.
* Filter source files based on complex recursive conditions.
* Extract deterministic "facts" from code via regex to supplement AI summaries.

#### Responsibilities
* **Configuration Management**: Aggregates global engine settings, AI provider parameters, and hardware-specific tuning (e.g., llama.cpp GPU layers).
* **File Context Evaluation**: Validates and matches files against logical condition trees (AND/OR/NOT) based on metadata like size, extension, and path.
* **Metric Extraction**: Performs regex-based counting of source code elements to provide precise quantitative data.
* **Oversize Handling**: Defines strategies for processing files that exceed the AI model's context window.

#### Key units
* `SrcMorphConfiguration`: Root JavaBean aggregating all engine settings, including file paths and AI definitions.
* `AiGenerationConfig`: Detailed parameters for AI inference, covering sampling (top-p, top-k), KV cache optimization, and hardware offloading.
* `AiConditionEvaluator`: Logic engine that determines if an `AiFileContext` satisfies an `AiCondition` tree.
* `AiFactExtractor`: Utility that generates formatted "facts" blocks by applying `AiFactCounter` regexes to source text.
* `AiOversizeStrategy`: Enum defining behaviors (`FAIL`, `SAMPLE`, `MAP_REDUCE`, `DETERMINISTIC`) for files exceeding context limits.
* `AiConditionGroup`: Wrapper for `AiCondition` lists to facilitate Maven XML binding.

#### Data flow
1. **Initialization**: `SrcMorphConfiguration` is populated via Maven/JSON.
2. **Filtering**: `AiConditionEvaluator` processes `AiFileContext` against `AiCondition` trees to decide if a file should be analyzed.
3. **Analysis**:
    * `AiFactExtractor` scans source code for regex matches to produce a deterministic facts string.
    * `AiGenerationConfig` provides the necessary hyperparameters to the AI provider.
    * If a file is too large, `AiOversizeStrategy` determines the truncation or splitting method.

#### Dependencies
* **Internal**: `AiPromptDefinition`, `AiModelDefinition`, `AiFieldGenerationConfig`, `AiFactDefinition`, `AiCalibration`, `AiSourceExcludeFilter`.
* **External**: `java.util.regex` (for fact extraction), `java.time` (for date-based filtering), `lombok` (for boilerplate), `org.jspecify.annotations`.

#### Cross-cutting
* **Validation**: Both `AiConditionEvaluator` and `AiFactExtractor` implement `validate()` methods to ensure configuration integrity (e.g., valid regexes, non-empty groups) before execution.
* **Configuration Pattern**: Heavy use of mutable JavaBeans for binding external configuration to internal logic.
* **Error Handling**: Use of `IllegalArgumentException` for misconfigured AI parameters or invalid condition trees.
