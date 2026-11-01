### AiConditionGroup.java
- H: 1.0
- C: 66805F67
- D: 2026-11-01T14:19:41Z
- T: 2026-11-01T14:19:51Z
- G: 
- A: 0.0.0
- X: file
---
> Groups child conditions so Maven configuration can express the children of an `<and>` or `<or>` condition.

#### Purpose
- Holds the child `AiCondition` entries for a condition combinator.
- Uses a `conditions` field so Maven binds child elements as `<conditions><condition>…</condition></conditions>`.

#### Type
- `AiConditionGroup` — public class.
- Annotated with Lombok `@ToString`; suppresses initialization warnings.

#### Input
- `setConditions(Collection<AiCondition>)` accepts child conditions, or `null`.

#### Output
- Stores a defensive `ArrayList` copy of the supplied collection, or `null`.
- `getConditions()` returns the stored list or `null`.

#### Core logic
- `setConditions` copies non-null input into a new `ArrayList`; it does not retain the caller’s collection.
- The child list is nullable and is not initialized by the no-argument constructor.

#### Public API
- `AiConditionGroup()` — creates an empty group.
- `getConditions() -> @Nullable List<AiCondition>` — returns child conditions.
- `setConditions(@Nullable Collection<AiCondition>) -> void` — replaces child conditions.

#### Dependencies
- `AiCondition`
- `ArrayList`, `Collection`, `List`
- Lombok `@ToString`
- JSpecify `@Nullable`

#### Exceptions / Errors
- No exceptions are explicitly thrown or caught.
- Null input to `setConditions` sets the field to `null`.

#### Concurrency
- No synchronization; mutable and not thread-safe.
