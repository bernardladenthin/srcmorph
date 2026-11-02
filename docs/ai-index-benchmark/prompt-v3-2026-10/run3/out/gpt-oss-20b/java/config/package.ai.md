### java/config
- H: 1.0
- C: 245AEBCF
- D: 2026-10-31T20:34:56Z
- T: 2026-10-31T20:45:31Z
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
> A configuration‑driven AI‑powered source‑code transformation engine that evaluates file‑selection rules, extracts language‑agnostic facts, and orchestrates model‑based generation for documentation or refactoring.

#### Purpose
- Bind runtime settings from Maven, JSON/YAML, or Java code into a single mutable configuration.
- Evaluate logical file‑selection conditions and determine if file contents must be read.
- Extract deterministic fact counts from source files for AI prompts.
- Define how oversized source files are handled during routing.
- Provide a comprehensive holder for AI generation parameters.

#### Responsibilities
- **Configuration Management** – `SrcMorphConfiguration` aggregates all runtime options, including file filters, AI provider settings, and model paths.
- **Condition Evaluation** – `AiConditionEvaluator` validates and evaluates `AiCondition` trees against file metadata.
- **Fact Extraction** – `AiFactExtractor` builds fact blocks from regex counters and validates counter definitions.
- **Oversize Handling** – `AiOversizeStrategy` enumerates strategies for routing oversized files.
- **Generation Parameter Storage** – `AiGenerationConfig` holds detailed AI model and prompt settings for a single generation run.

#### Key units
- `SrcMorphConfiguration` – mutable JavaBean for all srcmorph settings.
- `AiConditionEvaluator` – stateless validator/evaluator for `AiCondition` trees.
- `AiConditionGroup` – container for child conditions in `<and>`/`<or>` combinators.
- `AiFactExtractor` – static utility for building fact blocks from `AiFactCounter` lists.
- `AiOversizeStrategy` – enum mapping config tokens to oversize handling policies.
- `AiGenerationConfig` – mutable holder for AI generation parameters (model path, context size, temperature, cache, etc.).

#### Data flow
1. **Configuration Load** – `SrcMorphConfiguration` is populated via Maven, JSON/YAML, or Java code.
2. **File Selection** – For each source file, `AiConditionEvaluator` checks the configured `AiCondition` tree; `usesLines` determines if the file must be read.
3. **Fact Extraction** – If required, `AiFactExtractor.factsBlock` generates a facts string from `AiFactCounter` definitions.
4. **Routing Decision** – Oversized files are handled per `AiOversizeStrategy`; routing rules from `AiFieldGenerationConfig` are applied.
5. **Generation** – `AiGenerationConfig` supplies all parameters to the AI provider for prompt construction and response generation.

#### Dependencies
- **Internal**: `AiCondition`, `AiFactCounter`, `AiPromptDefinition`, `AiModelDefinition`, `AiFieldGenerationConfig`, `AiFactDefinition`.
- **External**: `java.time`, `java.util`, `java.util.regex`, `org.jspecify.annotations`, Lombok (`@ToString`), `net.ladenthin.srcmorph.support.AiSourceExcludeFilter`.

#### Cross-cutting
- **Thread safety**: `AiConditionEvaluator` and `AiFactExtractor` are stateless and thread‑safe; configuration classes are not synchronized.
- **Null handling**: Many setters accept `null` and reset to defaults; collections may be `null` to indicate “no value”.
- **Validation**: `AiConditionEvaluator.validate` and `AiFactExtractor.validate` enforce well‑formed configurations, throwing `IllegalArgumentException` on errors.
- **Configuration tokens**: `AiOversizeStrategy.fromConfig` parses string tokens, defaulting to `FAIL` when blank or `null`.
