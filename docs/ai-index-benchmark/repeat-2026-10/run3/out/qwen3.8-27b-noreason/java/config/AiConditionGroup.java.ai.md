### AiConditionGroup.java
- H: 1.0
- C: 66805F67
- D: 2026-10-29T14:19:17Z
- T: 2026-10-29T14:45:56Z
- G: 
- A: 0.0.0
- X: file
---
> Wraps a list of `AiCondition` children to enable Maven XML binding for `<and>`/`<or>` combinator bodies.

#### Purpose
- Holds a collection of `AiCondition` instances as the body of an `and` or `or` combinator in `AiCondition`.
- Provides a named field (`conditions`) so Maven's plugin framework binds nested XML items correctly.

#### Type
- `public class AiConditionGroup`
- Annotations: `@ToString` (Lombok), `@SuppressWarnings({"NullAway.Init", "initialization.fields.uninitialized"})`

#### Input
- `setConditions(final @Nullable Collection<AiCondition> conditions)` — accepts a collection of `AiCondition` objects.

#### Output
- `getConditions()` returns `@Nullable List<AiCondition>` — the stored list or `null`.
- Mutates internal field `conditions` via defensive copy in `setConditions`.

#### Core logic
- Stores a `List<AiCondition>` in a private `@Nullable` field.
- `setConditions` defensively copies the input `Collection` into a new `ArrayList` if non-null; otherwise sets field to `null`.
- `getConditions` returns the field directly (no copy on read).

#### Public API
- `AiConditionGroup()` -> `void` — no-arg constructor.
- `getConditions()` -> `@Nullable List<AiCondition>` — returns child conditions.
- `setConditions(Collection<AiCondition>)` -> `void` — sets child conditions with copy.

#### Dependencies
- `java.util.ArrayList`
- `java.util.Collection`
- `java.util.List`
- `lombok.ToString`
- `org.jspecify.annotations.Nullable`
- `net.ladenthin.srcmorph.config.AiCondition`

#### Exceptions / Errors
- No exceptions thrown.
- `@Nullable` on both field and getter; `setConditions` handles `null` input by storing `null`.
