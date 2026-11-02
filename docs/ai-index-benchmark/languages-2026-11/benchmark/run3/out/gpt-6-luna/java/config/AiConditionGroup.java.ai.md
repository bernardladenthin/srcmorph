### AiConditionGroup.java
- H: 1.0
- C: 66805F67
- D: 2026-11-01T01:56:59Z
- T: 2026-11-01T01:57:09Z
- G: 
- A: 0.0.0
- X: file
---
> Groups child conditions for Maven-configured AND/OR rules while preserving their XML collection element structure.

#### Purpose
- `AiConditionGroup` stores child `AiCondition` entries for an `AiCondition` AND/OR combinator.
- The `conditions` field supports Maven binding as `<conditions><condition>…</condition></conditions>`.

#### Type
- `AiConditionGroup` — public class.
- Annotated with Lombok `@ToString` and initialization-related suppression annotations.

#### Input
- `setConditions(Collection<AiCondition>)` accepts nullable child conditions.
- Maven configuration can populate the child-condition collection.

#### Output
- `getConditions()` returns the nullable child list.
- `setConditions(...)` stores a defensive `ArrayList` copy, or `null`.

#### Core logic
- Holds child `AiCondition` objects in nullable `conditions`.
- Uses the field name `conditions` so Maven derives the child item element as `condition`.
- Copies incoming collections to avoid retaining the caller’s collection instance.

#### Public API
- `AiConditionGroup()` -> creates an empty group.
- `getConditions()` -> `@Nullable List<AiCondition>` — returns child conditions.
- `setConditions(Collection<AiCondition>)` -> `void` — copies child conditions.

#### Dependencies
- `AiCondition`
- `ArrayList`, `Collection`, `List`
- Lombok `ToString`
- JSpecify `Nullable`

#### Exceptions / Errors
- No explicit exceptions or error handling.
- `null` input to `setConditions` leaves the field `null`.

#### Concurrency
- No synchronization or thread-safety guarantees.
