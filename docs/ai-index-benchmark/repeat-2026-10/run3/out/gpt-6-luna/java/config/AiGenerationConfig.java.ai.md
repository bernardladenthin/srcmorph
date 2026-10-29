### AiGenerationConfig.java
- H: 1.0
- C: 93D12775
- D: 2026-10-29T14:19:17Z
- T: 2026-10-29T14:19:40Z
- G: 
- A: 0.0.0
- X: file
---
> Configures one AI generation step, including prompt limits, sampling, model/provider routing, inference hardware, reasoning, and stop behavior.

#### Purpose
- `AiGenerationConfig` carries Maven-configured generation parameters to AI provider implementations.
- Supplies defaults for llama.cpp and OpenAI-compatible generation, plus optional timing calibration.

#### Type
- Public mutable class; no superclass or implemented interfaces declared.
- `@ToString` generates a field-based diagnostic string; equality and hash code are not generated.
- `@Nullable` marks optional model path, thinking mode, and calibration.

#### Input
- No-argument constructor; initializes configuration fields to defaults.
- Setters accept generation, input-trimming, sampling, caching, hardware, provider, reasoning, DRY, stop-string, and calibration settings.
- List setters accept DRY sequence breakers and stop strings; `null` resets either list to empty.
- `DEFAULT_THREADS` derives from available processors, capped at four.

#### Output
- Getters expose configured values; list getters return unmodifiable views.
- Mutable state is held in instance fields. Null passed to selected string setters resets to that setting’s default.
- Default values cover context/output limits, sampling, prompt/KV caching, hardware, provider routing, reasoning, and repetition control.

#### Core logic
- Acts as a JavaBean configuration carrier; setters assign values without validating ranges.
- Provides defaults for context size, output tokens, temperature, thread count, input trimming, and sampling.
- Uses tri-state `chatTemplateEnableThinking`: `null` leaves the model template default; `true` or `false` is explicit.
- Uses sentinel values such as `-1` and empty strings to leave selected llama.cpp options unset.
- Supports OpenAI-compatible endpoint/model/API-key environment-variable configuration; the API key itself is not stored.
- Supports DRY repetition settings, stop strings, and optional per-machine `AiCalibration`.

#### Public API
- `AiGenerationConfig()` → constructor; initializes defaults.
- Getter/setter pairs for `modelPath`, `contextSize`, `maxOutputTokens`, `temperature`, `threads`, `charsPerToken`, `maxInputChars`, `warnOnTrim`, `topP`, `topK`, `repeatPenalty`, `minP`, `topNSigma`, `chatTemplateEnableThinking`, `cachePrompt`, `swaFull`, `cacheReuse`, `gpuLayers`, `cpuMoeLayers`, `seed`, `cpuFfnLayers`, `kvUnifiedPerSlot`, `lazyMode`, `repeatLastN`, `cacheTypeK`, `cacheTypeV`, `flashAttn`, `batchSize`, `ubatchSize`, `threadsBatch`, `mainGpu`, `devices`, `provider`, `remoteModel`, `baseUrl`, `apiKeyEnv`, `reasoningEffort`, `reasoningBudgetTokens`, `dryMultiplier`, `dryBase`, `dryAllowedLength`, `dryPenaltyLastN`, `drySequenceBreakers`, `stopStrings`, and `calibration`; read or update configuration.
- Getter types match their values; `setX(value) -> void`. `warnOnTrim`, `cachePrompt`, `swaFull`, and `flashAttn` use `isX() -> boolean`; other getters use `getX()`.
- `getDrySequenceBreakers() -> List<String>` and `getStopStrings() -> List<String>`; return unmodifiable views.
- Public constants `DEFAULT_*`; expose defaults for generation, sampling, caching, hardware, provider, reasoning, and DRY configuration.

#### Dependencies
- `AiCalibration`
- Java: `ArrayList`, `Collections`, `List`, `Runtime`
- Lombok: `ToString`
- JSpecify: `Nullable`

#### Exceptions / Errors
- No explicit exceptions or validation.
- Nullable setters for `lazyMode`, `cacheTypeK`, `cacheTypeV`, `devices`, `provider`, `remoteModel`, `baseUrl`, and `apiKeyEnv` reset to defaults; list setters reset to empty lists.

#### Concurrency
- Mutable and unsynchronized; no thread-safety guarantees.
- List getters prevent mutation through the returned view, but setters retain the supplied list reference.
