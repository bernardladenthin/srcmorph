### AiConditionGroup.java
- H: 1.0
- C: 66805F67
- D: 2026-10-29T14:19:17Z
- T: 2026-10-29T14:51:53Z
- G: 
- A: 0.0.0
- X: file
---
> Holds a list of child AiCondition objects for <and>/<or> combinators in configuration.

#### Purpose
- Represents the body of an `<and>` or `<or>` combinator in AiCondition.
- Enables Maven plugin binding of child conditions via a `conditions` collection.

#### Type
- Class `AiConditionGroup`  
- Modifiers: public, @ToString, @SuppressWarnings({"NullAway.Init","initialization.fields.uninitialized"})

#### Input
- Constructor: no parameters.
- `setConditions(Collection<AiCondition> conditions)` receives a collection of AiCondition, may be null.

#### Output
- `getConditions()` returns the internal `List<AiCondition>` or null.
- Internal state: `conditions` list, defensively copied on set.

#### Core logic
- `setConditions` copies the provided collection into a new `ArrayList` if not null; otherwise sets internal list to null.
- `getConditions` simply returns the internal list reference.

#### Public API
- `AiConditionGroup()` → constructs an empty group.  
- `List<AiCondition> getConditions()` → retrieves child conditions.  
- `void setConditions(Collection<AiCondition> conditions)` → assigns child conditions.

#### Dependencies
- `java.util.ArrayList`, `java.util.Collection`, `java.util.List`  
- `lombok.ToString`  
- `org.jspecify.annotations.Nullable`  
- `net.ladenthin.srcmorph.config.AiCondition`

#### Exceptions / Errors
- No checked exceptions thrown.  
- Null handling: accepts null in `setConditions`, propagates null via `getConditions`.

#### Concurrency
- No synchronization; class is not thread‑safe.  
- Mutability: `setConditions` replaces the list reference; callers must not modify the returned list directly.
