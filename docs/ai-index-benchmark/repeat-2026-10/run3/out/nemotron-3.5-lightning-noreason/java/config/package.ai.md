### java/config
- H: 1.0
- C: FDDD5FDA
- D: 2026-10-29T14:19:17Z
- T: 2026-10-29T14:41:24Z
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
> This package provides a configurable framework for AI-assisted source modification, governing how AI models are selected, conditioned, and applied to source files during build-time processing.

#### Purpose
- Serves as the central configuration and orchestration layer for AI-driven source transformation, integrating condition evaluation, fact extraction, generation parameters, and oversize handling into a single build-time pipeline.

#### Responsibilities
- **Condition evaluation**: `AiConditionEvaluator` walks AI condition trees to determine file inclusion based on metadata, paths, and content.
- **Fact extraction**: `AiFactExtractor` computes deterministic whole-file count facts to prepend to AI-generated content.
- **Generation configuration**: `AiGenerationConfig` holds all sampling parameters and defaults for model inference.
- **Oversize strategy**: `AiOversizeStrategy` defines how files exceeding model context windows are handled.
- **Root configuration**: `SrcMorphConfiguration` aggregates all per-run parameters, bridging Maven plugin, JSON/YAML, and direct Java construction.

#### Key units
- `AiConditionEvaluator`: Recursive tree walker that evaluates `and`/`or`/`not` combinators, extensions, size, lines, and path globs against `AiFileContext`.
- `AiConditionGroup`: Defensive container for child `AiCondition` instances, used as the body of `<and>`/`<or>` combinators.
- `AiFactExtractor`: Utility that produces a `FACTS_HEADER`/`FACTS_SUFFIX` block from `AiFactCounter` configurations.
- `AiGenerationConfig`: Mutable JavaBean carrying model path, sampling parameters (temperature, top-p, top-k), retry policy, and trimming limits.
- `AiOversizeStrategy`: Enum with four strategies (`FAIL`, `SAMPLE`, `MAP_REDUCE`, `DETERMINISTIC`) for handling oversize files.
- `SrcMorphConfiguration`: Root mutable JavaBean with setters/getters mirroring Maven `@Parameter` names, holding all per-run parameters independent of binding source.

#### Data flow
1. `SrcMorphConfiguration` is populated via Maven plugin, JSON/YAML, or direct Java construction.
2. `AiConditionEvaluator.matches()` walks `AiCondition` trees using file facts (`fileName`, `sizeBytes`, `lineCount`, `lastModifiedEpochMilli`, `relativePath`) to produce a boolean inclusion decision.
3. `AiFactExtractor.factsBlock()` computes count facts from `AiFactCounter` patterns over source text, prepending `FACTS_HEADER`/`FACTS_SUFFIX`.
4. `AiGenerationConfig` supplies sampling parameters (temperature, top-p, top-k, threads, etc.) to AI provider implementations.
5. `AiOversizeStrategy` dictates the fallback behavior (`FAIL`, `SAMPLE`, `MAP_REDUCE`, `DETERMINISTIC`) when a file exceeds its routed model's context window.

#### Dependencies
- Internal: `net.ladenthin.srcmorph.config.AiCondition`, `AiFactCounter`, `AiPromptDefinition`, `AiModelDefinition`, `AiCalibration`, `AiSourceExcludeFilter`.
- External: `java.time.Instant`, `java.time.format.DateTimeParseException`, `java.util.Collections`, `java.util.List`, `java.util.regex.Pattern`, `org.jspecify.annotations.Nullable`, `lombok.ToString`, `net.ladenthin.srcmorph.support.AiSourceExcludeFilter`.

#### Cross-cutting
- **Null-safety**: `@Nullable` annotations pervade `getConditions`, `getModelDirectory`, `getLlamaModelPath`, `getProjectName`, and list-returning getters; `setConditions` and `setDrySequenceBreakers`/`setStopStrings` accept `@Nullable List<String>`, null resetting to empty.
- **Immutability**: `AiConditionEvaluator` and `AiOversizeStrategy` are effectively immutable/thread-safe; `AiGenerationConfig` and `SrcMorphConfiguration` are mutable JavaBeans with no thread-safety guarantees.
- **Defaults**: Static final defaults dot `AiGenerationConfig` (context size 32768, max output 128, temperature 0.15f, etc.) and `AiOversizeStrategy` (`DEFAULT` = `FAIL`); `SrcMorphConfiguration` constructor applies `DEFAULT_OUTPUT_DIRECTORY`, `DEFAULT_GENERATION_PROVIDER`, `DEFAULT_LLAMA_CONTEXT_SIZE`, etc.
- **Configuration mirroring**: `SrcMorphConfiguration` field names intentionally mirror Maven `@Parameter` names so JSON/YAML config keys read identically to existing plugin XML.
