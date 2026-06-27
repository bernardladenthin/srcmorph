### AiFieldGenerationSelector.java
- H: 1.0
- C: ACD28FC9
- D: 2026-06-26T14:41:56Z
- T: 2026-06-26T18:07:09Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Selects the appropriate AI field generation configuration for a source file based on file extension matching rules.

#### Purpose
- Applies language-specific or generic AI prompts to source files by matching extensions.
- Enables dynamic prompt variation per file while keeping a single loaded AI model active.

#### Type
class; final; extends Object; implements none; annotated with @ToString; key generics none; type bounds none; notable annotations @lombok.ToString, @jspecify.annotations.Nullable.

#### Input
- Iterable<AiFieldGenerationConfig> configs: list of configured field generation rules.
- String fileName: the name of the source file being processed.
- Injected dependencies none; consumed fields none; read resources none.

#### Output
- AiFieldGenerationConfig: the selected configuration instance matching the file extension or fallback rule.
- Produced state none; mutated fields none; written resources none; side effects none.

#### Core logic
- Iterates through configured field generation configs in declaration order.
- Skips null entries and identifies extension-agnostic fallbacks (null or empty extension lists).
- Matches the input fileName against specific file extensions defined in each config using endsWith.
- Returns the first matching config immediately upon finding a match.
- Returns the last identified fallback config if no specific extension matches.
- Returns null if no config matches and no fallback is configured.

#### Public API
- selectForFileName(Iterable<AiFieldGenerationConfig> configs, String fileName) -> @Nullable AiFieldGenerationConfig: selects matching config by extension.

#### Dependencies
AiFieldGenerationConfig, Iterable, List, String, AiFieldGenerationSelector.

#### Exceptions / Errors
No exceptions thrown; null input handled gracefully via skip logic and return value.

#### Concurrency
Immutability ensures thread-safety; no synchronization required as stateless selection logic.
