### AiConditionGroup.java
- H: 1.0
- C: 66805F67
- D: 2026-10-29T09:50:36Z
- T: 2026-10-29T09:56:46Z
- G: 
- A: 0.0.0
- X: file
---
> Wraps a list of `AiCondition` objects to serve as the body of `<and>`/`<or>` combinators in Maven plugin configuration.

#### Purpose
- Holds a collection of child `AiCondition` instances for use within logical combinator nodes.
- Facilitates Maven plugin configuration binding by using a `conditions` field name to generate `<conditions><condition>` XML structure.

#### Type
- `public class AiConditionGroup`
- Annotated with `@ToString` (Lombok) and `@SuppressWarnings` for NullAway initialization checks.

#### Input
- `setConditions`: accepts a `@Nullable Collection<AiCondition>`.

#### Output
- `getConditions`: returns `@Nullable List<AiCondition>`.
- Mutates internal `conditions` field via defensive copy.

#### Core logic
- Stores child conditions in a `List<AiCondition>` field.
- `setConditions` performs a defensive copy using `new ArrayList<>(conditions)` if input is non-null; sets field to `null` otherwise.
- `getConditions` returns the internal list directly or `null`.

#### Public API
- `AiConditionGroup()` -> `void` : No-arg constructor.
- `getConditions()` -> `@Nullable List<AiCondition>` : Returns child conditions.
- `setConditions(Collection<AiCondition>)` -> `void` : Sets child conditions.

#### Dependencies
- `java.util.ArrayList`
- `java.util.Collection`
- `java.util.List`
- `lombok.ToString`
- `org.jspecify.annotations.Nullable`
- `net.ladenthin.srcmorph.config.AiCondition`

#### Exceptions / Errors
- No exceptions thrown.
- Null-safety handled via `@Nullable` annotations and explicit null checks in `setConditions`.
