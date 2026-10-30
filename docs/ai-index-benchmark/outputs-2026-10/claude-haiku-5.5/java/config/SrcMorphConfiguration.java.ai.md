### SrcMorphConfiguration.java
- H: 1.0
- C: 1D938DBE
- D: 2026-10-30T00:29:58Z
- T: 2026-10-30T00:30:38Z
- G: 
- A: 0.0.0
- X: file
---
> Holds every configuration parameter for an AI-driven Java source summarisation run, bindable from Maven, YAML/JSON, or code.

#### Purpose
- Root mutable JavaBean carrying all run settings for the srcmorph engine.
- Field names mirror Maven `@Parameter` names so config keys match plugin XML.

#### Type
- Class, public, mutable JavaBean (no final).
- Annotations: `@ToString` (Lombok); `@SuppressWarnings({"NullAway.Init", "initialization.fields.uninitialized"})`.
- Null-marked fields use `@Nullable` (org.jspecify).

#### Input
- No constructor parameters; public no-arg constructor.
- Setters (all public, one per field):
  - `baseDirectory` (File), `outputDirectory` (File), `force` (boolean)
  - `subtrees`, `excludes`, `fileExtensions` (List&lt;String&gt;, nullable)
  - `minFileSizeBytes`, `maxFileSizeBytes` (long)
  - `planOnly` (boolean), `generationProvider` (String)
  - `promptDefinitions` (List&lt;AiPromptDefinition&gt;, nullable)
  - `aiDefinitions` (List&lt;AiModelDefinition&gt;, nullable)
  - `modelDirectory` (File, nullable), `fieldGenerations` (List&lt;AiFieldGenerationConfig&gt;, nullable)
  - `factDefinitions` (List&lt;AiFactDefinition&gt;, nullable)
  - `llamaModelPath` (String, nullable), `llamaContextSize`, `llamaMaxOutputTokens` (int), `llamaTemperature` (float), `llamaThreads` (int)
  - `pluginVersion`, `aiVersion` (String), `projectName` (String, nullable)

#### Output
- Getters return stored state; no computation or I/O.
- Setters mutate fields only.

#### Core logic
- Holds state only; no branching or processing logic.
- Defaults applied at field initialisation:
  - `outputDirectory` = `src/site/ai`
  - `generationProvider` = `mock`
  - `llamaContextSize` = 2048
  - `llamaMaxOutputTokens` = 128
  - `llamaTemperature` = 0.15f
  - `llamaThreads` = `AiGenerationConfig.DEFAULT_THREADS`
  - `aiVersion` = `0.0.0`
  - `pluginVersion` = empty string
- Excludes per-goal skip flags (`skip`, `skipFile`, `skipPackage`, `skipProject`); these stay mojo-side.
- `llamaModelPath` and `llamaContext*`/`llamaMaxOutputTokens`/`llamaTemperature`/`llamaThreads` apply only when `fieldGenerations` is empty.
- `modelDirectory` resolves relative model paths; absolute paths bypass it.

