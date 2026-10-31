### java/config
- H: 1.0
- C: 8E572D64
- D: 2026-10-31T10:13:39Z
- T: 2026-10-31T10:17:43Z
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
> AI documentation run configuration, file filtering, and oversize handling for source-code summarization.
#### Purpose
- Central configuration and gating for AI-powered `.ai.md` generation runs: file scope, model/prompt routing, generation parameters, and oversize fallback.
#### Responsibilities
- Run-scope config: `SrcMorphConfiguration` holds base/output dirs, subtrees/excludes/extensions, size bounds, provider, prompts/models, field/fact rules, llama fallback, provenance headers, force/planOnly.
- Inference tuning: `AiGenerationConfig` carries sampling, KV-cache, GPU-offload, batching, DRY, reasoning, trimming, provider-routing defaults with `DEFAULT_*` sentinels.
- File selection: `AiConditionEvaluator` + `AiConditionGroup` match `AiFileContext` by extension/size/lines/modifiedAfter/modifiedBefore/pathGlob with and/or/not trees.
- Oversize policy: `AiOversizeStrategy` — `FAIL`/`SAMPLE`/`MAP_REDUCE`/`DETERMINISTIC` parsed via `fromConfig` from `<onOversize>`.
- Exact facts: `AiFactExtractor` builds `**Facts (exact, whole file):**` `label: count` block from `AiFactCounter` regexes, validates patterns fail-fast.
#### Key units
- `SrcMorphConfiguration` — root mutable JavaBean mirroring Maven `@Parameter` names, drives generation engine.
- `AiGenerationConfig` — single-step LLM parameters for local llama.cpp and remote OpenAI-compatible providers.
- `AiConditionEvaluator` — stateless `matches`/`validate`/`usesLines` over `AiCondition` trees.
- `AiConditionGroup` — nullable `conditions` list backing `<and>`/`<or>` Maven `<condition>` binding.
- `AiOversizeStrategy` — immutable enum with `configValue()`/`fromConfig()` and `DEFAULT`.
- `AiFactExtractor` — static `factsBlock`/`validate`/`countMatches` with `FACTS_HEADER`/`ENTRY_SEPARATOR`/`FACTS_SUFFIX`.
#### Data flow
- Maven/XML/JSON/YAML → `SrcMorphConfiguration`/`AiGenerationConfig` setters → planner routes per `AiFieldGenerationConfig`/`AiModelDefinition`/`AiPromptDefinition`.
- `AiFileContext` facts → `AiConditionEvaluator.matches` → include/exclude decision, `usesLines` signals line-count need.
- Oversize source → `AiOversizeStrategy.fromConfig` selects fail/trim/chunk/deterministic path within context window.
- Full source + `AiFactCounter`/`AiFactDefinition` → `AiFactExtractor.factsBlock` prepended to AI body.
#### Dependencies
- Internal: `AiCondition`, `AiRangeCondition`, `AiFileContext`, `AiSourceExcludeFilter`, `AiFactCounter`, `AiFactDefinition`, `AiModelDefinition`, `AiFieldGenerationConfig`, `AiPromptDefinition`, `AiCalibration`.
- External: `java.time.Instant`, `java.util.regex.Pattern/Matcher`, `java.io.File`, `java.util.List/Collections`, Lombok `@ToString`, `org.jspecify.annotations.Nullable`.
#### Cross-cutting
- Mutable non-thread-safe JavaBeans (`SrcMorphConfiguration`, `AiGenerationConfig`) with `DEFAULT_*` constants, null-coerced setters, defensive list copies; stateless thread-safe helpers (`AiConditionEvaluator`, `AiFactExtractor`, `AiOversizeStrategy`).
- Fail-fast validation via `IllegalArgumentException`: condition branch-count/bounds/glob/date checks, fact label/pattern/`PatternSyntaxException` wrapping, unknown `onOversize` token; null-tolerant matching and facts skipping.
