### AiConditionGroup.java
- H: 1.0
- C: 66805F67
- D: 2026-11-04T07:09:59Z
- T: 2026-11-04T07:14:33Z
- G: 
- A: 0.0.0
- X: file
- P: 36159BF2
---
> A container for child AI conditions used by <and> / <or> combinators in configuration.

#### Purpose
- Holds a list of child `AiCondition` objects for combinator bodies.

#### Type
`public class AiConditionGroup` – class, public, no modifiers, annotated with `@ToString`, `@SuppressWarnings`.

#### Input
- `setConditions(Collection<AiCondition>)` receives a collection of `AiCondition` instances (nullable).

#### Output
- `getConditions()` returns the internal `List<AiCondition>` (nullable).
- Internal state: `private @Nullable List<AiCondition> conditions`.

#### Core logic
- Constructor: no initialization.
- `setConditions`: if argument non‑null, defensively copies into a new `ArrayList`; otherwise sets field to `null`.
- `getConditions`: returns the stored list or `null`.

#### Public API
- `AiConditionGroup()` → creates empty group  
- `List<AiCondition> getConditions()` → retrieve child conditions  
- `void setConditions(Collection<AiCondition>)` → set child conditions (defensive copy)

#### Dependencies
- `java.util.ArrayList`, `java.util.Collection`, `java.util.List`  
- `lombok.ToString`  
- `org.jspecify.annotations.Nullable`  
- `net.ladenthin.srcmorph.config.AiCondition`

#### Exceptions / Errors
- No checked exceptions thrown.  
- Null handling: `setConditions` accepts `null`, stores `null`; `getConditions` may return `null`.

#### Concurrency
- No synchronization; class is not thread‑safe.  
- Mutability: `setConditions` replaces the list; external callers must not modify the returned list to avoid unintended side effects.
