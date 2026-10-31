### AiGenerationConfig.java
- H: 1.0
- C: 93D12775
- D: 2026-10-31T09:32:09Z
- T: 2026-10-31T09:34:17Z
- G: 
- A: 0.0.0
- X: file
---
> Holds the tunable parameters for one AI summary generation run (model path, sampling, GPU/CPU placement, KV cache, retry-free trimming limits, remote provider routing, stop strings) and passes them from the Maven config layer to the AI providers.

#### Purpose
- Mutable JavaBean carrying all per-model generation settings, populated by the Maven plugin framework through setters.
- Holds the library-wide default values as public `DEFAULT_*` constants, each with javadoc explaining its llama.cpp mapping and sentinel meaning.

#### Type
- Public class, not final, no extends, no implements.
- Annotations: `@ToString` (Lombok, build-log diagnostics), `@SuppressWarnings({"NullAway.Init", "initialization.fields.uninitialized"})`.
- Nullability via `@Nullable` (jspecify) on `modelPath`, `chatTemplateEnableThinking`, `calibration`, and several setter parameters.
- No `equals`/`hashCode` generated, by design: identity semantics.

#### Input
- No-arg constructor only; all state set via setters.
- Setter parameters: String, int, float, boolean, `Boolean` (nullable), `List<String>` (nullable), `AiCalibration` (nullable).
- `DEFAULT_THREADS` derived from `Runtime.getRuntime().availableProcessors()` at class init.

#### Output
- Getter values for provider and model configuration.
- `getDrySequenceBreakers()` and `getStopStrings()` return `Collections.unmodifiableList` views.
- No I/O, no file or network access in this class.

#### Core logic
- Field defaults: each private field initialized from its `DEFAULT_*` constant.
- Null-normalizing setters: `setLazyMode`, `setCacheTypeK`, `setCacheTypeV` map null to `""`; `setDevices`, `setProvider`, `setRemoteModel`, `setBaseUrl`, `setApiKeyEnv` map null to their defaults; `setDrySequenceBreakers`, `setStopStrings` map null to a new empty `ArrayList`.
- `chatTemplateEnableThinking` is tri-state: `null` omits the `enable_thinking` kwarg, `true`/`false` forwarded.
- Sentinel `-1` convention for "do not set" on int knobs (GPU layers, MoE/FFN CPU layers, batch sizes, seed, repeat-last-n, DRY penalty window, main GPU, threads-batch, reasoning budget).
- `0` is a meaningful value for several knobs (`gpuLayers` forces CPU, `repeatLastN` disables penalty, `dryPenaltyLastN` disables, `cacheReuse` disables, `cpuMoeLayers`/`cpuFfnLayers` keep none on CPU).
- `charsPerToken` of `0` disables automatic `maxInputChars` calculation, falling back to `maxInputChars`.
- `DEFAULT_THREADS` capped at 4 or core count, to avoid ggml spin-barrier slowdown.
- `DEFAULT_API_KEY_ENV` is empty so no key is sent to custom `baseUrl` unless a model names a variable.
- `DEFAULT_SEED` is `-1` (random per request); fixed seed pins sampling, not arithmetic.
- `DEFAULT_FLASH_ATTN` is `false` but the javadoc notes llama.cpp's own default is `auto`; `false` here means the value is forwarded explicitly.

#### Public API
- `AiGenerationConfig()` -> constructor, no-op.
- 45 bean properties, each with one getter and one setter (90 accessors total):
  - `getModelPath()` / `setModelPath(String)` -> GGUF model file path
  - `getContextSize()` / `setContextSize(int)` -> context window in tokens
  - `getMaxOutputTokens()` / `setMaxOutputTokens(int)` -> output token cap per call
  - `getTemperature()` / `setTemperature(float)` -> sampling temperature
  - `getThreads()` / `setThreads(int)` -> CPU threads for inference
  - `getCharsPerToken()` / `setCharsPerToken(int)` -> chars-per-token estimate, 0 disables
  - `getMaxInputChars()` / `setMaxInputChars(int)` -> max prompt source characters
  - `isWarnOnTrim()` / `setWarnOnTrim(boolean)` -> warn when source trimmed
  - `getTopP()` / `setTopP(float)` -> nucleus sampling threshold
  - `getTopK()` / `setTopK(int)` -> top-k sampling limit
  - `getRepeatPenalty()` / `setRepeatPenalty(float)` -> repetition penalty
  - `getMinP()` / `setMinP(float)` -> min-p truncation threshold
  - `getTopNSigma()` / `setTopNSigma(float)` -> top-n-sigma threshold
  - `getChatTemplateEnableThinking()` / `setChatTemplateEnableThinking(Boolean)` -> thinking mode tri-state
  - `isCachePrompt()` / `setCachePrompt(boolean)` -> prompt-prefix KV reuse
  - `isSwaFull()` / `setSwaFull(boolean)` -> keep full SWA KV cache
  - `getCacheReuse()` / `setCacheReuse(int)` -> KV prefix-reuse chunk size
  - `getGpuLayers()` / `setGpuLayers(int)` -> layers offloaded to GPU
  - `getCpuMoeLayers()` / `setCpuMoeLayers(int)` -> MoE layers kept on CPU
  - `getCpuFfnLayers()` / `setCpuFfnLayers(int)` -> FFN layers kept on CPU
  - `getSeed()` / `setSeed(int)` -> RNG seed, -1 random
  - `getKvUnifiedPerSlot()` / `setKvUnifiedPerSlot(int)` -> per-slot KV context cap
  - `getLazyMode()` / `setLazyMode(String)` -> tensor-read laziness
  - `getRepeatLastN()` / `setRepeatLastN(int)` -> repeat-penalty token window
  - `getCacheTypeK()` / `setCacheTypeK(String)` -> K cache quantization type
  - `getCacheTypeV()` / `setCacheTypeV(String)` -> V cache quantization type
  - `isFlashAttn()` / `setFlashAttn(boolean)` -> Flash Attention toggle
  - `getBatchSize()` / `setBatchSize(int)` -> logical batch size
  - `getUbatchSize()` / `setUbatchSize(int)` -> physical batch size
  - `getThreadsBatch()` / `setThreadsBatch(int)` -> prompt-processing thread count
  - `getMainGpu()` / `setMainGpu(int)` -> primary GPU index
  - `getDevices()` / `setDevices(String)` -> comma-separated device list
  - `getProvider()` / `setProvider(String)` -> per-model AI provider override
  - `getRemoteModel()` / `setRemoteModel(String)` -> remote model id
  - `getBaseUrl()` / `setBaseUrl(String)` -> OpenAI-compatible base URL
  - `getApiKeyEnv()` / `setApiKeyEnv(String)` -> env var name for API key
  - `getReasoningEffort()` / `setReasoningEffort(String)` -> gpt-oss reasoning level
  - `getReasoningBudgetTokens()` / `setReasoningBudgetTokens(int)` -> reasoning token cap
  - `getDryMultiplier()` / `setDryMultiplier(float)` -> DRY penalty multiplier
  - `getDryBase()` / `setDryBase(float)` -> DRY growth base
  - `getDryAllowedLength()` / `setDryAll
