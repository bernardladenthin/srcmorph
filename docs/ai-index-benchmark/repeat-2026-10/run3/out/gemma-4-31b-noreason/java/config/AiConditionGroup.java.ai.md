### AiConditionGroup.java
- H: 1.0
- C: 66805F67
- D: 2026-10-29T14:19:17Z
- T: 2026-10-29T14:25:33Z
- G: 
- A: 0.0.0
- X: file
---
> Groups child AI conditions to enable Maven plugin XML binding for logical AND/OR combinators.

#### Purpose
* Acts as a wrapper for a collection of `AiCondition` objects.
* Ensures Maven XML configuration maps child elements to `<conditions><condition>...</condition></conditions>`.

#### Type
class; public. Annotated with `@ToString`.

#### Input
* `Collection<AiCondition>` via `setConditions`.

#### Output
* `List<AiCondition>` via `getConditions`.

#### Core logic
* Maintains a list of `AiCondition` instances.
* Performs a defensive copy of the input collection using `ArrayList` during assignment to prevent external mutation.
* Handles nullability for cases where no conditions are configured.

#### Public API
* AiConditionGroup() -> void: Default constructor.
* getConditions() -> List<AiCondition>: Returns configured child conditions.
* setConditions(Collection<AiCondition>) -> void: Sets conditions with defensive copy.

#### Dependencies
* java.util.ArrayList
* java.util.Collection
* java.util.List
* lombok.ToString
* org.jspecify.annotations.Nullable
* AiCondition
