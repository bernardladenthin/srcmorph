### AiGenerationConfig.java
- H: 1.0
- C: 93D12775
- D: 2026-11-04T11:56:04Z
- T: 2026-11-04T11:56:27Z
- G: 
- A: 0.0.0
- X: file
- P: 0B812A3C
---
> Configures model routing, prompt limits, sampling, hardware, caching, and stopping behavior for an AI generation step.

#### Purpose
- Carries mutable per-model generation settings between Maven configuration and AI providers.
- Supplies defaults for inference, input trimming, provider selection, and calibration.

#### Type
- `public class AiGenerationConfig` — class; annotated `@ToString`; no declared supertypes.
- Uses `@Nullable` for `modelPath`, `chatTemplateEnableThinking`, and `calibration`.

#### Input
- Public no-argument constructor; initializes settings from `DEFAULT_*` constants.
- Setters accept model path, inference and sampling parameters, prompt limits, provider/API settings, hardware options, DRY options, stop strings, and optional `AiCalibration`.
- `setLazyMode`, `setCacheTypeK`, `setCacheTypeV`, `setDevices`, `setProvider`, `setRemoteModel`, `setBaseUrl`, and `setApiKeyEnv` normalize `null` to defaults; list setters reset `null` to empty lists.

#### Output
- Getters expose configuration values and optional model path, thinking mode, and calibration.
- `getDrySequenceBreakers()` and `getStopStrings()` return unmodifiable views.
- Setters mutate instance fields; Lombok-generated `toString()` supports diagnostics.

#### Core logic
- Initializes inference defaults: context size `32768`, output tokens `128`, temperature `0.15f`, and CPU threads `Math.min(4, Runtime.getRuntime().availableProcessors())`.
- Sets prompt trimming defaults: `charsPerToken=4`, `maxInputChars=120000`, `warnOnTrim=true`; this class stores limits but does not calculate them.
- Sets sampling defaults: `topP=0.9f`, `topK=40`, `minP=0.0f`, `topNSigma=-1.0f`, `repeatPenalty=1.0f`, `repeatLastN=-1`.
- Sets inference and cache defaults, including `cachePrompt=true`, `swaFull=true`, `cacheReuse=256`, `flashAttn=false`, and unset sentinels for hardware and batch options.
- Sets provider defaults: empty `provider`, `remoteModel`, and `apiKeyEnv`; `baseUrl="https://openrouter.ai/api/v1"`.
- Sets reasoning defaults: `reasoningEffort="low"`, `reasoningBudgetTokens=-1`, nullable `chatTemplateEnableThinking`.
- Sets DRY defaults: disabled `dryMultiplier=0.0f`, `dryBase=1.75f`, `dryAllowedLength=2`, `dryPenaltyLastN=-1`; initializes `drySequenceBreakers` and `stopStrings` as empty `ArrayList`s.
- `setLazyMode`, `setCacheTypeK`, `setCacheTypeV`, and string routing setters replace `null` with the corresponding `DEFAULT_*`; `setDevices` does likewise.
- List getters wrap their current lists with `Collections.unmodifiableList`; list setters retain a supplied non-null list or allocate an empty `ArrayList`.
- Defines defaults for context/output sizing, sampling, prompt limits, caching, GPU/CPU offload, KV cache, batching, provider routing, reasoning, and DRY configuration.

