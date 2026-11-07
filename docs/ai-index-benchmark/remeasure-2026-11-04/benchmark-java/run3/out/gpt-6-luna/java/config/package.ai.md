### java/config
- H: 1.0
- C: 6E389E7C
- D: 2026-11-04T07:43:16Z
- T: 2026-11-04T07:46:13Z
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
> Defines source-selection rules and AI documentation-generation settings, including model routing, oversized-file handling, and exact source facts.

#### Purpose
- Configure which source files are eligible for indexing and how their AI-generated documentation is produced.
- Provide condition evaluation and regex-based fact extraction used to prepare source inputs.

#### Responsibilities
- **Source filtering:** Combine condition trees over file metadata, including extensions, size, line count, modification time, and relative-path globs; validate rules and identify when line counting is needed.
- **Generation configuration:** Store source scope, output paths, prompts, model and field-routing definitions, fallback llama.cpp settings, and generated-document metadata.
- **Prompt facts and size handling:** Validate regex fact counters and format exact whole-file counts; define policies for files exceeding model context limits.

#### Key units
- `AiConditionEvaluator` — validates and evaluates `AiCondition` trees against `AiFileContext`, and detects line-count requirements.
- `AiConditionGroup` — holds defensively copied child conditions for Maven-configured `and`/`or` groups.
- `AiFactExtractor` — validates `AiFactCounter` definitions and formats whole-source regex counts for prompts.
- `AiGenerationConfig` — stores model, sampling, reasoning, hardware, caching, provider, and stop-behavior settings for a generation step.
- `AiOversizeStrategy` — represents `FAIL`, `SAMPLE`, `MAP_REDUCE`, and `DETERMINISTIC` handling modes for oversized files.
- `SrcMorphConfiguration` — stores run-wide source selection, routing, fallback-model, output, and documentation metadata settings.

#### Data flow
- File metadata and an `AiCondition` tree enter `AiConditionEvaluator`; validation checks rule shape and bounds before matching, while `usesLines` indicates whether source contents must be read for line counts.
- Full source text and configured `AiFactCounter` entries enter `AiFactExtractor`; usable regexes produce exact counts formatted as a facts block.
- `SrcMorphConfiguration` supplies source scope and generation-routing definitions; `AiGenerationConfig` carries settings for a generation step, while `AiOversizeStrategy` expresses the configured policy for oversized inputs.

#### Dependencies
- Internal configuration and domain types: `AiCondition`, `AiRangeCondition`, `AiFileContext`, `AiSourceExcludeFilter`, `AiFactCounter`, `AiCalibration`, `AiPromptDefinition`, `AiModelDefinition`, `AiFieldGenerationConfig`, and `AiFactDefinition`.
- Java utilities include `Instant`, collections, regex APIs, and `File`; annotations and generated behavior use JSpecify `Nullable` and Lombok `ToString`.

#### Cross-cutting
- `AiGenerationConfig` and `SrcMorphConfiguration` are mutable JavaBeans without synchronization; evaluator and fact-extractor logic is stateless.
- Configuration validation is explicit for condition trees and fact counters; generation-setting setters generally do not validate ranges.
- Nullable configuration is used throughout; `AiOversizeStrategy.fromConfig` defaults blank or null values to `FAIL`, but rejects unknown nonblank tokens.
