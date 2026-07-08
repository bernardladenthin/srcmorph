### AiModelDefinition.java
- H: 1.0
- C: 7AF67E46
- D: 2026-07-02T19:31:59Z
- T: 2026-07-02T21:57:02Z
- G: 1.0.3-SNAPSHOT
- A: 0.0.0
- X: file
---
**Facts (exact, whole file):** type declarations: 1; public declarations: 60; TODO/FIXME: 0; @Override: 0; methods (approx): 56; constructors: 1; field declarations (w/ modifier): 26

> A Maven plugin configuration POJO that defines a named set of AI model parameters for reuse across field‑generation goals.

#### Purpose
- Holds AI model configuration for Maven goals.
- Enables reuse of a single model definition by key.

#### Type
- Class, mutable JavaBean, no annotations except `@ToString`.

#### Input
- Constructor: default.
- Setters: `setKey`, `setModelPath`, `setContextSize`, `setMaxOutputTokens`, `setTemperature`, `setThreads`, `setCharsPerToken`, `setWarnOnTrim`, `setTopP`, `setTopK`, `setRepeatPenalty`, `setMinP`, `setTopNSigma`, `setChatTemplateEnableThinking`, `setCachePrompt`, `setSwaFull`, `setCacheReuse`, `setGpuLayers`, `setMainGpu`, `setDevices`, `setReasoningEffort`, `setReasoningBudgetTokens`, `setDryMultiplier`, `setDryBase`, `setDryAllowedLength`, `setDryPenaltyLastN`, `setDrySequenceBreakers`, `setStopStrings`, `setCalibration`.

#### Output
- Getters: `getKey`, `getModelPath`, `getContextSize`, `getMaxOutputTokens`, `getTemperature`, `getThreads`, `getCharsPerToken`, `isWarnOnTrim`, `getTopP`, `getTopK`, `getRepeatPenalty`, `getMinP`, `getTopNSigma`, `isChatTemplateEnableThinking`, `isCachePrompt`, `isSwaFull`, `getCacheReuse`, `getGpuLayers`, `getMainGpu`, `getDevices`, `getReasoningEffort`, `getReasoningBudgetTokens`, `getDryMultiplier`, `getDryBase`, `getDryAllowedLength`, `getDryPenaltyLastN`, `getDrySequenceBreakers`, `getStopStrings`, `getCalibration`.

#### Core logic
- Holds primitive and object fields with default values from `AiGenerationConfig`.
- Provides standard getters and setters for all fields.
- Defensive copying for mutable list fields (`drySequenceBreakers`, `stopStrings`).
- `setDevices` normalises null to default.
- `getDrySequenceBreakers`/`getStopStrings` return unmodifiable views if set.

#### Public API
- `AiModelDefinition() -> void` (constructor)
- `setKey(String) -> void` (store lookup key)
- `setModelPath(String) -> void` (store GGUF path)
- `setContextSize(int) -> void` (context window)
- `setMaxOutputTokens(int) -> void` (max output)
- `setTemperature(float) -> void` (sampling temperature)
- `setThreads(int) -> void` (CPU threads)
- `setCharsPerToken(int) -> void` (chars per token)
- `setWarnOnTrim(boolean) -> void` (trim warning)
- `setTopP(float) -> void` (nucleus threshold)
- `setTopK(int) -> void` (top‑k limit)
- `setRepeatPenalty(float) -> void` (repetition penalty)
- `setMinP(float) -> void` (min‑p threshold)
- `setTopNSigma(float) -> void` (top‑n‑sigma threshold)
- `setChatTemplateEnableThinking(boolean) -> void` (chat‑template thinking)
- `setCachePrompt(boolean) -> void` (prompt cache)
- `setSwaFull(boolean) -> void` (full‑size SWA)
- `setCacheReuse(int) -> void` (KV reuse chunk)
- `setGpuLayers(int) -> void` (GPU layer offload)
- `setMainGpu(int) -> void` (primary GPU)
- `setDevices(String) -> void` (device list)
- `setReasoningEffort(String) -> void` (reasoning level)
- `setReasoningBudgetTokens(int) -> void` (thinking budget)
- `setDryMultiplier(float) -> void` (DRY multiplier)
- `setDryBase(float) -> void` (DRY base)
- `setDryAllowedLength(int) -> void` (DRY allowed length)
- `setDryPenaltyLastN(int) -> void` (DRY penalty window)
- `setDrySequenceBreakers(Collection<String>) -> void` (DRY breakers)
- `setStopStrings(Collection<String>) -> void` (stop strings)
- `setCalibration(AiCalibration) -> void` (timing calibration)
- `getKey() -> String` (lookup key)
- `getModelPath() -> String` (model path)
- `getContextSize() -> int` (context)
- `getMaxOutputTokens() -> int` (max output)
- `getTemperature() -> float` (temperature)
- `getThreads() -> int` (threads)
- `getCharsPerToken() -> int` (chars per token)
- `isWarnOnTrim() -> boolean` (trim warning flag)
- `getTopP() -> float` (top‑p)
- `getTopK() -> int` (top‑k)
- `getRepeatPenalty() -> float` (repeat penalty)
- `getMinP() -> float` (min‑p)
- `getTopNSigma() -> float` (top‑n‑sigma)
- `isChatTemplateEnableThinking() -> boolean` (chat‑template flag)
- `isCachePrompt() -> boolean` (prompt cache flag)
- `isSwaFull() -> boolean` (SWA full flag)
- `getCacheReuse() -> int` (cache reuse)
- `getGpuLayers() -> int` (GPU layers)
- `getMainGpu() -> int` (main GPU)
- `getDevices() -> String` (device list)
- `getReasoningEffort() -> String` (reasoning level)
- `getReasoningBudgetTokens() -> int` (budget)
- `getDryMultiplier() -> float` (DRY multiplier)
- `getDryBase() -> float` (DRY base)
- `getDryAllowedLength() -> int` (DRY allowed length)
- `getDryPenaltyLastN() -> int` (DRY penalty)
- `getDrySequenceBreakers() -> List<String>` (unmodifiable)
- `getStopStrings() -> List<String>` (unmodifiable)
- `getCalibration() -> AiCalibration` (timing calibration)

#### Dependencies
- `java.util.*` (ArrayList, Collection, List, Collections)
- `lombok.ToString`
- `org.jspecify.annotations.Nullable`
- `net.ladenthin.maven.llamacpp.aiindex.config.AiGenerationConfig`
- `net.ladenthin.maven.llamacpp.aiindex.config.AiCalibration`

#### Exceptions / Errors
- No checked exceptions thrown; setters accept null for optional fields.

#### Concurrency
- No synchronization; instance is mutable and intended for single‑thread configuration usage.
