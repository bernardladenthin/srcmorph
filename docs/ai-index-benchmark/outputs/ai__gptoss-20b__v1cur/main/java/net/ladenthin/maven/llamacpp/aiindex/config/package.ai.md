### main/java/net/ladenthin/maven/llamacpp/aiindex/config
- H: 1.0
- C: C16C9ACE
- D: 2026-07-02T19:31:59Z
- T: 2026-07-02T23:19:39Z
- G: 1.0.3-SNAPSHOT
- A: 0.0.0
- X: package
- F: [AiCalibration.java](AiCalibration.java.ai.md)
- F: [AiCondition.java](AiCondition.java.ai.md)
- F: [AiConditionEvaluator.java](AiConditionEvaluator.java.ai.md)
- F: [AiConditionGroup.java](AiConditionGroup.java.ai.md)
- F: [AiFactCounter.java](AiFactCounter.java.ai.md)
- F: [AiFactDefinition.java](AiFactDefinition.java.ai.md)
- F: [AiFactDefinitionSupport.java](AiFactDefinitionSupport.java.ai.md)
- F: [AiFactExtractor.java](AiFactExtractor.java.ai.md)
- F: [AiFieldGenerationConfig.java](AiFieldGenerationConfig.java.ai.md)
- F: [AiFieldGenerationSelector.java](AiFieldGenerationSelector.java.ai.md)
- F: [AiFileContext.java](AiFileContext.java.ai.md)
- F: [AiGenerationConfig.java](AiGenerationConfig.java.ai.md)
- F: [AiGenerationKind.java](AiGenerationKind.java.ai.md)
- F: [AiModelDefinition.java](AiModelDefinition.java.ai.md)
- F: [AiModelDefinitionSupport.java](AiModelDefinitionSupport.java.ai.md)
- F: [AiOversizeStrategy.java](AiOversizeStrategy.java.ai.md)
- F: [AiRangeCondition.java](AiRangeCondition.java.ai.md)
---
> A Maven plugin that configures, evaluates, and routes AI‑based code‑analysis tasks using llama‑cpp models, applying file‑matching rules, fact extraction, and oversize handling.

#### Purpose
- Configure AI model parameters and routing rules for source‑code generation.
- Evaluate file selection conditions and apply fact extraction.
- Resolve model and fact definitions into concrete runtime configurations.

#### Responsibilities
- **Model configuration** – `AiModelDefinition`, `AiModelDefinitionSupport`, `AiGenerationConfig`.
- **Fact management** – `AiFactDefinition`, `AiFactDefinitionSupport`, `AiFactCounter`, `AiFactExtractor`.
- **Condition evaluation** – `AiCondition`, `AiConditionGroup`, `AiRangeCondition`, `AiConditionEvaluator`.
- **Routing selection** – `AiFieldGenerationConfig`, `AiFieldGenerationSelector`.
- **Oversize handling** – `AiOversizeStrategy`.
- **Calibration** – `AiCalibration`.
- **Context snapshot** – `AiFileContext`.

#### Key units
- `AiModelDefinition` – key + full GGUF model settings.
- `AiModelDefinitionSupport` – builds `Map<String, AiGenerationConfig>`.
- `AiGenerationConfig` – mutable runtime parameters for llama‑cpp.
- `AiFactDefinition` – named group of `AiFactCounter` regexes.
- `AiFactDefinitionSupport` – resolves fact keys to counters.
- `AiFactExtractor` – produces deterministic facts block.
- `AiCondition` – recursive boolean tree for file selection.
- `AiConditionGroup` – container for AND/OR children.
- `AiRangeCondition` – numeric bounds for size/lines.
- `AiConditionEvaluator` – validates and matches conditions.
- `AiFieldGenerationConfig` – single routing rule (priority, action, oversize, facts).
- `AiFieldGenerationSelector` – selects applicable rule for a file.
- `AiOversizeStrategy` – enum of oversize handling options.
- `AiCalibration` – per‑model timing and token‑to‑char ratios.
- `AiFileContext` – immutable snapshot of file metadata.

#### Data flow
1. **Configuration phase**  
   - Maven plugin loads `AiModelDefinition` and `AiFactDefinition` lists.  
   - `AiModelDefinitionSupport` converts models to `AiGenerationConfig`.  
   - `AiFactDefinitionSupport` maps fact keys to counters.

2. **Routing decision**  
   - `AiFieldGenerationSelector` iterates `AiFieldGenerationConfig` rules.  
   - Uses `AiConditionEvaluator` to test each rule’s `AiCondition` against a `AiFileContext`.  
   - Highest‑priority matching rule (or fallback) is chosen.

3. **Fact extraction**  
   - If the rule defines facts (inline or via key), `AiFactExtractor.factsBlock` generates a header string with exact regex counts.

4. **Model execution**  
   - The selected `AiGenerationConfig` (from `AiModelDefinitionSupport`) provides all runtime parameters to llama‑cpp.  
   - `AiCalibration` supplies hardware‑specific timing adjustments.

#### Dependencies
- **Internal** – `AiConditionEvaluator`, `AiFieldGenerationSelector`, `AiFactExtractor`, `AiModelDefinitionSupport`, `AiFactDefinitionSupport`.
- **External libraries** – Lombok (`@ToString`), jspecify (`@Nullable`), Java 8 `java.time`, regex APIs.
- **Data sources** – Maven plugin configuration XML, source files (via `AiFileContext`).

#### Cross‑cutting
- All configuration beans use Lombok `@ToString` and suppress uninitialized field warnings.  
- Mutable configuration classes are not thread‑safe; instances are built once during plugin execution.  
- Validation logic resides in `AiConditionEvaluator` and `AiFieldGenerationSelector`.  
- Fact extraction is pure and stateless, enabling reuse across threads.  
- Enums (`AiOversizeStrategy`, `AiGenerationKind`) provide immutable, thread‑safe configuration constants.
