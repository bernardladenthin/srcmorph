### AiGenerationConfig.java
- H: 1.0
- C: 93D12775
- D: 2026-10-31T15:13:57Z
- T: 2026-10-31T15:14:32Z
- G: 
- A: 0.0.0
- X: file
---
> Carries all AI code-summarization generation parameters from Maven config to local and remote LLM providers.

#### Purpose
- Mutable JavaBean holding single AI generation step parameters for srcmorph Maven plugin.
- Bridges Maven configuration layer and AI provider implementations with build-log diagnostics.

#### Type
- Kind: public class `AiGenerationConfig`, mutable, identity-managed.
- Annotations: `@ToString` (Lombok), `@SuppressWarnings({"NullAway.Init", "initialization.fields.uninitialized"})`.
- No `equals`/`hashCode` generated intentionally; `toString` over instance fields.

#### Input
- Constructor `AiGenerationConfig()` applies field-initializer defaults, no parameters.
- Setter parameters: `modelPath`, `contextSize`, `maxOutputTokens`, `temperature`, `threads`, `charsPerToken`, `maxInputChars`, `warnOnTrim`, `topP`, `topK`, `repeatPenalty`, `minP`, `topNSigma`, `chatTemplateEnableThinking`, `cachePrompt`, `swaFull`, `cacheReuse`, `gpuLayers`, `seed`, `cpuMoeLayers`, `cpuFfnLayers`, `kvUnifiedPerSlot`, `lazyMode`, `repeatLastN`, `cacheTypeK`, `cacheTypeV`, `flashAttn`, `batchSize`, `ubatchSize`, `threadsBatch`, `mainGpu`, `devices`, `provider`, `remoteModel`, `baseUrl`, `apiKeyEnv`, `reasoningEffort`, `reasoningBudgetTokens`, `dryMultiplier`, `dryBase`, `dryAllowedLength`, `dryPenaltyLastN`, `drySequenceBreakers`, `stopStrings`, `calibration`.
- Consumed fields: 42 `DEFAULT_*` constants, 45 private instance fields initialized from defaults.
- Nullable inputs accepted: `modelPath`, `chatTemplateEnableThinking`, `lazyMode`, `cacheTypeK`, `cacheTypeV`, `devices`, `provider`, `remoteModel`, `baseUrl`, `apiKeyEnv`, `drySequenceBreakers`, `stopStrings`, `calibration`.

#### Output
- Getters expose `AiGenerationConfig` sampling, trimming, caching, GPU, remote, DRY state.
- Mutated state: all 45 instance fields via setters; `drySequenceBreakers`/`stopStrings` replaced on set.
- Side effects: none beyond field mutation; `getDrySequenceBreakers`/`getStopStrings` return unmodifiable views.
- `toString` emits Lombok diagnostic dump for build logs.

#### Core logic
- Defaults centralization: 42 `DEFAULT_*` constants define context, sampling, trimming, cache, GPU, remote, DRY policy.
- Automatic input budgeting: `charsPerToken`, `maxInputChars`, `DEFAULT_SAFETY_MARGIN_CHARS` interplay; `0` disables auto-calculation.
- Tri-state thinking control: `chatTemplateEnableThinking` null omits `enable_thinking` kwarg, true/false forwarded verbatim.
- Sentinel forwarding convention: `-1`/empty means omit flag and keep llama.cpp binding default (`gpuLayers`, `seed`, `repeatLastN`, `batchSize`, etc.).
- Null-coalescing setters: `lazyMode`, `cacheTypeK/V`, `devices`, `provider`, `remoteModel`, `baseUrl`, `apiKeyEnv` reset to `DEFAULT_*` on null.
- List defensiveness: `drySequenceBreakers`/`stopStrings` null resets to empty `ArrayList`, getters wrap with `Collections.unmodifiableList`.
- Calibration hook: nullable `AiCalibration` overrides estimator built-in timing model.

