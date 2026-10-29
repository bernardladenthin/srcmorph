### java/config
- H: 1.0
- C: 2C867AC0
- D: 2026-10-29T13:48:14Z
- T: 2026-10-29T13:52:49Z
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
> This package manages the configuration, evaluation, and fact extraction necessary for AI-driven source code modification and analysis.

#### Purpose
* To centralize and manage all parameters governing an AI-driven source code modification run (`SrcMorphConfiguration`).
* To provide mechanisms for evaluating file context against complex AI-defined conditions (`AiConditionEvaluator`).

#### Responsibilities
* **Configuration Management:** Holding and providing access to all mutable parameters for AI generation, including model paths, context sizes, and sampling parameters (`SrcMorphConfiguration`, `AiGenerationConfig`).
* **Condition Logic:** Evaluating whether a file context meets a set of structured criteria defined by logical operators (`AiConditionEvaluator`, `AiConditionGroup`).
* **Fact Generation:** Computing structured data blocks from source text based on regex patterns and counters (`AiFactExtractor`).
* **Strategy Definition:** Determining how to handle source files that exceed model context limits (`AiOversizeStrategy`).

#### Key units
* `SrcMorphConfiguration`: Central mutable object holding all run parameters (e.g., `baseDirectory`, `generationProvider`, `llamaContextSize`).
* `AiGenerationConfig`: Encapsulates mutable parameters for a single AI generation step (e.g., `setModelPath`, `getTopP`).
* `AiConditionEvaluator`: Recursively traverses and validates `AiCondition` trees against a `AiFileContext`.
* `AiConditionGroup`: Manages collections of child `AiCondition` objects for logical grouping (AND/OR).
* `AiFactExtractor`: Computes a structured facts block from source text using configured `AiFactCounter`s.
* `AiOversizeStrategy`: Enum defining strategies for handling context overflow.

#### Data flow
Configuration parameters flow from `SrcMorphConfiguration` into specialized objects like `AiGenerationConfig` or are used to define constraints in `AiConditionGroup` structures. `AiConditionEvaluator` consumes an `AiFileContext` and an `AiCondition` tree to produce a boolean match result. `AiFactExtractor` takes a source string and a list of `AiFactCounter`s to output a facts block string.

#### Dependencies
* **Internal:** `AiCondition`, `AiFileContext`, `AiFactCounter`, `AiPromptDefinition`, `AiModelDefinition`, `AiFieldGenerationConfig`, `AiFactDefinition`.
* **External/System:** `java.io.File`, `java.time.Instant`, `net.ladenthin.srcmorph.support.AiSourceExcludeFilter`.

#### Cross-cutting
* **Configuration:** Heavy reliance on mutable configuration objects (`SrcMorphConfiguration`, `AiGenerationConfig`) which are JavaBeans pattern implementations.
* **Error Handling:** Specific exceptions are used for configuration validation: `IllegalArgumentException` (e.g., in `AiConditionEvaluator.validate` or `AiFactExtractor.validate`) and `IllegalStateException` (e.g., in `AiConditionEvaluator.matches`).
* **Immutability/Thread Safety:** `AiConditionEvaluator` is designed as a `final` class suggesting thread-safety for evaluation logic, while `SrcMorphConfiguration` is mutable and requires external synchronization.
