### java/config
- H: 1.0
- C: FF3CA8C1
- D: 2026-10-29T00:01:31Z
- T: 2026-10-29T00:05:22Z
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
> This package manages the configuration, validation, and execution logic for AI-driven source code generation and summarization, including model routing, file filtering, and context handling.

#### Purpose
*   Defines the configuration schema and validation rules for AI code generation runs, including model parameters, file inclusion/exclusion logic, and prompt definitions.
*   Provides utility classes for extracting file metadata, generating deterministic facts, and handling oversized source files within the AI generation pipeline.

#### Responsibilities
*   **Configuration Management**: Centralizes all parameters for AI generation runs (model paths, sampling settings, file filters, prompt definitions) via `SrcMorphConfiguration` and `AiGenerationConfig`.
*   **Condition Evaluation**: Validates and evaluates complex logical rules (`AiCondition`) to determine which source files should be included or excluded from processing.
*   **Fact Extraction**: Generates deterministic, language-agnostic metadata blocks containing exact regex match counts for source files.
*   **Oversize Handling**: Defines strategies for processing source files that exceed model context windows (abort, sample, map-reduce, deterministic).

#### Key units
*   `SrcMorphConfiguration`: Central mutable JavaBean holding all parameters for a srcmorph AI code generation run, unifying Maven Mojo fields, JSON/YAML configs, and direct instantiation.
*   `AiGenerationConfig`: Stores mutable configuration for a single AI generation step including model paths, sampling strategies, KV cache optimization, and hardware offloading.
*   `AiConditionEvaluator`: Evaluates and validates AI condition trees against file contexts to determine file inclusion/exclusion based on complex logical rules.
*   `AiFactExtractor`: Generates a deterministic, language-agnostic "facts" block containing exact regex match counts from a source file, prepended to AI summaries.
*   `AiOversizeStrategy`: Enumerates strategies for handling source files that exceed the context window of a routed AI model (FAIL, SAMPLE, MAP_REDUCE, DETERMINISTIC).
*   `AiConditionGroup`: Aggregates child AI conditions into a structured group for use as the body of AND/OR combinator nodes.

#### Data flow
*   Configuration parameters are bound into `SrcMorphConfiguration` and `AiGenerationConfig` objects, which serve as the single source of truth for the generation engine.
*   `AiConditionEvaluator` consumes `AiCondition` trees and `AiFileContext` facts to produce boolean match results, validating structural correctness first.
*   `AiFactExtractor` takes a list of `AiFactCounter` definitions and source text to produce a formatted facts block string.
*   `AiOversizeStrategy` selects a processing mode based on input configuration to handle files exceeding context limits.

#### Dependencies
*   **Internal**: `AiCondition`, `AiConditionGroup`, `AiFactCounter`, `AiPromptDefinition`, `AiModelDefinition`, `AiFieldGenerationConfig`, `AiFactDefinition`.
*   **External**: `java.util.regex` (Pattern, Matcher), `java.io.File`, `java.util.List`, `java.util.Collections`, `org.jspecify.annotations.Nullable`, `net.ladenthin.srcmorph.config.AiCalibration`, `net.ladenthin.srcmorph.prompt.AiPromptDefinition`.

#### Cross-cutting
*   **Validation**: `AiConditionEvaluator` enforces strict rules (exactly one branch/leaf, non-empty groups, bounded ranges, valid ISO-8601 dates, non-blank globs) before execution.
*   **Null Safety**: Extensive use of `org.jspecify.annotations.Nullable` for string and list fields to handle optional configuration values safely.
*   **Concurrency**: `AiConditionEvaluator` and `AiOversizeStrategy` are thread-safe (stateless/immutable), while `AiGenerationConfig` and `SrcMorphConfiguration` are mutable JavaBeans requiring external synchronization if accessed concurrently.
*   **Error Handling**: Specific exceptions (`IllegalArgumentException`, `DateTimeParseException`, `PatternSyntaxException`) are thrown for misconfigured nodes, invalid dates, or invalid regex patterns.
