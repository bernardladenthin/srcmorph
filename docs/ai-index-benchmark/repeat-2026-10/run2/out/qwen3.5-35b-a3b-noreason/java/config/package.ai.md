### java/config
- H: 1.0
- C: 2C867AC0
- D: 2026-10-29T13:48:14Z
- T: 2026-10-29T14:05:23Z
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
> Central configuration and evaluation engine for AI-driven source code summarization, managing file filtering, condition matching, generation parameters, and deterministic fact extraction.

#### Purpose
- Aggregates Maven plugin bindings and runtime settings to control AI model behavior, file selection criteria, and output formatting.
- Provides the structural backbone for evaluating file contexts against complex condition trees and extracting regex-based statistics.

#### Responsibilities
- **Condition Evaluation**: Recursively validates and matches `AiCondition` trees against file metadata (size, lines, paths) using logical operators (AND/OR/NOT).
- **Configuration Management**: Serves as a mutable JavaBean carrier for global project settings, fallback Llama.cpp parameters, and provider-specific options.
- **Fact Extraction**: Computes deterministic counts of structural elements (e.g., functions, rows) via regex patterns to prepend to AI summaries.
- **Strategy Handling**: Defines behaviors for processing files that exceed model context windows (fail, sample, map-reduce, deterministic).
- **Data Binding**: Exposes fields mirroring Maven `@Parameter` names to enable consistent configuration from XML, JSON, or YAML sources.

#### Key units
- `AiConditionEvaluator`: Evaluates `AiCondition` trees against `AiFileContext` to determine file matches and validates tree structure.
- `AiConditionGroup`: Container for child `AiCondition` instances enabling `<and>` and `<or>` logical combinators in configuration.
- `AiFactExtractor`: Static utility generating formatted facts blocks by counting regex matches in source text.
- `AiGenerationConfig`: Mutable JavaBean holding detailed AI sampling parameters, GPU/CPU offloading settings, and retry policies.
- `AiOversizeStrategy`: Enum defining handling modes (`FAIL`, `SAMPLE`, `MAP_REDUCE`, `DETERMINISTIC`) for oversized files.
- `SrcMorphConfiguration`: Central aggregator for file filters, prompt definitions, model definitions, and fallback Llama.cpp defaults.
- `AiFileContext`: Data object containing file name, size, line count, modification time, and path used during evaluation.
- `AiRangeCondition`: Component within condition trees defining bounds for size or line count comparisons.
- `AiSourceExcludeFilter`: Utility delegated for glob pattern matching on file paths.

#### Data flow
1.  **Initialization**: `SrcMorphConfiguration` aggregates user inputs (Maven params, files) and populates `AiGenerationConfig` with model/sampling details.
2.  **Filtering**: `AiConditionEvaluator` traverses the configured `AiCondition` tree, querying `AiFileContext` properties (via `matches`, `usesLines`).
3.  **Validation**: `validate` ensures condition trees are structurally sound before execution; `AiFactExtractor.validate` checks regex syntax.
4.  **Extraction**: If `usesLines` is true, content is read; `AiFactExtractor.factsBlock` computes counts using `AiFactCounter` patterns.
5.  **Routing**: Oversized files trigger `AiOversizeStrategy` logic; valid files proceed to generation with `AiGenerationConfig` parameters.

#### Dependencies
- **Internal**: `net.ladenthin.srcmorph.config.AiCondition`, `AiConditionGroup`, `AiRangeCondition`, `AiFileContext`, `AiFactCounter`, `AiPromptDefinition`, `AiModelDefinition`, `AiFieldGenerationConfig`, `AiFactDefinition`.
- **External Support**: `net.ladenthin.srcmorph.support.AiSourceExcludeFilter`.
- **Java Standard**: `java.util.regex`, `java.time`, `java.io.File`, `lombok.ToString`, `org.jspecify.annotations.Nullable`.

#### Cross-cutting
- **Null Safety**: Extensive use of `@Nullable` annotations; setters often reset to defaults if null inputs are provided.
- **Immutability Patterns**: `AiConditionGroup` uses defensive copying; `AiGenerationConfig` returns unmodifiable views for list fields.
- **Error Handling**: Validation methods throw `IllegalArgumentException` for structural violations (empty lists, invalid ranges, bad regex); `DateTimeParseException` wrapped internally.
- **Thread Safety**: `AiFactExtractor` is stateless and thread-safe; other classes are mutable JavaBeans not designed for concurrent access.
- **Tri-State Logic**: `AiGenerationConfig` implements tri-state flags for optional features (e.g., thinking mode, GPU layers, DRY penalty) using specific sentinel values (-1, null, 0).
