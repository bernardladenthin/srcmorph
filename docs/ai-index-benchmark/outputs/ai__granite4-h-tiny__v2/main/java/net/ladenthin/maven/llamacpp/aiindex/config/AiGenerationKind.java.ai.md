### AiGenerationKind.java
- H: 1.0
- C: 502093E2
- D: 2026-06-25T05:31:58Z
- T: 2026-06-27T05:32:01Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Identifies whether an AI generation operates on a single source file or a whole package.

#### Purpose
- Enum for AI generation kind: single file or package summary.

#### Type
- Enum: `AiGenerationKind`

#### Input
- None

#### Output
- None

#### Core logic
- Defines two constants:
  - `FILE_SUMMARY`
  - `PACKAGE_SUMMARY`

#### Public API
- `FILE_SUMMARY`: Identifies generation for a single source file.
- `PACKAGE_SUMMARY`: Identifies generation for a package aggregate.

#### Dependencies
- None

#### Exceptions / Errors
- None

#### Concurrency
- Not thread-safe (enum is inherently immutable and thread-safe)
