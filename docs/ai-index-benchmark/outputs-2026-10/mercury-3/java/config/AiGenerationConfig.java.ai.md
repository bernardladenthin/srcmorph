### AiGenerationConfig.java
- H: 1.0
- C: 93D12775
- D: 2026-10-31T09:32:09Z
- T: 2026-10-31T09:32:17Z
- G: 
- A: 0.0.0
- X: file
---
> Mutable Maven-plugin configuration bean carrying every AI-generation parameter (model path, sampling, KV-cache, batching, provider/endpoint settings) between the Maven layer and AI providers, with all defaults.

#### Purpose
- Single mutable JavaBean carrying all parameters for one AI generation step.
- Populated through setters by the Maven plugin framework; consumed by AI provider implementations.
- Lombok `@ToString` over instance fields for build-log diagnostics; `equals`/`hashCode` intentionally not generated (identity management).

#### Type
- Class, public, non-final, not sealed.
- Modifiers: none beyond `public`.
- Annotations: `@SuppressWarnings({"NullAway.Init", "initialization.fields.uninitialized"})`, `@ToString` (Lombok).
- Does not extend or implement anything; no generics.

#### Input
- Constructor `AiGenerationConfig()` — no-op, all defaults applied to fields.
- Mutators (setters) for every parameter, invoked by Maven plugin config layer.
- `@Nullable` fields: `modelPath`, `chatTemplateEnableThinking`, `calibration`.
- List fields `drySequenceBreakers` and `stopStrings` initialized to empty `ArrayList`; setters accept `@Nullable` and reset to empty list.

#### Output
- Getters return current field values; list getters return unmodifiable views via `Collections.unmodifiableList`.
- Produces state via field mutation only; no resources written, no side effects.

#### Core logic
- Holds 40+ configuration fields with defaults, all defaults declared as `public static final` constants.
- Defaults applied as field initializers; no logic in constructor.
- Field defaults: `DEFAULT_CONTEXT_SIZE`=32768, `DEFAULT_MAX_OUTPUT_TOKENS`=128, `DEFAULT_TEMPERATURE`=0.15f, `DEFAULT_THREADS`=`Math.min(4, Runtime.getRuntime().availableProcessors())`, `DEFAULT_MAX_INPUT_CHARS`=120000, `DEFAULT_CHARS_PER_TOKEN`=4, `DEFAULT_SAFETY_MARGIN_CHARS`=500, `DEFAULT_WARN_ON_TRIM`=true, `DEFAULT_TOP_P`=0.9f, `DEFAULT_TOP_K`=40, `DEFAULT_MIN_P`=0.0f, `DEFAULT_TOP_N_SIGMA`=-1.0f, `DEFAULT_REPEAT_PENALTY`=1.0f, `DEFAULT_CHAT_TEMPLATE_ENABLE_THINKING`=null, `DEFAULT_CACHE_PROMPT`=true, `DEFAULT_SWA_FULL`=true, `DEFAULT_CACHE_REUSE`=256, `DEFAULT_GPU_LAYERS`=-1, `DEFAULT_SEED`=-1, `DEFAULT_CPU_MOE_LAYERS`=-1, `DEFAULT_CPU_FFN_LAYERS`=-1, `DEFAULT_KV_UNIFIED_PER_SLOT`=-1, `DEFAULT_LAZY_MODE`="", `DEFAULT_REPEAT_LAST_N`=-1, `DEFAULT_CACHE_TYPE_K`="", `DEFAULT_CACHE_TYPE_V`="", `DEFAULT_FLASH_ATTN`=false, `DEFAULT_BATCH_SIZE`=-1, `DEFAULT_UBATCH_SIZE`=-1, `DEFAULT_THREADS_BATCH`=-1, `DEFAULT_MAIN_GPU`=-1, `DEFAULT_DEVICES`="", `DEFAULT_PROVIDER`="", `DEFAULT_REMOTE_MODEL`="", `DEFAULT_BASE_URL`="https://openrouter.ai/api/v1", `DEFAULT_API_KEY_ENV`="", `DEFAULT_DRY_MULTIPLIER`=0.0f, `DEFAULT_DRY_BASE`=1.75f, `DEFAULT_DRY_ALLOWED_LENGTH`=2, `DEFAULT_DRY_PENALTY_LAST_N`=-1, `DEFAULT_REASONING_EFFORT`="low", `DEFAULT_REASONING_BUDGET_TOKENS`=-1.
- `setLazyMode`, `setCacheTypeK`, `setCacheTypeV`, `setDevices`, `setProvider`, `setRemoteModel`, `setBaseUrl`, `setApiKeyEnv` coerce `null` to their `DEFAULT_*` value.
- `setDrySequenceBreakers` and `setStopStrings` coerce `null` to fresh empty `ArrayList`; getters wrap in `Collections.unmodifiableList`.

