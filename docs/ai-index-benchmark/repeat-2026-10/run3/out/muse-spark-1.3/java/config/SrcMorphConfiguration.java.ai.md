### SrcMorphConfiguration.java
- H: 1.0
- C: 1D938DBE
- D: 2026-10-31T15:20:26Z
- T: 2026-10-31T15:21:30Z
- G: 
- A: 0.0.0
- X: file
---
> Configures AI-powered source-code documentation generation runs with file scope, model, and prompt settings.

#### Purpose
- Root mutable JavaBean holding every `srcmorph` run parameter bindable from Maven, JSON/YAML, Java.
- Mirrors Maven `@Parameter` names for shared config across plugin, CLI, engine.

#### Type
- class `SrcMorphConfiguration`, mutable JavaBean.
- Annotations: `@ToString` (Lombok), `@SuppressWarnings({"NullAway.Init", "initialization.fields.uninitialized"})`.

#### Input
- Constructor `SrcMorphConfiguration()` applies defaults, no parameters.
- Setters consume `File` baseDirectory, outputDirectory, modelDirectory; `List<String>` subtrees, excludes, fileExtensions; `List<AiPromptDefinition>` promptDefinitions; `List<AiModelDefinition>` aiDefinitions; `List<AiFieldGenerationConfig>` fieldGenerations; `List<AiFactDefinition>` factDefinitions; `long` file-size bounds; `int/float/String/boolean` llama and version flags.
- Default constants: `DEFAULT_OUTPUT_DIRECTORY="src/site/ai"`, `DEFAULT_GENERATION_PROVIDER="mock"`, `DEFAULT_LLAMA_CONTEXT_SIZE=2048`, `DEFAULT_LLAMA_MAX_OUTPUT_TOKENS=128`, `DEFAULT_LLAMA_TEMPERATURE=0.15f`, `DEFAULT_LLAMA_THREADS`, `DEFAULT_AI_VERSION="0.0.0"`.

#### Output
- Getters return configured `File`, `List`, `long/int/float/String/boolean` values for engine consumption.
- Mutates 23 instance fields via setters; `outputDirectory`, `generationProvider`, llama settings, versions carry defaults.
- Side effect: drives `.ai.md` generation, routing plan, model resolution, header versions.

#### Core logic
- Centralizes file-scope config: `baseDirectory`, `outputDirectory`, `subtrees`, `excludes`, `fileExtensions`, `minFileSizeBytes`, `maxFileSizeBytes`, `force`, `planOnly`.
- Centralizes AI generation config: `generationProvider`, `promptDefinitions`, `aiDefinitions`, `fieldGenerations`, `factDefinitions`, `modelDirectory`.
- Provides llama.cpp JNI fallback config: `llamaModelPath`, `llamaContextSize`, `llamaMaxOutputTokens`, `llamaTemperature`, `llamaThreads` when `fieldGenerations` empty.
- Records provenance versions: `pluginVersion`, `aiVersion`, `projectName` for `.ai.md` headers and aggregate index.
- Excludes Maven skip flags as lifecycle-only, engine always executes.

#### Public API
- `SrcMorphConfiguration() -> SrcMorphConfiguration` — creates config with defaults.
- `getBaseDirectory() -> File` — returns project base directory.
- `setBaseDirectory(File) -> void` — sets project base directory.
- `getOutputDirectory() -> File` — returns `.ai.md` output directory.
- `setOutputDirectory(File) -> void` — sets `.ai.md` output directory.
- `isForce() -> boolean` — returns always-regenerate flag.
- `setForce(boolean) -> void` — sets always-regenerate flag.
- `getSubtrees() -> List<String>` — returns restricting source subdirectories.
- `setSubtrees(List<String>) -> void` — sets restricting source subdirectories.
- `getExcludes() -> List<String>` — returns skip glob patterns.
- `setExcludes(List<String>) -> void` — sets skip glob patterns.
- `getFileExtensions() -> List<String>` — returns extensions to index.
- `setFileExtensions(List<String>) -> void` — sets extensions to index.
- `getMinFileSizeBytes() -> long` — returns exclusive lower size bound.
- `setMinFileSizeBytes(long) -> void` — sets exclusive lower size bound.
- `getMaxFileSizeBytes() -> long` — returns inclusive upper size bound.
- `setMaxFileSizeBytes(long) -> void` — sets inclusive upper size bound.
- `isPlanOnly() -> boolean` — returns stop-after-plan flag.
- `setPlanOnly(boolean) -> void` — sets stop-after-plan flag.
- `getGenerationProvider() -> String` — returns AI provider name.
- `setGenerationProvider(String) -> void` — sets AI provider name.
- `getPromptDefinitions() -> List<AiPromptDefinition>` — returns prompt template definitions.
- `setPromptDefinitions(List<AiPromptDefinition>) -> void` — sets prompt template definitions.
- `getAiDefinitions() -> List<AiModelDefinition>` — returns AI model definitions.
- `setAiDefinitions(List<AiModelDefinition>) -> void` — sets AI model definitions.
- `getModelDirectory() -> File` — returns relative model-path base.
- `setModelDirectory(File) -> void` — sets relative model-path base.
- `getFieldGenerations() -> List<AiFieldGenerationConfig>` — returns per-field routing rules.
- `setFieldGenerations(List<AiFieldGenerationConfig>) -> void` — sets per-field routing rules.
- `getFactDefinitions() -> List<AiFactDefinition>` — returns named fact groups.
- `setFactDefinitions(List<AiFactDefinition>) -> void` — sets named fact groups.
- `getLlamaModelPath() -> String` — returns GGUF fallback model path.
- `setLlamaModelPath(String) -> void` — sets GGUF fallback model path.
- `getLlamaContextSize() -> int` — returns fallback context window size.
- `setLlamaContextSize(int) -> void` — sets fallback context window size.
- `getLlamaMaxOutputTokens() -> int` — returns fallback max output tokens.
- `setLlamaMaxOutputTokens(int) -> void` — sets fallback max output tokens.
- `getLlamaTemperature() -> float` — returns fallback sampling temperature.
- `setLlamaTemperature(float) -> void` — sets fallback sampling temperature.
- `getLlamaThreads() -> int` — returns fallback CPU thread count.
- `setLlamaThreads(int) -> void` — sets fallback CPU thread count.
- `getPluginVersion() -> String` — returns generator version for headers.
- `setPluginVersion(String) -> void` — sets generator version for headers.
- `getAiVersion() -> String` — returns summarisation logic version.
- `setAiVersion(String) -> void` — sets summarisation logic version.
- `getProjectName() -> String` — returns aggregate index title.
- `setProjectName(String) -> void` — sets aggregate index title.

#### Dependencies
- `java.io.File`, `java.util.List`, `lombok.ToString`, `net.ladenthin.srcmorph.prompt.AiPromptDefinition`, `org.jspecify.annotations.Nullable`, `AiGenerationConfig`, `AiModelDefinition`, `AiFieldGenerationConfig`, `AiFactDefinition`.

#### Exceptions / Errors
- Nullable `List/File/String` getters return `null` when unconfigured; callers must null-check.
- `<=0` file-size bounds disable limit/unlimited semantics.
