### AiConditionGroup.java
- H: 1.0
- C: 66805F67
- D: 2026-10-31T08:11:47Z
- T: 2026-10-31T08:16:33Z
- G: 
- A: 0.0.0
- X: file
---
> A container for child AiCondition objects used as the body of and/or combinators in AiCondition.

#### Purpose
- Holds child AiCondition objects as the body of and/or combinators in AiCondition.
- Enables Maven plugin framework binding of conditions as XML elements.

#### Type
- Class AiConditionGroup, final, @ToString, @Nullable annotations.
- Implements no interfaces; extends no classes.
- Key field: List<AiCondition> conditions.

#### Input
- Constructor: no parameters.
- Method setConditions(Collection<AiCondition>): collection of child conditions.
- Method getConditions(): no parameters.

#### Output
- Method getConditions(): returns List<AiCondition> or null.
- Method setConditions(): sets internal conditions field to a defensive copy.

#### Core logic
- setConditions defensively copies the input collection into a new ArrayList.
- If input is null, sets internal conditions to null.
- getConditions returns the stored conditions list unchanged.

#### Public API
- AiConditionGroup(): creates new AiConditionGroup.
- getConditions() -> @Nullable List<AiCondition>: returns child conditions or null.
- setConditions(Collection<AiCondition>) -> void: sets child conditions defensively.

#### Dependencies
- java.util.ArrayList
- java.util.Collection
- java.util.List
- lombok.ToString
- org.jspecify.annotations.Nullable
- net.ladenthin.srcmorph.config.AiCondition

#### Exceptions / Errors
- No exceptions thrown or caught.
- Null handling: conditions may be null; setConditions accepts null.

#### Concurrency
- No concurrency notes.
