### AiConditionGroup.java
- H: 1.0
- C: 66805F67
- D: 2026-10-31T14:30:30Z
- T: 2026-10-31T14:30:39Z
- G: 
- A: 0.0.0
- X: file
---
> Groups child conditions so Maven configuration can express `<and>` and `<or>` combinators with `<condition>` elements.

#### Purpose
- Represents the child-condition list used as the body of an `AiCondition` AND/OR combinator.
- Supports Maven binding through a `conditions` collection field.

#### Type
- `public class AiConditionGroup` — class; annotated `@ToString`.

#### Input
- `setConditions(Collection<AiCondition>)` accepts child conditions or `null`.

#### Output
- `getConditions()` returns the configured list or `null`.
- `setConditions(...)` stores a defensive `ArrayList` copy, or `null`.

#### Core logic
- Stores child `AiCondition` objects in the nullable `conditions` field.
- Copies non-null input collections to prevent retaining the caller’s collection instance.
- Uses the field name `conditions` for Maven’s `<conditions><condition>…</condition></conditions>` binding.

#### Public API
- `AiConditionGroup()` -> `void` — creates an empty group.
- `getConditions()` -> `@Nullable List<AiCondition>` — returns child conditions.
- `setConditions(@Nullable Collection<AiCondition>)` -> `void` — replaces child conditions.

#### Dependencies
- `AiCondition`
- `ArrayList`, `Collection`, `List`
- Lombok `@ToString`
- JSpecify `@Nullable`

#### Exceptions / Errors
- Accepts `null` in `setConditions`; `getConditions()` returns `null` when unset.
- No explicit exceptions are thrown or handled.
