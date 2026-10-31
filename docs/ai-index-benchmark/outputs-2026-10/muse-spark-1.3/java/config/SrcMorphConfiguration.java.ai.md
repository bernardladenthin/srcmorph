### SrcMorphConfiguration.java
- H: 1.0
- C: 1D938DBE
- D: 2026-10-31T10:13:39Z
- T: 2026-10-31T10:14:37Z
- G: 
- A: 0.0.0
- X: file
---
> Configures AI-powered source-code documentation generation runs with file scope, model, prompt, and output settings.

#### Purpose
- Root mutable JavaBean holding every `srcmorph` run parameter bindable from Maven, JSON/YAML, Java code.
- Single shared config object mirroring Maven `@Parameter` names for plugin/XML/file binding.

#### Type
- class `SrcMorphConfiguration`, mutable JavaBean.
- Annotations: `@ToString` (Lombok), `@SuppressWarnings({"NullAway.Init", "initialization.fields.uninitialized"})`.

#### Input
- Constructor `SrcMorphConfiguration()` applies defaults, no parameters.
- Setters consume `File baseDirectory`, `File outputDirectory`, `boolean force/planOnly`, `List<String> subtrees/excludes/fileExtensions`, `long minFileSizeBytes/maxFileSizeBytes`, `String generationProvider/llamaModelPath/pluginVersion/aiVersion/projectName`, `int llamaContextSize/llamaMaxOutputTokens/llamaThreads`, `float llamaTemperature`, `File modelDirectory`, `List<AiPromptDefinition> promptDefinitions`, `List<AiModelDefinition> aiDefinitions`, `List<AiFieldGenerationConfig> fieldGenerations`, `List<AiFactDefinition> factDefinitions`.
- Defaults read from `DEFAULT_*` constants and `AiGenerationConfig.DEFAULT_THREADS`.

#### Output
- Getters expose configured `File`, `List`, `String`, `int/long/float/boolean` state.
- Mutated fields: 23 private instance fields via setters.
- No files/streams written directly; drives `.ai.md` generation engine.

#### Core logic
- Centralizes file-scope selection via `baseDirectory`, `subtrees`, `excludes`, `fileExtensions`, `minFileSizeBytes`, `maxFileSizeBytes`.
- Centralizes AI routing via `generationProvider`, `promptDefinitions`, `aiDefinitions`, `fieldGenerations`, `factDefinitions`.
- Provides llama.cpp JNI fallback via `llamaModelPath`, `llamaContextSize`, `llamaMaxOutputTokens`, `llamaTemperature`, `llamaThreads` when `fieldGenerations` empty.
- Resolves relative model paths against `modelDirectory`, absolute paths used as-is.
- Records provenance headers via `pluginVersion`, `aiVersion`, `projectName` for `.ai.md` and `aggregate-project` index.
- Supports dry-run routing via `planOnly` and overwrite control via `force`.
- Defaults `outputDirectory` to `src/site/ai`, `generationProvider` to `mock`, `aiVersion` to `0.0.0`.

#### Public API
- `SrcMorphConfiguration() -> SrcMorphConfiguration` — creates config with defaults.
- `getBaseDirectory() -> File` — returns project base directory.
- `setBaseDirectory(File) -> void` — sets project base directory.
- `getOutputDirectory() -> File` — returns `.ai.md` output directory.
- `setOutputDirectory(File) -> void` — sets `.ai.md` output directory.
- `isForce() -> boolean` — returns always-regenerate flag.
- `setForce(boolean) -> void` — sets always-regenerate flag.
- `getSubtrees() -> List<String>` — returns restricting source subtrees.
- `setSubtrees(List<String>) -> void` — sets restricting source subtrees.
- `getExcludes() -> List<String>` — returns skipped-file glob patterns.
- `setExcludes(List<String>) -> void` — sets skipped-file glob patterns.
- `getFileExtensions() -> List<String>` — returns indexed file extensions.
- `setFileExtensions(List<String>) -> void` — sets indexed file extensions.
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
- `getModelDirectory() -> File` — returns relative-model-path base.
- `setModelDirectory(File) -> void` — sets relative-model-path base.
- `getFieldGenerations() -> List<AiFieldGenerationConfig>` — returns per-field routing rules.
- `setFieldGenerations(List<AiFieldGenerationConfig>) -> void` — sets per-field routing rules.
- `getFactDefinitions() -> List<AiFactDefinition>` — returns named fact groups.
- `setFactDefinitions(List<AiFactDefinition>) -> void` — sets named fact groups.
- `getLlamaModelPath() -> String` — returns fallback GGUF model path.
- `setLlamaModelPath(String) -> void` — sets fallback GGUF model path.
- `getLlamaContextSize() -> int` — returns fallback context window size.
- `setLlamaContextSize(int) -> void` — sets fallback context window size.
- `getLlamaMaxOutputTokens() -> int` — returns fallback max output tokens.
- `setLlamaMaxOutputTokens(int) -> void` — sets fallback max output tokens.
- `getLlamaTemperature() -> float` — returns fallback sampling temperature.
- `setLlamaTemperature(float) -> void` — sets fallback sampling temperature.
- `getLlamaThreads() -> int` — returns fallback CPU thread count.
- `setLlamaThreads(int) -> void` — sets fallback CPU thread count.
- `getPluginVersion() -> String` — returns generator version header.
- `setPluginVersion(String) -> void` — sets generator version header.
- `getAiVersion() -> String` — returns summarisation logic version.
- `setAiVersion(String) -> void` — sets summarisation logic version.
- `getProjectName() -> String` — returns aggregate index title.
- `setProjectName(String) -> void` — sets aggregate index title.

#### Dependencies
- `java.io.File`, `java.util.List`, `lombok.ToString`, `net.ladenthin.srcmorph.prompt.AiPromptDefinition`, `org.jspecify.annotations.Nullable`, `AiGenerationConfig`, `AiModelDefinition`, `AiFieldGenerationConfig`, `AiFactDefinition`.

#### Exceptions / Errors
- Nullable `List/File/String` getters return `null` when unconfigured; callers must null-check.
- `<=0` disables `minFileSizeBytes` bound and means unlimited `maxFileSizeBytes`.
