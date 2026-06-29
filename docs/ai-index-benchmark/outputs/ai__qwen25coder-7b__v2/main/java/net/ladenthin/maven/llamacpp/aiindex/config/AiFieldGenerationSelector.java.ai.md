### AiFieldGenerationSelector.java
- H: 1.0
- C: ACD28FC9
- D: 2026-06-26T14:41:56Z
- T: 2026-06-26T20:15:30Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Selects the `AiFieldGenerationConfig` that applies to a given source file based on file extensions.

#### Purpose
- Configures the selection of AI field generation settings for different file types.

#### Type
- `public final class` + `@ToString`

#### Input
- `configs`: Iterable of `AiFieldGenerationConfig` entries.
- `fileName`: The source file name.

#### Output
- Returns the first `AiFieldGenerationConfig` whose non-empty extension list matches the file name, or the first fallback entry if no match is found, or `null` if no entries are configured and no fallback is set.

#### Core logic
- Iterates over the `configs`.
- Skips any `null` entries.
- Checks each `AiFieldGenerationConfig`'s `fileExtensions`.
- Returns the first matching config based on file extension.
- If no match, returns the first fallback config (if any).
- Returns `null` if no match and no fallback is configured.

#### Public API
- `selectForFileName(final Iterable<AiFieldGenerationConfig> configs, final String fileName) -> @Nullable AiFieldGenerationConfig`: Returns the `AiFieldGenerationConfig` for the given file name.

#### Dependencies
- `AiFieldGenerationConfig`
- `@Nullable`

#### Exceptions / Errors
- Throws no exceptions; handles `null` entries in `configs`.

#### Concurrency
- Not applicable.
