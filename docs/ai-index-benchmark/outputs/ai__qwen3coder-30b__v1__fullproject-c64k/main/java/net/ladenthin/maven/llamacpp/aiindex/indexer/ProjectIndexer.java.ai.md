### ProjectIndexer.java
- H: 1.0
- C: D7879C38
- D: 2026-06-26T14:41:56Z
- T: 2026-06-26T16:34:59Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Builds a project-level AI index by aggregating package-level indexes into a single navigable markdown file.

#### Purpose
- Aggregates package-level AI indexes into a project-level index.
- Generates optional AI overview paragraph from package leads.

#### Type
Class, final. Implements no interfaces. Uses Lombok @ToString.

#### Input
- Constructor: Log, projectTitle, pluginVersion, aiVersion, force, optional AiGenerationProvider, AiFieldGenerationConfig, AiPromptSupport, AiModelDefinitionSupport.
- Method aggregate: Path rootDirectory.
- Dependencies: AiMdDocumentCodec, AiMdLeadExtractor, AiChecksumSupport, AiTimeSupport, Java8CompatibilityHelper.

#### Output
- Return value: int (1 if written, 0 if unchanged).
- Side effect: Writes project index file at rootDirectory + PROJECT_AI_MD_FILENAME.
- State mutation: None; immutable data structures used.

#### Core logic
- Walks output tree for package.ai.md files.
- Reads each package index to extract lead and link.
- Builds deterministic body with package listing and leads.
- Calculates checksum including overview signature.
- Conditionally generates AI overview from package leads.
- Writes project index file with header and body.

#### Public API
- aggregate(rootDirectory) -> int: Aggregates package indexes into project index.
- ProjectIndexer(log, title, version, force) -> ProjectIndexer: Constructs deterministic indexer.
- ProjectIndexer(log, title, version, force, provider, config, promptSupport, modelSupport) -> ProjectIndexer: Constructs AI-overview indexer.

#### Dependencies
- java.io.IOException, java.nio.file.Files, java.nio.file.Path, java.util.ArrayList, java.util.Collections, java.util.Comparator, java.util.List, java.util.stream.Stream
- lombok.ToString
- net.ladenthin.maven.llamacpp.aiindex.config.AiFieldGenerationConfig
- net.ladenthin.maven.llamacpp.aiindex.config.AiModelDefinitionSupport
- net.ladenthin.maven.llamacpp.aiindex.document.AiGenerationResult
- net.ladenthin.maven.llamacpp.aiindex.document.AiMdDocument
- net.ladenthin.maven.llamacpp.aiindex.document.AiMdDocumentCodec
- net.ladenthin.maven.llamacpp.aiindex.document.AiMdHeader
- net.ladenthin.maven.llamacpp.aiindex.document.AiMdHeaderCodec
- net.ladenthin.maven.llamacpp.aiindex.document.AiMdHeaderSupport
- net.ladenthin.maven.llamacpp.aiindex.document.AiMdLeadExtractor
- net.ladenthin.maven.llamacpp.aiindex.prompt.AiPromptPreparationSupport
- net.ladenthin.maven.llamacpp.aiindex.prompt.AiPromptSupport
- net.ladenthin.maven.llamacpp.aiindex.provider.AiGenerationProvider
- net.ladenthin.maven.llamacpp.aiindex.support.AiChecksumSupport
- net.ladenthin.maven.llamacpp.aiindex.support.AiTimeSupport
- net.ladenthin.maven.llamacpp.aiindex.support.Java8CompatibilityHelper
- org.apache.maven.plugin.logging.Log
- org.jspecify.annotations.Nullable

#### Exceptions / Errors
- Throws IOException when reading/writing files or during AI generation.
- Defensive copy of AiFieldGenerationConfig prevents external mutation.

#### Concurrency
- No concurrency features. Uses immutable data structures and thread-safe helpers.
