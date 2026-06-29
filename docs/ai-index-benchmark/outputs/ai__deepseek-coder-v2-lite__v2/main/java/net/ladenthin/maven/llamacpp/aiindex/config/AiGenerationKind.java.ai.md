### AiGenerationKind.java
- H: 1.0
- C: 502093E2
- D: 2026-06-25T05:31:58Z
- T: 2026-06-27T03:41:49Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> This file defines constants for specifying the kind of AI generation, whether it operates on a single source file or a whole package.

#### Purpose
- Define constants for specifying the kind of AI generation.

#### Type
- `enum AiGenerationKind`
- Modifiers: public
- Extends: none
- Notable annotations: none

#### Input
- None

#### Output
- Constants: `FILE_SUMMARY`, `PACKAGE_SUMMARY`

#### Core logic
- Represents the kind of AI generation.

#### Public API
- `FILE_SUMMARY() -> AiGenerationKind`: Generation for a single source file.
- `PACKAGE_SUMMARY() -> AiGenerationKind`: Generation for a package aggregate.

#### Dependencies
- None

#### Exceptions / Errors
- None

#### Concurrency
- Thread-safe: enum constants are inherently thread-safe.
