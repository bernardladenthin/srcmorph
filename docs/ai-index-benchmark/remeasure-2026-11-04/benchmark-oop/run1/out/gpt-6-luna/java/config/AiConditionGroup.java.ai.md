### AiConditionGroup.java
- H: 1.0
- C: 66805F67
- D: 2026-11-04T07:19:50Z
- T: 2026-11-04T07:19:57Z
- G: 
- A: 0.0.0
- X: file
- P: 1879E12B
---
> Groups child AI conditions so Maven configuration can bind `<and>` and `<or>` combinator bodies as `<conditions><condition>…</condition></conditions>`.

#### Purpose
- `AiConditionGroup` holds the child `AiCondition` values for an `and`/`or` combinator.
- Uses a `conditions` field so Maven derives the child element name as `condition`.

#### Type
- `public class AiConditionGroup` — class; annotated `@ToString`; no declared supertypes.

#### Input
- `setConditions(Collection<AiCondition> conditions)` accepts a nullable collection of child conditions.

#### Output
- `getConditions()` returns the nullable child-condition list.
- `setConditions(...)` stores a defensive `ArrayList` copy, or `null`.

#### Core logic
- Stores child conditions in nullable `List<AiCondition> conditions`.
- Setter copies the supplied collection to avoid retaining the caller’s collection; `null` clears the field.
- No-argument constructor performs no initialization.

#### Public API
- `AiConditionGroup()` — creates an empty group.
- `getConditions() -> @Nullable List<AiCondition>` — returns child conditions.
- `setConditions(@Nullable Collection<AiCondition>) -> void` — copies or clears child conditions.

#### Dependencies
- `AiCondition`
- `ArrayList`, `Collection`, `List`
- Lombok `ToString`
- JSpecify `Nullable`

#### Exceptions / Errors
- No explicit exception handling or precondition checks.
