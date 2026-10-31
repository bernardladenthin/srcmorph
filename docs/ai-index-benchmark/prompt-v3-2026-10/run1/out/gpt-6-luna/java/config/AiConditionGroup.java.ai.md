### AiConditionGroup.java
- H: 1.0
- C: 66805F67
- D: 2026-10-31T19:47:56Z
- T: 2026-10-31T19:48:05Z
- G: 
- A: 0.0.0
- X: file
---
> Groups child conditions so Maven-configured `<and>` and `<or>` rules can evaluate their condition lists.

#### Purpose
- `AiConditionGroup` holds child `AiCondition` entries for an `AiCondition` combinator.
- Uses a `conditions` field so Maven binds children as `<conditions><condition>…</condition></conditions>`.

#### Type
- `AiConditionGroup` — public class.
- Annotated with Lombok `@ToString`; suppresses initialization warnings.

#### Input
- `setConditions(Collection<AiCondition>)` accepts child conditions or `null`.

#### Output
- Stores a defensive `ArrayList` copy of the supplied collection, or `null`.
- `getConditions()` returns the stored list or `null`.

#### Core logic
- `setConditions` copies non-null input into a mutable list; assigning `null` clears the field.
- `getConditions` exposes the stored list directly.

#### Public API
- `AiConditionGroup()` -> constructor; creates an empty group.
- `getConditions()` -> `@Nullable List<AiCondition>`; returns child conditions.
- `setConditions(Collection<AiCondition>)` -> `void`; copies child conditions.

#### Dependencies
- `AiCondition`
- `ArrayList`, `Collection`, `List`
- Lombok `ToString`
- JSpecify `Nullable`

#### Exceptions / Errors
- No explicit exceptions or validation.
- `null` is accepted and represented by a null conditions field.

#### Concurrency
- Mutable state; no synchronization or thread-safety guarantees.
