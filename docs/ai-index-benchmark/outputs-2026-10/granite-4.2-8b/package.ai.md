### ai
- H: 1.0
- C: FFA8A94C
- D: 2026-10-31T10:06:17Z
- T: 2026-10-31T11:23:28Z
- G: 
- A: 0.0.0
- X: package
- F: [java/](java/package.ai.md)
- F: [other/](other/package.ai.md)
- F: [sql/](sql/package.ai.md)
---
> A tool that evaluates AI conditions against source files to decide inclusion, extracts structural facts, and invokes a pluggable AI text generation backend (local or remote) to answer code queries.

#### Purpose
- Determine which source files match AI query conditions using a validated condition tree evaluator.
- Extract deterministic file facts (line counts, glob matches) without reading full content when not needed.
- Provide a unified AI generation interface that can use local llama.cpp models via JNI or OpenAI-compatible endpoints.
- Measure generation timings for model calibration and performance monitoring.

#### Responsibilities
- Condition evaluation, validation, and line-usage detection (`AiConditionEvaluator`, `AiConditionGroup`).
- Structural fact extraction from source (`AiFactExtractor`).
- Configuration management for AI inference (`AiGenerationConfig`, `SrcMorphConfiguration`).
- Oversize strategy selection for large inputs (`AiOversizeStrategy`).
- Pluggable AI generation backend abstraction and concrete implementations (`AiGenerationProvider`, `AiGenerationProviderFactory`).
- Local inference via JNI-linked llama.cpp models (`LlamaCppJniAiGenerationProvider`, `LlamaCppJniConfig`).
- Remote OpenAI-compatible support without direct network calls (`OpenAiCompatibleProviderSupport`).

#### Key units
- `AiConditionEvaluator`: evaluates condition trees (and/or/not branches), validates structure, detects if `lines` condition is used.
- `AiConditionGroup`: container for child `AiCondition`s; enforces exactly one branch/leaf per node.
- `AiFactExtractor`: computes whole-file counts via regex; emits "Facts" header block.
- `AiGenerationConfig`: mutable settings for AI model inference (sampling, context size, provider, etc.).
- `AiOversizeStrategy`: enum (`FAIL`, `SAMPLE`, `MAP_REDUCE`, `DETERMINISTIC`) for handling oversized files.
- `SrcMorphConfiguration`: top-level mutable runtime configuration (paths, AI definitions, flags).
- `AiCompletionParser`: strips leading "thinking" blocks from LLM responses.
- `AiGenerationProvider`: interface requiring `generate`, `generateWithTimings`, and `close`.
- `AiGenerationProviderFactory`: stateless factory selecting provider via `effectiveProvider`.
- `LlamaCppJniAiGenerationProvider`: implements `AiGenerationProvider` for local llama.cpp via JNI; lazy model loading.
- `LlamaCppJniConfig`: immutable fluent-builder config for local model settings.
- `OpenAiCompatibleProviderSupport`: builds JSON request bodies, validates endpoints, computes timings from `ChatResponse`.

#### Data flow
- `SrcMorphConfiguration` provides routing rules and AI settings; feeds into `AiConditionEvaluator` and `AiFactExtractor`.
- `AiConditionEvaluator` receives a condition tree (`AiConditionGroup`) and `AiFileContext` (facts) to compute `matches` and `usesLines`.
- `AiFactExtractor` receives source text and counters to emit `factsBlock`; `validate` ensures regex safety.
- `AiGenerationConfig` stores generation parameters used by provider implementations.
- `AiOversizeStrategy` selects strategy based on config token for oversized files.
- `AiGenerationRequest` flows into a provider (`LlamaCppJniAiGenerationProvider`, `OpenAiCompatibleAiGenerationProvider`, or `MockAiGenerationProvider`).
- Provider calls `AiCompletionParser.parseCompletion()` to extract clean answer text (removing thinking block).
- `LlamaCppJniAiGenerationProvider` lazily initializes the `LlamaModel` on first `generate`/`generateWithTimings`, then parses chat response.
- `OpenAiCompatibleProviderSupport` builds `requestBody`, `endpoint`, and `apiKeyFor`; computes `AiGenerationTimings` from `ChatResponse`.
- `AiGenerationProviderFactory` receives `providerName`, `LlamaCppJniConfig`, `AiPromptSupport`; returns concrete provider via `effectiveProvider`.
- `generateWithTimings` returns `AiGenerationTimings`; `generate` returns only the cleaned answer string.

#### Dependencies
- Internal: `AiCondition`, `AiConditionGroup`, `AiFileContext`, `AiRangeCondition`, `AiSourceExcludeFilter`; `AiCalibration` (via `AiGenerationConfig`); `AiPromptDefinition`, `AiModelDefinition`, etc. (via `SrcMorphConfiguration`). Provider side: `AiCompletionParser`, `AiGenerationRequest`, `AiGenerationTimings`, `AiPromptSupport`, `Java8CompatibilityHelper`, `ChatResponseParser`, `ModelParameters`, `ReasoningFormat`, `FlashAttn`.
- External Java: `java.io`, `java.util`, `java.time`, `java.lang`; `org.jspecify.annotations.Nullable`.
- Jackson: `ObjectMapper`, `ObjectNode`, `ArrayNode`.
- Lombok: `@ToString`, `@EqualsAndHashCode`, `@ConvertToRecord`.
- `net.ladenthin.llama.*` types (`LlamaModel`, `ChatResponse`, `Timings`).

#### Cross-cutting
- Defensive copying in `AiConditionGroup.setConditions`; private helper methods (`anyUsesLines`, `branchCount`, `matchesExtension`, `globMatches`, `parseEpochMilli`, `countMatches`) used internally.
- Null safety via `@Nullable` annotations; getters return nullable types; setters accept nullable parameters.
- Immutable views (`Collections.unmodifiableList`) returned for shared lists (`drySequenceBreakers`, `stopStrings`).
- No shared mutable state across threads; all units are stateless or locally mutable; safe for concurrent reads; writers must not share the same instance without external sync.
- Configuration defaults stored in static final fields (`DEFAULT_*` constants).
- Error handling: `IllegalArgumentException` for misconfiguration (empty extensions, null label/pattern, invalid ISO-8601, unknown oversize token); `IllegalStateException` for unmatched condition tree; `PatternSyntaxException` caught internally in `AiFactExtractor`; `DateTimeParseException` caught internally in `AiConditionEvaluator`, rethrown as `IllegalArgumentException`.
- `IOException` is the only checked exception propagated from providers (`generate` may throw it; `generateWithTimings` catches and handles gracefully).
- Fluent builder pattern used by `LlamaCppJniConfig`; getters only, no setters after `build`.
- Static utilities (`isLoopbackHost`, `isSuccess`, `errorMessage`) in `OpenAiCompatibleProviderSupport` for safe host/status checks.
- Provider selection relies on string constant (`PROVIDER_MOCK`, `PROVIDER_LLAMACPP_JNI`, `PROVIDER_OPENAI_COMPATIBLE`); factory throws `IllegalArgumentException` for unknown providers.

> Note: The `other/` and `sql/` subdirectories describe unrelated functionality (OpenCL histogram kernels, ring buffers, backups, revenue reports; and three database systems for library, shop, warehouse). They are not part of the AI evaluation and generation tool described in `java/`. The summary focuses only on the `java/` package as the primary AI tool.
