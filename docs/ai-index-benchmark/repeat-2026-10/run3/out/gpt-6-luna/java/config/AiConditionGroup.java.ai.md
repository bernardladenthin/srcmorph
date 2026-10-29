### AiConditionGroup.java
- H: 1.0
- C: 66805F67
- D: 2026-10-29T14:19:17Z
- T: 2026-10-29T14:19:27Z
- G: 
- A: 0.0.0
- X: file
---
> Groups configured child conditions for `<and>` and `<or>` rules, preserving Maven’s expected `<conditions><condition>` configuration structure.

#### Purpose
- Holds the child `AiCondition` items used as a combinator body.
- Uses a `conditions` field so Maven binds the expected `<conditions><condition>…</condition></conditions>` structure.

#### Type
- Public class `AiConditionGroup`; annotated with Lombok `@ToString`.
- Contains nullable `List<AiCondition> conditions`.

#### Input
- `setConditions` accepts a nullable `Collection<AiCondition>`.

#### Output
- `getConditions` returns the configured nullable child-condition list.
- `setConditions` stores a defensive `ArrayList` copy, or `null`.

#### Core logic
- Copies non-null input collections into a mutable list, preventing the stored list from aliasing the caller’s collection.
- Retains `null` when no conditions are configured.

#### Public API
- `AiConditionGroup()` → `void` — creates an empty group.
- `getConditions()` → `@Nullable List<AiCondition>` — returns child conditions.
- `setConditions(Collection<AiCondition>)` → `void` — copies child conditions.

#### Dependencies
- `AiCondition`
- `ArrayList`, `Collection`, `List`
- Lombok `@ToString`
- JSpecify `@Nullable`

#### Exceptions / Errors
- No explicit exceptions or error handling.

#### Concurrency
- No synchronization; mutable and not documented as thread-safe.
