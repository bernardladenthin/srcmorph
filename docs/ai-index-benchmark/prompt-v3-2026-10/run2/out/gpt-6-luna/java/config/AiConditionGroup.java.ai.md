### AiConditionGroup.java
- H: 1.0
- C: 66805F67
- D: 2026-10-31T20:10:35Z
- T: 2026-10-31T20:10:45Z
- G: 
- A: 0.0.0
- X: file
---
> Stores configured child conditions for `<and>` and `<or>` rules in Maven configuration.

#### Purpose
- Groups `AiCondition` children for use as a combinator body.
- Uses a `conditions` field so Maven binds children as `<conditions><condition>…</condition></conditions>`.

#### Type
- `public class AiConditionGroup` — class.
- Annotated with Lombok `@ToString`.

#### Input
- `setConditions(Collection<AiCondition>)` accepts child conditions or `null`.
- No-argument constructor.

#### Output
- `getConditions()` returns the child list or `null`.
- Stores a defensive `ArrayList` copy of non-null input.

#### Core logic
- `setConditions` copies the supplied collection into a mutable list; preserves `null`.
- `getConditions` exposes the stored list directly.

#### Public API
- `AiConditionGroup()` -> `void` — constructs an empty group.
- `getConditions()` -> `@Nullable List<AiCondition>` — returns child conditions.
- `setConditions(Collection<AiCondition>)` -> `void` — replaces child conditions.

#### Dependencies
- `AiCondition`
- `ArrayList`, `Collection`, `List`
- Lombok `ToString`
- JSpecify `Nullable`

#### Concurrency
- Mutable state; no synchronization.
