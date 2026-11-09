### AiConditionGroup.java
- H: 1.0
- C: 66805F67
- D: 2026-11-04T11:56:04Z
- T: 2026-11-04T12:00:58Z
- G: 
- A: 0.0.0
- X: file
- P: 0B812A3C
---
> A container for child AI conditions used by `<and>` / `<or>` combinators in the AI configuration.

#### Purpose
- Holds a list of `AiCondition` objects for logical combinators.
- Provides defensive copying when setting the list.

#### Type
- `public class AiConditionGroup`  
  - Modifiers: public, final (implicitly), no superclass.  
  - Annotations: `@ToString`, `@SuppressWarnings({"NullAway.Init", "initialization.fields.uninitialized"})`.  
  - Field: `private @Nullable List<AiCondition> conditions`.  
  - Constructor: `public AiConditionGroup()` (no-op).  
  - Methods:  
    - `public @Nullable List<AiCondition> getConditions()` – returns the list or `null`.  
    - `public void setConditions(final @Nullable Collection<AiCondition> conditions)` – copies into a new `ArrayList` or sets to `null`.

#### Input
- `setConditions` receives a `Collection<AiCondition>` (nullable).  
- Constructor receives no parameters.

#### Output
- `getConditions` returns the internal `List<AiCondition>` or `null`.  
- `setConditions` mutates the private `conditions` field.

#### Core logic
- `setConditions`:  
  - If `conditions` argument is non‑null, create `new ArrayList<>(conditions)` and assign to `this.conditions`.  
  - Else assign `null` to `this.conditions`.

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
- Defensive copying prevents external mutation of the internal list.

#### Concurrency
- No synchronization; class is not thread‑safe.
