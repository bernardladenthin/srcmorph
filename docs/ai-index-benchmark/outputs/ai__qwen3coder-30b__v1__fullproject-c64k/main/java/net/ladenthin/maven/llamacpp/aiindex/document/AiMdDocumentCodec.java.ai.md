### AiMdDocumentCodec.java
- H: 1.0
- C: E353030C
- D: 2026-06-26T14:41:56Z
- T: 2026-06-26T16:03:09Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Reads and writes structured markdown documents with metadata headers and bodies from disk.

#### Purpose
- Parses .ai.md files into structured document objects.
- Serializes document objects back into .ai.md file format.

#### Type
Class, final. Implements no interfaces. Uses generics: List<String>, StringBuilder. Notable annotations: @ToString.

#### Input
- Constructor: no parameters.
- read(Path): Path to .ai.md file.
- read(List<String>): Lines of .ai.md content.
- write(AiMdDocument): Document object with header and body.
- write(Path, AiMdDocument): File path and document object.

#### Output
- read(Path): AiMdDocument object.
- read(List<String>): AiMdDocument object.
- write(AiMdDocument): String representation of .ai.md content.
- write(Path, AiMdDocument): writes UTF-8 file.

#### Core logic
- Splits input lines into header and body sections based on separator line.
- Identifies header lines by prefix patterns (title, field).
- Skips blank lines before header ends.
- Parses header section using AiMdHeaderCodec.
- Serializes header and body with proper formatting and separator.

#### Public API
- read(Path) -> AiMdDocument: Reads file to document object.
- write(Path, AiMdDocument) -> void: Writes document to UTF-8 file.

#### Dependencies
Imports: java.io.IOException, java.nio.charset.StandardCharsets, java.nio.file.Files, java.nio.file.Path, java.util.ArrayList, java.util.List, lombok.ToString, net.ladenthin.maven.llamacpp.aiindex.support.Java8CompatibilityHelper.

Referenced types: AiMdDocument, AiMdHeaderCodec, AiMdHeader.

#### Exceptions / Errors
Throws IOException when reading or writing files. Handles null or malformed input through compatibility helper and blank line checks.

#### Concurrency
No explicit concurrency handling; assumes single-threaded file access.
