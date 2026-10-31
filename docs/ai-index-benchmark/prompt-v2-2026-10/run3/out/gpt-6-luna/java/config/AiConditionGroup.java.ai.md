### AiConditionGroup.java
- H: 1.0
- C: 66805F67
- D: 2026-10-31T14:57:29Z
- T: 2026-10-31T14:57:36Z
- G: 
- A: 0.0.0
- X: file
---
> Groups child conditions for configuring `<and>` and `<or>` condition combinators.

#### Purpose
- Holds child `AiCondition` entries for Maven-configured condition groups.
- Uses a `conditions` field so Maven binds items as `<conditions><condition>…</condition></conditions>`.

#### Type
- `public class AiConditionGroup` — class; annotated `@ToString`.
- Nullable `List<AiCondition>` field; suppresses initialization warnings.

#### Input
- `setConditions(Collection<AiCondition>)` accepts a nullable collection of child conditions.

#### Output
- `getConditions()` returns the configured nullable list.
- `setConditions(...)` stores a defensive `ArrayList` copy, or `null`.

#### Core logic
- Stores child conditions in the nullable `conditions` field.
- Copies supplied collections to prevent retaining the caller’s collection object.

#### Public API
- `AiConditionGroup()` -> `void` — creates an empty group.
- `getConditions()` -> `@Nullable List<AiCondition>` — returns child conditions.
- `setConditions(Collection<AiCondition>)` -> `void` — replaces child conditions.

#### Dependencies
- `AiCondition`
- `ArrayList`, `Collection`, `List`
- Lombok `ToString`
- JSpecify `Nullable`

#### Exceptions / Errors
- `setConditions(null)` sets the field to `null`; no explicit exceptions are thrown.
