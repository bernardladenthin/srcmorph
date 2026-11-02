### java/config
- H: 1.0
- C: 6E389E7C
- D: 2026-11-01T23:56:56Z
- T: 2026-11-01T23:59:40Z
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
> Configures source-file selection, AI fact extraction, model generation and routing, and oversized-file handling for source-summary generation.

#### Purpose
- Defines the settings and rule evaluation used to select source files and produce AI-generated source summaries and metadata.
- Provides reusable configuration for generation providers, model routing, prompts, facts, output locations, and run behavior.

#### Responsibilities
- **Source selection:** `AiConditionEvaluator` validates and evaluates composable file conditions; `AiConditionGroup` holds `<and>` / `<or>` child conditions.
- **Facts and metadata:** `AiFactExtractor` validates regex fact counters and formats whole-source match counts for summary output.
- **Generation settings:** `AiGenerationConfig` holds per-model sampling, prompt limits, provider, hardware, cache, reasoning, and stopping options.
- **Run configuration:** `SrcMorphConfiguration` combines source filters, output settings, prompt/model/fact definitions, field routing, fallback llama.cpp settings, and version/project metadata.
- **Oversized files:** `AiOversizeStrategy` parses the configured behavior: fail, sample, map-reduce, or deterministic output.

#### Key units
- `AiConditionEvaluator` — matches `AiCondition` trees against file facts and validates their structure.
- `AiConditionGroup` — exposes configured child conditions for combinator binding.
- `AiFactExtractor` — counts regex matches across source text and formats a facts block.
- `AiGenerationConfig` — mutable per-model generation and provider settings with local-inference defaults.
- `AiOversizeStrategy` — enum mapping configuration tokens to oversized-file handling strategies.
- `SrcMorphConfiguration` — central run configuration for source indexing and `.ai.md` generation.

#### Data flow
- `SrcMorphConfiguration` supplies source scopes, exclusions, extensions, size bounds, and routing definitions; `AiConditionEvaluator` validates and applies conditions to file context.
- For selected source text, `AiFactExtractor` produces configured whole-source fact counts for inclusion in generated summaries.
- Generation consumers use `AiGenerationConfig` and run-level fallback settings; oversized inputs use the configured `AiOversizeStrategy`.
- `planOnly` allows a routing plan to be produced without model loading or generation.

#### Dependencies
- Condition evaluation uses `AiCondition`, `AiConditionGroup`, `AiRangeCondition`, `AiFileContext`, and `AiSourceExcludeFilter`.
- Run configuration references `AiPromptDefinition`, `AiModelDefinition`, `AiFieldGenerationConfig`, `AiFactDefinition`, `AiGenerationConfig`, and `AiCalibration`.
- Fact extraction uses Java regex APIs; configuration types use Java collections and `File`, Lombok `@ToString`, and JSpecify nullability annotations.

#### Cross-cutting
- Configuration is primarily mutable JavaBeans with no general validation or thread-safety guarantees; `AiConditionEvaluator` and `AiFactExtractor` are stateless.
- `AiConditionEvaluator.validate` and `AiFactExtractor.validate` reject invalid rule or regex configuration with `IllegalArgumentException`.
- `AiGenerationConfig` uses defaults and sentinel values for provider/inference options; list getters are unmodifiable views, while setters mutate configuration.
- `AiOversizeStrategy` defaults to `FAIL`; unknown nonblank tokens are rejected.
