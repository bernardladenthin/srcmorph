### AiGenerationConfig.java
- H: 1.0
- C: 93D12775
- D: 2026-10-29T08:10:04Z
- T: 2026-10-29T08:10:20Z
- G: 
- A: 0.0.0
- X: file
---
> Configures AI generation parameters for models (context size, sampling, threading, caching) in a mutable, extensible format

#### Purpose
- Defines configurable parameters for AI model generation steps  
- Bridges Maven plugin configuration with AI provider implementations  

#### Type
- Class (public, mutable)  
- Extends: none  
- Implements: none  
- Generics: none  
- Notable annotations: @ToString, @Nullable, @SuppressWarnings("NullAway.Init", "initialization.fields.uninitialized")  

#### Input
- Constructor: empty (default instance)  
- Method parameters: all setters accept typed values (e.g., int, float, String, List<String>)  
- Injected dependencies: none  
- Consumed fields: instance fields (all mutable)  

#### Output
- Return types: getters return fields directly (int, float, boolean, String, List<String>, @Nullable)  
- Produced state: mutable fields updated via setters  
- Side effects: none  

#### Core logic
- Stores AI generation parameters in a mutable object  
- Provides default values for all configuration fields  
- Enables dynamic configuration via setters and getters  
- Manages automatic max input character calculation based on context size and token metrics  
- Supports per-model overrides for AI provider, model path, and sampling settings  

#### Public API
- getModelPath() → String (get model file path)  
- setModelPath(String) → void (set model file path)  
- getContextSize() → int (get context window size)  
- setContextSize(int) → void (set context window size)  
- getMaxOutputTokens() → int (get max output tokens)  
- setMaxOutputTokens(int) → void (set max output tokens)  
- getTemperature() → float (get sampling temperature)  
- setTemperature(float) → void (set sampling temperature)  
- getThreads() → int (get CPU threads)  
- setThreads(int) → void (set CPU threads)  
- getCharsPerToken() → int (get chars per token)  
- setCharsPerToken(int) → void (set chars per token)  
- getMaxInputChars() → int (get max input chars)  
- setMaxInputChars(int) → void (set max input chars)  
- isWarnOnTrim() → boolean (check trim warning)  
- setWarnOnTrim(boolean) → void (enable/disable trim warning)  
- getTopP() → float (get nucleus sampling)  
- setTopP(float) → void (set nucleus sampling)  
- getTopK() → int (get top-k sampling)  
- setTopK(int) → void (set top-k sampling)  
- getRepeatPenalty() → float (get repetition penalty)  
- setRepeatPenalty(float) → void (set repetition penalty)  
- getMinP() → float (get min-p sampling)  
- setMinP(float) → void (set min-p sampling)  
- getTopNSigma() → float (get top-n-sigma sampling)  
- setTopNSigma(float) → void (set top-n-sigma sampling)  
- getChatTemplateEnableThinking() → Boolean (get thinking mode)  
- setChatTemplateEnableThinking(Boolean) → void (set thinking mode)  
- isCachePrompt() → boolean (check prompt caching)  
- setCachePrompt(boolean) → void (enable/disable prompt caching)  
- isSwaFull() → boolean (check full SWA KV)  
- setSwaFull(boolean) → void (enable full SWA KV)  
- getCacheReuse() → int (get KV reuse chunk size)  
- setCacheReuse(int) → void (set KV reuse chunk size)  
- getGpuLayers() → int (get GPU layers)  
- setGpuLayers(int) → void (set GPU layers)  
- getCpuMoeLayers() → int (get MoE layers on CPU)  
- setCpuMoeLayers(int) → void (set MoE layers on CPU)  
- getSeed() → int (get RNG seed)  
- setSeed(int) → void (set RNG seed)  
- getCpuFfnLayers() → int (get FFN layers on CPU)  
- setCpuFfnLayers(int) → void (set FFN layers on CPU)  
- getKvUnifiedPerSlot() → int (get per-slot KV cap)  
- setKvUnifiedPerSlot(int) → void (set per-slot KV cap)  
- getLazyMode() → String (get tensor read laziness)  
- setLazyMode(String) → void (set tensor read laziness)  
- getRepeatLastN() → int (get repeat penalty window)  
- setRepeatLastN(int) → void (set repeat penalty window)  
- getCacheTypeK() → String (get KV cache type K)  
- setCacheTypeK(String) → void (set KV cache type K)  
- getCacheTypeV() → String (get KV cache type V)  
- setCacheTypeV(String) → void (set KV cache type V)  
- isFlashAttn() → boolean (check Flash Attention)  
- setFlashAttn(boolean) → void (enable Flash Attention)  
- getBatchSize() → int (get logical batch size)  
- setBatchSize(int) → void (set logical batch size)  
- getUbatchSize() → int (get physical batch size)  
- setUbatchSize(int) → void (set physical batch size)  
- getThreadsBatch() → int (get batch thread count)  
- setThreadsBatch(int) → void (set batch thread count)  
- getMainGpu() → int (get primary GPU index)  
- setMainGpu(int) → void (set primary GPU index)  
- getDevices() → String (get device selection)  
- setDevices(String) → void (set device selection)  
- getProvider() → String (get AI provider)  
- setProvider(String) → void (set AI provider)  
- getRemoteModel() → String (get remote model ID)  
- setRemoteModel(String) → void (set remote model ID)  
- getBaseUrl() → String (get API base URL)  
- setBaseUrl(String) → void (set API base URL)  
- getApiKeyEnv() → String (get API key env var)  
- setApiKeyEnv(String) → void (set API key env var)  
- getReasoningEffort() → String (get reasoning effort)  
- setReasoningEffort(String) → void (set reasoning effort)  
- getReasoningBudgetTokens() → int (get reasoning budget)  
- setReasoningBudgetTokens(int) → void (set reasoning budget)  
- getDryMultiplier() → float (get DRY multiplier)  
- setDryMultiplier(float) → void (set DRY multiplier)  
- getDryBase() → float (get DRY base)  
- setDryBase(float) → void (set DRY base)  
- getDryAllowedLength() → int (get DRY allowed length)  
- setDryAllowedLength(int) → void (set DRY allowed length)  
- getDryPenaltyLastN() → int (get DRY penalty window)  
- setDryPenaltyLastN(int) → void (set DRY penalty window)  
- getDrySequenceBreakers() → List<String> (get DRY breakers)  
- setDrySequenceBreakers(List<String>) → void (set DRY breakers)  
- getStopStrings() → List<String> (get stop strings)  
- setStopStrings(List<String>) → void (set stop strings)  
- getCalibration() → AiCalibration (get timing calibration)  
- setCalibration(AiCalibration) → void (set timing calibration)  

#### Dependencies
- java.util.List  
- java.util.Collections  
- java.util.ArrayList  
- lombok.ToString  
- org.jspecify.annotations.Nullable  

#### Exceptions / Errors
- No exceptions thrown or caught  
- Null handling: fields marked @Nullable, setters handle nulls gracefully  
- Error conditions: no validation or error propagation in public methods  

#### Concurrency
- Immutable getters (all return unmodifiable views)  
- Mutable fields can be modified via setters  
- No synchronization or thread-safety guarantees  
- Thread-safe only via external synchronization on field access
