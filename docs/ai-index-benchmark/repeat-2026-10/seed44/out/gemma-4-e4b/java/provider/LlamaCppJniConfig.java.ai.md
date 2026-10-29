### LlamaCppJniConfig.java
- H: 1.0
- C: 13EDB813
- D: 2026-10-29T14:01:55Z
- T: 2026-10-29T14:04:07Z
- G: 
- A: 0.0.0
- X: file
---
> Provides an immutable, comprehensive configuration object for controlling parameters when using the llama.cpp JNI provider for AI generation.

#### Purpose
* Defines the immutable configuration state for LlamaCppJniConfig.
* Provides a Builder pattern for constructing LlamaCppJniConfig instances.

#### Type
final class LlamaCppJniConfig; final static class Builder.
Implements: @ConvertToRecord, @ToString, @EqualsAndHashCode.

#### Input
* Constructor: private LlamaCppJniConfig(Builder builder) -> LlamaCppJniConfig.
* Builder: Builder(String modelPath) -> Builder.
* Builder setters: Accepts various primitive types and List<String> for configuration.

#### Output
* LlamaCppJniConfig: Immutable configuration object containing 37 fields.
* Builder: Returns itself for fluent chaining.

#### Core logic
* LlamaCppJniConfig constructor copies all 37 fields from the provided Builder instance.
* LlamaCppJniConfig.builder(String modelPath) initializes and returns a Builder, requiring modelPath.
* Builder setters allow setting individual configuration parameters, applying defaults from AiGenerationConfig if null/empty is passed.
* Builder.build() finalizes the configuration by calling the private LlamaCppJniConfig constructor.

