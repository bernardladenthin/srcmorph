### java/config
- H: 1.0
- C: B03472EC
- D: 2026-10-29T08:50:27Z
- T: 2026-10-29T08:54:27Z
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
> srcmorph AI condition evaluation and configuration: validates condition trees, computes facts blocks, and manages generation parameters for model inference.

#### Purpose
The package provides the core building blocks for AI-driven file processing and generation: evaluating condition trees to determine file inclusion, extracting deterministic fact counters from source, and holding mutable configuration for AI generation parameters and generation strategies.

#### Responsibilities
- **Condition evaluation and validation**: Walks AI condition trees (`and`, `or`, `not` combinators) to determine file inclusion based on extensions, size, lines, date, and glob patterns; validates configuration fails fast; reports line-count usage.
- **Facts computation**: Computes deterministic, language-agnostic facts blocks from source text using regex counter configurations.
- **Generation configuration**: Holds all parameters for a single AI generation step, bridging Maven configuration to AI provider implementations.
- **Oversize strategy**: Determines how to handle source files exceeding a model's context window (fail, sample, map-reduce, deterministic).
- **Root configuration**: Centralizes every parameter required for a srcmorph AI code generation run, independent of binding source.

#### Key units
- **AiConditionEvaluator** (final class): Evaluates and validates AI condition trees against file context; walks `and`/`or`/`not` combinators and leaf conditions (extensions, size, lines, date, glob).
- **AiConditionGroup** (class): Container for child `AiCondition` instances, used as the body of `and`/`or` combinators; facilitates Maven plugin XML binding.
- **AiFactExtractor** (final utility class): Computes deterministic facts blocks from `AiFactCounter` configurations; counts non-overlapping regex matches.
- **AiGenerationConfig** (class): Mutable JavaBean carrying all parameters for a single AI generation step; bridges Maven to AI provider implementations.
- **AiOversizeStrategy** (enum): Determines behavior when a source file exceeds its routed model's context window (`FAIL`, `SAMPLE`, `MAP_REDUCE`, `DETERMINISTIC`).
- **SrcMorphConfiguration** (class): Root mutable JavaBean centralizing all parameters a srcmorph run needs; designed for binding from Maven, JSON/YAML, or direct Java construction.

#### Data flow
1. **Condition evaluation**: `AiConditionEvaluator.matches()` walks a condition tree (`AiCondition`/`AiConditionGroup`) against an `AiFileContext`, applying combinator logic (`and`/`or`/`not`) and leaf checks (extension match, size range, line count range, date comparison, glob pattern) to produce a boolean inclusion decision.
2. **Validation & line-detection**: The same evaluator provides `validate()` (fails fast on misconfiguration) and `usesLines()` (recursively detects if any node needs file contents).
3. **Facts extraction**: `AiFactExtractor.factsBlock()` iterates `AiFactCounter` configurations, counts regex matches via `countMatches()` across source text, and builds a `FACTS_HEADER`/`FACTS_SUFFIX` delimited string.
4. **Generation pipeline**: `SrcMorphConfiguration` is populated (Maven/JYAML/Java) and feeds into `AiGenerationConfig`; routing rules reference `AiOversizeStrategy` to handle oversize files; `AiFactExtractor` processes source before generation.

#### Dependencies
- **Internal collaborations**: `AiCondition`, `AiFileContext`, `AiConditionGroup`, `AiRangeCondition`, `AiSourceExcludeFilter`, `AiFactCounter`, `AiPromptDefinition`, `AiModelDefinition`, `AiFieldGenerationConfig`, `AiFactDefinition`, `AiCalibration`.
- **External modules**: `java.time`, `java.time.format`, `java.util.regex`, `java.util.Collections`, `java.util.List`, `java.util.ArrayList`, `org.jspecify.annotations.Nullable`, `lombok.ToString`, `net.ladenthin.srcmorph.support.AiSourceExcludeFilter`.
- **Configuration bindings**: Maven plexus reflection, Jackson/YAML mapping expectations; field names mirror Maven `@Parameter` names.

#### Cross-cutting
- **Nullability**: `@Nullable` widely used on list-returning getters, setter parameters, and optional fields (`modelPath`, `chatTemplateEnableThinking`, `calibration`, `cacheTypeK`/`V`, `provider`, `remoteModel`, `baseUrl`, `apiKeyEnv`, `devices`); null semantics vary (omit kwarg, reset to default, reset to empty).
- **Immutability / mutability**: `AiConditionEvaluator`, `AiFactExtractor`, and `AiOversizeStrategy` are stateless/pure/enum with no mutable state. `AiConditionGroup` has mutable `conditions` field accessed via getter/setter. `AiGenerationConfig` and `SrcMorphConfiguration` are mutable JavaBeans designed for identity-based management (equals/hashCode not generated) and population through setters.
- **Error handling**: `IllegalArgumentException` thrown for misconfigured condition nodes, unparseable ISO instants, blank pathGlob, missing counter labels/patterns, and unknown oversize strategy values; `PatternSyntaxException` caught and rethrown as `IllegalArgumentException` in `AiFactExtractor`.
- **Concurrency**: Stateless methods (`AiConditionEvaluator`, `AiFactExtractor`, `AiOversizeStrategy`) are thread-safe. Mutable beans (`AiGenerationConfig`, `SrcMorphConfiguration`) are not thread-safe by design and intended for single-threaded population/management.
