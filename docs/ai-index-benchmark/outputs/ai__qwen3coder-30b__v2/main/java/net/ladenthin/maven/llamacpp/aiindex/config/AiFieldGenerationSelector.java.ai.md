### AiFieldGenerationSelector.java
- H: 1.0
- C: ACD28FC9
- D: 2026-06-26T14:41:56Z
- T: 2026-06-26T23:09:17Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Selects AI field generation configurations based on file extensions to apply language-specific prompts.

#### Purpose
- Matches source files to AI configuration rules by file extension
- Provides fallback behavior when no extension matches

#### Type
Final class; implements no interfaces; key generics: `AiFieldGenerationConfig`; notable annotations: `@ToString`

#### Input
- Constructor takes no parameters
- Method `selectForFileName` consumes: `Iterable<AiFieldGenerationConfig> configs`, `String fileName`

#### Output
- Method returns: `@Nullable AiFieldGenerationConfig`
- Side effects: none

#### Core logic
- Iterates through configured entries in order
- Skips null configurations
- For each config, checks if file name ends with any extension in its list
- Returns first matching config or fallback config if no match found
- Returns null if no matches and no fallback

#### Public API
`selectForFileName(configs, fileName) -> AiFieldGenerationConfig` Picks configuration by file extension

#### Dependencies
`AiFieldGenerationConfig`, `Iterable`, `List`, `String`

#### Exceptions / Errors
Handles null configs gracefully; returns null when no match found

#### Concurrency
No concurrency concerns; stateless operation
