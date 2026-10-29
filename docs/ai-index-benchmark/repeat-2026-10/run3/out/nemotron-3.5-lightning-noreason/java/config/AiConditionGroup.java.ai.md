### AiConditionGroup.java
- H: 1.0
- C: 66805F67
- D: 2026-10-29T14:19:17Z
- T: 2026-10-29T14:34:01Z
- G: 
- A: 0.0.0
- X: file
---
> Manages a group of child conditions for Maven plugin combinators.

#### Purpose
A container for child {@link AiCondition} instances used as the body of {@code <and>} or {@code <or>} combinators in Maven plugin configuration.

#### Type
Class `AiConditionGroup` in package `net.ladenthin.srcmorph.config`. Annotated with `@ToString`. No explicit extends or implements.

#### Input
Constructor: no-argument. Method `setConditions` accepts a `@Nullable Collection<AiCondition>`.

#### Output
Method `getConditions` returns `@Nullable List<AiCondition>`. Method `setConditions` returns void.

#### Core logic
- Defensively copies the provided collection into a new `ArrayList` when setting conditions.
- Stores `null` when the input collection is null.
- The field `conditions` is annotated `@Nullable` to permit absence of child conditions.

#### Public API
- `getConditions() -> @Nullable List<AiCondition>` — Returns the child conditions, or null when none were configured.
- `setConditions(final @Nullable Collection<AiCondition> conditions) -> void` — Sets the child conditions, defensively copying the input.

#### Dependencies
`java.util.ArrayList`, `java.util.Collection`, `java.util.List`, `lombok.ToString`, `org.jspecify.annotations.Nullable`.

#### Exceptions / Errors
No explicit exception throwing. Null-safety handled via `@Nullable` annotation and runtime null check in `setConditions`.

#### Concurrency
No synchronization or immutability guarantees. The internal `conditions` field is mutable and not thread-safe.
