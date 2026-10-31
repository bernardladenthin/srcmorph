### java/config
- H: 1.0
- C: 2F80D394
- D: 2026-10-30T16:34:09Z
- T: 2026-10-30T16:47:01Z
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
> Configures the srcmorph Maven plugin's AI-driven source documentation pipeline: source-file inclusion rules, per-step inference parameters, oversize-file handling strategies, and deterministic fact extraction for `.ai.md` generation.

#### Purpose
- Central configuration layer bridging Maven plugin XML/JSON/YAML binding to the AI provider implementations and file-filtering engine
- Holds all parameters for a srcmorph run: file selection, AI model routing, inference knobs, and oversize handling

#### Responsibilities
- **Source filtering**: Evaluate `AiCondition` trees (and/or/not combinators with extensions, size, lines, modification-date, and path-glob leaves) against `AiFileContext` facts to decide file inclusion via `AiConditionEvaluator`; bind XML `<conditions><condition>` children via `AiConditionGroup`
- **AI generation parameterization**: Carry per-step inference settings (GGUF model path, context size, sampling, GPU offload, KV cache, DRY penalties, stop strings, provider/baseUrl/apiKeyEnv) in `AiGenerationConfig`; hold top-level run settings (directories, subtrees, excludes, extensions, size bounds, force/planOnly, prompt/model/fact definitions, llama.cpp fallbacks, versions) in `SrcMorphConfiguration`
- **Oversize handling**: Parse `<onOversize>` tokens into `AiOversizeStrategy` enum values (`FAIL`, `SAMPLE`, `MAP_REDUCE`, `DETERMINISTIC`) to route files exceeding the model context window
- **Deterministic facts**: Generate exact whole-file regex match-count blocks via `AiFactExtractor` to prepend to AI summaries on the oversize path

#### Key units
- `SrcMorphConfiguration` — top-level mutable JavaBean with 23 fields binding Maven XML to engine runs (base/output directories, file filters, execution mode, provider, prompt/model/fact definition lists, llama.cpp fallback params, plugin/ai versions)
- `AiGenerationConfig` — per-step inference JavaBean with 45 settable fields and 42 `DEFAULT_*` constants (model path, context/output tokens, temperature/top-p/top-k/min-p, GPU layers, KV cache types, batch sizes, DRY params, stop strings, remote provider settings)
- `AiConditionEvaluator` — stateless evaluator walking `AiCondition` trees against `AiFileContext` to produce boolean inclusion decisions; validates tree structure and signals `usesLines` for lazy content reading
- `AiConditionGroup` — mutable container for child `AiCondition` lists inside AND/OR combinators, bound from `<conditions><condition>` XML elements
- `AiOversizeStrategy` — enum parsed via `fromConfig` from config tokens; `DEFAULT` is `FAIL`
- `AiFactExtractor` — utility computing `factsBlock` strings of exact regex match counts from `AiFactCounter` patterns and validating counter configuration

#### Data flow
- Maven plugin XML → `SrcMorphConfiguration` setters → engine planning (respecting `planOnly`)
- `SrcMorphConfiguration` references (`aiDefinitions`, `fieldGenerations`, `factDefinitions`) feed `AiGenerationConfig` and condition trees
- `AiConditionEvaluator.matches` consumes `AiCondition` trees + `AiFileContext` (fileName, sizeBytes, lineCount, lastModifiedEpochMilli, relativePath) → boolean inclusion; `usesLines` triggers file-content reading only when needed
- `AiOversizeStrategy.fromConfig` selects oversize path; on `DETERMINISTIC` or oversize AI paths, `AiFactExtractor.factsBlock` prepends deterministic counts to model output

#### Dependencies
- Internal same-package: `AiCondition`, `AiConditionGroup`, `AiFileContext`, `AiRangeCondition`, `AiCalibration`, `AiModelDefinition`, `AiFieldGenerationConfig`, `AiFactDefinition`, `AiFactCounter`
- Internal cross-package: `net.ladenthin.srcmorph.support.AiSourceExcludeFilter` (path-glob matching), `net.ladenthin.srcmorph.prompt.AiPromptDefinition`
- JDK: `java.time.Instant`, `java.time.format.DateTimeParseException`, `java.util.regex.Pattern/Matcher/PatternSyntaxException`, `java.io.File`, `java.util.List/ArrayList/Collection/Collections`
- Libraries: Lombok `@ToString`, `org.jspecify.annotations.Nullable`
- Binding surfaces: Maven plexus reflection, Jackson/YAML (future), direct Java calls

#### Cross-cutting
- Null-tolerance: `@Nullable` fields/setters throughout; null inputs reset to `DEFAULT_*` constants or empty lists (notably in `AiGenerationConfig` and `SrcMorphConfiguration`)
- `@SuppressWarnings({"NullAway.Init", "initialization.fields.uninitialized"})` on mutable JavaBeans with uninitialized fields
- Defensive copy on write for collections (`AiConditionGroup.setConditions`); unmodifiable views on read (`AiGenerationConfig` list getters)
- Fail-fast validation: `AiConditionEvaluator.validate` and `AiFactExtractor.validate` throw `IllegalArgumentException` on misconfiguration (invalid ranges, blank globs, bad regex, unparseable ISO-8601 dates) before evaluation
- Thread-safety split: stateless thread-safe utilities (`AiConditionEvaluator`, `AiFactExtractor`) vs. mutable single-threaded JavaBeans (`SrcMorphConfiguration`, `AiGenerationConfig`, `AiConditionGroup`)
- Lombok `@ToString` on config beans for build-log diagnostics
- Size-bound semantics: `minFileSizeBytes` exclusive lower bound, `maxFileSizeBytes` inclusive upper bound (`<=0` disables)
