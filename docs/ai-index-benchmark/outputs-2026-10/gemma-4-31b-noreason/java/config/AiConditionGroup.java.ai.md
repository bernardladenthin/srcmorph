### AiConditionGroup.java
- H: 1.0
- C: 66805F67
- D: 2026-10-29T09:50:36Z
- T: 2026-10-29T09:50:47Z
- G: 
- A: 0.0.0
- X: file
---
> Groups a collection of AI conditions to enable Maven plugin XML binding for logical combinators like AND/OR.

#### Purpose
* Acts as a wrapper for a list of `AiCondition` objects.
* Ensures Maven XML configuration maps child elements to `<conditions><condition>...</condition></conditions>`.

#### Type
* Class.
* Annotations: `@ToString`, `@SuppressWarnings`.

#### Input
* `Collection<AiCondition>` via `setConditions`.

#### Output
* `List<AiCondition>` via `getConditions`.

#### Core logic
* Maintains a nullable list of `AiCondition` instances.
* Performs a defensive copy of the input collection using `ArrayList` during assignment to prevent external mutation.

#### Public API
* `AiConditionGroup() -> void` Constructor.
* `getConditions() -> List<AiCondition>` Returns configured child conditions.
* `setConditions(Collection<AiCondition>) -> void` Sets conditions with defensive copy.

#### Dependencies
* `java.util.ArrayList`
* `java.util.Collection`
* `java.util.List`
* `lombok.ToString`
* `org.jspecify.annotations.Nullable`
* `AiCondition`
