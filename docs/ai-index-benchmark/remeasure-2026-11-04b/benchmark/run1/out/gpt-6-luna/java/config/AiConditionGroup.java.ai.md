### AiConditionGroup.java
- H: 1.0
- C: 66805F67
- D: 2026-11-04T11:46:43Z
- T: 2026-11-04T11:46:54Z
- G: 
- A: 0.0.0
- X: file
- P: 0B812A3C
---
> Groups child AI conditions for Maven-configured `<and>` and `<or>` combinators.

#### Purpose
- Holds child `AiCondition` entries in the `conditions` collection used by `AiCondition` combinators.
- Uses a plural field name so Maven binds children as `<conditions><condition>…</condition></conditions>`.

#### Type
- `public class AiConditionGroup` — class; annotated `@ToString`; extends `Object`.

#### Input
- `setConditions(Collection<AiCondition>)` accepts child conditions; parameter may be `null`.
- No-argument constructor accepts no inputs.

#### Output
- `getConditions()` returns the configured child conditions or `null`.
- `setConditions(...)` stores a defensive `ArrayList` copy, or stores `null`.

#### Core logic
- `setConditions` copies a non-null collection with `new ArrayList<>(conditions)`; otherwise assigns `null` to `this.conditions`.
- `getConditions` returns the `conditions` field directly.

#### Public API
- `AiConditionGroup()` -> constructor; creates an empty group.
- `getConditions()` -> `@Nullable List<AiCondition>`; returns child conditions.
- `setConditions(@Nullable Collection<AiCondition>)` -> `void`; defensively stores child conditions.

#### Dependencies
- `AiCondition`
- `ArrayList`, `Collection`, `List`
- Lombok `ToString`
- JSpecify `Nullable`

#### Exceptions / Errors
- `conditions` is nullable; no explicit validation or exception handling.
