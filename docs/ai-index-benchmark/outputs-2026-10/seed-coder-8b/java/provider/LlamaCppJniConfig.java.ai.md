### LlamaCppJniConfig.java
- H: 1.0
- C: 13EDB813
- D: 2026-10-29T00:10:55Z
- T: 2026-10-29T00:15:12Z
- G: 
- A: 0.0.0
- X: file
---
> Configures llama.cpp JNI provider settings for AI model execution, supporting extensive customization of model parameters and execution environment.

#### Purpose
Immutable configuration for llama.cpp JNI provider, supporting detailed AI model execution setup.

#### Type
Final class with Lombok annotations for equals, hashCode, and toString generation.

#### Input
Builder pattern with thirty-seven configurable parameters, including model path, context size, output tokens, temperature, threading, and various AI-specific settings.

#### Output
Immutable LlamaCppJniConfig object with getter methods for all parameters.

#### Core logic
1. Implements builder pattern for flexible configuration
2. Uses Lombok annotations for boilerplate code reduction
3. Provides comprehensive AI model execution parameters

#### Public API
- `builder(String modelPath)`: Starts configuration
- `modelPath()`: Returns model file path
- `contextSize()`: Returns context window size
- `maxOutputTokens()`: Returns max output tokens
- `temperature()`: Returns sampling temperature
- `threads()`: Returns CPU thread count
- `topP()`: Returns nucleus sampling probability
- `topK()`: Returns top-k sampling limit
- `minP()`: Returns min-p sampling threshold
- `topNSigma()`: Returns top-n-sigma sampling threshold
- `repeatPenalty()`: Returns repetition penalty
- `chatTemplateEnableThinking()`: Returns chat template thinking mode
- `cachePrompt()`: Returns prompt caching flag
- `swaFull()`: Returns full SWA KV cache flag
- `cacheReuse()`: Returns KV cache reuse chunk size
- `gpuLayers()`: Returns GPU layers count
- `seed()`: Returns RNG seed
- `cpuMoeLayers()`: Returns CPU MoE layers count
- `cpuFfnLayers()`: Returns CPU FFN layers count
- `kvUnifiedPerSlot()`: Returns per-slot unified KV context cap
- `lazyMode()`: Returns tensor read laziness mode
- `repeatLastN()`: Returns repeat penalty window
- `cacheTypeK()`: Returns KV cache type for K
- `cacheTypeV()`: Returns KV cache type for V
- `flashAttn()`: Returns Flash Attention flag
- `batchSize()`: Returns logical batch size
- `ubatchSize()`: Returns physical batch size
- `threadsBatch()`: Returns batch processing thread count
- `mainGpu()`: Returns primary GPU index
- `devices()`: Returns device selection
- `provider()`: Returns AI provider
- `remoteModel()`: Returns remote model ID
- `baseUrl()`: Returns API base URL
- `apiKeyEnv()`: Returns API key environment variable
- `reasoningEffort()`: Returns reasoning effort level
- `reasoningBudgetTokens()`: Returns reasoning budget tokens
- `dryMultiplier()`: Returns DRY sampling multiplier
- `dryBase()`: Returns DRY base
- `dryAllowedLength()`: Returns DRY allowed length
- `dryPenaltyLastN()`: Returns DRY penalty look-back window
- `drySequenceBreakers()`: Returns DRY sequence breakers
- `stopStrings()`: Returns stop strings

#### Dependencies
- `java.util.Collections`
- `java.util.List`
- `java.util.Objects`
- Lombok annotations
- `net.ladenthin.srcmorph.config.AiGenerationConfig`
- `net.ladenthin.srcmorph.support.ConvertToRecord`
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- `NullPointerException` if modelPath is null in builder

#### Concurrency
Immutable class, thread-safe for concurrent read operations.
