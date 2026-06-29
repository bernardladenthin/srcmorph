### main/java/net/ladenthin/maven/llamacpp/aiindex/support
- H: 1.0
- C: 4A74F585
- D: 2026-06-26T14:41:56Z
- T: 2026-06-26T17:26:00Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: package
- F: [AiChecksumSupport.java](AiChecksumSupport.java.ai.md)
- F: [AiPathSupport.java](AiPathSupport.java.ai.md)
- F: [AiSourceExcludeFilter.java](AiSourceExcludeFilter.java.ai.md)
- F: [AiTimeSupport.java](AiTimeSupport.java.ai.md)
- F: [ConvertToRecord.java](ConvertToRecord.java.ai.md)
- F: [Java8CompatibilityHelper.java](Java8CompatibilityHelper.java.ai.md)
- F: [package-info.java](package-info.java.ai.md)
---
> Provides foundational utility functions for AI metadata indexing, including checksums, path normalization, time formatting, and Java 8 compatibility.

#### Purpose
- Generates and validates file integrity checksums for AI index metadata.
- Normalizes source paths and formats timestamps for consistent indexing.

#### Responsibilities
- File integrity and validation: CRC32 checksum computation for files and strings.
- Path manipulation: Normalizing source file paths by stripping redundant segments.
- Time formatting: Formatting timestamps with second-level precision for metadata.
- Java compatibility: Wrapping modern Java APIs for use in older JDKs.
- Exclusion filtering: Applying glob patterns to exclude specific source files from indexing.

#### Key units
- `AiChecksumSupport` computes hexadecimal CRC32 checksums for file and string content.
- `AiPathSupport` normalizes source paths by removing leading "src" segments.
- `AiTimeSupport` formats timestamps into ISO-8601 strings with second precision.
- `AiSourceExcludeFilter` filters source paths using glob patterns to exclude files from indexing.
- `Java8CompatibilityHelper` wraps Java 9+ APIs for compatibility with Java 8.
- `ConvertToRecord` serves as a compile-time marker for future refactoring to Java records.

#### Data flow
Input file paths and content are processed through checksum and path normalization utilities before being formatted into AI metadata. Timestamps are generated at indexing time, and exclusion filters determine which files contribute to the index. All operations are stateless and side-effect free.

#### Dependencies
- `java.io.IOException`, `java.nio.charset.StandardCharsets`, `java.nio.file.Files`, `java.nio.file.Path`, `java.util.zip.CRC32`
- `java.time.Instant`, `java.time.format.DateTimeFormatter`, `java.time.temporal.ChronoUnit`
- `java.util.ArrayList`, `java.util.Collection`, `java.util.List`, `java.util.regex.Pattern`
- `lombok.ToString`, `org.jspecify.annotations.Nullable`

#### Cross-cutting
- Statelessness: All utility classes are immutable and stateless, ensuring thread safety.
- Exception handling: Standard Java exceptions like `IOException` and `NullPointerException` are used where applicable.
- Java 8 compatibility: The `Java8CompatibilityHelper` centralizes workarounds for Java 9+ APIs to maintain JDK 8 support.
- Glob pattern matching: `AiSourceExcludeFilter` uses regex compilation for efficient path exclusion based on glob syntax.
