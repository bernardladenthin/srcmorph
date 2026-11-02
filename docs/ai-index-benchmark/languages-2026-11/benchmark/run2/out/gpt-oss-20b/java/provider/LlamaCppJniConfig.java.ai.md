### LlamaCppJniConfig.java
- H: 1.0
- C: 13EDB813
- D: 2026-11-01T01:35:52Z
- T: 2026-11-01T01:41:03Z
- G: 
- A: 0.0.0
- X: file
---
> Immutable configuration holder for llama.cpp JNI provider, exposing all model‑and‑runtime options.

#### Purpose
- Stores all llama.cpp JNI configuration values.
- Provides read‑only access to configuration for the provider.

#### Type
`final class LlamaCppJniConfig` (class, final)  
extends `Object`; implements no interfaces.  
Annotations: `@ConvertToRecord`, `@ToString`, `@EqualsAndHashCode`.

#### Input
- `Builder` instance supplied to private constructor.
- Builder fields initialized from `AiGenerationConfig.DEFAULT_*` or caller values.

#### Output
- Immutable configuration values.
- Unmodifiable lists for `drySequenceBreakers` and `stopStrings`.

#### Core logic
- Constructor copies all 37 builder fields into final fields.
- Getter methods return stored values; list getters wrap with `Collections.unmodifiableList`.
- `Builder` initializes defaults from `AiGenerationConfig`.
- Each setter assigns a field and returns `this`.
- `build()` creates a new `LlamaCppJniConfig` from the builder.

#### Public API
- `static Builder builder(String modelPath)` → starts builder with required model path.  
- Getters (38 total):  
  `modelPath()`, `contextSize()`, `maxOutputTokens()`, `temperature()`, `threads()`, `topP()`, `topK()`, `minP()`, `topNSigma()`, `repeatPenalty()`, `chatTemplateEnableThinking()`, `cachePrompt()`, `swaFull()`, `cacheReuse()`, `gpuLayers()`, `seed()`, `cpuMoeLayers()`, `cpuFfnLayers()`, `kvUnifiedPerSlot()`, `lazyMode()`, `repeatLastN()`, `cacheTypeK()`, `cacheTypeV()`, `flashAttn()`, `batchSize()`, `ubatchSize()`, `threadsBatch()`, `mainGpu()`, `devices()`, `provider()`, `remoteModel()`, `baseUrl()`, `apiKeyEnv()`, `reasoningEffort()`, `reasoningBudgetTokens()`, `dryMultiplier()`, `dryBase()`, `dryAllowedLength()`, `dryPenaltyLastN()`, `drySequenceBreakers()`, `stopStrings()`.

- Builder methods (41 total):  
  `contextSize(int)`, `maxOutputTokens(int)`, `temperature(float)`, `threads(int)`, `topP(float)`, `topK(int)`, `minP(float)`, `topNSigma(float)`, `repeatPenalty(float)`, `chatTemplateEnableThinking(Boolean)`, `cachePrompt(boolean)`, `swaFull(boolean)`, `cacheReuse(int)`, `gpuLayers(int)`, `seed(int)`, `cpuMoeLayers(int)`, `cpuFfnLayers(int)`, `kvUnifiedPerSlot(int)`, `lazyMode(String)`, `repeatLastN(int)`, `cacheTypeK(String)`, `cacheTypeV(String)`, `flashAttn(boolean)`, `batchSize(int)`, `ubatchSize(int)`, `threadsBatch(int)`, `mainGpu(int)`, `devices(String)`, `provider(String)`, `remoteModel(String)`, `baseUrl(String)`, `apiKeyEnv(String)`, `reasoningEffort(String)`, `reasoningBudgetTokens(int)`, `dryMultiplier(float)`, `dryBase(float)`, `dryAllowedLength(int)`, `dryPenaltyLastN(int)`, `drySequenceBreakers(List<String>)`, `stopStrings(List<String>)`, `build()`.

#### Dependencies
- `java.util.*` (Collections, List, Objects)
- `lombok.*` (EqualsAndHashCode, ToString)
- `net.ladenthin.srcmorph.config.AiGenerationConfig`
- `net.ladenthin.srcmorph.support.ConvertToRecord`
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- `builder(String)` throws `NullPointerException` if `modelPath` is null.
- `Builder` setters accept any value; no validation beyond null checks for optional strings.

#### Concurrency
- Immutable after construction; thread‑safe read access.
- Builder is not thread‑safe; intended single‑thread usage.
