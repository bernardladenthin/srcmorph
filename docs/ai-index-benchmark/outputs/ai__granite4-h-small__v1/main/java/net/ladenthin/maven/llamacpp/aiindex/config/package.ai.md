### main/java/net/ladenthin/maven/llamacpp/aiindex/config
- H: 1.0
- C: C16C9ACE
- D: 2026-07-02T19:31:59Z
- T: 2026-07-02T22:14:19Z
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
> A Maven plugin configuration library that models AI‑generation rules, file‑matching conditions, fact extraction, and runtime AI settings for the LlamaCPP indexer.

#### Purpose
- Store and validate AI‑generation routing rules and model configurations.
- Provide immutable file metadata and condition evaluators for rule selection.
- Generate deterministic fact blocks from source files for AI prompts.

#### Responsibilities
- **Configuration POJOs** – `AiFieldGenerationConfig`, `AiCondition`, `AiFactDefinition`, `AiModelDefinition`, `AiGenerationConfig`, `AiCalibration`.
- **Condition logic** – `AiConditionEvaluator`, `AiConditionGroup`, `AiRangeCondition`, `AiOversizeStrategy`.
- **Fact handling** – `AiFactCounter`, `AiFactExtractor`, `AiFactDefinitionSupport`.
- **Rule selection** – `AiFieldGenerationSelector`.
- **Support utilities** – `AiFileContext`, `AiGenerationKind`, `AiModelDefinitionSupport`.

#### Key units
- **`AiFieldGenerationConfig`** – routing rule: condition, model key, prompt key, priority, fallback/skip flags, oversize strategy, fact counters.
- **`AiCondition` / `AiConditionGroup` / `AiRangeCondition`** – tree of file‑matching predicates (extensions, size, lines, dates, path glob).
- **`AiConditionEvaluator`** – validates condition trees, tests file matches, detects line‑usage.
- **`AiFactExtractor`** – builds deterministic `label: count` blocks from regex counters.
- **`AiFactDefinitionSupport`** – resolves shared fact counter groups into rule configs.
- **`AiModelDefinitionSupport`** – maps model keys to fully‑configured `AiGenerationConfig` instances.
- **`AiGenerationConfig` / `AiCalibration`** – AI inference parameters and timing calibration.
- **`AiFieldGenerationSelector`** – picks the best rule for a file, enforces fallback semantics.

#### Data flow
1. **Configuration phase**  
   - Maven reads XML into **`AiFieldGenerationConfig`**, **`AiFactDefinition`**, **`AiModelDefinition`** objects.  
   - `AiFactDefinitionSupport` injects fact counters into each rule.  
   - `AiModelDefinitionSupport` converts model definitions into ready‑to‑use `AiGenerationConfig` instances.

2. **Runtime phase**  
   - `AiFileContext` is created per source file (name, path, size, lines, modified time).  
   - `AiFieldGenerationSelector` iterates rules, using `AiConditionEvaluator` to test matches and determine the applicable rule.  
   - If a rule requires facts, `AiFactExtractor` generates the fact block from the file content.  
   - The selected rule’s `AiGenerationConfig` (including `AiCalibration`) drives the LlamaCPP inference engine.

#### Dependencies
- Lombok (`@ToString`) for concise POJOs.  
- `org.jspecify.annotations.Nullable` for optional fields.  
- `java.time` for date parsing in conditions.  
- `java.util.regex` in fact extraction.  
- Internal support: `AiSourceExcludeFilter`, `Java8CompatibilityHelper`.  
- External: Maven plugin framework (not shown) for configuration binding.

#### Cross‑cutting
- All configuration classes are plain JavaBeans with getters/setters, enabling Maven’s XML‑to‑object binding.  
- Validation is centralized in `AiConditionEvaluator` and `AiFieldGenerationSelector`.  
- Immutable value objects (`AiFileContext`, enums) ensure thread safety during rule evaluation.  
- Mutable runtime objects (`AiGenerationConfig`, `AiCalibration`) are used only in single‑threaded plugin execution.
