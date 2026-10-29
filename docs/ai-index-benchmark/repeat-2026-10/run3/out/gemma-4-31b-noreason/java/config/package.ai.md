### java/config
- H: 1.0
- C: FDDD5FDA
- D: 2026-10-29T14:19:17Z
- T: 2026-10-29T14:31:20Z
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
> Provides configuration schemas and evaluation logic for AI-driven source code analysis, including file filtering, regex-based fact extraction, and LLM inference parameter management.

#### Purpose
* Centralize AI generation settings and model parameters for binding from Maven, JSON, or YAML.
* Filter and validate source files based on complex boolean condition trees.
* Extract deterministic metadata (counts) from source code using regular expressions.

#### Responsibilities
* **Configuration Management**: Defines the root settings for the AI run and detailed inference parameters for providers like llama.cpp and OpenAI.
* **File Filtering & Validation**: Evaluates whether files meet specific criteria (size, date, glob patterns) to determine inclusion in the AI pipeline.
* **Deterministic Analysis**: Performs regex-based counting of code patterns to supplement AI summaries with exact "facts."
* **Oversize Handling**: Maps configuration tokens to strategies for processing files that exceed model context windows.

#### Key units
* `SrcMorphConfiguration`: Root JavaBean holding global paths, file constraints, and AI definition lists.
* `AiGenerationConfig`: Detailed inference settings including sampling (top-p, top-k), hardware offloading (GPU layers), and KV cache optimization.
* `AiConditionEvaluator`: Logic engine that recursively evaluates `AiCondition` trees against `AiFileContext`.
* `AiFactExtractor`: Utility that generates a formatted "facts" block by counting regex matches in source text.
* `AiOversizeStrategy`: Enum defining behaviors (FAIL, SAMPLE, MAP_REDUCE, DETERMINISTIC) for oversized files.
* `AiConditionGroup`: Wrapper for grouping `AiCondition` objects to support Maven XML binding.

#### Data flow
1. **Configuration**: `SrcMorphConfiguration` is populated via Maven/JSON/YAML.
2. **Filtering**: `AiConditionEvaluator` processes `AiCondition` trees and `AiFileContext` to filter the file set.
3. **Analysis**: `AiFactExtractor` scans the source text of selected files using `AiFactCounter` regexes to produce a facts summary.
4. **Inference Setup**: `AiGenerationConfig` provides the specific sampling and hardware parameters to the AI provider for the final generation step.

#### Dependencies
* **Internal**: `AiPromptDefinition`, `AiModelDefinition`, `AiFieldGenerationConfig`, `AiFactDefinition`, `AiCalibration`, `AiSourceExcludeFilter`.
* **External**: `java.time` (ISO-8601 parsing), `java.util.regex` (pattern matching), `lombok` (ToString).

#### Cross-cutting
* **Validation**: Extensive use of `IllegalArgumentException` across `AiConditionEvaluator` and `AiFactExtractor` to ensure configuration integrity at build-time.
* **Mutability**: Configuration objects (`SrcMorphConfiguration`, `AiGenerationConfig`) are implemented as mutable JavaBeans.
* **Recursive Processing**: Condition evaluation is handled via recursive tree walking of logical operators (AND, OR, NOT).
