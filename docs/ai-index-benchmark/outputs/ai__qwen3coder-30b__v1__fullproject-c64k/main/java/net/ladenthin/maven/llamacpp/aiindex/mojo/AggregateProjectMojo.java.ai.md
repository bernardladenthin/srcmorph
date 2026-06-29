### AggregateProjectMojo.java
- H: 1.0
- C: CB445C6F
- D: 2026-06-26T14:41:56Z
- T: 2026-06-26T16:51:13Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Aggregates per-package AI index files into a single project-level index, optionally including an AI-generated overview paragraph.

#### Purpose
- Generates a consolidated AI index for a Maven project.
- Optionally produces an AI summary of package-level indexes.

#### Type
Class `AggregateProjectMojo` extends `AbstractAiIndexMojo`, final. Implements no interfaces. Uses Lombok `@ToString`.

#### Input
- Constructor: none.
- Parameters from Maven: `outputDirectory`, `force`, `skip`, `projectName`, `pluginVersion`, `aiVersion`, `llamaContextSize`, `llamaThreads`, `fieldGenerations`.
- Dependencies injected by Maven: `getLog()`, `generationProvider`, `buildLlamaCppJniConfig()`, `buildPromptSupport()`, `buildAiModelDefinitionSupport()`.

#### Output
- Writes one or more `.ai.md` files to `outputDirectory`.
- Logs informational and error messages via `getLog()`.

#### Core logic
- Skips execution if global or phase-specific skip flag is set.
- Determines project title from `projectName` or defaults to "project".
- Chooses deterministic aggregation path if no `fieldGenerations` configured; otherwise, creates AI provider and executes AI-driven summary.
- Calls `ProjectIndexer.aggregate()` for either path.

#### Public API
- `execute() -> void` : Runs the index aggregation process.
- `getLlamaContextSize() -> int` : Returns context size for llama.cpp.
- `getLlamaThreads() -> int` : Returns thread count for llama.cpp.
- `isPhaseSkipped() -> boolean` : Checks if project phase is skipped.

#### Dependencies
Imports: `java.io.IOException`, `java.nio.file.Path`, `lombok.ToString`, `net.ladenthin.maven.llamacpp.aiindex.config.AiFieldGenerationConfig`, `net.ladenthin.maven.llamacpp.aiindex.config.AiModelDefinitionSupport`, `net.ladenthin.maven.llamacpp.aiindex.indexer.ProjectIndexer`, `net.ladenthin.maven.llamacpp.aiindex.prompt.AiPromptSupport`, `net.ladenthin.maven.llamacpp.aiindex.provider.AiGenerationProvider`, `net.ladenthin.maven.llamacpp.aiindex.provider.AiGenerationProviderFactory`, `net.ladenthin.maven.llamacpp.aiindex.support.Java8CompatibilityHelper`, `org.apache.maven.plugin.MojoExecutionException`, `org.apache.maven.plugins.annotations.Mojo`, `org.apache.maven.plugins.annotations.Parameter`.

#### Exceptions / Errors
- Throws `MojoExecutionException` when provider/model setup fails or I/O error occurs.
- Handles null or blank `projectName` with default title.
- Catches and rethrows `IOException` during index writing.

#### Concurrency
- Marked `@Mojo(threadSafe = true)`.
- Uses immutable fields; no shared mutable state.
