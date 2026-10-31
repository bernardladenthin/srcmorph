### java
- H: 1.0
- C: 194C99AD
- D: 2026-10-31T09:32:09Z
- T: 2026-10-31T09:33:21Z
- G: 
- A: 0.0.0
- X: package
- F: [config/](config/package.ai.md)
- F: [provider/](provider/package.ai.md)
---
> Configuration and pluggable AI-generation backend for the srcmorph Maven plugin's code-indexer pipeline, deciding which source files are summarized and how AI text summaries are generated via native, remote, or mock providers.

#### Purpose
- Defines Maven-plugin configuration beans for AI generation (model, sampling, caching, batching, provider endpoints) and per-rule oversize handling.
- Abstracts AI text-generation backends behind `AiGenerationProvider` for local llama.cpp JNI inference, OpenAI-compatible endpoints, and a mock provider, with thinking-block stripping and timing-aware generation.

#### Responsibilities
- **Configuration beans**: `AiGenerationConfig` (40+ parameters with defaults) and `SrcMorphConfiguration` (root run config binding Maven mojos, CLI/YAML, or Java); `LlamaCppJniConfig` immutable provider-specific config via fluent `Builder`.
- **Oversize strategy**: `AiOversizeStrategy` enum (`FAIL`, `SAMPLE`, `MAP_REDUCE`, `DETERMINISTIC`) selected via `<onOversize>` token; default FAIL.
- **Include/exclude condition evaluation**: `AiConditionEvaluator` walks `AiCondition` trees (and/or/not + leaf predicates on extension, size, lines, dates, glob path); `AiConditionGroup` wraps nested `<and>`/`<or>` combinators.
- **Facts block generation**: `AiFactExtractor` counts whole-file regex matches to build the deterministic "facts" block prepended to AI summaries.
- **Provider abstraction & selection**: `AiGenerationProvider` interface (extends `AutoCloseable`) with `generate`, `generateWithTimings`, `close`; `AiGenerationProviderFactory` maps keys (`mock`, `llamacpp-jni`, `openai-compatible`) and resolves model-level over run-level provider.
- **Local GGUF inference**: `LlamaCppJniAiGenerationProvider` runs llama.cpp JNI (`LlamaModel`, `ChatResponseParser`, `Timings`) with lazy init and config-to-`ModelParameters` translation.
- **OpenAI-compatible offline support**: `OpenAiCompatibleProviderSupport` builds chat-completions JSON body, endpoint URL, leak-safe API-key lookup (rejects cleartext to non-loopback hosts), success/error reporting, throughput timings.
- **Completion parsing**: `AiCompletionParser` strips Gemma-4 `thinking` blocks (`<|channel>thought` … `<channel|>`); throws `IOException` on unclosed leading block.

#### Key units
- `AiGenerationConfig` — mutable JavaBean; 40+ `DEFAULT_*` static constants; null-coercing setters; unmodifiable lists for dry sequence breakers and stop strings.
- `SrcMorphConfiguration` — root config bean; 22 field accessors; defaults for output dir (`src/site/ai`), provider (`mock`), llama context (`2048`), AI version (`0.0.0`); links `AiPromptDefinition`, `AiModelDefinition`, `AiFieldGenerationConfig`, `AiFactDefinition`.
- `AiOversizeStrategy` — enum with `fromConfig(@Nullable String)` parser; case-insensitive; `IllegalArgumentException` listing valid tokens.
- `AiConditionEvaluator` — stateless, immutable, thread-safe; `matches`, `validate`, `usesLines`; `branchCount` enforces one branch/leaf per node; `parseEpochMilli` wraps `DateTimeParseException`.
- `AiConditionGroup` — nullable `List<AiCondition>` holder with defensive copy in setter; not thread-safe.
- `AiFactExtractor` — static utility; `factsBlock` joins `label: count` entries under a header/suffix; `validate` checks nulls and regex compilability.
- `AiGenerationProvider` — interface contract for text generation with optional timing and resource closing.
- `AiGenerationProviderFactory` — selects/instantiates provider implementation from key; defaults to `MockAiGenerationProvider` when no key given.
- `LlamaCppJniAiGenerationProvider` — final class; native GGUF generation via `LlamaModel`, lazy model caching, `chatResponse`/`timings` parsing, `close()` on native handle.
- `LlamaCppJniConfig` / `LlamaCppJniConfig.Builder` — immutable config with 41 accessors and 40 fluent setters; sole required field `modelPath`.
- `AiCompletionParser` — static-marker parser returning only the model's final answer text.
- `OpenAiCompatibleProviderSupport` — package-private utility for request serialization, endpoint validation, API-key gating, and timing extraction.