#### Public API
- `SrcMorphConfiguration()` -> constructor; no-op, defaults applied.
- `getBaseDirectory()` -> File; returns project base directory.
- `setBaseDirectory(File)` -> void; sets base directory.
- `getOutputDirectory()` -> File; output directory for `.ai.md`.
- `setOutputDirectory(File)` -> void; sets output directory.
- `isForce()` -> boolean; regenerate existing fields.
- `setForce(boolean)` -> void; sets force regeneration.
- `getSubtrees()` -> List&lt;String&gt;; restricting source subtrees, nullable.
- `setSubtrees(List&lt;String&gt;)` -> void; sets subtrees.
- `getExcludes()` -> List&lt;String&gt;; exclude globs, nullable.
- `setExcludes(List&lt;String&gt;)` -> void; sets exclude globs.
- `getFileExtensions()` -> List&lt;String&gt;; indexed extensions, nullable.
- `setFileExtensions(List&lt;String&gt;)` -> void; sets extensions.
- `getMinFileSizeBytes()` -> long; exclusive lower size bound.
- `setMinFileSizeBytes(long)` -> void; sets lower bound.
- `getMaxFileSizeBytes()` -> long; inclusive upper size bound.
- `setMaxFileSizeBytes(long)` -> void; sets upper bound.
- `isPlanOnly()` -> boolean; stop after routing plan.
- `setPlanOnly(boolean)` -> void; sets plan-only mode.
- `getGenerationProvider()` -> String; AI provider name.
- `setGenerationProvider(String)` -> void; sets provider name.
- `getPromptDefinitions()` -> List&lt;AiPromptDefinition&gt;; prompt templates, nullable.
- `setPromptDefinitions(List&lt;AiPromptDefinition&gt;)` -> void; sets prompt templates.
- `getAiDefinitions()` -> List&lt;AiModelDefinition&gt;; model definitions, nullable.
- `setAiDefinitions(List&lt;AiModelDefinition&gt;)` -> void; sets model definitions.
- `getModelDirectory()` -> File; base for relative model paths, nullable.
- `setModelDirectory(File)` -> void; sets model directory.
- `getFieldGenerations()` -> List&lt;AiFieldGenerationConfig&gt;; routing rules, nullable.
- `setFieldGenerations(List&lt;AiFieldGenerationConfig&gt;)` -> void; sets routing rules.
- `getFactDefinitions()` -> List&lt;AiFactDefinition&gt;; named fact groups, nullable.
- `setFactDefinitions(List&lt;AiFactDefinition&gt;)` -> void; sets fact groups.
- `getLlamaModelPath()` -> String; GGUF model path, nullable.
- `setLlamaModelPath(String)` -> void; sets GGUF model path.
- `getLlamaContextSize()` -> int; llama.cpp context window.
- `setLlamaContextSize(int)` -> void; sets context window.
- `getLlamaMaxOutputTokens()` -> int; max output tokens per call.
- `setLlamaMaxOutputTokens(int)` -> void; sets max output tokens.
- `getLlamaTemperature()` -> float; sampling temperature.
- `setLlamaTemperature(float)` -> void; sets sampling temperature.
- `getLlamaThreads()` -> int; CPU thread count.
- `setLlamaThreads(int)` -> void; sets CPU thread count.
- `getPluginVersion()` -> String; generator version in headers.
- `setPluginVersion(String)` -> void; sets generator version.
- `getAiVersion()` -> String; summarisation logic version.
- `setAiVersion(String)` -> void; sets summarisation version.
- `getProjectName()` -> String; aggregate index title, nullable.
- `setProjectName(String)` -> void; sets index title.
- Constants (public static final): `DEFAULT_OUTPUT_DIRECTORY`, `DEFAULT_GENERATION_PROVIDER`, `DEFAULT_LLAMA_CONTEXT_SIZE`, `DEFAULT_LLAMA_MAX_OUTPUT_TOKENS`, `DEFAULT_LLAMA_TEMPERATURE`, `DEFAULT_LLAMA_THREADS`, `DEFAULT_AI_VERSION`.

#### Dependencies
- `java.io.File`, `java.util.List`
- `lombok.ToString`
- `org.jspecify.annotations.Nullable`
- `net.ladenthin.srcmorph.prompt.AiPromptDefinition`
- `AiModelDefinition`, `AiFieldGenerationConfig`, `AiFactDefinition`, `AiGenerationConfig` (same package or unimported in source)

#### Exceptions / Errors
- No exceptions thrown or caught.
- Nullable fields: `subtrees`, `excludes`, `fileExtensions`, `promptDefinitions`, `aiDefinitions`, `modelDirectory`, `fieldGenerations`, `factDefinitions`, `llamaModelPath`, `projectName`.
- `baseDirectory` unset by default; `getBaseDirectory()` may return null without annotation.

#### Concurrency
- Not thread-safe; mutable fields with plain setters.
- No synchronization, no volatile fields.
