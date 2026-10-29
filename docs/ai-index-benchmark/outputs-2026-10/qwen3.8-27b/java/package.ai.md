### java
- H: 1.0
- C: DA147B25
- D: 2026-10-28T21:24:22Z
- T: 2026-10-28T22:45:53Z
- G: 
- A: 0.0.0
- X: package
- F: [config/](config/package.ai.md)
- F: [provider/](provider/package.ai.md)
---
> Pluggable AI text-generation and configuration layer for the srcmorph source-documentation engine: selects, configures, and executes LLM inference (local GGUF via llama.cpp JNI or remote OpenAI-compatible) to produce code or documentation completions, governed by file-exclusion filters, per-model inference parameters, oversize policies, and deterministic fact extraction.

#### Purpose
- Central configuration surface and pluggable inference backend layer for a single srcmorph AI-documentation generation run.
- Bridges Maven plugin parameters (or JSON/YAML) to concrete LLM execution, supporting three backends: mock (default), local llama.cpp JNI (GGUF), and OpenAI-compatible remote endpoints.

#### Responsibilities
- **Run-level configuration** – `SrcMorphConfiguration` aggregates all top-level parameters (paths, provider, routing rules, versioning, size bounds, subtree/exclude filters).
- **Per-model inference parameters** – `AiGenerationConfig` carries 45 tunables (sampling, context, GPU offload, KV-cache, DRY, provider routing) between config and provider layers.
- **File exclusion conditions** – `AiConditionEvaluator` walks and/or/not condition trees with leaf predicates (extensions, size, lines, modification time, path glob) to gate file inclusion.
- **Oversize policy** – `AiOversizeStrategy` enum resolves the `onOversize` token (fail, sample, mapReduce, deterministic).
- **Fact extraction** – `AiFactExtractor` computes exact regex match counts and formats a deterministic facts block prepended to AI summaries.
- **Provider SPI & factory** – `AiGenerationProvider` interface + `AiGenerationProviderFactory` for key-based instantiation with model-level override over run-level default.
- **Local inference** – `LlamaCppJniAiGenerationProvider` drives GGUF model loading, chat completion, KV-cache reuse, GPU offload, and timing extraction.
- **Remote inference** – `OpenAiCompatibleProviderSupport` builds request bodies, resolves endpoints, guards API-key transmission security, and derives timings.
- **Output parsing** – `AiCompletionParser` strips Gemma-4 chain-of-thought reasoning blocks before the answer is persisted.

#### Key units
- `SrcMorphConfiguration` – top-level mutable JavaBean; 23 getter/setter pairs; defaults for output dir, provider, llama fallbacks, AI version.
- `AiGenerationConfig` – 45-field mutable JavaBean; per-model inference knobs; null-to-default coercion on String/list setters.
- `AiConditionEvaluator` – stateless final class; `matches`, `validate`, `usesLines` public API; recursive tree walk over `AiCondition`.
- `AiConditionGroup` – Lombok `@ToString` DTO; holds `List<AiCondition>` for Maven XML `<conditions>` binding.
- `AiOversizeStrategy` – enum with `FAIL`, `SAMPLE`, `MAP_REDUCE`, `DETERMINISTIC`; `fromConfig` parser; `DEFAULT = FAIL`.
- `AiFactExtractor` – static utility; `factsBlock` and `validate`; compiles and counts non-overlapping regex matches per `AiFactCounter`.
- `AiGenerationProvider` – SPI interface (`generate`, `generateWithTimings`, `close`); all backends implement this.
- `AiGenerationProviderFactory` – resolves effective provider key (model override > run default) and instantiates the matching concrete provider.
- `LlamaCppJniAiGenerationProvider` – final class; lazy-loads `LlamaModel`, executes `chatComplete`, parses via `ChatResponseParser`, returns cleaned text or `AiGenerationTimings`.
- `LlamaCppJniConfig` – immutable builder-pattern config; single source of truth for all inference knobs; defaults from `AiGenerationConfig`.
- `OpenAiCompatibleProviderSupport` – package-private final class; Jackson serialization of chat-completions body, URL construction, loopback-only cleartext-key guard, 2xx status check, timing derivation.
- `AiCompletionParser` – stateless utility; strips leading thinking-block span; throws `IOException` on unterminated block.
- `AiGenerationTimings` – value object carrying generated text plus prompt/predicted token counts, cache-hit count, and per-second throughput rates.

