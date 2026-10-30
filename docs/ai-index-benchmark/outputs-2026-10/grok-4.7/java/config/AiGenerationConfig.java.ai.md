### AiGenerationConfig.java
- H: 1.0
- C: 93D12775
- D: 2026-10-30T00:29:58Z
- T: 2026-10-30T00:38:51Z
- G: 
- A: 0.0.0
- X: file
---
Preparing a dense, structured markdown summary of the AiGenerationConfig.java source file.

Analyzing the file structure and counting public constants. The summary will follow a strict indexing format covering purpose, type, input, output, core logic, public API, dependencies, exceptions, and concurrency.

Counting the remaining public constants. The tally reaches 30 so far, covering safety margins, sampling parameters, KV cache settings, and GPU layer configuration.

The public static final constants total 42. The constructor is public. Now cataloging the getter and setter methods.

The getter and setter methods are being catalogued. The list now includes max output tokens, temperature, threads, character limits, trim warnings, top-p, top-k, repeat penalty, and minimum probability.

The getter and setter catalog now includes top-n sigma, chat template thinking, cache prompt, SWA full, cache reuse, GPU layers, CPU MoE layers, seed, CPU FFN layers, and KV unified per slot.

The getter and setter catalog now includes lazy mode, repeat last N, cache type K and V, flash attention, batch size, ubatch size, threads batch, and main GPU.

The getter and setter catalog now includes devices, provider, remote model, base URL, API key environment, reasoning effort, reasoning budget tokens, and dry multiplier.

The getter and setter catalog is now complete. It includes dry base, allowed length, penalty last N, sequence breakers, stop strings, and calibration methods. The total public API surface is 90 methods plus the constructor and 42 constants.

The public API will be compacted. The 90 getters and setters are a family of near-identical JavaBean accessors, so they will be enumerated in the analysis channel and summarized with their exact total and index range.

The near-identical getters and setters will be listed individually. Collapsing them into a range would erase their distinct names, which are needed for searchability.

The 42 constants are now fully enumerated. The private instance fields are being catalogued.

The private instance fields are now fully enumerated. The total is 45.

Boolean accessors use `is*` instead of `get*`, such as `isWarnOnTrim`, `isCachePrompt`, `isSwaFull`, and `isFlashAttn`. `setCachePrompt` exists but is not paired with `isCachePrompt`. All 45 private fields have both a getter and a setter.

The public API totals 133 members: 42 constants, one constructor, and 90 methods. The near-identical getter and setter pairs will be collapsed into a single family entry rather than listed individually.

The 90 getters and setters will be listed individually as paired `get/set` entries. Each name appears once with a brief descriptive clause, keeping the list compact while preserving searchability. The family exception only applies to indexed sequences like `fooN`.

The 42 DEFAULT_* constants will be listed individually. They are not a numeric family like fooN, so the family exception does not apply. Each constant name and value is distinct and searchable.

The Lombok `@ToString` annotation will be noted in the Type and Dependencies sections. It is not a handwritten method, so it will not be listed as a public member.

Core logic notes that equals and hashCode are intentionally not generated. Null handling is mixed: lazy mode, cache types, devices, provider, remote model, base URL, and API key all fall back to defaults on null input; dry sequence breakers and stop strings default to new ArrayLists. Reasoning effort lacks a null guard. modelPath, chatTemplateEnableThinking, and calibration are nullable fields. getDrySequenceBreakers and getStopStrings return unmodifiable lists. No exceptions are thrown in this file.

The class is mutable and not thread-safe. Its setters store ArrayList references directly without copying, so callers can mutate the underlying lists after passing them in. DEFAULT_THREADS is computed at class initialization from Runtime.availableProcessors(). It declares no extends or implements clause. The package is net.ladenthin.srcmorph.config. Dependencies are java.util.ArrayList, Collections, List, Lombok ToString, and Jspecify Nullable.

AiCalibration is referenced in the same package without an import. The class javadoc mentions a retry policy, but no retry fields exist in the source, so that behavior will not be indexed. Javadoc also references Runtime, ModelParameters, InferenceParameters, LlamaCppJniAiGenerationProvider, and FlashAttn.

