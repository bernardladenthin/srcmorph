### java/config
- H: 1.0
- C: 6E389E7C
- D: 2026-11-04T12:04:37Z
- T: 2026-11-04T12:07:38Z
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
> Defines source-selection rules, code-fact extraction, and AI generation settings used to configure source analysis and oversized-file handling.

#### Purpose
- Provides JavaBean configuration for `srcmorph` source scope, AI providers and models, field-generation routing, and output metadata.
- Supplies condition evaluation, regex-based fact formatting, and strategy parsing for AI generation workflows.

#### Responsibilities
- **Source selection:** Validate and evaluate nested `AiCondition` rules against file extensions, size, line count, modification time, and relative-path globs; represent `<and>`/`<or>` child conditions with `AiConditionGroup`.
- **Generation configuration:** Store provider, model, sampling, inference hardware, prompt limits, and output controls in `AiGenerationConfig`; configure run-level source filters, routing definitions, fallback settings, and metadata in `SrcMorphConfiguration`.
- **Facts and oversized files:** Validate regex fact counters and format whole-source counts with `AiFactExtractor`; define and parse oversized-file strategies with `AiOversizeStrategy`.

#### Key units
- `SrcMorphConfiguration` — run-level JavaBean for source scope, generation routing, llama.cpp fallback parameters, and output metadata.
- `AiGenerationConfig` — per-model inference, sampling, provider, prompt, and output settings with defaults.
- `AiConditionEvaluator` — validates `AiCondition` trees and matches them against `AiFileContext` facts.
- `AiConditionGroup` — holds child `AiCondition` entries for Maven-bound combinators.
- `AiFactExtractor` — validates `AiFactCounter` regexes and formats their source-match counts.
- `AiOversizeStrategy` — defines `FAIL`, `SAMPLE`, `MAP_REDUCE`, and `DETERMINISTIC` configuration tokens.

#### Data flow
- `SrcMorphConfiguration` supplies source filters and generation definitions; `AiConditionEvaluator` applies condition trees to per-file facts.
- `AiFactExtractor` takes configured counters and source text, then returns a formatted facts block.
- `AiOversizeStrategy.fromConfig` maps a nullable configuration token to a strategy, defaulting to `FAIL`.

#### Dependencies
- Condition evaluation uses `AiCondition`, `AiConditionGroup`, `AiRangeCondition`, `AiFileContext`, and `AiSourceExcludeFilter`.
- Run configuration references `AiPromptDefinition`, `AiModelDefinition`, `AiFieldGenerationConfig`, `AiFactDefinition`, and `AiGenerationConfig.DEFAULT_THREADS`.
- Fact extraction uses `AiFactCounter` and Java regex APIs; time conditions use `Instant`.

#### Cross-cutting
- Configuration types are mutable, unsynchronized JavaBeans; `AiGenerationConfig` initializes inference defaults and exposes unmodifiable views of its list settings.
- `AiConditionEvaluator` and `AiFactExtractor` are stateless utilities; condition validation and fact-counter validation reject malformed configuration.
- Nullable settings and null normalization are explicit in configuration APIs; `AiOversizeStrategy.fromConfig` accepts null or blank values as the default.
