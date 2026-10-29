### java/config
- H: 1.0
- C: B03472EC
- D: 2026-10-29T08:50:27Z
- T: 2026-10-29T08:57:42Z
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
> Provides configuration, validation, and execution logic for AI-driven source code analysis, file filtering, and generation parameter management within the srcmorph build tool.

#### Purpose
- Aggregates and validates configuration for AI model routing, file filtering, and generation parameters for the srcmorph Maven plugin.
- Evaluates file conditions against context to determine matches and generates deterministic statistics blocks from source code.

#### Responsibilities
- **Condition Evaluation**: Recursively evaluates logical condition trees (`and`, `or`, `not`) against file attributes (size, lines, dates, extensions) to determine file inclusion.
- **Configuration Management**: Serves as the central mutable JavaBean for passing AI generation parameters, model definitions, and file scope settings between the Maven layer and engine.
- **Fact Extraction**: Computes exact, whole-file regex-based statistics (label: count pairs) from source code for AI summaries.
- **Strategy Handling**: Defines and parses strategies for handling source files that exceed the model's context window (e.g., `MAP_REDUCE`, `FAIL`, `SAMPLE`).
- **Data Binding**: Provides JavaBean getters/setters for binding configuration from Maven XML, JSON, or YAML sources.

#### Key units
- `AiConditionEvaluator`: Evaluates `AiCondition` trees against `AiFileContext` to determine matches and validates configuration integrity.
- `AiConditionGroup`: Container for child `AiCondition` instances enabling `<and>` and `<or>` combinator elements in Maven configuration.
- `AiFactExtractor`: Utility class generating deterministic "facts" blocks by counting non-overlapping regex pattern matches in source code.
- `AiGenerationConfig`: Mutable JavaBean carrying AI generation parameters including model paths, sampling strategies, and hardware offloading settings.
- `AiOversizeStrategy`: Enum defining behaviors for files exceeding context windows (`FAIL`, `SAMPLE`, `MAP_REDUCE`, `DETERMINISTIC`).
- `SrcMorphConfiguration`: Central configuration bean aggregating file filtering rules, AI provider settings, and model definitions.
- `AiFileContext`: Context object holding file facts (name, size, line count, modification time) used for condition evaluation.
- `AiRangeCondition`: Component defining bounds for `size` and `lines` fields within condition trees.

#### Data flow
- **Configuration Ingestion**: `SrcMorphConfiguration` and `AiGenerationConfig` are populated via Maven plugin setters or external files, establishing defaults for models, prompts, and file filters.
- **File Filtering**: `AiConditionEvaluator` traverses `AiCondition` trees using `AiFileContext` data to determine if a file matches inclusion criteria or requires line counting.
- **Fact Generation**: `AiFactExtractor` processes source strings against `AiFactCounter` definitions to produce formatted statistics blocks.
- **Strategy Resolution**: `AiOversizeStrategy.fromConfig` parses configuration tokens to select handling logic for oversized files.
- **Execution**: Configuration objects are passed to the generation engine to drive AI model calls and source code morphing.

#### Dependencies
- **Internal**: `AiCondition`, `AiConditionGroup`, `AiFileContext`, `AiRangeCondition`, `AiFactCounter`, `AiCalibration`, `AiPromptDefinition`, `AiModelDefinition`, `AiFieldGenerationConfig`, `AiFactDefinition`.
- **External**: `net.ladenthin.srcmorph.support.AiSourceExcludeFilter` for path glob evaluation; `java.time` for date parsing; `java.util.regex` for pattern matching; `lombok` for boilerplate reduction; `org.jspecify.annotations` for null safety.

#### Cross-cutting
- **Null Safety**: Extensive use of `@Nullable` annotations and defensive copying (e.g., `AiConditionGroup.setConditions`) to handle optional configuration fields.
- **Validation**: `AiConditionEvaluator` and `AiFactExtractor` enforce strict configuration integrity, throwing `IllegalArgumentException` for missing branches, invalid regex, or malformed dates.
- **State Management**: Configuration classes (`AiGenerationConfig`, `SrcMorphConfiguration`) are mutable JavaBeans intended for single-threaded population, lacking thread-safety guarantees.
- **Error Handling**: Consistent wrapping of `DateTimeParseException` and `PatternSyntaxException` into `IllegalArgumentException` for uniform error reporting.
