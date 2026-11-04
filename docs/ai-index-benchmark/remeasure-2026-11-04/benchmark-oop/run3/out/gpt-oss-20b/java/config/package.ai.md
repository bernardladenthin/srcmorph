### java/config
- H: 1.0
- C: 6E389E7C
- D: 2026-11-04T07:50:25Z
- T: 2026-11-04T07:56:25Z
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
> A configuration‑driven framework for AI‑powered source‑code generation, validation, and file‑selection logic used by the Maven srcmorph plugin.

#### Purpose
- Centralizes AI generation parameters and file‑selection rules.
- Validates condition trees and fact counter configurations.
- Provides utilities for fact extraction and oversize handling.

#### Responsibilities
- **Configuration Management** – `SrcMorphConfiguration` and `AiGenerationConfig` hold mutable settings for the plugin and AI provider.
- **Condition Evaluation** – `AiConditionEvaluator` validates and evaluates file‑selection trees (`AiCondition`/`AiConditionGroup`).
- **Fact Extraction** – `AiFactExtractor` builds deterministic fact blocks from source text.
- **Oversize Strategy** – `AiOversizeStrategy` defines how to handle files that exceed the model context window.

#### Key units
- `SrcMorphConfiguration` – JavaBean for plugin‑level settings (directories, prompts, models, Llama options).
- `AiGenerationConfig` – mutable bean for a single AI generation step (model path, sampling, retry, stop strings, etc.).
- `AiConditionEvaluator` – stateless validator/evaluator for `AiCondition` trees.
- `AiConditionGroup` – container for child conditions in `<and>`/`<or>` combinators.
- `AiFactExtractor` – utility for counting regex matches and assembling fact blocks.
- `AiOversizeStrategy` – enum of strategies (`FAIL`, `SAMPLE`, `MAP_REDUCE`, `DETERMINISTIC`) with parsing logic.

#### Data flow
1. **Configuration**: `SrcMorphConfiguration` is populated via Maven/JSON/YAML.  
2. **Prompt & Model Setup**: `AiPromptDefinition`/`AiModelDefinition` feed into `AiGenerationConfig`.  
3. **File Selection**: `AiConditionEvaluator` receives a root `AiCondition` and `AiFileContext` to decide inclusion.  
4. **Fact Generation**: `AiFactExtractor` processes source files to produce fact blocks for prompts.  
5. **Oversize Handling**: `AiOversizeStrategy.fromConfig` selects a strategy for large files before generation.

#### Dependencies
- **Internal**: `AiCondition`, `AiConditionGroup`, `AiFactCounter`, `AiPromptDefinition`, `AiModelDefinition`, `AiFieldGenerationConfig`, `AiFactDefinition`, `AiSourceExcludeFilter`.
- **External**: `java.time.Instant`, `java.util.regex`, Lombok (`@ToString`), `org.jspecify.annotations.Nullable`.

#### Cross‑cutting
- Defensive copying in `AiConditionGroup.setConditions`.
- Stateless, thread‑safe utilities (`AiConditionEvaluator`, `AiFactExtractor`).
- Validation throws `IllegalArgumentException` for misconfigurations.
- Lombok `@ToString` used for diagnostic output across beans.
