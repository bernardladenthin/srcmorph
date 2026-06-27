### MockAiGenerationProvider.java
- H: 1.0
- C: C1880BAD
- D: 2026-06-25T05:31:58Z
- T: 2026-06-27T03:50:46Z
- G: 1.0.1-SNAPSHOT
- A: 0.0.0
- X: file
---
> MockAiGenerationProvider is a deterministic test-only implementation of AiGenerationProvider that returns a mock summary for testing purposes.

#### Purpose
- Provide a mock AI generation summary for testing purposes.

#### Type
```java
public class MockAiGenerationProvider implements AiGenerationProvider {
```

#### Input
- `final AiGenerationRequest request`: The AI generation request containing the source file to be summarized.

#### Output
- `String`: A mock summary string for the given file name.

#### Core logic
1. Extract the source file path from the request.
2. Retrieve the file name from the path.
3. Construct a mock summary string using the file name.

#### Public API
```java
public String generate(final AiGenerationRequest request) -> String {
    // Returns a mock summary for the given file.
}
```

#### Dependencies
- `net.ladenthin.maven.llamacpp.aiindex.document.AiGenerationRequest`
- `java.nio.file.Path`

#### Exceptions / Errors
- Throws `IOException`: If there is an issue reading the file or processing the request.

#### Concurrency
- This class is thread-safe as it does not maintain any state that would be affected by concurrent access.
