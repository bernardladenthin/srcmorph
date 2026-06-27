### AiGenerationRequest.java
- H: 1.0
- C: BE482ECE
- D: 2026-06-25T05:31:58Z
- T: 2026-06-26T16:00:34Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Encapsulates input for AI content generation requests, including prompt template, source file, and header data.

#### Purpose
- Carries immutable data for AI generation tasks.
- Supports mapping of source files to AI prompt templates and headers.

#### Type
Final class implementing value semantics via Lombok annotations. Implements `EqualsAndHashCode`, `ToString`. No interfaces or generics.

#### Input
Constructor takes: `String promptKey`, `Path sourceFile`, `String sourceText`, `AiMdHeader currentHeader`. All parameters are required and validated with `Objects.requireNonNull`.

#### Output
Public accessors return: `promptKey`, `sourceFile`, `sourceText`, `currentHeader` as declared types.

#### Core logic
- Constructs immutable request object from four fields.
- Provides record-style accessors for all fields.
- Enforces non-null inputs at construction time.

#### Public API
- `promptKey() -> String` retrieves prompt template identifier
- `sourceFile() -> Path` retrieves source file path
- `sourceText() -> String` retrieves source file content
- `currentHeader() -> AiMdHeader` retrieves current AI markdown header

#### Dependencies
Imports: `java.nio.file.Path`, `java.util.Objects`, `lombok.EqualsAndHashCode`, `lombok.ToString`, `net.ladenthin.maven.llamacpp.aiindex.support.ConvertToRecord`, `net.ladenthin.maven.llamacpp.aiindex.document.AiMdHeader`

#### Exceptions / Errors
Throws `NullPointerException` if any constructor argument is null. Null-checks enforced via `Objects.requireNonNull`.

#### Concurrency
Immutable design ensures thread-safe usage without synchronization.
