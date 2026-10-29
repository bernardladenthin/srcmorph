### SrcMorphConfiguration.java
- H: 1.0
- C: 1D938DBE
- D: 2026-10-28T21:24:22Z
- T: 2026-10-28T22:12:17Z
- G: 
- A: 0.0.0
- X: file
---
> Configures AI code generation parameters for srcmorph, supporting Maven, JSON, and direct Java binding.

#### Purpose
- Holds all configurable parameters for the srcmorph AI code generation process.
- Serves as a unified configuration object for multiple binding mechanisms (Maven, JSON/YAML, Java).

#### Type
Class, mutable JavaBean. Implements no interfaces. Uses Lombok @ToString annotation.

#### Input
- Constructor: no parameters.
- Setters: all fields accept their respective types (File, boolean, List<String>, long, int, float, String, @Nullable List<>).
- Dependencies: File, List, AiPromptDefinition, AiModelDefinition, AiFieldGenerationConfig, AiFactDefinition.

#### Output
- Getters: return corresponding field values (File, boolean, List<String>, long, int, float, String).
- Side effects: none; fields are mutated directly.

#### Core logic
- Provides centralized configuration for AI code generation.
- Supports multiple input methods (Maven, JSON/YAML, Java) via uniform JavaBean interface.
- Defines default values for all configurable parameters.
- Excludes Maven lifecycle flags (skip, skipFile, etc.) to keep configuration focused on generation logic.

#### Public API
- getBaseDirectory() -> File
- setBaseDirectory(File)
- getOutputDirectory() -> File
- setOutputDirectory(File)
- isForce() -> boolean
- setForce(boolean)
- getSubtrees() -> List<String>
- setSubtrees(List<String>)
- getExcludes() -> List<String>
- setExcludes(List<String>)
- getFileExtensions() -> List<String>
- setFileExtensions(List<String>)
- getMinFileSizeBytes() -> long
- setMinFileSizeBytes(long)
- getMaxFileSizeBytes() -> long
- setMaxFileSizeBytes(long)
- isPlanOnly() -> boolean
- setPlanOnly(boolean)
- getGenerationProvider() -> String
- setGenerationProvider(String)
- getPromptDefinitions() -> List<AiPromptDefinition>
- setPromptDefinitions(List<AiPromptDefinition>)
- getAiDefinitions() -> List<AiModelDefinition>
- setAiDefinitions(List<AiModelDefinition>)
- getModelDirectory() -> File
- setModelDirectory(File)
- getFieldGenerations() -> List<AiFieldGenerationConfig>
- setFieldGenerations(List<AiFieldGenerationConfig>)
- getFactDefinitions() -> List<AiFactDefinition>
- setFactDefinitions(List<AiFactDefinition>)
- getLlamaModelPath() -> String
- setLlamaModelPath(String)
- getLlamaContextSize() -> int
- setLlamaContextSize(int)
- getLlamaMaxOutputTokens() -> int
- setLlamaMaxOutputTokens(int)
- getLlamaTemperature() -> float
- setLlamaTemperature(float)
- getLlamaThreads() -> int
- setLlamaThreads(int)
- getPluginVersion() -> String
- setPluginVersion(String)
- getAiVersion() -> String
- setAiVersion(String)
- getProjectName() -> String
- setProjectName(String)

#### Dependencies
- java.io.File
- java.util.List
- lombok.ToString
- net.ladenthin.srcmorph.prompt.AiPromptDefinition
- org.jspecify.annotations.Nullable

#### Exceptions / Errors
- No explicit exceptions thrown.
- Null handling: @Nullable annotations indicate nullable fields.
- Default values provided for all fields to prevent nulls.

#### Concurrency
- No concurrency considerations mentioned.
- Class is designed for mutable state, implying single-threaded use during configuration population.
