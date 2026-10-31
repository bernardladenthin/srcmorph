### AiConditionGroup.java
- H: 1.0
- C: 66805F67
- D: 2026-10-31T14:30:30Z
- T: 2026-10-31T14:35:15Z
- G: 
- A: 0.0.0
- X: file
---
> Holds a list of child conditions for an <and> / <or> combinator in AI configuration.

#### Purpose
- Represents the body of an `<and>` or `<or>` combinator in `AiCondition`.
- Provides a container for child `AiCondition` instances.

#### Type
`public class AiConditionGroup` – class, public, no modifiers, annotated with `@ToString`, `@SuppressWarnings`.

#### Input
- Constructor: no parameters.
- `setConditions(Collection<AiCondition> conditions)` – accepts a collection of child conditions (nullable).

#### Output
- `getConditions()` returns the stored list (nullable).
- Internal field `conditions` holds a defensive copy of the input collection.

#### Core logic
- `setConditions`: if input is non‑null, creates a new `ArrayList` copying the collection; otherwise sets field to `null`.
- `getConditions`: simply returns the field value.

#### Public API
- `AiConditionGroup()` – constructs an empty group.  
- `List<AiCondition> getConditions()` – retrieves child conditions.  
- `void setConditions(Collection<AiCondition> conditions)` – assigns child conditions defensively.

#### Dependencies
- `java.util.ArrayList`, `java.util.Collection`, `java.util.List`  
- `lombok.ToString`  
- `org.jspecify.annotations.Nullable`  
- `net.ladenthin.srcmorph.config.AiCondition`

#### Exceptions / Errors
- No checked exceptions thrown.  
- Null handling: methods accept and return `@Nullable` values.

#### Concurrency
- No synchronization; class is not thread‑safe.  
- Defensive copying ensures external modifications do not affect internal state.
