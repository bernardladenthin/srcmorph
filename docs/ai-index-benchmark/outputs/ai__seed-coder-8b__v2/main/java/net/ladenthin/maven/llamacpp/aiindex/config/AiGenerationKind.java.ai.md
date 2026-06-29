### AiGenerationKind.java
- H: 1.0
- C: 502093E2
- D: 2026-06-25T05:31:58Z
- T: 2026-06-27T04:39:55Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Enum defining AI generation scope: single file or entire package

#### Purpose
- Defines AI generation targets (file vs. package)
- Enumerates possible generation modes

#### Type
enum public + final

#### Core logic
- Lists two generation modes: FILE_SUMMARY and PACKAGE_SUMMARY
- Each mode represents a distinct purpose for AI processing

#### Public API
AiGenerationKind() -> void
values() -> AiGenerationKind[]
valueOf(String) -> AiGenerationKind
FILE_SUMMARY -> enum constant representing file-level summary
PACKAGE_SUMMARY -> enum constant representing package-level summary

#### Dependencies
java.lang.Enum
java.lang.String

#### Exceptions / Errors
No explicit exceptions
