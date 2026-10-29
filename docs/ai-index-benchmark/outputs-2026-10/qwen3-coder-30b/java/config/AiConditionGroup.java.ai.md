### AiConditionGroup.java
- H: 1.0
- C: 66805F67
- D: 2026-10-28T21:24:22Z
- T: 2026-10-28T22:07:53Z
- G: 
- A: 0.0.0
- X: file
---
> Groups child AI conditions for logical combinator operations in configuration.

#### Purpose
- Holds a collection of AI conditions for use in logical AND/OR operations.
- Supports Maven plugin configuration binding with named field for XML serialization.

#### Type
Class, final. Implements no interfaces. Uses Lombok @ToString. Field `conditions` is typed as `@Nullable List<AiCondition>`.

#### Input
- Constructor takes no parameters.
- `setConditions` accepts a `@Nullable Collection<AiCondition>`.

#### Output
- `getConditions` returns a `@Nullable List<AiCondition>`.
- `setConditions` mutates the internal `conditions` field.

#### Core logic
- Stores and retrieves a list of AI conditions.
- Defensively copies input collection to prevent external mutation.
- Handles null input for conditions list.

#### Public API
- `getConditions() -> List<AiCondition>` returns child conditions.
- `setConditions(Collection<AiCondition>)` sets child conditions with defensive copy.

#### Dependencies
- `java.util.ArrayList`
- `java.util.Collection`
- `java.util.List`
- `lombok.ToString`
- `org.jspecify.annotations.Nullable`
- `AiCondition` (referenced type)

#### Exceptions / Errors
- No explicit exceptions thrown.
- Null handling for `conditions` field and input collection.

#### Concurrency
- No concurrency considerations mentioned.
- Field `conditions` is not synchronized; assumes single-threaded access.
