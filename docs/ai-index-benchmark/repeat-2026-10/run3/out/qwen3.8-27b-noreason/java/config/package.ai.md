### java/config
- H: 1.0
- C: FDDD5FDA
- D: 2026-10-29T14:19:17Z
- T: 2026-10-29T14:49:54Z
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
> Defines the configuration model, condition evaluation, and fact extraction logic for AI-assisted source code summarization and indexing in the srcmorph build pipeline.

#### Purpose
- Provides the data structures and validation logic for configuring AI source file inclusion, model inference parameters, and deterministic fact extraction.
- Bridges Maven plugin configuration (XML/JavaBean) to the underlying AI generation providers (llama.cpp, OpenAI-compatible).

#### Responsibilities
- **Condition Evaluation**: Recursively evaluates and validates `AiCondition` trees (and/or/not, extensions, size, lines, dates, globs) to filter source files.
- **Configuration Management**: Holds mutable JavaBean state for global run settings (`SrcMorphConfiguration`) and per-model inference parameters (`AiGenerationConfig`).
- **Fact Extraction**: Computes deterministic regex-based match counts from source text to generate exact "facts" blocks for AI prompts.
- **Strategy Routing**: Defines enum-based strategies for handling source files that exceed model context limits.

#### Key units
- `AiConditionEvaluator`: Stateless utility that matches `AiCondition` trees against `AiFileContext` metadata and validates configuration integrity.
- `AiConditionGroup`: Maven XML binding wrapper for `and`/`or` combinator bodies, holding a list of `AiCondition` children.
- `AiFactExtractor`: Static utility that generates deterministic label-count pairs from `AiFactCounter` regex definitions applied to source text.
- `AiGenerationConfig`: Mutable JavaBean carrying 40+ inference parameters (sampling, KV cache, GPU offload, provider routing) for a single AI generation step.
- `AiOversizeStrategy`: Enum defining handling strategies (`FAIL`, `SAMPLE`, `MAP_REDUCE`, `DETERMINISTIC`) for oversized files, with case-insensitive config parsing.
- `SrcMorphConfiguration`: Central mutable bean binding source filters, provider settings, prompt/model definitions, and llama.cpp fallback parameters for the entire run.

#### Data flow
- Maven plugin or JSON/YAML input populates `SrcMorphConfiguration` and `AiGenerationConfig` via JavaBean setters.
- `AiConditionEvaluator` consumes `AiCondition` trees (bound via `AiConditionGroup`) and `AiFileContext` records to filter source files.
- `AiFactExtractor` consumes `AiFactCounter` lists and raw source strings to produce deterministic facts blocks.
- `AiOversizeStrategy` parses configuration tokens to determine routing behavior for files exceeding context limits.
- Validated configurations are passed to AI provider implementations (llama.cpp JNI or OpenAI-compatible) for generation.

#### Dependencies
- **Internal**: `net.ladenthin.srcmorph.config.AiCondition`, `AiRangeCondition`, `AiFileContext`, `AiFactCounter`, `AiCalibration`, `AiPromptDefinition`, `AiModelDefinition`, `AiFieldGenerationConfig`, `AiFactDefinition`.
- **Support**: `net.ladenthin.srcmorph.support.AiSourceExcludeFilter` for glob matching.
- **External**: `lombok` (`@ToString`), `org.jspecify.annotations` (`@Nullable`), `java.time` (ISO-8601 parsing), `java.util.regex` (pattern matching).

#### Cross-cutting
- **Null Safety**: Extensive use of `@Nullable` annotations; setters often null-coalesce to defaults or handle null inputs gracefully.
- **Validation**: Fail-fast validation via `IllegalArgumentException` for invalid condition trees, regex patterns, or config tokens.
- **Immutability Patterns**: List getters return `Collections.unmodifiableList` views; utility classes (`AiConditionEvaluator`, `AiFactExtractor`) are stateless and thread-safe.
- **Maven Binding**: Classes are structured as mutable JavaBeans with defensive copying in setters to support Maven plugin XML binding and Jackson/YAML deserialization.
- **Concurrency**: Configuration beans are mutable and not thread-safe by design; intended to be populated before execution and treated as read-only during the run.
