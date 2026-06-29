### AiFieldGenerationSelector.java
- H: 1.0
- C: ACD28FC9
- D: 2026-06-26T14:41:56Z
- T: 2026-06-27T05:30:11Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Selects the appropriate AI field generation configuration based on a source file's extensions.

#### Purpose
- Chooses the correct AI field generation configuration for a given source file based on file extensions.
- Supports extension-specific prompts and a fallback for files without matching extensions.

#### Type
- Class: `AiFieldGenerationSelector`
- Extends: None
- Implements: None
- Key generics: None
- Annotations: `@ToString`, `@lombok.ToString`, `@org.jspecify.annotations.Nullable`

#### Input
- Constructor: None
- Method `selectForFileName`:
  - `Iterable<AiFieldGenerationConfig> configs`: Configurations in declaration order.
  - `String fileName`: The source file name.

#### Output
- Returns: `AiFieldGenerationConfig` or `null`
- Produces: Config selection based on file extensions.
- Writes: None
- Side effects: None

#### Core Logic
- Iterates over `configs`:
  - Skips `null` entries.
  - Checks if the configuration has non-empty extensions.
  - Matches `fileName` with each extension.
  - Returns the first matching configuration.
  - Falls back to the first configuration with no extensions or `null` if none match.

#### Public API
- `AiFieldGenerationSelector()`: No-op constructor.
- `selectForFileName(Iterable<AiFieldGenerationConfig>, String)`: Returns the appropriate `AiFieldGenerationConfig` based on file extensions.

#### Dependencies
- `AiFieldGenerationConfig`
- `List<String>`

#### Exceptions / Errors
- None explicitly thrown or caught.

#### Concurrency
- Not explicitly threaded or thread-safe.
