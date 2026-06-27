### ai
- H: 1.0
- C: EBEF804D
- D: 2026-06-26T14:41:56Z
- T: 2026-06-26T17:38:28Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: package
- F: [main/](main/package.ai.md)
---
> Automates intelligent documentation generation and structured indexing of Maven-based Java projects using AI-powered analysis.

#### Purpose
- Generates AI-driven documentation for Java source files and projects.
- Produces deterministic markdown indexes with metadata for code analysis.

#### Responsibilities
- AI configuration and prompt management: defines model parameters, prompt templates, and generation settings.
- Document processing: reads, writes, and validates .ai.md file formats.
- Source indexing: creates per-file and aggregated package/project AI summaries.
- AI inference: supports local GGUF models and mock backends for code analysis.
- Maven integration: automates documentation within the Maven build lifecycle.

#### Key units
- AiFieldGenerationConfig: maps prompt templates to model definitions for file-specific AI processing.
- AiMdDocument: stores structured metadata and content for AI indexing in .ai.md format.
- AiGenerationProvider: interface for AI text generation with retry logic, implemented by native and mock providers.
- SourceFileIndexer: generates per-file .ai.md documentation using AI prompts and summaries.
- GenerateMojo: Maven plugin goal that indexes Java files and produces AI-enhanced markdown outputs.
- AiPromptSupport: manages prompt templates and renders them into structured inputs for AI models.

#### Data flow
Source code is filtered by extension, then processed by `SourceFileIndexer` to generate `.ai.md` documents. These are aggregated by `PackageIndexer` into package-level indexes, which are consolidated by `ProjectIndexer` into a project-wide summary. AI prompts are prepared using `AiPromptPreparationSupport`, rendered via `AiPromptSupport`, and fed into either `LlamaCppJniAiGenerationProvider` or `MockAiGenerationProvider`. Results are parsed by `AiCompletionParser` to remove internal reasoning before being written back to `.ai.md` files.

#### Dependencies
- Internal modules: config, document, indexer, prompt, provider, support.
- External libraries: Maven plugin API, Lombok annotations, Java 8 compatibility helpers.
- AI inference backend: llama.cpp via JNI bindings for native model execution.

#### Cross-cutting
- Immutable design across core classes ensures thread safety without synchronization.
- Shared use of Lombok annotations reduces boilerplate code.
- Null safety enforced via `@Nullable` and `Objects.requireNonNull`.
- Consistent field ordering and formatting support deterministic behavior in manifests and headers.
- Exception handling wraps low-level I/O, configuration, and AI backend errors in standard Java exceptions.
