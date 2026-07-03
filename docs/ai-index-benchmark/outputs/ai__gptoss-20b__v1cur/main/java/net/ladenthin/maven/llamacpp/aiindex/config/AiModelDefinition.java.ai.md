### AiModelDefinition.java
- H: 1.0
- C: 7AF67E46
- D: 2026-07-02T19:31:59Z
- T: 2026-07-02T23:03:15Z
- G: 1.0.3-SNAPSHOT
- A: 0.0.0
- X: file
---
**Facts (exact, whole file):** type declarations: 1; public declarations: 60; TODO/FIXME: 0; @Override: 0; methods (approx): 56; constructors: 1; field declarations (w/ modifier): 26

> Holds a unique key and full set of parameters for a GGUF AI model, used by the Maven plugin to configure inference.

#### Purpose
- Stores all AI model configuration values for Maven plugin use.
- Enables reuse of a single model definition across multiple generation goals.

#### Type
- Class, mutable JavaBean, `@ToString`, `@SuppressWarnings({"NullAway.Init","initialization.fields.uninitialized"})`.

#### Input
- Constructor: no parameters.
- Setters: 31 public setters (`setKey`, `setModelPath`, …, `setCalibration`).
- Direct field access via setters; no external dependencies.

#### Output
- Getters: 31 public getters (`getKey`, `getModelPath`, …, `getCalibration`).
- Mutated state: all 31 fields.
- No external side effects.

#### Core logic
- Default field values are taken from `AiGenerationConfig` constants.
- `setDevices` normalizes `null` to the default empty string.
- Collection setters (`setDrySequenceBreakers`, `setStopStrings`) copy input into a mutable list.
- Collection getters return an unmodifiable view or `null`.

#### Public API
- `AiModelDefinition()` → constructs with defaults.  
- `String getKey()` → unique lookup key.  
- `void setKey(String)` → assign key.  
- `String getModelPath()` → path to GGUF file.  
- `void setModelPath(String)` → assign model path.  
- `int getContextSize()` → context window size.  
- `void setContextSize(int)` → set context size.  
- `int getMaxOutputTokens()` → max output tokens.  
- `void setMaxOutputTokens(int)` → set max output tokens.  
- `float getTemperature()` → sampling temperature.  
- `void setTemperature(float)` → set temperature.  
- `int getThreads()` → CPU threads.  
- `void setThreads(int)` → set threads.  
- `int getCharsPerToken()` → chars-per-token ratio.  
- `void setCharsPerToken(int)` → set ratio.  
- `boolean isWarnOnTrim()` → trim warning flag.  
- `void setWarnOnTrim(boolean)` → set flag.  
- `float getTopP()` → nucleus-sampling threshold.  
- `void setTopP(float)` → set threshold.  
- `int getTopK()` → top‑k limit.  
- `void setTopK(int)` → set limit.  
- `float getRepeatPenalty()` → repetition penalty.  
- `void setRepeatPenalty(float)` → set penalty.  
- `float getMinP()` → min‑p threshold.  
- `void setMinP(float)` → set threshold.  
- `float getTopNSigma()` → top‑n‑sigma threshold.  
- `void setTopNSigma(float)` → set threshold.  
- `boolean isChatTemplateEnableThinking()` → chat‑template thinking flag.  
- `void setChatTemplateEnableThinking(boolean)` → set flag.  
- `boolean isCachePrompt()` → llama.cpp prompt caching flag.  
- `void setCachePrompt(boolean)` → set flag.  
- `boolean isSwaFull()` → full‑size SWA KV cache flag.  
- `void setSwaFull(boolean)` → set flag.  
- `int getCacheReuse()` → KV prefix‑reuse chunk size.  
- `void setCacheReuse(int)` → set chunk size.  
- `int getGpuLayers()` → GPU layer offload count.  
- `void setGpuLayers(int)` → set offload count.  
- `int getMainGpu()` → primary GPU index.  
- `void setMainGpu(int)` → set index.  
- `String getDevices()` → comma‑separated device list.  
- `void setDevices(String)` → set list or reset to default.  
- `String getReasoningEffort()` → gpt‑oss reasoning‑effort level.  
- `void setReasoningEffort(String)` → set level.  
- `int getReasoningBudgetTokens()` → reasoning token budget.  
- `void setReasoningBudgetTokens(int)` → set budget.  
- `float getDryMultiplier()` → DRY multiplier.  
- `void setDryMultiplier(float)` → set multiplier.  
- `float getDryBase()` → DRY base.  
- `void setDryBase(float)` → set base.  
- `int getDryAllowedLength()` → DRY allowed length.  
- `void setDryAllowedLength(int)` → set length.  
- `int getDryPenaltyLastN()` → DRY penalty look‑back window.  
- `void setDryPenaltyLastN(int)` → set window.  
- `List<String> getDrySequenceBreakers()` → DRY sequence breakers.  
- `void setDrySequenceBreakers(Collection<String>)` → set breakers.  
- `List<String> getStopStrings()` → stop strings.  
- `void setStopStrings(Collection<String>)` → set stop strings.  
- `AiCalibration getCalibration()` → per‑machine timing calibration.  
- `void setCalibration(AiCalibration)` → set calibration.

#### Dependencies
- `net.ladenthin.maven.llamacpp.aiindex.config.AiGenerationConfig`
- `net.ladenthin.maven.llamacpp.aiindex.config.AiCalibration`
- `java.util.*`
- `lombok.ToString`
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- No explicit exceptions thrown; setters assume valid input.

#### Concurrency
- No synchronization; intended for single‑threaded configuration use.
