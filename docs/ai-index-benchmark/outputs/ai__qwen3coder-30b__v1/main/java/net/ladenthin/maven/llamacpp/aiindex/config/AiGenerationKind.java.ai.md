### AiGenerationKind.java
- H: 1.0
- C: 502093E2
- D: 2026-06-25T05:31:58Z
- T: 2026-06-26T22:45:17Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Identifies the scope of AI generation tasks for source files or packages

#### Purpose
- Distinguishes AI generation modes for individual files vs. package-level aggregates
- Supports configuration of AI processing workflows

#### Type
Enum, final. No extends or implements clauses.

#### Input
None directly consumed. Enum constants defined statically.

#### Output
None produced. Constants serve as labeled identifiers.

#### Core logic
- Defines two distinct AI generation modes: FILE_SUMMARY and PACKAGE_SUMMARY
- Represents discrete processing scopes for automated code analysis

#### Public API
- FILE_SUMMARY() -> AiGenerationKind - single file analysis mode
- PACKAGE_SUMMARY() -> AiGenerationKind - package aggregate analysis mode

#### Dependencies
None imported. No external type references.

#### Exceptions / Errors
None thrown or caught. No null handling required.

#### Concurrency
No concurrency concerns. Enum constants are immutable and thread-safe by design.
