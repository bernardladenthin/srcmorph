### SrcMorphConfiguration.java
- H: 1.0
- C: 1D938DBE
- D: 2026-10-31T10:06:17Z
- T: 2026-10-31T10:24:55Z
- G: 
- A: 0.0.0
- X: file
---
> <one-sentence domain essence>
> A mutable configuration object gathering all parameters required for a srcmorph run, usable by Maven plugins, JSON/YAML configs, or direct Java construction.

#### Purpose
- Holds runtime configuration for the srcmorph tool/engine.
- Provides getters/setters for all configurable fields (directory paths, AI provider settings, filtering rules, etc.).
- Serves as a shared config surface bound from multiple sources (Maven, CLI, programmatic).

#### Type
- Class `SrcMorphConfiguration` in package `net.ladenthin.srcmorph.config`.
- Not abstract, not final, not sealed.
- Uses Lombok `@ToString`; includes `@SuppressWarnings`.
- No inheritance; no explicit implements.
- No generics.

#### Input
- Constructor receives no parameters (no-op).
- Setter methods receive values for each field; typical inputs are `File`, `List<?>`, `String`, primitives (`int`, `long`, `float`, `boolean`).
- Fields may be `null` for optional lists (`subtrees`, `excludes`, `fileExtensions`, `promptDefinitions`, `aiDefinitions`, `modelDirectory`, `fieldGenerations`, `factDefinitions`, `llamaModelPath`, `projectName`).

#### Output
- Getter methods return the current value of each field.
- Return types: `File`, `boolean`, `long`, `int`, `float`, `String`, `List<?>` (nullable), `@Nullable String`.
- Mutates internal state via setters; no other output side effects.

#### Core logic
- Stores default values in static final fields (`DEFAULT_OUTPUT_DIRECTORY`, `DEFAULT_GENERATION_PROVIDER`, etc.).
- Initializes `outputDirectory` with `DEFAULT_OUTPUT_DIRECTORY` and `llamaThreads` with `DEFAULT_LLAMA_THREADS` (from `AiGenerationConfig`).
- Provides no computation or validation logic in the class body; all logic resides in field storage and accessor methods.
- No loops, conditionals, or algorithmic processing; purely a data holder with JavaBean style.

#### Public API
- `getBaseDirectory() -> File` – returns base directory.
- `setBaseDirectory(File) -> void` – sets base directory.
- `getOutputDirectory() -> File` – returns output directory.
- `setOutputDirectory(File) -> void` – sets output directory.
- `isForce() -> boolean` – returns force flag.
- `setForce(boolean) -> void` – sets force flag.
- `getSubtrees() -> @Nullable List<String>` – returns subtrees list.
- `setSubtrees(@Nullable List<String>) -> void` – sets subtrees list.
- `getExcludes() -> @Nullable List<String>` – returns excludes list.
- `setExcludes(@Nullable List<String>) -> void` – sets excludes list.
- `getFileExtensions() -> @Nullable List<String>` – returns file extensions list.
- `setFileExtensions(@Nullable List<String>) -> void` – sets extensions list.
- `getMinFileSizeBytes() -> long` – returns min file size.
- `setMinFileSizeBytes(long) -> void` – sets min file size.
- `getMaxFileSizeBytes() -> long` – returns max file size.
- `setMaxFileSizeBytes(long) -> void` – sets max file size.
- `isPlanOnly() -> boolean` – returns plan-only flag.
- `setPlanOnly(boolean) -> void` – sets plan-only flag.
- `getGenerationProvider() -> String` – returns generation provider name.
- `setGenerationProvider(String) -> void` – sets provider name.
- `getPromptDefinitions() -> @Nullable List<AiPromptDefinition>` – returns prompt definitions.
- `setPromptDefinitions(@Nullable List<AiPromptDefinition>) -> void` – sets prompt definitions.
- `getAiDefinitions() -> @Nullable List<AiModelDefinition>` – returns AI model definitions.
- `setAiDefinitions(@Nullable List<AiModelDefinition>) -> void` – sets AI definitions.
- `getModelDirectory() -> @Nullable File` – returns model directory.
- `setModelDirectory(@Nullable File) -> void` – sets model directory.
- `getFieldGenerations() -> @Nullable List<AiFieldGenerationConfig>` – returns field generation rules.
- `setFieldGenerations(@Nullable List<AiFieldGenerationConfig>) -> void` – sets field rules.
- `getFactDefinitions() -> @Nullable List<AiFactDefinition>` – returns fact definitions.
- `setFactDefinitions(@Nullable List<AiFactDefinition>) -> void` – sets fact definitions.
- `getLlamaModelPath() -> @Nullable String` – returns llama model path.
- `setLlamaModelPath(@Nullable String) -> void` – sets llama model path.
- `getLlamaContextSize() -> int` – returns llama context size.
- `setLlamaContextSize(int) -> void` – sets context size.
- `getLlamaMaxOutputTokens() -> int` – returns max output tokens.
- `setLlamaMaxOutputTokens(int) -> void` – sets max tokens.
- `getLlamaTemperature() -> float` – returns temperature.
- `setLlamaTemperature(float) -> void` – sets temperature.
- `getLlamaThreads() -> int` – returns thread count.
- `setLlamaThreads(int) -> void` – sets thread count.
- `getPluginVersion() -> String` – returns plugin version.
- `setPluginVersion(String) -> void` – sets plugin version.
- `getAiVersion() -> String` – returns AI version.
- `setAiVersion(String) -> void` – sets AI version.
- `getProjectName() -> @Nullable String` – returns project name.
- `setProjectName(@Nullable String) -> void` – sets project name.

> Total public API members: 30 getters and 30 setters (15 property pairs). Emit as `getBaseDirectory( ) -> File`, etc., but in the answer we only state the exact total and index range: `getBaseDirectory..setProjectName (30 total)`.

#### Dependencies
- `java.io.File`
- `java.util.List`
- `net.ladenthin.srcmorph.prompt.AiPromptDefinition`
- `net.ladenthin.srcmorph.config.AiGenerationConfig` (referenced via `DEFAULT_THREADS`)
- `net.ladenthin.srcmorph.config.AiModelDefinition`
- `net.ladenthin.srcmorph.config.AiFieldGenerationConfig`
- `net.ladenthin.srcmorph.config.AiFactDefinition`
- `org.jspecify.annotations.Nullable`
- `lombok.ToString`

#### Exceptions / Errors
- No checked exceptions thrown by any method.
- Null handling: optional lists and strings are annotated with `@Nullable`; getters return `@Nullable` types; setters accept `@Nullable` parameters. No null-dereference enforcement in this class (Lombok `@ToString` and `@SuppressWarnings` allow it).
- Setter methods accept `null` for nullable fields; no validation performed (e.g., `minFileSizeBytes <= 0` is documented but not enforced).

#### Concurrency
- No synchronization, thread-locks, or volatile fields.
- Fields are mutable; class is not immutable.
- No concurrency guarantees stated; assumes external code handles thread safety if accessed from multiple threads.

> Note: The class is designed to be populated by various binders (Maven reflection, Jackson, direct Java), hence mutability is intentional. No internal state is shared across threads within the class.
