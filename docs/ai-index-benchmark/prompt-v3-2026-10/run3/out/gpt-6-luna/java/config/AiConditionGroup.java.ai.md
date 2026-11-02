### AiConditionGroup.java
- H: 1.0
- C: 66805F67
- D: 2026-10-31T20:34:56Z
- T: 2026-10-31T20:35:03Z
- G: 
- A: 0.0.0
- X: file
---
> Groups child conditions for Maven-configured `<and>` and `<or>` condition combinators.

#### Purpose
- Holds child `AiCondition` entries used as a combinator body.
- Uses a `conditions` field so Maven binds children under `<conditions><condition>…</condition></conditions>`.

#### Type
- `public class AiConditionGroup` — class; annotated `@ToString`.

#### Input
- `AiConditionGroup()` — no inputs.
- `setConditions(Collection<AiCondition>)` — accepts child conditions or `null`.

#### Output
- `getConditions()` returns the configured child list or `null`.
- `setConditions(...)` stores a defensive `ArrayList` copy, or `null`.

#### Core logic
- Stores child `AiCondition` values in a nullable `conditions` list.
- Copies incoming collections to an `ArrayList` when setting the list.

#### Public API
- `AiConditionGroup()` -> `void` — creates an empty group.
- `getConditions()` -> `@Nullable List<AiCondition>` — returns child conditions.
- `setConditions(@Nullable Collection<AiCondition>)` -> `void` — replaces child conditions.

#### Dependencies
- `AiCondition`
- `java.util.ArrayList`
- `java.util.Collection`
- `java.util.List`
- `lombok.ToString`
- `org.jspecify.annotations.Nullable`
