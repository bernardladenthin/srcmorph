### AiConditionGroup.java
- H: 1.0
- C: 66805F67
- D: 2026-10-28T21:24:22Z
- T: 2026-10-28T22:48:15Z
- G: 
- A: 0.0.0
- X: file
---
> Holds a list of child AI conditions for logical combinators in configuration.

#### Purpose
- Stores child conditions for `<and>` / `<or>` combinators in AI configuration.

#### Type
- Class, public, non‑abstract, non‑final.  
- Annotated with `@ToString` and `@SuppressWarnings({"NullAway.Init","initialization.fields.uninitialized"})`.

#### Input
- Constructor: no parameters.  
- `setConditions(Collection<AiCondition>)`: accepts a collection of `AiCondition` or `null`.

#### Output
- `getConditions()`: returns the internal `List<AiCondition>` or `null`.  
- Internal state: `conditions` field holds a defensive copy of the provided collection.

#### Core logic
- `setConditions` creates a new `ArrayList` from the supplied collection if not `null`; otherwise sets `conditions` to `null`.  
- `getConditions` simply returns the stored list.

#### Public API
- `AiConditionGroup() -> void` – creates an empty group.  
- `getConditions() -> List<AiCondition> | null` – retrieves child conditions.  
- `setConditions(Collection<AiCondition>) -> void` – assigns child conditions defensively.

#### Dependencies
- `java.util.ArrayList`, `java.util.Collection`, `java.util.List`.  
- `lombok.ToString`.  
- `org.jspecify.annotations.Nullable`.  
- `net.ladenthin.srcmorph.config.AiCondition`.

#### Exceptions / Errors
- No checked exceptions thrown.  
- `setConditions` handles `null` gracefully.

#### Concurrency
- No synchronization; class is not thread‑safe.  
- Immutable after construction if `setConditions` is not called.
