### SrcMorphConfiguration.java
- H: 1.0
- C: 1D938DBE
- D: 2026-10-28T21:24:22Z
- T: 2026-10-28T22:49:07Z
- G: 
- A: 0.0.0
- X: file
---
> Holds all mutable configuration parameters for a srcmorph run, bindable from Maven, JSON/YAML, or Java code.

#### Purpose
- Central configuration holder for srcmorph engine.
- Mutable JavaBean for binding frameworks.

#### Type
- Class `SrcMorphConfiguration` (public, mutable).
- Annotations: `@SuppressWarnings({"NullAway.Init","initialization.fields.uninitialized"})`, `@ToString`.
- Extends `Object`; no interfaces.

#### Input
- Constructor: no-arg, defaults only.
- Fields set via public setters or direct field access by binding frameworks.
- Dependencies: `File`, `List`, `AiPromptDefinition`, `AiModelDefinition`, `AiFieldGenerationConfig`, `AiFactDefinition`, `AiGenerationConfig`.

#### Output
- Getter methods expose current configuration state.
- No side‑effects beyond field mutation.

#### Core logic
- Provides default constants for output directory, provider, llama settings, AI version.
- Stores configuration values as private fields.
- Getters return current values; setters assign values.
- No additional computation or validation.

#### Public API
- `getBaseDirectory() -> File` – retrieve base dir.
- `setBaseDirectory(File)` – set base dir.
- `getOutputDirectory() -> File` – retrieve output dir.
- `setOutputDirectory(File)` – set output dir.
- `isForce() -> boolean` – check force flag.
- `setForce(boolean)` – set force flag.
- `getSubtrees() -> List<String>` – get subtrees.
- `setSubtrees(List<String>)` – set subtrees.
- `getExcludes() -> List<String>` – get excludes.
- `setExcludes(List<String>)` – set excludes.
- `getFileExtensions() -> List<String>` – get extensions.
- `setFileExtensions(List<String>)` – set extensions.
- `getMinFileSizeBytes() -> long` – get min size.
- `setMinFileSizeBytes(long)` – set min size.
- `getMaxFileSizeBytes() -> long` – get max size.
- `setMaxFileSizeBytes(long)` – set max size.
- `isPlanOnly() -> boolean` – check plan‑only.
- `setPlanOnly(boolean)` – set plan‑only.
- `getGenerationProvider() -> String` – get provider.
- `setGenerationProvider(String)` – set provider.
- `getPromptDefinitions() -> List<AiPromptDefinition>` – get prompts.
- `setPromptDefinitions(List<AiPromptDefinition>)` – set prompts.
- `getAiDefinitions() -> List<AiModelDefinition>` – get model defs.
- `setAiDefinitions(List<AiModelDefinition>)` – set model defs.
- `getModelDirectory() -> File` – get model dir.
- `setModelDirectory(File)` – set model dir.
- `getFieldGenerations() -> List<AiFieldGenerationConfig>` – get field rules.
- `setFieldGenerations(List<AiFieldGenerationConfig>)` – set field rules.
- `getFactDefinitions() -> List<AiFactDefinition>` – get fact groups.
- `setFactDefinitions(List<AiFactDefinition>)` – set fact groups.
- `getLlamaModelPath() -> String` – get llama model path.
- `setLlamaModelPath(String)` – set llama model path.
- `getLlamaContextSize() -> int` – get llama context size.
- `setLlamaContextSize(int)` – set llama context size.
- `getLlamaMaxOutputTokens() -> int` – get llama max tokens.
- `setLlamaMaxOutputTokens(int)` – set llama max tokens.
- `getLlamaTemperature() -> float` – get llama temperature.
- `setLlamaTemperature(float)` – set llama temperature.
- `getLlamaThreads() -> int` – get llama thread count.
- `setLlamaThreads(int)` – set llama thread count.
- `getPluginVersion() -> String` – get plugin version.
- `setPluginVersion(String)` – set plugin version.
- `getAiVersion() -> String` – get AI logic version.
- `setAiVersion(String)` – set AI logic version.
- `getProjectName() -> String` – get project title.
- `setProjectName(String)` – set project title.

#### Dependencies
- `java.io.File`
- `java.util.List`
- `lombok.ToString`
- `net.ladenthin.srcmorph.prompt.AiPromptDefinition`
- `org.jspecify.annotations.Nullable`
- `AiModelDefinition`
- `AiFieldGenerationConfig`
- `AiFactDefinition`
- `AiGenerationConfig`

#### Exceptions / Errors
- No explicit exceptions thrown or caught.

#### Concurrency
- No synchronization; mutable state is not thread‑safe.
