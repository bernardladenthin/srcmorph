### java/config
- H: 1.0
- C: ED38D5D7
- D: 2026-10-31T10:00:28Z
- T: 2026-10-31T10:04:45Z
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
> Evaluates source-file conditions, extracts deterministic facts, and carries mutable AI generation and run configuration for srcmorph processing.

#### Purpose
- Provides configuration types and validation for matching source files against AI condition trees and generating AI summaries.
- Centralizes mutable run parameters and per-generation settings used by the Maven plugin and AI provider implementations.

#### Responsibilities
- Condition tree evaluation and validation for file selection (`AiConditionEvaluator`, `AiConditionGroup`).
- Deterministic fact counting and facts-block assembly for AI input (`AiFactExtractor`).
- Mutable JavaBean configuration for a single AI generation step (`AiGenerationConfig`).
- Mutable JavaBean configuration for an entire srcmorph run (`SrcMorphConfiguration`).
- Oversize-file handling strategy parsing (`AiOversizeStrategy`).

#### Key units
- `AiConditionEvaluator` — recursively evaluates and/or/not condition trees against `AiFileContext` facts; validates tree configuration; detects line-count requirements.
- `AiConditionGroup` — nullable container for child `AiCondition` elements in and/or combinators, with defensive copy on set.
- `AiFactExtractor` — stateless utility that counts regex matches for fact labels and builds a labeled facts block string.
- `AiGenerationConfig` — mutable JavaBean holding model path, sampling, retry, trimming, and stop-string parameters for one AI generation step.
- `SrcMorphConfiguration` — central mutable JavaBean for all srcmorph run parameters, including output paths, filters, provider/model settings, prompts, fields, facts, and header version metadata.
- `AiOversizeStrategy` — enum parsing `<onOversize>` config tokens into `FAIL`, `SAMPLE`, `MAP_REDUCE`, or `DETERMINISTIC`.

#### Data flow
- `SrcMorphConfiguration` supplies run-level scope (subtrees, excludes, extensions, size bounds) and AI definitions to the engine.
- `AiConditionEvaluator.matches` consumes `AiCondition` trees and `AiFileContext` to decide whether a file is in scope.
- `AiConditionEvaluator.usesLines` signals whether the indexer must read file contents for line counting.
- `AiFactExtractor.factsBlock` consumes `List<AiFactCounter>` and source text to produce a deterministic facts block prepended to AI summaries.
- `AiGenerationConfig` carries per-step generation parameters from Maven configuration to AI provider implementations.
- `AiOversizeStrategy.fromConfig` converts the `<onOversize>` token into a strategy constant for oversized-file handling.

#### Dependencies
- Internal: `net.ladenthin.srcmorph.config.AiCondition`, `AiConditionGroup`, `AiFileContext`, `AiRangeCondition`, `AiFactCounter`, `AiCalibration`, `AiModelDefinition`, `AiFieldGenerationConfig`, `AiFactDefinition`, `net.ladenthin.srcmorph.prompt.AiPromptDefinition`, `net.ladenthin.srcmorph.support.AiSourceExcludeFilter`.
- External: `java.io.File`, `java.time.Instant`, `java.time.format.DateTimeParseException`, `java.util.ArrayList`, `java.util.Collection`, `java.util.Collections`, `java.util.List`, `java.util.regex.Matcher`, `java.util.regex.Pattern`, `java.util.regex.PatternSyntaxException`, `lombok.ToString`, `org.jspecify.annotations.Nullable`.

#### Cross-cutting
- Mutable JavaBeans (`AiGenerationConfig`, `SrcMorphConfiguration`) are not thread-safe and intended for single-threaded population before use.
- Stateless utilities (`AiConditionEvaluator`, `AiFactExtractor`) are thread-safe with no shared mutable state.
- `AiConditionGroup` and `AiGenerationConfig` defensively copy or null-coerce mutable collections in setters; list getters return unmodifiable views where applicable.
- `validate` methods fail fast with `IllegalArgumentException` on misconfigured condition trees, fact counters, or unrecognized oversize strategy tokens.
- Lombok `@ToString` and `@SuppressWarnings({"NullAway.Init", "initialization.fields.uninitialized"})` are used on configuration classes.
