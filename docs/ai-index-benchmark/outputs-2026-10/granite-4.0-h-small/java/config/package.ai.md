### java/config
- H: 1.0
- C: 016D1294
- D: 2026-10-29T04:50:45Z
- T: 2026-10-29T05:14:20Z
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
> A Maven plugin for AI-assisted code summarization and metadata extraction.

#### Purpose
- Generates AI-powered summaries and metadata for source code files.
- Configurable via Maven, JSON, or YAML.
- Supports multiple AI providers and models.

#### Responsibilities
- **Configuration Management**: Handles configuration parameters for the summarization process.
- **AI Integration**: Integrates with various AI providers and models for summarization.
- **File Processing**: Processes source code files to generate summaries and metadata.
- **Output Generation**: Produces `.ai.md` files containing the generated summaries and metadata.

#### Key Units
- **AiConditionEvaluator**: Evaluates and validates conditions for file matching.
- **AiConditionGroup**: Manages a list of `AiCondition` objects for logical combinators.
- **AiFactExtractor**: Computes deterministic facts from regex counters for AI summarization.
- **AiGenerationConfig**: Holds parameters for a single AI generation step.
- **AiOversizeStrategy**: Enum defining strategies for handling oversized source files.
- **SrcMorphConfiguration**: Root mutable JavaBean holding configuration parameters for a `srcmorph` run.

#### Data Flow
1. **Configuration**: The plugin reads configuration parameters from Maven, JSON, or YAML.
2. **Condition Evaluation**: `AiConditionEvaluator` evaluates conditions to determine file matching.
3. **Fact Extraction**: `AiFactExtractor` computes facts from source code files.
4. **AI Generation**: `AiGenerationConfig` holds parameters for AI generation steps.
5. **Oversize Handling**: `AiOversizeStrategy` defines strategies for handling oversized files.
6. **Output Generation**: The plugin generates `.ai.md` files with summaries and metadata.

#### Dependencies
- **Internal**: `AiCondition`, `AiFileContext`, `AiRangeCondition`, `AiSourceExcludeFilter`.
- **External**: Maven, JSON/YAML parsers, AI providers and models.

#### Cross-Cutting
- **Configuration**: Shared configuration parameters across files.
- **Error Handling**: Common exception handling for invalid configurations.
- **Concurrency**: Stateless methods ensuring thread safety.
