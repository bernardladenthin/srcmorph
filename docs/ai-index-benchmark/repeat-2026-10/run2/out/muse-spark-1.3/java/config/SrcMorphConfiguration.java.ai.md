### SrcMorphConfiguration.java
- H: 1.0
- C: 1D938DBE
- D: 2026-10-31T15:13:57Z
- T: 2026-10-31T15:15:05Z
- G: 
- A: 0.0.0
- X: file
---
> Centralizes all settings for AI-generated source documentation runs.
#### Purpose
- Mutable JavaBean holding every `srcmorph` run parameter bindable from Maven, JSON/YAML, Java code.
- Single shared config object with field names mirroring Maven `@Parameter` names.
#### Type
- class public `SrcMorphConfiguration`, mutable JavaBean.
- Annotations: `@ToString` (Lombok), `@SuppressWarnings({"NullAway.Init", "initialization.fields.uninitialized"})`.
#### Input
- No-arg constructor `SrcMorphConfiguration()` applies defaults, no parameters.
- Setter parameters: `baseDirectory: File`, `outputDirectory: File`, `force: boolean`, `subtrees/excludes/fileExtensions: List<String>`, `minFileSizeBytes/maxFileSizeBytes: long`, `planOnly: boolean`, `generationProvider: String`, `promptDefinitions: List<AiPromptDefinition>`, `aiDefinitions: List<AiModelDefinition>`, `modelDirectory: File`, `fieldGenerations: List<AiFieldGenerationConfig>`, `factDefinitions: List<AiFactDefinition>`, `llamaModelPath: String`, `llamaContextSize/llamaMaxOutputTokens/llamaThreads: int`, `llamaTemperature: float`, `pluginVersion/aiVersion/projectName: String`.
- Consumed defaults: `DEFAULT_OUTPUT_DIRECTORY="src/site/ai"`, `DEFAULT_GENERATION_PROVIDER="mock"`, `DEFAULT_LLAMA_CONTEXT_SIZE=2048`, `DEFAULT_LLAMA_MAX_OUTPUT_TOKENS=128`, `DEFAULT_LLAMA_TEMPERATURE=0.15f`, `DEFAULT_LLAMA_THREADS=AiGenerationConfig.DEFAULT_THREADS`, `DEFAULT_AI_VERSION="0.0.0"`.
#### Output
- Getters return `File`, `boolean`, `List<String>`, `long`, `int`, `float`, `String`, `List<AiPromptDefinition/AiModelDefinition/AiFieldGenerationConfig/AiFactDefinition>`.
- Mutated state: 23 private fields covering directories, filters, AI provider/models/prompts/routing, llama.cpp fallback, versions.
- Side effect: none; pure configuration holder, engine executes when asked.
#### Core logic
- Provides defaults for `outputDirectory`, `generationProvider`, `llamaContextSize`, `llamaMaxOutputTokens`, `llamaTemperature`, `llamaThreads`, `aiVersion`.
- Defines source scope via `baseDirectory`, `subtrees`, `excludes` globs, `fileExtensions`, `minFileSizeBytes`, `maxFileSizeBytes`.
- Defines AI generation via `generationProvider`, `promptDefinitions`, `aiDefinitions`, `modelDirectory`, `fieldGenerations` routing rules, `factDefinitions`, llama.cpp fallback `llamaModelPath` + params.
- Defines output headers via `pluginVersion`, `aiVersion`, `projectName`, plus `force` regeneration and `planOnly` dry-run.
- Deliberately excludes Maven lifecycle `skip/skipFile/skipPackage/skipProject` flags.
#### Public API
- SrcMorphConfiguration() -> SrcMorphConfiguration : create with defaults applied
- getBaseDirectory() -> File : return project base directory
- setBaseDirectory(File) -> void : set project base directory
- getOutputDirectory() -> File : return .ai.md output directory
- setOutputDirectory(File) -> void : set .ai.md output directory
- isForce() -> boolean : check always-regenerate flag
- setForce(boolean) -> void : set always-regenerate flag
- getSubtrees() -> List<String> : return restricting source subtrees
- setSubtrees(List<String>) -> void : set restricting source subtrees
- getExcludes() -> List<String> : return skip glob patterns
- setExcludes(List<String>) -> void : set skip glob patterns
- getFileExtensions() -> List<String> : return extensions to index
- setFileExtensions(List<String>) -> void : set extensions to index
- getMinFileSizeBytes() -> long : return lower size bound
- setMinFileSizeBytes(long) -> void : set lower size bound
- getMaxFileSizeBytes() -> long : return upper size bound
- setMaxFileSizeBytes(long) -> void : set upper size bound
- isPlanOnly() -> boolean : check stop-after-plan flag
- setPlanOnly(boolean) -> void : set stop-after-plan flag
- getGenerationProvider() -> String : return AI provider name
- setGenerationProvider(String) -> void : set AI provider name
- getPromptDefinitions() -> List<AiPromptDefinition> : return prompt templates
- setPromptDefinitions(List<AiPromptDefinition>) -> void : set prompt templates
- getAiDefinitions() -> List<AiModelDefinition> : return AI model definitions
- setAiDefinitions(List<AiModelDefinition>) -> void : set AI model definitions
- getModelDirectory() -> File : return relative model base
- setModelDirectory(File) -> void : set relative model base
- getFieldGenerations() -> List<AiFieldGenerationConfig> : return per-field routing rules
- setFieldGenerations(List<AiFieldGenerationConfig>) -> void : set per-field routing rules
- getFactDefinitions() -> List<AiFactDefinition> : return named fact groups
- setFactDefinitions(List<AiFactDefinition>) -> void : set named fact groups
- getLlamaModelPath() -> String : return GGUF fallback path
- setLlamaModelPath(String) -> void : set GGUF fallback path
- getLlamaContextSize() -> int : return fallback context size
- setLlamaContextSize(int) -> void : set fallback context size
- getLlamaMaxOutputTokens() -> int : return fallback max tokens
- setLlamaMaxOutputTokens(int) -> void : set fallback max tokens
- getLlamaTemperature() -> float : return fallback sampling temperature
- setLlamaTemperature(float) -> void : set fallback sampling temperature
- getLlamaThreads() -> int : return fallback CPU threads
- setLlamaThreads(int) -> void : set fallback CPU threads
- getPluginVersion() -> String : return generator version header
- setPluginVersion(String) -> void : set generator version header
- getAiVersion() -> String : return summarisation version header
- setAiVersion(String) -> void : set summarisation version header
- getProjectName() -> String : return aggregate index title
- setProjectName(String) -> void : set aggregate index title
#### Dependencies
- java.io.File, java.util.List, lombok.ToString, org.jspecify.annotations.Nullable
- net.ladenthin.srcmorph.prompt.AiPromptDefinition, net.ladenthin.srcmorph.config.AiGenerationConfig, net.ladenthin.srcmorph.config.AiModelDefinition, net.ladenthin.srcmorph.config.AiFieldGenerationConfig, net.ladenthin.srcmorph.config.AiFactDefinition
#### Exceptions / Errors
- No thrown/caught exceptions; nullable `subtrees`, `excludes`, `fileExtensions`, `promptDefinitions`, `aiDefinitions`, `modelDirectory`, `fieldGenerations`, `factDefinitions`, `llamaModelPath`, `projectName` mean unset/unlimited.
