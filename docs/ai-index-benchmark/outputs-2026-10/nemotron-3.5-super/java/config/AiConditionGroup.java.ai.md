### AiConditionGroup.java
- H: 1.0
- C: 66805F67
- D: 2026-10-31T09:32:09Z
- T: 2026-10-31T09:38:15Z
- G: 
- A: 0.0.0
- X: file
---
> Holds a nullable list of child AiCondition objects that form the body of Maven <and> / <or> combinators in AiCondition configuration.

#### Purpose
- Groups child AiCondition instances for Maven plugin <and> / <or> combinator configuration.
- Exposes a nullable getter and a defensive-copy setter for the conditions list.

#### Type
- Kind: public class (non-final, non-abstract).
- Annotations: @ToString (Lombok), @SuppressWarnings({"NullAway.Init", "initialization.fields.uninitialized"}).
- Field: private @Nullable List<AiCondition> conditions.

#### Input
- Constructor AiConditionGroup(): no parameters.
- setConditions(@Nullable Collection<AiCondition> conditions): accepts a nullable collection of AiCondition.
- Field conditions: nullable List<AiCondition> read by getConditions.

#### Output
- getConditions() -> @Nullable List<AiCondition>: returns the internal list or null.
- setConditions(...): mutates conditions with a new ArrayList copy or null.
- Side effect: defensive copy isolates the field from the caller's collection, but getConditions returns the live internal list.

#### Core logic
- AiConditionGroup stores child conditions in a field named conditions so Maven binds <conditions><condition>…</condition></conditions> for <and>/<or> combinators.
- getConditions returns the stored @Nullable List<AiCondition> directly, which may be null.
- setConditions creates a new ArrayList<>(conditions) when the argument is non-null, otherwise assigns null.
- No validation of element nullability is performed.

#### Public API
- AiConditionGroup() -> AiConditionGroup: creates a new instance.
- getConditions() -> @Nullable List<AiCondition>: returns child conditions or null.
- setConditions(@Nullable Collection<AiCondition>) -> void: sets child conditions with defensive copy.

#### Dependencies
- java.util.ArrayList
- java.util.Collection
- java.util.List
- lombok.ToString
- org.jspecify.annotations.Nullable
- net.ladenthin.srcmorph.config.AiCondition

#### Exceptions / Errors
- No checked exceptions declared.
- Passing null to setConditions assigns null to the field without throwing.
- Null elements inside the passed collection are not rejected by the ArrayList constructor.

#### Concurrency
- No synchronization; instances are not thread-safe.
- setConditions defensively copies the input collection, but getConditions returns the mutable internal list reference.
