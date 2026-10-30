### java/config
- H: 1.0
- C: 211308CB
- D: 2026-10-29T23:43:20Z
- T: 2026-10-29T23:47:49Z
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
> Maintains the AI-driven file-selection and generation configuration that decides which source files are eligible, how oversized inputs are handled, and what model and prompt settings are used for srcmorph indexing.

#### Purpose
- Centralizes config and validation for AI-backed source filtering, generation requests, and prompt facts.
- Provides deterministic policy and metadata utilities that can be used without I/O or model execution.

#### Responsibilities
- File-selection rules: `AiConditionEvaluator` validates and evaluates `AiCondition` trees against `AiFileContext` metadata, including `extensions`, `size`, `lines`, timestamps, and glob matching.
- JavaBean configuration: `AiGenerationConfig` and `SrcMorphConfiguration` hold the mutable settings for a single generation step and an entire srcmorph run.
- Rule composition and binding: `AiConditionGroup` preserves nullable `and`/`or` child collections for Maven/XML-style configuration.
- Oversized-input policy: `AiOversizeStrategy` maps config tokens like `sample` and `mapReduce` to safe handling strategies.
- Facts summarization: `AiFactExtractor` builds compact `label: count` facts blocks from regex counters and source text.

#### Key units
- `AiConditionEvaluator` — validates tree structure and evaluates whether a file matches a condition, while detecting whether line counting is required.
- `AiConditionGroup` — nullable container for grouped `AiCondition` children, with defensive copying and binding-friendly semantics.
- `AiFactExtractor` — compiles regex counters and emits a deterministic facts block for AI prompts.
- `AiGenerationConfig` — mutable per-request generation settings for model, prompt, sampling, cache, batching, provider, and calibration.
- `AiOversizeStrategy` — enum-based policy for `FAIL`, `SAMPLE`, `MAP_REDUCE`, and `DETERMINISTIC` oversized-file handling.
- `SrcMorphConfiguration` — root mutable JavaBean for scanning scope, file filters, generation provider, model definitions, prompt definitions, and llama.cpp defaults.
- `AiCondition` / `AiRangeCondition` / `AiFileContext` — condition tree, range constraints, and file metadata types consumed by the evaluator.

#### Data flow
- Configuration assembly: caller-created setters or Maven/XML binding populate `SrcMorphConfiguration`; nested generation parameters flow into `AiGenerationConfig`.
- File filter evaluation: `AiCondition` trees are validated by `AiConditionEvaluator.validate(...)`, then tested by `matches(...)` against `AiFileContext` values such as `fileName()`, `sizeBytes()`, `lineCount()`, `lastModifiedEpochMilli()`, and `relativePath()`.
- Glob and time checks: `pathGlob` uses `AiSourceExcludeFilter`; ISO-8601 timestamps are parsed with `Instant.parse` and compared with `modifiedAfter` / `modifiedBefore`.
- Facts generation: configured `AiFactCounter` objects and the full source text are passed to `AiFactExtractor.factsBlock(...)`, which returns a compact facts summary string.
- Oversize handling: config input is normalized by `AiOversizeStrategy.fromConfig(...)` to a canonical enum strategy used for oversized-input processing.

#### Dependencies
- Internal collaborations:
  - `AiConditionEvaluator` depends on `AiCondition`, `AiConditionGroup`, `AiFileContext`, `AiRangeCondition`, and `AiSourceExcludeFilter`.
  - `SrcMorphConfiguration` references `AiGenerationConfig`, `AiPromptDefinition`, `AiModelDefinition`, `AiFieldGenerationConfig`, and `AiFactDefinition`.
  - `AiFactExtractor` consumes `AiFactCounter` entries and regex-based fact metadata.
- External modules / libraries:
  - `java.time.Instant`, `java.util.*`, `java.io.File`, `java.util.regex.*`
  - `org.jspecify.annotations.Nullable`
  - Lombok `@ToString`
  - Maven/Plexus-style JavaBean binding assumptions for config containers
  - `llama.cpp` style runtime settings surfaced in `SrcMorphConfiguration`

#### Cross-cutting
- Mutable JavaBean pattern: `AiGenerationConfig`, `SrcMorphConfiguration`, and `AiConditionGroup` are writable objects intended for configuration assembly; no synchronization or immutability is enforced.
- Null-handling and defaults: `AiConditionGroup` stores `null` for absent child lists; `AiOversizeStrategy.fromConfig(...)` returns `DEFAULT` for `null` or blank input; setters in config classes normalize nulls or default values.
- Defensive copying: `AiConditionGroup.setConditions(...)` copies the incoming collection before storing; config list getters expose unmodifiable views.
- Validation-first design: `AiConditionEvaluator.validate(...)` enforces structural correctness; `AiFactExtractor.validate(...)` compiles regexes and rejects invalid counters; `AiOversizeStrategy.fromConfig(...)` throws for unknown non-blank values.
- Stateless execution surfaces: `AiConditionEvaluator` and `AiFactExtractor` are effectively stateless; error handling is explicit via `IllegalArgumentException` / `IllegalStateException` rather than a shared exception framework.
- Diagnostics and logging support: Lombok `@ToString` is used on config and condition-group classes to expose readable object state.
