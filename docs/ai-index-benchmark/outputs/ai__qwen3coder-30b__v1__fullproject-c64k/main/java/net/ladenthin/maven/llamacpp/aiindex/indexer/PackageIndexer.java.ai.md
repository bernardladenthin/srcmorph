### PackageIndexer.java
- H: 1.0
- C: 1E8F476A
- D: 2026-06-26T14:41:56Z
- T: 2026-06-26T16:13:26Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Aggregates package-level AI index files by walking output directories, collecting child summaries, and generating consolidated package-level AI content.

#### Purpose
- Builds package-level AI documentation from child entries.
- Integrates AI-generated summaries into structured markdown documents.

#### Type
class final PackageIndexer extends Object implements Serializable

#### Input
- Constructor parameters: Log, Path baseDirectory, Path outputRoot, String pluginVersion, String aiVersion, Collection<Path> sourceSubtrees, boolean force, AiGenerationProvider generationProvider, Collection<AiFieldGenerationConfig> fieldGenerations, AiPromptSupport promptSupport, AiModelDefinitionSupport modelDefinitionSupport
- Method parameters: Path rootDirectory in aggregate(), Path directory in writePackageFile(), collectContents(), collectChildLinks(), buildPackageSourceText(), calculatePackageChecksum(), calculatePackageDate()
- Dependencies: AiPathSupport, AiTimeSupport, AiChecksumSupport, AiMdHeaderSupport, AiMdChildEntryLineFormatter, AiMdDocumentCodec, AiMdHeaderCodec, Java8CompatibilityHelper, AiFieldGenerationSupport

#### Output
- Return values: int from aggregate(), aggregateRecursive()
- Side effects: Writes .ai.md files to disk; logs messages via Log instance
- State mutations: Fields updated during processing, such as checksums and dates

#### Core logic
- Traverses output directory tree recursively to find package directories.
- Determines whether a package file should be created or updated based on child content presence.
- Collects contents and child links for header metadata.
- Builds source text by embedding child summaries from .ai.md files.
- Generates AI content using configured field generation strategies.
- Calculates checksums and dates from child entries.
- Writes final markdown documents with headers and bodies.

#### Public API
- aggregate(rootDirectory) -> int: Walks directory tree and aggregates package files.
- writePackageFile(directory) -> void: Writes a single package index file.

#### Dependencies
net.ladenthin.maven.llamacpp.aiindex.config.AiFieldGenerationConfig, net.ladenthin.maven.llamacpp.aiindex.config.AiModelDefinitionSupport, net.ladenthin.maven.llamacpp.aiindex.document.AiGenerationResult, net.ladenthin.maven.llamacpp.aiindex.document.AiMdChildEntryLineFormatter, net.ladenthin.maven.llamacpp.aiindex.document.AiMdDocument, net.ladenthin.maven.llamacpp.aiindex.document.AiMdDocumentCodec, net.ladenthin.maven.llamacpp.aiindex.document.AiMdHeader, net.ladenthin.maven.llamacpp.aiindex.document.AiMdHeaderCodec, net.ladenthin.maven.llamacpp.aiindex.document.AiMdHeaderSupport, net.ladenthin.maven.llamacpp.aiindex.prompt.AiPromptPreparationSupport, net.ladenthin.maven.llamacpp.aiindex.prompt.AiPromptSupport, net.ladenthin.maven.llamacpp.aiindex.provider.AiGenerationProvider, net.ladenthin.maven.llamacpp.aiindex.support.AiChecksumSupport, net.ladenthin.maven.llamacpp.aiindex.support.AiPathSupport, net.ladenthin.maven.llamacpp.aiindex.support.AiTimeSupport, net.ladenthin.maven.llamacpp.aiindex.support.Java8CompatibilityHelper, org.apache.maven.plugin.logging.Log

#### Exceptions / Errors
- IOException: Thrown when reading or writing files fails.
- IllegalStateException: Thrown if Path.getFileName() returns null during sorting.

#### Concurrency
No explicit concurrency mechanisms. All operations are synchronous and assume single-threaded use per instance.
