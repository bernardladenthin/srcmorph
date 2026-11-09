### java/config
- H: 1.0
- C: 6E389E7C
- D: 2026-11-04T07:09:59Z
- T: 2026-11-04T07:13:08Z
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
> Defines source-indexing and AI-generation configuration, including file-selection rules, model routing, exact source facts, and oversized-file handling for generated project documentation.

#### Purpose
- Centralizes configuration and utility logic for selecting source files, validating AI rules, and preparing generation settings.
- Supplies defaults and parsing/formatting helpers for AI generation, fact extraction, and oversized-source policies.

#### Responsibilities
- **File-selection rules:** `AiConditionEvaluator` validates and evaluates nested conditions over file extensions, size, line count, modification time, and relative-path globs; `AiConditionGroup` stores Maven-bindable `and`/`or` children.
- **Generation configuration:** `AiGenerationConfig` carries model, provider, prompt limits, sampling, reasoning, caching, hardware, DRY, stop-string, and calibration settings; `SrcMorphConfiguration` holds run-level scope, routing, fallback inference, and output metadata.
- **Facts and oversized files:** `AiFactExtractor` validates regex counters and formats exact whole-source match counts; `AiOversizeStrategy` parses configured policies for oversized source handling.

#### Key units
- `AiConditionEvaluator` — validates condition trees, detects line-count requirements, and matches conditions against `AiFileContext`.
- `AiConditionGroup` — stores child `AiCondition` entries for Maven combinators.
- `AiFactExtractor` — validates `AiFactCounter` definitions and formats exact counts into a facts block.
- `AiGenerationConfig` — mutable per-generation settings bean with defaults for local and OpenAI-compatible providers.
- `AiOversizeStrategy` — enum mapping `fail`, `sample`, `mapReduce`, and `deterministic` tokens to oversized-file policies; defaults to `FAIL`.
- `SrcMorphConfiguration` — mutable run-level configuration for source scope, AI definitions and routing, fallback settings, and `.ai.md` output metadata.

#### Data flow
- `AiConditionEvaluator` validates configured `AiCondition` trees, detects whether line counts are needed, then evaluates file facts from `AiFileContext`.
- `AiFactExtractor` validates counters and scans complete source text with each configured regex, formatting reportable counts as a facts block.
- Configuration beans carry run-level and generation-level settings to consumers; `AiOversizeStrategy.fromConfig` converts configured tokens into policy enum values.

#### Dependencies
- Internal rule and generation types: `AiCondition`, `AiRangeCondition`, `AiFileContext`, `AiFactCounter`, `AiCalibration`, `AiPromptDefinition`, `AiModelDefinition`, `AiFieldGenerationConfig`, and `AiFactDefinition`.
- Java time, collection, and regex utilities; Lombok `@ToString`; JSpecify `@Nullable`.
- `AiConditionEvaluator` also depends on `AiSourceExcludeFilter`; `AiGenerationConfig` uses `Runtime` to cap its default inference-thread count.

#### Cross-cutting
- Configuration classes are mutable, unsynchronized JavaBeans; `AiConditionEvaluator` and `AiFactExtractor` are stateless.
- Validation rejects malformed condition trees, invalid timestamps, missing fact labels or patterns, and invalid regexes; invalid nonblank oversize tokens raise `IllegalArgumentException`.
- Nullable inputs represent unspecified settings in several configuration types; list getters in `AiGenerationConfig` expose unmodifiable views.
