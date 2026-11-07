### AiConditionGroup.java
- H: 1.0
- C: 66805F67
- D: 2026-11-04T12:04:37Z
- T: 2026-11-04T12:09:12Z
- G: 
- A: 0.0.0
- X: file
- P: 0B812A3C
---
> Holds a list of child conditions for an AI combinator, enabling <and>/<or> logic in configuration.

#### Purpose
- Stores child `AiCondition` objects for combinator elements.
- Provides defensive copying when setting the list.

#### Type
- `public class AiConditionGroup`  
  - Modifiers: public, final (implicitly), no superclass.  
  - Field: `private @Nullable List<AiCondition> conditions`.  
  - Constructor: `public AiConditionGroup()`.  
  - Methods:  
    - `public @Nullable List<AiCondition> getConditions()`.  
    - `public void setConditions(final @Nullable Collection<AiCondition> conditions)`.

#### Input
- Constructor: none.  
- `setConditions`: accepts a `Collection<AiCondition>` (nullable).

#### Output
- `getConditions`: returns the internal `List<AiCondition>` or `null`.  
- `setConditions`: assigns a defensive copy of the provided collection to `conditions`.

#### Core logic
- `setConditions` checks if the argument is `null`; if not, creates a new `ArrayList<>(conditions)` and assigns it to the field.

#### Public API
- `getConditions() -> List<AiCondition>` – retrieve child conditions.  
- `setConditions(Collection<AiCondition>) -> void` – set child conditions.

#### Dependencies
- `java.util.ArrayList`, `java.util.Collection`, `java.util.List`.  
- `lombok.ToString`.  
- `org.jspecify.annotations.Nullable`.  
- `net.ladenthin.srcmorph.config.AiCondition`.

#### Exceptions / Errors
- No explicit exceptions thrown.  
- Defensive copy prevents external mutation of internal list.

#### Concurrency
- No synchronization; class is not thread‑safe.
