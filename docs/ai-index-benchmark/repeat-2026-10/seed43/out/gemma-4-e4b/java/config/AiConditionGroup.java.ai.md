### AiConditionGroup.java
- H: 1.0
- C: 66805F67
- D: 2026-10-29T13:54:46Z
- T: 2026-10-29T13:55:09Z
- G: 
- A: 0.0.0
- X: file
---
> Manages a collection of child AiCondition objects used within logical combinators like AND/OR.

#### Purpose
* Holds a list of AiCondition objects for logical grouping.

#### Type
class AiConditionGroup; @ToString; @SuppressWarnings({"NullAway.Init", "initialization.fields.uninitialized"})

#### Input
* Constructor: No parameters.
* setConditions(Collection<AiCondition> conditions) -> Accepts collection of AiCondition.

#### Output
* getConditions() -> Returns List<AiCondition> or null.

#### Core logic
* Initializes AiConditionGroup with no state.
* setConditions defensively copies input Collection<AiCondition> into internal List<AiCondition>.

#### Public API
getConditions() -> List<AiCondition> | null: Retrieves the configured child conditions.
setConditions(Collection<AiCondition> conditions) -> void: Sets child conditions, defensively copying input.

#### Dependencies
java.util.ArrayList, java.util.Collection, java.util.List, lombok.ToString, org.jspecify.annotations.Nullable, net.ladenthin.srcmorph.config.AiCondition

#### Exceptions / Errors
* Null handling in setConditions: If input collection is null, internal conditions field is set to null.

#### Concurrency
* No explicit concurrency controls; relies on standard Java object behavior.
