### AiFileContext.java
- H: 1.0
- C: ADE292A5
- D: 2026-06-29T19:51:15Z
- T: 2026-07-02T22:55:42Z
- G: 1.0.3-SNAPSHOT
- A: 0.0.0
- X: file
---
**Facts (exact, whole file):** type declarations: 1; public declarations: 7; TODO/FIXME: 0; @Override: 0; methods (approx): 6; constructors: 1; field declarations (w/ modifier): 5

> Immutable snapshot of a file’s metadata for AI condition evaluation.

#### Purpose
- Holds file facts for AI condition checks.
- Provides read‑only access to metadata.

#### Type
- `final class` `AiFileContext`  
- Lombok `@ToString` annotation.

#### Input
- Constructor parameters: `fileName`, `relativePath`, `sizeBytes`, `lineCount`, `lastModifiedEpochMilli`.

#### Output
- Getter methods exposing each field.

#### Core logic
- Stores constructor arguments in final fields.
- Provides simple accessor methods for each field.

#### Public API
- `fileName() -> String` – returns file name.  
- `relativePath() -> String` – returns base‑relative path.  
- `sizeBytes() -> long` – returns size in bytes.  
- `lineCount() -> int` – returns line count.  
- `lastModifiedEpochMilli() -> long` – returns last‑modified epoch milliseconds.

#### Dependencies
- `lombok.ToString`.

#### Exceptions / Errors
- None.

#### Concurrency
- Immutable; thread‑safe.
