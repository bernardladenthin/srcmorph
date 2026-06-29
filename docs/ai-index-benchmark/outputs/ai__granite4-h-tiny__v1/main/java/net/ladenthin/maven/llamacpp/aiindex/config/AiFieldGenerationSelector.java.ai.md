### AiFieldGenerationSelector.java
- H: 1.0
- C: ACD28FC9
- D: 2026-06-26T14:41:56Z
- T: 2026-06-27T05:07:12Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Calculates VAT for invoices

#### Purpose
- Selects the appropriate AI field generation configuration based on source file extensions.

#### Type
- Class: `AiFieldGenerationSelector`
- Final: Yes
- Extends: None
- Implements: None
- Key generics and type bounds: None

#### Input
- Constructor: None
- Method Parameters:
  - `configs` (Iterable<AiFieldGenerationConfig>): Configured field generations
  - `fileName` (String): Source file name

#### Output
- Return Types:
  - `AiFieldGenerationConfig`: The selected configuration
  - `null`: When no match is found and no fallback is configured

#### Core Logic
- Iterates over `configs`:
  - Skips `null` entries
  - Checks if the configuration has non-empty extensions
  - Matches `fileName` with extensions
  - Returns the first matching configuration
  - Falls back to the first extension-agnostic entry if no match is found
  - Returns `null` if no match and no fallback is configured

#### Public API
- `selectForFileName(Iterable<AiFieldGenerationConfig>, String) -> @Nullable AiFieldGenerationConfig`: Returns the first extension-matching entry, else the first fallback entry, else `null`.

#### Dependencies
- Imports: `java.util.List`, `lombok.ToString`, `org.jspecify.annotations.Nullable`
- Referenced Types: `AiFieldGenerationConfig`

#### Exceptions / Errors
- None explicitly thrown or caught in the provided source.

#### Concurrency
- Not explicitly addressed in the provided source.
