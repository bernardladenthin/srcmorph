### AiGenerationKind.java
- H: 1.0
- C: 502093E2
- D: 2026-06-25T05:31:58Z
- T: 2026-06-27T05:47:07Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Defines whether AI generation targets individual source files or entire packages

#### Purpose
- Specifies scope of AI-generated content: per-file or per-package  
- Enables routing of generation steps based on context size and output type

#### Type
public enum AiGenerationKind

#### Input
None

#### Output
None

#### Core logic
- No computation; only provides named constants for scope selection  
- Values are statically defined and immutably accessible  

#### Public API
- FILE_SUMMARY(file) → void — generates fields for a single source file  
- PACKAGE_SUMMARY(pkg) → void — generates fields for a package aggregate  

#### Dependencies
AiGenerationKind, net.ladenthin.maven.llamacpp.aiindex.config

#### Exceptions / Errors
None thrown; no runtime error handling

#### Concurrency
Immutable and thread-safe by design
