### AiGenerationKind.java
- H: 1.0
- C: 502093E2
- D: 2026-06-25T05:31:58Z
- T: 2026-06-27T04:09:47Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Identifies AI generation scope: single files or packages

#### Purpose
- Enumerates AI generation targets (per-file or aggregate)

#### Type
enum public sealed

#### Input
- None (pure enum)

#### Output
- None (no methods, only constants)

#### Core logic
- Defines two constants for file-level and package-level AI generation

#### Public API
- FILE_SUMMARY -> Represents per-file AI generation
- PACKAGE_SUMMARY -> Represents aggregate package AI generation

#### Dependencies
- net.ladenthin.maven.llamacpp.aiindex.config.AiGenerationKind

#### Exceptions / Errors
- None (enum constants)

#### Concurrency
- Immutable, thread-safe
