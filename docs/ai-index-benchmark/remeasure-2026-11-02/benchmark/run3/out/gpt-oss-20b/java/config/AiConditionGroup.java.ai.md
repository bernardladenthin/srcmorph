### AiConditionGroup.java
- H: 1.0
- C: 66805F67
- D: 2026-11-01T23:56:56Z
- T: 2026-11-02T00:01:03Z
- G: 
- A: 0.0.0
- X: file
- P: C721BDEF
---
> A container for child AI conditions used by <and> / <or> combinators in configuration.

#### Purpose
- Holds a list of child `AiCondition` objects for logical combinators.

#### Type
`public class AiConditionGroup` – class, public, no modifiers, annotated `@ToString`, `@SuppressWarnings`.

#### Input
- Constructor: no parameters.
- `setConditions(Collection<AiCondition> conditions)` – accepts a collection of `AiCondition` (nullable).

#### Output
- `getConditions()` returns the internal `List<AiCondition>` (nullable).
- Internal state: `conditions` field set by `setConditions`.

#### Core logic
- `setConditions` defensively copies the provided collection into a new `ArrayList` or sets to `null` if input is `null`.
- `getConditions` simply returns the stored list reference.

#### Public API
- `AiConditionGroup()` – creates an empty group.  
- `List<AiCondition> getConditions()` – retrieve child conditions.  
- `void setConditions(Collection<AiCondition> conditions)` – set child conditions.

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
