### LlamaCppJniConfig.java
- H: 1.0
- C: 13EDB813
- D: 2026-10-29T13:48:14Z
- T: 2026-10-29T13:50:20Z
- G: 
- A: 0.0.0
- X: file
---
> Provides an immutable, comprehensive configuration object for controlling parameters when interacting with a llama.cpp JNI provider.

#### Purpose
* Immutable configuration container for LlamaCppJniConfig.
* Builder pattern implementation via LlamaCppJniConfig.Builder for configuration setup.

#### Type
final class LlamaCppJniConfig; final static class Builder.
Implements: @ConvertToRecord, @ToString, @EqualsAndHashCode.

#### Input
* Constructor: LlamaCppJniConfig(Builder builder) -> Takes fully populated Builder instance.
* Builder: Builder(String modelPath) -> Requires non-null modelPath.
* Builder setters: Accept various primitive types and List<String> parameters.

#### Output
* LlamaCppJniConfig: Returns an immutable configuration object.
* Builder: Returns 'this' builder instance for chaining.

#### Core logic
* LlamaCppJniConfig constructor copies all 37 fields from the Builder.
* LlamaCppJniConfig.builder(String modelPath) initializes Builder with required modelPath and defaults.
* Builder setters allow granular configuration of 37 parameters, applying defaults from AiGenerationConfig.
* Builder.build() finalizes configuration by calling LlamaCppJniConfig constructor.

#### Public API
modelPath() -> String: Retrieves the GGUF model file path.
contextSize() -> int: Retrieves the context window size in tokens.
maxOutputTokens() -> int: Retrieves the maximum number of output tokens.
temperature() -> float: Retrieves the sampling temperature.
threads() -> int: Retrieves the number of CPU threads.
topP() -> float: Retrieves the nucleus-sampling probability threshold.
topK() -> int: Retrieves the top-k sampling limit.
minP() -> float: Retrieves the min-p sampling threshold.
topNSigma() -> float: Retrieves the top-n-sigma sampling threshold.
repeatPenalty() -> float: Retrieves the repetition penalty.
chatTemplateEnableThinking() -> @Nullable Boolean: Retrieves chat-template thinking mode.
cachePrompt() -> boolean: Checks if llama.cpp prompt caching is enabled.
swaFull() -> boolean: Checks if full-size SWA KV cache is kept.
cacheReuse() -> int: Retrieves KV prefix-reuse minimum chunk size.
gpuLayers() -> int: Retrieves number of model layers offloaded to GPU.
seed() -> int: Retrieves the RNG seed used for generation.
cpuMoeLayers() -> int: Retrieves number of MoE layers kept on CPU.
cpuFfnLayers() -> int: Retrieves number of FFN layers kept on CPU.
kvUnifiedPerSlot() -> int: Retrieves per-slot unified KV context cap.
lazyMode() -> String: Retrieves tensor-read laziness setting.
repeatLastN() -> int: Retrieves the repeat-penalty window.
cacheTypeK() -> String: Retrieves KV-cache data type for K.
cacheTypeV() -> String: Retrieves KV-cache data type for V.
flashAttn() -> boolean: Checks if Flash Attention is enabled.
batchSize() -> int: Retrieves the logical batch size.
ubatchSize() -> int: Retrieves the physical batch size.
threadsBatch() -> int: Retrieves batch/prompt-processing thread count.
mainGpu() -> int: Retrieves the primary GPU index.
devices() -> String: Retrieves the device selection string.
provider() -> String: Retrieves the AI provider of this model.
remoteModel() -> String: Retrieves the model ID for OpenAI-compatible provider.
baseUrl() -> String: Retrieves the base URL for OpenAI-compatible API.
apiKeyEnv() -> String: Retrieves the environment variable name for API key.
reasoningEffort() -> String: Retrieves gpt-oss reasoning-effort kwarg value.
reasoningBudgetTokens() -> int: Retrieves the reasoning/think-token budget.
dryMultiplier() -> float: Retrieves the DRY sampling multiplier.
dryBase() -> float: Retrieves the DRY base.
dryAllowedLength() -> int: Retrieves the DRY allowed length.
dryPenaltyLastN() -> int: Retrieves the DRY penalty look-back window.
drySequenceBreakers() -> List<String>: Retrieves unmodifiable list of DRY sequence breakers.
stopStrings() -> List<String>: Retrieves unmodifiable list of stop strings.
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
Builder.repeatLastN(int) -> Builder: Sets the repeat-penalty window.
Builder.cacheTypeK(String) -> Builder: Sets KV-cache data type for K.
Builder.cacheTypeV(String) -> Builder: Sets KV-cache data type for V.
Builder.flashAttn(boolean) -> Builder: Sets Flash Attention enablement.
Builder.batchSize(int) -> Builder: Sets the logical batch size.
Builder.ubatchSize(int) -> Builder: Sets the physical batch size.
Builder.threadsBatch(int) -> Builder: Sets batch/prompt-processing thread count.
Builder.mainGpu(int) -> Builder: Sets primary GPU index.
Builder.devices(String) -> Builder: Sets device selection string.
Builder.provider(String) -> Builder: Sets the AI provider.
Builder.remoteModel(String) -> Builder: Sets the remote model ID.
Builder.baseUrl(String) -> Builder: Sets the base URL.
Builder.apiKeyEnv(String) -> Builder: Sets the API key environment variable name.
Builder.reasoningEffort(String) -> Builder: Sets reasoning effort kwarg value.
Builder.reasoningBudgetTokens(int) -> Builder: Sets reasoning/think-token budget.
Builder.dryMultiplier(float) -> Builder: Sets DRY sampling multiplier.
Builder.dryBase(float) -> Builder: Sets DRY base.
Builder.dryAllowedLength(int) -> Builder: Sets DRY allowed length.
Builder.dryPenaltyLastN(int) -> Builder: Sets DRY penalty look-back window.
Builder.drySequenceBreakers(List<String>) -> Builder: Sets DRY sequence breakers.
Builder.stopStrings(List<String>) -> Builder: Sets stop strings.
Builder.build() -> LlamaCppJniConfig: Constructs the immutable configuration.

#### Dependencies
java.util.Collections, java.util.List, java.util.Objects
lombok.EqualsAndHashCode, lombok.ToString
net.ladenthin.srcmorph.config.AiGenerationConfig
net.ladenthin.srcmorph.support.ConvertToRecord
org.jspecify.annotations.Nullable

#### Exceptions / Errors
* NullPointerException: Thrown by Builder constructor if modelPath is null.
* Null handling: Builder setters handle null inputs for String/List fields by reverting to defaults.

#### Concurrency
* LlamaCppJniConfig is immutable and thread-safe after construction.
* Builder is not thread-safe; intended for single-thread population.
