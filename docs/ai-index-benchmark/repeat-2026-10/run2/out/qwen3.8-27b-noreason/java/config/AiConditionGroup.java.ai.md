### AiConditionGroup.java
- H: 1.0
- C: 66805F67
- D: 2026-10-29T13:48:14Z
- T: 2026-10-29T14:06:31Z
- G: 
- A: 0.0.0
- X: file
---
> Wraps a list of `AiCondition` objects to serve as the body of `<and>`/`<or>` combinators in Maven plugin configuration.

#### Purpose
- Holds a collection of child `AiCondition` instances for use within logical combinator nodes.
- Facilitates Maven plugin XML binding by using a `conditions` field name to derive the `<condition>` item element.

#### Type
- `public class AiConditionGroup`
- Annotated with `@ToString` (Lombok) and `@SuppressWarnings` for NullAway initialization checks.

#### Input
- `setConditions`: accepts a `@Nullable Collection<AiCondition>`.

#### Output
- `getConditions`: returns `@Nullable List<AiCondition>`.
- Mutates internal `conditions` field via defensive copy.

#### Core logic
- Stores a nullable list of `AiCondition` objects.
- `setConditions` creates a new `ArrayList` from the input collection to prevent external mutation; sets field to `null` if input is `null`.
- `getConditions` returns the internal list reference directly.

#### Public API
- `AiConditionGroup()` -> `void` : No-arg constructor.
- `getConditions()` -> `@Nullable List<AiCondition>` : Returns child conditions.
- `setConditions(Collection<AiCondition>)` -> `void` : Sets child conditions defensively.

#### Dependencies
- `java.util.ArrayList`
- `java.util.Collection`
- `java.util.List`
- `lombok.ToString`
- `org.jspecify.annotations.Nullable`
- `net.ladenthin.srcmorph.config.AiCondition`

#### Exceptions / Errors
- No exceptions thrown.
- Null handling: `setConditions` explicitly checks for `null` input; `getConditions` may return `null`.

#### Concurrency
- Not thread-safe; internal `ArrayList` is mutable and shared via `getConditions`.
