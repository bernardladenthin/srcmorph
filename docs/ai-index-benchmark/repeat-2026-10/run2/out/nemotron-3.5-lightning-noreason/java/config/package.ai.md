### java/config
- H: 1.0
- C: 2C867AC0
- D: 2026-10-29T13:48:14Z
- T: 2026-10-29T14:02:33Z
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
> srcmorph configures AI-assisted source processing: condition evaluation, generation parameters, and oversize handling for file transformation pipelines.

#### Purpose
The `net.ladenthin.srcmorph.config` package provides the configuration and evaluation logic for AI-assisted source code processing. It defines how AI conditions filter files, how generation parameters are held and adjusted, and how oversized files are handled during AI routing. It serves as the central configuration hub bindable from Maven plugins, JSON/YAML, or direct Java construction, enabling the core AI-assisted transformation workflow.

#### Responsibilities
* **AI condition evaluation**: Walking condition trees (`and`/`or`/`not` combinators) to determine file inclusion based on extensions, size, modification time, path patterns, and line counts.
* **Generation parameter holding**: Mutable JavaBean holding all parameters for a single AI generation step, including model path, sampling parameters, retry policy, trimming limits, and stop strings.
* **Oversize strategy definition**: Enumerating strategies (`FAIL`, `SAMPLE`, `MAP_REDUCE`, `DETERMINISTIC`) for handling source files that exceed a model's context window.
* **Root configuration bean**: Holding every parameter a `srcmorph` run needs, independent of binding source (Maven, JSON/YAML, direct Java), with defaults and setters for flexible population.

#### Key units
* **`AiConditionEvaluator`**: Evaluates and validates AI condition trees against file contexts to determine file inclusion; walks `and`/`or`/`not` combinators and checks leaf conditions (extensions, size ranges, line counts, timestamps, path globs).
* **`AiConditionGroup`**: Container holding a defensively copied list of `AiCondition` children, enabling Maven XML binding for `and`/`or` combinators.
* **`AiGenerationConfig`**: Configuration object carrying all parameters for a single AI generation step (model path, sampling parameters, retry policy, input trimming limits, stop strings) between the Maven configuration layer and AI provider implementations.
* **`AiOversizeStrategy`**: Enum defining four strategies (`FAIL`, `SAMPLE`, `MAP_REDUCE`, `DETERMINISTIC`) for handling source files exceeding a model's context window.
* **`SrcMorphConfiguration`**: Root mutable JavaBean holding every parameter a `srcmorph` run needs, independent of binding source; the single shared config object bindable from every surface.

#### Data flow
1. **File filtering pipeline**: `AiConditionEvaluator.matches()` walks a condition tree to accept or reject a file based on its context (path, size, extensions, modification time, line count). This is used by `SrcMorphConfiguration` to select which files undergo AI processing.
2. **AI generation configuration**: `AiGenerationConfig` collects parameters (model path, temperature, token limits, stop strings) that are consumed by AI provider implementations during a generation step.
3. **Oversize handling**: After a file passes the condition filter, `AiOversizeStrategy` determines the strategy (`FAIL`, `SAMPLE`, `MAP_REDUCE`, `DETERMINISTIC`) if the file's size or line count exceeds the model's context window (`AiGenerationConfig` limits).
4. **Root configuration**: `SrcMorphConfiguration` aggregates all the above, providing the base directory, output directory, file filters, and AI settings that drive the overall processing loop.

#### Dependencies
* **Internal collaborations**: `AiConditionEvaluator` depends on `AiFileContext` and `AiSourceExcludeFilter`; `AiGenerationConfig` references `AiCalibration`; `SrcMorphConfiguration` references `AiPromptDefinition`, `AiModelDefinition`, `AiFieldGenerationConfig`, and `AiFactDefinition`.
* **External modules/packages**: `java.time.Instant`, `java.time.format.DateTimeParseException`, `java.util.Collections`, `java.util.List`, `java.util.regex.Matcher`, `java.util.regex.Pattern`, `org.jspecify.annotations.Nullable`, `lombok.ToString`.
* **Configuration binding**: `SrcMorphConfiguration` intentionally mirrors Maven `@Parameter` field names to enable binding from Maven plexus reflection, Jackson ObjectMapper/YAMLMapper, or direct Java construction.

#### Cross-cutting
* **Defensive copying**: `AiConditionGroup.setConditions()` defensively copies input collections; `AiGenerationConfig` resets list setters to empty on null input and returns unmodifiable views.
* **Null-safety**: `@Nullable` annotations are prevalent on getters/parameters for model paths, chat template thinking, cache types, devices, provider, remote model, base URL, API key env, and sequence breakers/stops lists. `AiGenerationConfig` setters null-safe replace nulls with defaults for lazy mode, cache type K/V, devices, provider, remote model, base URL, and API key env.
* **Statelessness**: `AiConditionEvaluator` and `AiFactExtractor` methods are stateless and side-effect free; `AiOversizeStrategy` is a pure enum with no mutable state.
* **Immutability patterns**: `AiConditionGroup` is final with defensive copies; `AiFactExtractor` is a utility class with a private constructor; `AiOversizeStrategy` is an enum with constants.