#### Public API
- `AiGenerationConfig() -> AiGenerationConfig` creates defaults-applied config.
- `getModelPath() -> String` returns GGUF path or null.
- `setModelPath(String) -> void` sets GGUF model path.
- `getContextSize() -> int` returns context window tokens.
- `setContextSize(int) -> void` sets context window tokens.
- `getMaxOutputTokens() -> int` returns max output tokens.
- `setMaxOutputTokens(int) -> void` sets max output tokens.
- `getTemperature() -> float` returns sampling temperature.
- `setTemperature(float) -> void` sets sampling temperature.
- `getThreads() -> int` returns inference CPU threads.
- `setThreads(int) -> void` sets inference CPU threads.
- `getCharsPerToken() -> int` returns chars-per-token ratio.
- `setCharsPerToken(int) -> void` sets chars-per-token ratio.
- `getMaxInputChars() -> int` returns max input characters.
- `setMaxInputChars(int) -> void` sets max input characters.
- `isWarnOnTrim() -> boolean` reports trim warning enabled.
- `setWarnOnTrim(boolean) -> void` toggles trim warning.
- `getTopP() -> float` returns nucleus threshold.
- `setTopP(float) -> void` sets nucleus threshold.
- `getTopK() -> int` returns top-k limit.
- `setTopK(int) -> void` sets top-k limit.
- `getRepeatPenalty() -> float` returns repetition penalty.
- `setRepeatPenalty(float) -> void` sets repetition penalty.
- `getMinP() -> float` returns min-p threshold.
- `setMinP(float) -> void` sets min-p threshold.
- `getTopNSigma() -> float` returns top-n-sigma threshold.
- `setTopNSigma(float) -> void` sets top-n-sigma threshold.
- `getChatTemplateEnableThinking() -> Boolean` returns thinking mode tri-state.
- `setChatTemplateEnableThinking(Boolean) -> void` sets thinking mode tri-state.
- `isCachePrompt() -> boolean` reports prompt caching enabled.
- `setCachePrompt(boolean) -> void` toggles prompt caching.
- `isSwaFull() -> boolean` reports full SWA KV.
- `setSwaFull(boolean) -> void` toggles full SWA KV.
- `getCacheReuse() -> int` returns cache-reuse chunk size.
- `setCacheReuse(int) -> void` sets cache-reuse chunk size.
- `getGpuLayers() -> int` returns GPU offload layers.
- `setGpuLayers(int) -> void` sets GPU offload layers.
- `getCpuMoeLayers() -> int` returns CPU MoE layers.
- `setCpuMoeLayers(int) -> void` sets CPU MoE layers.
- `getSeed() -> int` returns generation RNG seed.
- `setSeed(int) -> void` sets generation RNG seed.
- `getCpuFfnLayers() -> int` returns CPU FFN layers.
- `setCpuFfnLayers(int) -> void` sets CPU FFN layers.
- `getKvUnifiedPerSlot() -> int` returns unified KV cap.
- `setKvUnifiedPerSlot(int) -> void` sets unified KV cap.
- `getLazyMode() -> String` returns tensor-read laziness.
- `setLazyMode(String) -> void` sets tensor-read laziness.
- `getRepeatLastN() -> int` returns repeat-penalty window.
- `setRepeatLastN(int) -> void` sets repeat-penalty window.
- `getCacheTypeK() -> String` returns K cache type.
- `setCacheTypeK(String) -> void` sets K cache type.
- `getCacheTypeV() -> String` returns V cache type.
- `setCacheTypeV(String) -> void` sets V cache type.
- `isFlashAttn() -> boolean` reports Flash Attention enabled.
- `setFlashAttn(boolean) -> void` toggles Flash Attention.
- `getBatchSize() -> int` returns logical batch size.
- `setBatchSize(int) -> void` sets logical batch size.
- `getUbatchSize() -> int` returns physical batch size.
- `setUbatchSize(int) -> void` sets physical batch size.
- `getThreadsBatch() -> int` returns batch thread count.
- `setThreadsBatch(int) -> void` sets batch thread count.
- `getMainGpu() -> int` returns primary GPU index.
- `setMainGpu(int) -> void` sets primary GPU index.
- `getDevices() -> String` returns device selection list.
- `setDevices(String) -> void` sets device selection list.
- `getProvider() -> String` returns model AI provider.
- `setProvider(String) -> void` sets model AI provider.
- `getRemoteModel() -> String` returns remote model id.
- `setRemoteModel(String) -> void` sets remote model id.
- `getBaseUrl() -> String` returns OpenAI-compatible base URL.
- `setBaseUrl(String) -> void` sets OpenAI-compatible base URL.
- `getApiKeyEnv() -> String` returns API key env name.
- `setApiKeyEnv(String) -> void` sets API key env name.
- `getReasoningEffort() -> String` returns gpt-oss reasoning effort.
- `setReasoningEffort(String) -> void` sets gpt-oss reasoning effort.
- `getReasoningBudgetTokens() -> int` returns reasoning token budget.
- `setReasoningBudgetTokens(int) -> void` sets reasoning token budget.
- `getDryMultiplier() -> float` returns DRY multiplier.
- `setDryMultiplier(float) -> void` sets DRY multiplier.
- `getDryBase() -> float` returns DRY base.
- `setDryBase(float) -> void` sets DRY base.
- `getDryAllowedLength() -> int` returns DRY allowed length.
- `setDryAllowedLength(int) -> void` sets DRY allowed length.
- `getDryPenaltyLastN() -> int` returns DRY look-back window.
- `setDryPenaltyLastN(int) -> void` sets DRY look-back window.
- `getDrySequenceBreakers() -> List<String>` returns unmodifiable breaker list.
- `setDrySequenceBreakers(List<String>) -> void` sets DRY breaker list.
- `getStopStrings() -> List<String>` returns unmodifiable stop list.
- `setStopStrings(List<String>) -> void` sets stop strings list.
- `getCalibration() -> AiCalibration` returns timing calibration or null.
- `setCalibration(AiCalibration) -> void` sets timing calibration.

#### Dependencies
- `java.util.ArrayList`, `java.util.Collections`, `java.util.List` for `drySequenceBreakers`, `stopStrings`.
- `lombok.ToString` for diagnostic `toString`.
- `org.jspecify.annotations.Nullable` for optional fields/parameters.
- `net.ladenthin.srcmorph.config.AiCalibration` calibration type.
- `net.ladenthin.llama.parameters.ModelParameters` referenced in thinking-mode docs.
- `Runtime.getRuntime().availableProcessors()` for `DEFAULT_THREADS` calculation.

#### Exceptions / Errors
- No thrown exceptions; no try/catch.
- Null handling: string/list setters coalesce null to `DEFAULT_*`/empty list; `modelPath`, `chatTemplateEnableThinking`, `calibration` retain null as unset.
- List getters prevent external mutation via unmodifiable wrappers; internal lists remain mutable via setter replacement.

#### Concurrency
- Mutable non-synchronized JavaBean, not thread-safe; assumes Maven-populated then read-only use.
- `DEFAULT_THREADS` computed once at class load from available processors.
