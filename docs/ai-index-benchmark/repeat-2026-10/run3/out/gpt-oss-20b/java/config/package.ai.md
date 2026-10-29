### java/config
- H: 1.0
- C: FDDD5FDA
- D: 2026-10-29T14:19:17Z
- T: 2026-10-29T14:55:08Z
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
> A Maven‑style Java library that orchestrates AI‑powered source‑code transformations, providing configuration, condition evaluation, fact extraction, and oversize‑file handling for a code‑morphing workflow.  

#### Purpose
- Central configuration and orchestration for AI‑driven source‑code transformations.  
- Evaluate file‑selection logic, extract deterministic facts, and manage oversized‑file strategies.

#### Responsibilities
- **Configuration** – `SrcMorphConfiguration` aggregates all runtime settings; `AiGenerationConfig` holds per‑model generation parameters.  
- **Condition evaluation** – `AiConditionEvaluator` validates and evaluates logical file‑selection trees; `AiConditionGroup` groups child conditions.  
- **Fact extraction** – `AiFactExtractor` computes deterministic, language‑agnostic fact counts from regex counters.  
- **Oversize handling** – `AiOversizeStrategy` defines how to process files that exceed the model’s context window.

#### Key units
- `SrcMorphConfiguration` – mutable bean exposing Maven‑style parameters for directories, AI provider, prompts, models, and generation rules.  
- `AiGenerationConfig` – JavaBean for AI model run settings (model path, context size, sampling, GPU/CPU options, cache, etc.).  
- `AiConditionEvaluator` – stateless evaluator that recursively walks `AiCondition` trees, validates syntax, and determines line‑count usage.  
- `AiConditionGroup` – container for `<and>`/`<or>` child conditions used in configuration.  
- `AiFactExtractor` – utility that builds a facts block string from a list of `AiFactCounter` objects and validates regex patterns.  
- `AiOversizeStrategy` – enum (`FAIL`, `SAMPLE`, `MAP_REDUCE`, `DETERMINISTIC`) with parsing logic and default handling.

#### Data flow
1. **Configuration**: `SrcMorphConfiguration` is populated by Maven or CLI, referencing `AiPromptDefinition`, `AiModelDefinition`, `AiFieldGenerationConfig`, and `AiFactDefinition`.  
2. **Condition evaluation**: `AiConditionEvaluator` receives an `AiCondition` tree and an `AiFileContext`; it validates the tree, checks for line‑count needs, and returns a boolean match result.  
3. **Fact extraction**: `AiFactExtractor` consumes a list of `AiFactCounter` and source text, producing a formatted facts block for AI prompts.  
4. **Oversize handling**: `AiOversizeStrategy.fromConfig` parses a string token from configuration to determine how oversized files are processed during routing.

#### Dependencies
- **Internal**: `net.ladenthin.srcmorph.config` (AiCondition, AiFactCounter, AiCalibration), `net.ladenthin.srcmorph.prompt` (AiPromptDefinition).  
- **External**: `java.time`, `java.util`, `java.util.regex`, `org.jspecify.annotations`, Lombok (`@ToString`), `net.ladenthin.srcmorph.support.AiSourceExcludeFilter`.  
- **Enum**: `AiOversizeStrategy` uses `org.jspecify.annotations.Nullable` for config parsing.

#### Cross-cutting
- **Thread safety**: `AiConditionEvaluator` and `AiFactExtractor` are stateless and thread‑safe; configuration beans are mutable and not thread‑safe.  
- **Validation**: `AiConditionEvaluator.validate` and `AiFactExtractor.validate` enforce non‑null labels/patterns and correct regex syntax.  
- **Error handling**: Illegal arguments are thrown for misconfigurations; parsing errors are wrapped in `IllegalArgumentException`.  
- **Configuration defaults**: `AiOversizeStrategy.DEFAULT` is `FAIL`; `AiGenerationConfig` provides sensible defaults for all fields.  
- **Immutability**: `AiOversizeStrategy` enum is immutable; configuration lists are returned unmodifiable where appropriate.
