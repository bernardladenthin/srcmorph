### AiGenerationConfig.java
- H: 1.0
- C: 93D12775
- D: 2026-10-30T00:29:58Z
- T: 2026-10-30T00:34:14Z
- G: 
- A: 0.0.0
- X: file
---
> Configures model selection, prompt limits, sampling, hardware use, and provider options for one AI generation step.

#### Purpose
- `AiGenerationConfig` carries Maven-configured generation settings to AI providers.
- Supplies defaults for inference, prompt trimming, caching, hardware, reasoning, and remote APIs.

#### Type
- Mutable public class; no superclass or interfaces declared.
- `@ToString`; Lombok generates `toString` only. `equals` and `hashCode` are not generated.
- Public no-argument constructor.
- 42 public default constants, including `DEFAULT_CONTEXT_SIZE`, `DEFAULT_TEMPERATURE`, `DEFAULT_PROVIDER`, and `DEFAULT_DRY_MULTIPLIER`.

#### Input
- Setters accept model path, context and token limits, sampling settings, provider/API options, hardware parameters, stop strings, DRY sequence breakers, and optional `AiCalibration`.
- No files, streams, or other resources are read by this class.

#### Output
- Stores configuration as mutable instance fields for consumers to retrieve.
- List getters return unmodifiable views; setters accept lists or reset to empty on `null`.
- Lombok-generated `toString` provides field-based diagnostics.

#### Core logic
- Initializes configuration fields from `DEFAULT_*` constants.
- Provides JavaBean getters and setters; performs no generation or validation.
- Normalizes `null` to defaults for selected strings and lists; preserves nullable model path, thinking mode, and calibration.
- `charsPerToken` and `maxInputChars` carry inputs for automatic prompt-limit calculation; calculation is not performed here.

#### Public API
- `AiGenerationConfig() -> AiGenerationConfig` — initialize defaults.
- 90 getter/setter methods across 45 property pairs:
  - Model/prompt: `get/setModelPath`, `get/setContextSize`, `get/setMaxOutputTokens`, `get/setCharsPerToken`, `get/setMaxInputChars`, `is/setWarnOnTrim`.
  - Sampling: `get/setTemperature`, `get/setTopP`, `get/setTopK`, `get/setMinP`, `get/setTopNSigma`, `get/setRepeatPenalty`, `get/setRepeatLastN`.
  - Thinking/reasoning: `get/setChatTemplateEnableThinking`, `get/setReasoningEffort`, `get/setReasoningBudgetTokens`.
  - Cache/inference: `is/setCachePrompt`, `is/setSwaFull`, `get/setCacheReuse`, `get/setSeed`, `get/setLazyMode`, `get/setCacheTypeK`, `get/setCacheTypeV`, `is/setFlashAttn`, `get/setBatchSize`, `get/setUbatchSize`, `get/setThreads`, `get/setThreadsBatch`.
  - Hardware: `get/setGpuLayers`, `get/setCpuMoeLayers`, `get/setCpuFfnLayers`, `get/setKvUnifiedPerSlot`, `get/setMainGpu`, `get/setDevices`.
  - Provider: `get/setProvider`, `get/setRemoteModel`, `get/setBaseUrl`, `get/setApiKeyEnv`.
  - DRY: `get/setDryMultiplier`, `get/setDryBase`, `get/setDryAllowedLength`, `get/setDryPenaltyLastN`, `get/setDrySequenceBreakers`.
  - Other: `get/setStopStrings`, `get/setCalibration`.

#### Dependencies
- `AiCalibration`
- `java.util.ArrayList`, `java.util.Collections`, `java.util.List`
- Lombok `@ToString`
- JSpecify `@Nullable`

#### Exceptions / Errors
- No explicit exceptions or validation.
- Nullable setters normalize values selectively; other setters store supplied values directly.

#### Concurrency
- Mutable and not thread-safe.
- List getters prevent mutation through the returned view, but the view reflects backing-list changes.
