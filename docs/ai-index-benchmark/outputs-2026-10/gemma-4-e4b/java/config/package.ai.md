### java/config
- H: 1.0
- C: B4A3877F
- D: 2026-10-28T23:55:11Z
- T: 2026-10-28T23:59:38Z
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
> This package manages the configuration, evaluation, and fact extraction necessary for AI-driven source code modification and indexing.

#### Purpose
* To centralize and manage all parameters required for an AI-driven source code modification process (SrcMorphConfiguration).
* To provide mechanisms for evaluating file contexts against complex AI-defined conditions (AiConditionEvaluator).

#### Responsibilities
* **Configuration Management:** Holding and providing access to all mutable parameters for AI generation, including model paths, context sizes, and sampling parameters (SrcMorphConfiguration, AiGenerationConfig).
* **Condition Logic:** Evaluating whether a file context satisfies a structured condition tree using logical operators (AiConditionEvaluator, AiConditionGroup).
* **Data Extraction:** Computing structured facts from source text by counting regex matches based on configured counters (AiFactExtractor).
* **Strategy Definition:** Defining how to handle source files that exceed the context window size (AiOversizeStrategy).

#### Key units
* SrcMorphConfiguration: Central mutable configuration object for the entire AI modification run.
* AiGenerationConfig: Container for mutable parameters specific to a single AI generation step (e.g., context size, temperature).
* AiConditionEvaluator: Class responsible for recursively matching an AiFileContext against an AiCondition tree.
* AiConditionGroup: Manages collections of child AiCondition objects for logical AND/OR combinations.
* AiFactExtractor: Computes a structured facts block by counting non-overlapping regex matches from source text.
* AiOversizeStrategy: Enum defining strategies for handling oversized source files.

#### Data flow
Configuration parameters flow from SrcMorphConfiguration into AiGenerationConfig and potentially into AiConditionEvaluator for context validation. File content is passed to AiFactExtractor for fact generation, and file metadata (AiFileContext) is passed to AiConditionEvaluator for matching against configured AiCondition trees.

#### Dependencies
* **Configuration:** Relies heavily on AiGenerationConfig, AiModelDefinition, AiPromptDefinition, AiFieldGenerationConfig, and AiFactDefinition for defining AI tasks.
* **Evaluation:** Depends on AiFileContext and AiCondition for context matching.
* **Extraction:** Depends on AiFactCounter for defining regex counting rules.
* **External:** Uses standard Java utilities (java.time, java.util) and Lombok annotations.

#### Cross-cutting
* **Mutability:** SrcMorphConfiguration and AiGenerationConfig are mutable JavaBeans, requiring external synchronization if shared across threads.
* **Configuration:** Configuration is highly centralized in SrcMorphConfiguration, which dictates parameters used by AiGenerationConfig.
* **Error Handling:** AiConditionEvaluator throws specific exceptions (IllegalArgumentException, IllegalStateException) for configuration validation failures.
* **Immutability/Safety:** AiFactExtractor and AiOversizeStrategy utilize immutable or inherently thread-safe structures.
