### SrcMorphConfiguration.java
- H: 1.0
- C: 1D938DBE
- D: 2026-10-31T09:32:09Z
- T: 2026-10-31T09:34:38Z
- G: 
- A: 0.0.0
- X: file
---
> Holds every parameter of an srcmorph AI documentation run as a mutable JavaBean shared across Maven, JSON/YAML, and plain-Java configuration surfaces.

#### Purpose
- Single shared configuration object for an srcmorph run: source selection, file-size filters, AI provider/model routing, llama.cpp fallback settings, and output metadata.
- Field names mirror Maven `@Parameter` names so YAML/JSON keys match plugin XML; per-goal skip flags are deliberately excluded (mojo-side lifecycle concern).

#### Type
- Class, public, not final, not abstract; mutable JavaBean with getters and setters.
- Annotations: `@ToString` (Lombok); `@SuppressWarnings({"NullAway.Init", "initialization.fields.uninitialized"})`.
- Nullability via `org.jspecify.annotations.Nullable` on fields, getters, setters.

#### Input
- No-arg constructor only; all state set via setters or field defaults.
- Consumed by binding technologies: Maven plexus reflection, Jackson ObjectMapper/YAMLMapper, or direct caller.
- Config properties: baseDirectory (File), outputDirectory (File), force (boolean), subtrees, excludes, fileExtensions (List of String, nullable), minFileSizeBytes, maxFileSizeBytes (long), planOnly (boolean), generationProvider (String), promptDefinitions (List of AiPromptDefinition), aiDefinitions (List of AiModelDefinition), modelDirectory (File), fieldGenerations (List of AiFieldGenerationConfig), factDefinitions (List of AiFactDefinition), llamaModelPath (String), llamaContextSize, llamaMaxOutputTokens (int), llamaTemperature (float), llamaThreads (int), pluginVersion, aiVersion (String), projectName (String, nullable).

#### Output
- Getter results and setter-populated state; no I/O, no side effects.
- Defaults applied at field initialization: outputDirectory = File("src/site/ai"), generationProvider = "mock", llamaContextSize = 2048, llamaMaxOutputTokens = 128, llamaTemperature = 0.15f, llamaThreads = AiGenerationConfig.DEFAULT_THREADS, aiVersion = "0.0.0", pluginVersion = "".

#### Core logic
- Acts as passive data holder; no business logic in this file.
- Path resolution contract (documented): relative aiDefinitions/llamaModelPath paths resolve against modelDirectory; absolute paths used as-is; null modelDirectory resolves against JVM working directory.
- Routing contract (documented): fieldGenerations non-empty takes precedence; llama* settings act as fallback only when fieldGenerations is empty.
- Provider contract: generationProvider in {mock, llamacpp-jni, openai-compatible}; a model definition's own provider overrides it.
- File-size contract: minFileSizeBytes exclusive lower bound, <= 0 disables; maxFileSizeBytes inclusive upper bound, <= 0 unlimited.
- Null subtrees means all discovered source roots in scope; null excludes means none excluded; null fileExtensions defers to engine default (typically .java).
- planOnly stops a generate run after routing plan printout.
- Public constants: 7 defaults (DEFAULT_OUTPUT_DIRECTORY, DEFAULT_GENERATION_PROVIDER, DEFAULT_LLAMA_CONTEXT_SIZE, DEFAULT_LLAMA_MAX_OUTPUT_TOKENS, DEFAULT_LLAMA_TEMPERATURE, DEFAULT_LLAMA_THREADS, DEFAULT_AI_VERSION).

#### Public API
- SrcMorphConfiguration() -> constructor, applies defaults
- getBaseDirectory() -> File; project base directory
- setBaseDirectory(File) -> void; sets base directory
- getOutputDirectory() -> File; output dir for .ai.md files
- setOutputDirectory(File) -> void; sets output dir
- isForce() -> boolean; regenerate even when populated
- setForce(boolean) -> void; sets force flag
- getSubtrees() -> List<String>; nullable subtree restriction
- setSubtrees(List<String>) -> void; sets subtrees
- getExcludes() -> List<String>; nullable exclude globs
- setExcludes(List<String>) -> void; sets exclude globs
- getFileExtensions() -> List<String>; nullable extensions to index
- setFileExtensions(List<String>) -> void; sets extensions
- getMinFileSizeBytes() -> long; exclusive lower size bound
- setMinFileSizeBytes(long) -> void; sets lower bound
- getMaxFileSizeBytes() -> long; inclusive upper size bound
- setMaxFileSizeBytes(long) -> void; sets upper bound
- isPlanOnly() -> boolean; stop after routing plan
- setPlanOnly(boolean) -> void; sets plan-only flag
- getGenerationProvider() -> String; provider name
- setGenerationProvider(String) -> void; sets provider name
- getPromptDefinitions() -> List<AiPromptDefinition>; nullable prompt templates
- setPromptDefinitions(List<AiPromptDefinition>) -> void; sets prompt templates
- getAiDefinitions() -> List<AiModelDefinition>; nullable model definitions
- setAiDefinitions(List<AiModelDefinition>) -> void; sets model definitions
- getModelDirectory() -> File; nullable model path base
- setModelDirectory(File) -> void; sets model path base
- getFieldGenerations() -> List<AiFieldGenerationConfig>; nullable routing rules
- setFieldGenerations(List<AiFieldGenerationConfig>) -> void; sets routing rules
- getFactDefinitions() -> List<AiFactDefinition>; nullable reusable fact groups
- setFactDefinitions(List<AiFactDefinition>) -> void; sets fact groups
- getLlamaModelPath() -> String; nullable GGUF model path
- setLlamaModelPath(String) -> void; sets GGUF model path
- getLlamaContextSize() -> int; fallback context window size
- setLlamaContextSize(int) -> void; sets context window size
- getLlamaMaxOutputTokens() -> int; fallback max output tokens
- setLlamaMaxOutputTokens(int) -> void; sets max output tokens
- getLlamaTemperature() -> float; fallback sampling temperature
- setLlamaTemperature(float) -> void; sets sampling temperature
- getLlamaThreads() -> int; fallback CPU thread count
- setLlamaThreads(int) -> void; sets CPU thread count
- getPluginVersion() -> String; generator version for headers
- setPluginVersion(String) -> void; sets generator version
- getAiVersion() -> String; AI logic version for headers
- setAiVersion(String) -> void; sets AI logic version
- getProjectName() -> String; nullable aggregate index title
- setProjectName(String) -> void; sets project title

#### Dependencies
- java.io.File
- java.util.List
- lombok.ToString
- net.ladenthin.srcmorph.prompt.AiPromptDefinition
- org.jspecify.annotations.Nullable
- Referenced in same package or elsewhere: AiGenerationConfig (DEFAULT_THREADS), AiModelDefinition, AiFieldGenerationConfig, AiFactDefinition

#### Exceptions / Errors
- No exceptions thrown or caught.
- No null checks; nullable fields documented as null-permitted, with null meaning "not configured" or engine default.
- Primitive fields cannot be null; numeric bounds <= 0 are sentinel values.

#### Concurrency
- Not thread-safe; mutable fields with plain setters and no synchronization.
- No volatile or immutable copies; List references are stored as given.