#### Public API
- `AiGenerationConfig() -> AiGenerationConfig` — creates config with defaults.
- `getModelPath() -> @Nullable String` — returns GGUF model path; `setModelPath(@Nullable String) -> void` — sets model file path.
- `getContextSize() -> int` — returns context tokens; `setContextSize(int) -> void` — sets context size.
- `getMaxOutputTokens() -> int` — returns output tokens; `setMaxOutputTokens(int) -> void` — sets output tokens.
- `getTemperature() -> float` — returns temperature; `setTemperature(float) -> void` — sets temperature.
- `getThreads() -> int` — returns CPU threads; `setThreads(int) -> void` — sets CPU threads.
- `getCharsPerToken() -> int` — returns chars per token; `setCharsPerToken(int) -> void` — sets chars per token.
- `getMaxInputChars() -> int` — returns max input chars; `setMaxInputChars(int) -> void` — sets max input chars.
- `isWarnOnTrim() -> boolean` — returns warning enabled; `setWarnOnTrim(boolean) -> void` — sets trim warning.
- `getTopP() -> float` — returns top-p; `setTopP(float) -> void` — sets top-p.
- `getTopK() -> int` — returns top-k; `setTopK(int) -> void` — sets top-k.
- `getMinP() -> float` — returns min-p; `setMinP(float) -> void` — sets min-p.
- `getTopNSigma() -> float` — returns top-n-sigma; `setTopNSigma(float) -> void` — sets top-n-sigma.
- `getRepeatPenalty() -> float` — returns repeat penalty; `setRepeatPenalty(float) -> void` — sets repeat penalty.
- `getChatTemplateEnableThinking() -> @Nullable Boolean` — returns thinking mode; `setChatTemplateEnableThinking(@Nullable Boolean) -> void` — sets thinking mode.
- `isCachePrompt() -> boolean` — returns prompt cache enabled; `setCachePrompt(boolean) -> void` — sets prompt cache.
- `isSwaFull() -> boolean` — returns SWA full; `setSwaFull(boolean) -> void` — sets SWA full.
- `getCacheReuse() -> int` — returns cache reuse; `setCacheReuse(int) -> void` — sets cache reuse.
- `getGpuLayers() -> int` — returns GPU layers; `setGpuLayers(int) -> void` — sets GPU layers.
- `getSeed() -> int` — returns seed; `setSeed(int) -> void` — sets seed.
- `getCpuMoeLayers() -> int` — returns CPU MoE; `setCpuMoeLayers(int) -> void` — sets CPU MoE.
- `getCpuFfnLayers() -> int` — returns CPU FFN; `setCpuFfnLayers(int) -> void` — sets CPU FFN.
- `getKvUnifiedPerSlot() -> int` — returns KV cap; `setKvUnifiedPerSlot(int) -> void` — sets KV cap.
- `getLazyMode() -> String` — returns lazy mode; `setLazyMode(@Nullable String) -> void` — sets lazy mode.
- `getRepeatLastN() -> int` — returns repeat window; `setRepeatLastN(int) -> void` — sets repeat window.
- `getCacheTypeK() -> String` — returns K cache type; `setCacheTypeK(@Nullable String) -> void` — sets K cache type.
- `getCacheTypeV() -> String` — returns V cache type; `setCacheTypeV(@Nullable String) -> void` — sets V cache type.
- `isFlashAttn() -> boolean` — returns flash attention; `setFlashAttn(boolean) -> void` — sets flash attention.
- `getBatchSize() -> int` — returns logical batch; `setBatchSize(int) -> void` — sets logical batch.
- `getUbatchSize() -> int` — returns physical batch; `setUbatchSize(int) -> void` — sets physical batch.
- `getThreadsBatch() -> int` — returns batch threads; `setThreadsBatch(int) -> void` — sets batch threads.
- `getMainGpu() -> int` — returns main GPU; `setMainGpu(int) -> void` — sets main GPU.
- `getDevices() -> String` — returns device list; `setDevices(String) -> void` — sets device list.
- `getProvider() -> String` — returns provider; `setProvider(String) -> void` — sets provider.
- `getRemoteModel() -> String` — returns remote model; `setRemoteModel(String) -> void` — sets remote model.
- `getBaseUrl() -> String` — returns base URL; `setBaseUrl(String) -> void` — sets base URL.
- `getApiKeyEnv() -> String` — returns key env name; `setApiKeyEnv(String) -> void` — sets key env name.
- `getReasoningEffort() -> String` — returns reasoning effort; `setReasoningEffort(String) -> void` — sets reasoning effort.
- `getReasoningBudgetTokens() -> int` — returns reasoning budget; `setReasoningBudgetTokens(int) -> void` — sets reasoning budget.
- `getDryMultiplier() -> float` — returns DRY multiplier; `setDryMultiplier(float) -> void` — sets DRY multiplier.
- `getDryBase() -> float` — returns DRY base; `setDryBase(float) -> void` — sets DRY base.
- `getDryAllowedLength() -> int` — returns DRY allowed; `setDryAllowedLength(int) -> void` — sets DRY allowed.
- `getDryPenaltyLastN() -> int` — returns DRY last-n; `setDryPenaltyLastN(int) -> void` — sets DRY last-n.
- `getDrySequenceBreakers() -> List<String>` — returns unmodifiable breakers; `setDrySequenceBreakers(@Nullable List<String>) -> void` — sets breakers.
- `getStopStrings() -> List<String>` — returns unmodifiable stops; `setStopStrings(@Nullable List<String>) -> void` — sets stop strings.
- `getCalibration() -> @Nullable AiCalibration` — returns calibration; `setCalibration(@Nullable AiCalibration) -> void` — sets calibration.
- Total public/protected members: 86 (1 constructor, 2 static final fields are not members counted; 43 getters, 43 setters). Getter/setter pairs: 43. Counted: `getX`/`setX` pairs = 42 field-accessor pairs plus `isCachePrompt`/`setCachePrompt`, `isSwaFull`/`setSwaFull`, `isFlashAttn`/`setFlashAttn` — note `setWarnOnTrim` pairs with `isWarnOnTrim`. Exact getter count: 43 (includes `isWarnOnTrim`, `isCachePrompt`, `isSwaFull`, `isFlashAttn`); setter count: 43. 86 total.

#### Dependencies
- `java.util.ArrayList`
- `java.util.Collections`
- `java.util.List`
- `lombok.ToString`
- `org.jspecify.annotations.Nullable`
- `net.ladenthin.srcmorph.config.AiCalibration` (same package)
- Referenced in javadoc: `net.ladenthin.llama.parameters.ModelParameters`

#### Exceptions / Errors
- No checked or runtime exceptions thrown in this file.
- No null-handling beyond `@Nullable` annotations and `null`-coercion in setters (`setLazyMode`, `setCacheTypeK`, `setCacheTypeV`, `setDevices`, `setProvider`, `setRemoteModel`, `setBaseUrl`, `setApiKeyEnv`, `setDrySequenceBreakers`, `setStopStrings`).
- `@SuppressWarnings({"NullAway.Init", "initialization.fields.uninitialized"})` suppresses null/d uniformity warnings.

#### Concurrency
- Not thread-safe; mutable JavaBean with no synchronization.
- Managed by identity; designed for single-threaded Maven plugin configuration population.
- List getters return unmodifiable views but backing lists can be mutated via setters.
