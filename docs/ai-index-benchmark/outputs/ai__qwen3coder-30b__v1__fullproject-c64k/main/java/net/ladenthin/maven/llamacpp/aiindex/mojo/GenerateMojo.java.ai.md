### GenerateMojo.java
- H: 1.0
- C: 64782257
- D: 2026-06-26T14:41:56Z
- T: 2026-06-26T16:53:23Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Generates AI-powered summaries and keywords for Java source files within a Maven project.

#### Purpose
- Indexes Java source files.
- Populates AI-generated metadata into indexed files.

#### Type
Class, final. Extends AbstractAiIndexMojo. Implements no interfaces.

#### Input
- Constructor: none.
- Parameters:
  - `skipFile`: boolean, controls skipping file phase.
  - `pluginVersion`: String, Maven plugin version.
  - `aiVersion`: String, AI model version.
  - `fileExtensions`: List<String>, file extensions to index.
  - `excludes`: List<String>, glob patterns for excluded files.
  - `llamaContextSize`: int, context window size for llama.cpp.
  - `llamaThreads`: int, CPU threads for inference.
- Fields:
  - `baseDirectory`: File, project base directory.
  - `outputDirectory`: File, output directory for AI index files.
  - `generationProvider`: String, AI provider identifier.
  - `force`: boolean, whether to overwrite existing files.
  - `fieldGenerations`: List<GenerationField>, fields to generate.
- Resources:
  - File system paths resolved from base and output directories.

#### Output
- Side effects:
  - Writes AI-generated index files to output directory.
  - Logs execution status and warnings.
- Return types:
  - None directly, but mutates state via file writes and logging.

#### Core logic
- Skips execution if global or phase skip flag is set.
- Resolves base path, output path, and file extensions.
- Builds prompt support and AI model definition support.
- Instantiates an AI generation provider using JNI config.
- Creates a source file indexer with resolved parameters.
- Iterates over subtrees to index Java files.
- Logs count of generated AI files upon completion.

#### Public API
- `execute() -> void`: Runs the AI indexing process.
- `getLlamaContextSize() -> int`: Returns configured llama context size.
- `getLlamaThreads() -> int`: Returns configured llama thread count.
- `isPhaseSkipped() -> boolean`: Indicates if file phase is skipped.

#### Dependencies
- java.io.IOException
- java.nio.file.Path
- java.util.List
- lombok.ToString
- net.ladenthin.maven.llamacpp.aiindex.config.AiModelDefinitionSupport
- net.ladenthin.maven.llamacpp.aiindex.indexer.SourceFileIndexer
- net.ladenthin.maven.llamacpp.aiindex.prompt.AiPromptSupport
- net.ladenthin.maven.llamacpp.aiindex.provider.AiGenerationProvider
- net.ladenthin.maven.llamacpp.aiindex.provider.AiGenerationProviderFactory
- net.ladenthin.maven.llamacpp.aiindex.support.Java8CompatibilityHelper
- org.apache.maven.plugin.MojoExecutionException
- org.apache.maven.plugins.annotations.Mojo
- org.apache.maven.plugins.annotations.Parameter

#### Exceptions / Errors
- Throws MojoExecutionException on IOException during file operations.
- Logs warnings for missing subtrees.

#### Concurrency
- Thread-safe due to `@Mojo(threadSafe = true)`.
