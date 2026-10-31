### java
- H: 1.0
- C: 194C99AD
- D: 2026-10-31T09:32:09Z
- T: 2026-10-31T09:36:56Z
- G: 
- A: 0.0.0
- X: package
- F: [config/](config/package.ai.md)
- F: [provider/](provider/package.ai.md)
---
> Generates AI-written per-source-file documentation for srcmorph by configuring which files are summarized and how oversize or fact-bearing content is handled, then routing generation to mock, local llama.cpp, or OpenAI-compatible backends.

#### Purpose
- Owns run configuration, file-selection rules, oversize handling, and deterministic fact counts for AI source documentation.
- Owns provider abstraction and selection for text generation, including llama.cpp and OpenAI-compatible settings, endpoint and key safety, and cleanup of leading reasoning blocks.

#### Responsibilities
- Configuration and selection (`config/`):
  - `SrcMorphConfiguration` is the shared run bean; `AiGenerationConfig` holds per-model generation knobs.
  - `AiConditionEvaluator` evaluates nested `AiConditionGroup`/`AiCondition` trees (and/or/not, extension, size, lines, modified date, path glob) against `AiFileContext`.
  - `AiOversizeStrategy` enumerates FAIL, SAMPLE, MAP_REDUCE, DETERMINISTIC and parses the `<onOversize>` token.
  - `AiFactExtractor` runs regex fact counts over full source and emits a `FACTS_HEADER` block.
- Generation providers (`provider/`):
  - `AiGenerationProvider` contract with `generate`, `generateWithTimings`, `close`.
  - `AiGenerationProviderFactory` maps "mock", "llamacpp-jni", "openai-compatible" and resolves per-model versus run-level provider.
  - `LlamaCppJniAiGenerationProvider` with immutable `LlamaCppJniConfig` for local inference and native timings.
  - `OpenAiCompatibleProviderSupport` builds chat-completion JSON, endpoint URL, API-key lookup, HTTP error messages, and timings.
- Output cleaning:
  - `AiCompletionParser` strips a leading `<|channel>thought ... <channel|>` block and rejects unterminated blocks.

#### Key units
- `SrcMorphConfiguration`: shared run configuration bean; Maven, JSON/YAML, and plain-Java binding.
- `AiGenerationConfig`: per-model generation settings; sampling, GPU/CPU placement, KV cache, DRY, remote routing; `DEFAULT_THREADS`.
- `AiConditionEvaluator`: stateless evaluator; `matches`, `validate`, `usesLines`.
- `AiConditionGroup`: child-list wrapper for and/or combinators; carries `<condition>` Maven item tags.
- `AiFactExtractor`: static utility; `factsBlock`, `validate`, `countMatches`; `FACTS_HEADER`, `LABEL_COUNT_SEPARATOR`, `ENTRY_SEPARATOR`, `FACTS_SUFFIX`.
- `AiOversizeStrategy`: enum with `configValue()` and `fromConfig(String)`; `DEFAULT` is FAIL.
- `AiGenerationProvider`: generation interface with default `generateWithTimings` and default no-op `close`.
- `AiGenerationProviderFactory`: `create`, `effectiveProvider`; `PROVIDER_MOCK`, `PROVIDER_LLAMACPP_JNI`, `PROVIDER_OPENAI_COMPATIBLE`.
- `LlamaCppJniAiGenerationProvider`: final class; lazy `LlamaModel` via `model()`, `chatResponse`, `generateWithTimings`.
- `LlamaCppJniConfig`: final immutable value class; nested `Builder` with about 40 setters; `builder(modelPath)`.
- `OpenAiCompatibleProviderSupport`: package-private final helper; `requestBody`, `endpoint`, `apiKeyFor`, `isLoopbackHost`, `errorMessage`, `timings`.
- `AiCompletionParser`: `parseCompletion`; `THINKING_BLOCK_START_MARKER`, `THINKING_BLOCK_END_MARKER`.
- `AiGenerationTimings`: result carrier for text, token counts, and rates (defined outside these summaries).

