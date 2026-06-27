### AiGenerationKind.java
- H: 1.0
- C: 502093E2
- D: 2026-06-25T05:31:58Z
- T: 2026-06-26T21:11:06Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Defines AI generation target: single file or entire package.

#### Purpose
- Identifies AI generation scope for configuration.  
- Distinguishes file‑level from package‑level generation steps.

#### Type
- `enum AiGenerationKind`  
- Final, non‑abstract; extends `java.lang.Enum`.  

#### Core logic
- Declares constants `FILE_SUMMARY` and `PACKAGE_SUMMARY`.  
- No additional methods or logic.

#### Public API
- `FILE_SUMMARY` → single‑file generation.  
- `PACKAGE_SUMMARY` → package‑wide generation.  

#### Dependencies
- None.  

#### Exceptions / Errors
- None.  

#### Concurrency
- Immutable by design; thread‑safe.
