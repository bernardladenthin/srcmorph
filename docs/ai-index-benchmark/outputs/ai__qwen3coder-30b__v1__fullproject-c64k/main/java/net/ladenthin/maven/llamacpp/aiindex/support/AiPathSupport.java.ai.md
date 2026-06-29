### AiPathSupport.java
- H: 1.0
- C: 09DC92FB
- D: 2026-06-25T05:31:58Z
- T: 2026-06-26T17:06:19Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Resolves and cleans source file paths for indexing by removing leading "src" segments.

#### Purpose
- Normalizes file paths for consistent indexing.
- Strips redundant directory prefixes from source paths.

#### Type
class AiPathSupport modifiers: public
extends: none
implements: none
generics: none
annotations: @ToString

#### Input
- Constructor: no parameters
- Method `relativizeFromSrc`: baseDirectory (Path), path (Path)

#### Output
- Return type: Path
- Side effect: none
- Mutated state: none

#### Core logic
- Relativizes input path against base directory.
- Checks if the relativized path starts with "src" segment.
- Removes the leading "src" segment if present.

#### Public API
relativizeFromSrc(baseDirectory, path) -> Path strips leading src from relativized path

#### Dependencies
java.nio.file.Path
lombok.ToString

#### Exceptions / Errors
None explicitly handled; relies on standard Path operations.

#### Concurrency
No concurrency concerns; class is stateless and immutable.
