### AiConditionGroup.java
- H: 1.0
- C: 66805F67
- D: 2026-11-04T12:56:06Z
- T: 2026-11-04T12:56:12Z
- G: 
- A: 0.0.0
- X: file
- P: 0B812A3C
---
> Holds a list of child AI conditions for logical combinators in the Maven plugin configuration.

#### Purpose
- Stores child `AiCondition` objects for `<and>` / `<or>` combinators.
- Provides defensive copying of the condition list.

#### Type
- `public class AiConditionGroup`  
  - Modifiers: public, final (implicitly), @ToString, @SuppressWarnings({"NullAway.Init","initialization.fields.uninitialized"})  
  - Field: `private @Nullable List<AiCondition> conditions`  
  - Constructor: `public AiConditionGroup()` (no-op)  
  - Methods:  
    - `public @Nullable List<AiCondition> getConditions()`  
    - `public void setConditions(final @Nullable Collection<AiCondition> conditions)`

#### Input
- Constructor: none.  
- `setConditions`: accepts `Collection<AiCondition>` (nullable).

#### Output
- `getConditions`: returns `List<AiCondition>` (nullable).  
- `setConditions`: assigns a defensive copy of the input collection to `conditions`.

#### Core logic
- `setConditions`:  
  - If `conditions` is non‑null, creates `new ArrayList<>(conditions)`; otherwise assigns `null`.  
  - Stores result in the private field `conditions`.

#### Public API
- `getConditions() -> List<AiCondition>` – retrieve child conditions.  
- `setConditions(Collection<AiCondition>) -> void` – set child conditions defensively.

#### Dependencies
- `java.util.ArrayList`, `java.util.Collection`, `java.util.List`  
- `lombok.ToString`  
- `org.jspecify.annotations.Nullable`  
- `net.ladenthin.srcmorph.config.AiCondition`

#### Exceptions / Errors
- No explicit exceptions thrown.  
- Null handling: `@Nullable` annotations indicate parameters and return values may be null.

#### Concurrency
- No synchronization; class is not thread‑safe.
