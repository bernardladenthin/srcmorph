### AiMdDocument.java
- H: 1.0
- C: 3B8113EA
- D: 2026-06-25T05:31:58Z
- T: 2026-06-26T16:02:32Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Represents an immutable .ai.md document with structured metadata and content for AI indexing.

#### Purpose
- Stores structured document data for AI processing
- Provides access to metadata and content separately

#### Type
- final class
- Implements @ToString and @EqualsAndHashCode from Lombok
- Marked @ConvertToRecord for future migration

#### Input
- Constructor requires AiMdHeader and String body parameters
- Both parameters must be non-null
- Header parameter is required to be non-null
- Body parameter is required to be non-null

#### Output
- Returns AiMdHeader via header() accessor method
- Returns String body via body() accessor method
- Produces immutable instances with consistent state

#### Core logic
- Validates constructor arguments for null values
- Stores header and body as final fields
- Provides record-style accessors for header and body fields

#### Public API
- AiMdDocument(AiMdHeader, String) -> void Creates new document instance
- header() -> AiMdHeader Retrieves metadata header
- body() -> String Retrieves markdown content

#### Dependencies
- java.util.Objects
- lombok.EqualsAndHashCode
- lombok.ToString
- net.ladenthin.maven.llamacpp.aiindex.support.ConvertToRecord
- net.ladenthin.maven.llamacpp.aiindex.document.AiMdHeader

#### Exceptions / Errors
- Throws NullPointerException if header or body parameters are null
- Null validation occurs in constructor

#### Concurrency
- Immutable design ensures thread safety
- Final fields prevent modification after construction
- No synchronization required for instance usage
