### AiGenerationKind.java
- H: 1.0
- C: 502093E2
- D: 2026-06-25T05:31:58Z
- T: 2026-06-27T06:17:54Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Defines whether AI generates per-file or per-package summaries

#### Purpose
- Specifies scope of AI-generated code summaries  
- Distinguishes between file-level and package-level generation

#### Type
enum public AiGenerationKind

#### Input
none

#### Output
none

#### Core logic
- Provides two distinct values for generation scope: FILE_SUMMARY and PACKAGE_SUMMARY  
- No computation or state transformation; purely a domain enum

#### Public API
FILE_SUMMARY() -> void: Represents per-file AI summary generation  
PACKAGE_SUMMARY() -> void: Represents per-package AI summary generation

#### Dependencies
none

#### Exceptions / Errors
none

#### Concurrency
none
