### AiChecksumSupport.java
- H: 1.0
- C: 2A51047F
- D: 2026-06-25T05:31:58Z
- T: 2026-06-26T17:05:38Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Computes CRC32 checksums for files and strings used in AI metadata headers.

#### Purpose
- Generates hexadecimal CRC32 checksums for content validation.
- Supports AI index file integrity checks.

#### Type
class public final
Implements: none
Extends: none
Generics: none
Annotations: @ToString

#### Input
- Constructor: no parameters
- Method `calculateCrc32Hex(Path)`: Path file
- Method `calculateCrc32Hex(String)`: String value
- Method `calculateCrc32Hex(byte[])`: byte[] bytes

#### Output
- Return type `String` for all methods
- Side effect: file read (in `calculateCrc32Hex(Path)`)

#### Core logic
- Read file contents into byte array
- Compute CRC32 checksum on byte array
- Format checksum as 8-character uppercase hexadecimal string

#### Public API
- `calculateCrc32Hex(Path) -> String` computes file checksum
- `calculateCrc32Hex(String) -> String` computes string checksum
- `calculateCrc32Hex(byte[]) -> String` computes byte array checksum

#### Dependencies
java.io.IOException
java.nio.charset.StandardCharsets
java.nio.file.Files
java.nio.file.Path
java.util.zip.CRC32
lombok.ToString

#### Exceptions / Errors
- Throws IOException when file cannot be read
- Null handling: no explicit null checks, assumes valid inputs

#### Concurrency
None noted. Class is stateless and immutable.
