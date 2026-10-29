### LlamaCppJniConfig.java
- H: 1.0
- C: 13EDB813
- D: 2026-10-29T08:10:04Z
- T: 2026-10-29T08:12:33Z
- G: 
- A: 0.0.0
- X: file
---
> Defines immutable configuration for llama.cpp JNI model parameters including context size, sampling settings, GPU offloading, and OpenAI-compatible API options.

#### Purpose
- Encapsulates configuration for llama.cpp JNI model execution with hardware and sampling parameters  
- Provides immutable, thread-safe configuration via a builder pattern with default values

#### Type
final class with @ConvertToRecord, @ToString, @EqualsAndHashCode  
implements no interfaces  
generics: none  
fields: 37 private final fields (including nullable Boolean)  
notable annotations: @Nullable, @ConvertToRecord, Lombok

#### Input
- Constructor: `builder(String modelPath)` with required model path  
- Builder: all fields set via optional setters starting from `AiGenerationConfig.DEFAULT_*`  
- Dependencies: `AiGenerationConfig` for default values, `Collections` for empty lists

#### Output
- Immutable instance with all 37 fields accessible via getter methods  
- Unmodifiable lists for `drySequenceBreakers` and `stopStrings`  
- Configurable values for sampling, GPU, batching, and API settings

#### Core logic
- All fields are initialized from a builder with defaults from `AiGenerationConfig`  
- Builder sets values with null handling and fallback to defaults  
- Final config is constructed via private constructor with no normalization  
- Accessors return immutable values; lists are defensively copied via `Collections.unmodifiableList`

#### Public API
- `builder(String)` → creates builder with model path required  
- `modelPath()`, `contextSize()`, `maxOutputTokens()`, `temperature()`, `threads()`, `topP()`, `topK()`, `minP()`, `topNSigma()`, `repeatPenalty()`, `chatTemplateEnableThinking()`, `cachePrompt()`, `swaFull()`, `cacheReuse()`, `gpuLayers()`, `seed()`, `cpuMoeLayers()`, `cpuFfnLayers()`, `kvUnifiedPerSlot()`, `lazyMode()`, `repeatLastN()`, `cacheTypeK()`, `cacheTypeV()`, `flashAttn()`, `batchSize()`, `ubatchSize()`, `threadsBatch()`, `mainGpu()`, `devices()`, `provider()`, `remoteModel()`, `baseUrl()`, `apiKeyEnv()`, `reasoningEffort()`, `reasoningBudgetTokens()`, `dryMultiplier()`, `dryBase()`, `dryAllowedLength()`, `dryPenaltyLastN()`, `drySequenceBreakers()`, `stopStrings()` → each returns corresponding field  
- Builder has 37 setters (all optional) and one `build()` method  
- 37 total fields, 37 total setters

#### Dependencies
- java.util.Collections  
- java.util.Objects  
- net.ladenthin.srcmorph.config.AiGenerationConfig  
- net.ladenthin.srcmorph.support.ConvertToRecord  
- org.jspecify.annotations.Nullable  
- lombok.EqualsAndHashCode  
- lombok.ToString  

#### Exceptions / Errors
- `NullPointerException` thrown in `builder(String)` if modelPath is null  
- No runtime exceptions; all values are validated at construction time  
- Null values handled gracefully with fallback to defaults

#### Concurrency
- Immutable instance; thread-safe by design  
- Builder is not thread-safe (single-threaded usage assumed)  
- No synchronization or shared state  
- All fields are final and accessed via immutable getters
