### java/config
- H: 1.0
- C: 8DC39E9B
- D: 2026-10-29T09:50:36Z
- T: 2026-10-29T09:59:45Z
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
> Configuration and evaluation logic for AI-driven source code indexing, including file exclusion rules, inference parameter management, and deterministic fact extraction.

#### Purpose
- Centralizes all configuration parameters for the `srcmorph` AI code-indexing pipeline, supporting binding from Maven, JSON/YAML, or direct Java construction.
- Provides logic to evaluate file exclusion conditions and compute deterministic regex-based facts to supplement AI-generated summaries.

#### Responsibilities
- **Configuration Management**: Holds mutable JavaBean state for source scope (subtrees, excludes, extensions), AI provider routing, and llama.cpp inference tuning (`AiGenerationConfig`, `SrcMorphConfiguration`).
- **Condition Evaluation**: Recursively evaluates and validates AI file-exclusion condition trees (and/or/not, size, lines, dates, globs) against file contexts (`AiConditionEvaluator`, `AiConditionGroup`).
- **Fact Extraction**: Computes exact, model-free regex match counts for configured labels to generate deterministic "facts" blocks (`AiFactExtractor`).
- **Strategy Definition**: Defines handling strategies for oversized source files (`AiOversizeStrategy`).

#### Key units
- `SrcMorphConfiguration`: Central mutable JavaBean for the entire indexing run; binds source scope, provider settings, and llama.cpp parameters.
- `AiGenerationConfig`: Data carrier for 42+ llama.cpp/OpenAI-compatible inference parameters (sampling, KV cache, GPU offload, DRY, provider routing) with tuned defaults.
- `AiConditionEvaluator`: Stateless utility to `matches` condition trees against `AiFileContext`, `validate` structure, and check `usesLines`.
- `AiConditionGroup`: Wrapper for `List<AiCondition>` to support Maven XML binding for `<and>`/`<or>` combinators.
- `AiFactExtractor`: Static utility to generate formatted facts blocks from `AiFactCounter` regex definitions and source text.
- `AiOversizeStrategy`: Enum for oversized file handling (`FAIL`, `SAMPLE`, `MAP_REDUCE`, `DETERMINISTIC`) with case-insensitive config parsing.

#### Data flow
- `SrcMorphConfiguration` and `AiGenerationConfig` are populated via setters (Maven/Plexus or Jackson) and passed to the execution engine.
- `AiConditionEvaluator` consumes `AiCondition` trees (structured via `AiConditionGroup`) and `AiFileContext` records to determine file inclusion/exclusion.
- `AiFactExtractor` consumes `AiFactCounter` lists and raw source strings to produce deterministic count strings, which are prepended to AI outputs.
- `AiOversizeStrategy` is parsed from config strings to determine routing behavior for files exceeding context limits.

#### Dependencies
- **Internal**: `net.ladenthin.srcmorph.config.AiCondition`, `AiRangeCondition`, `AiFileContext`, `AiCalibration`, `AiFactCounter`, `AiPromptDefinition`, `AiModelDefinition`, `AiFieldGenerationConfig`, `AiFactDefinition`.
- **External**: `lombok.ToString`, `org.jspecify.annotations.Nullable`, `java.time.Instant`, `java.util.regex.Pattern`, `java.util.Collections`.
- **Referenced**: `net.ladenthin.llama.parameters.ModelParameters` (javadoc), `net.ladenthin.srcmorph.support.AiSourceExcludeFilter` (glob matching).

#### Cross-cutting
- **Null Safety**: Extensive use of `@Nullable` annotations and defensive null-checks in setters and evaluators.
- **Statelessness**: `AiConditionEvaluator` and `AiFactExtractor` are stateless/thread-safe; configuration classes are mutable JavaBeans intended to be read-only during execution.
- **Validation**: `AiConditionEvaluator.validate` and `AiFactExtractor.validate` throw `IllegalArgumentException` for misconfigured inputs (invalid regex, empty groups, unparseable dates).
- **Defaults**: `AiGenerationConfig` and `SrcMorphConfiguration` apply tuned `DEFAULT_*` constants at construction to ensure safe inference behavior.
