### AiConditionGroup.java
- H: 1.0
- C: 66805F67
- D: 2026-10-31T10:06:17Z
- T: 2026-10-31T11:30:10Z
- G: 
- A: 0.0.0
- X: file
---
> AiConditionGroup is a container for child AiCondition objects used as the body of an and/or combinator in AiCondition.

#### Purpose
- Holds a list of child AiCondition instances.
- Enables Maven plugin framework item binding via field name.

#### Type
- Public class AiConditionGroup.
- No inheritance; implicitly extends Object.
- Generic field: List<AiCondition> (nullable).

#### Input
- Constructor: no parameters.
- setConditions: takes a nullable Collection<AiCondition> and stores a defensive copy.

#### Output
- getConditions returns the stored list or null.
- setConditions stores a new ArrayList copy of the input.

#### Core logic
- Stores child conditions in the conditions field.
- Defensive copying in setConditions prevents external mutation.
- getConditions returns the current list reference.

#### Public API
- AiConditionGroup() -> creates a new group instance.
- getConditions() -> returns child conditions or null.
- setConditions(Collection<AiCondition> conditions) -> stores a defensive copy.

#### Dependencies
- java.util.ArrayList
- java.util.Collection
- java.util.List
- lombok.ToString
- org.jspecify.annotations.Nullable
- AiCondition (referenced class)

#### Exceptions / Errors
- No checked exceptions thrown.
- setConditions handles null defensively; returns null when empty.
- NullAway suppression flags uninitialized field access.

#### Concurrency
- Not thread-safe; list is mutable without synchronization.
