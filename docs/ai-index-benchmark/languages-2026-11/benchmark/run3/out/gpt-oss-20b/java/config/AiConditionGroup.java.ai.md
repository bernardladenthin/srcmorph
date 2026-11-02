### AiConditionGroup.java
- H: 1.0
- C: 66805F67
- D: 2026-11-01T01:56:59Z
- T: 2026-11-01T02:02:52Z
- G: 
- A: 0.0.0
- X: file
---
> Holds a list of child conditions for logical combinators in AI configuration.

#### Purpose
- Stores child `AiCondition` objects for `<and>` / `<or>` combinators.

#### Type
- `public class AiConditionGroup`  
  - Modifiers: `@ToString`, `@SuppressWarnings({"NullAway.Init","initialization.fields.uninitialized"})`  
  - Field: `private @Nullable List<AiCondition> conditions;`

#### Input
- Constructor: none.  
- `setConditions(Collection<AiCondition> conditions)` receives a collection of child conditions.

#### Output
- `getConditions()` returns the stored list or `null`.  
- `setConditions` stores a defensive copy (`ArrayList`) or `null`.

#### Core logic
- Defensive copying of input collection to prevent external mutation.  
- Null handling: if input is `null`, store `null`.

#### Public API
- `AiConditionGroup()` → creates empty group.  
- `List<AiCondition> getConditions()` → child conditions or `null`.  
- `void setConditions(Collection<AiCondition> conditions)` → set child conditions defensively.

#### Dependencies
- `java.util.ArrayList`, `java.util.Collection`, `java.util.List`  
- `lombok.ToString`  
- `org.jspecify.annotations.Nullable`  
- `net.ladenthin.srcmorph.config.AiCondition`

#### Exceptions / Errors
- None declared; defensive copy handles null safely.

#### Concurrency
- No synchronization; class is not thread‑safe.
