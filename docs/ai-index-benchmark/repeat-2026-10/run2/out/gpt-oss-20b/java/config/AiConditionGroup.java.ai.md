### AiConditionGroup.java
- H: 1.0
- C: 66805F67
- D: 2026-10-29T13:48:14Z
- T: 2026-10-29T14:11:33Z
- G: 
- A: 0.0.0
- X: file
---
> Holds a list of child AiCondition objects for <and>/<or> combinators in AiCondition.

#### Purpose
- Stores child conditions for logical combinators.
- Provides defensive copying on set.

#### Type
- Class, public, non‑abstract, non‑final.
- Annotated with @ToString, @SuppressWarnings.

#### Input
- Constructor: no parameters.
- `setConditions(Collection<AiCondition>)`: optional collection of AiCondition.

#### Output
- `getConditions()`: returns List<AiCondition> or null.
- Internal state: `conditions` field.

#### Core logic
- `setConditions`: if input non‑null, copy into new ArrayList; else set field to null.
- `getConditions`: return field as is.

#### Public API
- `AiConditionGroup()` → constructs empty group.  
- `getConditions() -> List<AiCondition> | null` → returns child list.  
- `setConditions(Collection<AiCondition>) -> void` → sets child list defensively.

#### Dependencies
- `java.util.ArrayList`, `java.util.Collection`, `java.util.List`.
- `lombok.ToString`.
- `org.jspecify.annotations.Nullable`.
- `net.ladenthin.srcmorph.config.AiCondition`.

#### Exceptions / Errors
- No checked exceptions thrown.
- Null handling: accepts null collection, stores null.

#### Concurrency
- No synchronization; class is not thread‑safe.
