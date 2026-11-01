### java/config
- H: 1.0
- C: 6E389E7C
- D: 2026-11-01T14:41:21Z
- T: 2026-11-01T14:44:40Z
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
> Configures AI-assisted source indexing by defining file-selection rules, generation/model settings, reusable source facts, and oversized-file handling.

#### Purpose
- Provides Maven-bindable configuration and supporting evaluators for selecting source files and routing their AI-generated documentation.
- Defines generation defaults, condition validation, regex-based fact extraction, and oversized-file strategy tokens.

#### Responsibilities
- **Source selection:** `AiConditionEvaluator` validates and evaluates nested file conditions over extension, size, line count, modification time, and relative-path glob; `AiConditionGroup` holds `<and>`/`<or>` children.
- **Generation and run configuration:** `SrcMorphConfiguration` stores indexing filters, output metadata, model and prompt definitions, routing rules, and fallback llama.cpp settings; `AiGenerationConfig` carries provider, sampling, runtime, prompt-limit, and stop settings.
- **Facts and oversized inputs:** `AiFactExtractor` validates regex counters and formats source-wide counts; `AiOversizeStrategy` parses the configured behavior for oversized files.

#### Key units
- `SrcMorphConfiguration` — shared mutable configuration for source indexing and AI documentation generation.
- `AiGenerationConfig` — mutable provider and model-generation settings with defaults for context, output, sampling, and runtime resources.
- `AiConditionEvaluator` — validates condition trees, tests them against `AiFileContext`, and detects line-count requirements.
- `AiConditionGroup` — stores child `AiCondition` entries for Maven-bound combinator rules.
- `AiFactExtractor` — counts configured regex matches and formats a deterministic facts block.
- `AiOversizeStrategy` — enum of `FAIL`, `SAMPLE`, `MAP_REDUCE`, and `DETERMINISTIC`, with configuration parsing and `FAIL` default.

#### Data flow
- File facts in `AiFileContext` are tested against validated `AiCondition` trees; `usesLines` identifies when line-count data is required.
- Configured `AiFactCounter` patterns are matched against full source text and formatted as a facts block when reportable counters exist.
- Configuration objects hold source filters, prompts, models, field-generation and fact definitions, plus generation settings for consumers; `AiOversizeStrategy.fromConfig` maps the configured token to a strategy.

#### Dependencies
- Internal types: `AiCondition`, `AiConditionGroup`, `AiRangeCondition`, `AiFileContext`, `AiSourceExcludeFilter`, `AiFactCounter`, `AiCalibration`, `AiPromptDefinition`, `AiModelDefinition`, `AiFieldGenerationConfig`, `AiFactDefinition`, and `AiGenerationConfig`.
- Java utilities include collections, regex matching, `Instant`, and `File`; Lombok `@ToString` and JSpecify `@Nullable` annotate configuration types.

#### Cross-cutting
- Configuration classes are mutable JavaBeans; `AiGenerationConfig` and `SrcMorphConfiguration` are unsynchronized and not thread-safe.
- `AiConditionEvaluator` and `AiFactExtractor` are stateless; condition validation and fact-counter validation report invalid configuration with `IllegalArgumentException`.
- Nullable list settings are handled differently by type: `AiGenerationConfig` list setters reset null to empty lists, while `AiConditionGroup` preserves a null conditions list.
