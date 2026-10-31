### java/config
- H: 1.0
- C: B56E33BA
- D: 2026-10-31T09:32:09Z
- T: 2026-10-31T09:36:39Z
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
> Configures and evaluates the AI-driven source-documentation pipeline in srcmorph: which files get summarized, how oversize files are handled, how fact counts and file-selection conditions are evaluated, and how generation settings reach the AI providers.

#### Purpose
- Holds all run parameters (source selection, size filters, provider routing, llama.cpp fallbacks, output metadata) for srcmorph AI documentation generation.
- Decides per-file whether an AI condition tree selects it, validates that configuration fail-fast, and builds deterministic regex fact counts prepended to summaries.

#### Responsibilities
- Configuration model: `SrcMorphConfiguration` is the shared JavaBean; `AiGenerationConfig` carries per-model llama.cpp/remote generation knobs with `DEFAULT_*` constants.
- File-selection logic: `AiConditionEvaluator` evaluates nested `AiConditionGroup`/`AiCondition` trees (and/or/not, extensions, size, lines, modified dates, path globs) against `AiFileContext`, and validates them.
- Deterministic fact extraction: `AiFactExtractor` counts regex matches over full source and validates `AiFactCounter` lists; output is a `FACTS_HEADER` block.
- Oversize policy: `AiOversizeStrategy` enumerates FAIL, SAMPLE, MAP_REDUCE, DETERMINISTIC and parses the `<onOversize>` token.

#### Key units
- `SrcMorphConfiguration`: shared run configuration bean; Maven, JSON/YAML, and plain-Java binding; path, routing, and size contracts.
- `AiGenerationConfig`: per-model generation settings bean; sampling, GPU/CPU placement, KV cache, DRY, remote provider routing; `DEFAULT_THREADS`.
- `AiConditionEvaluator`: stateless evaluator; `matches`, `validate`, `usesLines` over `AiCondition` trees.
- `AiConditionGroup`: child-list wrapper for and/or combinators; exists to get `<condition>` Maven item tags.
- `AiFactExtractor`: static utility; `factsBlock`, `validate`, `countMatches`; constants `FACTS_HEADER`, `LABEL_COUNT_SEPARATOR`, `ENTRY_SEPARATOR`, `FACTS_SUFFIX`.
- `AiOversizeStrategy`: enum with `configValue()` and `fromConfig(String)`; `DEFAULT` equals FAIL.

#### Data flow
- Maven/YAML/JSON bind into `SrcMorphConfiguration`; llama* fields act as fallback only when `fieldGenerations` is empty; per-model settings flow to `AiGenerationConfig`.
- Source file facts (`AiFileContext`) are tested by `AiConditionEvaluator.matches` against `AiCondition` trees; `usesLines` signals whether line counting is needed.
- Oversize files route through `AiOversizeStrategy`: SAMPLE trims to a window; MAP_REDUCE chunks at line boundaries and combines partials; DETERMINISTIC skips the model and emits size, line count, and head/tail sample; FAIL aborts.
- Full source text is passed to `AiFactExtractor.factsBlock`, whose output is prepended to summaries on the oversize path.

#### Dependencies
- Internal: `AiCondition`, `AiRangeCondition`, `AiFileContext`, `AiFactCounter`, `AiFactDefinition`, `AiModelDefinition`, `AiFieldGenerationConfig`, `AiPromptDefinition`, `AiSourceExcludeFilter` (`net.ladenthin.srcmorph.support`).
- External: `java.time.Instant`, `java.util.regex.*`, `java.io.File`, `java.util.Collections`.
- Lombok `@ToString`; JSpecify `@Nullable`.
- Consumers outside package: Maven plugin parameter binding, Jackson ObjectMapper/YAMLMapper, llama.cpp JNI and OpenAI-compatible providers (named as `generationProvider` values).

#### Cross-cutting
- Mutable JavaBeans with getters/setters; Maven-name alignment for XML and YAML keys; `@SuppressWarnings({"NullAway.Init", "initialization.fields.uninitialized"})` on beans.
- JSpecify `@Nullable` on nullable fields, parameters, and returns; null means "not configured" or engine default.
- Sentinel conventions: `-1` means "do not set" (GPU layers, batch sizes, seed); `0` is meaningful for several knobs; `<= 0` disables size bounds.
- Fail-fast validation: `IllegalArgumentException` from `AiConditionEvaluator.validate`, `AiFactExtractor.validate`, `AiOversizeStrategy.fromConfig`; `PatternSyntaxException` wrapped.
- Stateless static/utility classes are thread-safe; all bean classes are not thread-safe and lack synchronization.
- Default values: provider `mock`, output `src/site/ai`, context 2048, max output 128, temperature 0.15, AI version `0.0.0`.
