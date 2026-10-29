### java/config
- H: 1.0
- C: 693B5E2E
- D: 2026-10-28T21:24:22Z
- T: 2026-10-28T22:52:53Z
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
> A configuration‑driven AI‑powered source‑code transformation engine that validates file‑level filters, extracts deterministic code‑facts, and orchestrates AI model calls with fine‑grained generation settings.

#### Purpose
- Centralize AI‑based source‑code processing configuration.
- Validate and evaluate file‑selection rules.
- Generate deterministic fact blocks for oversized files.
- Expose mutable settings for AI generation and source‑morph execution.

#### Responsibilities
- **Configuration Management** – `SrcMorphConfiguration` aggregates all runtime parameters, binding from Maven, JSON/YAML, or code.
- **AI Condition Handling** – `AiConditionEvaluator` validates and evaluates logical trees (`and`, `or`, `not`) against file metadata; `AiConditionGroup` stores child conditions for combinators.
- **Fact Extraction** – `AiFactExtractor` counts regex matches in source text to prepend deterministic facts to AI prompts.
- **Oversize Strategy** – `AiOversizeStrategy` enumerates handling modes for files exceeding size limits.
- **Generation Settings** – `AiGenerationConfig` holds all AI provider parameters (model path, sampling, retries, stop strings, etc.) and exposes a JavaBean API.

#### Key units
- `SrcMorphConfiguration` – mutable JavaBean for run‑time settings.
- `AiConditionEvaluator` – stateless validator/evaluator of `AiCondition` trees.
- `AiConditionGroup` – container for child `AiCondition` objects.
- `AiFactExtractor` – utility producing a facts block from `AiFactCounter` list.
- `AiOversizeStrategy` – enum with `FAIL`, `SAMPLE`, `MAP_REDUCE`, `DETERMINISTIC`.
- `AiGenerationConfig` – mutable holder for AI provider parameters (model path, temperature, top‑k, stop strings, etc.).

#### Data flow
1. **Configuration Load** → `SrcMorphConfiguration` populated via binding frameworks.  
2. **File Selection** → `AiConditionEvaluator` receives `AiCondition` tree and `AiFileContext`; returns match boolean and whether line counting is needed.  
3. **Fact Generation** (if oversized) → `AiFactExtractor.factsBlock` consumes `AiFactCounter` list and source text, producing a deterministic facts header.  
4. **AI Prompt Construction** → facts header + user prompt → `AiGenerationConfig` supplies generation parameters to the AI provider.  
5. **Oversize Handling** → `AiOversizeStrategy` dictates whether to fail, sample, map‑reduce, or deterministically process oversized files.

#### Dependencies
- **Internal**: `AiCondition`, `AiFileContext`, `AiFactCounter`, `AiPromptDefinition`, `AiModelDefinition`, `AiFieldGenerationConfig`, `AiFactDefinition`.  
- **External**: `java.time`, `java.util`, `java.util.regex`, `net.ladenthin.srcmorph.support.AiSourceExcludeFilter`, `org.jspecify.annotations`, `lombok.ToString`.  
- **AI Providers**: configured via `AiGenerationConfig` (model path, remote URL, API key env, etc.).

#### Cross‑cutting
- **Thread Safety**: `AiConditionEvaluator`, `AiFactExtractor`, and `AiOversizeStrategy` are stateless and thread‑safe; configuration classes are mutable and not thread‑safe.  
- **Validation**: `AiConditionEvaluator.validate` ensures well‑formed trees; `AiFactExtractor.validate` checks counter integrity.  
- **Error Handling**: `IllegalArgumentException` for misconfigurations; `IllegalStateException` for invalid node states.  
- **Immutability**: `AiConditionGroup` defensively copies collections; `AiOversizeStrategy` is an enum.  
- **Configuration Defaults**: `AiGenerationConfig` supplies sensible defaults for all AI parameters; `SrcMorphConfiguration` provides defaults for directories, provider, and model settings.
