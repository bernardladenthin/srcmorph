### SrcMorphConfiguration.java
- H: 1.0
- C: 1D938DBE
- D: 2026-10-30T00:29:58Z
- T: 2026-10-30T00:40:23Z
- G: 
- A: 0.0.0
- X: file
---
I will produce a dense structured markdown summary of the Java source file.

Seven public constants are present. The constructor and all getters and setters will be listed with their signatures and concise purpose clauses.

The getters and setters are distinct named accessors with different purposes, so they will be listed individually rather than collapsed into a family.

The total is 47 public members: 7 constants, 1 constructor, and 39 accessors. The getters include boolean predicates such as `isForce` and `isPlanOnly`.

The 23 getters are now fully enumerated. The private fields are being catalogued.

The private fields total 23, matching the 23 getter and setter pairs. Each accessor pair has a distinct searchable name, so all will be listed individually rather than collapsed into a numbered family.

The indexing format takes priority over the general shortness instruction. No exceptions are thrown. The class is a mutable JavaBean and not thread-safe. Lombok's @ToString generates toString, and @SuppressWarnings NullAway.Init suppresses initialization warnings. Dependencies include java.io.File, java.util.List, and lombok.ToString.

The class also references `AiGenerationConfig`, `AiModelDefinition`, `AiFieldGenerationConfig`, and `AiFactDefinition` from the same package. `org.jspecify.annotations.Nullable` is used. I am now drafting the structured index.

The index will use `name(params) -> returnType` for methods and `NAME -> Type` for constants. Each purpose clause is limited to six words. The output begins with a blockquote and omits sections that do not apply.> Holds every parameter a srcmorph run needs to index sources and generate `.ai.md` summaries.

#### Purpose
- Mutable shared JavaBean for a `srcmorph` run, bindable from Maven `@Parameter` fields, future JSON/YAML, or plain Java.
- Field names match existing plugin XML keys; Maven `skip` / `skipFile` / `skipPackage` / `skipProject` stay mojo-side and are excluded.

#### Type
- Concrete class `SrcMorphConfiguration` in `net.ladenthin.srcmorph.config`; not abstract, final, or sealed; no superclass or interfaces.
- `@ToString` (Lombok); `@SuppressWarnings({"NullAway.Init", "initialization.fields.uninitialized"})`.

#### Input
- No-arg constructor; no injected dependencies.
- Setters consume `File`, `boolean`, `long`, `String`, `int`, `float`, and `@Nullable` lists of `String`, `AiPromptDefinition`, `AiModelDefinition`, `AiFieldGenerationConfig`, `AiFactDefinition`.
- `DEFAULT_LLAMA_THREADS` reads `AiGenerationConfig.DEFAULT_THREADS`.

#### Output
- Getters return the 23 stored fields; setters mutate them.
- Lombok `toString()` reflects current state.
- No files, streams, or generation side effects in this type.

#### Core logic
- JavaBean only: constructor is a no-op; defaults are field initializers, not constructor logic.
- `outputDirectory` defaults to `new File("src/site/ai")`; `generationProvider` to `"mock"`; llama fallbacks to context `2048`, max output tokens `128`, temperature `0.15f`, threads from `AiGenerationConfig.DEFAULT_THREADS`.
- `pluginVersion` defaults to `""`; `aiVersion` to `"0.0.0"`; `force`, `planOnly`, and both file-size bounds default to unset/`false`/`0`.
- Nullable lists (`subtrees`, `excludes`, `fileExtensions`, `promptDefinitions`, `aiDefinitions`, `fieldGenerations`, `factDefinitions`) and nullable `modelDirectory`, `llamaModelPath`, `projectName` mean “not configured”; engine interprets those.
- `minFileSizeBytes <= 0` disables the lower bound; `maxFileSizeBytes <= 0` means unlimited.
- `modelDirectory` resolves relative `aiDefinitions` `modelPath` values and `llamaModelPath`; absolute paths are used as-is.
- `llamaModelPath` and llama numeric fields are fallbacks only when `fieldGenerations` is empty.
- `pluginVersion` and `aiVersion` are recorded in written `.ai.md` headers; `projectName` is the `aggregate-project` index title.

