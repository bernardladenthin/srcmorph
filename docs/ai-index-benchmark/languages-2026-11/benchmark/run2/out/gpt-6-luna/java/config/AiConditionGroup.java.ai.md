### AiConditionGroup.java
- H: 1.0
- C: 66805F67
- D: 2026-11-01T01:35:52Z
- T: 2026-11-01T01:36:01Z
- G: 
- A: 0.0.0
- X: file
---
> Groups child conditions for configuring `<and>` and `<or>` rules in Maven.

#### Purpose
- Holds the child `AiCondition` items used by an `AiCondition` combinator.
- Supports Maven’s `<conditions><condition>…</condition></conditions>` configuration shape.

#### Type
- `public class AiConditionGroup` — class.
- Annotated with Lombok `@ToString`.

#### Input
- `setConditions(Collection<AiCondition>)` accepts child conditions or `null`.

#### Output
- `getConditions()` returns the stored child list or `null`.
- `setConditions(...)` stores a defensive `ArrayList` copy of the supplied collection, or `null`.

#### Core logic
- Stores child conditions in a nullable `List<AiCondition>` field named `conditions`.
- Uses the field name to support Maven collection-item binding as `<condition>`.

#### Public API
- `AiConditionGroup()` → `void` — creates an empty group.
- `getConditions()` → `@Nullable List<AiCondition>` — returns child conditions.
- `setConditions(@Nullable Collection<AiCondition>)` → `void` — replaces child conditions.

#### Dependencies
- `AiCondition`
- `java.util.ArrayList`
- `java.util.Collection`
- `java.util.List`
- Lombok `ToString`
- JSpecify `Nullable`

#### Exceptions / Errors
- No explicit exceptions.
- Accepts `null` conditions; getter returns `null` when none were configured.
