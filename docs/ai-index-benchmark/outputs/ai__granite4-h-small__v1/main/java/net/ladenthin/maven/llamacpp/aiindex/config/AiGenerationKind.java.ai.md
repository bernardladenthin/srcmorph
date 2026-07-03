### AiGenerationKind.java
- H: 1.0
- C: 502093E2
- D: 2026-06-25T05:31:58Z
- T: 2026-07-02T21:56:45Z
- G: 1.0.3-SNAPSHOT
- A: 0.0.0
- X: file
---
**Facts (exact, whole file):** type declarations: 1; public declarations: 1; TODO/FIXME: 0; @Override: 0; methods (approx): 0; constructors: 0; field declarations (w/ modifier): 0

> Enumerates whether AI generation targets a single source file or an entire package.

#### Purpose
- Defines AI generation scope for summarization steps.

#### Type
- `enum` AiGenerationKind; public.

#### Input
- None.

#### Output
- None.

#### Core logic
- Provides two constants:
  - `FILE_SUMMARY` for file‑level generation.
  - `PACKAGE_SUMMARY` for package‑level generation.

#### Public API
- `FILE_SUMMARY -> single‑file generation`  
- `PACKAGE_SUMMARY -> package‑level generation`

#### Dependencies
- Java language enum.

#### Exceptions / Errors
- None.

#### Concurrency
- Immutable enum constants.
