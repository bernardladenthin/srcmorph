### SrcMorphConfiguration.java
- H: 1.0
- C: 1D938DBE
- D: 2026-10-28T21:24:22Z
- T: 2026-10-28T22:41:42Z
- G: 
- A: 0.0.0
- X: file
---
> Central mutable configuration bean that aggregates every parameter (file paths, AI provider settings, llama.cpp inference knobs, routing rules, versioning) needed to drive a single `srcmorph` AI-documentation generation run, bindable from Maven plugin parameters, JSON/YAML, or plain Java.

#### Purpose
- Single shared config object for the `srcmorph` engine; field names intentionally mirror Maven `@Parameter` names so the same keys work across binding surfaces.
- Deliberately excludes per-goal `skip`/`skipFile`/`skipPackage`/`skipProject` flags (those remain mojo-side lifecycle concerns).

#### Type
- `public class SrcMorphConfiguration` — no extends/implements.
- Annotations: `@SuppressWarnings({"NullAway.Init", "initialization.fields.uninitialized"})`, `@ToString` (Lombok).

#### Input
- Populated via 23 setters (Maven Plexus reflection, Jackson `ObjectMapper`/`YAMLMapper`, or direct Java calls).
- No constructor parameters; no-arg constructor only.

#### Output
- 23 getter methods returning the configured values; no side effects, no I/O.

#### Core logic
- Pure data holder (JavaBean); no business logic, no validation, no computation.
- 7 `public static final` constants provide defaults: `DEFAULT_OUTPUT_DIRECTORY` (`"src/site/ai"`), `DEFAULT_GENERATION_PROVIDER` (`"mock"`), `DEFAULT_LLAMA_CONTEXT_SIZE` (2048), `DEFAULT_LLAMA_MAX_OUTPUT_TOKENS` (128), `DEFAULT_LLAMA_TEMPERATURE` (0.15f), `DEFAULT_LLAMA_THREADS` (delegates to `AiGenerationConfig.DEFAULT_THREADS`), `DEFAULT_AI_VERSION` (`"0.0.0"`).
- Fields with `@Nullable` (11 of 23): `subtrees`, `excludes`, `fileExtensions`, `promptDefinitions`, `aiDefinitions`, `modelDirectory`, `fieldGenerations`, `factDefinitions`, `llamaModelPath`, `projectName` — `null` means "use engine default" or "not configured".
- `llamaModelPath`, `llamaContextSize`, `llamaMaxOutputTokens`, `llamaTemperature`, `llamaThreads` are fallback values used only when `fieldGenerations` is empty.
- `modelDirectory` resolves relative model paths; absolute paths bypass it.

#### Public API
- `SrcMorphConfiguration()` -> `SrcMorphConfiguration` — no-arg constructor, all defaults applied.
- `getBaseDirectory()` -> `File` — project base directory.
- `setBaseDirectory(File)` -> `void` — set project base directory.
- `getOutputDirectory()` -> `File` — `.ai.md` output directory.
- `setOutputDirectory(File)` -> `void` — set output directory.
- `isForce()` -> `boolean` — always regenerate AI fields.
- `setForce(boolean)` -> `void` — set force flag.
- `getSubtrees()` -> `@Nullable List<String>` — restrict processing to subdirs.
- `setSubtrees(@Nullable List<String>)` -> `void` — set subtree filter.
- `getExcludes()` -> `@Nullable List<String>` — glob patterns to skip.
- `setExcludes(@Nullable List<String>)` -> `void` — set exclude globs.
- `getFileExtensions()` -> `@Nullable List<String>` — file extensions to index.
- `setFileExtensions(@Nullable List<String>)` -> `void` — set file extensions.
- `getMinFileSizeBytes()` -> `long` — exclusive lower size bound.
- `setMinFileSizeBytes(long)` -> `void` — set lower size bound.
- `getMaxFileSizeBytes()` -> `long` — inclusive upper size bound.
- `setMaxFileSizeBytes(long)` -> `void` — set upper size bound.
- `isPlanOnly()` -> `boolean` — stop after routing plan.
- `setPlanOnly(boolean)` -> `void` — set plan-only flag.
- `getGenerationProvider()` -> `String` — AI provider name.
- `setGenerationProvider(String)` -> `void` — set provider name.
- `getPromptDefinitions()` -> `@Nullable List<AiPromptDefinition>` — prompt templates.
- `setPromptDefinitions(@Nullable List<AiPromptDefinition>)` -> `void` — set prompt templates.
- `getAiDefinitions()` -> `@Nullable List<AiModelDefinition>` — model definitions.
- `setAiDefinitions(@Nullable List<AiModelDefinition>)` -> `void` — set model definitions.
- `getModelDirectory()` -> `@Nullable File` — base for relative model paths.
- `setModelDirectory(@Nullable File)` -> `void` — set model directory.
- `getFieldGenerations()` -> `@Nullable List<AiFieldGenerationConfig>` — per-field routing rules.
- `setFieldGenerations(@Nullable List<AiFieldGenerationConfig>)` -> `void` — set routing rules.
- `getFactDefinitions()` -> `@Nullable List<AiFactDefinition>` — named fact groups.
- `setFactDefinitions(@Nullable List<AiFactDefinition>)` -> `void` — set fact groups.
- `getLlamaModelPath()` -> `@Nullable String` — GGUF fallback model path.
- `setLlamaModelPath(@Nullable String)` -> `void` — set fallback model path.
- `getLlamaContextSize()` -> `int` — llama.cpp context window.
- `setLlamaContextSize(int)` -> `void` — set context window.
- `getLlamaMaxOutputTokens()` -> `int` — llama.cpp max output tokens.
- `setLlamaMaxOutputTokens(int)` -> `void` — set max output tokens.
- `getLlamaTemperature()` -> `float` — llama.cpp sampling temperature.
- `setLlamaTemperature(float)` -> `void` — set temperature.
- `getLlamaThreads()` -> `int` — llama.cpp CPU thread count.
- `setLlamaThreads(int)` -> `void` — set thread count.
- `getPluginVersion()` -> `String` — generator version for headers.
- `setPluginVersion(String)` -> `void` — set generator version.
- `getAiVersion()` -> `String` — AI logic version for headers.
- `setAiVersion(String)` -> `void` — set AI version.
- `getProjectName()` -> `@Nullable String` — aggregate-project index title.
- `setProjectName(@Nullable String)` -> `void` — set project title.

Total: 1 constructor + 23 getters + 23 setters = 47 public methods.

#### Dependencies
- `java.io.File`
- `java.util.List`
- `lombok.ToString`
- `net.ladenthin.srcmorph.prompt.AiPromptDefinition`
- `net.ladenthin.srcmorph.config.AiModelDefinition`
- `net.ladenthin.srcmorph.config.AiFieldGenerationConfig`
- `net.ladenthin.srcmorph.config.AiFactDefinition`
- `net.ladenthin.srcmorph.config.AiGenerationConfig` (referenced via `DEFAULT_THREADS`)
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- None thrown or caught; pure data holder.
- `@Nullable` fields signal "not configured" to callers; no null-checks performed here.

#### Concurrency
- No synchronization; mutable bean, not thread-safe. Caller is responsible for populating before sharing.
