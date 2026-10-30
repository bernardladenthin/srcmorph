### java/config
- H: 1.0
- C: F42640AF
- D: 2026-10-30T00:29:58Z
- T: 2026-10-30T00:32:41Z
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
> Configuration model and condition/fact/oversize rules that control how srcmorph selects, sizes, and summarises Java source files for AI-generated indexes.

#### Purpose
- Defines all run settings for AI-driven source summarisation, bindable from Maven, YAML/JSON, or code.
- Provides file-filter condition evaluation, fact counting, and oversize-handling policy used during indexing.

#### Responsibilities
- Root run configuration: `SrcMorphConfiguration` holds directories, file-size bounds, extensions, excludes, provider, and llama defaults; field names mirror Maven `@Parameter` names.
- Model generation settings: `AiGenerationConfig` carries sampling, batching, GPU, KV-cache, DRY, and remote-provider parameters with `DEFAULT_*` constants and `-1`/empty "do not send" sentinels.
- File selection rules: `AiConditionEvaluator` matches and validates nested `and`/`or`/`not` trees over extensions, size, lines, modification dates, and path globs.
- Condition containers: `AiConditionGroup` wraps child conditions as `<conditions><condition>` for combinators.
- Deterministic facts: `AiFactExtractor` builds regex-based `label: count` facts over full source text, prepended to oversize summaries.
- Oversize policy: `AiOversizeStrategy` maps `<onOversize>` tokens (`fail`, `sample`, `mapReduce`, `deterministic`) to handling modes; default `FAIL`.

#### Key units
- `SrcMorphConfiguration`: root mutable JavaBean for all run settings.
- `AiGenerationConfig`: per-step generation parameters, 45 fields, JavaBean accessors.
- `AiConditionEvaluator`: stateless `matches`, `validate`, `usesLines` over condition trees.
- `AiConditionGroup`: nullable child-condition list for `and`/`or`.
- `AiFactExtractor`: static `factsBlock`, `validate`, `countMatches` for fact counters.
- `AiOversizeStrategy`: enum with `configValue()` and `fromConfig(String)`.

#### Data flow
- Maven/YAML/code populates `SrcMorphConfiguration`, including `fieldGenerations`, `factDefinitions`, and `aiDefinitions`.
- `AiConditionEvaluator.validate` checks condition trees; `matches` filters files using `AiFileContext` facts; `usesLines` signals whether line counting is needed.
- `AiFactExtractor.factsBlock` produces the facts header for oversize inputs.
- `AiOversizeStrategy.fromConfig` selects handling for files exceeding the model context window.
- `AiGenerationConfig` values flow to AI provider implementations (`llamacpp-jni`, `openai-compatible`, `mock`).

#### Dependencies
- Internal: `AiConditionEvaluator` uses `AiSourceExcludeFilter`, `AiCondition`, `AiRangeCondition`, `AiFileContext`; `AiFactExtractor` uses `AiFactCounter`; `SrcMorphConfiguration` uses `AiPromptDefinition`, `AiModelDefinition`, `AiFieldGenerationConfig`, `AiFactDefinition`, `AiGenerationConfig`; `AiGenerationConfig` uses `AiCalibration`.
- External: `java.time.Instant`, `java.util.regex`, `java.io.File`, `java.util.*`, `lombok.ToString`, `org.jspecify.annotations.Nullable`, `net.ladenthin.llama.parameters.ModelParameters` (javadoc), `net.ladenthin.llama` `FlashAttn` (javadoc).

#### Cross-cutting
- Mutable JavaBean pattern with no-arg constructors, plain setters, and Lombok `@ToString`; no `equals`/`hashCode`.
- Null-tolerance via jspecify `@Nullable`; `@SuppressWarnings({"NullAway.Init", "initialization.fields.uninitialized"})` on config beans.
- Validation fails fast with `IllegalArgumentException` (conditions, fact regexes, oversize tokens); `IllegalStateException` for malformed condition nodes.
- Defaults centralised as `public static final` `DEFAULT_*` constants (e.g., `DEFAULT_THREADS`, `DEFAULT_LLAMA_CONTEXT_SIZE`).
- Thread safety: none of the mutable beans are synchronized; `AiConditionEvaluator`, `AiFactExtractor`, and `AiOversizeStrategy` are stateless or immutable.
