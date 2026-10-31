### AiConditionGroup.java
- H: 1.0
- C: 66805F67
- D: 2026-10-31T10:06:17Z
- T: 2026-10-31T13:07:32Z
- G: 
- A: 0.0.0
- X: file
---
> Holds a list of child AiCondition objects for <and>/<or> combinators in AI configuration.

#### Purpose
- Represents the body of an <and> or <or> combinator in AiCondition.
- Enables Maven plugin binding of nested conditions.

#### Type
- Class, public, @ToString, @SuppressWarnings({"NullAway.Init","initialization.fields.uninitialized"}).

#### Input
- Constructor: no parameters.
- setConditions(Collection<AiCondition> conditions): accepts a collection of AiCondition, may be null.

#### Output
- getConditions(): returns List<AiCondition> or null.
- setConditions(): stores a defensive copy of the input collection.

#### Core logic
- Defensive copy of input collection into an ArrayList when non‑null.
- Stores null when input is null.

#### Public API
- `AiConditionGroup() -> no-op`  
- `getConditions() -> List<AiCondition> or null`  
- `setConditions(Collection<AiCondition>) -> void`  

#### Dependencies
- java.util.ArrayList, java.util.Collection, java.util.List  
- lombok.ToString  
- org.jspecify.annotations.Nullable  
- net.ladenthin.srcmorph.config.AiCondition

#### Exceptions / Errors
- None thrown; null input handled gracefully.

#### Concurrency
- No synchronization; instance is not thread‑safe.
