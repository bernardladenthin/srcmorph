### AiFieldGenerationSelector.java
- H: 1.0
- C: ACD28FC9
- D: 2026-06-26T14:41:56Z
- T: 2026-06-26T19:21:38Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Selects the `AiFieldGenerationConfig` that applies to a given source file based on file extensions.

#### Purpose
- Configures selection of AI field generation settings for source files based on their extensions.

#### Type
- Class (`public final class`)
- Annotations: `@ToString`, `@Nullable`

#### Input
- `configs`: Iterable of `AiFieldGenerationConfig` objects (nullable entries are skipped)
- `fileName`: String representing the source file name

#### Output
- Returns the first `AiFieldGenerationConfig` whose non-empty extension list matches the file name, else the first fallback entry, else `null`

#### Core logic
- Iterates through each `AiFieldGenerationConfig` in the provided iterable
- Skips any `null` entries
- Checks if the `AiFieldGenerationConfig` has a non-empty list of file extensions
- If no extension list or it is empty, sets this as the fallback configuration
- For each extension, checks if the file name ends with that extension and returns the corresponding `AiFieldGenerationConfig`
- If no matching configuration is found, returns the fallback configuration

#### Public API
- `selectForFileName(final Iterable<AiFieldGenerationConfig> configs, final String fileName) -> @Nullable AiFieldGenerationConfig`: Selects the appropriate field generation configuration for a given file name.

#### Dependencies
- Imports: `java.util.List`, `lombok.ToString`, `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- No notable exceptions; handles `null` entries in `configs` by skipping them

#### Concurrency
- Not thread-safe; intended to be used in a single-threaded environment
