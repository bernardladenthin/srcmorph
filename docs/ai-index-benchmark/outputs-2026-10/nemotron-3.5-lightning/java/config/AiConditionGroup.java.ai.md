### AiConditionGroup.java
- H: 1.0
- C: 66805F67
- D: 2026-10-28T22:56:50Z
- T: 2026-10-28T22:57:24Z
- G: 
- A: 0.0.0
- X: file
---
> Manages child AiCondition groups for logical combinators in configuration.

#### Purpose
- Holds child AiCondition instances as a logical combinator group (<and>/<or>) for configuration.
- Binds to Maven XML structure via field-name-derived element naming.

#### Type
- Class: `public class AiConditionGroup`
- Annotations: `@ToString`, `@SuppressWarnings({"NullAway.Init", "initialization.fields.uninitialized"})`
- No type parameters, no extends, no implements

#### Input
- No-arg constructor (initializes empty state)
- `setConditions(Collection<AiCondition>)` parameter for child conditions
- Read field `conditions` of type `@Nullable List<AiCondition>`

#### Output
- `getConditions() -> List<AiCondition>` (may return null) returns child conditions
- `setConditions` mutates internal field with defensive copy

#### Core logic
- Stores conditions in a nullable list field
- Defensively copies input on setConditions to prevent external mutation
- Returns conditions list (possibly null) on getConditions

#### Public API
- `AiConditionGroup()` -> constructs new instance
- `getConditions() -> List<AiCondition>` -> returns child conditions
- `setConditions(Collection<AiCondition>) -> void` -> sets child conditions defensively copied

#### Dependencies
- `java.util.ArrayList`
- `java.util.Collection`
- `java.util.List`
- `lombok.ToString`
- `org.jspecify.annotations.Nullable`
- `net.ladenthin.srcmorph.config.AiCondition`

#### Exceptions / Errors
- `getConditions()` may return null (nullable field)
- `setConditions` stores null if input null; defensive copy otherwise
- No checked exceptions declared

#### Concurrency
- No synchronization or immutability; mutable state `conditions` not thread-safe by default
