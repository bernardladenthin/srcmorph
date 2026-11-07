### AiConditionGroup.java
- H: 1.0
- C: 66805F67
- D: 2026-11-04T07:43:16Z
- T: 2026-11-04T07:47:39Z
- G: 
- A: 0.0.0
- X: file
- P: 36159BF2
---
> A container for child AI conditions used by <and> / <or> combinators in configuration.

#### Purpose
- Holds a list of child `AiCondition` objects for logical combinators.

#### Type
`public class AiConditionGroup` – class, public, no modifiers, annotated `@ToString`, `@SuppressWarnings`.

#### Input
- Constructor: no parameters.
- `setConditions(Collection<AiCondition>)`: accepts a collection of `AiCondition` (nullable).

#### Output
- `getConditions()`: returns the internal `List<AiCondition>` (nullable).
- Internal state: `conditions` list, defensively copied on set.

#### Core logic
- `setConditions`: if argument non‑null, creates a new `ArrayList` from it; otherwise sets field to `null`.
- `getConditions`: simply returns the stored list.

#### Public API
- `AiConditionGroup()` → creates empty group  
- `List<AiCondition> getConditions()` → child conditions or null  
- `void setConditions(Collection<AiCondition>)` → set child conditions

#### Dependencies
- `java.util.ArrayList`, `java.util.Collection`, `java.util.List`  
- `lombok.ToString`  
- `org.jspecify.annotations.Nullable`  
- `net.ladenthin.srcmorph.config.AiCondition`

#### Exceptions / Errors
- No checked exceptions thrown.  
- Null handling: accepts and returns `null` for empty configuration.

#### Concurrency
- No synchronization; instance is not thread‑safe.
