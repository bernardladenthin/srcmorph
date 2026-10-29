### AiConditionGroup.java
- H: 1.0
- C: 66805F67
- D: 2026-10-29T13:48:14Z
- T: 2026-10-29T13:48:22Z
- G: 
- A: 0.0.0
- X: file
---
> Groups child conditions for Maven-configured `<and>` and `<or>` condition combinators.

#### Purpose
- Represents the child-condition list used as an `AiCondition` combinator body.
- Names the collection `conditions` so Maven binds nested `<condition>` elements.

#### Type
- Public class `AiConditionGroup`.
- Annotated `@ToString`; nullable `List<AiCondition>` field.

#### Input
- `setConditions` accepts a nullable `Collection<AiCondition>`.

#### Output
- `getConditions` returns the nullable child-condition list.
- `setConditions` stores a defensive `ArrayList` copy, or `null`.

#### Core logic
- Copies supplied conditions into a new list; preserves `null` when no collection is supplied.

#### Public API
- `AiConditionGroup()` → constructor; initializes an empty group.
- `getConditions()` → `@Nullable List<AiCondition>`; returns child conditions.
- `setConditions(Collection<AiCondition>)` → `void`; replaces child conditions.
- Lombok-generated `toString()` → `String`; formats group state.

#### Dependencies
- `AiCondition`, `ArrayList`, `Collection`, `List`, Lombok `ToString`, JSpecify `Nullable`.

#### Exceptions / Errors
- No explicit exception handling or validation.

#### Concurrency
- No synchronization; mutable list field.