#### Data flow
- Maven plugin config → `SrcMorphConfiguration` (and `AiGenerationConfig`) → AI provider implementations consume the populated beans.
- Source indexer → `AiConditionEvaluator.matches(condition, context)` decides file inclusion/exclusion; `usesLines` gates whether file contents are read; `AiFactExtractor.factsBlock(counters, source)` produces the deterministic facts block prepended to AI summaries.
- `AiGenerationRequest` (prompt key, source file, source text, current header) → `generate`/`generateWithTimings`.
- `LlamaCppJniAiGenerationProvider.generate`: `model().chatComplete(support.buildInferenceParameters(request))` → `ChatResponseParser.parseResponse` → `warnOnTruncatedAnswer`/`logPromptCacheReuse` → `AiCompletionParser.parseCompletion(response.getFirstContent())` → clean answer `String`.
- `generateWithTimings`: parses `Timings`; null timings degrade to zero-rate `AiGenerationTimings`.
- `OpenAiCompatibleProviderSupport` builds request JSON/endpoint; success/error messages and timings derived from response status, body excerpt, and `Usage` counts.

#### Dependencies
- Internal (config): `AiCondition`, `AiFileContext`, `AiRangeCondition`, `AiSourceExcludeFilter`, `AiFactCounter`, `AiCalibration`, `AiPromptDefinition`, `AiModelDefinition`, `AiFieldGenerationConfig`, `AiFactDefinition`.
- External (config): `java.util` (ArrayList, Collections, List), `java.time.Instant`/`DateTimeParseException`, `java.util.regex` (Pattern, Matcher, PatternSyntaxException), `java.io.File`, Lombok `@ToString`, `org.jspecify.annotations.Nullable`.
- External (provider): `net.ladenthin:llama` JNI (`LlamaModel`, `ChatResponseParser`, `ModelParameters`, `ChatResponse`, `Timings`, `FlashAttn`, `ReasoningFormat`); Jackson (`ObjectMapper`, `ObjectNode`, `ArrayNode`); Lombok `@ToString`/`@EqualsAndHashCode`; `org.jspecify.annotations.Nullable`; `java.io.IOException`, `java.net.URL`, `java.util.function.Function`.
- Cross-package: `AiGenerationConfig.DEFAULT_*` from config; `AiPromptSupport`; `Java8CompatibilityHelper`; `AiGenerationRequest` from `net.ladenthin.srcmorph.document`.

#### Cross-cutting
- Config beans: `@SuppressWarnings({"NullAway.Init", "initialization.fields.uninitialized"})` + Lombok `@ToString`; mutable, not thread-safe, no synchronization.
- Provider classes: effective immutability and thread-safety — `LlamaCppJniConfig`, `OpenAiCompatibleProviderSupport`, `AiCompletionParser` use final fields, unmodifiable list views, static markers.
- Null tolerance: `@Nullable` pervasive; String setters restore defaults on null; List setters coalesce null to empty; `Objects.requireNonNull` on constructor inputs; `parseCompletion(null)` returns empty string.
- Validation pattern: fail-fast `IllegalArgumentException` with context (label, valid tokens, branch count); `IOException` for unclosed completion block, Jackson serialization failure, cleartext API key to non-loopback host.
- Single-threaded assumption on lazily initialized `LlamaModel` field; no synchronization or volatile.
