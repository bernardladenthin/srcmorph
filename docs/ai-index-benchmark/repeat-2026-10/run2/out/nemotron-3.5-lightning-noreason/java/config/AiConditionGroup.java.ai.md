### AiConditionGroup.java
- H: 1.0
- C: 66805F67
- D: 2026-10-29T13:48:14Z
- T: 2026-10-29T14:01:19Z
- G: 
- A: 0.0.0
- X: file
---
> Calculates logical grouping of conditions for Maven plugin XML configuration.

#### Purpose
A container class that holds a defensively copied list of {@code AiCondition} children, enabling Maven XML binding for {@code <and>} / {@code <or>} combinators via a field named {@code conditions}.

#### Type
class; final; @ToString; implements no interfaces; extends java.lang.Object; generic type: List<AiCondition>; annotations: lombok.ToString, org.jspecify.annotations.Nullable

#### Input
- Constructor: no arguments
- setConditions(@Nullable Collection<AiCondition> conditions): collection of child conditions to assign
- getConditions(): returns the current conditions field

#### Output
- getConditions(): {@code List<AiCondition>} or {@code null}
- setConditions(): mutates the internal {@code conditions} field with a defensive copy

#### Core logic
- Default constructor initializes with no state
- setConditions replaces the field, copying the input collection into a new ArrayList to prevent external mutation; if null is passed, the field is set to null
- getConditions returns the stored reference (or null) without copying

#### Public API
- AiConditionGroup() -> void
- getConditions() -> @Nullable List<AiCondition>
- setConditions(@Nullable Collection<AiCondition>) -> void

#### Dependencies
imported: java.util.ArrayList, java.util.Collection, java.util.List, org.jspecify.annotations.Nullable

#### Exceptions / Errors
No explicit exception throwing; null-safety annotations indicate null is a valid state for conditions

#### Concurrency
No synchronization or immutability guarantees; the defensive copy in setConditions provides thread-safe mutation from the caller's perspective
