### AiConditionGroup.java
- H: 1.0
- C: 66805F67
- D: 2026-11-04T11:56:04Z
- T: 2026-11-04T11:56:15Z
- G: 
- A: 0.0.0
- X: file
- P: 0B812A3C
---
> Groups child AI conditions so Maven configuration can supply them as the body of an `<and>` or `<or>` combinator.

#### Purpose
- Stores the child `AiCondition` entries configured for a condition combinator.
- Uses a `conditions` field to support Maven’s collection-item XML binding.

#### Type
- `public class AiConditionGroup` — class; no supertypes declared; annotated `@ToString` and `@SuppressWarnings({"NullAway.Init", "initialization.fields.uninitialized"})`.
- Field: `private @Nullable List<AiCondition> conditions`.

#### Input
- `setConditions(Collection<AiCondition> conditions)` accepts a nullable collection of child conditions.

#### Output
- `getConditions()` returns the nullable child-condition list.
- `setConditions(...)` stores a defensive `ArrayList` copy, or `null`.

#### Core logic
- `AiConditionGroup()` performs no initialization.
- `getConditions()` returns `conditions`.
- `setConditions(...)` assigns `new ArrayList<>(conditions)` when non-null; otherwise assigns `null`.

#### Public API
- `AiConditionGroup()` — creates an empty group.
- `getConditions() -> @Nullable List<AiCondition>` — returns child conditions.
- `setConditions(@Nullable Collection<AiCondition>) -> void` — sets a defensive copy.

#### Dependencies
- `AiCondition`
- `java.util.ArrayList`
- `java.util.Collection`
- `java.util.List`
- `lombok.ToString`
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- No explicit exceptions or validation; null input clears the configured conditions.
