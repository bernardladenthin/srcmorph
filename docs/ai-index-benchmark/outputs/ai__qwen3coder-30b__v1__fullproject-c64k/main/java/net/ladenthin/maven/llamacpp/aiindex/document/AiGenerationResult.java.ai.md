### AiGenerationResult.java
- H: 1.0
- C: DE646195
- D: 2026-06-25T05:31:58Z
- T: 2026-06-26T16:01:21Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Represents the outcome of an AI-driven document field generation process, encapsulating the generated content as a structured, immutable value.

#### Purpose
- Encapsulates AI-generated text output from field processing.
- Provides a stable, value-based representation for downstream use.

#### Type
Final class implementing record-like behavior via Lombok annotations. Marked with `@ConvertToRecord` for future migration. No extends or implements clauses.

#### Input
Constructor takes a `String body` parameter; enforced to be non-null via `Objects.requireNonNull`.

#### Output
Public accessor method `body()` returns the immutable `String` field, which may be empty but never null.

#### Core logic
- Validates input `body` is not null during construction.
- Stores AI-generated text in an immutable field.
- Exposes the stored text through a record-style getter method.

#### Public API
- `AiGenerationResult(String body)` → constructor; initializes result with body text
- `String body()` → accessor; retrieves generated body content

#### Dependencies
- `java.util.Objects`
- `lombok.EqualsAndHashCode`
- `lombok.ToString`
- `net.ladenthin.maven.llamacpp.aiindex.support.ConvertToRecord`

#### Exceptions / Errors
Throws `NullPointerException` if constructor argument `body` is null.

#### Concurrency
Immutable design ensures thread-safe access to instance state. No synchronization required.
