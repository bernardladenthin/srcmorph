### AiGenerationKind.java
- H: 1.0
- C: 502093E2
- D: 2026-06-25T05:31:58Z
- T: 2026-06-26T20:22:46Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Identifies whether an AI generation operates on a single source file or a whole package.

#### Purpose
- Enumerates the kinds of AI generation steps.
- Defines constants for summarizing individual files and packages.

#### Type
```java
enum AiGenerationKind
```

#### Core logic
- `FILE_SUMMARY`: Indicates that the AI generation step produces fields for a single source file.
- `PACKAGE_SUMMARY`: Indicates that the AI generation step produces fields for a package aggregate.
