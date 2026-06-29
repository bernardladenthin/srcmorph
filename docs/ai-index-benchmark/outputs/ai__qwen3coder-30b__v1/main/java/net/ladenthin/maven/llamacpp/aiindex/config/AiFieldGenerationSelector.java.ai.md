### AiFieldGenerationSelector.java
- H: 1.0
- C: ACD28FC9
- D: 2026-06-26T14:41:56Z
- T: 2026-06-26T22:41:44Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Selects appropriate AI field generation configuration based on file extensions and fallback rules.

#### Purpose
- Matches source files to AI prompt configurations by file extension.
- Provides fallback behavior when no matching extension is found.

#### Type
Class, final. No extends or implements. Uses @ToString annotation. Generic type: Iterable<AiFieldGenerationConfig>.

#### Input
- Constructor takes no parameters.
- Method `selectForFileName` consumes:
  - configs: Iterable of AiFieldGenerationConfig, may contain nulls
  - fileName: String representing a file name (e.g., "Foo.java")

#### Output
- Method returns @Nullable AiFieldGenerationConfig.
- Returns first matching config by extension or first fallback config if none match.
- Returns null when no match and no fallback is present.

#### Core logic
- Iterates through configured AiFieldGenerationConfig entries.
- Skips null configs in the input iterable.
- For each config, checks if it has an empty or absent file extension list; tracks first such entry as fallback.
- For non-empty extension lists, checks if fileName ends with any of those extensions.
- Returns matching config immediately upon first match.
- Falls back to tracked fallback config if no extension matches.
- Returns null if no match and no fallback exists.

#### Public API
- `selectForFileName(configs, fileName) -> AiFieldGenerationConfig` selects configuration by file name

#### Dependencies
- java.util.List
- lombok.ToString
- org.jspecify.annotations.Nullable
- AiFieldGenerationConfig

#### Exceptions / Errors
- Null handling: skips null entries in configs iterable.
- Returns null when no matching config and no fallback is configured.

#### Concurrency
- No concurrency concerns; class is stateless and method is pure.
