### java/config
- H: 1.0
- C: 2C867AC0
- D: 2026-10-29T13:48:14Z
- T: 2026-10-29T14:09:29Z
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
> Configures and validates the parameters, exclusion rules, and generation settings for AI-driven source code summarization and indexing within the srcmorph build pipeline.

#### Purpose
- Defines the root configuration surface (`SrcMorphConfiguration`) and granular settings for AI model routing, sampling, and hardware acceleration.
- Provides logic to evaluate file exclusion criteria and extract deterministic code facts for AI context enrichment.

#### Responsibilities
- **Root Configuration**: `SrcMorphConfiguration` aggregates source filters, output paths, provider selection, and model definitions for the entire build run.
- **Generation Parameters**: `AiGenerationConfig` carries 40+ sampling, hardware, and provider-specific parameters (llama.cpp/OpenAI) for individual AI generation steps.
- **Exclusion Logic**: `AiConditionEvaluator` and `AiConditionGroup` implement a recursive tree structure to filter files based on size, lines, dates, extensions, and path globs.
- **Fact Extraction**: `AiFactExtractor` computes exact regex-based metrics (e.g., function counts) to prepend deterministic data to AI prompts.
- **Oversize Handling**: `AiOversizeStrategy` defines enum-based strategies (`FAIL`, `SAMPLE`, `MAP_REDUCE`, `DETERMINISTIC`) for files exceeding context limits.

#### Key units
- `SrcMorphConfiguration`: Root JavaBean holding base directory, filters, provider name, and lists of `AiModelDefinition`/`AiPromptDefinition`.
- `AiGenerationConfig`: Mutable data carrier for model path, context size, temperature, top-p/k, GPU layers, and provider routing details.
- `AiConditionEvaluator`: Stateless utility to `matches` file metadata against `AiCondition` trees and `validate` configuration integrity.
- `AiConditionGroup`: Wrapper for `List<AiCondition>` enabling Maven XML binding for `and`/`or` logical combinators.
- `AiFactExtractor`: Static utility to generate formatted "facts blocks" from `AiFactCounter` regex definitions.
- `AiOversizeStrategy`: Enum mapping config tokens to processing strategies for oversized files.

#### Data flow
1.  **Configuration Binding**: Maven plugin or CLI populates `SrcMorphConfiguration` with global settings and nested `AiGenerationConfig` objects.
2.  **Filtering**: `AiConditionEvaluator` uses `AiConditionGroup` trees to filter source files, determining if line counts are needed (`usesLines`) before processing.
3.  **Fact Enrichment**: `AiFactExtractor` processes source text using `AiFactCounter` definitions to produce a deterministic header string.
4.  **Generation**: `AiGenerationConfig` parameters are passed to inference providers (llama.cpp JNI or OpenAI-compatible HTTP) to execute the AI summarization step.

#### Dependencies
- **Internal**: `net.ladenthin.srcmorph.support.AiSourceExcludeFilter` (for glob matching), `net.ladenthin.srcmorph.prompt.AiPromptDefinition`, `net.ladenthin.srcmorph.config.AiModelDefinition`.
- **External**: `lombok` (for `@ToString`), `org.jspecify` (for `@Nullable` annotations), `java.time` (for ISO-8601 parsing), `java.util.regex` (for fact counting).

#### Cross-cutting
- **Null Safety**: Extensive use of `@Nullable` annotations and defensive null-checks in setters (e.g., `AiGenerationConfig` null-guards to defaults).
- **Immutability Patterns**: `AiOversizeStrategy` and `AiConditionEvaluator` are stateless/immutable; `AiGenerationConfig` returns unmodifiable list views for collection fields.
- **Validation**: Fail-fast validation in `AiConditionEvaluator.validate` and `AiFactExtractor.validate` throws `IllegalArgumentException` for misconfigured trees or invalid regexes.
- **Concurrency**: Configuration objects are mutable JavaBeans (not thread-safe) intended to be read-only after initialization; evaluation/extraction utilities are thread-safe.
