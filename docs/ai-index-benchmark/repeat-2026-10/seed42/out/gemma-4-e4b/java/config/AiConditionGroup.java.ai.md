### AiConditionGroup.java
- H: 1.0
- C: 66805F67
- D: 2026-10-29T13:48:14Z
- T: 2026-10-29T13:48:38Z
- G: 
- A: 0.0.0
- X: file
---
> Manages a collection of child AiCondition objects used within logical AND/OR condition combinators.

#### Purpose
* Holds a list of AiCondition objects for AiCondition combinators.

#### Type
class AiConditionGroup; @ToString; @SuppressWarnings({"NullAway.Init", "initialization.fields.uninitialized"})

#### Input
* Constructor: No parameters.
* setConditions(Collection<AiCondition> conditions) -> Accepts collection of AiCondition.

#### Output
* getConditions() -> Returns List<AiCondition> or null.

#### Core logic
* AiConditionGroup constructor initializes state (no-op).
* setConditions defensively copies the input Collection<AiCondition> into the internal List<AiCondition>.
* getConditions returns the internal List<AiCondition> or null if not set.

#### Public API
getConditions() -> List<AiCondition> | null: Retrieves the child conditions list.
setConditions(Collection<AiCondition> conditions) -> void: Sets child conditions defensively.

#### Dependencies
java.util.ArrayList, java.util.Collection, java.util.List, lombok.ToString, org.jspecify.annotations.Nullable, net.ladenthin.srcmorph.config.AiCondition

#### Exceptions / Errors
* Null handling for conditions: setConditions handles null input by setting internal field to null.
* getConditions returns null if conditions were not configured.

#### Concurrency
* No explicit concurrency mechanisms present; state modification via setConditions is not inherently thread-safe if multiple threads access it concurrently.
