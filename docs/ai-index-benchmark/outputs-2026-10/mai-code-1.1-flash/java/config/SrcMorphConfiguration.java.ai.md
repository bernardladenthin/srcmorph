### SrcMorphConfiguration.java
- H: 1.0
- C: 1D938DBE
- D: 2026-10-29T23:43:20Z
- T: 2026-10-29T23:44:14Z
- G: 
- A: 0.0.0
- X: file
---
> Root mutable JavaBean that carries the configuration for a srcmorph generation run, from project scanning to model selection and output routing.

#### Purpose
- Stores every runtime setting for a `srcmorph` run in one shared configuration object.
- Supports Maven-style binding, future config-file binding, and direct Java construction via JavaBean setters.

#### Type
- `class SrcMorphConfiguration`
- `public`, mutable, not `abstract`, `final`, or `sealed`
- No `extends` or `implements`
- Annotated with `@ToString` and `@SuppressWarnings({"NullAway.Init", "initialization.fields.uninitialized"})`

#### Input
- Constructor: `SrcMorphConfiguration()`
- Bean-style inputs via setters:
  - `File baseDirectory`
  - `File outputDirectory`
  - `boolean force`
  - `@Nullable List<String> subtrees`
  - `@Nullable List<String> excludes`
  - `@Nullable List<String> fileExtensions`
  - `long minFileSizeBytes`
  - `long maxFileSizeBytes`
  - `boolean planOnly`
  - `String generationProvider`
  - `@Nullable List<AiPromptDefinition> promptDefinitions`
  - `@Nullable List<AiModelDefinition> aiDefinitions`
  - `@Nullable File modelDirectory`
  - `@Nullable List<AiFieldGenerationConfig> fieldGenerations`
  - `@Nullable List<AiFactDefinition> factDefinitions`
  - `@Nullable String llamaModelPath`
  - `int llamaContextSize`
  - `int llamaMaxOutputTokens`
  - `float llamaTemperature`
  - `int llamaThreads`
  - `String pluginVersion`
  - `String aiVersion`
  - `@Nullable String projectName`
- Default constants read at startup: `DEFAULT_OUTPUT_DIRECTORY`, `DEFAULT_GENERATION_PROVIDER`, `DEFAULT_LLAMA_CONTEXT_SIZE`, `DEFAULT_LLAMA_MAX_OUTPUT_TOKENS`, `DEFAULT_LLAMA_TEMPERATURE`, `DEFAULT_LLAMA_THREADS`, `DEFAULT_AI_VERSION`

#### Output
- Mutates instance fields to hold runtime state.
- Exposes configuration via getters returning `File`, `boolean`, `long`, `int`, `float`, `String`, and `@Nullable List<...>`.
- No file writes, network calls, or external side effects; output is the configured object state itself.

#### Core logic
- Defines a single mutable JavaBean for all `srcmorph` run parameters.
- Applies default values for generation provider, output directory, LLaMA settings, and AI version.
- Groups configuration into project scope, file filtering, model routing, prompt templates, fact definitions, and fallback llama.cpp settings.
- Keeps the object binding-friendly for Maven Plexus reflection, JSON/YAML configuration, or direct Java construction.
- Uses nullable fields to represent optional configuration such as subtrees, exclusions, model definitions, and project title.

#### Public API
- `SrcMorphConfiguration() -> SrcMorphConfiguration`
- `getBaseDirectory..setProjectName() (46 total)`

#### Dependencies
- `java.io.File`
- `java.util.List`
- `lombok.ToString`
- `org.jspecify.annotations.Nullable`
- `net.ladenthin.srcmorph.prompt.AiPromptDefinition`
- `AiGenerationConfig`
- `AiModelDefinition`
- `AiFieldGenerationConfig`
- `AiFactDefinition`
- `SrcMorphConfiguration`

#### Exceptions / Errors
- No checked exceptions declared.
- No explicit catches or throws.
- Nullability is expressed via `@Nullable` on optional fields and setter parameters; no null guards are implemented here.
- The class does not validate values beyond default initialization and setter assignment.

#### Concurrency
- Mutable bean state; no synchronization or immutability.
- Not thread-safe by design; intended for configuration binding and single-run usage.
- The class is a shared config container, not a concurrent service or cache.
