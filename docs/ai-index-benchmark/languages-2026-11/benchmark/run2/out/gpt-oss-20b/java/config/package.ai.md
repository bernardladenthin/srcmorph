### java/config
- H: 1.0
- C: A0F2D5E6
- D: 2026-11-01T01:35:52Z
- T: 2026-11-01T01:42:44Z
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
> Provides a configurable AI‑powered source‑code transformation engine that evaluates file‑level conditions, extracts deterministic facts, and orchestrates model‑generation parameters for Maven‑based projects.  

#### Purpose
- Central configuration holder for AI‑generation and source‑morphing tasks.  
- Validates and evaluates AI filter trees against file metadata.  
- Generates deterministic fact blocks for oversized files.  

#### Responsibilities
- **Configuration Management** – `SrcMorphConfiguration` aggregates all runtime settings (paths, providers, prompts, models, field rules, facts).  
- **Condition Evaluation** – `AiConditionEvaluator` validates and matches `AiCondition` trees against `AiFileContext`.  
- **Fact Extraction** – `AiFactExtractor` builds a deterministic facts header from regex counters.  
- **Oversize Handling** – `AiOversizeStrategy` defines strategies for files exceeding context limits.  
- **Generation Settings** – `AiGenerationConfig` stores per‑run AI provider parameters (sampling, caching, GPU, etc.).  

#### Key units
- **`SrcMorphConfiguration`** – mutable bean for Maven/JSON/YAML configuration.  
- **`AiConditionEvaluator`** – stateless validator/evaluator for `AiCondition` trees.  
- **`AiConditionGroup`** – container for nested logical conditions.  
- **`AiFactExtractor`** – utility that counts regex matches and formats a facts block.  
- **`AiOversizeStrategy`** – enum of oversize handling modes (FAIL, SAMPLE, MAP_REDUCE, DETERMINISTIC).  
- **`AiGenerationConfig`** – JavaBean holding all AI‑generation parameters for a single run.  

#### Data flow
1. **Configuration Load** – `SrcMorphConfiguration` is populated via Maven plugin, Jackson, or code.  
2. **Condition Validation** – `AiConditionEvaluator.validate()` checks each `AiCondition` tree from the config.  
3. **File Matching** – For each source file, `AiConditionEvaluator.matches()` decides if the file should be processed.  
4. **Oversize Decision** – If a file exceeds the model’s context window, `AiOversizeStrategy` selects handling logic.  
5. **Fact Generation** – `AiFactExtractor.factsBlock()` produces a deterministic facts header for oversized files.  
6. **Generation Execution** – `AiGenerationConfig` supplies all parameters to the AI provider for generating transformed source.  

#### Dependencies
- **Internal**: `AiCondition`, `AiFactCounter`, `AiPromptDefinition`, `AiModelDefinition`, `AiFieldGenerationConfig`, `AiFactDefinition`.  
- **External**: `java.time`, `java.util.regex`, `lombok`, `org.jspecify.annotations`.  
- **Provider‑specific**: AI model libraries (e.g., llama.cpp, remote APIs) referenced via `AiGenerationConfig`.  

#### Cross‑cutting
- **Thread‑Safety**: `AiConditionEvaluator` and `AiFactExtractor` are stateless and thread‑safe; other beans are mutable and intended for single‑thread use.  
- **Validation**: All configuration objects expose `validate()` or similar checks to enforce non‑null labels, regex compilation, and logical consistency.  
- **Immutability**: `AiOversizeStrategy` is an immutable enum; `AiConditionGroup` defensively copies collections.  
- **Error Handling**: Configuration errors surface as `IllegalArgumentException` or `IllegalStateException`; parsing errors propagate from `DateTimeParseException` or `PatternSyntaxException`.  
- **Configuration Defaults**: `AiGenerationConfig` supplies default constants for all fields, resetting to defaults when `null` or empty values are set.  

This package equips a Maven‑based build to apply AI‑driven transformations to source files, with fine‑grained control over file selection, fact extraction, and model‑generation parameters.
