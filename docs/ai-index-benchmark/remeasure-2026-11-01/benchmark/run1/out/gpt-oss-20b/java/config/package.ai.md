### java/config
- H: 1.0
- C: 6E389E7C
- D: 2026-11-01T14:19:41Z
- T: 2026-11-01T14:28:21Z
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
> Provides AI‑powered source‑code analysis, fact extraction, and generation configuration for a Maven‑based code‑generation plugin.

#### Purpose
- Central configuration holder for AI‑based source‑generation workflow.
- Evaluates AI‑condition trees against file metadata.
- Extracts deterministic fact blocks from source files.
- Defines oversize handling strategies for large files.

#### Responsibilities
- **Configuration Management** – `SrcMorphConfiguration`, `AiGenerationConfig`, `AiOversizeStrategy`.
- **Condition Evaluation** – `AiConditionEvaluator`, `AiConditionGroup`.
- **Fact Extraction** – `AiFactExtractor`.
- **Data Validation** – internal checks in evaluator and fact extractor.

#### Key units
- `SrcMorphConfiguration` – mutable JavaBean for runtime settings (directories, providers, prompts, models, field rules).
- `AiGenerationConfig` – per‑generation mutable settings (model path, context size, temperature, GPU layers, caching, stop strings, dry‑run tuning).
- `AiOversizeStrategy` – enum for handling files that exceed model context (FAIL, SAMPLE, MAP_REDUCE, DETERMINISTIC).
- `AiConditionEvaluator` – stateless evaluator that validates and matches `AiCondition` trees against `AiFileContext`.
- `AiConditionGroup` – container for child `AiCondition` objects used by logical combinators.
- `AiFactExtractor` – utility that builds deterministic fact blocks from regex counters.

#### Data flow
1. **Configuration**: `SrcMorphConfiguration` is populated via Maven, JSON/YAML, or code.
2. **Routing**: `AiConditionEvaluator` checks each source file against configured `AiCondition` trees to decide if AI processing applies.
3. **Fact Extraction**: For matched files, `AiFactExtractor` counts regex matches and produces a facts block.
4. **Generation**: `AiGenerationConfig` supplies parameters to the AI provider (llama.cpp or remote) for generating code or summaries.
5. **Oversize Handling**: `AiOversizeStrategy` determines how to split or sample oversized files before generation.

#### Dependencies
- Java standard libraries (`java.util`, `java.time`, `java.io`, `java.util.regex`).
- Lombok for boilerplate (`@ToString`).
- `org.jspecify.annotations` for null‑safety annotations.
- `net.ladenthin.srcmorph.support.AiSourceExcludeFilter` used by evaluator.
- Prompt and model definition classes (`AiPromptDefinition`, `AiModelDefinition`, `AiFactDefinition`).

#### Cross‑cutting
- **Stateless, thread‑safe utilities**: `AiConditionEvaluator` and `AiFactExtractor` contain no mutable state.
- **Validation**: Both evaluator and fact extractor perform strict configuration checks, throwing `IllegalArgumentException` or `IllegalStateException` on malformed input.
- **Mutable configuration beans**: `SrcMorphConfiguration` and `AiGenerationConfig` expose JavaBean setters/getters; no synchronization, intended for single‑threaded setup.
- **Enum immutability**: `AiOversizeStrategy` is a thread‑safe enum with a static default and a parsing helper.
- **Null handling**: Optional collections are allowed to be `null`; setters replace with empty lists to avoid `NullPointerException`.
