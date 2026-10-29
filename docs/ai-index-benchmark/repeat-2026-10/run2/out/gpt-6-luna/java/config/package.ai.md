### java/config
- H: 1.0
- C: 2C867AC0
- D: 2026-10-29T13:48:14Z
- T: 2026-10-29T13:51:23Z
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
> Defines source-selection, AI generation, model-routing, and oversized-file policies for producing project source summaries and metadata.

#### Purpose
- Provides mutable configuration objects and validation helpers for source filtering, fact extraction, generation providers, and output metadata.
- Defines condition evaluation and oversized-file behavior used by configured generation workflows.

#### Responsibilities
- **Source selection:** `AiConditionEvaluator` validates and evaluates nested file conditions; `AiConditionGroup` stores Maven-bindable `<and>` / `<or>` children; `SrcMorphConfiguration` holds source scopes, exclusions, extensions, and size bounds.
- **Generation settings and routing:** `AiGenerationConfig` carries provider, model, prompt-limit, sampling, reasoning, stop, caching, and inference-hardware options; `SrcMorphConfiguration` stores shared prompt, model, fact, and field-generation definitions plus fallback llama.cpp settings.
- **Oversize handling:** `AiOversizeStrategy` parses policies for failing, sampling, map-reduce summarization, or deterministic output.
- **Whole-file facts:** `AiFactExtractor` validates regex counters and formats exact match totals for inclusion with generated summaries.
- **Output metadata:** `SrcMorphConfiguration` carries output directory, project name, and generator/AI versions.

#### Key units
- `SrcMorphConfiguration` — central JavaBean for source filters, generation definitions, fallback model settings, and output metadata.
- `AiGenerationConfig` — per-generation-step provider and inference configuration, with defaults and optional calibration.
- `AiConditionEvaluator` — validates condition trees and matches them against `AiFileContext` facts.
- `AiConditionGroup` — stores child `AiCondition` entries for Maven combinators.
- `AiFactExtractor` — validates `AiFactCounter` definitions and builds a formatted exact-count facts block.
- `AiOversizeStrategy` — exposes `FAIL`, `SAMPLE`, `MAP_REDUCE`, and `DETERMINISTIC`, with configuration-token parsing.

#### Data flow
- Source configuration supplies scope, exclusions, extensions, size limits, and optional condition definitions; `AiConditionEvaluator` validates and applies condition trees to file context.
- Configured `AiFactCounter` regexes are validated and run across full source text; matching totals are formatted as a facts block.
- Generation settings and routing definitions provide provider/model parameters and oversize policy for downstream summary generation.
- Output configuration supplies destination and project/version metadata for generated artifacts.

#### Dependencies
- Condition evaluation uses `AiCondition`, `AiConditionGroup`, `AiRangeCondition`, `AiFileContext`, and `AiSourceExcludeFilter`.
- Configuration references `AiCalibration`, `AiPromptDefinition`, `AiModelDefinition`, `AiFieldGenerationConfig`, and `AiFactDefinition`; fact extraction uses `AiFactCounter`.
- Java collections, regex, time, and file APIs; Lombok `@ToString`; JSpecify `@Nullable`.
- `AiGenerationConfig` documents llama.cpp and OpenAI-compatible chat-completion settings.

#### Cross-cutting
- Configuration classes are mutable JavaBeans; no synchronization or thread-safety guarantees are stated.
- `AiConditionEvaluator`, `AiFactExtractor`, and `AiOversizeStrategy` are stateless; validation failures generally surface as `IllegalArgumentException`.
- Defaults include `FAIL` for oversize handling, `mock` as the shared generation provider, and configured llama.cpp generation defaults; nullable fields are used for optional model paths, calibration, and definitions.
