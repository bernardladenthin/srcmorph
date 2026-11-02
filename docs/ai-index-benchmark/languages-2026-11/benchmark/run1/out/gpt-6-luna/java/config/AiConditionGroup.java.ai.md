### AiConditionGroup.java
- H: 1.0
- C: 66805F67
- D: 2026-11-01T01:13:43Z
- T: 2026-11-01T01:13:50Z
- G: 
- A: 0.0.0
- X: file
---
> Groups child conditions for Maven-configured `<and>` and `<or>` logic.

#### Purpose
- Holds child `AiCondition` items for an `AiCondition` combinator.
- Uses a `conditions` field so Maven binds children under `<conditions><condition>…</condition></conditions>`.

#### Type
- `public class AiConditionGroup` — class; annotated with Lombok `@ToString`.

#### Input
- `setConditions(Collection<AiCondition>)` accepts child conditions or `null`.

#### Output
- `getConditions()` returns the configured list or `null`.
- `setConditions(...)` stores a defensive `ArrayList` copy, or `null`.

#### Core logic
- Keeps child conditions in a nullable `List<AiCondition>`.
- Copies the supplied collection when setting conditions, avoiding retention of the caller’s collection.

#### Public API
- `AiConditionGroup()` -> `void` — creates an empty group.
- `getConditions()` -> `@Nullable List<AiCondition>` — returns child conditions.
- `setConditions(@Nullable Collection<AiCondition>)` -> `void` — sets a defensive copy.

#### Dependencies
- `AiCondition`
- `ArrayList`, `Collection`, `List`
- Lombok `ToString`
- JSpecify `Nullable`
