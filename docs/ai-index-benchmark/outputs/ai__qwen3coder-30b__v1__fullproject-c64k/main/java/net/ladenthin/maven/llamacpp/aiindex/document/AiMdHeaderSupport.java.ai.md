### AiMdHeaderSupport.java
- H: 1.0
- C: F47C5B40
- D: 2026-06-25T05:31:58Z
- T: 2026-06-26T16:07:47Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Determines if an .ai.md document header needs regeneration based on version, content, or structural differences.

#### Purpose
- Compares document headers to decide whether to rewrite a file.
- Evaluates file existence, content, and header version for changes.

#### Type
Class, final. Implements no interfaces. Uses Lombok @ToString annotation.

#### Input
- Constructor: no parameters.
- Method `shouldWrite`: boolean force, Path targetFile, AiMdHeader expectedHeader.
- Dependencies: Java8CompatibilityHelper, AiMdDocumentCodec, AiMdHeaderCodec.
- Reads file content via Files.exists, Files.read.

#### Output
- Returns boolean indicating whether to rewrite the file.
- Side effect: reads from filesystem at targetFile path.

#### Core logic
- If force flag is true, always returns true.
- If target file does not exist, returns true.
- Parses existing document using AiMdDocumentCodec.
- Checks if header version matches HEADER_VERSION_1_0.
- Checks if body is blank using Java8CompatibilityHelper.
- Compares all fields of expectedHeader against actualHeader.

#### Public API
- `shouldWrite(force, targetFile, expectedHeader) -> boolean` determines rewrite necessity.

#### Dependencies
- java.io.IOException
- java.nio.file.Files
- java.nio.file.Path
- net.ladenthin.maven.llamacpp.aiindex.support.Java8CompatibilityHelper
- net.ladenthin.maven.llamacpp.aiindex.document.AiMdDocumentCodec
- net.ladenthin.maven.llamacpp.aiindex.document.AiMdHeaderCodec
- net.ladenthin.maven.llamacpp.aiindex.document.AiMdHeader

#### Exceptions / Errors
- Throws IOException when file cannot be read.
- Null handling: assumes expectedHeader is not null.

#### Concurrency
- No explicit concurrency control; class is stateless.
