### AiConditionGroup.java
- H: 1.0
- C: 66805F67
- D: 2026-10-29T08:50:27Z
- T: 2026-10-29T08:50:32Z
- G: 
- A: 0.0.0
- X: file
---
> Calculates logical grouping of conditions for Maven plugin XML binding.

#### Purpose
- A container for child {@link AiCondition} instances, used as the body of an {@code <and>} or {@code <or>} combinator in {@link AiCondition}.
- Facilitates Maven plugin XML binding by naming the field {@code conditions} so the framework derives the correct item element name.

#### Type
- Class: `public class AiConditionGroup`
- Annotations: `@ToString`
- Fields: private `@Nullable List<AiCondition> conditions`
- No explicit extends or implements clauses.

#### Input
- Constructor: no-argument constructor with no parameters.
- `setConditions(final @Nullable Collection<AiCondition> conditions)`: accepts a nullable collection of {@link AiCondition} items.

#### Output
- `getConditions()`: returns `@Nullable List<AiCondition>` — the child conditions, or null when none were configured.
- `setConditions()`: returns void; stores a defensively copied {@link ArrayList} or null.

#### Core logic
- Defensively copies the input collection into a new {@link ArrayList} to prevent external mutation.
- Stores {@code null} when the input collection is null.

#### Public API
- `getConditions() -> List<AiCondition>` — Returns the child conditions, or null when none were configured.
- `setConditions(Collection<AiCondition> conditions) -> void` — Sets the child conditions (defensively copied).

#### Dependencies
- `java.util.ArrayList`
- `java.util.Collection`
- `java.util.List`
- `lombok.ToString`
- `org.jspecify.annotations.Nullable`

#### Exceptions / Errors
- No explicit exception handling or throw declarations.

#### Concurrency
- No synchronization or immutability guarantees; mutable state (`conditions` field) is accessed via getter/setter.
