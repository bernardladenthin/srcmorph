### SrcMorphConfiguration.java
- H: 1.0
- C: 1D938DBE
- D: 2026-11-04T11:46:43Z
- T: 2026-11-04T11:47:26Z
- G: 
- A: 0.0.0
- X: file
- P: 0B812A3C
---
> Configures source-file AI indexing, generation routing, model inference, and output metadata for a srcmorph run.

#### Purpose
- Provides a mutable JavaBean configuration shared across Maven, future file-based binding, and direct Java callers.
- Holds source-selection rules, AI prompt/model routing, llama.cpp fallback settings, and generated-index metadata.

#### Type
- `public class SrcMorphConfiguration` — class; annotated `@ToString`; mutable JavaBean with no declared supertypes.
- Constants define default output directory, generation provider, llama.cpp context size, output tokens, temperature, thread count, and AI version.

#### Input
- No-argument constructor; initializes defaults.
- Setters accept project and output directories; source subtree, exclude, and extension lists; file-size bounds; force and plan-only flags; provider; prompt, model, field-generation, and fact-definition lists; model paths and llama.cpp settings; plugin and AI versions; project title.
- Nullable configuration includes `subtrees`, `excludes`, `fileExtensions`, prompt/model/routing/fact lists, `modelDirectory`, `llamaModelPath`, and `projectName`.

#### Output
- Getters expose all configured values; setters mutate the corresponding fields.
- Defaults: `outputDirectory = "src/site/ai"`, `generationProvider = "mock"`, `llamaContextSize = 2048`, `llamaMaxOutputTokens = 128`, `llamaTemperature = 0.15f`, `llamaThreads = AiGenerationConfig.DEFAULT_THREADS`, `pluginVersion = ""`, and `aiVersion = "0.0.0"`.
- Primitive booleans and numeric limits default to Java zero values; nullable references default to `null`.

#### Core logic
- Constructor `SrcMorphConfiguration()` performs no additional work; field initializers supply configured defaults.
- Stores source-selection controls: `baseDirectory`, `subtrees`, `excludes`, `fileExtensions`, `minFileSizeBytes`, and `maxFileSizeBytes`.
- Stores output and execution controls: `outputDirectory`, `force`, and `planOnly`.
- Stores AI routing definitions: `generationProvider`, `promptDefinitions`, `aiDefinitions`, `fieldGenerations`, and `factDefinitions`.
- Stores model resolution and llama.cpp fallback settings: `modelDirectory`, `llamaModelPath`, `llamaContextSize`, `llamaMaxOutputTokens`, `llamaTemperature`, and `llamaThreads`.
- Stores generated-file metadata: `pluginVersion`, `aiVersion`, and `projectName`.
- Relative model paths resolve against `modelDirectory` when configured; otherwise they remain relative to the JVM working directory. Absolute paths remain unchanged.
- `llamaModelPath` and `llamaContextSize` are fallback settings used only when `fieldGenerations` is empty.

#### Public API
- `SrcMorphConfiguration()` -> constructor; initializes defaults.
- `getBaseDirectory()` -> `File`; reads project base directory.
- `setBaseDirectory(File)` -> `void`; sets project base directory.
- `getOutputDirectory()` -> `File`; reads AI output directory.
- `setOutputDirectory(File)` -> `void`; sets AI output directory.
- `isForce()` -> `boolean`; reads forced regeneration flag.
- `setForce(boolean)` -> `void`; sets forced regeneration flag.
- `getSubtrees()` -> `@Nullable List<String>`; reads source subtrees.
- `setSubtrees(@Nullable List<String>)` -> `void`; sets source subtrees.
- `getExcludes()` -> `@Nullable List<String>`; reads excluded-file globs.
- `setExcludes(@Nullable List<String>)` -> `void`; sets excluded-file globs.
- `getFileExtensions()` -> `@Nullable List<String>`; reads indexed extensions.
- `setFileExtensions(@Nullable List<String>)` -> `void`; sets indexed extensions.
- `getMinFileSizeBytes()` -> `long`; reads exclusive lower size bound.
- `setMinFileSizeBytes(long)` -> `void`; sets exclusive lower size bound.
- `getMaxFileSizeBytes()` -> `long`; reads inclusive upper size bound.
- `setMaxFileSizeBytes(long)` -> `void`; sets inclusive upper size bound.
- `isPlanOnly()` -> `boolean`; reads planning-only flag.
- `setPlanOnly(boolean)` -> `void`; sets planning-only flag.
- `getGenerationProvider()` -> `String`; reads default AI provider.
- `setGenerationProvider(String)` -> `void`; sets default AI provider.
- `getPromptDefinitions()` -> `@Nullable List<AiPromptDefinition>`; reads prompt templates.
- `setPromptDefinitions(@Nullable List<AiPromptDefinition>)` -> `void`; sets prompt templates.
- `getAiDefinitions()` -> `@Nullable List<AiModelDefinition>`; reads model definitions.
- `setAiDefinitions(@Nullable List<AiModelDefinition>)` -> `void`; sets model definitions.
- `getModelDirectory()` -> `@Nullable File`; reads relative model-path base.
- `setModelDirectory(@Nullable File)` -> `void`; sets relative model-path base.
- `getFieldGenerations()` -> `@Nullable List<AiFieldGenerationConfig>`; reads field-routing rules.
- `setFieldGenerations(@Nullable List<AiFieldGenerationConfig>)` -> `void`; sets field-routing rules.
- `getFactDefinitions()` -> `@Nullable List<AiFactDefinition>`; reads reusable fact groups.
- `setFactDefinitions(@Nullable List<AiFactDefinition>)` -> `void`; sets reusable fact groups.
- `getLlamaModelPath()` -> `@Nullable String`; reads llama.cpp fallback model path.
- `setLlamaModelPath(@Nullable String)` -> `void`; sets llama.cpp fallback model path.
- `getLlamaContextSize()` -> `int`; reads llama.cpp fallback context size.
- `setLlamaContextSize(int)` -> `void`; sets llama.cpp fallback context size.
- `getLlamaMaxOutputTokens()` -> `int`; reads llama.cpp output-token limit.
- `setLlamaMaxOutputTokens(int)` -> `void`; sets llama.cpp output-token limit.
- `getLlamaTemperature()` -> `float`; reads llama.cpp sampling temperature.
- `setLlamaTemperature(float)` -> `void`; sets llama.cpp sampling temperature.
- `getLlamaThreads()` -> `int`; reads llama.cpp CPU thread count.
- `setLlamaThreads(int)` -> `void`; sets llama.cpp CPU thread count.
- `getPluginVersion()` -> `String`; reads generator version metadata.
- `setPluginVersion(String)` -> `void`; sets generator version metadata.
- `getAiVersion()` -> `String`; reads AI logic version metadata.
- `setAiVersion(String)` -> `void`; sets AI logic version metadata.
- `getProjectName()` -> `@Nullable String`; reads aggregate index title.
- `setProjectName(@Nullable String)` -> `void`; sets aggregate index title.

#### Dependencies
- `java.io.File`, `java.util.List`
- `lombok.ToString`
- `net.ladenthin.srcmorph.prompt.AiPromptDefinition`
- `AiGenerationConfig`, `AiModelDefinition`, `AiFieldGenerationConfig`, `AiFactDefinition`
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- Nullable fields and setters permit `null`; no validation, explicit exception handling, or precondition checks.
- Size-bound documentation specifies that values `<= 0` disable the lower bound or make the upper bound unlimited.

#### Concurrency
- Mutable, unsynchronized configuration object; no thread-safety guarantees are declared.
