### AiConditionGroup.java
- H: 1.0
- C: 66805F67
- D: 2026-11-04T07:27:53Z
- T: 2026-11-04T07:32:47Z
- G: 
- A: 0.0.0
- X: file
- P: 36159BF2
---
> A container for child AI conditions used by `<and>` / `<or>` combinators in AI configuration.

#### Purpose
- Holds a list of child `AiCondition` objects for logical combinators.

#### Type
`public class AiConditionGroup` – class, public, no generics, annotated `@ToString`, `@SuppressWarnings({"NullAway.Init","initialization.fields.uninitialized"})`.

#### Input
- Constructor: no parameters.
- `setConditions(Collection<AiCondition> conditions)` – accepts a collection of `AiCondition`, may be `null`.

#### Output
- `getConditions()` returns the internal `List<AiCondition>` or `null`.
- Internal state: `private @Nullable List<AiCondition> conditions` (defensively copied on set).

#### Core logic
- `setConditions` copies the provided collection into a new `ArrayList` if not `null`; otherwise sets field to `null`.
- `getConditions` simply returns the stored list reference.

#### Public API
- `AiConditionGroup()` – create empty group.  
- `List<AiCondition> getConditions()` – retrieve child conditions.  
- `void setConditions(Collection<AiCondition> conditions)` – set child conditions.

#### Dependencies
- `java.util.ArrayList`, `java.util.Collection`, `java.util.List`  
- `lombok.ToString`  
- `org.jspecify.annotations.Nullable`  
- `net.ladenthin.srcmorph.config.AiCondition`

#### Exceptions / Errors
- No checked exceptions thrown.  
- `setConditions` handles `null` gracefully.

#### Concurrency
- No synchronization; class is not thread‑safe.
