### java/config
- H: 1.0
- C: 93DADB01
- D: 2026-10-29T00:10:55Z
- T: 2026-10-29T00:24:42Z
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
> Provides AI-driven condition evaluation, fact extraction, and source code transformation capabilities for projects.

#### Purpose
- Evaluates AI conditions
- Extracts facts from code
- Transforms source code using AI

#### Responsibilities
- Condition evaluation
- Fact extraction
- Source code transformation

#### Key units
- `AiConditionEvaluator`: Evaluates and validates AI condition trees
- `AiFactExtractor`: Computes deterministic facts from AI fact counters
- `AiGenerationConfig`: Configures AI generation parameters
- `SrcMorphConfiguration`: Configures AI-driven source code transformations

#### Data flow
Conditions are evaluated against file contexts, facts are extracted from source text, and source code is transformed based on AI configurations.

#### Dependencies
- Core Java utilities
- AI-specific libraries (e.g., jSpecify for nullability annotations)
- Custom AI condition and fact counter classes

#### Cross-cutting
- Thread-safe immutable classes for condition evaluation and fact extraction
- Configurable AI generation parameters
- Extensible source code transformation strategies
