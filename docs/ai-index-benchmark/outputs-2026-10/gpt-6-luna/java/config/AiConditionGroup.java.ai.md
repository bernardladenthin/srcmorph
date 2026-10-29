### AiConditionGroup.java
- H: 1.0
- C: 66805F67
- D: 2026-10-28T21:19:32Z
- T: 2026-10-28T21:19:39Z
- G: 
- A: 0.0.0
- X: file
---
> Groups child AI conditions so Maven configuration can bind the body of `<and>` and `<or>` combinators.

#### Purpose
- `AiConditionGroup` holds child `AiCondition` entries for combinator configuration.
- The `conditions` field supports Maven’s `<conditions><condition>…</condition></conditions>` binding.

#### Type
- Public class; annotated with Lombok `@ToString`.
- Nullable `List<AiCondition>` field.

#### Input
- `setConditions` accepts a nullable `Collection<AiCondition>`.

#### Output
- `getConditions` returns the configured nullable list.
- `setConditions` stores a defensive `ArrayList` copy, or `null`.

#### Core logic
- Copies supplied child conditions into a mutable list.
- Preserves `null` when no collection is supplied.

#### Public API
- `AiConditionGroup()` → constructor; creates an unconfigured group.
- `getConditions()` → `@Nullable List<AiCondition>`; returns child conditions.
- `setConditions(Collection<AiCondition>)` → `void`; sets child conditions.

#### Dependencies
- `AiCondition`, `ArrayList`, `Collection`, `List`.
- Lombok `ToString`; JSpecify `Nullable`.

#### Exceptions / Errors
- No explicit exception handling or validation.

#### Concurrency
- No synchronization; mutable and not thread-safe.
