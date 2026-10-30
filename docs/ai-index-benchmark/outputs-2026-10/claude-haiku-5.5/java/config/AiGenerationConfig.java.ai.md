### AiGenerationConfig.java
- H: 1.0
- C: 93D12775
- D: 2026-10-30T00:29:58Z
- T: 2026-10-30T00:30:16Z
- G: 
- A: 0.0.0
- X: file
---
> Holds the mutable parameter set (model path, sampling, batching, GPU, KV-cache, provider and retry-free limits) for one AI text-generation step.

#### Purpose
- Carries per-step generation settings from the Maven configuration layer to AI provider implementations.
- Stores defaults as `public static final` `DEFAULT_*` constants; `-1` or empty usually means "do not send, let the binding decide."

#### Type
- Public class, not final or abstract; no `extends` or `implements`.
- `@ToString` (Lombok) generates `toString` only; `equals`/`hashCode` deliberately absent (identity semantics).
- `@SuppressWarnings({"NullAway.Init", "initialization.fields.uninitialized"})`.
- Uses `@Nullable` (jspecify) on `modelPath`, `chatTemplateEnableThinking`, `calibration`, and some setter parameters.

#### Input
- No-arg constructor; all state set through setters (JavaBean style, populated by the Maven plugin framework).
- Setter parameters: `String`, `int`, `float`, `boolean`, `Boolean` (tri-state), `List<String>`, `AiCalibration`.
- Static inputs: `Runtime.getRuntime().availableProcessors()` for `DEFAULT_THREADS`.

#### Output
- Getters return current field values; list getters return `Collections.unmodifiableList` views.
- Mutates only its own instance fields; no I/O, no file or network access.

#### Core logic
- Field defaults initialized from `DEFAULT_*` constants (`DEFAULT_CONTEXT_SIZE = 32768`, `DEFAULT_TEMPERATURE = 0.15f`, `DEFAULT_TOP_P = 0.9f`, `DEFAULT_THREADS = min(4, cores)`).
- `maxInputChars` is a fallback when `charsPerToken` is `0`; otherwise intended to be derived from context size, output tokens, and `DEFAULT_SAFETY_MARGIN_CHARS`. No derivation logic exists in this class.
- Sentinel convention: `-1` means "do not send" for `gpuLayers`, `seed`, `cpuMoeLayers`, `cpuFfnLayers`, `kvUnifiedPerSlot`, `repeatLastN`, `batchSize`, `ubatchSize`, `threadsBatch`, `mainGpu`, `reasoningBudgetTokens` (`-1` = unrestricted), `dryPenaltyLastN`, `topNSigma`.
- `repeatLastN`: `0` disables the penalty; `>= 0` is meaningful.
- `chatTemplateEnableThinking` is tri-state: `null` omits the `enable_thinking` kwarg; `true`/`false` forwarded.
- `flashAttn` boolean; `false` emits nothing, leaving llama.cpp default `auto`.
- `provider` selects `llamacpp-jni`, `openai-compatible`, or `mock`; empty uses run-level `generationProvider`.
- `baseUrl` defaults to OpenRouter (`https://openrouter.ai/api/v1`); `apiKeyEnv` defaults to empty so no key is sent unless configured.
- `reasoningEffort` default `"low"`; empty omits the kwarg.
- DRY sampling group: `dryMultiplier` (`0.0` disables), `dryBase` (`1.75`), `dryAllowedLength` (`2`), `dryPenaltyLastN`, `drySequenceBreakers`.
- `stopStrings` and `drySequenceBreakers` default to empty `ArrayList`.
- `calibration` (`AiCalibration`) holds per-machine timing; `null` uses the estimator's built-in model.

#### Public API
- `AiGenerationConfig()` -> constructor, defaults applied (no-op body).
- `getX()` / `setX(x)` for each of the 45 instance fields (90 accessors total):
  - `modelPath`, `contextSize`, `maxOutputTokens`, `temperature`, `threads`, `charsPerToken`, `maxInputChars`, `warnOnTrim` (`isWarnOnTrim`), `topP`, `topK`, `minP`, `topNSigma`, `repeatPenalty`, `chatTemplateEnableThinking`, `cachePrompt` (`isCachePrompt`), `swaFull` (`isSwaFull`), `cacheReuse`, `gpuLayers`, `seed`, `cpuMoeLayers`, `cpuFfnLayers`, `kvUnifiedPerSlot`, `lazyMode`, `repeatLastN`, `cacheTypeK`, `cacheTypeV`, `flashAttn` (`isFlashAttn`), `batchSize`, `ubatchSize`, `threadsBatch`, `mainGpu`, `devices`, `provider`, `remoteModel`, `baseUrl`, `apiKeyEnv`, `reasoningEffort`, `reasoningBudgetTokens`, `dryMultiplier`, `dryBase`, `dryAllowedLength`, `dryPenaltyLastN`, `drySequenceBreakers`, `stopStrings`, `calibration`.
- Public static constants: `DEFAULT_*` values for every configurable field (defaults, not members of the accessor family).

#### Dependencies
- `java.util.ArrayList`, `java.util.Collections`, `java.util.List`
- `lombok.ToString`
- `org.jspecify.annotations.Nullable`
- `AiCalibration` (same package `net.ladenthin.srcmorph.config`, no import)
- `net.ladenthin.llama.parameters.ModelParameters` (javadoc `@link` only)
- `net.ladenthin.llama` 6.0.0 `FlashAttn` (javadoc reference only)

#### Exceptions / Errors
- No exceptions thrown or caught.
- Null handling on setters:
  - `setLazyMode`, `setCacheTypeK`, `setCacheTypeV`, `setDevices`, `setProvider`, `setRemoteModel`, `setBaseUrl`, `setApiKeyEnv`: `null` resets to the matching `DEFAULT_*`.
  - `setDrySequenceBreakers`, `setStopStrings`: `null` resets to empty `ArrayList`.
  - `setChatTemplateEnableThinking`, `setModelPath`, `setCalibration`: `null` stored as-is.
  - `setReasoningEffort`: stored without null handling.
- Getters return `null` only for `modelPath`, `chatTemplateEnableThinking`, `calibration` (when unset).

#### Concurrency
- Not thread-safe; no `synchronized`, `volatile`, or atomics.
- Mutable JavaBean; intended for single-owner population then read.
- List getters return unmodifiable views over the internal list; the backing list is still mutable via the setter reference.
