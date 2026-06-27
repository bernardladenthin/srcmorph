### AiMdHeader.java
- H: 1.0
- C: C6F165E5
- D: 2026-06-26T14:41:56Z
- T: 2026-06-26T16:04:12Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Defines a structured header model for AI-generated Markdown documents, separating deterministic metadata from AI content for machine parsing.

#### Purpose
- Represents metadata for .ai.md documents.
- Supports deterministic node comparison and navigation.

#### Type
Class, final. Implements `EqualsAndHashCode`, `ToString`. Annotated with `@ConvertToRecord`.

#### Input
Constructor parameters include title, h, c, d, t, g, a, x, and children. Dependencies: `Collection<String>` for children, `String` fields. Injected or consumed fields: `title`, `h`, `c`, `d`, `t`, `g`, `a`, `x`, `children`.

#### Output
Return types include `String` (title, h, c, d, t, g, a, x) and `List<String>` (children). Mutated fields: `children` stored as unmodifiable list. Side effects: defensive copy of input collection.

#### Core logic
- Enforces non-null constraints on all constructor parameters.
- Stores children in an immutable list after copying input.
- Provides accessor methods for each field.
- Ensures deterministic header comparison via structural fields.

#### Public API
- `AiMdHeader(title, h, c, d, t, g, a, x, children)` → AiMdHeader: Creates header with child links.
- `AiMdHeader(title, h, c, d, t, g, a, x)` → AiMdHeader: Creates header without child links.
- `title()` → String: Returns display title.
- `h()` → String: Returns header version.
- `c()` → String: Returns checksum.
- `d()` → String: Returns source timestamp.
- `t()` → String: Returns generation timestamp.
- `g()` → String: Returns generator version.
- `a()` → String: Returns AI schema version.
- `x()` → String: Returns node type.
- `children()` → List<String>: Returns unmodifiable child links.

#### Dependencies
Imports: `java.util.ArrayList`, `java.util.Collection`, `java.util.Collections`, `java.util.List`, `java.util.Objects`, `lombok.EqualsAndHashCode`, `lombok.ToString`, `net.ladenthin.maven.llamacpp.aiindex.support.ConvertToRecord`.

#### Exceptions / Errors
Throws `NullPointerException` on null inputs. Defensive copying prevents external mutation of children list.

#### Concurrency
No explicit concurrency handling; fields are final and immutable. Thread-safe for read-only access.
