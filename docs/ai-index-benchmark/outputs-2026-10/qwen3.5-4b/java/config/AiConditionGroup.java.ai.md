### AiConditionGroup.java
- H: 1.0
- C: 66805F67
- D: 2026-10-29T00:01:31Z
- T: 2026-10-29T00:01:51Z
- G: 
- A: 0.0.0
- X: file
---
> Aggregates child AI conditions into a structured group for use as the body of AND/OR combinator nodes in the AI condition configuration model.

#### Purpose
- Represents a container for child `AiCondition` elements used within `AiCondition` AND/OR combinator nodes.
- Provides defensive copying when setting the internal list of conditions to prevent external mutation.

#### Type
- `public class AiConditionGroup`
- Modifiers: `@ToString`, `@SuppressWarnings`
- Extends: none
- Implements: none
- Key generics: `List<AiCondition>`, `Collection<AiCondition>`
- Annotations: `@ToString`, `@SuppressWarnings`

#### Input
- `AiConditionGroup()` constructor: none
- `setConditions(Collection<AiCondition>)`: accepts a collection of `AiCondition` objects
- `getConditions()`: reads the internal `conditions` field

#### Output
- `getConditions()`: returns `List<AiCondition>` or `null`
- `setConditions()`: updates internal `conditions` field with a defensive copy or `null`
- No external resources produced or consumed

#### Core logic
- Initializes internal `conditions` field to `null` by default.
- `setConditions()` creates a new `ArrayList` from the input collection if not null, otherwise sets `conditions` to `null`.
- `getConditions()` returns the current state of the `conditions` list.

#### Public API
- `AiConditionGroup()` -> void
- `getConditions()` -> `List<AiCondition>`
- `setConditions(Collection<AiCondition>)` -> void

#### Dependencies
- `java.util.ArrayList`
- `java.util.Collection`
- `java.util.List`
- `lombok.ToString`
- `org.jspecify.annotations.Nullable`
- `AiCondition`

#### Exceptions / Errors
- No exceptions thrown.
- Null handling: `setConditions()` safely handles `null` input by setting internal field to `null`.
- No error conditions defined.

#### Concurrency
- Not thread-safe; internal state is mutable.
- No synchronization or immutability guarantees.
