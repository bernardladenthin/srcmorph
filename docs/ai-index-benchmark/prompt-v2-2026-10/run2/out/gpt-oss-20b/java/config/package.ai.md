### java/config
- H: 1.0
- C: E192320D
- D: 2026-10-31T14:44:17Z
- T: 2026-10-31T14:54:39Z
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
> A configuration‑driven framework for routing source files to AI models, evaluating file‑selection rules, extracting deterministic facts, and managing AI generation parameters.

#### Purpose
- Central configuration holder for srcmorph runs.
- Evaluate logical file‑selection conditions.
- Extract deterministic fact blocks for AI documentation.
- Define AI generation settings and oversized‑file handling.

#### Responsibilities
- **Configuration Management** – `SrcMorphConfiguration` aggregates all runtime settings (paths, providers, prompts, models, field rules, facts, llama.cpp options).
- **Condition Evaluation** – `AiConditionEvaluator` validates and evaluates `AiCondition` trees against file metadata, determining if line counts are needed.
- **Fact Extraction** – `AiFactExtractor` builds a facts block by counting regex matches in source text.
- **Oversize Strategy** – `AiOversizeStrategy` selects how to handle files that exceed the model context window.
- **Condition Grouping** – `AiConditionGroup` stores child conditions for logical combinators.

#### Key units
- `SrcMorphConfiguration` – mutable JavaBean with getters/setters for all srcmorph settings.
- `AiConditionEvaluator` – stateless validator/evaluator for `AiCondition` trees.
- `AiFactExtractor` – static utility for deterministic fact block generation.
- `AiOversizeStrategy` – enum mapping config tokens to handling strategies.
- `AiConditionGroup` – container for child `AiCondition` objects.

#### Data flow
1. **Configuration**: `SrcMorphConfiguration` is populated (Maven, JSON/YAML, code).
2. **Routing**: `AiConditionEvaluator` checks each file against configured `AiCondition` trees to decide routing.
3. **Fact Generation**: For selected files, `AiFactExtractor` counts regex matches and produces a facts block.
4. **AI Generation**: `AiGenerationConfig` (referenced by `SrcMorphConfiguration`) supplies model parameters to the AI provider.
5. **Oversize Handling**: `AiOversizeStrategy` determines action when a file exceeds the model’s context window.

#### Dependencies
- Java standard libraries (`java.util`, `java.time`, `java.io`, `java.util.regex`).
- `net.ladenthin.srcmorph.support.AiSourceExcludeFilter` for glob matching.
- Lombok (`@ToString`) for debugging output.
- `org.jspecify.annotations.Nullable` for null‑safety annotations.
- External AI provider interfaces (implied by `AiGenerationConfig` and `AiPromptDefinition`).

#### Cross-cutting
- **Statelessness**: `AiConditionEvaluator` and `AiFactExtractor` are thread‑safe, no shared mutable state.
- **Null handling**: Many setters replace `null` with defaults or empty collections; `AiConditionGroup` accepts nullable collections.
- **Validation**: `AiConditionEvaluator.validate` and `AiFactExtractor.validate` enforce configuration correctness, throwing `IllegalArgumentException` on misconfiguration.
- **Immutability**: `AiOversizeStrategy` enum is immutable; `AiConditionGroup` defensively copies lists.
- **Configuration defaults**: `SrcMorphConfiguration` and `AiGenerationConfig` provide sensible defaults and expose mutable JavaBeans for runtime tweaking.
