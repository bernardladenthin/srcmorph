### AiConditionGroup.java
- H: 1.0
- C: 66805F67
- D: 2026-10-31T19:47:56Z
- T: 2026-10-31T19:52:26Z
- G: 
- A: 0.0.0
- X: file
---
> Holds a list of child conditions for an <and> / <or> combinator in AI configuration.

#### Purpose
- Represents the body of an `<and>` or `<or>` combinator in AI condition XML.
- Enables Maven plugin binding of child `<condition>` elements.

#### Type
`public class AiConditionGroup` – class, public, no modifiers, annotated with `@ToString`, `@SuppressWarnings`.

#### Input
- Constructor: no parameters.
- `setConditions(Collection<AiCondition>)`: accepts a collection of child conditions, may be `null`.

#### Output
- `getConditions()`: returns the internal list or `null`.
- Internal state: `conditions` list, defensively copied on set.

#### Core logic
- `setConditions`: if input is non‑null, creates a new `ArrayList` copy; otherwise sets field to `null`.
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
- Null handling: `setConditions` accepts `null` and stores `null`; `getConditions` may return `null`.

#### Concurrency
- No synchronization; class is not thread‑safe.  
- Mutability: `conditions` can be changed via `setConditions`.
