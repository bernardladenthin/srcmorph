### java/config
- H: 1.0
- C: 4E0ADEFA
- D: 2026-10-29T14:01:55Z
- T: 2026-10-29T14:06:55Z
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
> This package manages the comprehensive configuration, evaluation, and data extraction logic required for AI-driven source code analysis and generation.

#### Purpose
* Centralizes all parameters governing the AI-driven source code morphing process via `SrcMorphConfiguration`.
* Provides mechanisms to evaluate file contexts against complex inclusion/exclusion criteria using `AiConditionEvaluator`.

#### Responsibilities
* Configuration Management: Holding and setting all parameters for AI generation, including model paths, context sizes, and sampling parameters (`SrcMorphConfiguration`, `AiGenerationConfig`).
* File Filtering and Context Evaluation: Determining if a file context meets specific criteria using condition trees (`AiConditionEvaluator`, `AiConditionGroup`).
* Data Extraction: Calculating structural metrics (facts) from source code using regular expressions (`AiFactExtractor`).
* Strategy Definition: Defining how to handle source files that exceed model context limits (`AiOversizeStrategy`).

#### Key units
* `SrcMorphConfiguration`: Central mutable object holding all run parameters (directories, filters, AI definitions).
* `AiConditionEvaluator`: Class responsible for recursively validating if an `AiFileContext` matches an `AiCondition` tree.
* `AiConditionGroup`: Manages collections of `AiCondition` objects for logical AND/OR grouping.
* `AiFactExtractor`: Computes structured "facts" from source text based on configured `AiFactCounter` regexes.
* `AiGenerationConfig`: Encapsulates mutable parameters for a single AI generation step (e.g., temperature, context size, GPU layers).
* `AiOversizeStrategy`: Enum defining strategies for handling oversized source files.

#### Data flow
Configuration flows from `SrcMorphConfiguration` into specialized objects like `AiGenerationConfig` and `AiConditionEvaluator`. File contexts (`AiFileContext`) are passed to `AiConditionEvaluator` for validation, and source text is passed to `AiFactExtractor` to generate structured data blocks.

#### Dependencies
* Internal: `AiCondition`, `AiFileContext`, `AiFactCounter`, `AiPromptDefinition`, `AiModelDefinition`, `AiFieldGenerationConfig`, `AiFactDefinition`.
* External: `java.time.Instant`, `net.ladenthin.srcmorph.support.AiSourceExcludeFilter`, `lombok.ToString`.

#### Cross-cutting
* Configuration: `SrcMorphConfiguration` acts as the primary configuration hub, referencing `AiGenerationConfig` for generation specifics.
* Error Handling: `AiConditionEvaluator` throws `IllegalArgumentException` for condition misconfigurations and `IllegalStateException` if required branches are missing.
* Mutability: `SrcMorphConfiguration` and `AiGenerationConfig` are mutable JavaBeans, requiring external synchronization for thread safety.
