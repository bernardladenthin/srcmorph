### java/config
- H: 1.0
- C: FDDD5FDA
- D: 2026-10-29T14:19:17Z
- T: 2026-10-29T14:44:56Z
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
> Provides configuration management, condition evaluation, and deterministic fact extraction for AI-driven source code summarization and generation within the srcmorph engine.

#### Purpose
- Aggregates and validates all parameters for AI model inference, file filtering, and generation strategies.
- Computes exact code metrics and evaluates file inclusion criteria to drive deterministic AI workflows.

#### Responsibilities
- **Configuration Aggregation**: Centralizes Maven plugin parameters, model paths, and inference settings in `SrcMorphConfiguration` and `AiGenerationConfig`.
- **Condition Evaluation**: Recursively matches file attributes (size, path, extensions) against logical trees in `AiConditionEvaluator`.
- **Metric Extraction**: Generates deterministic regex-based counts of code elements (functions, tables) for AI context augmentation.
- **Strategy Resolution**: Maps configuration strings to specific handling behaviors for oversized files via `AiOversizeStrategy`.
- **Data Binding**: Supports defensive copying and null-safe initialization for XML/JSON configuration binding.

#### Key units
- `SrcMorphConfiguration`: Central mutable JavaBean aggregating global project settings, model paths, and generation parameters.
- `AiGenerationConfig`: Mutable carrier for AI inference parameters including temperature, context size, GPU layers, and sampling strategies.
- `AiConditionEvaluator`: Recursive engine evaluating `AiCondition` trees against `AiFileContext` to determine file matches.
- `AiFactExtractor`: Static utility computing exact regex-based counts of code elements to prepend as facts blocks.
- `AiConditionGroup`: Container wrapper for lists of `AiCondition` instances supporting logical AND/OR combinators.
- `AiOversizeStrategy`: Enum defining fallback behaviors (FAIL, SAMPLE, MAP_REDUCE, DETERMINISTIC) for files exceeding context limits.
- `AiFileContext`: Data structure holding file metadata (name, size, lines, modification time) for condition evaluation.
- `AiFactCounter`: Configuration object defining labels and regex patterns for metric extraction.

#### Data flow
1.  **Configuration Ingestion**: Maven plugin or external source populates `SrcMorphConfiguration` and `AiGenerationConfig` with project paths, model definitions, and inference parameters.
2.  **File Filtering**: `AiConditionEvaluator` traverses `AiCondition` trees using `AiFileContext` data to validate file inclusion based on size, path globs, and extensions.
3.  **Metric Calculation**: For included files, `AiFactExtractor` applies `AiFactCounter` regex patterns to source text to generate deterministic fact strings.
4.  **Strategy Application**: If file size exceeds `AiGenerationConfig` context limits, `AiOversizeStrategy` determines whether to fail, sample, or map-reduce the input.
5.  **Inference Execution**: Finalized configuration and facts are passed to AI providers (e.g., `llamacpp-jni`) for generation or summarization.

#### Dependencies
- **Internal**: `AiCondition`, `AiFileContext`, `AiRangeCondition`, `AiSourceExcludeFilter`, `AiPromptDefinition`, `AiModelDefinition`, `AiFieldGenerationConfig`, `AiFactDefinition`.
- **External**: `java.time.Instant`, `java.util.regex`, `java.io.File`, `lombok.ToString`, `org.jspecify.annotations.Nullable`.
- **Infrastructure**: Maven Plexus (implied via configuration binding), Jackson/YAML (implied via JSON/YAML support).

#### Cross-cutting
- **Null Safety**: Extensive use of `@Nullable` annotations and defensive copying (e.g., `AiConditionGroup`) to prevent `NullPointerException` during configuration binding.
- **Validation**: `AiConditionEvaluator` and `AiFactExtractor` perform strict validation (e.g., regex syntax, non-empty lists) to fail fast on misconfiguration.
- **Thread Safety**: `AiFactExtractor` and `AiOversizeStrategy` are stateless and thread-safe; `SrcMorphConfiguration` and `AiGenerationConfig` are mutable and not thread-safe.
- **Immutable Views**: `AiGenerationConfig` returns unmodifiable list views for `drySequenceBreakers` and `stopStrings` to prevent external mutation.
