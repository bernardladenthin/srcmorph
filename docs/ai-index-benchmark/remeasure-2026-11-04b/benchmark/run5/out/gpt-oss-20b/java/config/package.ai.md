### java/config
- H: 1.0
- C: 6E389E7C
- D: 2026-11-04T12:53:23Z
- T: 2026-11-04T12:55:10Z
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
> A configuration and evaluation framework that drives AI‑powered source‑code transformation jobs, handling file selection, fact extraction, and generation settings for a Maven‑based build pipeline.

#### Purpose
- Central configuration holder for AI‑based source morphing.
- Validates and evaluates file‑selection conditions.
- Generates deterministic fact blocks from source text.
- Defines strategies for oversized source files.

#### Responsibilities
- **Configuration Management** – `SrcMorphConfiguration` aggregates all runtime parameters (directories, file filters, AI provider, model paths, prompt definitions, field rules, fact definitions, Llama settings, etc.).
- **Condition Evaluation** – `AiConditionEvaluator` checks whether a file satisfies a tree of `AiCondition` nodes (extensions, size, lines, timestamps, path glob).
- **Fact Extraction** – `AiFactExtractor` counts language‑agnostic constructs via regex and builds a facts block.
- **Oversize Handling** – `AiOversizeStrategy` enumerates and parses strategies for files that exceed size limits.

#### Key units
- `SrcMorphConfiguration` – mutable JavaBean for Maven/JSON/YAML binding.
- `AiConditionEvaluator` – stateless validator/evaluator for `AiCondition` trees.
- `AiFactExtractor` – static utility for regex‑based fact counting.
- `AiOversizeStrategy` – enum with `FAIL`, `SAMPLE`, `MAP_REDUCE`, `DETERMINISTIC`; default `FAIL`.
- `AiConditionGroup` – container for child `AiCondition` objects (used by evaluator).

#### Data flow
1. **Configuration Load** – `SrcMorphConfiguration` is populated from Maven plugin parameters or external config.
2. **File Selection** – For each source file, `AiConditionEvaluator.matches` is invoked with the file’s metadata (`AiFileContext`) and the configured condition tree.
3. **Fact Generation** – If a file is selected, `AiFactExtractor.factsBlock` produces a facts section from configured `AiFactCounter` objects.
4. **Oversize Decision** – If a file exceeds configured size limits, `AiOversizeStrategy.fromConfig` selects the handling strategy (e.g., `FAIL` aborts, `SAMPLE` trims).
5. **Generation** – `AiGenerationConfig` (nested within `SrcMorphConfiguration`) supplies AI model parameters to the generation engine.

#### Dependencies
- Internal: `AiCondition`, `AiConditionGroup`, `AiRangeCondition`, `AiFileContext`, `AiFactCounter`, `AiPromptDefinition`, `AiModelDefinition`, `AiFieldGenerationConfig`, `AiFactDefinition`, `AiCalibration`.
- External: `java.time.Instant`, `java.util.regex`, `org.jspecify.annotations.Nullable`, Lombok (`@ToString`), `net.ladenthin.srcmorph.support.AiSourceExcludeFilter`.

#### Cross-cutting
- **Validation** – `AiConditionEvaluator.validate` and `AiFactExtractor.validate` enforce configuration correctness.
- **Thread‑safety** – All evaluator and extractor methods are stateless; configuration beans are not thread‑safe but intended for single‑threaded build execution.
- **Immutability** – `AiOversizeStrategy` enum and `AiConditionGroup` defensive copying promote safe sharing.
- **Error handling** – `IllegalArgumentException` signals misconfiguration; `IllegalStateException` guards against malformed condition trees.
