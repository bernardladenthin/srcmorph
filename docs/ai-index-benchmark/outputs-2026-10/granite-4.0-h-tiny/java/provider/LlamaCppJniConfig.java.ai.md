### LlamaCppJniConfig.java
- H: 1.0
- C: 13EDB813
- D: 2026-10-29T00:07:37Z
- T: 2026-10-29T00:08:30Z
- G: 
- A: 0.0.0
- X: file
---
```markdown
> Immutable configuration for the llama.cpp JNI provider

#### Purpose
Immutable configuration for the llama.cpp JNI provider

#### Type
final class LlamaCppJniConfig with 37 fields, record-shaped, builder pattern

#### Input
- modelPath (String)
- contextSize (int)
- maxOutputTokens (int)
- temperature (float)
- threads (int)
- topP (float)
- topK (int)
- minP (float)
- topNSigma (float)
- repeatPenalty (float)
- chatTemplateEnableThinking (Boolean)
- cachePrompt (boolean)
- swaFull (boolean)
- cacheReuse (int)
- gpuLayers (int)
- seed (int)
- cpuMoeLayers (int)
- cpuFfnLayers (int)
- kvUnifiedPerSlot (int)
- lazyMode (String)
- repeatLastN (int)
- cacheTypeK (String)
- cacheTypeV (String)
- flashAttn (boolean)
- batchSize (int)
- ubatchSize (int)
- threadsBatch (int)
- mainGpu (int)
- devices (String)
- provider (String)
- remoteModel (String)
- baseUrl (String)
- apiKeyEnv (String)
- reasoningEffort (String)
- reasoningBudgetTokens (int)
- dryMultiplier (float)
- dryBase (float)
- dryAllowedLength (int)
- dryPenaltyLastN (int)
- drySequenceBreakers (List<String>)
- stopStrings (List<String>)

#### Output
- Returns values from constructor parameters
- Returns boolean flags and numeric values
- Writes no state

#### Core logic
- Private constructor from Builder
- Public static Builder factory method
- Builder with default values for all fields
- Fluent setters for each field

#### Public API
- modelPath() -> String
- contextSize() -> int
- maxOutputTokens() -> int
- temperature() -> float
- threads() -> int
- topP() -> float
- topK() -> int
- minP() -> float
- topNSigma() -> float
- repeatPenalty() -> float
- chatTemplateEnableThinking() -> @Nullable Boolean
- cachePrompt() -> boolean
- swaFull() -> boolean
- cacheReuse() -> int
- gpuLayers() -> int
- seed() -> int
- cpuMoeLayers() -> int
- cpuFfnLayers() -> int
- kvUnifiedPerSlot() -> int
- lazyMode() -> String
- repeatLastN() -> int
- cacheTypeK() -> String
- cacheTypeV() -> String
- flashAttn() -> boolean
- batchSize() -> int
- ubatchSize() -> int
- threadsBatch() -> int
- mainGpu() -> int
- devices() -> String
- provider() -> String
- remoteModel() -> String
- baseUrl() -> String
- apiKeyEnv() -> String
- reasoningEffort() -> String
- reasoningBudgetTokens() -> int
- dryMultiplier() -> float
- dryBase() -> float
- dryAllowedLength() -> int
- dryPenaltyLastN() -> int
- drySequenceBreakers() -> List<String>
- stopStrings() -> List<String>

#### Dependencies
- java.util.Collections
- java.util.List
- java.util.Objects
- lombok.EqualsAndHashCode
- lombok.ToString
- net.ladenthin.srcmorph.config.AiGenerationConfig
- net.ladenthin.srcmorph.support.ConvertToRecord
- org.jspecify.annotations.Nullable

#### Exceptions / Errors
- NullPointerException if modelPath is null in Builder constructor

#### Concurrency
- Immutable after build
- Builder not thread-safe
```
