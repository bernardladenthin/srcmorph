### AiFieldGenerationSelector.java
- H: 1.0
- C: ACD28FC9
- D: 2026-06-26T14:41:56Z
- T: 2026-06-26T15:52:31Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Selects AI field generation configuration based on file extensions and fallback rules.

#### Purpose
- Matches source files to AI prompt configurations by file extension.
- Provides fallback behavior when no extension matches.

#### Type
Class, final. No extends or implements. Generics: Iterable<AiFieldGenerationConfig>. Annotations: @ToString.

#### Input
- Constructor takes no parameters.
- Method `selectForFileName` consumes:
  - configs: Iterable of AiFieldGenerationConfig, may contain nulls
  - fileName: String, e.g. "Foo.java"

#### Output
- Method returns @Nullable AiFieldGenerationConfig.
- Null when no matching config or fallback is found.

#### Core logic
- Iterates through AiFieldGenerationConfig entries in order.
- Skips null configs.
- For each config:
  - If file extensions list is empty/absent, stores as fallback if not already set.
  - Otherwise checks if fileName ends with any extension in the list.
  - Returns first matching config.
- Returns fallback if no match found.

#### Public API
- `selectForFileName(configs, fileName) -> AiFieldGenerationConfig` selects config by file extension

#### Dependencies
- java.util.List
- lombok.ToString
- org.jspecify.annotations.Nullable
- AiFieldGenerationConfig

#### Exceptions / Errors
- Null handling: skips null configs in Iterable.
- Returns null when no match and no fallback.

#### Concurrency
- No concurrency concerns; stateless class with immutable inputs.
