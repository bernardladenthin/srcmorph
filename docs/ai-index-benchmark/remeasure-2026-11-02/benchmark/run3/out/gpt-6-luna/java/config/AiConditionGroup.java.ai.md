### AiConditionGroup.java
- H: 1.0
- C: 66805F67
- D: 2026-11-01T23:56:56Z
- T: 2026-11-01T23:57:04Z
- G: 
- A: 0.0.0
- X: file
- P: C721BDEF
---
> Groups configured AI child conditions so `<and>` and `<or>` rules can bind their `<condition>` entries correctly.

#### Purpose
- Represents the child-condition list used as the body of an `AiCondition` `<and>` or `<or>` combinator.
- Uses the `conditions` field name for Maven plugin configuration binding.

#### Type
- `public class AiConditionGroup` — class; annotated with Lombok `@ToString`.
- Contains one nullable field: `List<AiCondition> conditions`.

#### Input
- No-argument constructor.
- `setConditions` accepts a nullable `Collection<AiCondition>`.

#### Output
- `getConditions` returns the nullable child-condition list.
- `setConditions` stores a defensive `ArrayList` copy of the input collection, or `null`.

#### Core logic
- `AiConditionGroup.setConditions` copies configured conditions into a mutable list, preventing the stored list from being the caller’s collection.
- Preserves `null` when no conditions are configured.

#### Public API
- `AiConditionGroup()` -> `AiConditionGroup` — creates an empty group.
- `getConditions()` -> `@Nullable List<AiCondition>` — returns child conditions.
- `setConditions(Collection<AiCondition>)` -> `void` — defensively copies child conditions.

#### Dependencies
- `AiCondition`
- `ArrayList`, `Collection`, `List`
- Lombok `@ToString`
- JSpecify `@Nullable`

#### Exceptions / Errors
- No explicit exceptions or error handling.
- Null input to `setConditions` clears the stored list to `null`.