#### Public API
modelPath() -> String: Returns the GGUF model file path.
contextSize() -> int: Returns the context window size in tokens.
maxOutputTokens() -> int: Returns the maximum number of output tokens.
temperature() -> float: Returns the sampling temperature.
threads() -> int: Returns the number of CPU threads.
topP() -> float: Returns the nucleus-sampling probability threshold.
topK() -> int: Returns the top-k sampling limit.
minP() -> float: Returns the min-p sampling threshold.
topNSigma() -> float: Returns the top-n-sigma sampling threshold.
repeatPenalty() -> float: Returns the repetition penalty.
chatTemplateEnableThinking() -> @Nullable Boolean: Returns chat-template thinking mode setting.
cachePrompt() -> boolean: Returns if llama.cpp prompt caching is enabled.
swaFull() -> boolean: Returns if full-size SWA KV cache is kept.
cacheReuse() -> int: Returns KV prefix-reuse minimum chunk size.
gpuLayers() -> int: Returns number of model layers offloaded to GPU.
seed() -> int: Returns the RNG seed used for generation.
cpuMoeLayers() -> int: Returns number of MoE layers kept on CPU.
cpuFfnLayers() -> int: Returns number of FFN layers kept on CPU.
kvUnifiedPerSlot() -> int: Returns per-slot unified KV context cap.
lazyMode() -> String: Returns tensor-read laziness setting.
repeatLastN() -> int: Returns the repeat-penalty window size.
cacheTypeK() -> String: Returns KV-cache data type for K.
cacheTypeV() -> String: Returns KV-cache data type for V.
flashAttn() -> boolean: Returns whether Flash Attention is enabled.
batchSize() -> int: Returns the logical batch size.
ubatchSize() -> int: Returns the physical batch size.
threadsBatch() -> int: Returns batch/prompt-processing thread count.
mainGpu() -> int: Returns the primary GPU index.
devices() -> String: Returns the device selection string.
provider() -> String: Returns the AI provider of this model.
remoteModel() -> String: Returns the model ID for compatible providers.
baseUrl() -> String: Returns the base URL for API calls.
apiKeyEnv() -> String: Returns the environment variable name for API key.
reasoningEffort() -> String: Returns gpt-oss reasoning-effort kwarg value.
reasoningBudgetTokens() -> int: Returns the reasoning/think-token budget.
dryMultiplier() -> float: Returns the DRY sampling multiplier.
dryBase() -> float: Returns the DRY base value.
dryAllowedLength() -> int: Returns the DRY allowed length.
dryPenaltyLastN() -> int: Returns the DRY penalty look-back window.
drySequenceBreakers() -> List<String>: Returns unmodifiable DRY sequence breakers.
stopStrings() -> List<String>: Returns unmodifiable stop strings.
Builder.contextSize(int) -> Builder: Sets context window size.
Builder.maxOutputTokens(int) -> Builder: Sets maximum output tokens.
Builder.temperature(float) -> Builder: Sets sampling temperature.
Builder.threads(int) -> Builder: Sets number of CPU threads.
Builder.topP(float) -> Builder: Sets nucleus-sampling probability threshold.
Builder.topK(int) -> Builder: Sets top-k sampling limit.
Builder.minP(float) -> Builder: Sets min-p sampling threshold.
Builder.topNSigma(float) -> Builder: Sets top-n-sigma sampling threshold.
Builder.repeatPenalty(float) -> Builder: Sets repetition penalty.
Builder.chatTemplateEnableThinking(@Nullable Boolean) -> Builder: Sets chat-template thinking mode.
Builder.cachePrompt(boolean) -> Builder: Sets prompt caching enablement.
Builder.swaFull(boolean) -> Builder: Sets full-size SWA KV cache.
Builder.cacheReuse(int) -> Builder: Sets KV prefix-reuse minimum chunk size.
Builder.gpuLayers(int) -> Builder: Sets GPU layers to offload.
Builder.seed(int) -> Builder: Sets RNG seed for generation.
Builder.cpuMoeLayers(int) -> Builder: Sets MoE layers kept on CPU.
Builder.cpuFfnLayers(int) -> Builder: Sets FFN layers kept on CPU.
Builder.kvUnifiedPerSlot(int) -> Builder: Sets per-slot unified KV context cap.
Builder.lazyMode(String) -> Builder: Sets tensor-read laziness mode.
Builder.repeatLastN(int) -> Builder: Sets repeat-penalty window.
Builder.cacheTypeK(String) -> Builder: Sets KV-cache data type for K.
Builder.cacheTypeV(String) -> Builder: Sets KV-cache data type for V.
Builder.flashAttn(boolean) -> Builder: Sets Flash Attention enablement.
Builder.batchSize(int) -> Builder: Sets logical batch size.
Builder.ubatchSize(int) -> Builder: Sets physical batch size.
Builder.threadsBatch(int) -> Builder: Sets batch/prompt-processing thread count.
Builder.mainGpu(int) -> Builder: Sets primary GPU index.
Builder.devices(String) -> Builder: Sets device selection string.
Builder.provider(String) -> Builder: Sets the AI provider.
Builder.remoteModel(String) -> Builder: Sets the remote model ID.
Builder.baseUrl(String) -> Builder: Sets the base URL.
Builder.apiKeyEnv(String) -> Builder: Sets the API key environment variable name.
Builder.reasoningEffort(String) -> Builder: Sets reasoning-effort kwarg value.
Builder.reasoningBudgetTokens(int) -> Builder: Sets reasoning/think-token budget.
Builder.dryMultiplier(float) -> Builder: Sets DRY sampling multiplier.
Builder.dryBase(float) -> Builder: Sets DRY base value.
Builder.dryAllowedLength(int) -> Builder: Sets DRY allowed length.
Builder.dryPenaltyLastN(int) -> Builder: Sets DRY penalty look-back window.
Builder.drySequenceBreakers(List<String>) -> Builder: Sets DRY sequence breakers.
Builder.stopStrings(List<String>) -> Builder: Sets stop strings.
Builder.build() -> LlamaCppJniConfig: Builds the immutable configuration.

#### Dependencies
java.util.Collections, java.util.List, java.util.Objects
lombok.EqualsAndHashCode, lombok.ToString
net.ladenthin.srcmorph.config.AiGenerationConfig
net.ladenthin.srcmorph.support.ConvertToRecord
org.jspecify.annotations.Nullable

#### Exceptions / Errors
* NullPointerException: Thrown by Builder constructor if modelPath is null.
* Null handling: Builder setters handle null inputs for String/List fields by restoring default values from AiGenerationConfig.

#### Concurrency
* LlamaCppJniConfig is immutable, ensuring thread safety for configuration state.
* Builder is not thread-safe; intended for single-threaded construction.
