### java/config
- H: 1.0
- C: 6E389E7C
- D: 2026-11-01T14:58:32Z
- T: 2026-11-01T15:07:47Z
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
> A configuration‑driven AI source‑generation engine that evaluates file‑level filters, extracts deterministic facts, and orchestrates model‑specific generation settings for a Maven‑based code‑generation workflow.  

#### Purpose
- Provide a unified, mutable configuration for AI‑powered source morphing.
- Validate and evaluate AI filter trees against file metadata.
- Extract deterministic fact blocks from source files.
- Define oversize handling strategies for model context limits.

#### Responsibilities
- **Configuration Management** – `SrcMorphConfiguration`, `AiGenerationConfig`, and related data classes expose all runtime parameters.
- **Condition Evaluation** – `AiConditionEvaluator` validates and evaluates logical condition trees (`AiCondition`, `AiConditionGroup`).
- **Fact Extraction** – `AiFactExtractor` builds fact blocks from regex counters.
- **Oversize Strategy** – `AiOversizeStrategy` enumerates handling modes for oversized source files.

#### Key units
- `SrcMorphConfiguration` – central mutable config holder for the plugin/CLI.
- `AiGenerationConfig` – AI model and prompt‑generation parameters.
- `AiConditionEvaluator` – recursive evaluator for AI filter trees.
- `AiConditionGroup` – container for child conditions in logical groups.
- `AiFactExtractor` – static utility for deterministic fact counting.
- `AiOversizeStrategy` – enum for oversize handling strategies.

#### Data flow
1. **Configuration**: `SrcMorphConfiguration` is populated (Maven, JSON/YAML, or code) and supplies `AiGenerationConfig` and other AI definitions.  
2. **File Selection**: `AiConditionEvaluator` receives an `AiCondition` tree and `AiFileContext` for each source file, returning a match flag and whether line counts are needed.  
3. **Fact Extraction**: For matched files, `AiFactExtractor.factsBlock` generates a deterministic fact string using configured `AiFactCounter`s.  
4. **Generation**: `AiGenerationConfig` supplies model parameters to the AI provider; oversize handling is chosen via `AiOversizeStrategy`.

#### Dependencies
- **Internal**: `AiCondition`, `AiConditionGroup`, `AiFactCounter`, `AiFactDefinition`, `AiPromptDefinition`, `AiModelDefinition`, `AiFieldGenerationConfig`.  
- **External**: `net.ladenthin.srcmorph.support.AiSourceExcludeFilter`, `java.time`, `java.util.regex`, Lombok annotations, `org.jspecify.annotations`.

#### Cross‑cutting
- **Thread safety**: All evaluator and extractor utilities are stateless; configuration objects are mutable but intended for single‑threaded use.  
- **Validation**: `AiConditionEvaluator.validate` and `AiFactExtractor.validate` enforce structural correctness and regex syntax.  
- **Configuration defaults**: `AiGenerationConfig` and `SrcMorphConfiguration` provide sensible defaults and reset null inputs to defaults.  
- **Error handling**: Validation errors surface as `IllegalArgumentException` or `IllegalStateException`; no checked exceptions are thrown.  
- **Immutability**: `AiOversizeStrategy` is an immutable enum; lists in config classes are exposed as unmodifiable views.
