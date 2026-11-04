### java/config
- H: 1.0
- C: 6E389E7C
- D: 2026-11-04T11:56:04Z
- T: 2026-11-04T12:03:09Z
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
> A configuration‑driven framework for AI‑powered source‑code generation, routing, and fact extraction within a Maven‑based build pipeline.

#### Purpose
- Centralizes AI generation settings, file‑selection logic, and fact extraction for source‑code transformation.
- Provides runtime decision‑making for oversized files and condition validation.

#### Responsibilities
- **Configuration Management** – `SrcMorphConfiguration` and `AiGenerationConfig` expose mutable JavaBeans for Maven, JSON/YAML, or programmatic use.
- **Condition Evaluation** – `AiConditionEvaluator` validates and matches `AiCondition` trees against file metadata.
- **Fact Extraction** – `AiFactExtractor` computes deterministic regex‑based fact counts for AI prompts.
- **Oversize Handling** – `AiOversizeStrategy` defines strategies for files exceeding the model context window.

#### Key units
- `SrcMorphConfiguration` – holds all run‑time parameters (directories, providers, prompts, models, etc.).
- `AiGenerationConfig` – mutable bean for a single AI generation step (model path, sampling, retry, stop strings, etc.).
- `AiConditionEvaluator` – stateless validator and matcher for `AiCondition` trees.
- `AiConditionGroup` – container for child conditions used by `<and>`/`<or>` combinators.
- `AiFactExtractor` – utility for building fact blocks from `AiFactCounter` configurations.
- `AiOversizeStrategy` – enum mapping config tokens to handling strategies (`FAIL`, `SAMPLE`, `MAP_REDUCE`, `DETERMINISTIC`).

#### Data flow
1. **Configuration**: `SrcMorphConfiguration` is populated (Maven plugin, JSON/YAML, or code).  
2. **Condition Setup**: `AiCondition` trees are defined within the configuration.  
3. **File Selection**: `AiConditionEvaluator` receives a file’s `AiFileContext` and evaluates the tree to decide inclusion and whether line counting is needed.  
4. **Oversize Decision**: If a file exceeds the context window, `AiOversizeStrategy` determines the handling path.  
5. **Fact Extraction**: `AiFactExtractor` processes source text with configured `AiFactCounter`s to produce a facts block for AI prompts.  
6. **Generation**: `AiGenerationConfig` supplies the AI provider with prompt, model, and runtime parameters for the actual generation step.

#### Dependencies
- **Internal**: `AiCondition`, `AiConditionGroup`, `AiFactCounter`, `AiPromptDefinition`, `AiModelDefinition`, `AiFieldGenerationConfig`, `AiFactDefinition`, `AiSourceExcludeFilter`.
- **External**: `java.time`, `java.util`, `java.util.regex`, `org.jspecify.annotations`, Lombok (`@ToString`), Maven plugin APIs.

#### Cross‑cutting
- Defensive copying in `AiConditionGroup` and `SrcMorphConfiguration` to prevent external mutation.
- Thread‑safe stateless utilities (`AiConditionEvaluator`, `AiFactExtractor`).
- Consistent null handling: setters replace `null` with defaults; getters may return `null` for optional fields.
- Error handling: `IllegalArgumentException` for misconfiguration; `IllegalStateException` for unsupported condition nodes.
- Configuration tokens parsed case‑insensitively (`AiOversizeStrategy.fromConfig`).

#### Notes
- The package is designed for build‑time source transformation; all components are pure JavaBeans or stateless utilities.
- No external concurrency primitives; intended for single‑threaded configuration and evaluation phases.
