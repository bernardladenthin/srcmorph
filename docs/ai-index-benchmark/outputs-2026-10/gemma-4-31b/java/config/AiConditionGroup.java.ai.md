### AiConditionGroup.java
- H: 1.0
- C: 66805F67
- D: 2026-10-28T21:24:22Z
- T: 2026-10-28T21:24:57Z
- G: 
- A: 0.0.0
- X: file
---
> Groups multiple AI conditions for use within logical combinators in Maven configuration.

#### Purpose
* Acts as a container for `AiCondition` elements to ensure correct XML mapping for `<and>` and `<or>` combinators in Maven plugins.

#### Type
public class. Annotated with `@ToString`.

#### Input
* `Collection<AiCondition>` passed to `setConditions`.

#### Output
* `List<AiCondition>` returned by `getConditions`.
* Mutated internal `conditions` field.

#### Core logic
* Maintains a nullable list of `AiCondition` objects.
* Performs defensive copying of the input `Collection<AiCondition>` into a new `ArrayList` within `setConditions` to prevent external mutation.

#### Public API
* `AiConditionGroup() -> void` Constructor.
* `getConditions() -> List<AiCondition>` Returns configured conditions or null.
* `setConditions(Collection<AiCondition>) -> void` Sets conditions with defensive copy.

#### Dependencies
* `java.util.ArrayList`
* `java.util.Collection`
* `java.util.List`
* `lombok.ToString`
* `org.jspecify.annotations.Nullable`
* `AiCondition`
