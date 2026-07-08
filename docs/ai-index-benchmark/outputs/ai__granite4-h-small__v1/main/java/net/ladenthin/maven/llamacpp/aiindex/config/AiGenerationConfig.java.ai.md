### AiGenerationConfig.java
- H: 1.0
- C: 2B41382A
- D: 2026-07-02T19:31:59Z
- T: 2026-07-02T21:50:54Z
- G: 1.0.3-SNAPSHOT
- A: 0.0.0
- X: file
---
**Facts (exact, whole file):** type declarations: 1; public declarations: 86; TODO/FIXME: 0; @Override: 0; methods (approx): 57; constructors: 1; field declarations (w/ modifier): 54

> Holds AI generation parameters (model path, sampling, prompt limits, GPU settings) passed between Maven configuration and AI provider.

#### Purpose
* Mutable JavaBean for AI inference settings.
* Transports configuration from Maven plugin to provider implementations.

#### Type
Class, final Lombok @ToString, mutable, no inheritance.

#### Input
* Constructor: no arguments.
* Setter methods: all fields.
* Injected via Maven plugin framework.
* Optional `AiCalibration` instance.

#### Output
* Getter methods expose all fields.
* `getDrySequenceBreakers` & `getStopStrings` return unmodifiable lists.
* `isCachePrompt`, `isSwaFull`, etc. expose boolean flags.

#### Core logic
* Stores primitive and string configuration values.
* Provides defensive copying for list getters.
* Handles `null` for optional fields (`AiCalibration`, list setters).
* No additional computation beyond storing and retrieving values.

#### Public API
* `String getModelPath() -> model file path`
* `void setModelPath(String) -> set model path`
* `int getContextSize() -> context window size`
* `void setContextSize(int) -> set context size`
* `int getMaxOutputTokens() -> max output tokens`
* `void setMaxOutputTokens(int) -> set max output`
* `float getTemperature() -> sampling temperature`
* `void setTemperature(float) -> set temperature`
* `int getThreads() -> CPU thread count`
* `void setThreads(int) -> set thread count`
* `int getCharsPerToken() -> chars per token`
* `void setCharsPerToken(int) -> set ratio`
* `int getMaxInputChars() -> max input chars`
* `void setMaxInputChars(int) -> set max input`
* `boolean isWarnOnTrim() -> trim warning flag`
* `void setWarnOnTrim(boolean) -> set trim warning`
* `float getTopP() -> nucleus threshold`
* `void setTopP(float) -> set top‑p`
* `int getTopK() -> top‑k limit`
* `void setTopK(int) -> set top‑k`
* `float getRepeatPenalty() -> repetition penalty`
* `void setRepeatPenalty(float) -> set repeat penalty`
* `float getMinP() -> min‑p threshold`
* `void setMinP(float) -> set min‑p`
* `float getTopNSigma() -> top‑sigma threshold`
* `void setTopNSigma(float) -> set top‑sigma`
* `boolean isChatTemplateEnableThinking() -> chat‑template flag`
* `void setChatTemplateEnableThinking(boolean) -> set flag`
* `boolean isCachePrompt() -> prompt cache flag`
* `boolean isSwaFull() -> full‑SWA flag`
* `void setSwaFull(boolean) -> set full‑SWA`
* `int getCacheReuse() -> prefix reuse size`
* `void setCacheReuse(int) -> set reuse size`
* `int getGpuLayers() -> GPU layer count`
* `void setGpuLayers(int) -> set GPU layers`
* `int getMainGpu() -> primary GPU index`
* `void setMainGpu(int) -> set primary GPU`
* `String getDevices() -> device list`
* `void setDevices(String) -> set device list`
* `void setCachePrompt(boolean) -> set prompt cache`
* `String getReasoningEffort() -> reasoning effort`
* `void setReasoningEffort(String) -> set effort`
* `int getReasoningBudgetTokens() -> reasoning budget`
* `void setReasoningBudgetTokens(int) -> set budget`
* `float getDryMultiplier() -> DRY multiplier`
* `void setDryMultiplier(float) -> set multiplier`
* `float getDryBase() -> DRY base`
* `void setDryBase(float) -> set base`
* `int getDryAllowedLength() -> DRY allowed length`
* `void setDryAllowedLength(int) -> set length`
* `int getDryPenaltyLastN() -> DRY penalty window`
* `void setDryPenaltyLastN(int) -> set window`
* `List<String> getDrySequenceBreakers() -> unmodifiable breakers`
* `void setDrySequenceBreakers(List<String>) -> set breakers`
* `List<String> getStopStrings() -> unmodifiable stops`
* `void setStopStrings(List<String>) -> set stops`
* `AiCalibration getCalibration() -> calibration`
* `void setCalibration(AiCalibration) -> set calibration`

#### Dependencies
* `java.util.ArrayList`, `java.util.Collections`, `java.util.List`
* `lombok.ToString`
* `org.jspecify.annotations.Nullable`
* `AiCalibration`

#### Exceptions / Errors
* No exceptions thrown; setters accept null for optional fields.

#### Concurrency
* All fields are mutable; no synchronization. Intended for single‑threaded Maven configuration usage.
