### main/java/net/ladenthin/maven/llamacpp/aiindex/mojo
- H: 1.0
- C: A7DF4B16
- D: 2026-06-26T14:41:56Z
- T: 2026-06-26T17:19:42Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: package
- F: [AbstractAiIndexMojo.java](AbstractAiIndexMojo.java.ai.md)
- F: [AggregatePackagesMojo.java](AggregatePackagesMojo.java.ai.md)
- F: [AggregateProjectMojo.java](AggregateProjectMojo.java.ai.md)
- F: [GenerateMojo.java](GenerateMojo.java.ai.md)
- F: [package-info.java](package-info.java.ai.md)
---
> Automates the generation of AI-powered documentation and code summaries from Java source files within Maven projects, supporting per-file, per-package, and project-level indexing.

#### Purpose
- Generates AI-enhanced metadata and summaries for Java source code.
- Integrates with Maven build lifecycle to produce structured documentation artifacts.

#### Responsibilities
- **File-level indexing**: Processes individual Java files to extract and augment code information using AI.
- **Package-level aggregation**: Combines per-file AI indexes into consolidated package summaries.
- **Project-level synthesis**: Creates a unified project overview from aggregated package indexes, optionally including an AI-generated summary paragraph.
- **Configuration management**: Centralizes AI model parameters and prompt definitions for consistent usage across indexing phases.

#### Key units
- `AbstractAiIndexMojo`: Base class providing shared configuration and utility methods for all AI index Mojos.
- `GenerateMojo`: Indexes individual Java files using AI to produce per-file `.ai.md` outputs.
- `AggregatePackagesMojo`: Aggregates package-level AI indexes into a structured summary.
- `AggregateProjectMojo`: Consolidates project-wide AI indexes into a single, optionally summarized index file.
- `LlamaCppJniConfig`: Configuration object for llama.cpp inference settings.
- `AiPromptSupport`: Manages prompt templates used in AI generation.
- `AiModelDefinitionSupport`: Encapsulates model configurations for AI providers.

#### Data flow
- Input source files are resolved from project base directory and filtered by extensions and exclusions.
- AI models are instantiated with configured parameters (e.g., context size, threads) via `LlamaCppJniConfig`.
- Prompt and model definitions are built from lists of configurations, used during generation steps.
- Generated content is written to output directories as `.ai.md` files, organized per file, package, or project scope.

#### Dependencies
- Internal: `net.ladenthin.maven.llamacpp.aiindex.config`, `net.ladenthin.maven.llamacpp.aiindex.indexer`, `net.ladenthin.maven.llamacpp.aiindex.prompt`, `net.ladenthin.maven.llamacpp.aiindex.provider`
- External: Maven plugin API (`org.apache.maven.plugin`), Lombok annotations, Java NIO and collections

#### Cross-cutting
- All Mojos extend `AbstractAiIndexMojo` for shared behavior.
- Uses consistent logging via `getLog()`.
- Configurable skip flags control execution at both global and phase levels.
- Thread safety ensured by `@Mojo(threadSafe = true)` on final classes.
- Exception handling wraps low-level I/O and configuration errors in `MojoExecutionException`.
