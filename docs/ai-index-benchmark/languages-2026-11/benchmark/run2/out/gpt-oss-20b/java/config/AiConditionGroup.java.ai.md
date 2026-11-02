### AiConditionGroup.java
- H: 1.0
- C: 66805F67
- D: 2026-11-01T01:35:52Z
- T: 2026-11-01T01:40:21Z
- G: 
- A: 0.0.0
- X: file
---
> Holds a list of child conditions for an AI combinator, enabling nested <and>/<or> logic in configuration.

#### Purpose
- Stores child `AiCondition` objects for combinator bodies.
- Provides defensive copying when setting conditions.

#### Type
`public class AiConditionGroup` – class, public, no generics, annotated with `@ToString`, `@SuppressWarnings`.

#### Input
- Constructor: no parameters.
- `setConditions(Collection<AiCondition> conditions)` – accepts a collection of `AiCondition` (nullable).

#### Output
- `getConditions()` returns the internal `List<AiCondition>` (nullable).
- Internal state: `conditions` field holds a defensive copy or `null`.

#### Core logic
- `setConditions`: if input is non‑null, creates a new `ArrayList` from it; otherwise sets field to `null`.
- `getConditions`: simply returns the stored list.

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
- Null handling: accepts `null` for `setConditions`; `getConditions` may return `null`.

#### Concurrency
- No synchronization; class is not thread‑safe.
