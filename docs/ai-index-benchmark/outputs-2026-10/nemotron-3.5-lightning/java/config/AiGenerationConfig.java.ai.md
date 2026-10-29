### AiGenerationConfig.java
- H: 1.0
- C: 93D12775
- D: 2026-10-28T22:56:50Z
- T: 2026-10-28T22:58:17Z
- G: 
- A: 0.0.0
- X: file
---
> Configures AI generation parameters for source code transformation workflows

#### Purpose
- Mutable JavaBeans configuration object for a single AI generation step
- Carries all parameters between the Maven plugin layer and AI provider implementations

#### Type
- public class AiGenerationConfig
- Annotations: @SuppressWarnings({"NullAway.Init", "initialization.fields.uninitialized"}), @ToString (Lombok)
- No extends / implements clauses
- Key generics: List<String> for drySequenceBreakers and stopStrings fields
- Notable: @Nullable on several fields and getter return types

#### Input
- No-arg constructor initializes all fields to named static defaults
- Populated exclusively through setter methods by the Maven plugin framework
- Instance fields serve as mutable state consumed by AI provider build methods

#### Output
- Getter methods return current values of each configuration parameter
- Setter methods mutate corresponding instance fields
- Two getters return unmodifiable List views (drySequenceBreakers, stopStrings)

#### Core logic
- All instance fields initialized to static final default constants defined at class level
- Setter-driven population enables identity-based management by the Maven plugin framework
- Optional AiCalibration field supplies per-machine timing estimates; null defers to built-in estimator
- Unmodifiable list wrappers protect drySequenceBreakers and stopStrings from external mutation

