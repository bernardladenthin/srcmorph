### java/config
- H: 1.0
- C: 6E389E7C
- D: 2026-11-04T07:43:16Z
- T: 2026-11-04T07:49:25Z
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
> A configuration‑centric framework that evaluates file‑selection rules, extracts deterministic code‑facts, and orchestrates AI‑generation settings for source‑morph indexing.

#### Purpose
- Centralize runtime parameters for srcmorph runs.
- Validate and evaluate file‑selection conditions.
- Compute deterministic fact blocks for AI summarization.

#### Responsibilities
- **Configuration Management** – `SrcMorphConfiguration` holds all plugin, AI, and file‑selection settings.
- **Condition Evaluation** – `AiConditionEvaluator` checks `AiCondition` trees against file metadata.
- **Fact Extraction** – `AiFactExtractor` counts regex‑based facts in source files.
- **Oversize Handling** – `AiOversizeStrategy` defines actions for files exceeding context limits.
- **AI Generation Parameters** – `AiGenerationConfig` stores model, sampling, and caching options.

#### Key units
- `SrcMorphConfiguration` – mutable bean for Maven/Jackson binding.
- `AiConditionEvaluator` – stateless validator/evaluator for `AiCondition` trees.
- `AiConditionGroup` – container for logical combinator children.
- `AiFactExtractor` – utility for deterministic fact counting.
- `AiOversizeStrategy` – enum of oversize handling policies.
- `AiGenerationConfig` – JavaBean of AI generation settings.

#### Data flow
1. **Configuration Load** – `SrcMorphConfiguration` is populated from Maven or JSON/YAML.
2. **File Routing** – `AiConditionEvaluator` uses `AiCondition` trees to decide if a file is included.
3. **Fact Computation** – `AiFactExtractor` processes source text to produce a facts block.
4. **AI Generation** – `AiGenerationConfig` supplies parameters to the provider layer, which may use `AiOversizeStrategy` to handle oversized inputs.

#### Dependencies
- Java core (`java.util`, `java.io`, `java.time`, `java.util.regex`).
- Lombok (`@ToString`).
- `org.jspecify.annotations.Nullable`.
- `net.ladenthin.srcmorph.config` package (AiCondition, AiPromptDefinition, AiModelDefinition, AiFieldGenerationConfig, AiFactDefinition, AiCalibration).
- `net.ladenthin.srcmorph.support.AiSourceExcludeFilter`.

#### Cross‑cutting
- **Null handling** – setters replace `null` with defaults or empty collections.
- **Thread safety** – all utilities are stateless; configuration beans are not thread‑safe.
- **Validation** – `AiConditionEvaluator.validate` and `AiFactExtractor.validate` enforce configuration correctness.
- **Immutability** – `AiOversizeStrategy` enum is immutable; `AiConditionGroup` defensively copies collections.
