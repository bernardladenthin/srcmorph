### AiGenerationKind.java
- H: 1.0
- C: 502093E2
- D: 2026-06-25T05:31:58Z
- T: 2026-06-26T15:56:25Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Determines whether AI generation processes individual files or entire packages

#### Purpose
- Distinguishes AI processing scope for code analysis
- Supports configuration of generation strategies

#### Type
Enum, final

#### Core logic
- Defines two generation modes: FILE_SUMMARY and PACKAGE_SUMMARY
- Represents discrete AI processing strategies based on scope

#### Public API
- FILE_SUMMARY() -> AiGenerationKind: Single file analysis mode
- PACKAGE_SUMMARY() -> AiGenerationKind: Package aggregate analysis mode

#### Dependencies
- net.ladenthin.maven.llamacpp.aiindex.config.AiGenerationKind

#### Exceptions / Errors
- No explicit exceptions handled
- Null handling not applicable for enum values

#### Concurrency
- Enum is inherently immutable and thread-safe
- No synchronization required for usage
