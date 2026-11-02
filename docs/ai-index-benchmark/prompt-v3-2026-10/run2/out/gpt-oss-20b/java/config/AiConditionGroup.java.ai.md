### AiConditionGroup.java
- H: 1.0
- C: 66805F67
- D: 2026-10-31T20:10:35Z
- T: 2026-10-31T20:14:56Z
- G: 
- A: 0.0.0
- X: file
---
> Holds a list of child AiCondition objects for <and>/<or> combinators in configuration.

#### Purpose
- Represents the body of an <and> or <or> combinator in AiCondition.
- Provides defensive copying of child conditions.

#### Type
`public class AiConditionGroup` – class, public, no modifiers, annotated with `@ToString`, `@SuppressWarnings`.

#### Input
- Constructor: no parameters.
- `setConditions(Collection<AiCondition> conditions)` – accepts a collection of AiCondition, may be null.

#### Output
- `getConditions()` returns the internal list or null.
- Internal state: `private @Nullable List<AiCondition> conditions`.

#### Core logic
- Constructor: no-op.
- `setConditions`: if input is non‑null, creates a new `ArrayList<>(conditions)`; otherwise sets field to null.
- `getConditions`: returns the field as is.

#### Public API
- `AiConditionGroup()` – constructs an empty group.
- `List<AiCondition> getConditions()` – returns child conditions or null.
- `void setConditions(Collection<AiCondition> conditions)` – sets child conditions defensively.

#### Dependencies
- `java.util.ArrayList`
- `java.util.Collection`
- `java.util.List`
- `lombok.ToString`
- `org.jspecify.annotations.Nullable`
- `net.ladenthin.srcmorph.config.AiCondition`

#### Exceptions / Errors
- No checked exceptions thrown.
- Null handling: `setConditions` accepts null and stores null; `getConditions` may return null.

#### Concurrency
- No synchronization; class is not thread‑safe.
