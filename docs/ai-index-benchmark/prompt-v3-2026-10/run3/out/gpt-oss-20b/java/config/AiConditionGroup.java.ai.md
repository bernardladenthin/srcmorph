### AiConditionGroup.java
- H: 1.0
- C: 66805F67
- D: 2026-10-31T20:34:56Z
- T: 2026-10-31T20:39:36Z
- G: 
- A: 0.0.0
- X: file
---
> Represents a group of child AI conditions for logical combinators in configuration.

#### Purpose
- Holds a list of child `AiCondition` objects for `<and>` / `<or>` combinators.

#### Type
`public class AiConditionGroup` – class, public, no modifiers, annotated with `@ToString`, `@SuppressWarnings`.

#### Input
- Constructor: no parameters.
- `setConditions(Collection<AiCondition> conditions)` – accepts a collection of `AiCondition` (nullable).

#### Output
- `getConditions()` returns the internal `List<AiCondition>` (nullable).
- Internal state: `conditions` field holds a defensive copy or `null`.

#### Core logic
- `setConditions` copies the provided collection into a new `ArrayList` if non‑null; otherwise sets `conditions` to `null`.
- `getConditions` simply returns the stored list.

#### Public API
- `AiConditionGroup()` – creates an empty group.  
- `List<AiCondition> getConditions()` – retrieve child conditions.  
- `void setConditions(Collection<AiCondition> conditions)` – set child conditions defensively.

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
- Defensive copying ensures external modifications do not affect internal state.
