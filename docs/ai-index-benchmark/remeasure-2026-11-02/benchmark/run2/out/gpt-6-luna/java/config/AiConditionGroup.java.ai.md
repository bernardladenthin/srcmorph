### AiConditionGroup.java
- H: 1.0
- C: 66805F67
- D: 2026-11-01T23:42:40Z
- T: 2026-11-01T23:42:48Z
- G: 
- A: 0.0.0
- X: file
- P: C721BDEF
---
> Groups child AI conditions for use inside `<and>` and `<or>` configuration combinators.

#### Purpose
- Stores the child `AiCondition` entries that Maven binds under a `<conditions>` element.
- Allows the child-condition collection to be absent.

#### Type
- `public class AiConditionGroup` — class; annotated `@ToString`.

#### Input
- `setConditions(Collection<AiCondition>)` accepts child conditions or `null`.
- Maven plugin configuration binds child entries through the `conditions` field.

#### Output
- `getConditions()` returns the stored list or `null`.
- `setConditions(...)` defensively copies non-null input into an `ArrayList`.

#### Core logic
- Holds a nullable list of child `AiCondition` objects.
- Copies configured conditions on assignment; preserves `null` when no collection is supplied.
- Uses the field name `conditions` for Maven’s `<conditions><condition>…</condition></conditions>` binding.

#### Public API
- `AiConditionGroup()` — creates an unconfigured group.
- `getConditions() -> @Nullable List<AiCondition>` — returns child conditions.
- `setConditions(Collection<AiCondition>) -> void` — replaces children with a defensive copy.

#### Dependencies
- `AiCondition`
- `ArrayList`, `Collection`, `List`
- Lombok `@ToString`
- JSpecify `@Nullable`

#### Exceptions / Errors
- No explicit exceptions or error handling.
- `null` conditions are accepted and retained as `null`.

#### Concurrency
- No synchronization or thread-safety guarantees.
