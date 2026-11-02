### java/config
- H: 1.0
- C: 626FB0FF
- D: 2026-11-01T01:56:59Z
- T: 2026-11-01T02:06:04Z
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
> Provides AI‑powered source‑code generation and analysis, orchestrating configuration, condition evaluation, fact extraction, and oversized‑file handling for a Maven‑based code‑generation plugin.  

#### Purpose
- Central AI configuration holder for a source‑generation run.  
- Validates and evaluates file‑selection conditions.  
- Extracts deterministic facts for AI prompts.  
- Defines strategies for handling files that exceed model context limits.  

#### Responsibilities
- **Configuration Management** – `SrcMorphConfiguration`, `AiGenerationConfig`, `AiOversizeStrategy`.  
- **Condition Evaluation** – `AiConditionEvaluator`, `AiConditionGroup`.  
- **Fact Extraction** – `AiFactExtractor`.  
- **Strategy Definition** – `AiOversizeStrategy`.  

#### Key units
- **`SrcMorphConfiguration`** – mutable JavaBean holding all runtime settings (directories, file filters, AI provider, prompts, models, field rules, facts).  
- **`AiGenerationConfig`** – mutable bean exposing AI model knobs (context size, tokens, temperature, GPU/CPU layers, caching, etc.).  
- **`AiConditionEvaluator`** – stateless validator/evaluator for `AiCondition` trees against `AiFileContext`.  
- **`AiConditionGroup`** – container for child `AiCondition` objects used by `<and>`/`<or>` combinators.  
- **`AiFactExtractor`** – static utility producing a facts block from regex counters.  
- **`AiOversizeStrategy`** – enum of strategies (`FAIL`, `SAMPLE`, `MAP_REDUCE`, `DETERMINISTIC`) with parsing helpers.  

#### Data flow
1. **Configuration**: `SrcMorphConfiguration` is populated via Maven/JSON/YAML.  
2. **Condition Setup**: `AiConditionGroup` instances are built from config and passed to `AiConditionEvaluator`.  
3. **File Evaluation**: For each source file, `AiConditionEvaluator.matches()` decides inclusion; `usesLines()` flags line‑count needs.  
4. **Fact Extraction**: If a file is selected, `AiFactExtractor.factsBlock()` generates a deterministic facts header.  
5. **Oversize Handling**: `AiOversizeStrategy` determines how oversized files are processed (fail, sample, map‑reduce, deterministic).  

#### Dependencies
- **Internal**: `AiCondition`, `AiFileContext`, `AiFactCounter`, `AiPromptDefinition`, `AiModelDefinition`, `AiFieldGenerationConfig`, `AiFactDefinition`.  
- **External**: `java.time`, `java.util.regex`, `lombok`, `org.jspecify.annotations`, `net.ladenthin.srcmorph.support.AiSourceExcludeFilter`.  

#### Cross-cutting
- **Thread safety**: `AiConditionEvaluator` and `AiFactExtractor` are stateless and thread‑safe; configuration beans are mutable and intended for single‑threaded use.  
- **Validation**: `AiConditionEvaluator.validate()` and `AiFactExtractor.validate()` enforce configuration correctness, throwing `IllegalArgumentException` on errors.  
- **Immutability**: `AiOversizeStrategy` is an immutable enum; configuration lists are exposed as unmodifiable views.  
- **Error handling**: All public APIs surface configuration errors via unchecked exceptions; no checked exceptions are declared.  
- **Configuration defaults**: `AiGenerationConfig` and `SrcMorphConfiguration` provide sensible defaults and preserve them when `null` or empty values are supplied.
