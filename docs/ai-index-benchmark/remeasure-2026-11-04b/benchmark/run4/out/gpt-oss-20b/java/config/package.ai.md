### java/config
- H: 1.0
- C: 6E389E7C
- D: 2026-11-04T12:50:12Z
- T: 2026-11-04T12:52:03Z
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
> A configuration‑driven AI‑generation framework that routes source files, evaluates file‑matching rules, extracts deterministic language facts, and supplies all parameters for model execution.

#### Purpose
- Centralize AI generation settings for a Maven/CLI run.
- Provide file‑selection logic and fact extraction for source‑to‑AI pipelines.

#### Responsibilities
- **Configuration Management** – `SrcMorphConfiguration` aggregates all runtime options (directories, file filters, model specs, prompt templates, fact groups, Llama settings).
- **Condition Evaluation** – `AiConditionEvaluator` validates and evaluates logical file‑matching trees (`AiCondition`, `AiConditionGroup`).
- **Fact Extraction** – `AiFactExtractor` builds deterministic fact blocks from source text using regex counters.
- **Oversize Handling** – `AiOversizeStrategy` defines how to process files that exceed the model’s context window.
- **Generation Parameters** – `AiGenerationConfig` holds per‑run model and sampling options.

#### Key units
- `SrcMorphConfiguration` – mutable bean for Maven/CLI configuration.
- `AiConditionEvaluator` – stateless evaluator/validator for `AiCondition` trees.
- `AiConditionGroup` – container for child conditions in `<and>/<or>` combinators.
- `AiFactExtractor` – static utility to produce fact blocks from source.
- `AiOversizeStrategy` – enum of oversize strategies (`FAIL`, `SAMPLE`, `MAP_REDUCE`, `DETERMINISTIC`).
- `AiGenerationConfig` – JavaBean with 57 fields for model, sampling, caching, and provider settings.

#### Data flow
1. **Configuration**: `SrcMorphConfiguration` is populated via Maven/CLI or programmatically.
2. **File Selection**: `AiConditionEvaluator` receives an `AiCondition` tree and `AiFileContext` for each source file, returning a boolean match.
3. **Fact Generation**: For matched files, `AiFactExtractor.factsBlock` produces a deterministic facts block from the source text.
4. **Oversize Decision**: `AiOversizeStrategy.fromConfig` selects a strategy based on config; the routing logic (outside this package) applies it.
5. **Model Invocation**: `AiGenerationConfig` supplies all parameters to the chosen AI provider for prompt generation and response handling.

#### Dependencies
- **Internal**: `AiCondition`, `AiConditionGroup`, `AiFileContext`, `AiSourceExcludeFilter`, `AiFactCounter`, `AiPromptDefinition`, `AiModelDefinition`, `AiFieldGenerationConfig`, `AiFactDefinition`, `AiCalibration`.
- **External**: `java.time.Instant`, `java.util.regex`, `org.jspecify.annotations.Nullable`, Lombok (`@ToString`), and standard Java collections.

#### Cross-cutting
- Defensive copying in `AiConditionGroup.setConditions`.
- Validation of condition syntax and regex patterns in `AiConditionEvaluator.validate` and `AiFactExtractor.validate`.
- Thread‑safe, stateless utilities (`AiConditionEvaluator`, `AiFactExtractor`).
- Immutable enums (`AiOversizeStrategy`).
- Mutable JavaBeans (`SrcMorphConfiguration`, `AiGenerationConfig`) expose getters/setters; lists returned are unmodifiable where appropriate.
