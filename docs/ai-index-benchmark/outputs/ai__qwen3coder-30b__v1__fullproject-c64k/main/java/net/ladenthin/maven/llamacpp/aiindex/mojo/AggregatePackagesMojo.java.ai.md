### AggregatePackagesMojo.java
- H: 1.0
- C: 6A34E4E4
- D: 2026-06-26T14:41:56Z
- T: 2026-06-26T16:49:44Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Aggregates per-package AI-generated index files into a unified summary and keyword structure.

#### Purpose
- Builds aggregated package index files from individual .ai.md files.
- Configures AI model parameters for fast inference during package processing.

#### Type
Class, final. Extends AbstractAiIndexMojo. Implements no interfaces.

#### Input
- Constructor: no parameters.
- Parameters:
  - skipPackage: boolean flag to conditionally skip phase.
  - pluginVersion: Maven project version string.
  - aiVersion: AI model version string.
  - llamaContextSize: integer for context window size.
  - llamaThreads: integer for CPU threads used during inference.
- Fields read:
  - baseDirectory, outputDirectory, force, generationProvider, fieldGenerations.

#### Output
- Side effects: logs informational messages.
- Return type: void (execute method).
- Mutated fields: none directly; indirectly affects package index file content.

#### Core logic
- Checks if execution should be skipped based on global or phase-specific flags.
- Resolves subtree paths from base directory.
- Builds prompt and model definition support objects.
- Instantiates AI generation provider with configured llama.cpp JNI settings.
- Creates PackageIndexer with all necessary parameters including provider.
- Executes aggregation of package index files into output directory.
- Logs results and execution summary.

#### Public API
- execute() -> void: runs the main aggregation logic.
- getLlamaContextSize() -> int: returns configured context window size.
- getLlamaThreads() -> int: returns configured thread count for inference.
- isPhaseSkipped() -> boolean: checks if current phase is skipped.

#### Dependencies
- java.io.IOException
- java.nio.file.Path
- java.util.Collections
- java.util.List
- lombok.ToString
- net.ladenthin.maven.llamacpp.aiindex.config.AiModelDefinitionSupport
- net.ladenthin.maven.llamacpp.aiindex.indexer.PackageIndexer
- net.ladenthin.maven.llamacpp.aiindex.prompt.AiPromptSupport
- net.ladenthin.maven.llamacpp.aiindex.provider.AiGenerationProvider
- net.ladenthin.maven.llamacpp.aiindex.provider.AiGenerationProviderFactory
- org.apache.maven.plugin.MojoExecutionException
- org.apache.maven.plugins.annotations.Mojo
- org.apache.maven.plugins.annotations.Parameter

#### Exceptions / Errors
- IOException: thrown when output directory does not exist or cannot be accessed.
- MojoExecutionException: wraps IOExceptions during execution.

#### Concurrency
- Thread-safe due to @Mojo annotation with threadSafe = true.
- Uses try-with-resources for AiGenerationProvider to ensure proper cleanup.
