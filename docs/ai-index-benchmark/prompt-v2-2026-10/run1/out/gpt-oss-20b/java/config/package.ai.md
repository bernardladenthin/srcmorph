### java/config
- H: 1.0
- C: AB725D96
- D: 2026-10-31T14:30:30Z
- T: 2026-10-31T14:41:18Z
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
> A configuration‑driven AI‑generation framework that evaluates file‑selection rules, extracts deterministic facts, and orchestrates AI model invocation for source‑code summarisation and transformation.

#### Purpose
- Provide a unified, Maven‑plugin‑friendly configuration for AI‑based source‑code processing.
- Evaluate complex file‑selection conditions and handle oversized files.
- Generate deterministic fact blocks for AI prompts.
- Expose mutable AI generation parameters for runtime control.

#### Responsibilities
- **Condition evaluation** – `AiConditionEvaluator` parses and validates `AiCondition` trees, determining file inclusion and whether line counts are needed.
- **Condition grouping** – `AiConditionGroup` stores child conditions for logical combinators.
- **Fact extraction** – `AiFactExtractor` builds a header‑prefixed facts string from regex counters.
- **Oversize handling** – `AiOversizeStrategy` defines strategies for oversized source files.
- **Configuration aggregation** – `SrcMorphConfiguration` holds all runtime options (file filters, AI provider, model paths, prompt definitions, etc.).
- **AI generation settings** – `AiGenerationConfig` encapsulates model, sampling, and runtime parameters for a single AI run.

#### Key units
- `AiConditionEvaluator` – evaluates `AiCondition` trees against `AiFileContext`.
- `AiConditionGroup` – container for child `AiCondition` instances.
- `AiFactExtractor` – builds deterministic facts block from `AiFactCounter` list.
- `AiOversizeStrategy` – enum with `FAIL`, `SAMPLE`, `MAP_REDUCE`, `DETERMINISTIC`; parses config strings.
- `SrcMorphConfiguration` – mutable JavaBean exposing all srcmorph runtime options.
- `AiGenerationConfig` – mutable JavaBean for AI model and sampling parameters.

#### Data flow
1. **Configuration load** – `SrcMorphConfiguration` is populated via Maven plugin or JSON/YAML.
2. **File routing** – `AiConditionEvaluator` checks each file against conditions; `AiOversizeStrategy` decides handling for oversized files.
3. **Fact generation** – `AiFactExtractor` produces a facts header for AI prompts.
4. **AI invocation** – `AiGenerationConfig` supplies parameters to the chosen AI provider (e.g., llama.cpp, remote API).
5. **Result integration** – AI output is written back to source files or aggregated indices.

#### Dependencies
- **Internal**: `AiCondition`, `AiFactCounter`, `AiPromptDefinition`, `AiModelDefinition`, `AiFieldGenerationConfig`, `AiFactDefinition`.
- **External**: `java.time`, `java.util`, `java.util.regex`, `org.jspecify.annotations`, Lombok (`@ToString`), `net.ladenthin.srcmorph.support.AiSourceExcludeFilter`.

#### Cross-cutting
- **Stateless utilities** – `AiConditionEvaluator`, `AiFactExtractor` are thread‑safe and stateless.
- **Defensive copying** – `AiConditionGroup` copies input collections to avoid external mutation.
- **Validation** – `AiConditionEvaluator.validate` and `AiFactExtractor.validate` enforce configuration correctness.
- **Configuration immutability** – enums (`AiOversizeStrategy`) and final classes (`AiConditionEvaluator`, `AiFactExtractor`) provide thread‑safe constants.
- **Mutable beans** – `SrcMorphConfiguration` and `AiGenerationConfig` are intentionally mutable for build‑time configuration; not thread‑safe.
