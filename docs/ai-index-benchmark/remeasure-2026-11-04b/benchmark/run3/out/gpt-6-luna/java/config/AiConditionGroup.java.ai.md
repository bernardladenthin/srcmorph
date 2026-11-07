### AiConditionGroup.java
- H: 1.0
- C: 66805F67
- D: 2026-11-04T12:04:37Z
- T: 2026-11-04T12:04:47Z
- G: 
- A: 0.0.0
- X: file
- P: 0B812A3C
---
> Groups child AI conditions for Maven-configured `<and>` and `<or>` condition combinators.

#### Purpose
- Provides the child-condition collection used as the body of `AiCondition` combinators.
- Uses a `conditions` field so Maven binds child entries as `<conditions><condition>…</condition></conditions>`.

#### Type
- `public class AiConditionGroup` — class; annotated `@ToString`; no declared supertypes.

#### Input
- `setConditions(Collection<AiCondition> conditions)` accepts a nullable collection of child conditions.

#### Output
- `getConditions()` returns the nullable child-condition list.
- `setConditions(...)` stores a defensive `ArrayList` copy, or `null` when passed `null`.

#### Core logic
- `conditions` is a nullable `List<AiCondition>`.
- `setConditions(...)` copies non-null input with `new ArrayList<>(conditions)`; otherwise assigns `null`.
- The no-argument constructor performs no initialization.

#### Public API
- `AiConditionGroup()` -> constructor; creates an empty group.
- `getConditions()` -> `@Nullable List<AiCondition>`; returns child conditions.
- `setConditions(Collection<AiCondition>)` -> `void`; replaces child conditions with a defensive copy.

#### Dependencies
- `AiCondition`
- `java.util.ArrayList`
- `java.util.Collection`
- `java.util.List`
- `lombok.ToString`
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- No explicit exception handling or precondition checks.

#### Concurrency
- No synchronization or other concurrency controls.