#### Data flow
- Maven/YAML/JSON bind into `SrcMorphConfiguration`; llama* fields are fallback only when `fieldGenerations` is empty; per-model settings flow to `AiGenerationConfig`.
- `generationProvider` (run) and `provider` (model) feed `effectiveProvider` → `create` → concrete provider.
- `AiFileContext` is tested by `AiConditionEvaluator.matches`; `usesLines` signals whether line counting is required.
- Oversize files route through `AiOversizeStrategy`:
  - SAMPLE trims to a window.
  - MAP_REDUCE chunks at line boundaries and combines partials.
  - DETERMINISTIC skips the model and emits size, line count, and head/tail sample.
  - FAIL aborts.
- Full source text goes to `AiFactExtractor.factsBlock`; output is prepended to summaries on the oversize path.
- Request (`AiGenerationRequest`: prompt key, source file, source text) → `AiPromptSupport` builds messages → provider call.
- llama.cpp path: `LlamaCppJniConfig` → `ModelParameters` → `LlamaModel.chatComplete` → `ChatResponseParser` → `AiCompletionParser` → text.
- OpenAI-compatible path: `requestBody` JSON → HTTP POST to `endpoint()` (http/https validated, cleartext key check) → `ChatResponse` → `timings` and text cleaning.
- Output is the cleaned string or `AiGenerationTimings`, persisted by the caller to an AI index file.

#### Dependencies
- Internal config types: `AiCondition`, `AiRangeCondition`, `AiFileContext`, `AiFactCounter`, `AiFactDefinition`, `AiModelDefinition`, `AiFieldGenerationConfig`, `AiPromptDefinition`, `AiSourceExcludeFilter` (`net.ladenthin.srcmorph.support`).
- Internal provider types: `AiGenerationRequest`, `AiPromptSupport`, `Java8CompatibilityHelper`, `AiGenerationConfig` defaults, `ConvertToRecord`.
- llama.cpp binding (`net.ladenthin.llama`): `LlamaModel`, `ModelParameters`, `ChatResponse`, `Timings`, `Usage`, `ChatResponseParser`, `FlashAttn`, `ReasoningFormat`.
- External: `java.time.Instant`, `java.util.regex.*`, `java.io.File`, Jackson `ObjectMapper`/`ObjectNode`/`ArrayNode`/`YAMLMapper`, Lombok `@ToString`/`@EqualsAndHashCode`, JSpecify `@Nullable`.
- Consumers: Maven plugin parameter binding; llama.cpp JNI and OpenAI-compatible backends selected by `generationProvider` values.
- Referenced but not summarized here: `MockAiGenerationProvider`, `OpenAiCompatibleAiGenerationProvider`, `LlamaCppJniProviderSupport`.

#### Cross-cutting
- Mutable JavaBeans with getters and setters; Maven-name alignment for XML and YAML keys; `@SuppressWarnings` on beans for NullAway and initialization.
- JSpecify `@Nullable`; null means "not configured" or engine default.
- Sentinels: `-1` means unset (GPU layers, batch sizes, seed); `0` is meaningful for several knobs; `<= 0` disables size bounds; `0.0` disables minP and DRY multiplier; `-1.0` disables topNSigma.
- Fail-fast validation: `IllegalArgumentException` from `AiConditionEvaluator.validate`, `AiFactExtractor.validate`, `AiOversizeStrategy.fromConfig`; `PatternSyntaxException` wrapped.
- Errors: `IOException` for provider and parse failures (unterminated thinking block, HTTP errors, cleartext key to non-loopback host); unchecked for config and unsupported providers; unknown provider key throws `IllegalArgumentException`, blank key falls back to mock.
- Null handling: `Objects.requireNonNull` in constructors; builder string and list setters map null to defaults.
- Immutability: `LlamaCppJniConfig` and output carriers are immutable with unmodifiable lists; `OpenAiCompatibleProviderSupport` uses final fields only.
- Concurrency:
  - Stateless utilities and the factory are thread-safe; each `create` returns a fresh provider.
  - Bean classes are not thread-safe.
  - `LlamaCppJniAiGenerationProvider` lazy `model` init is unsynchronized and not volatile; concurrent first calls may create multiple models.
  - `close()` does not null the model field, so repeated calls re-close the native handle.
- Defaults: provider `mock`, output `src/site/ai`, context 2048, max output 128, temperature 0.15, AI version `0.0.0`.