#### Public API
- `DEFAULT_OUTPUT_DIRECTORY -> String` — default relative output dir
- `DEFAULT_GENERATION_PROVIDER -> String` — default provider `"mock"`
- `DEFAULT_LLAMA_CONTEXT_SIZE -> int` — default context `2048`
- `DEFAULT_LLAMA_MAX_OUTPUT_TOKENS -> int` — default max tokens `128`
- `DEFAULT_LLAMA_TEMPERATURE -> float` — default temperature `0.15f`
- `DEFAULT_LLAMA_THREADS -> int` — default llama CPU threads
- `DEFAULT_AI_VERSION -> String` — default AI logic version
- `SrcMorphConfiguration() -> SrcMorphConfiguration` — create bean with defaults
- `getBaseDirectory() -> File` — project base directory
- `setBaseDirectory(File) -> void` — set project base directory
- `getOutputDirectory() -> File` — `.ai.md` output directory
- `setOutputDirectory(File) -> void` — set `.ai.md` output directory
- `isForce() -> boolean` — regenerate existing AI fields
- `setForce(boolean) -> void` — set always-regenerate flag
- `getSubtrees() -> @Nullable List<String>` — source roots in scope
- `setSubtrees(@Nullable List<String>) -> void` — restrict source subtrees
- `getExcludes() -> @Nullable List<String>` — base-relative skip globs
- `setExcludes(@Nullable List<String>) -> void` — set skip globs
- `getFileExtensions() -> @Nullable List<String>` — extensions to index
- `setFileExtensions(@Nullable List<String>) -> void` — set indexed extensions
- `getMinFileSizeBytes() -> long` — exclusive lower size bound
- `setMinFileSizeBytes(long) -> void` — set lower size bound
- `getMaxFileSizeBytes() -> long` — inclusive upper size bound
- `setMaxFileSizeBytes(long) -> void` — set upper size bound
- `isPlanOnly() -> boolean` — stop after routing plan
- `setPlanOnly(boolean) -> void` — set plan-only flag
- `getGenerationProvider() -> String` — default AI provider name
- `setGenerationProvider(String) -> void` — set AI provider name
- `getPromptDefinitions() -> @Nullable List<AiPromptDefinition>` — prompt templates
- `setPromptDefinitions(@Nullable List<AiPromptDefinition>) -> void` — set prompt templates
- `getAiDefinitions() -> @Nullable List<AiModelDefinition>` — named model parameters
- `setAiDefinitions(@Nullable List<AiModelDefinition>) -> void` — set model definitions
- `getModelDirectory() -> @Nullable File` — relative model-path root
- `setModelDirectory(@Nullable File) -> void` — set model directory
- `getFieldGenerations() -> @Nullable List<AiFieldGenerationConfig>` — per-field routing rules
- `setFieldGenerations(@Nullable List<AiFieldGenerationConfig>) -> void` — set routing rules
- `getFactDefinitions() -> @Nullable List<AiFactDefinition>` — named fact groups
- `setFactDefinitions(@Nullable List<AiFactDefinition>) -> void` — set fact groups
- `getLlamaModelPath() -> @Nullable String` — GGUF fallback model path
- `setLlamaModelPath(@Nullable String) -> void` — set GGUF fallback path
- `getLlamaContextSize() -> int` — llama context fallback
- `setLlamaContextSize(int) -> void` — set context fallback
- `getLlamaMaxOutputTokens() -> int` — llama token-cap fallback
- `setLlamaMaxOutputTokens(int) -> void` — set token-cap fallback
- `getLlamaTemperature() -> float` — llama temperature fallback
- `setLlamaTemperature(float) -> void` — set temperature fallback
- `getLlamaThreads() -> int` — llama thread-count fallback
- `setLlamaThreads(int) -> void` — set thread-count fallback
- `getPluginVersion() -> String` — generator version for headers
- `setPluginVersion(String) -> void` — set generator version
- `getAiVersion() -> String` — summarisation version for headers
- `setAiVersion(String) -> void` — set summarisation version
- `getProjectName() -> @Nullable String` — aggregate index title
- `setProjectName(@Nullable String) -> void` — set aggregate index title

Public members: 7 constants, 1 constructor, 23 getters, 23 setters (54 total).

#### Dependencies
- `java.io.File`, `java.util.List`, `lombok.ToString`, `org.jspecify.annotations.Nullable`
- `net.ladenthin.srcmorph.prompt.AiPromptDefinition`
- Same-config types: `AiGenerationConfig`, `AiModelDefinition`, `AiFieldGenerationConfig`, `AiFactDefinition`

#### Concurrency
- Mutable unsynchronized JavaBean; not thread-safe. Intended to be populated then read by an engine that always executes when asked.
