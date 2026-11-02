### java/config
- H: 1.0
- C: A55858BE
- D: 2026-11-01T01:13:43Z
- T: 2026-11-01T01:20:58Z
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
> A configuration‑driven framework for AI‑powered source‑code indexing, fact extraction, and generation, exposing a Maven‑style JavaBean API for runtime control.

#### Purpose
- Centralize AI‑generation and file‑selection settings for a source‑code morphing tool.
- Validate and evaluate complex file‑selection conditions.
- Compute deterministic fact counts from source files.

#### Responsibilities
- **Configuration Management** – `SrcMorphConfiguration` aggregates all runtime parameters, including AI provider settings, file filters, and output paths.
- **Condition Evaluation** – `AiConditionEvaluator` validates and evaluates `AiCondition` trees, determining whether a file matches user‑defined criteria.
- **Fact Extraction** – `AiFactExtractor` counts regex‑based facts in source files, producing a formatted facts block.
- **AI Generation Settings** – `AiGenerationConfig` holds mutable parameters for AI model invocation (sampling, caching, device, etc.).
- **Oversize Handling** – `AiOversizeStrategy` defines policies for files exceeding model context limits.
- **Condition Grouping** – `AiConditionGroup` represents `<and>`/`<or>` combinators in the condition DSL.

#### Key units
- `SrcMorphConfiguration` – mutable JavaBean for all srcmorph settings.
- `AiConditionEvaluator` – stateless validator/evaluator for `AiCondition` trees.
- `AiFactExtractor` – static utility for regex‑based fact counting.
- `AiGenerationConfig` – JavaBean for AI provider configuration.
- `AiOversizeStrategy` – enum of oversize handling policies.
- `AiConditionGroup` – container for child `AiCondition` nodes.

#### Data flow
1. **Configuration**: `SrcMorphConfiguration` is populated via Maven, JSON/YAML, or code.
2. **Routing**: `AiConditionEvaluator` receives `AiCondition` trees from the configuration and evaluates each file’s `AiFileContext` to decide inclusion.
3. **Fact Counting**: For included files, `AiFactExtractor.factsBlock` is called with a list of `AiFactCounter` objects to produce a facts block.
4. **AI Generation**: `AiGenerationConfig` supplies parameters to the AI provider when generating summaries or prompts.
5. **Oversize Policy**: `AiOversizeStrategy` is consulted during routing to handle files that exceed context limits.

#### Dependencies
- **Internal**: `AiCondition`, `AiConditionGroup`, `AiRangeCondition`, `AiFileContext`, `AiFactCounter`, `AiPromptDefinition`, `AiModelDefinition`, `AiFieldGenerationConfig`, `AiFactDefinition`, `AiCalibration`.
- **External**: `java.time`, `java.util.regex`, `org.jspecify.annotations`, Lombok (`@ToString`), `net.ladenthin.srcmorph.support.AiSourceExcludeFilter`.

#### Cross‑cutting
- **Statelessness**: `AiConditionEvaluator` and `AiFactExtractor` are thread‑safe, using only local state.
- **Defensive Copying**: `AiConditionGroup.setConditions` copies input collections to prevent external mutation.
- **Immutable Enums**: `AiOversizeStrategy` provides a thread‑safe, case‑insensitive parser.
- **Configuration Defaults**: `AiGenerationConfig` defines standard defaults and normalizes `null` inputs to defaults.
- **Error Handling**: Validation methods throw `IllegalArgumentException` for misconfigurations; parsing errors are wrapped with clear messages.
- **Lombok**: `@ToString` is used across beans for concise debugging output.
