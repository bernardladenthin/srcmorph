### AiFieldGenerationSelector.java
- H: 1.0
- C: ACD28FC9
- D: 2026-06-26T14:41:56Z
- T: 2026-06-27T04:05:08Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Selects AI field generation configuration based on source file extensions

#### Purpose
- Matches AI field generation config to source files by extension
- Implements selection logic for AI prompts

#### Type
- `final class`
- Uses Lombok's `@ToString` annotation

#### Input
- Iterable of `AiFieldGenerationConfig` objects
- Source file name as String

#### Output
- Matching `AiFieldGenerationConfig` or null

#### Core logic
- Iterates through configs in declaration order
- Checks for matching extensions
- Handles fallback configs
- Returns first match or fallback

#### Public API
- `AiFieldGenerationSelector() -> void`
  Constructs a new selector instance
- `selectForFileName(Iterable<AiFieldGenerationConfig>, String) -> @Nullable AiFieldGenerationConfig`
  Selects appropriate config for given file name

#### Dependencies
- `java.util.List`
- `lombok.ToString`
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- Handles null inputs gracefully
- Returns null when no match and no fallback

#### Concurrency
- Immutable class, thread-safe
