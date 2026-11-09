### AiConditionGroup.java
- H: 1.0
- C: 66805F67
- D: 2026-11-04T07:43:16Z
- T: 2026-11-04T07:43:24Z
- G: 
- A: 0.0.0
- X: file
- P: 36159BF2
---
> Groups child AI conditions for Maven-configured `and`/`or` combinators.

#### Purpose
- Holds the child `AiCondition` items used as an `and`/`or` combinator body.
- Uses a `conditions` field so Maven binds items as `<conditions><condition>…</condition></conditions>`.

#### Type
- `public class AiConditionGroup` — class; annotated `@ToString`.
- Nullable `List<AiCondition> conditions`.

#### Input
- `setConditions` accepts a nullable `Collection<AiCondition>`.

#### Output
- `getConditions` returns the nullable child-condition list.
- `setConditions` stores a defensive `ArrayList` copy, or `null`.

#### Core logic
- `AiConditionGroup()` performs no initialization.
- `setConditions` copies non-null input into a new list; preserves null as null.

#### Public API
- `AiConditionGroup()` -> `AiConditionGroup` — creates an empty group.
- `getConditions()` -> `@Nullable List<AiCondition>` — returns child conditions.
- `setConditions(Collection<AiCondition>)` -> `void` — defensively copies conditions.

#### Dependencies
- `AiCondition`
- `ArrayList`, `Collection`, `List`
- Lombok `ToString`
- JSpecify `Nullable`

#### Exceptions / Errors
- No explicit exceptions.
- Null input to `setConditions` is stored as null.
