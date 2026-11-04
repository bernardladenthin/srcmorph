### AiConditionGroup.java
- H: 1.0
- C: 66805F67
- D: 2026-11-04T07:50:25Z
- T: 2026-11-04T07:54:34Z
- G: 
- A: 0.0.0
- X: file
- P: 1879E12B
---
> A container for child AI conditions used by the `<and>` / `<or>` combinators in the AI configuration.

#### Purpose
- Holds a list of `AiCondition` objects for combinator bodies.
- Provides defensive copying when setting the list.

#### Type
- `public class AiConditionGroup`  
  - Modifiers: public, final (implicit), no superclass.  
  - Fields: `private @Nullable List<AiCondition> conditions`.  
  - Annotations: `@ToString`, `@SuppressWarnings({"NullAway.Init","initialization.fields.uninitialized"})`.

#### Input
- Constructor: no parameters.  
- `setConditions(Collection<AiCondition> conditions)` receives a nullable collection of `AiCondition`.

#### Output
- `getConditions()` returns the internal `List<AiCondition>` or `null`.  
- `setConditions` stores a defensive copy (`new ArrayList<>(conditions)`) or `null`.

#### Core logic
- `setConditions`:  
  1. If `conditions` is non‑null, create a new `ArrayList` from it.  
  2. Assign the new list to the field; otherwise assign `null`.

#### Public API
- `AiConditionGroup()` → constructs an empty group.  
- `List<AiCondition> getConditions()` → child conditions or null.  
- `void setConditions(Collection<AiCondition> conditions)` → set child conditions defensively.

#### Dependencies
- `java.util.ArrayList`, `java.util.Collection`, `java.util.List`.  
- `lombok.ToString`.  
- `org.jspecify.annotations.Nullable`.  
- `net.ladenthin.srcmorph.config.AiCondition`.

#### Exceptions / Errors
- No explicit exceptions thrown; defensive copy handles null safely.

#### Concurrency
- No synchronization; the class is not thread‑safe.
