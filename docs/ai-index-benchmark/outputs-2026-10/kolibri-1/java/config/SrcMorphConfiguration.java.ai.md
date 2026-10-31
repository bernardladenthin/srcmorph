### SrcMorphConfiguration.java
- H: 1.0
- C: 1D938DBE
- D: 2026-10-31T08:11:47Z
- T: 2026-10-31T08:21:04Z
- G: 
- A: 0.0.0
- X: file
---
> Holds every parameter a srcmorph run needs, bindable from Maven mojos, JSON/YAML config, or plain Java code.

#### Purpose
- Root mutable JavaBean for srcmorph run parameters
- Single shared config object across binding surfaces

#### Type
- class SrcMorphConfiguration
- modifiers: public, final (no abstract/sealed)
- implements: none
- annotations: @ToString (Lombok), @Nullable (jspecify), @SuppressWarnings
- key generics: none

#### Input
- constructor: no-op
- fields consumed: baseDirectory, outputDirectory, force, subtrees, excludes, fileExtensions, minFileSizeBytes, maxFileSizeBytes, planOnly, generationProvider, promptDefinitions, aiDefinitions, modelDirectory, fieldGenerations, factDefinitions, llamaModelPath, llamaContextSize, llamaMaxOutputTokens, llamaTemperature, llamaThreads, pluginVersion, aiVersion, projectName
- read resources: none

#### Output
- return types: File, boolean, @Nullable List<String>, long, String, int, float, @Nullable List<AiPromptDefinition>, @Nullable List<AiModelDefinition>, @Nullable File, @Nullable List<AiFieldGenerationConfig>, @Nullable List<AiFactDefinition>, @Nullable String
- produced state: none
- written resources: none
- side effects: none

#### Core logic
- Holds all run parameters as mutable JavaBean fields
- Defaults applied via static constants and field initializers
- Excluded per-goal skip flags remain mojo-side
- Must remain mutable for reflection-based binding

#### Public API
- getBaseDirectory() -> File: returns project base directory
- setBaseDirectory(File) -> void: sets project base directory
- getOutputDirectory() -> File: returns output directory for .ai.md files
- setOutputDirectory(File) -> void: sets output directory for .ai.md files
- isForce() -> boolean: returns whether to always regenerate AI fields
- setForce(boolean) -> void: sets force regeneration flag
- getSubtrees() -> @Nullable List<String>: returns source subdirectory paths
- setSubtrees(@Nullable List<String>) -> void: sets source subdirectory paths
- getExcludes() -> @Nullable List<String>: returns source file skip globs
- setExcludes(@Nullable List<String>) -> void: sets source file skip globs
- getFileExtensions() -> @Nullable List<String>: returns file extensions to index
- setFileExtensions(@Nullable List<String>) -> void: sets file extensions to index
- getMinFileSizeBytes() -> long: returns exclusive lower file-size bound
- setMinFileSizeBytes(long) -> void: sets exclusive lower file-size bound
- getMaxFileSizeBytes() -> long: returns inclusive upper file-size bound
- setMaxFileSizeBytes(long) -> void: sets inclusive upper file-size bound
- isPlanOnly() -> boolean: returns whether to stop after planning
- setPlanOnly(boolean) -> void: sets plan-only flag
- getGenerationProvider() -> String: returns AI generation provider name
- setGenerationProvider(String) -> void: sets AI generation provider name
- getPromptDefinitions() -> @Nullable List<AiPromptDefinition>: returns prompt template definitions
- setPromptDefinitions(@Nullable List<AiPromptDefinition>) -> void: sets prompt template definitions
- getAiDefinitions() -> @Nullable List<AiModelDefinition>: returns AI model definitions
- setAiDefinitions(@Nullable List<AiModelDefinition>) -> void: sets AI model definitions
- getModelDirectory() -> @Nullable File: returns model directory for relative paths
- setModelDirectory(@Nullable File) -> void: sets model directory for relative paths
- getFieldGenerations() -> @Nullable List<AiFieldGenerationConfig>: returns per-field generation rules
- setFieldGenerations(@Nullable List<AiFieldGenerationConfig>) -> void: sets per-field generation rules
- getFactDefinitions() -> @Nullable List<AiFactDefinition>: returns reusable fact definition groups
- setFactDefinitions(@Nullable List<AiFactDefinition>) -> void: sets reusable fact definition groups
- getLlamaModelPath() -> @Nullable String: returns GGUF model file path
- setLlamaModelPath(@Nullable String) -> void: sets GGUF model file path
- getLlamaContextSize() -> int: returns llama.cpp context window size
- setLlamaContextSize(int) -> void: sets llama.cpp context window size
- getLlamaMaxOutputTokens() -> int: returns max llama.cpp output tokens
- setLlamaMaxOutputTokens(int) -> void: sets max llama.cpp output tokens
- getLlamaTemperature() -> float: returns llama.cpp sampling temperature
- setLlamaTemperature(float) -> void: sets llama.cpp sampling temperature
- getLlamaThreads() -> int: returns llama.cpp CPU thread count
- setLlamaThreads(int) -> void: sets llama.cpp CPU thread count
- getPluginVersion() -> String: returns generator version for headers
- setPluginVersion(String) -> void: sets generator version for headers
- getAiVersion() -> String: returns AI summarisation logic version
- setAiVersion(String) -> void: sets AI summarisation logic version
- getProjectName() -> @Nullable String: returns project title for index
- setProjectName(@Nullable String) -> void: sets project title for index

#### Dependencies
- java.io.File
- java.util.List
- lombok.ToString
- net.ladenthin.srcmorph.prompt.AiPromptDefinition
- org.jspecify.annotations.Nullable
- net.ladenthin.srcmorph.config.AiModelDefinition
- net.ladenthin.srcmorph.config.AiFieldGenerationConfig
- net.ladenthin.srcmorph.config.AiFactDefinition

#### Exceptions / Errors
- None explicitly declared or caught

#### Concurrency
- None noted