The 42 DEFAULT_* constants are a family of near-identical public static finals, so they will be grouped as a counted family rather than listed individually.

The DEFAULT_* constants will be listed as compact bullets with their values. The 90 getters and setters will be written as `getX()/setX(type)` pairs with brief descriptive clauses. A final scan of the source is underway to verify the exact method count.

Default values and null coalescing rules are being mapped. Negative one signals "leave default" for integer knobs such as gpuLayers, seed, and reasoningBudget. Empty strings omit lazyMode, cache types, devices, provider, remoteModel, and apiKeyEnv. Zero disables features like cacheReuse, gpuLayers forcing CPU fallback, minP, and dryMultiplier. topNSigma of -1.0 disables its effect. null chatTemplateEnableThinking omits the keyword argument.

The response will use blockquotes and four-level headers. Code fences are omitted per the indexing task. KaTeX is unnecessary. SAFETY_MARGIN is a constant without a corresponding instance field.

Automatic maxInputChars calculation is documented as happening elsewhere when charsPerToken is positive, not implemented in this class. The class javadoc mentions a retry policy but no retry fields exist, so retry logic will not be claimed as implemented. Lombok generates toString over instance fields; equals and hashCode are intentionally omitted. The Public API section will list the 42 constants and accessor pairs, with each descriptive clause kept to six words or fewer.

The class is a mutable JavaBean bridging Maven plugin configuration to AI provider implementations. It stores per-model inference knobs, prompt-trim limits, stop strings, and optional calibration data. It carries no extends or implements clause and uses Lombok's @ToString alongside NullAway suppression annotations.> Holds mutable per-model sampling, cache, prompt-trim, and endpoint settings for one srcmorph AI generation step.

#### Purpose
- Maven-populated JavaBean carrying one model definition from plugin config to AI providers (`llamacpp-jni`, `openai-compatible`, `mock`).
- Stores llama.cpp inference knobs, OpenAI-compatible routing, stop strings, DRY sampling, and optional `AiCalibration`.

#### Type
- `public class AiGenerationConfig`; not abstract, final, or sealed; no extends or implements.
- `@SuppressWarnings({"NullAway.Init", "initialization.fields.uninitialized"})`, Lombok `@ToString`.
- `equals`/`hashCode` intentionally absent; identity-managed mutable bean.

#### Input
- `AiGenerationConfig()` no-arg; Maven sets 45 instance fields via setters.
- `@Nullable` inputs: `modelPath`, `chatTemplateEnableThinking`, `lazyMode`, `cacheTypeK`, `cacheTypeV`, `drySequenceBreakers`, `stopStrings`, `calibration`.
- Unannotated strings still null-checked: `devices`, `provider`, `remoteModel`, `baseUrl`, `apiKeyEnv`.
- `DEFAULT_THREADS` reads `Runtime.getRuntime().availableProcessors()` at class init.

#### Output
- Getters expose current field values; no files or streams written.
- `getDrySequenceBreakers()` and `getStopStrings()` return `Collections.unmodifiableList` views.
- List setters keep the caller `List` reference (no copy); `null` becomes a new `ArrayList`.
- Lombok `toString` over instance fields for build-log diagnostics.

#### Core logic
- Defaults applied at field init from 42 `DEFAULT_*` constants; constructor is a no-op.
- Sentinel `-1` means “do not forward / leave binding default” for GPU layers, seed, CPU MoE/FFN layers, KV unified cap, repeat-last-n, batch, ubatch, threads-batch, main GPU, and DRY penalty window; reasoning budget `-1` means unrestricted.
- Empty string omits or leaves default for lazy mode, KV cache types, devices, provider, remote model, and API-key env name.
- `charsPerToken == 0` disables automatic `maxInputChars`; `DEFAULT_SAFETY_MARGIN_CHARS` has no field (calculation lives elsewhere).
- `chatTemplateEnableThinking == null` omits `enable_thinking`; `true`/`false` are forwarded.
- Null coalesces to the matching default: `setLazyMode`, `setCacheTypeK`, `setCacheTypeV`, `setDevices`, `setProvider`, `setRemoteModel`, `setBaseUrl`, `setApiKeyEnv`.
- `setReasoningEffort` assigns the argument with no null guard.
- `DEFAULT_BASE_URL` is `https://openrouter.ai/api/v1`; `DEFAULT_API_KEY_ENV` is empty so no key is sent unless named.
- `DEFAULT_PROVIDER` empty means use the run’s `generationProvider`.

