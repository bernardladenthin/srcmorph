### java
- H: 1.0
- C: 11988C13
- D: 2026-10-31T10:13:39Z
- T: 2026-10-31T10:18:00Z
- G: 
- A: 0.0.0
- X: package
- F: [config/](config/package.ai.md)
- F: [provider/](provider/package.ai.md)
---
> AI-powered source-code summarization pipeline that selects files, builds prompts with exact facts, and generates documentation headers via local or remote LLM backends.
#### Purpose
- Central configuration, file gating, and pluggable generation backends for AI-powered `.ai.md` runs.
#### Responsibilities
- Run-scope and inference tuning: base/output dirs, subtrees/excludes/extensions, size bounds, provider/prompts/models, sampling/KV-cache/GPU-offload/batching/DRY/reasoning/trimming via `SrcMorphConfiguration`, `AiGenerationConfig` with `DEFAULT_*` sentinels.
- File selection and oversize handling: extension/size/lines/date/pathGlob and/or/not trees via `AiConditionEvaluator` + `AiConditionGroup` over `AiFileContext`; `FAIL`/`SAMPLE`/`MAP_REDUCE`/`DETERMINISTIC` via `AiOversizeStrategy.fromConfig`.
- Exact facts injection: `label: count` `**Facts (exact, whole file):**` block via `AiFactExtractor` from `AiFactCounter` regexes.
- Backend contract and execution: `generate`/`generateWithTimings`/`close` via `AiGenerationProvider`, resolved by `AiGenerationProviderFactory` to mock / `llamacpp-jni` / `openai-compatible`; local GGUF `chatComplete` via `LlamaCppJniAiGenerationProvider` + 41-field `LlamaCppJniConfig`; remote chat-completions JSON/endpoint/key/timings via `OpenAiCompatibleProviderSupport`; Gemma-4 thinking-block cleanup via `AiCompletionParser`.
#### Key units
- `SrcMorphConfiguration` — root mutable JavaBean mirroring Maven `@Parameter` names, drives generation engine.
- `AiGenerationConfig` — single-step LLM parameters for local llama.cpp and remote OpenAI-compatible routing.
- `AiConditionEvaluator` — stateless `matches`/`validate`/`usesLines` over condition trees.
- `AiConditionGroup` — nullable `conditions` list backing `<and>`/`<or>` Maven binding.
- `AiOversizeStrategy` — immutable enum with `configValue()`/`fromConfig()` and `DEFAULT`.
- `AiFactExtractor` — static `factsBlock`/`validate`/`countMatches` with `FACTS_HEADER`/`ENTRY_SEPARATOR`/`FACTS_SUFFIX`.
- `AiGenerationProvider` — backend contract for `AiGenerationRequest` to text plus timings.
- `AiGenerationProviderFactory` — instantiates `MockAiGenerationProvider`, `LlamaCppJniAiGenerationProvider`, `OpenAiCompatibleAiGenerationProvider` via `effectiveProvider`.
- `LlamaCppJniAiGenerationProvider` — local GGUF inference with lazy `LlamaModel`, `ReasoningFormat.AUTO`.
- `LlamaCppJniConfig` — immutable inference config with `builder(modelPath)`, record-style accessors, `build()`.
- `AiCompletionParser` — `parseCompletion` stripper with `THINKING_BLOCK_START_MARKER`/`THINKING_BLOCK_END_MARKER`.
- `OpenAiCompatibleProviderSupport` — `requestBody`, `endpoint`, `apiKey`/`apiKeyFor`, `isLoopbackHost`, `isSuccess`, `errorMessage`, `timings`.
#### Data flow
- Maven/XML/JSON/YAML → `SrcMorphConfiguration`/`AiGenerationConfig` setters → planner routes per `AiFieldGenerationConfig`/`AiModelDefinition`/`AiPromptDefinition`.
- `AiFileContext` → `AiConditionEvaluator.matches` → include/exclude decision; `AiOversizeStrategy.fromConfig` selects fail/trim/chunk/deterministic path.
- Full source + `AiFactCounter`/`AiFactDefinition` → `AiFactExtractor.factsBlock` prepended to AI body → `AiPromptSupport` system+user prompt → `LlamaModel.chatComplete` or remote endpoint → `ChatResponseParser` + `AiCompletionParser.parseCompletion` → cleaned text + `AiGenerationTimings`.
#### Dependencies
- Internal: `AiCondition`, `AiRangeCondition`, `AiFileContext`, `AiSourceExcludeFilter`, `AiFactCounter`, `AiFactDefinition`, `AiModelDefinition`, `AiFieldGenerationConfig`, `AiPromptDefinition`, `AiCalibration`, `AiGenerationRequest`, `AiPromptSupport`, `AiGenerationTimings`, `LlamaCppJniProviderSupport`, `Java8CompatibilityHelper`.
- External: `java.time.Instant`, `java.util.regex.Pattern/Matcher`, `java.io.File`, `java.util.List/Collections`, Lombok `@ToString`/`EqualsAndHashCode`, `org.jspecify.annotations.Nullable`, `net.ladenthin.llama` — `LlamaModel`, `ModelParameters`, `FlashAttn`, `ReasoningFormat`, `ChatResponseParser`, `ChatResponse`, `Timings`, `Usage`, `com.fasterxml.jackson.databind` `ObjectMapper`/`ObjectNode`/`ArrayNode`.
#### Cross-cutting
- Mutable non-thread-safe JavaBeans with null-coerced setters, defensive copies, `DEFAULT_*` constants vs immutable `LlamaCppJniConfig` + single-use `Builder` with `unmodifiableList`; stateless thread-safe helpers.
- Lombok `@ToString` throughout, `@ToString.Exclude` for model/support/environment handles.
- Fail-fast `IllegalArgumentException`: condition branch-count/bounds/glob/date, fact label/pattern/`PatternSyntaxException`, unknown `onOversize`/provider, bad `baseUrl`; `IOException` for truncated thinking block, provider failure, insecure cleartext key; blank/null fallback to mock.
- Timing-aware calibration via `generateWithTimings`, zero-rate fallback when native timings missing.
- Threading: lazy `model` init unsynchronized non-volatile unsafe for concurrent first use; `LlamaCppJniConfig` safe after publication, `Builder` not safe.
