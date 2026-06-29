### AiMdHeaderCodec.java
- H: 1.0
- C: 529B3335
- D: 2026-06-26T14:41:56Z
- T: 2026-06-26T16:06:07Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Parses and serializes metadata headers for AI index documents.

#### Purpose
- Reads and writes structured metadata from AI index document files.
- Manages key-value fields and child-link references in document headers.

#### Type
Class, final. Implements no interfaces. Uses Lombok @ToString annotation.

#### Input
- Constructor: no parameters.
- read(List<String> lines): list of text lines from a .ai.md file header.
- read(Path file): file path to an .ai.md file.
- write(AiMdHeader header): structured metadata object.

#### Output
- read(List<String>): AiMdHeader instance with parsed fields and children.
- write(AiMdHeader): serialized string representing the header.
- read(Path): AiMdHeader from file contents.

#### Core logic
- Parses lines to extract title, scalar fields, and child links.
- Matches field keys against predefined constants for correct mapping.
- Formats output strings using fixed prefixes and field values.
- Collects multiple child-link entries into a list in encounter order.

#### Public API
- read(List<String>) -> AiMdHeader: parses header from text lines.
- write(AiMdHeader) -> String: serializes header to string.
- read(Path) -> AiMdHeader: reads and parses header from file.

#### Dependencies
- java.io.IOException
- java.nio.charset.StandardCharsets
- java.nio.file.Files
- java.nio.file.Path
- java.util.ArrayList
- java.util.HashMap
- java.util.List
- java.util.Map
- net.ladenthin.maven.llamacpp.aiindex.support.Java8CompatibilityHelper
- net.ladenthin.maven.llamacpp.aiindex.document.AiMdHeader

#### Exceptions / Errors
- IOException: thrown when reading file contents fails.
- Null handling: defaults missing scalar fields to empty string.

#### Concurrency
- No explicit concurrency control; class is stateless and immutable.