#### Data flow
1. Maven plugin (or JSON/YAML) populates `SrcMorphConfiguration` via setters.
2. `SrcMorphConfiguration` references `AiFieldGenerationConfig` (routing rules) which in turn reference `AiGenerationConfig` per model.
3. At runtime, `AiConditionEvaluator.matches` is called per file with an `AiFileContext` to gate inclusion; `usesLines` lets the indexer skip content reads when no `lines` predicate exists.
4. `AiOversizeStrategy.fromConfig` resolves the policy token from routing config.
5. `AiFactExtractor.factsBlock` is invoked with the source text and a list of `AiFactCounter` to produce the deterministic facts prefix.
6. Caller invokes `AiGenerationProviderFactory.create(runProvider, llamaConfig, promptSupport)`.
7. Factory resolves effective key via `effectiveProvider(runProvider, llamaConfig.provider())`; instantiates the matching provider.
8. Provider receives `AiGenerationRequest` (prompt key, source file, source text, current header).
9. **Local path**: `LlamaCppJniAiGenerationProvider` lazily builds `ModelParameters` from `LlamaCppJniConfig`, calls `LlamaModel.chatComplete(...)`, parses `ChatResponse` via `ChatResponseParser`, then `AiCompletionParser.parseCompletion` strips thinking blocks.
10. **Remote path**: `OpenAiCompatibleProviderSupport.requestBody` serializes JSON; provider sends HTTP POST to `endpoint()`; response parsed and timings derived from `ChatResponse.Usage`.
11. Output: clean `String` or `AiGenerationTimings` returned to caller.

#### Dependencies
- **Internal (config)**: `AiCondition`, `AiRangeCondition`, `AiFileContext`, `AiSourceExcludeFilter`, `AiFactCounter`, `AiCalibration`, `AiPromptDefinition`, `AiModelDefinition`, `AiFieldGenerationConfig`, `AiFactDefinition`.
- **Internal (provider)**: `AiGenerationRequest` (document), `AiPromptSupport` (prompt), `AiGenerationConfig` (config defaults), `Java8CompatibilityHelper` (support), `LlamaCppJniProviderSupport`.
- **External (provider)**: `net.ladenthin:llama` JNI binding (`LlamaModel`, `ModelParameters`, `ChatResponseParser`, `ChatResponse`, `Timings`, `FlashAttn`, `ReasoningFormat`), Jackson (`ObjectMapper`, `ObjectNode`, `ArrayNode`).
- **External (shared)**: Lombok, `org.jspecify.annotations.Nullable`, `java.time.Instant`, `java.util.regex.Pattern/Matcher`.
- **Downstream consumers** (referenced in Javadoc): `ModelParameters`, `InferenceParameters`.

#### Cross-cutting
- **Nullability convention** – `@Nullable` (jspecify) marks "not configured" fields; setters coerce null to defaults or empty collections rather than storing null.
- **Validation pattern** – `AiConditionEvaluator.validate` and `AiFactExtractor.validate` fail fast at config time with `IllegalArgumentException`; runtime `matches` throws `IllegalStateException` only if validation was skipped.
- **Statelessness vs mutability** – `AiConditionEvaluator`, `AiFactExtractor`, `AiCompletionParser`, `OpenAiCompatibleProviderSupport`, `LlamaCppJniConfig` are stateless/immutable (thread-safe by construction); `SrcMorphConfiguration` and `AiGenerationConfig` are mutable and not thread-safe.
- **Lazy initialization** – `LlamaCppJniAiGenerationProvider.model()` uses check-then-act (not thread-safe; assumes single-threaded use per instance).
- **Security guard** – `OpenAiCompatibleProviderSupport.apiKeyFor` enforces loopback-only for cleartext HTTP key transmission; HTTPS passes unconditionally.
- **Timing degradation** – Both local and remote paths degrade to zero-rate `AiGenerationTimings` when timing data is absent.
- **Maven XML binding** – field names and collection wrapper types (`AiConditionGroup`) are shaped to satisfy Maven Plexus/JAXB singularization rules.
- **Lombok** – `@ToString` on all mutable beans and provider classes; `@EqualsAndHashCode` on `LlamaCppJniConfig`; `@ConvertToRecord` migration marker on config.
- **Builder pattern** – `LlamaCppJniConfig.Builder` is the sole construction path; all 39 optional fields default to `AiGenerationConfig.DEFAULT_*`.
