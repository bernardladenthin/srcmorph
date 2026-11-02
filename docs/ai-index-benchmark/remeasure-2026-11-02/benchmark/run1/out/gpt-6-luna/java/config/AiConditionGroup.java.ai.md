### AiConditionGroup.java
- H: 1.0
- C: 66805F67
- D: 2026-11-01T23:26:17Z
- T: 2026-11-01T23:26:25Z
- G: 
- A: 0.0.0
- X: file
- P: C721BDEF
---
> Groups child AI conditions for Maven-configured AND/OR combinators.

#### Purpose
- Holds the child `AiCondition` entries used as a combinator body.
- Uses a `conditions` field so Maven binds children as `<conditions><condition>…</condition></conditions>`.

#### Type
- `public class AiConditionGroup` — class; annotated `@ToString`.

#### Input
- `setConditions(Collection<AiCondition>)` accepts a nullable collection of child conditions.

#### Output
- `getConditions()` returns the nullable child-condition list.
- `setConditions` stores a defensive `ArrayList` copy, or `null`.

#### Core logic
- Stores children in nullable `List<AiCondition> conditions`.
- Copies non-null input collections when setting the list.

#### Public API
- `AiConditionGroup()` -> constructor; creates an empty group.
- `getConditions()` -> `@Nullable List<AiCondition>`; returns child conditions.
- `setConditions(Collection<AiCondition>)` -> `void`; replaces children with a defensive copy.

#### Dependencies
- `AiCondition`
- `ArrayList`, `Collection`, `List`
- Lombok `@ToString`
- JSpecify `@Nullable`

#### Concurrency
- No synchronization; mutable state is not thread-safe by implementation.
