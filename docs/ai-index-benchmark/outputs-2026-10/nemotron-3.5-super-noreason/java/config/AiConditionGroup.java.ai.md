### AiConditionGroup.java
- H: 1.0
- C: 66805F67
- D: 2026-10-31T10:00:28Z
- T: 2026-10-31T10:00:34Z
- G: 
- A: 0.0.0
- X: file
---
> Holds a nullable list of child AiCondition objects that form the body of an and/or combinator in AiCondition configuration.

#### Purpose
- Provides a container for nested AiCondition elements used by Maven plugin XML binding.
- Defensively copies the input collection when setting conditions.

#### Type
- Kind: final class
- Modifiers: public
- Annotations: @ToString, @SuppressWarnings({"NullAway.Init", "initialization.fields.uninitialized"})
- Package: net.ladenthin.srcmorph.config

#### Input
- Constructor: no parameters
- setConditions(Collection<AiCondition> conditions): nullable collection of child conditions, defensively copied into an ArrayList
- Field: private @Nullable List<AiCondition> conditions

#### Output
- getConditions() -> @Nullable List<AiCondition>: returns the stored child conditions or null
- setConditions: mutates the conditions field with a defensive copy or null

#### Core logic
- AiConditionGroup stores child AiCondition instances in a nullable List<AiCondition> field named conditions.
- getConditions returns the field directly without copying.
- setConditions creates a new ArrayList from the provided Collection when non-null, otherwise assigns null.
- The no-arg constructor performs no initialization.

#### Public API
- AiConditionGroup() -> AiConditionGroup: creates a new instance
- getConditions() -> @Nullable List<AiCondition>: returns child conditions or null
- setConditions(@Nullable Collection<AiCondition>) -> void: sets child conditions with defensive copy

#### Dependencies
- java.util.ArrayList
- java.util.Collection
- java.util.List
- lombok.ToString
- org.jspecify.annotations.Nullable
- net.ladenthin.srcmorph.config.AiCondition

#### Exceptions / Errors
- No explicit exception handling or throws clauses.
- Null input to setConditions results in null field assignment.

#### Concurrency
- No synchronization or thread-safety guarantees.
- Mutable List field is not defensively copied on read.
