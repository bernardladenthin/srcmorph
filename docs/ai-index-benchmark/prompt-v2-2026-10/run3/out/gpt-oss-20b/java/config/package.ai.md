### java/config
- H: 1.0
- C: 18A9ED9C
- D: 2026-10-31T14:57:29Z
- T: 2026-10-31T15:11:02Z
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
> A Java library that configures, validates, and orchestrates AI‑driven source‑code transformations, providing rule‑based routing, fact extraction, and generation parameter management for the srcmorph tool.

#### Purpose
- Central configuration and validation for AI‑based source‑code morphing.
- Supplies routing logic, fact extraction, and generation settings to the srcmorph engine.

#### Responsibilities
- **Configuration Management** – `SrcMorphConfiguration` aggregates all runtime options, while `AiGenerationConfig` holds per‑run generation parameters.
- **Condition Evaluation** – `AiConditionEvaluator` validates and evaluates logical condition trees (`AiConditionGroup`) against file metadata.
- **Fact Extraction** – `AiFactExtractor` computes deterministic fact blocks from source text using regex counters.
- **Oversize Handling** – `AiOversizeStrategy` defines policies for files that exceed the model context window.

#### Key units
- `SrcMorphConfiguration` – mutable JavaBean for the entire srcmorph run.
- `AiGenerationConfig` – mutable JavaBean for a single AI generation invocation.
- `AiConditionEvaluator` – stateless validator/evaluator for `AiCondition` trees.
- `AiConditionGroup` – container for child conditions in `<and>/<or>` combinators.
- `AiFactExtractor` – static utility for building fact blocks from regex counters.
- `AiOversizeStrategy` – enum of oversize handling policies (FAIL, SAMPLE, MAP_REDUCE, DETERMINISTIC).

#### Data flow
1. **Configuration**: `SrcMorphConfiguration` is populated (Maven, JSON/YAML, or code).  
2. **Routing**: For each source file, `AiConditionEvaluator` checks `AiConditionGroup` trees to decide inclusion and line‑count needs.  
3. **Fact Generation**: If facts are required, `AiFactExtractor.factsBlock` produces a deterministic fact string.  
4. **Generation**: `AiGenerationConfig` supplies parameters to the AI model; `AiOversizeStrategy` dictates how oversized files are handled before generation.

#### Dependencies
- Core Java (`java.util`, `java.io`, `java.time`, `java.util.regex`).
- External libs: Lombok (`@ToString`), `org.jspecify.annotations`, `net.ladenthin.srcmorph.support.AiSourceExcludeFilter`.
- Configuration classes: `AiPromptDefinition`, `AiModelDefinition`, `AiFactDefinition`, `AiFieldGenerationConfig`, `AiCalibration`.

#### Cross-cutting
- **Immutability & Thread‑Safety**: `AiConditionEvaluator`, `AiFactExtractor`, and `AiOversizeStrategy` are stateless and thread‑safe.  
- **Validation**: `AiConditionEvaluator.validate` and `AiFactExtractor.validate` enforce configuration correctness.  
- **Exception Handling**: `IllegalArgumentException` and `IllegalStateException` signal misconfigurations; regex errors are wrapped.  
- **Configuration Normalization**: Setters in `AiGenerationConfig` and `SrcMorphConfiguration` coerce nulls to defaults and expose unmodifiable lists.  
- **Logging/Diagnostics**: Lombok `@ToString` provides readable state dumps for debugging.
