### AiConditionGroup.java
- H: 1.0
- C: 66805F67
- D: 2026-11-04T12:50:12Z
- T: 2026-11-04T12:50:19Z
- G: 
- A: 0.0.0
- X: file
- P: 0B812A3C
---
> Holds a list of child conditions for an AI condition combinator.

#### Purpose
- Stores child `AiCondition` objects for `<and>` / `<or>` combinators.
- Provides defensive copying of the list.

#### Type
- `public class AiConditionGroup`  
  - Modifiers: public, final (implicit), no superclass.  
  - Field: `private @Nullable List<AiCondition> conditions`.  
  - Constructor: `public AiConditionGroup()`.  
  - Methods:  
    - `public @Nullable List<AiCondition> getConditions()`.  
    - `public void setConditions(final @Nullable Collection<AiCondition> conditions)`.

#### Input
- Constructor: none.  
- `setConditions`: a `Collection<AiCondition>` (nullable).

#### Output
- `getConditions`: returns the internal `List<AiCondition>` or `null`.  
- `setConditions`: assigns a defensive copy of the input collection to `conditions`.

#### Core logic
- `setConditions`:  
  - If input `conditions` is non‑null, create `new ArrayList<>(conditions)` and assign to field.  
  - Else assign `null` to field.

#### Public API
- `getConditions() -> List<AiCondition>` – retrieve child conditions.  
- `setConditions(Collection<AiCondition>) -> void` – set child conditions.

#### Dependencies
- `java.util.ArrayList`, `java.util.Collection`, `java.util.List`.  
- `lombok.ToString`.  
- `org.jspecify.annotations.Nullable`.  
- `net.ladenthin.srcmorph.config.AiCondition`.

#### Exceptions / Errors
- No checked exceptions thrown.  
- No explicit null checks beyond defensive copying.

#### Concurrency
- No synchronization; class is not thread‑safe.
