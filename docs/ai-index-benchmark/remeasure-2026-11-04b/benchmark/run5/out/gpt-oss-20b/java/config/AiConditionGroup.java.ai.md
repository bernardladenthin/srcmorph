### AiConditionGroup.java
- H: 1.0
- C: 66805F67
- D: 2026-11-04T12:53:23Z
- T: 2026-11-04T12:53:29Z
- G: 
- A: 0.0.0
- X: file
- P: 0B812A3C
---
> Holds a list of child conditions for an AI combinator, enabling <and>/<or> logic in configuration.

#### Purpose
- Stores child `AiCondition` objects for combinator bodies.
- Provides defensive copying on set.

#### Type
- `public class AiConditionGroup`  
  - Modifiers: public, final (implicit), @ToString, @SuppressWarnings.  
  - Field: `private @Nullable List<AiCondition> conditions`.  
  - Constructor: `public AiConditionGroup()` (no-op).  
  - Methods:  
    - `public @Nullable List<AiCondition> getConditions()` – returns stored list or null.  
    - `public void setConditions(final @Nullable Collection<AiCondition> conditions)` – copies into new `ArrayList` or sets null.

#### Input
- Constructor: none.  
- `setConditions`: accepts `Collection<AiCondition>` (nullable).

#### Output
- `getConditions`: returns `List<AiCondition>` or null.  
- `setConditions`: mutates `conditions` field.

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
- No explicit exceptions thrown or caught.

#### Concurrency
- No synchronization; class is not thread‑safe.
