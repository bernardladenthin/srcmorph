### LlamaCppJniConfig.java
- H: 1.0
- C: 13EDB813
- D: 2026-10-29T14:19:17Z
- T: 2026-10-29T14:38:35Z
- G: 
- A: 0.0.0
- X: file
---
> Immutable configuration for the llama.cpp JNI provider, built through a fluent builder with thirty-seven named fields.

#### Purpose
Value configuration for llama.cpp JNI provider with thirty-seven fields, constructed via a fluent builder; defaults sourced from AiGenerationConfig.

#### Type
final class LlamaCppJniConfig; implements no interfaces; extends java.lang.Object; annotated with @ConvertToRecord, @ToString, @EqualsAndHashCode; thirty-seven private final fields; private positional constructor taking Builder; public static final class Builder for fluent configuration.

#### Input
Constructor parameter: Builder builder; Builder sets every field from AiGenerationConfig.DEFAULT_* values or explicit caller-provided values; Builder constructor requires non-null String modelPath.

#### Output
Immutable LlamaCppJniConfig instance with thirty-seven accessor methods returning modelPath, contextSize, maxOutputTokens, temperature, threads, topP, topK, minP, topNSigma, repeatPenalty, chatTemplateEnableThinking, cachePrompt, swaFull, cacheReuse, gpuLayers, seed, cpuMoeLayers, cpuFfnLayers, kvUnifiedPerSlot, lazyMode, repeatLastN, cacheTypeK, cacheTypeV, flashAttn, batchSize, ubatchSize, threadsBatch, mainGpu, devices, provider, remoteModel, baseUrl, apiKeyEnv, reasoningEffort, reasoningBudgetTokens, dryMultiplier, dryBase, dryAllowedLength, dryPenaltyLastN, drySequenceBreakers, stopStrings; build() returns LlamaCppJniConfig.

#### Core logic
- Builder pattern with thirty-seven optional setters, each starting at AiGenerationConfig.DEFAULT_* default
- Private constructor copies all fields from Builder; no normalization performed (builder already normalized)
- build() constructs immutable LlamaCppJniConfig; not thread-safe (builder state mutated during build)
- All thirty-seven fields are private final; accessors are record-style methods

#### Public API
- builder(String modelPath) -> Builder
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
- build() -> LlamaCppJniConfig

Total: 37 accessor methods + builder + config = 39 public members.

#### Dependencies
java.util.Collections; java.util.List; java.util.Objects; lombok.EqualsAndHashCode; lombok.ToString; net.ladenthin.srcmorph.config.AiGenerationConfig; net.ladenthin.srcmorph.support.ConvertToRecord; org.jspecify.annotations.Nullable

#### Exceptions / Errors
NullPointerException if modelPath is null (enforced by Builder constructor); builder not thread-safe; dryBase/dryAllowedLength/dryPenaltyLastN effective only when dryMultiplier > 0

#### Concurrency
Builder not thread-safe, not meant to be: filled and built on one thread; produced LlamaCppJniConfig is immutable
