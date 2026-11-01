### AiConditionGroup.java
- H: 1.0
- C: 66805F67
- D: 2026-11-01T14:19:41Z
- T: 2026-11-01T14:24:33Z
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
- `setConditions(Collection<AiCondition>)`: accepts a collection of `AiCondition` (nullable).

#### Output
- `getConditions()`: returns the internal list (nullable).
- Internal state: `conditions` field (nullable `List<AiCondition>`).

#### Core logic
- `setConditions`: if argument non‑null, creates a defensive `ArrayList` copy; otherwise sets field to `null`.
- `getConditions`: simply returns the stored list.

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
- Null handling: `setConditions` accepts `null` and stores `null`; `getConditions` may return `null`.

#### Concurrency
- No synchronization; class is not thread‑safe.
