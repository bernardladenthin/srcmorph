### AiConditionGroup.java
- H: 1.0
- C: 66805F67
- D: 2026-10-29T13:48:14Z
- T: 2026-10-29T13:53:05Z
- G: 
- A: 0.0.0
- X: file
---
> Groups a collection of AI conditions to enable Maven plugin XML binding for logical AND/OR combinators.

#### Purpose
* Acts as a wrapper for a list of `AiCondition` objects.
* Ensures Maven XML configuration maps child elements to `<conditions><condition>...</condition></conditions>`.

#### Type
class; public; @ToString.

#### Input
* `Collection<AiCondition>` via `setConditions`.

#### Output
* `List<AiCondition>` via `getConditions`.

#### Core logic
* Maintains a nullable list of `AiCondition` objects.
* Performs a defensive copy of the provided `Collection` into a new `ArrayList` during assignment to prevent external mutation.

#### Public API
* `AiConditionGroup() -> void` constructor.
* `getConditions() -> List<AiCondition>` returns configured child conditions.
* `setConditions(Collection<AiCondition>) -> void` sets conditions with defensive copy.

#### Dependencies
* `java.util.ArrayList`
* `java.util.Collection`
* `java.util.List`
* `lombok.ToString`
* `org.jspecify.annotations.Nullable`
* `net.ladenthin.srcmorph.config.AiCondition`
