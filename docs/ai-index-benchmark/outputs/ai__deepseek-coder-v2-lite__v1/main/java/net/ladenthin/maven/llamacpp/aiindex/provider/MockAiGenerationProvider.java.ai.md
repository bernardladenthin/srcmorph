### MockAiGenerationProvider.java
- H: 1.0
- C: C1880BAD
- D: 2026-06-25T05:31:58Z
- T: 2026-06-27T03:19:41Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> Calculates VAT for invoices

#### Purpose
- To provide a mock implementation of `AiGenerationProvider` for testing purposes.
- To generate a deterministic summary based on an `AiGenerationRequest`.

#### Type
- Class (`public final class MockAiGenerationProvider`)
- Implements `AiGenerationProvider` interface.
- Uses Lombok's `@ToString` annotation for generating string representations.

#### Input
- Constructor parameters: none.
- Method parameters: `request` of type `AiGenerationRequest`.
- Consumed fields: `sourceFile` from `request`.

#### Output
- Return type: `String`.
- Produced state: A mock summary based on the file name and content.
- Side effects: None explicitly stated, but assumes reading from the file system.

#### Core logic
- Retrieves the source file path from the request.
- Extracts the file name from the path.
- Constructs a mock summary string using the file name.

#### Public API
- `MockAiGenerationProvider() -> void`: Constructor for creating a new `MockAiGenerationProvider`.
- `generate(AiGenerationRequest request) -> String`: Generates a mock summary for an invoice based on the provided request.

#### Dependencies
- `java.io.IOException`
- `java.nio.file.Path`
- `lombok.ToString`
- `net.ladenthin.maven.llamacpp.aiindex.document.AiGenerationRequest`
- `net.ladenthin.maven.llamacpp.aiindex.provider.AiGenerationProvider`

#### Exceptions / Errors
- Throws `IOException` if there's an issue reading the file.
- Handles null file names by defaulting to the full path string.

#### Concurrency
- The class is not thread-safe as it directly interacts with the file system and does not manage any shared resources or synchronization mechanisms.
