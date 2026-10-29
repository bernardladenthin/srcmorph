### AiConditionGroup.java
- H: 1.0
- C: 66805F67
- D: 2026-10-29T04:50:45Z
- T: 2026-10-29T04:52:05Z
- G: 
- A: 0.0.0
- X: file
---
> Manages a list of AiCondition objects for AND/OR combinators in configuration.

#### Purpose
- Represents a group of AiCondition instances used in AND/OR logical expressions.
- Provides getter/setter for the conditions list.

#### Type
- Class `AiConditionGroup`.
- Public, non-final, non-abstract.
- Implements `@ToString` from Lombok.

#### Input
- `Collection<AiCondition> conditions` in `setConditions` method.

#### Output
- `List<AiCondition> getConditions()` returns a defensive copy of the conditions list.

#### Core logic
- Stores conditions in a private `List<AiCondition>`.
- `setConditions` defensively copies the input collection to protect against external mutation.
- `getConditions` returns the internal list directly, allowing external mutation of the returned list.

#### Public API
- `AiConditionGroup()` -> `void`: Default constructor.
- `getConditions()` -> `List<AiCondition>`: Returns the conditions list.
- `setConditions(Collection<AiCondition>)` -> `void`: Sets the conditions list.

#### Dependencies
- `java.util.ArrayList`
- `java.util.Collection`
- `java.util.List`
- `lombok.ToString`
- `org.jspecify.annotations.Nullable`
- `AiCondition`

#### Exceptions / Errors
- None explicitly thrown.

#### Concurrency
- `getConditions` returns a defensive copy to prevent external mutation.
- `setConditions` accepts a `Collection` to allow flexibility in the input type.
