### AiConditionGroup.java
- H: 1.0
- C: 66805F67
- D: 2026-11-01T23:26:17Z
- T: 2026-11-01T23:30:35Z
- G: 
- A: 0.0.0
- X: file
- P: C721BDEF
---
> A container for child AI conditions used by `<and>` / `<or>` combinators in the AI configuration.

#### Purpose
- Holds a list of child `AiCondition` objects for logical combinators.

#### Type
`public class AiConditionGroup` – class, public, no generics, annotated with `@ToString`, `@SuppressWarnings`.

#### Input
- Constructor: no parameters.
- `setConditions(Collection<AiCondition>)`: accepts a collection of `AiCondition` (nullable).

#### Output
- `getConditions()`: returns the internal `List<AiCondition>` (nullable).
- `setConditions`: stores a defensive copy of the provided collection or `null`.

#### Core logic
- `setConditions`: if argument is non‑null, creates a new `ArrayList` from it; otherwise sets internal list to `null`.
- `getConditions`: simply returns the internal list reference.

#### Public API
- `AiConditionGroup()` → creates empty group  
- `getConditions() -> List<AiCondition>` → retrieve child conditions  
- `setConditions(Collection<AiCondition>) -> void` → set child conditions

#### Dependencies
- `java.util.ArrayList`, `java.util.Collection`, `java.util.List`  
- `lombok.ToString`  
- `org.jspecify.annotations.Nullable`  
- `net.ladenthin.srcmorph.config.AiCondition`

#### Exceptions / Errors
- No checked exceptions thrown.  
- Null handling: `setConditions` accepts `null` and stores `null`; `getConditions` may return `null`.

#### Concurrency
- No synchronization; class is not thread‑safe.  
- Internal list is mutable; callers receive the reference from `getConditions`.
