### java/config
- H: 1.0
- C: A46575FE
- D: 2026-10-29T13:54:46Z
- T: 2026-10-29T13:59:51Z
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
> This package manages the configuration, evaluation, and extraction of structured facts necessary for AI-driven source code analysis and generation.

#### Purpose
* Centralizes all parameters governing an AI-driven source code analysis and generation run via `SrcMorphConfiguration`.
* Provides mechanisms to validate and evaluate complex inclusion/exclusion criteria against file contexts using `AiConditionEvaluator`.

#### Responsibilities
* Configuration Management: Holding and managing all run parameters, including file filtering, AI provider settings, and generation rules (`SrcMorphConfiguration`, `AiGenerationConfig`).
* Condition Evaluation: Determining if a file context meets complex criteria defined by condition trees (`AiConditionEvaluator`, `AiConditionGroup`).
* Fact Extraction: Calculating deterministic structural counts from source text using configured regular expressions (`AiFactExtractor`).
* Strategy Definition: Defining how to handle source files that exceed the context window limit (`AiOversizeStrategy`).

#### Key units
* `SrcMorphConfiguration`: Central mutable object holding all run parameters (base directory, AI definitions, file filters).
* `AiGenerationConfig`: Encapsulates mutable parameters for a single AI generation step (model path, temperature, context size).
* `AiConditionEvaluator`: Recursively traverses `AiCondition` trees to match a `AiFileContext` against defined criteria.
* `AiConditionGroup`: Manages collections of `AiCondition` objects for logical grouping (AND/OR).
* `AiFactExtractor`: Computes structured "facts" from source text based on configured `AiFactCounter` patterns.
* `AiOversizeStrategy`: Enum defining strategies for handling context window overflow.

#### Data flow
Configuration flows from `SrcMorphConfiguration` into specialized objects like `AiGenerationConfig` and `AiConditionGroup`. File contexts (`AiFileContext`) are passed to `AiConditionEvaluator` for matching. If a match occurs, source text can be processed by `AiFactExtractor` using counters defined in the configuration.

#### Dependencies
* Internal: `AiCondition`, `AiFileContext`, `AiFactCounter`, `AiPromptDefinition`, `AiModelDefinition`, `AiFieldGenerationConfig`, `AiFactDefinition`.
* External: `java.time.Instant`, `lombok.ToString`, `net.ladenthin.srcmorph.support.AiSourceExcludeFilter`.

#### Cross-cutting
* Configuration: `SrcMorphConfiguration` acts as the primary configuration hub, referencing `AiGenerationConfig` for AI-specific settings.
* Error Handling: `AiConditionEvaluator` throws `IllegalStateException` for structural errors in condition trees, while `AiFactExtractor` throws `IllegalArgumentException` for invalid regex patterns.
* Immutability/Mutability: `AiConditionEvaluator` is final and relies on input parameters, while `SrcMorphConfiguration` and `AiGenerationConfig` are mutable JavaBeans.
