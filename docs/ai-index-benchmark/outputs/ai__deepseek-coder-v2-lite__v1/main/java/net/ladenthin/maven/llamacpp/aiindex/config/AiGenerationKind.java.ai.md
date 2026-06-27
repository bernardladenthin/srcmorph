### AiGenerationKind.java
- H: 1.0
- C: 502093E2
- D: 2026-06-25T05:31:58Z
- T: 2026-06-27T03:10:51Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Identifies whether an AI generation operates on a single source file or a whole package.

#### Purpose
- **Identify** the scope of AI generation (single file or entire package).

#### Type
- `enum`
- Modifiers: `public`

#### Input
- No input parameters.

#### Output
- Enum constants: `FILE_SUMMARY`, `PACKAGE_SUMMARY`.

#### Core logic
- **Define** two modes of AI generation based on the scope (single file or package).

#### Public API
- `FILE_SUMMARY() -> AiGenerationKind` - Indicates AI generation for a single source file.
- `PACKAGE_SUMMARY() -> AiGenerationKind` - Indicates AI generation for an entire package.

#### Dependencies
- No imports or referenced types.

#### Exceptions / Errors
- No exceptions or error handling mentioned.

#### Concurrency
- Enum values are thread-safe and do not involve concurrency.
