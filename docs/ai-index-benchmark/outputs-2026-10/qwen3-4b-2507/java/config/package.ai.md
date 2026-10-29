### java/config
- H: 1.0
- C: 7F3BDD4D
- D: 2026-10-29T08:10:04Z
- T: 2026-10-29T08:15:00Z
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
> This package provides the core configuration and evaluation logic for AI-driven source code analysis and generation, enabling conditional file matching, deterministic fact extraction, and intelligent handling of large files beyond model context limits.

#### Purpose
- Enables AI-powered source code transformation by evaluating file conditions and generating structured facts from source content  
- Configures AI generation parameters including context size, sampling, threading, and model-specific settings  
- Manages behavior when source files exceed model context limits through configurable oversize strategies  

#### Responsibilities
- Condition evaluation and file filtering via AI condition trees and groups  
- Deterministic fact extraction using regex-based, whole-file matching  
- AI generation configuration and parameter management  
- Oversize file handling strategies (trim, sample, map-reduce, deterministic output)  
- Centralized root configuration for source morphing workflows  

#### Key units
- **AiConditionEvaluator**: Evaluates file conditions against metadata and content to determine matches and detect line count needs  
- **AiConditionGroup**: Groups multiple AI conditions using AND/OR logic for complex configuration binding  
- **AiFactExtractor**: Generates deterministic, exact counts of code elements via regex matching across full source files  
- **AiGenerationConfig**: Central mutable configuration for AI model parameters including context, sampling, threading, and caching  
- **AiOversizeStrategy**: Defines how to handle files larger than model context (FAIL, SAMPLE, MAP_REDUCE, DETERMINISTIC)  
- **SrcMorphConfiguration**: Root configuration object for AI source transformation, managing paths, exclusions, AI providers, and generation rules  

#### Data flow
- File metadata (size, path, modification time) is evaluated by **AiConditionEvaluator** against condition trees (built from **AiConditionGroup**) to determine eligibility  
- Eligible files are passed to **AiFactExtractor** to compute exact regex-based fact counts from full source content  
- Generation parameters are pulled from **AiGenerationConfig** and applied via **SrcMorphConfiguration** to drive AI model behavior  
- If a file exceeds context size, **AiOversizeStrategy** determines the handling path (e.g., trim or sample) before processing  

#### Dependencies
- Internal: AiCondition, AiFileContext, AiRangeCondition, AiPromptDefinition, AiModelDefinition, AiFieldGenerationConfig, AiFactDefinition  
- External: java.util.List, java.io.File, java.time.Instant, java.util.regex.Pattern, java.util.regex.Matcher, lombok.ToString, org.jspecify.annotations.Nullable  

#### Cross-cutting
- Immutable and thread-safe design in core evaluators and extractors (no shared mutable state)  
- Defensive copying in collections (e.g., AiConditionGroup) to prevent external mutation  
- Comprehensive null handling via @Nullable annotations and graceful fallbacks  
- Configuration validation at build time (e.g., regex patterns, date parsing, condition structure)  
- All public APIs are stateless or provide immutable views; mutable state only via setters in configuration classes  
- Error handling focused on configuration validity, not runtime exceptions (e.g., invalid regex, malformed date)
