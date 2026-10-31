### SrcMorphConfiguration.java
- H: 1.0
- C: 1D938DBE
- D: 2026-10-31T10:06:17Z
- T: 2026-10-31T13:35:45Z
- G: 
- A: 0.0.0
- X: file
---
> Holds all configuration parameters required for a srcmorph execution, serving as a central, mutable JavaBean for various binding technologies.

#### Purpose
* Acts as the single shared configuration object for the srcmorph engine.
* Provides a unified structure for parameters regardless of whether they originate from Maven, JSON/YAML, or direct Java instantiation.

#### Type
class; public; @ToString;

#### Input
* Constructor: initializes a new instance with default values.
* Setters: used by binding technologies (Maven, Jackson, etc.) to populate fields.

#### Output
* Getters: provide access to all configuration parameters.
* Mutated state: updates internal fields via setters.

#### Core logic
* Maintains default values for output directories, AI providers, llama.cpp settings, and versioning.
* Provides a structure that mirrors Maven plugin parameters to ensure compatibility between XML and future JSON/YAML configurations.
* Separates engine configuration from Maven lifecycle concerns (like skip flags).

#### Public API
* `getBaseDirectory() -> File` + returns project base directory.
* `setBaseDirectory(File) -> void` + sets project base directory.
* `getOutputDirectory() -> File` + returns directory for `.ai.md` files.
* `setOutputDirectory(File) -> void` + sets directory for `.ai.md` files.
* `isForce() -> boolean` + returns if regeneration is forced.
* `setForce(boolean) -> void` + sets if regeneration is forced.
* `getSubtrees() -> List<String>` + returns restricted source subdirectories.
* `setSubtrees(List<String>) -> void` + sets restricted source subdirectories.
* `getExcludes() -> List<String>` + returns glob patterns for skipping files.
* `setExcludes(List<String>) -> void` + sets glob patterns for skipping files.
* `getFileExtensions() -> List<String>` + returns extensions to index.
* `setFileExtensions(List<String>) -> void` + sets extensions to index.
* `getMinFileSizeBytes() -> long` + returns lower file-size bound.
* `setMinFileSizeBytes(long) -> void` + sets lower file-size bound.
* `getMaxFileSizeBytes() -> long` + returns upper file-size bound.
* `setMaxFileSizeBytes(long) -> void` + sets upper file-size bound.
* `isPlanOnly() -> boolean` + returns if run stops after planning.
* `setPlanOnly(boolean) -> void` + sets if run stops after planning.
* `getGenerationProvider() -> String` + returns AI provider name.
* `setGenerationProvider(String) -> void` + sets AI provider name.
* `getPromptDefinitions() -> List<AiPromptDefinition>` + returns prompt templates.
* `setPromptDefinitions(List<AiPromptDefinition>) -> void` + sets prompt templates.
* `getAiDefinitions() -> List<AiModelDefinition>` + returns AI model definitions.
* `setAiDefinitions(List<AiModelDefinition>) -> void` + sets AI model definitions.
* `getModelDirectory() -> File` + returns directory for relative model paths.
* `setModelDirectory(File) -> void` + sets directory for relative model paths.
* `getFieldGenerations() -> List<AiFieldGenerationConfig>` + returns per-field routing rules.
* `setFieldGenerations(List<AiFieldGenerationConfig>) -> void` + sets per-field routing rules.
* `getFactDefinitions() -> List<AiFactDefinition>` + returns reusable fact groups.
* `setFactDefinitions(List<AiFactDefinition>) -> void` + sets reusable fact groups.
* `getLlamaModelPath() -> String` + returns GGUF model path fallback.
* `setLlamaModelPath(String) -> void` + sets GGUF model path fallback.
* `getLlamaContextSize() -> int` + returns llama.cpp context window fallback.
* `setLlamaContextSize(int) -> void` + sets llama.cpp context window fallback.
* `getLlamaMaxOutputTokens() -> int` + returns llama.cpp max output tokens fallback.
* `setLlamaMaxOutputTokens(int) -> void` + sets llama.cpp max output tokens fallback.
* `getLlamaTemperature() -> float` + returns llama.cpp sampling temperature fallback.
* `setLlamaTemperature(float) -> void` + sets llama.cpp sampling temperature fallback.
* `getLlamaThreads() -> int` + returns llama.cpp CPU thread count fallback.
* `setLlamaThreads(int) -> void` + sets llama.cpp CPU thread count fallback.
* `getPluginVersion() -> String` + returns recorded plugin version.
* `setPluginVersion(String) -> void` + sets recorded plugin version.
* `getAiVersion() -> String` + returns recorded AI version.
* `setAiVersion(String) -> void` + sets recorded AI version.
* `getProjectName() -> String` + returns project title.
* `setProjectName(String) -> void` + sets project title.

#### Dependencies
* `java.io.File`
* `java.util.List`
* `lombok.ToString`
* `net.ladenthin.srcmorph.prompt.AiPromptDefinition`
* `net.ladenthin.srcmorph.config.AiModelDefinition`
* `net.ladenthin.srcmorph.config.AiFieldGenerationConfig`
* `net.ladenthin.srcmorph.config.AiFactDefinition`
* `net.ladenthin.srcmorph.config.AiGenerationConfig`
* `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
* Suppresses `NullAway.Init` and `initialization.fields.uninitialized` warnings.
