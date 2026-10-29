### AiConditionGroup.java
- H: 1.0
- C: 66805F67
- D: 2026-10-29T14:01:55Z
- T: 2026-10-29T14:02:13Z
- G: 
- A: 0.0.0
- X: file
---
> Manages a collection of {@link AiCondition} objects used within logical AND/OR groupings.

#### Purpose
* Holds a list of {@link AiCondition}s for logical grouping.

#### Type
class AiConditionGroup; @ToString; @SuppressWarnings({"NullAway.Init", "initialization.fields.uninitialized"})

#### Input
* Constructor: No parameters.
* setConditions(Collection<AiCondition> conditions) -> Sets conditions, defensively copying input.

#### Output
* getConditions() -> Returns List<AiCondition> or null.

#### Core logic
* Initializes AiConditionGroup with no conditions.
* setConditions defensively copies the input Collection<AiCondition> into the internal list.

#### Public API
getConditions() -> List<AiCondition> | null: Retrieves the configured child conditions.
setConditions(Collection<AiCondition> conditions) -> void: Sets the child conditions defensively.

#### Dependencies
java.util.ArrayList, java.util.Collection, java.util.List, lombok.ToString, org.jspecify.annotations.Nullable, net.ladenthin.srcmorph.config.AiCondition

#### Exceptions / Errors
* Null handling in setConditions: If input collection is null, internal state is set to null.

#### Concurrency
* No explicit concurrency controls; relies on standard Java object behavior.
