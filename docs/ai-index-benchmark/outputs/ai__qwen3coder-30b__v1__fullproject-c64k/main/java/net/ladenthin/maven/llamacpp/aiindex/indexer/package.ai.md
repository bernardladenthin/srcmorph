### main/java/net/ladenthin/maven/llamacpp/aiindex/indexer
- H: 1.0
- C: 368B3C1F
- D: 2026-06-26T14:41:56Z
- T: 2026-06-26T17:16:59Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: package
- F: [AiFieldGenerationSupport.java](AiFieldGenerationSupport.java.ai.md)
- F: [PackageIndexer.java](PackageIndexer.java.ai.md)
- F: [ProjectIndexer.java](ProjectIndexer.java.ai.md)
- F: [SourceFileIndexer.java](SourceFileIndexer.java.ai.md)
- F: [package-info.java](package-info.java.ai.md)
---
> Automates AI-powered documentation and indexing of Java source code packages, files, and projects by generating structured markdown summaries with field-level metadata.

#### Purpose
- Generates AI-driven documentation for Java source files, packages, and projects.
- Consolidates AI outputs into navigable, hierarchical index structures.

#### Responsibilities
- Source file indexing: processes individual Java files to create AI-powered metadata documents.
- Package aggregation: builds package-level AI indexes from child entries and field generations.
- Project consolidation: aggregates package indexes into a project-wide navigable markdown file.
- Field generation: applies AI prompts to extract structured information from source code.

#### Key units
- **AiFieldGenerationSupport**: Processes AI field generation requests, prepares prompts, handles retries.
- **PackageIndexer**: Builds package-level AI documentation by collecting child summaries and generating consolidated content.
- **ProjectIndexer**: Aggregates package-level indexes into a project-level index with optional AI overview.
- **SourceFileIndexer**: Creates structured markdown index files for Java source files with AI-generated summaries.

#### Data flow
- Source code files are walked and filtered, then processed by `SourceFileIndexer` to generate `.ai.md` documents.
- Package directories are scanned by `PackageIndexer` to collect child content and build consolidated package indexes.
- `ProjectIndexer` aggregates all package-level `.ai.md` files into a project-level index file.
- AI field generation workflows use `AiFieldGenerationSupport` to process configurations, prepare prompts, and retry failed generations.

#### Dependencies
- Internal modules: AiMdDocument, AiMdHeader, AiGenerationProvider, AiPromptPreparationSupport, AiModelDefinitionSupport.
- External libraries: Lombok, Maven plugin logging, Java 8 compatibility helpers.
- Configuration types: AiFieldGenerationConfig, AiGenerationConfig, AiPromptSupport.

#### Cross-cutting
- Shared interfaces and patterns for AI generation and prompt preparation across components.
- Common exception handling with IOException, IllegalArgumentException, and retry logic for blank outputs.
- Thread-safe caching for max input character calculations in `AiFieldGenerationSupport`.
- Immutable data structures used in `ProjectIndexer` to ensure deterministic output.
- Use of `@ToString` and Lombok annotations for code clarity.
