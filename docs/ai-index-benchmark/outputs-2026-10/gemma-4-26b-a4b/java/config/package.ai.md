### java/config
- H: 1.0
- C: A3C47B7D
- D: 2026-10-31T10:06:17Z
- T: 2026-10-31T14:00:43Z
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
> Provides a configuration and evaluation framework for managing AI-driven source code summarization, including file filtering, model inference parameters, and structural fact extraction.

#### Purpose
* Centralizes configuration for the srcmorph engine, bridging Maven XML, JSON/YAML, and Java implementations.
* Manages the logic for determining which files to process and how to handle large files relative to AI model context limits.

#### Responsibilities
* **Configuration Management**: Acts as a central repository for all engine settings, including output directories, file filters, and AI provider details via `SrcMorphConfiguration`.
* **Condition Evaluation**: Validates and executes hierarchical logical trees to match files against metadata like size, extension, or path via `AiConditionEvaluator`.
* **Inference Parameterization**: Stores complex sampling and hardware settings (e.g., temperature, GPU layers, DRY penalty) for AI providers in `AiGenerationConfig`.
* **Structural Fact Extraction**: Performs regex-based counting of code elements to provide deterministic data to supplement AI summaries via `AiFactExtractor`.
* **Oversize Handling**: Defines strategies (Fail, Sample, Map-Reduce, Deterministic) for processing files that exceed model context windows via `AiOversizeStrategy`.

#### Key units
* `SrcMorphConfiguration`: The primary mutable JavaBean holding all global engine and plugin parameters.
* `AiConditionEvaluator`: A stateless engine that evaluates `AiCondition` trees against `AiFileContext` metadata.
* `AiGenerationConfig`: A container for fine-grained AI inference parameters, including sampling (Top-P, Min-P) and hardware offloading.
* `AiFactExtractor`: A utility for generating deterministic text blocks of regex-based counts (e.g., function counts) to ground AI output.
* `AiConditionGroup`: A wrapper for collections of conditions to support Maven XML binding.
* `AiOversizeStrategy`: An enum defining fallback behaviors when source files exceed the AI context window.

#### Data flow
1. **Configuration Loading**: `SrcMorphConfiguration` is populated by external binders (Maven/Jackson).
2. **File Filtering**: `AiConditionEvaluator` uses the configuration's filters and `AiCondition` trees to identify target files.
3. **Fact Extraction**: For matched files, `AiFactExtractor` scans source content to generate deterministic metadata.
4. **AI Inference**: `AiGenerationConfig` provides the necessary sampling and model parameters to the AI provider to generate summaries, applying `AiOversizeStrategy` if the file is too large.

#### Dependencies
* **Internal**: `AiCondition`, `AiFileContext`, `AiFactCounter`, `AiPromptDefinition`, `AiModelDefinition`, `AiCalibration`.
* **External**: `java.time` (ISO-8601 parsing), `java.util.regex` (pattern matching), `lombok` (data modeling), `net.ladenthin.srcmorph.support.AiSourceExcludeFilter` (glob matching).

#### Cross-cutting
* **Statelessness**: `AiConditionEvaluator` and `AiFactExtractor` are designed as stateless, thread-safe utility classes.
* **Validation**: Extensive tree and regex validation is performed during configuration and evaluation to ensure structural integrity.
* **Error Handling**: Uses `IllegalArgumentException` for malformed configurations, invalid regex, or broken condition trees.
