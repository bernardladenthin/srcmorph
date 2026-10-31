### java/config
- H: 1.0
- C: A3C47B7D
- D: 2026-10-31T10:06:17Z
- T: 2026-10-31T13:19:53Z
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
> A configuration‑driven AI‑powered source‑code transformation engine that validates file selection, extracts deterministic facts, and orchestrates AI generation steps for code generation or analysis.

#### Purpose
- Central runtime configuration holder for srcmorph executions.
- Validates AI filter trees and determines file eligibility.
- Extracts deterministic fact blocks for oversize handling.
- Exposes AI generation parameters to the provider.

#### Responsibilities
- **Configuration Management** – `SrcMorphConfiguration` aggregates all runtime settings (paths, AI models, prompts, routing rules, overrides).
- **Condition Validation & Evaluation** – `AiConditionEvaluator` checks syntax, bounds, and evaluates `AiCondition` trees against file metadata.
- **Fact Extraction** – `AiFactExtractor` compiles regex counters into a deterministic facts header for AI inputs.
- **Oversize Strategy** – `AiOversizeStrategy` defines how oversized files are routed (FAIL, SAMPLE, MAP_REDUCE, DETERMINISTIC).
- **AI Generation Settings** – `AiGenerationConfig` holds per‑generation tuning (model path, sampling, cache, GPU, etc.).

#### Key units
- `SrcMorphConfiguration` – mutable bean for Maven/JSON/YAML configuration.
- `AiConditionEvaluator` – stateless validator/evaluator for `AiCondition` trees.
- `AiFactExtractor` – static utility for building fact blocks.
- `AiOversizeStrategy` – enum mapping config strings to handling modes.
- `AiGenerationConfig` – JavaBean for AI provider parameters.

#### Data flow
1. **Configuration Load** – `SrcMorphConfiguration` is populated from Maven/JSON/YAML.
2. **File Selection** – For each source file, `AiConditionEvaluator.validate` checks the condition tree; `matches` decides inclusion.
3. **Fact Generation** – If a file is oversized, `AiFactExtractor.factsBlock` creates a facts header from `AiFactCounter` list.
4. **AI Generation** – `AiGenerationConfig` supplies model and sampling parameters to the AI provider; `AiOversizeStrategy` dictates routing for oversized files.

#### Dependencies
- Java core (`java.util`, `java.time`, `java.io`, `java.util.regex`).
- Lombok (`@ToString`).
- `org.jspecify.annotations.Nullable` for null safety.
- `net.ladenthin.srcmorph.config` package (AiCondition, AiFactCounter, AiPromptDefinition, AiModelDefinition, AiFieldGenerationConfig, AiFactDefinition, AiCalibration).
- `net.ladenthin.srcmorph.support.AiSourceExcludeFilter` for glob handling.

#### Cross-cutting
- **Stateless utilities**: `AiConditionEvaluator` and `AiFactExtractor` are thread‑safe, no shared mutable state.
- **Defensive copying**: `AiConditionGroup` copies collections; `SrcMorphConfiguration` returns unmodifiable lists.
- **Error handling**: Validation throws `IllegalArgumentException` or `IllegalStateException`; parsing errors are wrapped.
- **Configuration defaults**: All beans provide sensible defaults; null setters normalize to defaults.
- **Extensibility**: `AiOversizeStrategy` allows new strategies via enum constants; condition nodes support future extensions.