#### Public API
- `AiGenerationConfig()` -> constructor; applies defaults.
- Each property line gives getter / setter -> purpose:
- `getModelPath()` / `setModelPath(String)` -> GGUF model path.
- `getContextSize()` / `setContextSize(int)` -> context window.
- `getMaxOutputTokens()` / `setMaxOutputTokens(int)` -> output token limit.
- `getTemperature()` / `setTemperature(float)` -> sampling temperature.
- `getThreads()` / `setThreads(int)` -> inference thread count.
- `getCharsPerToken()` / `setCharsPerToken(int)` -> input token estimate.
- `getMaxInputChars()` / `setMaxInputChars(int)` -> prompt input character limit.
- `isWarnOnTrim()` / `setWarnOnTrim(boolean)` -> trim warning setting.
- `getTopP()` / `setTopP(float)` -> nucleus sampling threshold.
- `getTopK()` / `setTopK(int)` -> top-k limit.
- `getRepeatPenalty()` / `setRepeatPenalty(float)` -> repetition penalty.
- `getMinP()` / `setMinP(float)` -> min-p threshold.
- `getTopNSigma()` / `setTopNSigma(float)` -> top-n-sigma threshold.
- `getChatTemplateEnableThinking()` / `setChatTemplateEnableThinking(Boolean)` -> nullable thinking mode.
- `isCachePrompt()` / `setCachePrompt(boolean)` -> prompt-prefix caching.
- `isSwaFull()` / `setSwaFull(boolean)` -> full SWA KV cache.
- `getCacheReuse()` / `setCacheReuse(int)` -> KV prefix reuse chunk size.
- `getGpuLayers()` / `setGpuLayers(int)` -> GPU layer offload.
- `getCpuMoeLayers()` / `setCpuMoeLayers(int)` -> CPU MoE layers.
- `getSeed()` / `setSeed(int)` -> generation RNG seed.
- `getCpuFfnLayers()` / `setCpuFfnLayers(int)` -> CPU FFN layers.
- `getKvUnifiedPerSlot()` / `setKvUnifiedPerSlot(int)` -> per-slot KV context cap.
- `getLazyMode()` / `setLazyMode(String)` -> tensor-read laziness.
- `getRepeatLastN()` / `setRepeatLastN(int)` -> repetition penalty window.
- `getCacheTypeK()` / `setCacheTypeK(String)` -> K-cache data type.
- `getCacheTypeV()` / `setCacheTypeV(String)` -> V-cache data type.
- `isFlashAttn()` / `setFlashAttn(boolean)` -> Flash Attention setting.
- `getBatchSize()` / `setBatchSize(int)` -> logical batch size.
- `getUbatchSize()` / `setUbatchSize(int)` -> physical batch size.
- `getThreadsBatch()` / `setThreadsBatch(int)` -> prompt-processing threads.
- `getMainGpu()` / `setMainGpu(int)` -> primary GPU index.
- `getDevices()` / `setDevices(String)` -> selected backend devices.
- `getProvider()` / `setProvider(String)` -> per-model AI provider.
- `getRemoteModel()` / `setRemoteModel(String)` -> remote model identifier.
- `getBaseUrl()` / `setBaseUrl(String)` -> OpenAI-compatible API base URL.
- `getApiKeyEnv()` / `setApiKeyEnv(String)` -> API-key environment variable name.
- `getReasoningEffort()` / `setReasoningEffort(String)` -> reasoning effort kwarg.
- `getReasoningBudgetTokens()` / `setReasoningBudgetTokens(int)` -> reasoning token budget.
- `getDryMultiplier()` / `setDryMultiplier(float)` -> DRY repetition penalty multiplier.
- `getDryBase()` / `setDryBase(float)` -> DRY exponential base.
- `getDryAllowedLength()` / `setDryAllowedLength(int)` -> DRY unpenalized n-gram length.
- `getDryPenaltyLastN()` / `setDryPenaltyLastN(int)` -> DRY look-back window.
- `getDrySequenceBreakers()` / `setDrySequenceBreakers(List<String>)` -> DRY sequence breakers.
- `getStopStrings()` / `setStopStrings(List<String>)` -> generation stop strings.
- `getCalibration()` / `setCalibration(AiCalibration)` -> per-machine timing calibration.

#### Dependencies
- `java.util.ArrayList`, `java.util.Collections`, `java.util.List`
- `lombok.ToString`
- `org.jspecify.annotations.Nullable`
- `AiCalibration`
- `Runtime`

#### Exceptions / Errors
- No explicit exceptions or validation checks.
- Nullable model path, thinking mode, and calibration; nullable list/string setter inputs are handled as described above.
- Setters generally store values without range validation.

#### Concurrency
- Mutable JavaBean; no synchronization or thread-safety guarantees.
- List getters prevent mutation through the returned view, but setters can replace the backing list.
