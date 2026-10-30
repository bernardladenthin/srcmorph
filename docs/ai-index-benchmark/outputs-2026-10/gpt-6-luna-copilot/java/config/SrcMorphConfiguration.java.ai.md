### SrcMorphConfiguration.java
- H: 1.0
- C: 1D938DBE
- D: 2026-10-30T00:29:58Z
- T: 2026-10-30T00:34:41Z
- G: 
- A: 0.0.0
- X: file
---
> Configures source-file filtering, AI generation routing, model settings, and metadata for a srcmorph run.

#### Purpose
- `SrcMorphConfiguration` is the shared mutable JavaBean for run settings, independent of how configuration is bound.
- Excludes Maven lifecycle skip flags; an engine runs when invoked.

#### Type
- Public class; `@ToString`; no superclass or interfaces declared.
- Default constructor; mutable fields with setters.

#### Input
- Accepts project and output directories, source filters, file-size bounds, generation provider, prompt/model/routing/fact definitions, llama.cpp fallback settings, and version/title metadata.
- Nullable lists, files, and strings allow optional configuration.

#### Output
- Provides configuration values through getters; no direct resource writes or processing.
- Defaults: output directory `src/site/ai`; provider `mock`; llama context `2048`, max output tokens `128`, temperature `0.15f`, threads `AiGenerationConfig.DEFAULT_THREADS`; AI version `0.0.0`.

#### Core logic
- Stores settings for source scope (`subtrees`, `excludes`, extensions) and size limits.
- Stores AI prompt, model, field-generation routing, and reusable fact definitions.
- Provides llama.cpp fallback options, used when `fieldGenerations` is empty.
- Supports model-path resolution relative to `modelDirectory`; absolute paths remain usable.
- Exposes force-regeneration and plan-only flags, plus output metadata settings.
- Applies no validation or configuration binding itself.

#### Public API
- `DEFAULT_OUTPUT_DIRECTORY` → default output path.
- `DEFAULT_GENERATION_PROVIDER` → default provider.
- `DEFAULT_LLAMA_CONTEXT_SIZE` → default context size.
- `DEFAULT_LLAMA_MAX_OUTPUT_TOKENS` → default output-token limit.
- `DEFAULT_LLAMA_TEMPERATURE` → default sampling temperature.
- `DEFAULT_LLAMA_THREADS` → default inference thread count.
- `DEFAULT_AI_VERSION` → default AI logic version.
- `SrcMorphConfiguration()` → initializes field defaults.
- `getBaseDirectory() -> File` — read project base.
- `setBaseDirectory(File) -> void` — set project base.
- `getOutputDirectory() -> File` — read output directory.
- `setOutputDirectory(File) -> void` — set output directory.
- `isForce() -> boolean` — read regeneration flag.
- `setForce(boolean) -> void` — set regeneration flag.
- `getSubtrees() -> @Nullable List<String>` — read source subtrees.
- `setSubtrees(@Nullable List<String>) -> void` — set source subtrees.
- `getExcludes() -> @Nullable List<String>` — read exclude globs.
- `setExcludes(@Nullable List<String>) -> void` — set exclude globs.
- `getFileExtensions() -> @Nullable List<String>` — read indexed extensions.
- `setFileExtensions(@Nullable List<String>) -> void` — set indexed extensions.
- `getMinFileSizeBytes() -> long` — read exclusive lower size bound.
- `setMinFileSizeBytes(long) -> void` — set exclusive lower size bound.
- `getMaxFileSizeBytes() -> long` — read inclusive upper size bound.
- `setMaxFileSizeBytes(long) -> void` — set inclusive upper size bound.
- `isPlanOnly() -> boolean` — read planning-only flag.
- `setPlanOnly(boolean) -> void` — set planning-only flag.
- `getGenerationProvider() -> String` — read default AI provider.
- `setGenerationProvider(String) -> void` — set default AI provider.
- `getPromptDefinitions() -> @Nullable List<AiPromptDefinition>` — read prompt definitions.
- `setPromptDefinitions(@Nullable List<AiPromptDefinition>) -> void` — set prompt definitions.
- `getAiDefinitions() -> @Nullable List<AiModelDefinition>` — read model definitions.
- `setAiDefinitions(@Nullable List<AiModelDefinition>) -> void` — set model definitions.
- `getModelDirectory() -> @Nullable File` — read model path base.
- `setModelDirectory(@Nullable File) -> void` — set model path base.
- `getFieldGenerations() -> @Nullable List<AiFieldGenerationConfig>` — read routing rules.
- `setFieldGenerations(@Nullable List<AiFieldGenerationConfig>) -> void` — set routing rules.
- `getFactDefinitions() -> @Nullable List<AiFactDefinition>` — read fact groups.
- `setFactDefinitions(@Nullable List<AiFactDefinition>) -> void` — set fact groups.
- `getLlamaModelPath() -> @Nullable String` — read fallback model path.
- `setLlamaModelPath(@Nullable String) -> void` — set fallback model path.
- `getLlamaContextSize() -> int` — read fallback context size.
- `setLlamaContextSize(int) -> void` — set fallback context size.
- `getLlamaMaxOutputTokens() -> int` — read fallback token limit.
- `setLlamaMaxOutputTokens(int) -> void` — set fallback token limit.
- `getLlamaTemperature() -> float` — read fallback sampling temperature.
- `setLlamaTemperature(float) -> void` — set fallback sampling temperature.
- `getLlamaThreads() -> int` — read fallback thread count.
- `setLlamaThreads(int) -> void` — set fallback thread count.
- `getPluginVersion() -> String` — read generator version.
- `setPluginVersion(String) -> void` — set generator version.
- `getAiVersion() -> String` — read AI logic version.
- `setAiVersion(String) -> void` — set AI logic version.
- `getProjectName() -> @Nullable String` — read aggregate index title.
- `setProjectName(@Nullable String) -> void` — set aggregate index title.

#### Dependencies
- Java: `File`, `List`.
- Project types: `AiPromptDefinition`, `AiModelDefinition`, `AiFieldGenerationConfig`, `AiFactDefinition`, `AiGenerationConfig`.
- Annotations: Lombok `@ToString`; JSpecify `@Nullable`.

#### Exceptions / Errors
- No exceptions declared or handled; no validation or explicit null checks.

#### Concurrency
- Mutable, unsynchronized fields; no thread-safety guarantees.
