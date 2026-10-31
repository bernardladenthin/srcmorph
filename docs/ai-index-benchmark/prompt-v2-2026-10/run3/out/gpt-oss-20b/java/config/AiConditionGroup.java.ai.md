### AiConditionGroup.java
- H: 1.0
- C: 66805F67
- D: 2026-10-31T14:57:29Z
- T: 2026-10-31T15:02:10Z
- G: 
- A: 0.0.0
- X: file
---
> Holds a list of child AI conditions for logical combinators in configuration.

#### Purpose
- Stores child conditions for `<and>` / `<or>` combinators in AI configuration.

#### Type
`public class AiConditionGroup` – class, public, no modifiers.

#### Input
- Constructor: no parameters.
- `setConditions(Collection<AiCondition>)` – optional collection of child conditions.

#### Output
- `getConditions()` returns `List<AiCondition>` or `null`.
- Internal field `conditions` holds a defensive copy of the input collection.

#### Core logic
- `setConditions` copies the provided collection into a new `ArrayList` if non‑null; otherwise sets field to `null`.
- `getConditions` simply returns the stored list reference.

#### Public API
- `AiConditionGroup()` – creates an empty group.  
- `List<AiCondition> getConditions()` – retrieve child conditions.  
- `void setConditions(Collection<AiCondition>)` – set child conditions.

#### Dependencies
- `java.util.ArrayList`, `java.util.Collection`, `java.util.List`  
- `lombok.ToString`  
- `org.jspecify.annotations.Nullable`  
- `net.ladenthin.srcmorph.config.AiCondition`

#### Exceptions / Errors
- No checked exceptions thrown.  
- Defensive copying prevents external mutation of the internal list.

#### Concurrency
- No synchronization; class is not thread‑safe.  
- Immutable after construction if `setConditions` is not called.