#### Public API
- `AiGenerationConfig() -> AiGenerationConfig` — apply field defaults.
- `DEFAULT_CONTEXT_SIZE -> int` — context window 32768.
- `DEFAULT_MAX_OUTPUT_TOKENS -> int` — output cap 128.
- `DEFAULT_TEMPERATURE -> float` — sampling temperature 0.15.
- `DEFAULT_THREADS -> int` — min(4, available processors).
- `DEFAULT_MAX_INPUT_CHARS -> int` — fallback trim 120000.
- `DEFAULT_CHARS_PER_TOKEN -> int` — auto-trim ratio 4.
- `DEFAULT_SAFETY_MARGIN_CHARS -> int` — budget headroom 500.
- `DEFAULT_WARN_ON_TRIM -> boolean` — warn when trimmed.
- `DEFAULT_TOP_P -> float` — nucleus threshold 0.9.
- `DEFAULT_TOP_K -> int` — top-k limit 40.
- `DEFAULT_MIN_P -> float` — min-p disabled 0.
- `DEFAULT_TOP_N_SIGMA -> float` — sigma sampler disabled.
- `DEFAULT_REPEAT_PENALTY -> float` — no penalty at 1.
- `DEFAULT_CHAT_TEMPLATE_ENABLE_THINKING -> @Nullable Boolean` — unset omits kwarg.
- `DEFAULT_CACHE_PROMPT -> boolean` — prefix KV reuse on.
- `DEFAULT_REASONING_EFFORT -> String` — gpt-oss effort `low`.
- `DEFAULT_REASONING_BUDGET_TOKENS -> int` — unrestricted budget -1.
- `DEFAULT_SWA_FULL -> boolean` — keep full SWA KV.
- `DEFAULT_CACHE_REUSE -> int` — prefix reuse chunk 256.
- `DEFAULT_GPU_LAYERS -> int` — do not set -1.
- `DEFAULT_SEED -> int` — random per request.
- `DEFAULT_CPU_MOE_LAYERS -> int` — do not set -1.
- `DEFAULT_CPU_FFN_LAYERS -> int` — do not set -1.
- `DEFAULT_KV_UNIFIED_PER_SLOT -> int` — do not set -1.
- `DEFAULT_LAZY_MODE -> String` — empty leaves default.
- `DEFAULT_REPEAT_LAST_N -> int` — do not set -1.
- `DEFAULT_CACHE_TYPE_K -> String` — empty leaves K type.
- `DEFAULT_CACHE_TYPE_V -> String` — empty leaves V type.
- `DEFAULT_FLASH_ATTN -> boolean` — false emits nothing.
- `DEFAULT_BATCH_SIZE -> int` — do not set -1.
- `DEFAULT_UBATCH_SIZE -> int` — do not set -1.
- `DEFAULT_THREADS_BATCH -> int` — reuse decode threads.
- `DEFAULT_MAIN_GPU -> int` — do not set -1.
- `DEFAULT_DEVICES -> String` — empty leaves devices.
- `DEFAULT_PROVIDER -> String` — empty uses run provider.
- `DEFAULT_REMOTE_MODEL -> String` — empty sends no model.
- `DEFAULT_BASE_URL -> String` — OpenRouter v1 base.
- `DEFAULT_API_KEY_ENV -> String` — empty sends no key.
- `DEFAULT_DRY_MULTIPLIER -> float` — DRY disabled at 0.
- `DEFAULT_DRY_BASE -> float` — DRY base 1.75.
- `DEFAULT_DRY_ALLOWED_LENGTH -> int` — unpenalised n-gram 2.
- `DEFAULT_DRY_PENALTY_LAST_N -> int` — sentinel not forwarded.
- Accessors (90 total; 45 get/is + 45 set): `getModelPath()/setModelPath(@Nullable String) -> @Nullable String/void`; `getContextSize()/setContextSize(int)`; `getMaxOutputTokens()/setMaxOutputTokens(int)`; `getTemperature()/setTemperature(float)`; `getThreads()/setThreads(int)`; `getCharsPerToken()/setCharsPerToken(int)`; `getMaxInputChars()/setMaxInputChars(int)`; `isWarnOnTrim()/setWarnOnTrim(boolean)`; `getTopP()/setTopP(float)`; `getTopK()/setTopK(int)`; `getRepeatPenalty()/setRepeatPenalty(float)`; `getMinP()/setMinP(float)`; `getTopNSigma()/setTopNSigma(float)`; `getChatTemplateEnableThinking()/setChatTemplateEnableThinking(@Nullable Boolean)`; `isCachePrompt()/setCachePrompt(boolean)`; `isSwaFull()/setSwaFull(boolean)`; `getCacheReuse()/setCacheReuse(int)`; `getGpuLayers()/setGpuLayers(int)`; `getCpuMoeLayers()/setCpuMoeLayers(int)`; `getSeed()/setSeed(int)`; `getCpuFfnLayers()/setCpuFfnLayers(int)`; `getKvUnifiedPerSlot()/setKvUnifiedPerSlot(int)`; `getLazyMode()/setLazyMode(@Nullable String)`; `getRepeatLastN()/setRepeatLastN(int)`; `getCacheTypeK()/setCacheTypeK(@Nullable String)`; `getCacheTypeV()/setCacheTypeV(@Nullable String)`; `isFlashAttn()/setFlashAttn(boolean)`; `getBatchSize()/setBatchSize(int)`; `getUbatchSize()/setUbatchSize(int)`; `getThreadsBatch()/setThreadsBatch(int)`; `getMainGpu()/setMainGpu(int)`; `getDevices()/setDevices(String)`; `getProvider()/setProvider(String)`; `getRemoteModel()/setRemoteModel(String)`; `getBaseUrl()/setBaseUrl(String)`; `getApiKeyEnv()/setApiKeyEnv(String)`; `getReasoningEffort()/setReasoningEffort(String)`; `getReasoningBudgetTokens()/setReasoningBudgetTokens(int)`; `getDryMultiplier()/setDryMultiplier(float)`; `getDryBase()/setDryBase(float)`; `getDryAllowedLength()/setDryAllowedLength(int)`; `getDryPenaltyLastN()/setDryPenaltyLastN(int)`; `getDrySequenceBreakers()/setDrySequenceBreakers(@Nullable List<String>) -> List<String>/void`; `getStopStrings()/setStopStrings(@Nullable List<String>) -> List<String>/void`; `getCalibration()/setCalibration(@Nullable AiCalibration) -> @Nullable AiCalibration/void`.

#### Dependencies
- `java.util.ArrayList`, `java.util.Collections`, `java.util.List`, `Runtime`.
- `lombok.ToString`, `org.jspecify.annotations.Nullable`.
- `AiCalibration` (same package).
- Javadoc-only: `ModelParameters`, `InferenceParameters`, `FlashAttn`, `LlamaCppJniAiGenerationProvider`.

#### Exceptions / Errors
- No throw or catch in this file.
- Eight string setters and two list setters map `null` to defaults or empty lists.
- `setModelPath`, `setChatTemplateEnableThinking`, `setCalibration`, and `setReasoningEffort` store `null` if passed.
- Binding-side rejection of negative windows and non-positive batch sizes is documented, not enforced here.

#### Concurrency
- Mutable, unsynchronized, not thread-safe.
- `DEFAULT_THREADS` fixed once at class initialization.
- Unmodifiable list views do not copy; a retained setter argument can still mutate the stored list.
