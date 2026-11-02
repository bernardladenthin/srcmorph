### java/config
- H: 1.0
- C: 386EAE55
- D: 2026-10-31T20:10:35Z
- T: 2026-10-31T20:20:55Z
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
> A Java library that configures, evaluates, and drives AI‑powered source‑code generation and summarisation within the srcmorph toolchain.

#### Purpose
- Central configuration holder for AI‑based source‑generation workflows.
- Provides evaluation of file‑selection rules and fact extraction for deterministic AI prompts.
- Supplies runtime parameters for model execution and oversize handling.

#### Responsibilities
- **Configuration Management** – `SrcMorphConfiguration` aggregates all runtime settings, binding from Maven, JSON/YAML, or code.
- **Condition Evaluation** – `AiConditionEvaluator` and `AiConditionGroup` parse and validate complex file‑selection trees.
- **Fact Extraction** – `AiFactExtractor` generates deterministic fact blocks from regex counters.
- **Oversize Strategy** – `AiOversizeStrategy` defines actions for files exceeding model context limits.
- **Generation Parameters** – `AiGenerationConfig` stores per‑run AI model and sampling options.

#### Key units
- `SrcMorphConfiguration` – mutable JavaBean for all srcmorph settings.
- `AiConditionEvaluator` – stateless evaluator/validator for `AiCondition` trees.
- `AiConditionGroup` – container for child conditions in logical combinators.
- `AiFactExtractor` – utility for deterministic fact counting and block formatting.
- `AiOversizeStrategy` – enum of oversize handling policies (FAIL, SAMPLE, MAP_REDUCE, DETERMINISTIC).
- `AiGenerationConfig` – mutable holder for model path, context size, sampling, caching, GPU, and provider settings.

#### Data flow
1. **Configuration Load** – `SrcMorphConfiguration` is populated via binding mechanisms.
2. **File Routing** – `AiConditionEvaluator` checks each source file against conditions defined in `AiFieldGenerationConfig` (not shown but referenced).
3. **Fact Generation** – `AiFactExtractor` produces a facts block for files that need deterministic AI prompts.
4. **Oversize Handling** – `AiOversizeStrategy` determines whether to fail, sample, map‑reduce, or deterministically truncate oversized files.
5. **AI Generation** – `AiGenerationConfig` supplies runtime parameters to the underlying AI provider (e.g., llama.cpp, OpenAI).

#### Dependencies
- **Internal**: `AiCondition`, `AiFactCounter`, `AiPromptDefinition`, `AiModelDefinition`, `AiFieldGenerationConfig`, `AiFactDefinition`, `AiCalibration`.
- **External**: `java.time`, `java.util.regex`, `java.util`, `lombok`, `org.jspecify.annotations`, `net.ladenthin.srcmorph.support.AiSourceExcludeFilter`.

#### Cross-cutting
- **Thread‑Safety**: All evaluator and extractor utilities are stateless and thread‑safe; configuration classes are mutable but intended for single‑threaded setup.
- **Validation**: `AiConditionEvaluator.validate` and `AiFactExtractor.validate` enforce configuration correctness early.
- **Immutability**: `AiOversizeStrategy` enum is immutable; list getters in configuration expose unmodifiable views.
- **Error Handling**: Configuration parsing throws `IllegalArgumentException` for invalid tokens; evaluator throws `IllegalStateException` for malformed trees.
