### AiConditionGroup.java
- H: 1.0
- C: 66805F67
- D: 2026-11-01T01:13:43Z
- T: 2026-11-01T01:17:55Z
- G: 
- A: 0.0.0
- X: file
---
> Holds a list of child conditions for an <and> / <or> combinator in AI configuration.

#### Purpose
- Represents the body of an `<and>` or `<or>` combinator in `AiCondition`.
- Enables Maven plugin binding of child conditions via `<conditions>` element.

#### Type
`public class AiConditionGroup` – class, public, no modifiers, annotated with `@ToString`, `@SuppressWarnings({"NullAway.Init","initialization.fields.uninitialized"})`.

#### Input
- Constructor: no parameters.
- `setConditions(Collection<AiCondition> conditions)` – accepts a collection of `AiCondition` (nullable).

#### Output
- `getConditions()` returns a `List<AiCondition>` (nullable).
- Internal field `conditions` holds a defensive copy of the input collection.

#### Core logic
- `setConditions`: if input is non‑null, creates a new `ArrayList` from it; otherwise sets field to `null`.
- `getConditions`: simply returns the stored list (may be `null`).

#### Public API
- `AiConditionGroup()` – constructs an empty group.  
- `List<AiCondition> getConditions()` – retrieve child conditions.  
- `void setConditions(Collection<AiCondition> conditions)` – set child conditions defensively.

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
- Immutable defensive copy ensures external modifications do not affect internal state.
