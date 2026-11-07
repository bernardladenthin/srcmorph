### java/config
- H: 1.0
- C: 6E389E7C
- D: 2026-11-04T12:04:37Z
- T: 2026-11-04T12:11:23Z
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
> A configuration‑driven AI source‑generation framework that evaluates file‑selection rules, extracts deterministic language‑agnostic facts, and orchestrates model runs with fine‑grained tuning.

#### Purpose
- Provide a unified, Maven‑friendly configuration for AI‑powered source code generation.
- Evaluate complex file‑selection conditions and handle oversized files.
- Generate deterministic fact blocks from source text for AI prompts.

#### Responsibilities
- **Configuration Management** – `SrcMorphConfiguration`, `AiGenerationConfig`, `AiOversizeStrategy`.
- **Condition Evaluation** – `AiConditionEvaluator`, `AiConditionGroup`.
- **Fact Extraction** – `AiFactExtractor`.
- **Strategy Parsing** – `AiOversizeStrategy` enum.

#### Key units
- **`SrcMorphConfiguration`** – mutable bean aggregating all runtime settings (directories, file filters, AI provider, model paths, prompt and fact definitions).
- **`AiGenerationConfig`** – JavaBean holding per‑generation parameters (model path, context size, temperature, retry logic, stop strings, DRY settings, GPU/CPU allocation, etc.).
- **`AiOversizeStrategy`** – enum (`FAIL`, `SAMPLE`, `MAP_REDUCE`, `DETERMINISTIC`) with `fromConfig` parsing and default `FAIL`.
- **`AiConditionEvaluator`** – stateless evaluator that recursively checks `AiCondition` trees against `AiFileContext`, validates structure, and flags line‑count usage.
- **`AiConditionGroup`** – defensive‑copy container for child `AiCondition` objects used in combinator nodes.
- **`AiFactExtractor`** – static utility that builds a facts block from a list of `AiFactCounter` regexes, validating patterns and counting matches.

#### Data flow
1. **Configuration**: `SrcMorphConfiguration` is populated (via Maven plugin, CLI, or programmatically).  
2. **Condition Setup**: `AiCondition` trees are defined in the configuration and passed to `AiConditionEvaluator`.  
3. **File Selection**: For each source file, `AiConditionEvaluator.matches` decides inclusion; `usesLines` informs whether line counting is needed.  
4. **Fact Extraction**: `AiFactExtractor.factsBlock` runs on selected files, producing a deterministic facts string.  
5. **Generation**: `AiGenerationConfig` supplies model parameters to the AI provider, which consumes the facts block and other prompts to generate output.

#### Dependencies
- **Internal**: `AiCondition`, `AiConditionGroup`, `AiRangeCondition`, `AiFileContext`, `AiFactCounter`, `AiPromptDefinition`, `AiModelDefinition`, `AiFieldGenerationConfig`, `AiFactDefinition`, `AiCalibration`.
- **External**: `java.time`, `java.util`, `java.io`, `net.ladenthin.srcmorph.support.AiSourceExcludeFilter`, `org.jspecify.annotations`, Lombok `@ToString`.

#### Cross-cutting
- Defensive copying in `AiConditionGroup` prevents external mutation.
- Validation logic in `AiConditionEvaluator` and `AiFactExtractor` ensures configuration correctness before runtime.
- Thread‑safety: all evaluator and extractor methods are stateless; configuration beans are mutable but intended for single‑threaded setup.
- Error handling: `IllegalArgumentException` for misconfiguration; `IllegalStateException` for malformed condition trees.
