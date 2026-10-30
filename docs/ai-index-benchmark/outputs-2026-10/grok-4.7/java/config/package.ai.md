### java/config
- H: 1.0
- C: F42640AF
- D: 2026-10-30T00:29:58Z
- T: 2026-10-30T00:45:31Z
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
Synthesizing a package summary from the provided per-file summaries.> Configures and evaluates which sources a srcmorph run indexes, how oversized files are treated, and which model, sampling, and exact-fact settings drive `.ai.md` generation.

#### Purpose
- Holds Maven-bindable beans for a srcmorph run: source filters, model routing inputs, llama.cpp and OpenAI-compatible knobs, and header metadata.
- Evaluates `AiCondition` trees against file facts and builds model-free whole-file fact lines before AI summaries.

#### Responsibilities
- Run configuration: `SrcMorphConfiguration` stores roots, excludes, size bounds, provider, prompts, models, field routing, facts, llama fallbacks, and `.ai.md` header versions.
- Per-model generation: `AiGenerationConfig` carries sampling, KV cache, GPU/CPU layers, DRY, stop strings, remote endpoint, and optional `AiCalibration`.
- Source-filter evaluation: `AiConditionEvaluator` matches and validates condition trees; `AiConditionGroup` holds nested `<and>`/`<or>` children.
- Oversize policy: `AiOversizeStrategy` parses `<onOversize>` into abort, head-sample, map-reduce, or model-free deterministic output.
- Exact facts: `AiFactExtractor` turns `AiFactCounter` regexes into a deterministic `label: count` block.

#### Key units
- `SrcMorphConfiguration` — mutable shared bean for one srcmorph run, bindable from Maven or plain Java.
- `AiGenerationConfig` — one model definition’s inference, trim, stop, DRY, and endpoint settings.
- `AiConditionEvaluator` — walks `AiCondition` trees against `AiFileContext`; fail-fast `validate`; `usesLines` reports line-count need.
- `AiConditionGroup` — `conditions` list so Maven binds nested combinator children.
- `AiOversizeStrategy` — `FAIL`, `SAMPLE`, `MAP_REDUCE`, `DETERMINISTIC`; `fromConfig` defaults blank to `FAIL`.
- `AiFactExtractor` — static `factsBlock` / `validate` for whole-file regex counts.

#### Data flow
- Plugin or caller populates `SrcMorphConfiguration` (provider, `aiDefinitions` as `AiModelDefinition`, `fieldGenerations`, `factDefinitions`, llama fallbacks, excludes, size bounds).
- Relative `modelPath` / `llamaModelPath` resolve against `modelDirectory`; empty `fieldGenerations` falls back to llama numeric fields and `llamaModelPath`.
- Routing rules carry `<onOversize>` parsed by `AiOversizeStrategy.fromConfig`; oversized files abort, head-sample, map-reduce, or emit metadata plus head/tail.
- `AiConditionEvaluator.validate` rejects bad trees; `matches` tests `fileName`, `sizeBytes`, `lineCount`, `lastModifiedEpochMilli`, and `relativePath`; `usesLines` decides whether contents must be read.
- `AiFactExtractor.validate` checks counters; `factsBlock` prepends exact counts to an oversize AI summary when counters remain after skips.

#### Dependencies
- Same package: `AiCondition`, `AiConditionGroup`, `AiRangeCondition`, `AiFileContext`, `AiFactCounter`, `AiCalibration`, `AiModelDefinition`, `AiFieldGenerationConfig`, `AiFactDefinition`, `AiGenerationConfig`.
- External: `AiSourceExcludeFilter`, `AiPromptDefinition`; `Instant`, `Pattern`/`Matcher`, `File`, `List`/`ArrayList`/`Collections`.
- Javadoc-only on generation config: `ModelParameters`, `InferenceParameters`, `FlashAttn`, `LlamaCppJniAiGenerationProvider`.
- Providers named in config: `llamacpp-jni`, `openai-compatible`, `mock`; default provider `"mock"`; default remote base `https://openrouter.ai/api/v1`.

#### Cross-cutting
- Maven JavaBeans: no-arg constructors, setters, Lombok `@ToString`; `equals`/`hashCode` omitted on `AiGenerationConfig`.
- `@SuppressWarnings({"NullAway.Init", "initialization.fields.uninitialized"})` on mutable config holders; `@Nullable` on optional lists and strings.
- Fail-fast `IllegalArgumentException` on bad conditions, unparseable ISO-8601 dates, unknown `<onOversize>`, and invalid fact patterns; `matches` throws `IllegalStateException` if no branch is set.
- Sentinel `-1` and empty strings mean “do not forward / leave binding default” on many generation knobs; size bounds `<= 0` disable or unlimit file filters.
- Stateless evaluator and static fact extractor are shareable; config beans are mutable, unsynchronized, and not thread-safe. List setters on `AiGenerationConfig` retain caller lists; getters of breakers/stops return unmodifiable views.
