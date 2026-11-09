### java/config
- H: 1.0
- C: 6E389E7C
- D: 2026-11-04T07:35:21Z
- T: 2026-11-04T07:38:22Z
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
> Defines source-selection rules and AI generation settings, including file facts, model/provider options, and oversized-file handling for generated source summaries.

#### Purpose
- Centralizes configuration and supporting logic for selecting source files and preparing AI-generated `.ai.md` summaries.
- Provides reusable condition evaluation, regex-based fact extraction, and oversized-file strategy parsing.

#### Responsibilities
- **Source and generation configuration:** `SrcMorphConfiguration` stores discovery filters, routing definitions, llama.cpp fallback settings, and output metadata; `AiGenerationConfig` holds per-model inference, sampling, provider, prompt-limit, cache, hardware, and reasoning options.
- **File-condition rules:** `AiConditionGroup` represents combinator children; `AiConditionEvaluator` validates condition trees, matches file facts, and detects line-count requirements.
- **Summary facts and oversized inputs:** `AiFactExtractor` counts configured regex matches across source text and formats nonempty results; `AiOversizeStrategy` parses the configured oversized-file policy.

#### Key units
- `SrcMorphConfiguration` — mutable run-level bean for source discovery, output, routing, fallback, and generated-header settings.
- `AiGenerationConfig` — mutable per-model settings with defaults for inference and provider options.
- `AiConditionEvaluator` — stateless evaluator and validator for `AiCondition` trees against `AiFileContext`.
- `AiConditionGroup` — nullable, defensively copied list of child `AiCondition` entries.
- `AiFactExtractor` — utility for validating `AiFactCounter` patterns and formatting whole-source match counts.
- `AiOversizeStrategy` — enum for `FAIL`, `SAMPLE`, `MAP_REDUCE`, and `DETERMINISTIC`, with configuration-token parsing.

#### Data flow
- `AiConditionEvaluator` receives an `AiCondition` tree and `AiFileContext`, validates or evaluates the tree, and returns validity, a match result, or whether line counts are needed.
- `AiFactExtractor` receives counter definitions and complete source text, then returns a formatted facts block; empty or unusable counters produce an empty string.
- `AiOversizeStrategy.fromConfig` maps a configured token to a strategy, defaulting blank or null values to `FAIL`.
- `SrcMorphConfiguration` and `AiGenerationConfig` expose settings through JavaBean accessors; these classes do not themselves perform generation.

#### Dependencies
- Condition evaluation uses `AiCondition`, `AiConditionGroup`, `AiRangeCondition`, `AiFileContext`, and `AiSourceExcludeFilter`.
- Fact extraction uses `AiFactCounter`; run configuration references `AiPromptDefinition`, `AiModelDefinition`, `AiFieldGenerationConfig`, `AiFactDefinition`, and `AiGenerationConfig`.
- Configuration beans use Java collections, `File`, Lombok `@ToString`, and JSpecify `@Nullable`.

#### Cross-cutting
- Configuration classes are mutable JavaBeans without synchronization; `AiGenerationConfig` is explicitly not thread-safe.
- `AiGenerationConfig` normalizes several nullable settings to defaults and returns unmodifiable list views; `AiConditionGroup` copies non-null input collections.
- `AiConditionEvaluator` validates branch structure and rejects invalid ranges, empty groups, blank globs, and invalid timestamps; `AiFactExtractor.validate` rejects missing labels or patterns and invalid regexes.
- Defaults are explicit for generation settings and oversized-file handling; `SrcMorphConfiguration` includes fallback llama.cpp settings.
