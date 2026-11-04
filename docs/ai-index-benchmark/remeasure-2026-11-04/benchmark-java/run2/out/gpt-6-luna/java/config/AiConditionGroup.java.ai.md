### AiConditionGroup.java
- H: 1.0
- C: 66805F67
- D: 2026-11-04T07:27:53Z
- T: 2026-11-04T07:28:01Z
- G: 
- A: 0.0.0
- X: file
- P: 36159BF2
---
> Groups child AI conditions for Maven-configured `<and>` and `<or>` condition combinators.

#### Purpose
- Holds the child `AiCondition` entries used as the body of an AI condition combinator.
- Supports Maven binding through a `conditions` collection field.

#### Type
- `public class AiConditionGroup` — class; annotated with Lombok `@ToString`.

#### Input
- `setConditions(Collection<AiCondition>)` accepts a nullable collection of child conditions.

#### Output
- `getConditions()` returns the nullable child-condition list.
- `setConditions(...)` stores a defensive `ArrayList` copy, or `null`.

#### Core logic
- Stores child conditions in a nullable `List<AiCondition>` named `conditions`.
- Copies configured collections to support Maven’s `<conditions><condition>…</condition></conditions>` binding.

#### Public API
- `AiConditionGroup()` → `void` — creates an empty group.
- `getConditions()` → `@Nullable List<AiCondition>` — returns child conditions.
- `setConditions(Collection<AiCondition>)` → `void` — replaces child conditions.

#### Dependencies
- `AiCondition`
- `java.util.ArrayList`
- `java.util.Collection`
- `java.util.List`
- `lombok.ToString`
- `org.jspecify.annotations.Nullable`
