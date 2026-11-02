### AiConditionGroup.java
- H: 1.0
- C: 66805F67
- D: 2026-11-01T14:58:32Z
- T: 2026-11-01T15:03:51Z
- G: 
- A: 0.0.0
- X: file
---
> Holds a list of child AI conditions for logical combinators.

#### Purpose
- Stores child conditions for `<and>` / `<or>` combinators in AI configuration.

#### Type
`public class AiConditionGroup` – class, public, no modifiers, annotated `@ToString`, `@SuppressWarnings`.

#### Input
- Constructor: no parameters.
- `setConditions(Collection<AiCondition>)`: accepts a collection of `AiCondition` (nullable).

#### Output
- `getConditions()`: returns `List<AiCondition>` (nullable).
- Internal state: `conditions` field holds a defensive copy or `null`.

#### Core logic
- `setConditions`: if input is non‑null, creates a new `ArrayList` copying the collection; otherwise sets field to `null`.
- `getConditions`: simply returns the stored list or `null`.

#### Public API
- `AiConditionGroup()` – constructs an empty group.  
- `List<AiCondition> getConditions()` – retrieve child conditions.  
- `void setConditions(Collection<AiCondition>)` – set child conditions defensively.

#### Dependencies
- `java.util.ArrayList`, `java.util.Collection`, `java.util.List`  
- `lombok.ToString`  
- `org.jspecify.annotations.Nullable`  
- `net.ladenthin.srcmorph.config.AiCondition`

#### Exceptions / Errors
- No checked exceptions thrown.  
- Null handling: accepts `null` for `setConditions`; `getConditions` may return `null`.

#### Concurrency
- No synchronization; class is not thread‑safe.  
- Mutability: `setConditions` replaces the list reference; the returned list is mutable.
