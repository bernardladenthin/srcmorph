### AiConditionGroup.java
- H: 1.0
- C: 66805F67
- D: 2026-11-01T23:42:40Z
- T: 2026-11-01T23:47:06Z
- G: 
- A: 0.0.0
- X: file
- P: C721BDEF
---
> A container for child AI conditions used by <and> / <or> combinators in configuration.

#### Purpose
- Holds a list of child `AiCondition` objects for combinator bodies.  
- Enables Maven plugin binding of `<conditions>` elements.

#### Type
`public class AiConditionGroup` – class, public, no modifiers, annotated `@ToString`, `@SuppressWarnings`.

#### Input
- Constructor: no parameters.  
- `setConditions(Collection<AiCondition>)`: accepts a collection of `AiCondition` or `null`.  
- Field `conditions` is initially `null`.

#### Output
- `getConditions()`: returns the internal `List<AiCondition>` or `null`.  
- `setConditions` copies the input collection into a new `ArrayList` or sets `null`.

#### Core logic
- `setConditions`: if input is non‑null, creates a defensive `ArrayList` copy; otherwise assigns `null`.  
- `getConditions`: simply returns the stored list reference.

#### Public API
- `AiConditionGroup()` → constructs empty group.  
- `List<AiCondition> getConditions()` → retrieve child conditions.  
- `void setConditions(Collection<AiCondition>)` → set child conditions defensively.

#### Dependencies
- `java.util.ArrayList`, `java.util.Collection`, `java.util.List`  
- `lombok.ToString`  
- `org.jspecify.annotations.Nullable`  
- `net.ladenthin.srcmorph.config.AiCondition`

#### Exceptions / Errors
- No checked exceptions thrown.  
- `setConditions` handles `null` input gracefully.

#### Concurrency
- No synchronization; class is not thread‑safe.  
- Internal list is mutable; callers should not modify the returned list directly.
