### SourceFileIndexer.java
- H: 1.0
- C: 2126461D
- D: 2026-06-26T14:41:56Z
- T: 2026-06-26T16:41:20Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Generates AI-powered metadata index files for source code, summarizing content and extracting keywords per file.

#### Purpose
- Creates structured markdown index files for Java source files.
- Populates AI-generated summaries and keyword fields in index documents.

#### Type
class SourceFileIndexer
- final
- Implements no interfaces
- Notable annotations: @ToString

#### Input
- Constructor parameters:
  - Log log
  - Path baseDirectory
  - Path outputRoot
  - Collection<String> fileExtensions
  - String pluginVersion
  - String aiVersion
  - Collection<Path> subtrees
  - @Nullable Collection<String> excludes
  - boolean force
  - AiGenerationProvider generationProvider
  - @Nullable Collection<AiFieldGenerationConfig> fieldGenerations
  - AiPromptSupport promptSupport
  - AiModelDefinitionSupport modelDefinitionSupport

#### Output
- Return type: int from indexSourceRoot(Path)
- Side effects:
  - Writes .ai.md files to outputRoot directory
  - Logs informational and debug messages via Log

#### Core logic
- Walks source directories recursively using Files.walk
- Filters files by extension, subtree inclusion, and exclusion patterns
- Skips excluded files based on glob patterns relative to baseDirectory
- Generates AI index files with headers containing metadata like checksum and timestamps
- Uses field generation configurations to process source text and extract fields
- Writes final .ai.md documents using AiMdDocumentCodec

#### Public API
- indexSourceRoot(Path) -> int: indexes files under a given root directory
- isExcluded(Path) -> boolean: checks if file matches exclusion patterns
- matchesExtension(Path) -> boolean: verifies file extension match
- writeAiFile(Path) -> void: generates and writes AI index for a single source file
- matchesSubtree(Path) -> boolean: determines if path is within configured subtrees

#### Dependencies
Imports:
- java.io.IOException
- java.nio.file.Files
- java.nio.file.Path
- java.util.ArrayList
- java.util.Collection
- java.util.Collections
- java.util.List
- java.util.stream.Stream
- lombok.ToString
- net.ladenthin.maven.llamacpp.aiindex.config.AiFieldGenerationConfig
- net.ladenthin.maven.llamacpp.aiindex.config.AiFieldGenerationSelector
- net.ladenthin.maven.llamacpp.aiindex.config.AiModelDefinitionSupport
- net.ladenthin.maven.llamacpp.aiindex.document.AiGenerationResult
- net.ladenthin.maven.llamacpp.aiindex.document.AiMdDocument
- net.ladenthin.maven.llamacpp.aiindex.document.AiMdDocumentCodec
- net.ladenthin.maven.llamacpp.aiindex.document.AiMdHeader
- net.ladenthin.maven.llamacpp.aiindex.document.AiMdHeaderCodec
- net.ladenthin.maven.llamacpp.aiindex.document.AiMdHeaderSupport
- net.ladenthin.maven.llamacpp.aiindex.prompt.AiPromptPreparationSupport
- net.ladenthin.maven.llamacpp.aiindex.prompt.AiPromptSupport
- net.ladenthin.maven.llamacpp.aiindex.provider.AiGenerationProvider
- net.ladenthin.maven.llamacpp.aiindex.support.AiChecksumSupport
- net.ladenthin.maven.llamacpp.aiindex.support.AiPathSupport
- net.ladenthin.maven.llamacpp.aiindex.support.AiSourceExcludeFilter
- net.ladenthin.maven.llamacpp.aiindex.support.AiTimeSupport
- net.ladenthin.maven.llamacpp.aiindex.support.Java8CompatibilityHelper
- org.apache.maven.plugin.logging.Log
- org.jspecify.annotations.Nullable

#### Exceptions / Errors
- Throws IOException when reading or writing files
- Throws IllegalArgumentException when no field generation is configured or matches a file name
- Handles null inputs gracefully where annotations indicate @Nullable

#### Concurrency
No explicit concurrency mechanisms; assumes single-threaded use during Maven plugin execution.
