### LlamaCppJniConfig.java
- H: 1.0
- C: 13EDB813
- D: 2026-10-28T21:24:22Z
- T: 2026-10-28T22:15:18Z
- G: 
- A: 0.0.0
- X: file
---
> Configures llama.cpp JNI integration with 37 parameters for model behavior, caching, and inference settings.

#### Purpose
- Encapsulates configuration for llama.cpp JNI provider
- Provides immutable value object with 37 fields for AI model parameters

#### Type
Final class implementing value semantics with Lombok annotations; uses builder pattern with private constructor

#### Input
- Constructor parameter: `Builder` instance with 37 fields
- Builder constructor parameter: `String modelPath` (required)
- Builder setter parameters: 37 configurable fields with default values from `AiGenerationConfig`

#### Output
- Returns immutable `LlamaCppJniConfig` instance from `build()`
- Accessor methods return field values with appropriate types
- `stopStrings()` and `drySequenceBreakers()` return unmodifiable lists

#### Core logic
- Uses private constructor with builder to enforce named parameter pattern
- All 37 fields are stored as final instance variables
- Builder applies defaults from `AiGenerationConfig` for all optional parameters
- Lombok generates equals, hashCode, and toString methods
- Fields are validated through builder setters (no validation in constructor)

#### Public API
- `builder(String)` → `Builder` (creates new builder with required modelPath)
- `modelPath()` → `String` (returns GGUF model file path)
- `contextSize()` → `int` (returns context window size in tokens)
- `maxOutputTokens()` → `int` (returns maximum output tokens)
- `temperature()` → `float` (returns sampling temperature)
- `threads()` → `int` (returns number of CPU threads)
- `topP()` → `float` (returns nucleus-sampling probability threshold)
- `topK()` → `int` (returns top-k sampling limit)
- `minP()` → `float` (returns min-p sampling threshold)
- `topNSigma()` → `float` (returns top-n-sigma sampling threshold)
- `repeatPenalty()` → `float` (returns repetition penalty)
- `chatTemplateEnableThinking()` → `Boolean` (returns chat-template thinking mode)
- `cachePrompt()` → `boolean` (returns prompt caching flag)
- `swaFull()` → `boolean` (returns full SWA KV cache flag)
- `cacheReuse()` → `int` (returns KV prefix-reuse chunk size)
- `gpuLayers()` → `int` (returns GPU layers to offload)
- `seed()` → `int` (returns RNG seed)
- `cpuMoeLayers()` → `int` (returns CPU MoE layers)
- `cpuFfnLayers()` → `int` (returns CPU FFN layers)
- `kvUnifiedPerSlot()` → `int` (returns per-slot unified KV cap)
- `lazyMode()` → `String` (returns tensor-read laziness)
- `repeatLastN()` → `int` (returns repeat-penalty window)
- `cacheTypeK()` → `String` (returns KV-cache data type for K)
- `cacheTypeV()` → `String` (returns KV-cache data type for V)
- `flashAttn()` → `boolean` (returns Flash Attention flag)
- `batchSize()` → `int` (returns logical batch size)
- `ubatchSize()` → `int` (returns physical batch size)
- `threadsBatch()` → `int` (returns batch thread count)
- `mainGpu()` → `int` (returns primary GPU index)
- `devices()` → `String` (returns device selection)
- `provider()` → `String` (returns AI provider)
- `remoteModel()` → `String` (returns remote model ID)
- `baseUrl()` → `String` (returns base URL)
- `apiKeyEnv()` → `String` (returns API key env var name)
- `reasoningEffort()` → `String` (returns reasoning effort)
- `reasoningBudgetTokens()` → `int` (returns reasoning budget)
- `dryMultiplier()` → `float` (returns DRY sampling multiplier)
- `dryBase()` → `float` (returns DRY base)
- `dryAllowedLength()` → `int` (returns DRY allowed length)
- `dryPenaltyLastN()` → `int` (returns DRY penalty window)
- `drySequenceBreakers()` → `List<String>` (returns DRY sequence breakers)
- `stopStrings()` → `List<String>` (returns stop strings)
- `build()` → `LlamaCppJniConfig` (constructs final configuration)

#### Dependencies
- `java.util.Collections`
- `java.util.List`
- `java.util.Objects`
- `lombok.EqualsAndHashCode`
- `lombok.ToString`
- `net.ladenthin.srcmorph.config.AiGenerationConfig`
- `net.ladenthin.srcmorph.support.ConvertToRecord`
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- Throws `NullPointerException` when `modelPath` is null in builder constructor
- No explicit validation in constructor (assumes builder handles validation)
- Uses `Objects.requireNonNull` for required parameter validation

#### Concurrency
- Immutable configuration object
- Builder is not thread-safe (intended for single-threaded use)
- All fields are final and properly encapsulated
- Accessor methods are thread-safe due to immutability
