### AiGenerationKind.java
- H: 1.0
- C: 502093E2
- D: 2026-06-25T05:31:58Z
- T: 2026-06-26T19:29:16Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Identifies whether an AI generation operates on a single source file or a whole package.

#### Type
enum

#### Core logic
- Defines two constants: `FILE_SUMMARY` and `PACKAGE_SUMMARY`.
- Each constant represents a type of AI generation step.

#### Public API
- `FILE_SUMMARY` -> Represents a generation step that produces fields for a single source file.
- `PACKAGE_SUMMARY` -> Represents a generation step that produces fields for a package aggregate.
