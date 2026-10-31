### AiGenerationConfig.java
- H: 1.0
- C: 93D12775
- D: 2026-10-31T08:11:47Z
- T: 2026-10-31T08:17:53Z
- G: 
- A: 0.0.0
- X: file
---
> Carries all parameters for a single AI generation step between Maven configuration and AI provider implementations.

#### Purpose
- Mutable configuration object for AI generation steps
- Bridges Maven configuration layer and AI provider implementations
- Holds model path, sampling parameters, retry policy, input trimming limits, stop strings

#### Type
- class AiGenerationConfig
- final, @ToString (Lombok), @Nullable (jspecify)
- mutable JavaBean with setters, no generated equals/hashCode

#### Input
- constructor: no parameters
- setters for all fields
- static final constants for defaults
- List<String> drySequenceBreakers, stopStrings

#### Output
- getters for all fields
- unmodifiable views of drySequenceBreakers and stopStrings
- side effects: none in this class

#### Core logic
- No core logic; pure configuration holder
- Defaults applied via static final constants
- Mutable fields initialized to defaults in field declarations

#### Public API
- AiGenerationConfig() -> void constructor
- getModelPath() -> @Nullable String
- setModelPath(@Nullable String) -> void
- getContextSize() -> int
- setContextSize(int) -> void
- getMaxOutputTokens() -> int
- setMaxOutputTokens(int) -> void
- getTemperature() -> float
- setTemperature(float) -> void
- getThreads() -> int
- setThreads(int) -> void
- getCharsPerToken() -> int
- setCharsPerToken(int) -> void
- getMaxInputChars() -> int
- setMaxInputChars(int) -> void
- isWarnOnTrim() -> boolean
- setWarnOnTrim(boolean) -> void
- getTopP() -> float
- setTopP(float) -> void
- getTopK() -> int
- setTopK(int) -> void
- getRepeatPenalty() -> float
- setRepeatPenalty(float) -> void
- getMinP() -> float
- setMinP(float) -> void
- getTopNSigma() -> float
- setTopNSigma(float) -> void
- getChatTemplateEnableThinking() -> @Nullable Boolean
- setChatTemplateEnableThinking(@Nullable Boolean) -> void
- isCachePrompt() -> boolean
- setCachePrompt(boolean) -> void
- isSwaFull() -> boolean
- setSwaFull(boolean) -> void
- getCacheReuse() -> int
- setCacheReuse(int) -> void
- getGpuLayers() -> int
- setGpuLayers(int) -> void
- getCpuMoeLayers() -> int
- setCpuMoeLayers(int) -> void
- getSeed() -> int
- setSeed(int) -> void
- getCpuFfnLayers() -> int
- setCpuFfnLayers(int) -> void
- getKvUnifiedPerSlot() -> int
- setKvUnifiedPerSlot(int) -> void
- getLazyMode() -> String
- setLazyMode(@Nullable String) -> void
- getRepeatLastN() -> int
- setRepeatLastN(int) -> void
- getCacheTypeK() -> String
- setCacheTypeK(@Nullable String) -> void
- getCacheTypeV() -> String
- setCacheTypeV(@Nullable String) -> void
- isFlashAttn() -> boolean
- setFlashAttn(boolean) -> void
- getBatchSize() -> int
- setBatchSize(int) -> void
- getUbatchSize() -> int
- setUbatchSize(int) -> void
- getThreadsBatch() -> int
- setThreadsBatch(int) -> void
- getMainGpu() -> int
- setMainGpu(int) -> void
- getDevices() -> String
- setDevices(String) -> void
- getProvider() -> String
- setProvider(String) -> void
- getRemoteModel() -> String
- setRemoteModel(String) -> void
- getBaseUrl() -> String
- setBaseUrl(String) -> void
- getApiKeyEnv() -> String
- setApiKeyEnv(String) -> void
- getReasoningEffort() -> String
- setReasoningEffort(String) -> void
- getReasoningBudgetTokens() -> int
- setReasoningBudgetTokens(int) -> void
- getDryMultiplier() -> float
- setDryMultiplier(float) -> void
- getDryBase() -> float
- setDryBase(float) -> void
- getDryAllowedLength() -> int
- setDryAllowedLength(int) -> void
- getDryPenaltyLastN() -> int
- setDryPenaltyLastN(int) -> void
- getDrySequenceBreakers() -> List<String>
- setDrySequenceBreakers(@Nullable List<String>) -> void
- getStopStrings() -> List<String>
- setStopStrings(@Nullable List<String>) -> void
- getCalibration() -> @Nullable AiCalibration
- setCalibration(@Nullable AiCalibration) -> void

#### Dependencies
- java.util.ArrayList
- java.util.Collections
- java.util.List
- lombok.ToString
- org.jspecify.annotations.Nullable

#### Exceptions / Errors
- None thrown or caught in this class

#### Concurrency
- No concurrency notes; mutable JavaBean not thread-safe by design
