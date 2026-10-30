### AiConditionGroup.java
- H: 1.0
- C: 66805F67
- D: 2026-10-30T00:29:58Z
- T: 2026-10-30T00:34:03Z
- G: 
- A: 0.0.0
- X: file
---
> Groups child conditions so Maven can bind `<and>` and `<or>` combinator contents through `<conditions><condition>…</condition></conditions>`.

#### Purpose
- Holds child `AiCondition` entries for an `AiCondition` combinator.
- Uses a `conditions` field name for Maven collection-item binding.

#### Type
- Public class `AiConditionGroup`.
- Annotated with Lombok `@ToString`.

#### Input
- `setConditions` accepts a nullable `Collection<AiCondition>`.

#### Output
- Stores a defensive `ArrayList` copy of configured conditions, or `null`.
- `getConditions` returns the nullable child-condition list.

#### Core logic
- Copies non-null input collections into a new list.
- Preserves `null` when no conditions are configured.

#### Public API
- `AiConditionGroup()` -> `void` — creates an empty group.
- `getConditions()` -> `@Nullable List<AiCondition>` — returns child conditions.
- `setConditions(Collection<AiCondition>)` -> `void` — defensively copies conditions.

#### Dependencies
- `AiCondition`
- `ArrayList`, `Collection`, `List`
- Lombok `ToString`
- JSpecify `Nullable`
