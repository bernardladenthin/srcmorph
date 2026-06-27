### AiGenerationKind.java
- H: 1.0
- C: 502093E2
- D: 2026-06-25T05:31:58Z
- T: 2026-06-26T18:11:34Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Defines enumeration values to distinguish between AI generation operations on individual source files versus entire packages.

#### Purpose
- Identifies single-file summary generation steps.
- Identifies package-aggregate summary generation steps.

#### Type
Enum with no modifiers. No extends or implements. No annotations.

#### Input
None.

#### Output
None.

#### Core logic
None. Enum values are constant identifiers for generation scope.

#### Public API
`FILE_SUMMARY` -> constant (single-file scope)
`PACKAGE_SUMMARY` -> constant (package-aggregate scope)

#### Dependencies
net.ladenthin.maven.llamacpp.aiindex.config
