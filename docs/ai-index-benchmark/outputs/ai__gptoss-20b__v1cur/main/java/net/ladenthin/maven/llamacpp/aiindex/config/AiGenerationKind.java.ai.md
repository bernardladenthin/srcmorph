### AiGenerationKind.java
- H: 1.0
- C: 502093E2
- D: 2026-06-25T05:31:58Z
- T: 2026-07-02T23:02:58Z
- G: 1.0.3-SNAPSHOT
- A: 0.0.0
- X: file
---
**Facts (exact, whole file):** type declarations: 1; public declarations: 1; TODO/FIXME: 0; @Override: 0; methods (approx): 0; constructors: 0; field declarations (w/ modifier): 0

> Enumerates the scope of an AI generation step—either per-file or per-package.

#### Purpose
- Defines the granularity of AI generation.

#### Type
- `enum` AiGenerationKind  
  - No modifiers  
  - No supertype or interfaces

#### Input
- None

#### Output
- None

#### Core logic
- Two constants:  
  - `FILE_SUMMARY` – generation for a single source file.  
  - `PACKAGE_SUMMARY` – generation for a package aggregate.

#### Public API
- None

#### Dependencies
- None

#### Exceptions / Errors
- None

#### Concurrency
- None
