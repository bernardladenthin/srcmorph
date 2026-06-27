### AiGenerationKind.java
- H: 1.0
- C: 502093E2
- D: 2026-06-25T05:31:58Z
- T: 2026-06-27T05:09:25Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Identifies whether an AI generation operates on a single source file or a whole package.

#### Purpose
- Identifies AI generation operation scope: single file or package.

#### Type
- Enum: `AiGenerationKind`
- Modifiers: `public`
- No extends / implements
- No generics or type bounds
- No notable annotations

#### Input
- No constructor parameters
- No injected dependencies
- No consumed fields
- No read resources

#### Output
- No return values
- No produced state
- No mutated fields
- No written resources
- No side effects

#### Core logic
- Enum with two constants:
  - `FILE_SUMMARY`: Generation step for a single source file.
  - `PACKAGE_SUMMARY`: Generation step for a package aggregate.

#### Public API
- `FILE_SUMMARY`: Identifies generation for a single source file.
- `PACKAGE_SUMMARY`: Identifies generation for a package aggregate.

#### Dependencies
- No imports
- No referenced types

#### Exceptions / Errors
- No notable thrown/caught exceptions
- No null-handling or error conditions

#### Concurrency
- No threading, synchronization, immutability, or thread-safety notes
