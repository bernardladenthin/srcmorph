### java/config
- H: 1.0
- C: 6E389E7C
- D: 2026-11-04T07:35:21Z
- T: 2026-11-04T07:41:33Z
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
> Provides AI‑driven source‑code analysis and generation, including file‑selection, fact extraction, and configurable generation pipelines for the srcmorph tool.

#### Purpose
- Evaluate file‑selection rules for AI processing.
- Extract deterministic language‑agnostic facts from source text.
- Configure AI generation parameters and oversize handling.
- Store runtime configuration for srcmorph runs.

#### Responsibilities
- **Condition Evaluation** – `AiConditionEvaluator` parses and validates `AiCondition` trees, determining file matches and line‑count requirements.
- **Condition Grouping** – `AiConditionGroup` holds child conditions for combinators (`<and>`, `<or>`).
- **Fact Extraction** – `AiFactExtractor` counts regex matches to build a facts block for AI prompts.
- **Generation Configuration** – `AiGenerationConfig` exposes mutable settings for AI models, sampling, caching, and DRY logic.
- **Oversize Strategy** – `AiOversizeStrategy` defines how to route files that exceed the model’s context window.
- **Runtime Configuration** – `SrcMorphConfiguration` aggregates file paths, provider settings, prompt definitions, and model metadata for a srcmorph run.

#### Key units
- `AiConditionEvaluator` – evaluates and validates condition trees.
- `AiConditionGroup` – container for child `AiCondition` objects.
- `AiFactExtractor` – static utility for regex‑based fact counting.
- `AiGenerationConfig` – JavaBean holding AI generation parameters.
- `AiOversizeStrategy` – enum of oversize handling strategies (`FAIL`, `SAMPLE`, `MAP_REDUCE`, `DETERMINISTIC`).
- `SrcMorphConfiguration` – mutable configuration holder for srcmorph execution.

#### Data flow
1. **Configuration**: `SrcMorphConfiguration` supplies file selection rules (`AiConditionGroup`), prompt definitions, and AI model settings to the build pipeline.
2. **File Selection**: `AiConditionEvaluator` evaluates each file’s metadata (`AiFileContext`) against the condition tree to decide inclusion and whether line counts are needed.
3. **Fact Extraction**: For selected files, `AiFactExtractor` scans the source text with defined `AiFactCounter` patterns, producing a facts block.
4. **Generation**: `AiGenerationConfig` provides parameters to the AI provider; `AiOversizeStrategy` determines routing for oversized files.
5. **Output**: Results are written to the output directory defined in `SrcMorphConfiguration`.

#### Dependencies
- **Internal**: `AiSourceExcludeFilter`, `AiCondition`, `AiFactCounter`, `AiPromptDefinition`, `AiModelDefinition`, `AiFieldGenerationConfig`, `AiFactDefinition`.
- **External**: `java.time.Instant`, `java.util.regex`, `org.jspecify.annotations.Nullable`, Lombok (`@ToString`), `net.ladenthin.srcmorph.support`.
- **Configuration**: `AiGenerationConfig` and `SrcMorphConfiguration` are populated from Maven plugin parameters or external config files.

#### Cross-cutting
- Defensive copying in `AiConditionGroup.setConditions`.
- Stateless, thread‑safe utilities (`AiConditionEvaluator`, `AiFactExtractor`).
- Validation of configuration trees and regex patterns with clear exception messages.
- Default constants and null‑safe setters in configuration beans.
- Enum parsing with `fromConfig` in `AiOversizeStrategy` for robust configuration handling.
