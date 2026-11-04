### AiConditionGroup.java
- H: 1.0
- C: 66805F67
- D: 2026-11-04T07:35:21Z
- T: 2026-11-04T07:35:29Z
- G: 
- A: 0.0.0
- X: file
- P: 1879E12B
---
> Groups child AI conditions so Maven configuration can bind `<and>` and `<or>` combinator contents as `<conditions><condition>…</condition></conditions>`.

#### Purpose
- Holds the child `AiCondition` entries for an AI-condition combinator.
- Copies configured collections into a list; preserves `null` when no conditions are configured.

#### Type
- `public class AiConditionGroup` — class; annotated `@ToString`; no supertypes declared.
- `conditions` is a nullable `List<AiCondition>`.

#### Input
- `setConditions(Collection<AiCondition>)` accepts a nullable collection of child conditions.

#### Output
- `getConditions()` returns the nullable child-condition list.
- `setConditions(...)` stores a defensive `ArrayList` copy, or `null`.

#### Core logic
- `AiConditionGroup()` performs no initialization.
- `setConditions(...)` copies non-null input into a new `ArrayList`; null input clears the field.

#### Public API
- `AiConditionGroup()` — constructs an empty group.
- `getConditions() -> @Nullable List<AiCondition>` — returns child conditions.
- `setConditions(@Nullable Collection<AiCondition>) -> void` — replaces child conditions.
- `toString() -> String` — Lombok-generated representation from `@ToString`.

#### Dependencies
- `AiCondition`
- `java.util.ArrayList`
- `java.util.Collection`
- `java.util.List`
- `lombok.ToString`
- `org.jspecify.annotations.Nullable`
