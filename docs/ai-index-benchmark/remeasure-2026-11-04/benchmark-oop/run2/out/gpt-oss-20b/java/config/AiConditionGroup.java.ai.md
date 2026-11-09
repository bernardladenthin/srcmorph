### AiConditionGroup.java
- H: 1.0
- C: 66805F67
- D: 2026-11-04T07:35:21Z
- T: 2026-11-04T07:39:54Z
- G: 
- A: 0.0.0
- X: file
- P: 1879E12B
---
> A container for child AI conditions used by `<and>` / `<or>` combinators in the AI configuration.

#### Purpose
- Holds a list of `AiCondition` objects for combinator bodies.
- Provides defensive copying when setting conditions.

#### Type
- `public class AiConditionGroup`  
  - Modifiers: public, final (implicit), @ToString, @SuppressWarnings({"NullAway.Init","initialization.fields.uninitialized"})  
  - Supertype: `Object`  
  - Fields:  
    - `private @Nullable List<AiCondition> conditions`  
  - Constructors:  
    - `public AiConditionGroup()` – no‑op  
  - Methods:  
    - `public @Nullable List<AiCondition> getConditions()` – returns the list or `null`  
    - `public void setConditions(final @Nullable Collection<AiCondition> conditions)` – defensive copy to `ArrayList` or `null`

#### Input
- Constructor: none
- `setConditions` parameter: `Collection<AiCondition>` (nullable)

#### Output
- `getConditions` returns `List<AiCondition>` or `null`
- `setConditions` mutates the private `conditions` field

#### Core logic
- `setConditions`: if argument non‑null, create new `ArrayList<>(conditions)`; else set field to `null`.
- `getConditions`: simply return the field.

#### Public API
- `getConditions() -> List<AiCondition>` – retrieve child conditions  
- `setConditions(Collection<AiCondition>) -> void` – set child conditions

#### Dependencies
- `java.util.ArrayList`, `java.util.Collection`, `java.util.List`  
- `lombok.ToString`  
- `org.jspecify.annotations.Nullable`  
- `net.ladenthin.srcmorph.config.AiCondition`

#### Exceptions / Errors
- No explicit exceptions thrown; defensive copy handles null safely.

#### Concurrency
- No synchronization; class is not thread‑safe.
