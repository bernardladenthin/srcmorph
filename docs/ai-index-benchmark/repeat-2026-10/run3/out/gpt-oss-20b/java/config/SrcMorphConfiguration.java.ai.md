### SrcMorphConfiguration.java
- H: 1.0
- C: 1D938DBE
- D: 2026-10-29T14:19:17Z
- T: 2026-10-29T14:52:46Z
- G: 
- A: 0.0.0
- X: file
---
> Holds all runtime configuration for a srcmorph run, including directories, AI provider settings, and generation rules.

#### Purpose
- Central mutable config for srcmorph executions.
- Exposes Maven‑style parameters for plugin, CLI, or code construction.

#### Type
- Class, public, mutable JavaBean, annotated `@ToString`.

#### Input
- Constructor: no arguments, defaults applied.
- Setters: `File`, `boolean`, `List<String>`, `List<AiPromptDefinition>`, `List<AiModelDefinition>`, `List<AiFieldGenerationConfig>`, `List<AiFactDefinition>`, `String`, `int`, `float`, `File`, `@Nullable` values.

#### Output
- Getters return configured values; fields are mutable.

#### Core logic
- Provides getters/setters for all configuration properties.
- Holds default constants for output directory, provider, llama settings, and AI version.
- No additional computation or validation.

#### Public API
- `getBaseDirectory() -> File` – retrieve base dir  
- `setBaseDirectory(File)` – set base dir  
- `getOutputDirectory() -> File` – retrieve output dir  
- `setOutputDirectory(File)` – set output dir  
- `isForce() -> boolean` – check force flag  
- `setForce(boolean)` – set force flag  
- `getSubtrees() -> List<String>` – get source subtrees  
- `setSubtrees(List<String>)` – set source subtrees  
- `getExcludes() -> List<String>` – get exclude globs  
- `setExcludes(List<String>)` – set exclude globs  
- `getFileExtensions() -> List<String>` – get file extensions  
- `setFileExtensions(List<String>)` – set file extensions  
- `getMinFileSizeBytes() -> long` – get min file size  
- `setMinFileSizeBytes(long)` – set min file size  
- `getMaxFileSizeBytes() -> long` – get max file size  
- `setMaxFileSizeBytes(long)` – set max file size  
- `isPlanOnly() -> boolean` – check plan‑only flag  
- `setPlanOnly(boolean)` – set plan‑only flag  
- `getGenerationProvider() -> String` – get AI provider name  
- `setGenerationProvider(String)` – set AI provider name  
- `getPromptDefinitions() -> List<AiPromptDefinition>` – get prompt defs  
- `setPromptDefinitions(List<AiPromptDefinition>)` – set prompt defs  
- `getAiDefinitions() -> List<AiModelDefinition>` – get AI model defs  
- `setAiDefinitions(List<AiModelDefinition>)` – set AI model defs  
- `getModelDirectory() -> File` – get model dir  
- `setModelDirectory(File)` – set model dir  
- `getFieldGenerations() -> List<AiFieldGenerationConfig>` – get field generation rules  
- `setFieldGenerations(List<AiFieldGenerationConfig>)` – set field generation rules  
- `getFactDefinitions() -> List<AiFactDefinition>` – get fact groups  
- `setFactDefinitions(List<AiFactDefinition>)` – set fact groups  
- `getLlamaModelPath() -> String` – get fallback llama model path  
- `setLlamaModelPath(String)` – set fallback llama model path  
- `getLlamaContextSize() -> int` – get llama context size  
- `setLlamaContextSize(int)` – set llama context size  
- `getLlamaMaxOutputTokens() -> int` – get llama max output tokens  
- `setLlamaMaxOutputTokens(int)` – set llama max output tokens  
- `getLlamaTemperature() -> float` – get llama temperature  
- `setLlamaTemperature(float)` – set llama temperature  
- `getLlamaThreads() -> int` – get llama thread count  
- `setLlamaThreads(int)` – set llama thread count  
- `getPluginVersion() -> String` – get plugin version  
- `setPluginVersion(String)` – set plugin version  
- `getAiVersion() -> String` – get AI logic version  
- `setAiVersion(String)` – set AI logic version  
- `getProjectName() -> String` – get project title  
- `setProjectName(String)` – set project title  

#### Dependencies
- `java.io.File`, `java.util.List`, `lombok.ToString`, `net.ladenthin.srcmorph.prompt.AiPromptDefinition`, `org.jspecify.annotations.Nullable`.

#### Exceptions / Errors
- No explicit exceptions thrown; setters accept null where annotated.

#### Concurrency
- No synchronization; instance is not thread‑safe.
