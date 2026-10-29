### AiConditionGroup.java
- H: 1.0
- C: 66805F67
- D: 2026-10-29T13:48:14Z
- T: 2026-10-29T14:03:20Z
- G: 
- A: 0.0.0
- X: file
---
> Defines a container for a list of child AI conditions used as the body for logical AND/OR combinators in Maven configuration.

#### Purpose
- Acts as a wrapper for a collection of `AiCondition` instances to enable Maven plugin binding for `<and>` and `<or>` elements.
- Provides a mutable container for condition lists with defensive copying on set.

#### Type
- `public class AiConditionGroup`
- Annotations: `@ToString`, `@SuppressWarnings({"NullAway.Init", "initialization.fields.uninitialized"})`
- No extends or implements clauses.

#### Input
- Constructor: `AiConditionGroup()` (no arguments).
- Method `setConditions(Collection<AiCondition>)`: accepts a nullable collection of `AiCondition` objects.

#### Output
- Method `getConditions()`: returns `List<AiCondition>` or `null`.
- Internal state: `conditions` field holds a defensive copy of the input collection.

#### Core logic
- Initializes an empty or null `conditions` list upon construction.
- `setConditions` creates a new `ArrayList` from the provided collection to ensure immutability of the external reference.
- `getConditions` exposes the internal list reference directly, allowing `null` if no conditions were set.

#### Public API
- `AiConditionGroup()` -> `void` (Constructor)
- `getConditions() -> List<AiCondition>` (Returns child conditions or null)
- `setConditions(Collection<AiCondition>) -> void` (Sets conditions with defensive copy)

#### Dependencies
- `java.util.ArrayList`
- `java.util.Collection`
- `java.util.List`
- `lombok.ToString`
- `org.jspecify.annotations.Nullable`
- `net.ladenthin.srcmorph.config.AiCondition`

#### Exceptions / Errors
- None declared or thrown.
- Null handling: `conditions` field is nullable; `setConditions` accepts null and sets the field to null.

#### Concurrency
- No synchronization or thread-safety guarantees provided.
- Defensive copy in `setConditions` prevents external modification of the internal list but does not protect against concurrent modification of the returned list by the caller.
