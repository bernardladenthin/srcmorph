### java/config
- H: 1.0
- C: 6E389E7C
- D: 2026-11-01T23:42:40Z
- T: 2026-11-01T23:49:08Z
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
> A configuration‑driven framework for selecting, filtering, and summarizing source files with AI, exposing a rich set of runtime parameters and validation logic.

#### Purpose
- Central configuration bean for the srcmorph tool.
- Validates and evaluates file‑selection conditions.
- Generates deterministic fact blocks for AI summarization.
- Provides enumerated strategies for oversized files.

#### Responsibilities
- **Configuration aggregation** – `SrcMorphConfiguration` holds all runtime settings, including AI provider, model paths, file filters, and generation parameters.
- **Condition evaluation** – `AiConditionEvaluator` checks whether a file satisfies complex logical trees defined in the configuration.
- **Fact extraction** – `AiFactExtractor` builds a deterministic facts block summarizing regex match counts for AI output.
- **Oversize handling** – `AiOversizeStrategy` defines how to route files that exceed model context limits.
- **Condition grouping** – `AiConditionGroup` supports Maven plugin binding of `<conditions>` elements.

#### Key units
- `SrcMorphConfiguration` – mutable bean aggregating all srcmorph settings.
- `AiConditionEvaluator` – stateless validator/evaluator for `AiCondition` trees.
- `AiFactExtractor` – stateless utility for building fact blocks.
- `AiOversizeStrategy` – enum of four routing strategies (`FAIL`, `HEAD`, `CHUNK`, `METADATA`).
- `AiConditionGroup` – container for child `AiCondition` objects used by `<and>`/`<or>` combinators.

#### Data flow
1. **Configuration load** – Maven plugin or CLI populates `SrcMorphConfiguration`.
2. **Condition tree construction** – `AiConditionGroup` and `AiCondition` objects are built from XML/YAML.
3. **File filtering** – `AiConditionEvaluator` receives a file’s `AiFileContext` and the condition tree, returning a match flag.
4. **Oversize routing** – If a file exceeds context limits, `AiOversizeStrategy` determines the handling path.
5. **Fact generation** – `AiFactExtractor` counts regex matches in the source and prepends a facts block to AI output.

#### Dependencies
- **Internal**: `AiCondition`, `AiFactCounter`, `AiPromptDefinition`, `AiModelDefinition`, `AiFieldGenerationConfig`, `AiFactDefinition`, `AiGenerationConfig`.
- **External**: `net.ladenthin.srcmorph.support.AiSourceExcludeFilter`, `java.time.Instant`, `java.util.regex.Pattern`, `org.jspecify.annotations.Nullable`, Lombok annotations (`@ToString`).

#### Cross-cutting
- **Thread safety**: `AiConditionEvaluator` and `AiFactExtractor` are stateless and thread‑safe; other beans are mutable and not thread‑safe.
- **Validation**: `AiConditionEvaluator.validate()` ensures condition trees are well‑formed; `AiFactExtractor.validate()` checks counter definitions.
- **Configuration defaults**: `SrcMorphConfiguration` and `AiGenerationConfig` provide extensive default constants and null‑to‑default conversions.
- **Error handling**: Validation methods throw `IllegalArgumentException` with clear messages; parsing errors are wrapped appropriately.
