### AiConditionGroup.java
- H: 1.0
- C: 66805F67
- D: 2026-11-01T14:41:21Z
- T: 2026-11-01T14:41:31Z
- G: 
- A: 0.0.0
- X: file
---
> Groups child conditions for `<and>` and `<or>` rules in Maven configuration.

#### Purpose
- Holds child `AiCondition` entries for an `AiCondition` combinator.
- Supports Maven binding through a `conditions` collection field.

#### Type
- `public class AiConditionGroup` — class; annotated with Lombok `@ToString`.

#### Input
- `setConditions(Collection<AiCondition>)` accepts child conditions, including `null`.

#### Output
- `getConditions()` returns the configured child list or `null`.
- `setConditions(...)` stores a defensive `ArrayList` copy, or `null`.

#### Core logic
- Stores child `AiCondition` objects in a nullable `conditions` list.
- Uses the field name `conditions` so Maven binds entries as `<conditions><condition>…</condition></conditions>`.

#### Public API
- `AiConditionGroup()` -> `AiConditionGroup` — creates an empty group.
- `getConditions()` -> `@Nullable List<AiCondition>` — returns child conditions.
- `setConditions(Collection<AiCondition>)` -> `void` — defensively copies child conditions.

#### Dependencies
- `AiCondition`
- `ArrayList`, `Collection`, `List`
- Lombok `@ToString`
- JSpecify `@Nullable`

#### Exceptions / Errors
- `setConditions` accepts `null` and stores `null`; no explicit exceptions are thrown.
