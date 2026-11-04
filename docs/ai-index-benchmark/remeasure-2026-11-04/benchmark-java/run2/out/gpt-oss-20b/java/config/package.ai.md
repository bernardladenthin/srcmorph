### java/config
- H: 1.0
- C: 6E389E7C
- D: 2026-11-04T07:27:53Z
- T: 2026-11-04T07:34:18Z
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
> A Java library that powers AI‑driven source‑code generation, providing configuration, condition evaluation, fact extraction, and oversized‑file handling for a Maven‑style plugin.

#### Purpose
- Configure and run AI‑powered source‑generation workflows.
- Evaluate file‑selection rules, extract deterministic facts, and manage oversized source files.

#### Responsibilities
- **Configuration** – `SrcMorphConfiguration`, `AiGenerationConfig`, `AiOversizeStrategy`.
- **Condition evaluation** – `AiConditionEvaluator`, `AiConditionGroup`.
- **Fact extraction** – `AiFactExtractor`.
- **Oversize handling** – `AiOversizeStrategy` enum.

#### Key units
- `SrcMorphConfiguration` – mutable bean aggregating all runtime settings (paths, file filters, AI provider, model definitions, prompts, field rules, fact groups).
- `AiGenerationConfig` – bean holding AI provider parameters (model path, context size, sampling, GPU/offload, cache, DRY, etc.).
- `AiOversizeStrategy` – enum with `FAIL`, `SAMPLE`, `MAP_REDUCE`, `DETERMINISTIC`; parses config tokens.
- `AiConditionEvaluator` – stateless class that validates and evaluates `AiCondition` trees against `AiFileContext`.
- `AiConditionGroup` – container for child `AiCondition` objects used by `<and>`/`<or>` combinators.
- `AiFactExtractor` – utility that builds a deterministic facts block from regex counters.

#### Data flow
1. **Configuration**: `SrcMorphConfiguration` is populated (Maven, JSON/YAML, or programmatically).
2. **Condition tree**: `AiConditionEvaluator` validates the tree and checks each file via `AiFileContext`.
3. **Fact extraction**: `AiFactExtractor.factsBlock` generates a facts header for oversized files.
4. **Oversize strategy**: `AiOversizeStrategy.fromConfig` selects handling logic for files exceeding the model context window.
5. **Generation**: `AiGenerationConfig` supplies runtime parameters to the AI provider.

#### Dependencies
- **Internal**: `AiCondition`, `AiFileContext`, `AiPromptDefinition`, `AiModelDefinition`, `AiFieldGenerationConfig`, `AiFactDefinition`, `AiCalibration`.
- **External**: `java.time`, `java.util`, `java.util.regex`, `net.ladenthin.srcmorph.support.AiSourceExcludeFilter`, Lombok (`@ToString`), `org.jspecify.annotations.Nullable`.

#### Cross‑cutting
- **Thread safety**: `AiConditionEvaluator` and `AiFactExtractor` are stateless and thread‑safe; configuration beans are not synchronized.
- **Validation**: `AiConditionEvaluator.validate` and `AiFactExtractor.validate` enforce structural correctness and regex syntax.
- **Immutable lists**: `AiGenerationConfig` exposes unmodifiable lists for DRY breakers and stop strings.
- **Error handling**: `IllegalArgumentException` for misconfigured trees, unknown oversize tokens, or invalid regex patterns; `IllegalStateException` for node conflicts.
