### java/config
- H: 1.0
- C: 693B5E2E
- D: 2026-10-28T21:24:22Z
- T: 2026-10-28T22:45:23Z
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
> Configuration and filtering layer for the srcmorph AI source-documentation engine: defines how files are selected/excluded, how AI models are parameterized, and how oversize or fact-augmented generation is governed.

#### Purpose
- Central configuration surface for a single srcmorph AI-documentation generation run, bindable from Maven plugin parameters, JSON/YAML, or direct Java.
- Provides file-exclusion condition evaluation, per-model inference parameter carriers, oversize-policy resolution, and deterministic fact-block extraction.

#### Responsibilities
- **Run-level configuration** – `SrcMorphConfiguration` aggregates all top-level parameters (paths, provider, routing rules, versioning, size bounds, subtree/exclude filters).
- **Per-model inference parameters** – `AiGenerationConfig` carries 45 tunables (sampling, context, GPU offload, KV-cache, DRY, provider routing) between the config layer and llama.cpp / OpenAI-compatible providers.
- **File exclusion conditions** – `AiConditionEvaluator` walks and/or/not condition trees with leaf predicates (extensions, size, lines, modification time, path glob) to decide include/exclude; `AiConditionGroup` is the XML-binding DTO for combinator children.
- **Oversize policy** – `AiOversizeStrategy` enum resolves the `onOversize` token (fail, sample, mapReduce, deterministic) with a case-insensitive parser.
- **Fact extraction** – `AiFactExtractor` computes exact regex match counts from `AiFactCounter` configs and formats a deterministic facts block prepended to AI summaries.

#### Key units
- `SrcMorphConfiguration` – top-level mutable JavaBean; 23 getter/setter pairs; defaults for output dir, provider, llama fallbacks, AI version.
- `AiGenerationConfig` – 45-field mutable JavaBean; per-model inference knobs; null-to-default coercion on String/list setters; unmodifiable list views for `drySequenceBreakers`/`stopStrings`.
- `AiConditionEvaluator` – stateless final class; `matches`, `validate`, `usesLines` public API; recursive tree walk over `AiCondition`.
- `AiConditionGroup` – Lombok `@ToString` DTO; holds `List<AiCondition>` for Maven XML `<conditions>` binding.
- `AiOversizeStrategy` – enum with `FAIL`, `SAMPLE`, `MAP_REDUCE`, `DETERMINISTIC`; `fromConfig` parser; `DEFAULT = FAIL`.
- `AiFactExtractor` – static utility; `factsBlock` and `validate`; compiles and counts non-overlapping regex matches per `AiFactCounter`.

#### Data flow
- Maven plugin (or JSON/YAML) populates `SrcMorphConfiguration` via setters.
- `SrcMorphConfiguration` references `AiFieldGenerationConfig` (routing rules) which in turn reference `AiGenerationConfig` per model.
- At runtime, `AiConditionEvaluator.matches` is called per file with an `AiFileContext` to gate inclusion; `usesLines` lets the indexer skip content reads when no `lines` predicate exists.
- `AiOversizeStrategy.fromConfig` resolves the policy token from routing config.
- `AiFactExtractor.factsBlock` is invoked with the source text and a list of `AiFactCounter` to produce the deterministic facts prefix.

#### Dependencies
- Internal: `AiCondition`, `AiRangeCondition`, `AiFileContext`, `AiSourceExcludeFilter` (support), `AiFactCounter`, `AiCalibration`, `AiPromptDefinition`, `AiModelDefinition`, `AiFieldGenerationConfig`, `AiFactDefinition`.
- External: `lombok.ToString`, `org.jspecify.annotations.Nullable`, `java.time.Instant`, `java.util.regex.Pattern/Matcher`.
- Downstream consumers (referenced in Javadoc): `LlamaCppJniAiGenerationProvider`, `OpenAiCompatibleAiGenerationProvider`, `ModelParameters`, `InferenceParameters`.

#### Cross-cutting
- **Nullability convention** – `@Nullable` (jspecify) marks "not configured" fields; setters coerce null to defaults or empty collections rather than storing null.
- **Validation pattern** – `AiConditionEvaluator.validate` and `AiFactExtractor.validate` fail fast at config time with `IllegalArgumentException`; runtime `matches` throws `IllegalStateException` only if validation was skipped.
- **Statelessness** – `AiConditionEvaluator` and `AiFactExtractor` are stateless final classes (thread-safe by construction); the two config beans (`SrcMorphConfiguration`, `AiGenerationConfig`) are mutable and not thread-safe.
- **Maven XML binding** – field names and collection wrapper types (`AiConditionGroup`) are shaped to satisfy Maven Plexus/JAXB singularization rules.
- **Lombok** – `@ToString` on all mutable beans; no other Lombok annotations used.
