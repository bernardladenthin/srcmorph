### LlamaCppJniConfig.java
- H: 1.0
- C: 13EDB813
- D: 2026-10-29T13:54:46Z
- T: 2026-10-29T13:57:12Z
- G: 
- A: 0.0.0
- X: file
---
> Provides an immutable configuration object for controlling parameters when using the llama.cpp JNI provider for AI generation.

#### Purpose
* Defines the immutable configuration state for LlamaCppJniConfig.
* Provides a fluent Builder pattern for constructing LlamaCppJniConfig instances.

#### Type
final class LlamaCppJniConfig; final static class Builder.
Implements @ConvertToRecord, @ToString, @EqualsAndHashCode.

#### Input
* Constructor: private LlamaCppJniConfig(Builder builder) -> Takes one Builder instance.
* Builder: Builder(String modelPath) -> Requires non-null String modelPath.
* Builder Setters: Various methods accepting primitive types (int, float, boolean) or String/List<String> for configuration.

#### Output
* LlamaCppJniConfig: Returns an immutable configuration object.
* Builder: Returns the Builder instance for chaining.

#### Core logic
* LlamaCppJniConfig constructor copies all 37 fields from the provided Builder.
* Builder initializes all fields with defaults from AiGenerationConfig upon instantiation.
* Builder setters allow overriding defaults for specific configuration parameters.
* Builder.build() instantiates the final immutable LlamaCppJniConfig using the current builder state.

#### Public API
modelPath() -> String: Returns the GGUF model file path.
contextSize() -> int: Returns the context window size in tokens.
maxOutputTokens() -> int: Returns the maximum number of output tokens per call.
temperature() -> float: Returns the sampling temperature.
threads() -> int: Returns the number of CPU threads.
topP() -> float: Returns the nucleus-sampling probability threshold.
topK() -> int: Returns the top-k sampling limit.
minP() -> float: Returns the min-p sampling threshold.
topNSigma() -> float: Returns the top-n-sigma sampling threshold.
repeatPenalty() -> float: Returns the repetition penalty.
chatTemplateEnableThinking() -> @Nullable Boolean: Returns chat-template thinking mode setting.
cachePrompt() -> boolean: Returns whether llama.cpp prompt caching is enabled.
swaFull() -> boolean: Returns whether the full-size SWA KV cache is kept.
cacheReuse() -> int: Returns the KV prefix-reuse minimum chunk size.
gpuLayers() -> int: Returns the number of model layers to offload to GPU.
seed() -> int: Returns the RNG seed used for generation.
cpuMoeLayers() -> int: Returns the number of MoE layers kept on CPU.
cpuFfnLayers() -> int: Returns the number of FFN layers kept on CPU.
kvUnifiedPerSlot() -> int: Returns the per-slot unified KV context cap.
lazyMode() -> String: Returns the tensor-read laziness setting.
repeatLastN() -> int: Returns the repeat-penalty window size.
cacheTypeK() -> String: Returns the KV-cache data type for K.
cacheTypeV() -> String: Returns the KV-cache data type for V.
flashAttn() -> boolean: Returns whether Flash Attention is enabled.
batchSize() -> int: Returns the logical batch size.
ubatchSize() -> int: Returns the physical batch size.
threadsBatch() -> int: Returns the batch/prompt-processing thread count.
mainGpu() -> int: Returns the primary GPU index.
devices() -> String: Returns the device selection string.
provider() -> String: Returns the AI provider of this model.
remoteModel() -> String: Returns the model ID for OpenAI-compatible provider.
baseUrl() -> String: Returns the base URL for OpenAI-compatible API.
apiKeyEnv() -> String: Returns the environment variable name for API key.
reasoningEffort() -> String: Returns the gpt-oss reasoning-effort kwarg value.
reasoningBudgetTokens() -> int: Returns the reasoning/think-token budget.
dryMultiplier() -> float: Returns the DRY sampling multiplier.
dryBase() -> float: Returns the DRY base value.
dryAllowedLength() -> int: Returns the DRY allowed length.
dryPenaltyLastN() -> int: Returns the DRY penalty look-back window.
drySequenceBreakers() -> List<String>: Returns unmodifiable list of DRY sequence breakers.
stopStrings() -> List<String>: Returns unmodifiable list of stop strings.
builder(String modelPath) -> Builder: Starts building configuration with defaults.

#### Dependencies
java.util.Collections, java.util.List, java.util.Objects
lombok.EqualsAndHashCode, lombok.ToString
net.ladenthin.srcmorph.config.AiGenerationConfig
net.ladenthin.srcmorph.support.ConvertToRecord
org.jspecify.annotations.Nullable

#### Exceptions / Errors
* NullPointerException: Thrown in Builder constructor if modelPath is null.
* Null handling: Builder setters handle null inputs for String/List<String> by restoring defaults.

#### Concurrency
* LlamaCppJniConfig is immutable, ensuring thread safety for configuration state.
* Builder is explicitly noted as not thread-safe.
