### AiConditionGroup.java
- H: 1.0
- C: 66805F67
- D: 2026-10-28T22:56:50Z
- T: 2026-10-28T23:17:05Z
- G: 
- A: 0.0.0
- X: file
---
> Stores child AI conditions for logical combination in Maven configuration.

#### Purpose
*   Container for child `AiCondition` instances.
*   Enables Maven plugin binding for `<and>` / `<or>` combinator bodies.

#### Type
*   `public class AiConditionGroup`
*   `@ToString` (Lombok)
*   `@SuppressWarnings({"NullAway.Init", "initialization.fields.uninitialized"})`

#### Input
*   Constructor: none.
*   `setConditions`: `Collection<AiCondition>` (nullable).

#### Output
*   `getConditions`: `List<AiCondition>` (nullable).
*   State: `conditions` field (defensively copied list).

#### Core logic
*   Holds `conditions` field as nullable `List<AiCondition>`.
*   `setConditions` creates `ArrayList` copy of input collection.
*   `getConditions` returns internal list reference.

#### Public API
*   `AiConditionGroup()` -> Creates new instance.
*   `getConditions() -> List<AiCondition>` Returns child conditions.
*   `setConditions(Collection<AiCondition>) -> void` Sets child conditions.

#### Dependencies
*   `java.util.ArrayList`
*   `java.util.Collection`
*   `java.util.List`
*   `lombok.ToString`
*   `org.jspecify.annotations.Nullable`
*   `AiCondition`

#### Exceptions / Errors
*   None explicitly thrown.
*   Null handling via `@Nullable`.

#### Concurrency
*   Not thread-safe (mutable list).
