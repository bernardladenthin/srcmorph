### java/config
- H: 1.0
- C: 6E389E7C
- D: 2026-11-01T23:26:17Z
- T: 2026-11-01T23:32:44Z
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
> A configuration‑driven AI‑powered source‑morphing framework that evaluates file‑selection rules, extracts deterministic facts, and orchestrates AI generation for source‑code documentation and transformation.

#### Purpose
- Provide a unified, Java‑bean based configuration for AI‑driven source‑morphing.
- Evaluate file‑selection conditions and handle oversized files.
- Extract deterministic facts from source text for AI prompts.
- Store AI generation parameters for multiple providers.

#### Responsibilities
- **Configuration Management** – `SrcMorphConfiguration` aggregates all runtime settings, including directories, file filters, AI provider details, and prompt/model definitions.
- **Condition Evaluation** – `AiConditionEvaluator` validates and evaluates logical trees (`AiConditionGroup`) against file metadata.
- **Oversize Handling** – `AiOversizeStrategy` defines routing policies for files exceeding the model context window.
- **Fact Extraction** – `AiFactExtractor` compiles regex‑based fact counters into a formatted facts block.
- **Generation Settings** – `AiGenerationConfig` holds per‑generation parameters (model path, sampling, retry, stop strings, provider specifics).

#### Key units
- `SrcMorphConfiguration` – mutable bean for all run‑time settings.
- `AiConditionEvaluator` – stateless validator/evaluator for `AiCondition` trees.
- `AiConditionGroup` – container for child `AiCondition` objects used in `<and>`/`<or>` combinators.
- `AiOversizeStrategy` – enum with `FAIL`, `SAMPLE`, `MAP_REDUCE`, `DETERMINISTIC` strategies.
- `AiFactExtractor` – utility for building fact blocks from `AiFactCounter` lists.
- `AiGenerationConfig` – JavaBean holding AI request parameters (model, temperature, stop strings, provider, etc.).

#### Data flow
1. **Configuration Load** – `SrcMorphConfiguration` is populated from Maven, CLI, or JSON/YAML.
2. **File Routing** – `AiConditionEvaluator` checks each file against the configured `AiCondition` tree; `AiOversizeStrategy` decides how to route oversized files.
3. **Fact Generation** – `AiFactExtractor` processes source text with `AiFactCounter` regexes to produce a facts block.
4. **AI Generation** – `AiGenerationConfig` supplies the AI provider with prompt, model, and sampling settings; the provider returns generated content.

#### Dependencies
- **Internal**: `AiCondition`, `AiFactCounter`, `AiPromptDefinition`, `AiModelDefinition`, `AiFieldGenerationConfig`, `AiFactDefinition`.
- **External**: `java.time`, `java.util.regex`, `org.jspecify.annotations`, Lombok (`@ToString`), `net.ladenthin.srcmorph.support.AiSourceExcludeFilter`.

#### Cross‑cutting
- **Thread Safety**: All utility classes (`AiConditionEvaluator`, `AiFactExtractor`) are stateless and thread‑safe; configuration beans are mutable but intended for single‑threaded use.
- **Validation**: `AiConditionEvaluator.validate` and `AiFactExtractor.validate` enforce configuration correctness, throwing `IllegalArgumentException` on misconfiguration.
- **Immutability**: `AiOversizeStrategy` is an immutable enum; `AiConditionGroup` stores a defensive copy of its child list.
- **Configuration Exposure**: `SrcMorphConfiguration` and `AiGenerationConfig` expose getters/setters with defaults, ensuring nulls are replaced by sensible values.
