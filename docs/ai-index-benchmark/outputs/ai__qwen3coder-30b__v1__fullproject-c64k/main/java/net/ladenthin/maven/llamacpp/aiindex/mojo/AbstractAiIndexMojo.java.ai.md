### AbstractAiIndexMojo.java
- H: 1.0
- C: B294B09C
- D: 2026-06-26T14:41:56Z
- T: 2026-06-26T16:44:39Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Provides shared configuration and utility methods for Maven Mojo plugins that generate AI-powered documentation and summaries from source code.

#### Purpose
- Centralizes common AI index parameters and logic across multiple Maven goals.
- Supplies helper methods for resolving paths, building configurations, and logging execution details.

#### Type
- Abstract class extending `org.apache.maven.plugin.AbstractMojo`
- Implements no interfaces
- Uses Lombok `@ToString` annotation

#### Input
- Constructor: none
- Fields:
  - `baseDirectory`: injected by Maven (`${project.basedir}`)
  - `outputDirectory`: configurable property with default `${project.basedir}/src/site/ai`
  - `skip`: global toggle flag
  - `force`: regeneration control flag
  - `subtrees`: list of relative paths to restrict processing
  - `generationProvider`: string identifier for AI provider (e.g., "mock", "llamacpp-jni")
  - `promptDefinitions`: list of prompt templates used by field generation
  - `aiDefinitions`: list of model configurations referenced by field generations
  - `fieldGenerations`: list of per-field AI configuration entries
  - `llamaLibraryPath`, `llamaModelPath`, `llamaMaxOutputTokens`, `llamaTemperature`: llama.cpp specific parameters
- Methods:
  - `resolveSubtrees(Path basePath)` consumes `subtrees` and `baseDirectory`
  - `buildLlamaCppJniConfig()` uses `fieldGenerations`, `aiDefinitions`, and individual llama parameters
  - `buildPromptSupport()` uses `promptDefinitions`
  - `buildAiModelDefinitionSupport()` uses `aiDefinitions`
  - `logExecutionParameters(...)` consumes various execution parameters

#### Output
- Return types:
  - `List<Path>` from `resolveSubtrees()`
  - `int` from `sizeOf()`
  - `LlamaCppJniConfig` from `buildLlamaCppJniConfig()`
  - `AiPromptSupport` from `buildPromptSupport()`
  - `AiModelDefinitionSupport` from `buildAiModelDefinitionSupport()`
- Side effects:
  - Logs execution parameters using `getLog().info()`
  - Warns about missing subtrees via `getLog().warn()`

#### Core logic
- Determines whether to skip execution based on global or phase-specific skip flags.
- Resolves configured subtree paths against the project base directory, filtering out non-existent paths.
- Builds a LlamaCppJniConfig using either field-generation-driven definitions or fallback parameters.
- Constructs prompt and AI model definition support instances, handling missing required fields via exceptions.
- Logs standardized execution parameter set for debugging.

#### Public API
- `getLlamaContextSize() -> int` declares context size for llama.cpp
- `getLlamaThreads() -> int` declares thread count for llama.cpp inference
- `isPhaseSkipped() -> boolean` returns phase-specific skip flag
- `shouldSkip() -> boolean` evaluates overall skip condition
- `resolveSubtrees(Path basePath) -> List<Path>` resolves configured subtrees
- `sizeOf(Collection<?> collection) -> int` counts elements safely
- `buildLlamaCppJniConfig() -> LlamaCppJniConfig` constructs llama config
- `buildPromptSupport() -> AiPromptSupport` builds prompt support instance
- `buildAiModelDefinitionSupport() -> AiModelDefinitionSupport` builds model definition support
- `logExecutionParameters(...) -> void` logs execution parameters

#### Dependencies
- `java.io.File`
- `java.nio.file.Path`
- `java.util.ArrayList`
- `java.util.Collection`
- `java.util.Collections`
- `java.util.List`
- `lombok.ToString`
- `net.ladenthin.maven.llamacpp.aiindex.config.AiFieldGenerationConfig`
- `net.ladenthin.maven.llamacpp.aiindex.config.AiGenerationConfig`
- `net.ladenthin.maven.llamacpp.aiindex.config.AiModelDefinition`
- `net.ladenthin.maven.llamacpp.aiindex.config.AiModelDefinitionSupport`
- `net.ladenthin.maven.llamacpp.aiindex.prompt.AiPromptDefinition`
- `net.ladenthin.maven.llamacpp.aiindex.prompt.AiPromptSupport`
- `net.ladenthin.maven.llamacpp.aiindex.provider.LlamaCppJniConfig`
- `org.apache.maven.plugin.AbstractMojo`
- `org.apache.maven.plugin.MojoExecutionException`
- `org.apache.maven.plugins.annotations.Parameter`

#### Exceptions / Errors
- Throws `MojoExecutionException` when prompt or AI definition lists contain missing required fields
- Throws `IllegalArgumentException` in `buildLlamaCppJniConfig()` if referenced model definition key is invalid

#### Concurrency
- No explicit concurrency handling; relies on Maven's per-execution instantiation model
- Thread-safe methods assume immutable inputs and do not mutate shared state
