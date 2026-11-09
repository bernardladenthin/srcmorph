### java/config
- H: 1.0
- C: 6E389E7C
- D: 2026-11-04T07:09:59Z
- T: 2026-11-04T07:18:03Z
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
> A Java library that configures, validates, and orchestrates AI‑powered source‑code generation and routing for the srcmorph engine.

#### Purpose
- Provide a unified configuration model for AI generation, file selection, and oversize handling.
- Validate condition trees and generate deterministic fact blocks for AI prompts.

#### Responsibilities
- **Configuration Management** – `SrcMorphConfiguration` aggregates all runtime settings; `AiGenerationConfig` holds AI provider parameters.
- **Condition Evaluation** – `AiConditionEvaluator` checks file‑selection rules; `AiConditionGroup` stores child conditions.
- **Oversize Strategy** – `AiOversizeStrategy` defines actions for files exceeding context limits.
- **Fact Extraction** – `AiFactExtractor` builds a facts block summarizing regex match counts for a source file.

#### Key units
- `SrcMorphConfiguration` – mutable bean for plugin/CLI settings.
- `AiGenerationConfig` – mutable bean for AI provider options.
- `AiConditionEvaluator` – stateless validator/evaluator of `AiCondition` trees.
- `AiConditionGroup` – container for child `AiCondition` objects.
- `AiOversizeStrategy` – enum mapping config tokens to handling strategies.
- `AiFactExtractor` – static utility that counts regex matches and formats a facts block.

#### Data flow
1. **Configuration**: `SrcMorphConfiguration` is populated from Maven/CLI/JSON/YAML.  
2. **AI Settings**: `AiGenerationConfig` is derived from the configuration and passed to the AI provider.  
3. **File Selection**: For each source file, `AiConditionEvaluator` evaluates the configured `AiCondition` tree (via `AiConditionGroup`) to decide inclusion.  
4. **Oversize Handling**: If a file exceeds the model’s context window, `AiOversizeStrategy` determines whether to fail, sample, map‑reduce, or deterministically truncate.  
5. **Fact Generation**: When generating prompts, `AiFactExtractor` counts regex matches in the source and prepends a facts block to the AI body.

#### Dependencies
- **Internal**: `AiSourceExcludeFilter`, `AiCondition`, `AiFactCounter`, `AiPromptDefinition`, `AiModelDefinition`, `AiFieldGenerationConfig`, `AiFactDefinition`, `AiCalibration`.  
- **External**: `java.time`, `java.util`, `java.io`, `java.util.regex`, Lombok (`@ToString`), `org.jspecify.annotations`.

#### Cross‑cutting
- **Stateless utilities**: `AiConditionEvaluator` and `AiFactExtractor` are thread‑safe and contain no shared mutable state.  
- **Validation**: Both evaluator and fact extractor perform strict input validation, throwing `IllegalArgumentException` for malformed configurations or regexes.  
- **Configuration beans**: `SrcMorphConfiguration` and `AiGenerationConfig` are plain JavaBeans with getters/setters, no business logic, and are intentionally not thread‑safe.  
- **Enum immutability**: `AiOversizeStrategy` is a simple, immutable enum used for configuration parsing.
