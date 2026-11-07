### AiConditionGroup.java
- H: 1.0
- C: 66805F67
- D: 2026-11-04T07:19:50Z
- T: 2026-11-04T07:24:30Z
- G: 
- A: 0.0.0
- X: file
- P: 1879E12B
---
> A container for child AI conditions used by <and> / <or> combinators in the AI configuration.

#### Purpose
- Holds a list of `AiCondition` objects for logical combinators.
- Provides defensive copying when setting the list.

#### Type
- `public class AiConditionGroup`  
  - Modifiers: public, final (implicit), no superclass.  
  - Field: `private @Nullable List<AiCondition> conditions`.  
  - Constructor: `public AiConditionGroup()` (no-op).  
  - Methods:  
    - `public @Nullable List<AiCondition> getConditions()` – returns the list or null.  
    - `public void setConditions(final @Nullable Collection<AiCondition> conditions)` – copies into a new `ArrayList` or sets null.

#### Input
- `setConditions` accepts a `Collection<AiCondition>` (nullable).

#### Output
- `getConditions` returns the internal `List<AiCondition>` or null.
- `setConditions` mutates the internal `conditions` field.

#### Core logic
- `setConditions`: if argument is non‑null, create a new `ArrayList` from it; otherwise set field to null.
- `getConditions`: simply return the field.

#### Public API
- `getConditions() -> List<AiCondition>` – retrieve child conditions.  
- `setConditions(Collection<AiCondition>) -> void` – set child conditions.

#### Dependencies
- `java.util.ArrayList`, `java.util.Collection`, `java.util.List`  
- `lombok.ToString`  
- `org.jspecify.annotations.Nullable`  
- `net.ladenthin.srcmorph.config.AiCondition`

#### Exceptions / Errors
- No exceptions thrown; defensive copying handles nulls.

#### Concurrency
- No synchronization; class is not thread‑safe.
