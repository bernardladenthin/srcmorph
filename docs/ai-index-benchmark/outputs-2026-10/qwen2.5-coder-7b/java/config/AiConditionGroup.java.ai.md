### AiConditionGroup.java
- H: 1.0
- C: 66805F67
- D: 2026-10-29T08:10:04Z
- T: 2026-10-29T08:16:42Z
- G: 
- A: 0.0.0
- X: file
---
> Represents a list of child conditions used in combinator logic within an AI configuration system.

#### Purpose
- Holds a list of child conditions for logical combinators in an AI configuration.

#### Type
- Class
- Public
- Non-final
- Non-abstract
- Non-sealed
- `@ToString`
- `@SuppressWarnings({"NullAway.Init", "initialization.fields.uninitialized"})`

#### Input
- `conditions` (Collection of AiCondition): Child conditions to be set.

#### Output
- `conditions` (Nullable List of AiCondition): Child conditions retrieved.

#### Core logic
- Initializes an empty list of conditions.
- Provides a method to get the list of conditions.
- Provides a method to set the list of conditions, defensively copying the input collection.

#### Public API
- `AiConditionGroup()`: Creates a new AiConditionGroup.
- `getConditions() -> Nullable List of AiCondition`: Returns the child conditions.
- `setConditions(Collection of AiCondition)`: Sets the child conditions.

#### Dependencies
- `java.util.ArrayList`
- `java.util.Collection`
- `java.util.List`
- `lombok.ToString`
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- None explicitly thrown or caught.
- Defensive copy of input collection to prevent null pointer exceptions.

#### Concurrency
- Not thread-safe.
