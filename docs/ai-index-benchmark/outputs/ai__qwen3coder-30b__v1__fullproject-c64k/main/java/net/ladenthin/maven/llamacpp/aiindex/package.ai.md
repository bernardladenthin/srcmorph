### main/java/net/ladenthin/maven/llamacpp/aiindex
- H: 1.0
- C: E5A0A8E1
- D: 2026-06-26T14:41:56Z
- T: 2026-06-26T17:28:10Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: package
- F: [config/](config/package.ai.md)
- F: [document/](document/package.ai.md)
- F: [indexer/](indexer/package.ai.md)
- F: [mojo/](mojo/package.ai.md)
- F: [package-info.java](package-info.java.ai.md)
- F: [prompt/](prompt/package.ai.md)
- F: [provider/](provider/package.ai.md)
- F: [support/](support/package.ai.md)
---
> This package automates AI-powered documentation and indexing of Java source code, generating structured markdown summaries with field-level metadata for Maven projects.

#### Purpose
- Generates AI-driven documentation for Java source files, packages, and projects.
- Produces structured markdown index files with deterministic metadata and content.

#### Responsibilities
- AI configuration and prompt management: defines model parameters, prompt templates, and generation settings.
- Document processing: reads, writes, and validates .ai.md file formats with metadata headers.
- Source indexing: creates AI-powered summaries for individual Java files and aggregates them into package and project indexes.
- AI inference and generation: supports local GGUF models and mock backends for code analysis and field extraction.
- Maven integration: automates documentation generation within the Maven build lifecycle.

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