#### Public API
- AiGenerationConfig() -> void initializes all fields to defaults
- getModelPath() -> @Nullable String returns model file path or null
- setModelPath(@Nullable String modelPath) -> void sets GGUF model path
- getContextSize() -> int returns context window size in tokens
- setContextSize(int contextSize) -> void sets context window size
- getMaxOutputTokens() -> int returns max output tokens per inference
- setMaxOutputTokens(int maxOutputTokens) -> void sets max output tokens
- getTemperature() -> float returns sampling temperature
- setTemperature(float temperature) -> void sets sampling temperature
- getThreads() -> int returns CPU thread count for inference
- setThreads(int threads) -> void sets CPU thread count
- getCharsPerToken() -> int returns chars-per-token ratio for auto calculation
- setCharsPerToken(int charsPerToken) -> void sets chars-per-token ratio
- getMaxInputChars() -> int returns max input characters for prompt
- setMaxInputChars(int maxInputChars) -> void sets max input characters
- isWarnOnTrim() -> boolean returns whether trim warnings are enabled
- setWarnOnTrim(boolean warnOnTrim) -> void enables trim warnings
- getTopP() -> float returns nucleus-sampling probability threshold
- setTopP(float topP) -> void sets nucleus-sampling threshold
- getTopK() -> int returns top-k sampling limit
- setTopK(int topK) -> void sets top-k sampling limit
- getRepeatPenalty() -> float returns repetition penalty value
- setRepeatPenalty(float repeatPenalty) -> void sets repetition penalty
- getMinP() -> float returns min-p sampling threshold
- setMinP(float minP) -> void sets min-p threshold
- getTopNSigma() -> float returns top-n-sigma sampling threshold
- setTopNSigma(float topNSigma) -> void sets top-n-sigma threshold
- getChatTemplateEnableThinking() -> @Nullable Boolean returns thinking mode setting
- setChatTemplateEnableThinking(@Nullable Boolean chatTemplateEnableThinking) -> void sets thinking mode kwarg
- isCachePrompt() -> boolean returns whether prompt caching is enabled
- isSwaFull() -> boolean returns whether full SWA KV cache is kept
- setSwaFull(boolean swaFull) -> void sets full SWA KV cache
- getCacheReuse() -> int returns KV prefix-reuse minimum chunk size
- setCacheReuse(int cacheReuse) -> void sets KV prefix-reuse chunk size
- getGpuLayers() -> int returns GPU layers to offload
- setGpuLayers(int gpuLayers) -> void sets GPU layers to offload
- getCpuMoeLayers() -> int returns CPU MoE layers to keep
- setCpuMoeLayers(int cpuMoeLayers) -> void sets CPU MoE layers
- getSeed() -> int returns RNG seed for generation
- setSeed(int seed) -> void sets RNG seed
- getCpuFfnLayers() -> int returns CPU FFN layers to keep
- setCpuFfnLayers(int cpuFfnLayers) -> void sets CPU FFN layers
- getKvUnifiedPerSlot() -> int returns per-slot unified KV context cap
- setKvUnifiedPerSlot(int kvUnifiedPerSlot) -> void sets per-slot KV cap
- getLazyMode() -> String returns tensor-read laziness mode
- setLazyMode(@Nullable String lazyMode) -> void sets tensor-read laziness
- getRepeatLastN() -> int returns repeat-penalty last-n window
- setRepeatLastN(int repeatLastN) -> void sets repeat-penalty window
- getCacheTypeK() -> String returns KV cache type for K
- setCacheTypeK(@Nullable String cacheTypeK) -> void sets KV cache type for K
- getCacheTypeV() -> String returns KV cache type for V
- setCacheTypeV(@Nullable String cacheTypeV) -> void sets KV cache type for V
- isFlashAttn() -> boolean returns whether Flash Attention is enabled
- setFlashAttn(boolean flashAttn) -> void enables Flash Attention
- getBatchSize() -> int returns logical batch size
- setBatchSize(int batchSize) -> void sets logical batch size
- getUbatchSize() -> int returns physical batch size
- setUbatchSize(int ubatchSize) -> void sets physical batch size
- getThreadsBatch() -> int returns batch prompt-processing thread count
- setThreadsBatch(int threadsBatch) -> void sets batch thread count
- getMainGpu() -> int returns primary GPU index
- setMainGpu(int mainGpu) -> void sets primary GPU index
- getDevices() -> String returns comma-separated device list
- setDevices(String devices) -> void sets device selection
- getProvider() -> String returns AI provider name
- setProvider(String provider) -> void sets AI provider
- getRemoteModel() -> String returns model ID for openai-compatible provider
- setRemoteModel(String remoteModel) -> void sets model ID
- getBaseUrl() -> String returns base URL of OpenAI-compatible API
- setBaseUrl(String baseUrl) -> void sets base URL
- getApiKeyEnv() -> String returns API key environment variable name
- setApiKeyEnv(String apiKeyEnv) -> void sets API key env variable name
- setCachePrompt(boolean cachePrompt) -> void enables prompt caching
- getReasoningEffort() -> String returns gpt-oss reasoning effort level
- setReasoningEffort(String reasoningEffort) -> void sets reasoning effort level
- getReasoningBudgetTokens() -> int returns think-token budget
- setReasoningBudgetTokens(int reasoningBudgetTokens) -> void sets think-token budget
- getDryMultiplier() -> float returns DRY sampling multiplier
- setDryMultiplier(float dryMultiplier) -> void sets DRY multiplier
- getDryBase() -> float returns DRY base exponential growth base
- setDryBase(float dryBase) -> void sets DRY base
- getDryAllowedLength() -> int returns max unpenalized repeating n-gram length
- setDryAllowedLength(int dryAllowedLength) -> void sets DRY allowed length
- getDryPenaltyLastN() -> int returns DRY penalty look-back window
- setDryPenaltyLastN(int dryPenaltyLastN) -> void sets DRY penalty window
- getDrySequenceBreakers() -> List<String> returns unmodifiable DRY sequence breakers list
- setDrySequenceBreakers(@Nullable List<String> drySequenceBreakers) -> void sets DRY sequence breakers
- getStopStrings() -> List<String> returns unmodifiable stop strings list
- setStopStrings(@Nullable List<String> stopStrings) -> void sets stop strings
- getCalibration() -> @Nullable AiCalibration returns timing calibration or null
- setCalibration(@Nullable AiCalibration calibration) -> void sets timing calibration

**Total: 91 public members** (1 constructor + 90 methods)

#### Dependencies
- java.util.ArrayList (imported, used for drySequenceBreakers, stopStrings fields)
- java.util.Collections (imported, used for unmodifiableList wrappers)
- java.util.List (imported, used for drySequenceBreakers, stopStrings return types)
- lombok.ToString (annotation, generates toString over instance fields)
- org.jspecify.annotations.Nullable (annotation on fields and getter return types)
- net.ladenthin.srcmorph.config.AiCalibration (referenced type, field and getter/setter)

#### Exceptions / Errors
- No declared throws clauses in any method
- Null-handling governed by @Nullable annotations on fields and return types
- equals/hashCode intentionally omitted; instances managed by identity, not value equality
- Mutability means uninitialized fields possible; @SuppressWarnings suppresses init warnings

#### Concurrency
- Class is fully mutable via setter methods; no synchronization or thread-safety guarantees
- Designed for single-threaded population by Maven plugin framework, then identity-based management
- Not safe for concurrent access without external synchronization
