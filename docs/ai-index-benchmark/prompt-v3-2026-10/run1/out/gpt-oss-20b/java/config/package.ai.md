### java/config
- H: 1.0
- C: FE9A68C7
- D: 2026-10-31T19:47:56Z
- T: 2026-10-31T19:55:32Z
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
> A Maven‑style configuration and evaluation framework for AI‑driven source‑code generation, providing rule‑based file selection, fact extraction, and runtime AI generation settings.  

#### Purpose
- Centralize AI generation configuration for srcmorph runs.  
- Evaluate file‑selection conditions and extract deterministic facts.  

#### Responsibilities
- **Configuration Management** – `SrcMorphConfiguration`, `AiGenerationConfig`, `AiOversizeStrategy`.  
- **Condition Evaluation** – `AiConditionEvaluator`, `AiConditionGroup`.  
- **Fact Extraction** – `AiFactExtractor`.  

#### Key units
- `SrcMorphConfiguration` – mutable bean holding all runtime settings (paths, provider, prompts, models, routing rules).  
- `AiGenerationConfig` – AI provider parameters (model path, sampling, caching, GPU/CPU settings).  
- `AiOversizeStrategy` – enum of oversize handling modes (`FAIL`, `SAMPLE`, `MAP_REDUCE`, `DETERMINISTIC`, `DEFAULT`).  
- `AiConditionEvaluator` – stateless validator and matcher for `AiCondition` trees against `AiFileContext`.  
- `AiConditionGroup` – container for child `AiCondition` objects in `<and>/<or>` groups.  
- `AiFactExtractor` – utility that builds a facts block from a list of `AiFactCounter` regexes.  

#### Data flow
1. **Configuration**: `SrcMorphConfiguration` is populated via Maven, JSON/YAML, or code.  
2. **Routing**: `AiConditionEvaluator` checks each source file against the configured `AiCondition` trees to decide inclusion.  
3. **Fact Generation**: For selected files, `AiFactExtractor` produces a deterministic facts string using regex counters.  
4. **AI Generation**: `AiGenerationConfig` supplies runtime parameters to the AI provider, which consumes the facts and source snippets.  

#### Dependencies
- Java core (`java.util`, `java.time`, `java.io`, `java.util.regex`).  
- Lombok (`@ToString`).  
- `org.jspecify.annotations.Nullable`.  
- `net.ladenthin.srcmorph.support.AiSourceExcludeFilter`.  
- Prompt and model classes (`AiPromptDefinition`, `AiModelDefinition`, `AiFactDefinition`, `AiFieldGenerationConfig`).  

#### Cross‑cutting
- **Thread safety**: All evaluator and extractor classes are stateless; configuration beans are mutable but intended for single‑threaded use.  
- **Validation**: `AiConditionEvaluator.validate` and `AiFactExtractor.validate` enforce configuration correctness, throwing `IllegalArgumentException` on errors.  
- **Immutability**: `AiOversizeStrategy` is an immutable enum; lists returned by getters are unmodifiable.  
- **Null handling**: Setters replace `null` with defaults or empty collections to avoid `NullPointerException`.  

---
