### AiGenerationConfig.java
- H: 1.0
- C: 2B41382A
- D: 2026-07-02T19:31:59Z
- T: 2026-07-02T22:56:18Z
- G: 1.0.3-SNAPSHOT
- A: 0.0.0
- X: file
---
**Facts (exact, whole file):** type declarations: 1; public declarations: 86; TODO/FIXME: 0; @Override: 0; methods (approx): 57; constructors: 1; field declarations (w/ modifier): 54

> A mutable JavaBean holding all parameters for a single AI generation step (model, sampling, caching, GPU, DRY, etc.).

#### Purpose
- Stores configuration for an AI generation request.
- Supplies values to provider implementations.

#### Type
- Class `AiGenerationConfig` (mutable JavaBean).
- `@ToString` generated.
- No inheritance.

#### Input
- Constructor: no arguments, defaults initialized.
- Setter methods for every field.
- `setDevices(String)` accepts `null` to reset to default.
- `setDrySequenceBreakers(List)` and `setStopStrings(List)` accept `null` to clear.

#### Output
- Getter methods expose field values.
- `getDrySequenceBreakers()` and `getStopStrings()` return unmodifiable lists.

#### Core logic
- Simple state storage; no algorithmic processing.
- Constants provide default values.
- `setDevices` normalizes `null` to the empty default.
- `setDrySequenceBreakers` and `setStopStrings` replace `null` with empty lists.

#### Public API
- `getModelPath() -> String`  
  Retrieves GGUF model file path.  
- `setModelPath(String)`  
  Sets GGUF model file path.  
- `getContextSize() -> int`  
  Retrieves context window size.  
- `setContextSize(int)`  
  Sets context window size.  
- `getMaxOutputTokens() -> int`  
  Retrieves maximum output tokens.  
- `setMaxOutputTokens(int)`  
  Sets maximum output tokens.  
- `getTemperature() -> float`  
  Retrieves sampling temperature.  
- `setTemperature(float)`  
  Sets sampling temperature.  
- `getThreads() -> int`  
  Retrieves number of CPU threads.  
- `setThreads(int)`  
  Sets number of CPU threads.  
- `getCharsPerToken() -> int`  
  Retrieves characters‑per‑token ratio.  
- `setCharsPerToken(int)`  
  Sets characters‑per‑token ratio.  
- `getMaxInputChars() -> int`  
  Retrieves maximum input characters.  
- `setMaxInputChars(int)`  
  Sets maximum input characters.  
- `isWarnOnTrim() -> boolean`  
  Checks if trim warnings enabled.  
- `setWarnOnTrim(boolean)`  
  Enables/disables trim warnings.  
- `getTopP() -> float`  
  Retrieves nucleus‑sampling threshold.  
- `setTopP(float)`  
  Sets nucleus‑sampling threshold.  
- `getTopK() -> int`  
  Retrieves top‑k limit.  
- `setTopK(int)`  
  Sets top‑k limit.  
- `getRepeatPenalty() -> float`  
  Retrieves repetition penalty.  
- `setRepeatPenalty(float)`  
  Sets repetition penalty.  
- `getMinP() -> float`  
  Retrieves min‑p threshold.  
- `setMinP(float)`  
  Sets min‑p threshold.  
- `getTopNSigma() -> float`  
  Retrieves top‑n‑sigma threshold.  
- `setTopNSigma(float)`  
  Sets top‑n‑sigma threshold.  
- `isChatTemplateEnableThinking() -> boolean`  
  Checks chat‑template thinking mode.  
- `setChatTemplateEnableThinking(boolean)`  
  Enables/disables chat‑template thinking.  
- `isCachePrompt() -> boolean`  
  Checks if prompt prefix KV reuse enabled.  
- `setCachePrompt(boolean)`  
  Enables/disables prompt caching.  
- `isSwaFull() -> boolean`  
  Checks if full‑size SWA KV kept.  
- `setSwaFull(boolean)`  
  Enables/disables full SWA KV.  
- `getCacheReuse() -> int`  
  Retrieves cache‑reuse chunk size.  
- `setCacheReuse(int)`  
  Sets cache‑reuse chunk size.  
- `getGpuLayers() -> int`  
  Retrieves GPU layer offload count.  
- `setGpuLayers(int)`  
  Sets GPU layer offload count.  
- `getMainGpu() -> int`  
  Retrieves primary GPU index.  
- `setMainGpu(int)`  
  Sets primary GPU index.  
- `getDevices() -> String`  
  Retrieves device list.  
- `setDevices(String)`  
  Sets device list (null → default).  
- `getReasoningEffort() -> String`  
  Retrieves reasoning‑effort level.  
- `setReasoningEffort(String)`  
  Sets reasoning‑effort level.  
- `getReasoningBudgetTokens() -> int`  
  Retrieves reasoning‑budget token limit.  
- `setReasoningBudgetTokens(int)`  
  Sets reasoning‑budget token limit.  
- `getDryMultiplier() -> float`  
  Retrieves DRY multiplier.  
- `setDryMultiplier(float)`  
  Sets DRY multiplier.  
- `getDryBase() -> float`  
  Retrieves DRY base.  
- `setDryBase(float)`  
  Sets DRY base.  
- `getDryAllowedLength() -> int`  
  Retrieves DRY allowed length.  
- `setDryAllowedLength(int)`  
  Sets DRY allowed length.  
- `getDryPenaltyLastN() -> int`  
  Retrieves DRY penalty look‑back.  
- `setDryPenaltyLastN(int)`  
  Sets DRY penalty look‑back.  
- `getDrySequenceBreakers() -> List<String>`  
  Unmodifiable list of DRY sequence breakers.  
- `setDrySequenceBreakers(List)`  
  Replaces DRY sequence breakers (null → empty).  
- `getStopStrings() -> List<String>`  
  Unmodifiable list of stop strings.  
- `setStopStrings(List)`  
  Replaces stop strings (null → empty).  
- `getCalibration() -> AiCalibration`  
  Retrieves calibration (may be null).  
- `setCalibration(AiCalibration)`  
  Sets calibration.

#### Dependencies
- `java.util.ArrayList`, `java.util.Collections`, `java.util.List`.
- `lombok.ToString`.
- `org.jspecify.annotations.Nullable`.
- `net.ladenthin.maven.llamacpp.aiindex.config.AiCalibration`.

#### Exceptions / Errors
- No checked exceptions thrown.
- Null handling: setters accept `null` for lists and devices; they reset to defaults.

#### Concurrency
- No synchronization; class is not thread‑safe. Instances intended for single‑thread use or external coordination.
