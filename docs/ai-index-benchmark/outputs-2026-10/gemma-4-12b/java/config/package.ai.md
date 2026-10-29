### java/config
- H: 1.0
- C: 72D93C30
- D: 2026-10-29T03:15:31Z
- T: 2026-10-29T04:24:28Z
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
> This package provides a comprehensive configuration framework for an AI-driven source code analysis tool, managing file filtering, model parameters, and generation strategies.

#### Purpose
*   Provides a unified configuration schema for AI generation, model routing, and source code processing.
*   Facilitates the mapping of Maven-based configurations to internal engine parameters.

#### Responsibilities
*   **Condition Evaluation**: Logic for validating and matching complex nested condition trees (AND/OR/NOT) against file metadata (size, path, dates, extensions).
*   **AI Generation Parameters**: Management of inference settings including sampling (temperature, top-p, top-k), hardware acceleration (GPU layers, threads), and provider-specific options.
*   **Source Processing Strategies**: Definition of behaviors for handling oversized files (Fail, Sample, Map-Reduce, or Deterministic).
*   **Fact Extraction**: Generation of deterministic, regex-based code element counts (e.g., rows, tables) to be included in AI prompts.
*   **Configuration Aggregation**: Centralized storage for project paths, file exclusions, prompt definitions, and model routing rules.

#### Key units
*   `AiConditionEvaluator`: Recursively evaluates `AiCondition` trees against `AiFileContext` facts.
*   `AiGenerationConfig`: Mutable container for specific AI model parameters and inference settings.
*   `SrcMorphConfiguration`: The primary JavaBean aggregating all tool settings, including file filters and model definitions.
*   `AiFactExtractor`: Utility for generating formatted "facts" blocks based on regex pattern counts.
*   `AiOversizeStrategy`: Enum defining how to handle files exceeding model context windows.
*   `AiConditionGroup`: A wrapper for collections of `AiCondition` objects to support XML schema mapping.

#### Data flow
1.  `SrcMorphConfiguration` captures raw inputs from Maven/JSON/YAML.
2.  `AiConditionEvaluator` processes the filtering rules within the configuration to identify target files.
3.  `AiFactExtractor` processes the source text of selected files to generate metadata.
4.  `AiGenerationConfig` provides the final parameters used by the AI provider to generate the output.

#### Dependencies
*   `net.ladenthin.srcmorph.prompt.AiPromptDefinition`
*   `net.ladenthin.srcmorph.config.AiModelDefinition`
*   `net.ladenthin.srcmorph.config.AiFieldGenerationConfig`
*   `net.ladenthin.srcmorph.config.AiFactDefinition`
*   `net.ladenthin.srcmorph.support.AiSourceExcludeFilter`

#### Cross-cutting
*   **Validation**: Extensive use of `validate()` methods to ensure structural integrity of condition trees and regex patterns.
*   **Thread Safety**: Most configuration objects are mutable JavaBeans (not thread-safe), while evaluators and extractors are stateless and thread-safe.
*   **Null Handling**: Setters in configuration classes provide default values or handle nulls to ensure robust configuration binding.
