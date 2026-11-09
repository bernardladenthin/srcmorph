### AiConditionGroup.java
- H: 1.0
- C: 66805F67
- D: 2026-11-04T07:09:59Z
- T: 2026-11-04T07:10:08Z
- G: 
- A: 0.0.0
- X: file
- P: 36159BF2
---
> Groups child AI conditions so Maven configuration can represent the contents of `<and>` and `<or>` combinators.

#### Purpose
- Holds configured child `AiCondition` entries for an `AiCondition` combinator.
- Uses a `conditions` field so Maven binds items as `<conditions><condition>…</condition></conditions>`.

#### Type
- `public class AiConditionGroup` — class; annotated `@ToString`.

#### Input
- `setConditions(Collection<AiCondition>)` accepts child conditions or `null`.

#### Output
- `getConditions()` returns the stored `List<AiCondition>` or `null`.
- `setConditions(...)` stores a defensive `ArrayList` copy, or `null`.

#### Core logic
- `AiConditionGroup` stores child conditions in a nullable `conditions` list.
- Copies incoming collections to an `ArrayList` to avoid retaining the caller’s collection.

#### Public API
- `AiConditionGroup()` -> `void` — creates an empty group.
- `getConditions()` -> `@Nullable List<AiCondition>` — returns child conditions.
- `setConditions(Collection<AiCondition>)` -> `void` — replaces child conditions.

#### Dependencies
- `AiCondition`
- `ArrayList`, `Collection`, `List`
- Lombok `@ToString`
- JSpecify `@Nullable`

#### Exceptions / Errors
- `null` conditions are accepted and preserved as `null`.

#### Concurrency
- Mutable field; no synchronization or thread-safety guarantees.
