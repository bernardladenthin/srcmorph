### AiConditionGroup.java
- H: 1.0
- C: 66805F67
- D: 2026-10-31T09:32:09Z
- T: 2026-10-31T09:32:13Z
- G: 
- A: 0.0.0
- X: file
---
> Wraps a list of AiCondition children as the body of `<and>` / `<or>` combinators in AiCondition so the Maven plugin framework binds them as `<conditions><condition>` items.

#### Purpose
- Holds a nullable `List<AiCondition>` field named `conditions` to make Maven derive the item element name `<condition>` from the field's singular.
- Enables combinators to nest children in config rather than holding a `List<AiCondition>` directly (which would require `<and>`/`<or>` item names).

#### Type
- class `AiConditionGroup` in package `net.ladenthin.srcmorph.config`; public, non-final, non-abstract, non-sealed; implicitly extends Object.
- Annotations: `@ToString` (Lombok), `@SuppressWarnings({"NullAway.Init", "initialization.fields.uninitialized"})`.

#### Input
- No-arg constructor; no parameters, no injected dependencies.
- `setConditions(@Nullable Collection<AiCondition> conditions)` — nullable collection of child conditions; defensively copied into a new `ArrayList`.

#### Output
- `getConditions()` returns `@Nullable List<AiCondition>` (configured children, or null).
- `setConditions` mutates the private `conditions` field; no external resources or side effects.

#### Core logic
- Field `private @Nullable List<AiCondition> conditions` stores the child conditions.
- Constructor is a no-op; field relies on null as uninitialized state.
- Setter converts incoming non-null `Collection<AiCondition>` to a new `ArrayList<>(conditions)`; null input sets field to null.
- Getter returns the field as-is (no defensive copy on read).

#### Public API
- `AiConditionGroup() -> AiConditionGroup` — creates empty group, no-op.
- `getConditions() -> @Nullable List<AiCondition>` — returns child conditions or null.
- `setConditions(@Nullable Collection<AiCondition> conditions) -> void` — sets child conditions, defensively copied.

#### Dependencies
- `java.util.ArrayList`, `java.util.Collection`, `java.util.List`.
- `lombok.ToString`.
- `org.jspecify.annotations.Nullable`.
- `net.ladenthin.srcmorph.config.AiCondition` (Java type and javadoc reference).

#### Exceptions / Errors
- No thrown or caught exceptions.
- Null-handling: `@Nullable` on field, getter return, and setter parameter; null collection stored as null.
- Defensive copy prevents external mutation of the stored list at assignment time.

#### Concurrency
- Not thread-safe: plain mutable field, no synchronization, no immutability guarantees.
- Defensive copy limits aliasing only at `setConditions` time; subsequent mutation of the returned list via `getConditions()` still affects internal state.
