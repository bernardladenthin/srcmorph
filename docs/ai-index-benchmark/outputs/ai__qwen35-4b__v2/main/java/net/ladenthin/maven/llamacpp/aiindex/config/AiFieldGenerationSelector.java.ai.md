### AiFieldGenerationSelector.java
- H: 1.0
- C: ACD28FC9
- D: 2026-06-26T14:41:56Z
- T: 2026-06-26T18:45:32Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Selects the appropriate AI field generation configuration for a given source file based on its extension or a configured fallback.

#### Purpose
- Maps source file names (e.g., `.java`, `.sql`) to specific `AiFieldGenerationConfig` instances.
- Supports language-specific prompts while maintaining a single loaded AI model.

#### Type
public final class AiFieldGenerationSelector; @ToString annotation.

#### Input
- `Iterable<AiFieldGenerationConfig> configs`: Ordered list of configured field generations (null entries skipped).
- `String fileName`: Source file name (e.g., `Foo.java`).

#### Output
- `@Nullable AiFieldGenerationConfig`: The first extension-matching config, or the first extension-agnostic fallback, or `null` if none match.

#### Core logic
- Iterates through `configs` in declaration order.
- Skips null entries.
- Checks if an entry has non-empty file extensions; if yes, returns the first entry where `fileName.endsWith(extension)`.
- If no extension matches, stores the entry as a fallback if not already set.
- Returns the stored fallback only if the loop completes without finding an extension match.

#### Public API
`selectForFileName(Iterable<AiFieldGenerationConfig> configs, String fileName) -> @Nullable AiFieldGenerationConfig` — selects matching config or fallback.

#### Dependencies
`AiFieldGenerationConfig`, `List<String>`, `String`, `Iterable`.

#### Exceptions / Errors
- Returns `null` if no config matches and no fallback is configured.
- Handles null entries in the `configs` list by skipping them.

#### Concurrency
None; logic is single-threaded and stateless.
