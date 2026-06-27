### AiGenerationKind.java
- H: 1.0
- C: 502093E2
- D: 2026-06-25T05:31:58Z
- T: 2026-06-26T23:12:31Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Enumerates the scope of AI generation tasks, distinguishing between individual file and package-level processing.

#### Purpose
- Defines discrete AI generation scopes for code analysis.
- Supports configuration of AI behavior per source unit.

#### Type
Enum public AiGenerationKind implements none; key generics none; notable annotations none

#### Core logic
- Provides two distinct values: FILE_SUMMARY and PACKAGE_SUMMARY.
- Each value represents a unique processing context for AI tools.

#### Public API
JavaBean getters/setters for: FILE_SUMMARY, PACKAGE_SUMMARY

#### Dependencies
none

#### Exceptions / Errors
none

#### Concurrency
none
